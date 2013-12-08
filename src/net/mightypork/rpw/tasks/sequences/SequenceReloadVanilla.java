package net.mightypork.rpw.tasks.sequences;


import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Config;
import net.mightypork.rpw.Flags;
import net.mightypork.rpw.Paths;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.library.Sources;
import net.mightypork.rpw.tasks.Tasks;
import net.mightypork.rpw.tasks.sequences.AbstractMonitoredSequence;
import net.mightypork.rpw.tree.assets.AssetEntry;
import net.mightypork.rpw.tree.assets.EAsset;
import net.mightypork.rpw.utils.FileUtils;
import net.mightypork.rpw.utils.OsUtils;
import net.mightypork.rpw.utils.SimpleConfig;
import net.mightypork.rpw.utils.Utils;
import net.mightypork.rpw.utils.logging.Log;
import net.mightypork.rpw.utils.validation.FileSuffixFilter;


public class SequenceReloadVanilla extends AbstractMonitoredSequence {

	private String version;
	private boolean modsLoaded = false;

	/** Directory for saving loaded assets */
	private File outDir;

	/** Map of all the loaded stuff */
	private Map<String, AssetEntry> assets;

	private static final FileFilter ASSETS_DIR_FILTER = new FileFilter() {

		@Override
		public boolean accept(File entry) {

			String fname = FileUtils.escapeFilename(entry.getName());
			String[] split = FileUtils.getFilenameParts(fname);

			String name = split[0];
			String ext = split[1];

			if (name.startsWith("READ_ME")) return false;

			return EAsset.forExtension(ext).isAssetOrMeta();
		}
	};


	public SequenceReloadVanilla(String version) {

		this.version = version;
	}


	@Override
	protected String getMonitorHeading() {

		return "Importing Minecraft assets";
	}


	@Override
	public int getStepCount() {

		return 5; // Must match the actual number!
	}


	@Override
	protected boolean step(int step) {

		//@formatter:off
		switch (step) {
			case 0: return stepPrepareOutput();
			case 1:	return stepLoadFromJar();
			case 2:	return stepLoadFromAssetsDir();
			case 3:	return stepLoadMods();
			case 4:	return stepSaveStructure();
		}
		//@formatter:on

		return false;

	}


	@Override
	public String getStepName(int step) {

		//@formatter:off
		switch (step) {
			case 0: return "Preparing output directory.";
			case 1:	return "Getting files from jar.";
			case 2:	return "Getting files from the assets directory.";
			case 3:	return "Getting files from installed mods.";
			case 4:	return "Saving structure data to file.";
			default: return null;
		}
		//@formatter:on
	}


	/**
	 * Prepare output directory
	 * 
	 * @return success
	 */
	private boolean stepPrepareOutput() {

		Log.f2("Cleaning output directory");

		outDir = OsUtils.getAppDir(Paths.DIR_VANILLA, true);
		FileUtils.delete(outDir, true);
		outDir.mkdirs();
		return true;
	}


	/**
	 * Load asset files from Minecraft jar
	 * 
	 * @return success
	 */
	private boolean stepLoadFromJar() {

		Log.f2("Extracting Minecraft jar file");

		File zipFile = new File(OsUtils.getMcDir("versions/" + version), version + ".jar");

		assets = FileUtils.loadAssetsFromZip(zipFile, outDir);

		if (assets == null) {
			Log.e("Vanilla pack extraction failed, aborting.");

			return false;
		}

		return true;
	}


	/**
	 * Load files from .minecraft/assets
	 * 
	 * @return success
	 */
	private boolean stepLoadFromAssetsDir() {

		Log.f2("Preparing to copy files from assets folder");

		File source = null;


		Log.f3("Checking assets folder...");


		// determine the path
		if (OsUtils.getMcDir("assets/pack.mcmeta").exists()) {
			// rev1
			source = OsUtils.getMcDir("assets");
			Log.f3("Detected type 1 folder structure.");

		} else if (OsUtils.getMcDir("assets/virtual/legacy/pack.mcmeta").exists()) {
			// rev2
			source = OsUtils.getMcDir("assets/virtual/legacy");
			Log.f3("Detected type 2 folder structure.");

		} else {
			// fail
			Log.e("Unsupported structure found, aborting.");
			return false;
		}


		Log.f2("Copying asset files from: " + source);


		File target = new File(outDir, "assets/minecraft");


		// do the actual work
		try {
			ArrayList<File> list = new ArrayList<File>();

			// copy the right files, add entries into list
			FileUtils.copyDirectory(source, target, ASSETS_DIR_FILTER, list);

			for (File f : list) {
				String path = f.getAbsolutePath();
				path = path.replace(source.getAbsolutePath(), "assets/minecraft");

				path = FileUtils.escapeFilename(path);
				String[] parts = FileUtils.getFilenameParts(path);

				// slashes to dots
				String key = parts[0].replace('\\', '.').replace('/', '.');

				String ext = parts[1];

				EAsset type = EAsset.forExtension(ext);

				if (!type.isAsset()) {
					continue;
				}

				AssetEntry ae = new AssetEntry(key, type);

				assets.put(key, ae);

				if (Config.LOG_EXTRACTED_ASSETS) Log.f3("+ "+ae.toString());
			}

		} catch (Exception e) {
			Log.e(e);
			return false;
		}

		return true;
	}


	/**
	 * Load files from mods (if any)
	 * 
	 * @return success
	 */
	private boolean stepLoadMods() {

		List<File> list = FileUtils.listDirectory(OsUtils.getMcDir("mods"));

		List<File> modFiles = new ArrayList<File>();

		FileSuffixFilter fsf = new FileSuffixFilter("jar", "zip");


		Log.f2("Checking for installed mods");
		// check what mod files are installed
		for (File f : list) {
			if (f.exists() && fsf.accept(f)) {
				modFiles.add(f);
				Log.f3("found " + f.getName());
			}
		}

		// ask user for action
		if (modFiles.size() > 0) {

			Alerts.loading(false);

			//@formatter:off
			String message = "RPW detected some mod files in your\n" + 
					".minecraft/mods directory.\n" + 
					"\n" + 
					"Do you want to load them too?";
			//@formatter:on

			ArrayList<JCheckBox> ckboxes = new ArrayList<JCheckBox>();

			for (File f : modFiles) {
				ckboxes.add(new JCheckBox(f.getName(), true));
			}

			Object[] array = new Object[ckboxes.size() + 1];

			array[0] = message;

			for (int i = 0; i < ckboxes.size(); i++) {
				array[i + 1] = ckboxes.get(i);
			}

			//@formatter:off
		    int n = JOptionPane.showOptionDialog(
		    		App.getFrame(), //parent
		    		array, //message
		    		"Mods found", //title
		    		JOptionPane.YES_NO_OPTION, //option type
		    		JOptionPane.QUESTION_MESSAGE, // message type
		    		Icons.DIALOG_QUESTION, // icon
		    		new String[] {"Import selected","Ignore mods"}, // options
		    		null // default option
		    );
		    //@formatter:on

			boolean wantLoadMods = (n == 0);


			//@formatter:on
			Alerts.loading(true);

			if (wantLoadMods) {

				Log.f2("Extracting mod assets");
				// do the work
				int added = 0;
				for (int i = 0; i < modFiles.size(); i++) {

					if (!ckboxes.get(i).isSelected()) continue;

					int oldLen = assets.size();

					File f = modFiles.get(i);

					FileUtils.loadAssetsFromZip(f, outDir, assets);

					added += assets.size() - oldLen;
				}
				modsLoaded = added > 0;
			}

		}

		return true;
	}


	/**
	 * Save structure to file
	 * 
	 * @return success
	 */
	private boolean stepSaveStructure() {

		Log.f2("Saving structure to file");

		assets = Utils.sortByKeys(assets);

		Sources.vanilla.setAssets(assets);

		// make a map
		Map<String, String> saveMap = new LinkedHashMap<String, String>();

		for (AssetEntry e : assets.values()) {
			saveMap.put(e.getKey(), e.getType().toString());
		}

		// write to file
		File datafile = OsUtils.getAppDir(Paths.FILE_VANILLA_STRUCTURE);
		try {
			SimpleConfig.mapToFile(datafile, saveMap, false);
		} catch (IOException e) {
			Log.e(e);

			Alerts.loading(false);
			return false;
		}

		return true;

	}	

	
	@Override
	protected void doBefore() {

		Log.f1("Reloading Vanilla assets (" + version + ")");

	}
	

	@Override
	protected void doAfter(boolean success) {

		if (!success) {
			Log.e("Reloading Vanilla assets - FAILED.");

			//@formatter:off
			boolean end = Alerts.askYesNo(
					App.getFrame(),
					"Asset extraction failed", 
					"An error occured while extracting\n"+
					"Minecraft assets.\n"+
					"\n"+
					"Check the log file for details.\n"+
					"\n"+
					"RPW is pretty much useless without\n" +
					"the assets.\n" + 
					"Do you want to quit?"
			);
			//@formatter:on

			if (end) {
				Tasks.taskExit();
			}

			return;
		}

		Log.f1("Reloading Vanilla assets - done.");
		Flags.VANILLA_STRUCTURE_LOAD_OK = true;

		if (Config.FANCY_TREE && modsLoaded) {
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
				Config.FANCY_TREE = false;
				Config.save();
			}
		}
	}
}
