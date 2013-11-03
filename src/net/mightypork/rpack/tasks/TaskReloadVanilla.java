package net.mightypork.rpack.tasks;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import net.mightypork.rpack.App;
import net.mightypork.rpack.Config;
import net.mightypork.rpack.Flags;
import net.mightypork.rpack.Paths;
import net.mightypork.rpack.gui.windows.Alerts;
import net.mightypork.rpack.hierarchy.AssetEntry;
import net.mightypork.rpack.hierarchy.EAsset;
import net.mightypork.rpack.library.Sources;
import net.mightypork.rpack.utils.*;
import net.mightypork.rpack.utils.filters.StringFilter;


public class TaskReloadVanilla {

	public static void run(String version) {

		Log.f2("Reloading Vanilla assets (" + version + ")");

		Alerts.loading(true);

		File outDir = OsUtils.getAppDir(Paths.DIR_VANILLA, true);
		FileUtils.delete(outDir, true);
		outDir.mkdirs();


		Map<String, AssetEntry> assets = new LinkedHashMap<String, AssetEntry>();


		// EXTRACT FROM .minecraft/versions/[latest]

		File zipFile = new File(OsUtils.getMcDir("versions/" + version), version + ".jar");
		Log.f3("Extracting: " + zipFile);

		try {

			StringFilter filter = new StringFilter() {

				@Override
				public boolean accept(String path) {

					boolean ok = false;

					String[] parts = FileUtils.removeExtension(path);
					String ext = parts[1];
					EAsset type = EAsset.forExtension(ext);

					ok |= path.startsWith("assets");
					ok &= (type != null || ext.equals("mcmeta"));

					return ok;
				}
			};

			List<String> list = ZipUtils.extractZip(zipFile, outDir, filter);

			for (String s : list) {
				if (s.startsWith("assets")) {
					s = FileUtils.escapeFilename(s);
					String[] parts = FileUtils.removeExtension(s);
					String key = parts[0].replace('\\', '.');
					key = key.replace('/', '.');
					String ext = parts[1];
					EAsset type = EAsset.forExtension(ext);

					if (type == null) {
						if (Config.LOG_VANILLA_RELOAD) Log.f3("# excluding: " + s);
						continue;
					}

					assets.put(key, new AssetEntry(key, type));
				}
			}

		} catch (Exception e) {
			Log.e(e);

			Alerts.loading(false);
			return; // success = false
		}


		// COPY FROM .minecraft/assets (sounds & lang)
		File source = OsUtils.getMcDir("assets");
		Log.f3("Processing: " + source);

		File target = new File(outDir, "assets/minecraft");

		StringFilter filter = new StringFilter() {

			@Override
			public boolean accept(String entry) {

				entry = FileUtils.escapeFilename(entry);
				String[] split = FileUtils.removeExtension(entry);

				String name = split[0];
				String ext = split[1];

				if (name.contains("READ_ME")) return false;

				return (EAsset.forExtension(ext) != null);
			}
		};

		try {
			ArrayList<File> list = new ArrayList<File>();

			FileUtils.copyDirectory(source, target, filter, list);

			for (File f : list) {
				String path = f.getAbsolutePath();
				path = path.replace(source.getAbsolutePath(), "assets/minecraft");

				path = FileUtils.escapeFilename(path);
				String[] parts = FileUtils.removeExtension(path);
				String key;
				key = parts[0].replace('\\', '.');
				key = key.replace('/', '.');
				String ext = parts[1];
				EAsset type = EAsset.forExtension(ext);

				if (type == null) {
					if (Config.LOG_VANILLA_RELOAD) Log.f3("# excluding: " + path);
					continue;
				}

				assets.put(key, new AssetEntry(key, type));
			}

		} catch (Exception e) {
			Log.e(e);

			Alerts.loading(false);
			return; // success = false
		}


		assets = Utils.sortByKeys(assets);

		Sources.vanilla.setAssets(assets);


		// save to file

		Map<String, String> saveMap = new LinkedHashMap<String, String>();

		for (AssetEntry e : assets.values()) {
			if (Config.LOG_VANILLA_RELOAD) Log.f3("+ " + e);
			saveMap.put(e.getKey(), e.getType().toString());
		}

		File datafile = OsUtils.getAppDir(Paths.FILE_VANILLA_STRUCTURE);
		try {
			SimpleConfig.mapToFile(datafile, saveMap);
		} catch (IOException e) {
			Log.e(e);

			Alerts.loading(false);
			return; // success = false			
		}


		Log.f2("Reloading Vanilla assets - done.");
		Flags.VANILLA_STRUCTURE_LOAD_OK = true;
		Alerts.loading(false);
	}


	public static String getUserChoice(boolean isInitial) {

		//@formatter:off
		String initial = 
				"To start a ResourcePack Workbench, the\n" +
				"default pack must be extracted from\n" +
				"your Minecraft folder.\n" +
				"\n" +
				"Please, select a Minecraft version to use:";

		String user = 
				"Vanilla ResourcePack will be re-extracted.\n" +
				"\n" +
				"Assets of any mods installed at this point\n" +
				"will be included in the asset tree.\n" +
				"\n" +
				"Please, select a Minecraft version to use:";
		//@formatter:on

		// obtain applicable versions
		List<File> list = FileUtils.listDirectory(OsUtils.getMcDir("versions"));

		List<String> opts = new ArrayList<String>();

		for (File f : list) {
			if (f.exists() && f.isDirectory()) {
				File jar = new File(f, f.getName() + ".jar");

				if (jar.exists() && jar.isFile()) opts.add(f.getName());
			}
		}

		if (opts.size() == 0) {
			//@formatter:off
			App.die(
				"Your .minecraft/versions folder is empty.\n" +
				"Run Minecraft and try again."
			);
			//@formatter:on
		}

		Collections.sort(opts);
		Collections.reverse(opts);


		// build dialog
		final String[] possibilities = opts.toArray(new String[opts.size()]);

		String defChoice = possibilities[0];

		//@formatter:off
		String s = (String) JOptionPane.showInputDialog(
				App.getFrame(),
				isInitial?initial:user,
				"Extracting Vanilla ResourcePack",
				JOptionPane.QUESTION_MESSAGE,
				null,
				possibilities,
				defChoice
			);
		//@formatter:on

		return s;
	}
}
