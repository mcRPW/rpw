package net.mightypork.rpw.tasks;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Config;
import net.mightypork.rpw.Flags;
import net.mightypork.rpw.Paths;
import net.mightypork.rpw.gui.windows.Alerts;
import net.mightypork.rpw.hierarchy.AssetEntry;
import net.mightypork.rpw.hierarchy.EAsset;
import net.mightypork.rpw.library.Sources;
import net.mightypork.rpw.utils.FileUtils;
import net.mightypork.rpw.utils.Log;
import net.mightypork.rpw.utils.OsUtils;
import net.mightypork.rpw.utils.SimpleConfig;
import net.mightypork.rpw.utils.Utils;
import net.mightypork.rpw.utils.validation.FileSuffixFilter;
import net.mightypork.rpw.utils.validation.StringFilter;


public class TaskReloadVanilla {

	public static void run(String version) {

		Log.f2("Reloading Vanilla assets (" + version + ")");

		Alerts.loading(true);

		File outDir = OsUtils.getAppDir(Paths.DIR_VANILLA, true);
		FileUtils.delete(outDir, true);
		outDir.mkdirs();

		// EXTRACT FROM .minecraft/versions/[latest]

		File zipFile = new File(OsUtils.getMcDir("versions/" + version), version + ".jar");

		Map<String, AssetEntry> assets = FileUtils.loadAssetsFromZip(zipFile, outDir);

		if (assets == null) {
			Log.e("Vanilla pack extraction failed, aborting.");
			Alerts.loading(false);
			return;
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
					if (Config.LOG_ZIP_EXTRACTING) Log.f3("# excluding: " + path);
					continue;
				}

				assets.put(key, new AssetEntry(key, type));
			}

		} catch (Exception e) {
			Log.e(e);

			Alerts.loading(false);
			return; // success = false
		}


		// Do the /mods folder

		List<File> list = FileUtils.listDirectory(OsUtils.getMcDir("mods"));

		List<File> modFiles = new ArrayList<File>();

		FileSuffixFilter fsf = new FileSuffixFilter("jar", "zip");

		for (File f : list) {
			if (f.exists() && fsf.accept(f)) {
				System.out.println("Passed:" + f);
				modFiles.add(f);
			}
		}

		boolean modsLoaded = false;

		if (modFiles.size() > 0) {

			String modList = "";
			for (File f : modFiles) {
				modList += " * " + f.getName() + "\n";
			}
			modList = modList.trim();

			//@formatter:off
			boolean yeah = Alerts.askYesNo(
					App.getFrame(),
					"Mods found",
					"RPW detected some mod files in your\n" + 
					".minecraft/mods directory:\n" + 
					"\n" + 
					modList +
					"\n" + 
					"Do you want to load them too?"
			);
			//@formatter:on

			if (yeah) {
				int added = 0;
				for (File f : modFiles) {
					int oldLen = assets.size();
					FileUtils.loadAssetsFromZip(f, outDir, assets);
					added += assets.size() - oldLen;
				}
				modsLoaded = added > 0;
			}

		}


		assets = Utils.sortByKeys(assets);

		Sources.vanilla.setAssets(assets);


		// save to file

		Map<String, String> saveMap = new LinkedHashMap<String, String>();

		for (AssetEntry e : assets.values()) {
			if (Config.LOG_ZIP_EXTRACTING) Log.f3("+ " + e);
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

		if (Config.FANCY_GROUPS && modsLoaded) {
			//@formatter:off
			boolean yeah = Alerts.askYesNo(
					App.getFrame(),
					"Mods installed",
					"It is recommended to disable Fancy Tree display\n" + 
					"when mods are installed. You can toggle it in\n" + 
					"the Options menu.\n" + 
					"\n" +
					"Disable Fancy Tree now?"
			);
			//@formatter:on

			if (yeah) {
				Config.FANCY_GROUPS = false;
				Config.save();
			}
		}
	}


	/**
	 * Ask user for the level to use
	 * 
	 * @param isInitial is this the first startup?
	 * @return MC version selected
	 */
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
				"Assets installed in the game jar and the\n" +
				"assets folder will be included.\n" +
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
