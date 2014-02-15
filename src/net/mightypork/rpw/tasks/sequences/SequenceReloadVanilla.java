package net.mightypork.rpw.tasks.sequences;


import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.*;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Config;
import net.mightypork.rpw.Flags;
import net.mightypork.rpw.Paths;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.widgets.HBox;
import net.mightypork.rpw.gui.widgets.VBox;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.library.Sources;
import net.mightypork.rpw.struct.ModEntryList;
import net.mightypork.rpw.struct.VersionInfo;
import net.mightypork.rpw.tasks.Tasks;
import net.mightypork.rpw.tree.assets.AssetEntry;
import net.mightypork.rpw.tree.assets.EAsset;
import net.mightypork.rpw.utils.Utils;
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.files.OsUtils;
import net.mightypork.rpw.utils.files.SimpleConfig;
import net.mightypork.rpw.utils.files.ZipUtils;
import net.mightypork.rpw.utils.logging.Log;
import net.mightypork.rpw.utils.validation.FileSuffixFilter;
import net.mightypork.rpw.utils.validation.StringFilter;


public class SequenceReloadVanilla extends AbstractMonitoredSequence {

	private String version;
	private boolean modsLoaded = false;
	private String assetsVersion;

	/** Directory for saving loaded assets */
	private File outDir;

	/** Map of all the loaded stuff */
	private Map<String, AssetEntry> assets;
	protected ArrayList<JCheckBox> modCkboxes;
	private File modsDir;

	private static final StringFilter ASSETS_DIR_FILTER = new StringFilter() {

		@Override
		public boolean accept(String filename) {

			String fname = FileUtils.escapeFilename(filename);
			String[] split = FileUtils.getFilenameParts(fname);

			//String name = split[0];
			String ext = split[1];

			// discard crap we don't want			
			if (fname.equals("READ_ME_I_AM_VERY_IMPORTANT.txt")) return false;
			if (fname.equals("icon_16x16.png")) return false;
			if (fname.equals("icon_32x32.png")) return false;
			if (fname.equals("sounds.json")) return false;

			return EAsset.forExtension(ext).isAssetOrMeta();
		}
	};


	public SequenceReloadVanilla(String version) {

		this.version = version;
	}


	@Override
	protected String getMonitorHeading() {

		return "Importing Minecraft assets ("+version+")";
	}


	@Override
	public int getStepCount() {

		return 6; // Must match the actual number!
	}


	@Override
	protected boolean step(int step) {

		//@formatter:off
		switch (step) {
			case 0: return stepCheckVersionCompatibility();
			case 1: return stepPrepareOutput();
			case 2:	return stepLoadFromJar(); // must be BEFORE assets dir
			case 3:	return stepLoadFromAssetsDir();
			case 4:	return stepLoadMods();
			case 5:	return stepSaveStructure();
		}
		//@formatter:on

		return false;

	}


	@Override
	public String getStepName(int step) {

		//@formatter:off
		switch (step) {
			case 0: return "Checking version compatibility.";
			case 1: return "Cleaning output directory.";
			case 2:	return "Getting files from jar.";
			case 3:	return "Getting files from the assets directory.";
			case 4:	return "Checking for installed mods.";
			case 5:	return "Saving structure data to file.";
			default: return null;
		}
		//@formatter:on
	}


	/**
	 * Prepare output directory
	 * 
	 * @return success
	 */
	private boolean stepCheckVersionCompatibility() {

		File jsonFile = new File(OsUtils.getMcDir("versions/" + version), version + ".json");

		if (!jsonFile.exists()) {
			Log.e("Version JSON file not found, aborting!");
			return false;
		}

		Log.f3("Version JSON file: " + jsonFile);

		try {

			String s = FileUtils.fileToString(jsonFile);

			VersionInfo vi = VersionInfo.fromJson(s);

			if (vi.assets == null) {
				assetsVersion = "legacy"; // legacy assets index (1.7.2 and below)
			} else {
				assetsVersion = vi.assets;
			}

			if (vi.type == null) {
				Log.e("Version type not defined, aborting.\nIf you report this, ATTACH THE VERSION JSON FILE!");
				return false;
			} else {

				if (!vi.isReleaseOrSnapshot()) {
					Log.e("Unsupported version type: " + vi.type);
					Log.i("RPW supports only types 'release' and 'snapshot'.");
					return false;
				}
			}

			Log.f3("Assets version to be used: " + assetsVersion);

		} catch (IOException e) {
			Log.e("Error while parsing JSON file, aborting.", e);
			return false;
		} catch (IllegalArgumentException e) {
			Log.e("Bad JSON file structure, aborting.", e);
			return false;
		}

		return true;
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

		File zipFile = new File(OsUtils.getMcDir("versions/" + version), version + ".jar");

		assets = FileUtils.loadAssetsFromZip(zipFile, outDir);

		if (assets == null) {
			Log.e("Vanilla pack extraction failed, aborting.");

			return false;
		}

		Log.f3(assets.size() + " files extracted from JAR.");


		return true;
	}


	/**
	 * Load files from .minecraft/assets
	 * 
	 * @return success
	 */
	private boolean stepLoadFromAssetsDir() {

		File source = null;

		boolean useObjectRegistry = true; // files straight in the assets folder

		if (OsUtils.getMcDir("assets/pack.mcmeta").exists()) {
			// old system
			source = OsUtils.getMcDir("assets");
			useObjectRegistry = false;
			Log.f3("Detected legacy folder structure.");
			Log.w("YOU SHOULD UPDATE YOUR MINECRAFT LAUNCHER!");

		} else {
			// objects
			useObjectRegistry = true;
			source = OsUtils.getMcDir("assets/indexes/" + assetsVersion + ".json"); // try per-version file

			Log.f3("Detected object registry.");
			Log.f3("Checking index file: " + source);

			if (!source.exists()) {
				Log.e("Index file not found, aborting.");
				Log.i("TO FIX THIS, run Minecraft (v. " + version + "),\nclose it and try this again.");
				return false;
			}
		}

		File target = new File(outDir, "assets/minecraft");

		try {
			ArrayList<File> list = new ArrayList<File>();

			if (useObjectRegistry) {

				Log.f2("Using index file: " + source);
				FileUtils.extractObjectFiles(source, target, ASSETS_DIR_FILTER, list);

			} else {

				// legacy structure

				Log.f2("Copying assets from: " + source);

				FileUtils.copyDirectory(source, target, new FileFilter() {

					@Override
					public boolean accept(File f) {

						return ASSETS_DIR_FILTER.accept(f.getName());
					}

				}, list);
			}

			Log.f3(list.size() + " files extracted from assets storage.");

			for (File f : list) {
				String p = f.getAbsolutePath();

				String path = p.replace(target.getAbsolutePath(), "assets/minecraft");

				path = FileUtils.escapeFilename(path);
				String[] parts = FileUtils.getFilenameParts(path);

				// slashes to dots
				String key = parts[0].replace('\\', '.').replace('/', '.');

				String ext = parts[1];

				EAsset type = EAsset.forExtension(ext);

				if (!type.isAsset()) {
					if (Config.LOG_EXTRACTED_ASSETS) Log.f3("SKIPPED " + p);
					continue;
				}

				AssetEntry ae = new AssetEntry(key, type);

				assets.put(key, ae);

				if (Config.LOG_EXTRACTED_ASSETS) Log.f3("+ " + ae.toString());
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

		modsDir = OsUtils.getMcDir("mods");
		
		List<File> list = FileUtils.listDirectory(modsDir);

		List<File> modFiles = new ArrayList<File>();

		FileSuffixFilter fsf = new FileSuffixFilter("jar", "zip");

		// check what mod files are installed
		for (File f : list) {
			if (f.exists() && fsf.accept(f)) {
				modFiles.add(f);
				Log.f3("found mod: " + f.getName());
			}
		}

		// ask user for action
		if (modFiles.size() > 0) {

			Alerts.loading(false);


			modCkboxes = new ArrayList<JCheckBox>();

			VBox vb = new VBox();
			
			
		    
			JCheckBox ck;
			for (File f : modFiles) {
				
				String name = f.getName();
				
				ZipFile modzf;
				try {
					modzf = new ZipFile(f);
				} catch(Exception e) {
					Log.e("Error reading mod file, skipping: "+f.getName());
					continue;
				}
				
				try {
					ZipEntry ze_modinfo = modzf.getEntry("mcmod.info");		
					if(ze_modinfo != null) {
						String json_modinfo = ZipUtils.zipEntryToString(modzf, ze_modinfo);
						ModEntryList mel = ModEntryList.fromJson(json_modinfo);
						name = mel.getModListName();
					}
				} catch(Exception e) {
					Log.e("Broken mcmod.info file in "+f.getName()+"\n"+e.getMessage());
				}
				
				
				modCkboxes.add(ck = new JCheckBox(name, true));
				ck.setActionCommand(f.getName());
			}
			
			Collections.sort(modCkboxes, new Comparator<JCheckBox>() {

				@Override
				public int compare(JCheckBox o1, JCheckBox o2) {

					return o1.getText().compareToIgnoreCase(o2.getText());
				}
				
			});
			
			for(JCheckBox c: modCkboxes) {
				vb.add(c);
			}
		    
		    vb.padding(5, 5, 5, 5);
		    
		    JScrollPane sp = new JScrollPane(vb, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		    sp.setPreferredSize(new Dimension(350, 300));
		    
		    VBox vb2 = new VBox();
		    vb2.gap();
		    HBox hb = new HBox();
		    hb.add(new JLabel("Select mods:"));
		    hb.gap();
		    
		    JButton btnSelAll;
			hb.add(btnSelAll = new JButton("All"));
			
		    JButton btnSelNone;
			hb.add(btnSelNone = new JButton("None"));
			
		    hb.glue();
		    vb2.add(hb);
		    vb2.gap();
		    vb2.add(sp);
		    
		    btnSelAll.addActionListener(new ActionListener() {
		    					
				@Override
				public void actionPerformed(ActionEvent e) {
				
					for(JCheckBox c : modCkboxes) {
						c.setSelected(true);
					}
					
				}
			});
		    
		    btnSelNone.addActionListener(new ActionListener() {
		    					
				@Override
				public void actionPerformed(ActionEvent e) {
				
					for(JCheckBox c : modCkboxes) {
						c.setSelected(false);
					}
					
				}
			});
		    
		    //@formatter:off
		    Object[] params = {
		    		"Some mods have been found.\n" + 
		    		"RPW will include the selected mods.",
		    		vb2
		    }; 
		    //@formatter:on
		    
			//@formatter:off
			int n = JOptionPane.showOptionDialog(
				App.getFrame(), //parent
				params, //message
				"Mods found", //title
				JOptionPane.DEFAULT_OPTION, //option type
				JOptionPane.QUESTION_MESSAGE, // message type
				Icons.DIALOG_QUESTION, // icon
				new String[] {"OK", "Ignore"}, // options
				null // default option
			);
			//@formatter:on

			boolean wantLoadMods = (n == 0);

			Alerts.loading(true);

			if (wantLoadMods) {

				Log.f2("Extracting mod assets");
				// do the work
				int added = 0;
				for (JCheckBox c: modCkboxes) {

					if (!c.isSelected()) continue;

					int oldLen = assets.size();

					File f = new File(modsDir, c.getActionCommand());

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

		Log.f1("Extracting Minecraft assets (" + version + ")");

	}


	@Override
	protected void doAfter(boolean success) {

		if (!success) {
			Log.e("Extracting Minecraft assets - FAILED.");

			//@formatter:off
			Alerts.error(
					App.getFrame(),
					"Extraction failed", 
					"Something went wrong: check the log for details.\n"+
					"\n"+
					"If you think this is a bug, please report it to MightyPork."
			);
			//@formatter:on

			return;
		}

		Log.f1("Extracting Minecraft assets - done.");
		Flags.VANILLA_STRUCTURE_LOAD_OK = true;

		Config.LIBRARY_VERSION = version + "+" + assetsVersion + (modsLoaded ? "m" : "");
		Config.save();

		Tasks.taskUpdateTitlebar(); // update titlebar

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

		Alerts.info(App.getFrame(), "Minecraft assets reloaded.");
	}
}
