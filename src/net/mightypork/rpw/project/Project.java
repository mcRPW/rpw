package net.mightypork.rpw.project;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Const;
import net.mightypork.rpw.Paths;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.library.MagicSources;
import net.mightypork.rpw.library.Source;
import net.mightypork.rpw.library.Sources;
import net.mightypork.rpw.struct.SoundEntryMap;
import net.mightypork.rpw.tree.assets.AssetEntry;
import net.mightypork.rpw.utils.*;


public class Project extends Source implements NodeSourceProvider {

	private Map<String, String> files = new LinkedHashMap<String, String>();
	private Map<String, String> groups = new LinkedHashMap<String, String>();
	private SoundEntryMap sounds = new SoundEntryMap();

	private PropertyManager props;

	private File workDirBase = OsUtils.getAppDir(Paths.DIR_PROJECT_WORKING_COPY_TMP, true);
	private File privateCopiesBase;
	private File extraIncludesBase;
	private File customSoundsBase;
	private File fileSourcesFiles;
	private File fileSourcesGroups;
	private File fileSounds;
	private File fileConfig;
	private String dirName;

	private String projectName;
	@SuppressWarnings("unused")
	private Integer lastRpwVersion;


	public Project(String identifier) {

		dirName = identifier;

		projectName = identifier; // by default

		File f = getRealProjectBase();
		f.mkdirs();

		init();
	}


	private File getRealProjectBase() {

		return OsUtils.getAppDir(Paths.DIR_PROJECTS + "/" + dirName, true);
	}


	/**
	 * Initialize fields and (if needed) convert project to new format
	 */
	private void init() {

		copyFromBasedirToTmp();

		fileConfig = new File(workDirBase, Paths.FILENAME_PROJECT_CONFIG);

		props = new PropertyManager(fileConfig, "Project config file");
		props.cfgNewlineBeforeComments(false);
		props.cfgSeparateSections(false);

		props.putString("name", projectName);
		props.putInteger("version", Const.VERSION_SERIAL);
		props.apply();

		projectName = props.getString("name");
		lastRpwVersion = props.getInt("version");

		privateCopiesBase = new File(workDirBase, Paths.DIRNAME_PROJECT_PRIVATE);
		extraIncludesBase = new File(workDirBase, Paths.DIRNAME_PROJECT_INCLUDE);
		customSoundsBase = new File(workDirBase, Paths.DIRNAME_PROJECT_SOUNDS);

		fileSourcesFiles = new File(workDirBase, Paths.FILENAME_PROJECT_FILES);
		fileSourcesGroups = new File(workDirBase, Paths.FILENAME_PROJECT_GROUPS);
		fileSounds = new File(workDirBase, Paths.FILENAME_PROJECT_SOUNDS);

		copyInDefaultIcon(false);

		try {

			if (fileSourcesFiles.exists() && fileSourcesGroups.exists()) {
				files = SimpleConfig.mapFromFile(fileSourcesFiles);
				groups = SimpleConfig.mapFromFile(fileSourcesGroups);
			}

			if (fileSounds.exists()) {
				sounds = SoundEntryMap.fromJson(FileUtils.fileToString(fileSounds));
			}

			privateCopiesBase.mkdirs();
			extraIncludesBase.mkdirs();
			customSoundsBase.mkdirs();

			File tmpFile = new File(extraIncludesBase, "assets/minecraft");
			tmpFile.mkdirs();

			flushToDisk();

		} catch (IOException e) {
			Log.w("Project data files could not be loaded: " + projectName);
		}

	}


	public void flushToDisk() throws IOException {

		Log.f2("Saving ephemeral project data to workdir.");

		SimpleConfig.mapToFile(fileSourcesFiles, files, false);
		SimpleConfig.mapToFile(fileSourcesGroups, groups, false);
		FileUtils.stringToFile(fileSounds, sounds.toJson());

		saveProperties();
	}


	public boolean needsSave() {

		Log.f3("Calculating checksums");

		FastRecursiveDiff comparator = new FastRecursiveDiff();

		boolean retval = !comparator.areEqual(getProjectDirectory(), getRealProjectBase());

		Log.f3("Calculating checksums - done.");

		return retval;
	}


	/**
	 * Save project to real basedir
	 */
	public void save() {

		copyFromTmpToBasedir();
	}


	private void copyFromBasedirToTmp() {

		// delete backup
		FileUtils.delete(workDirBase, true);

		try {

			Log.f2("Copying project to temporary workdir");

			FileUtils.copyDirectory(getRealProjectBase(), workDirBase);

			Log.f2("Copying project to temporary workdir - done.");

		} catch (IOException e) {
			Alerts.error(App.getFrame(), "Error", "An error occured while\nopening the project.");
			Log.e(e);
		}
	}


	private void copyFromTmpToBasedir() {


		File realBase = getRealProjectBase();

		FileUtils.delete(realBase, true);

		try {

			Log.f2("Copying project from workdir to storage");

			FileUtils.copyDirectory(workDirBase, realBase);

			Log.f2("Copying project from workdir to storage - done.");

		} catch (IOException e) {
			Alerts.error(App.getFrame(), "Error Saving Project", "Failed to save project.\nThe project may have been corrupted.");
			return;
		}
	}


	public void saveProperties() {

		// all properties
		props.cfgForceSave(true);
		props.setValue("version", Const.VERSION_SERIAL);
		props.setValue("name", projectName);
		props.apply();
	}


	public void setSourceForGroup(String groupKey, String source) {

		groups.put(groupKey, source);
	}


	public void setSourceForFile(String groupKey, String source) {

		files.put(groupKey, source);
	}


	public void setProjectTitle(String name) {

		projectName = name;
		Projects.markChange();
	}


	public String getProjectName() {

		return projectName;
	}


	public void copyInDefaultIcon(boolean force) {

		File img = new File(workDirBase, "pack.png");
		try {
			if (img.exists() && !force) {
				return;
			}

			InputStream in = FileUtils.getResource("/data/export/pack.png");
			FileOutputStream out = new FileOutputStream(img);
			FileUtils.copyStream(in, out);
		} catch (IOException e) {
			Log.e("Error creating pack title image.", e);
		}
	}


	// NodeSourceProvider
	@Override
	public String getSourceForGroup(String groupKey) {

		String source = groups.get(groupKey);
		if (source == null) return MagicSources.INHERIT;

		return source;
	}


	// NodeSourceProvider
	@Override
	public String getSourceForFile(String assetKey) {

		String source = files.get(assetKey);
		if (source == null) return MagicSources.INHERIT;

		return source;
	}


	// ISource
	@Override
	public File getAssetFile(String key) {

		AssetEntry ae = Sources.vanilla.getAssetForKey(key);

		if (ae == null) {
			Log.w("NULL vanilla asset entry for key: " + key);
			Utils.printStackTrace();
			return null;
		}

		String path = ae.getPath();

		File file = new File(privateCopiesBase, path);

		if (!file.exists()) return null;

		return file;
	}


	// ISource
	@Override
	public File getAssetsBaseDirectory() {

		return privateCopiesBase;
	}


	public File getExtrasDirectory() {

		return extraIncludesBase;
	}


	/**
	 * Working copy directory
	 * 
	 * @return workdir
	 */
	public File getProjectDirectory() {

		return workDirBase;
	}


	public File getCustomSoundsDirectory() {

		return customSoundsBase;
	}


	public String getDirName() {

		return dirName;
	}


	public SoundEntryMap getSoundsMap() {

		return sounds;
	}

}
