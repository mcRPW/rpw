package net.mightypork.rpw.project;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Const;
import net.mightypork.rpw.Paths;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.library.MagicSources;
import net.mightypork.rpw.library.Source;
import net.mightypork.rpw.library.Sources;
import net.mightypork.rpw.struct.SoundEntryMap;
import net.mightypork.rpw.tree.assets.AssetEntry;
import net.mightypork.rpw.utils.UpdateHelper;
import net.mightypork.rpw.utils.Utils;
import net.mightypork.rpw.utils.files.FastRecursiveDiff;
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.files.OsUtils;
import net.mightypork.rpw.utils.files.PropertyManager;
import net.mightypork.rpw.utils.files.SimpleConfig;
import net.mightypork.rpw.utils.logging.Log;


public class Project extends Source implements NodeSourceProvider {

	private Map<String, String> files = new LinkedHashMap<String, String>();
	private Map<String, String> groups = new LinkedHashMap<String, String>();
	private SoundEntryMap sounds = new SoundEntryMap();

	private PropertyManager props;

	private File tmpBase;
	private File privateCopiesBase;
	private File extraIncludesBase;
	private File customSoundsBase;
	private File customLanguagesBase;

	private File fileSourcesFiles;
	private File fileSourcesGroups;
	private File fileSounds;
	@SuppressWarnings("unused")
	private File fileLanguages;
	private File fileConfig;

	private String projectName;
	private String projectTitle;

	private Integer lastRpwVersion;


	public Project(String identifier) {

		projectName = identifier;

		projectTitle = identifier; // by default

		File f = getRealProjectBase();
		f.mkdirs();

		tmpBase = OsUtils.getAppDir(Paths.DIR_PROJECT_WORKING_COPY_TMP + "-" + identifier, true);

		init();
	}


	private File getRealProjectBase() {

		return OsUtils.getAppDir(Paths.DIR_PROJECTS + "/" + projectName, true);
	}


	/**
	 * Initialize fields and (if needed) convert project to new format
	 */
	private void init() {

		copyFromBasedirToTmp();

		reload();
	}


	/**
	 * Load from workdir (discard changes not flushed to disk)
	 */
	public void reload() {

		Log.f2(getLogPrefix() + " Loading from TMP");

		fileConfig = new File(tmpBase, Paths.FILENAME_PROJECT_CONFIG);

		props = new PropertyManager(fileConfig, "Project '" + projectName + "' config file");
		props.cfgNewlineBeforeComments(false);
		props.cfgSeparateSections(false);

		props.putString("title", projectTitle);
		props.putInteger("version", Const.VERSION_SERIAL);

		props.renameKey("name", "title"); // change 3.8.3 -> 3.8.4

		props.apply();

		projectTitle = props.getString("title");
		lastRpwVersion = props.getInteger("version");

		privateCopiesBase = new File(tmpBase, Paths.DIRNAME_PROJECT_PRIVATE);
		extraIncludesBase = new File(tmpBase, Paths.DIRNAME_PROJECT_INCLUDE);
		customSoundsBase = new File(tmpBase, Paths.DIRNAME_PROJECT_SOUNDS);
		customLanguagesBase = new File(tmpBase, Paths.DIRNAME_PROJECT_LANGUAGES);

		fileSourcesFiles = new File(tmpBase, Paths.FILENAME_PROJECT_FILES);
		fileSourcesGroups = new File(tmpBase, Paths.FILENAME_PROJECT_GROUPS);
		fileSounds = new File(tmpBase, Paths.FILENAME_PROJECT_SOUNDS);
		fileLanguages = new File(tmpBase, Paths.FILENAME_PROJECT_LANGUAGES);

		installDefaultIcon(false);
		installReadme(true);

		try {

			if (fileSourcesFiles.exists() && fileSourcesGroups.exists()) {
				files = SimpleConfig.mapFromFile(fileSourcesFiles);
				groups = SimpleConfig.mapFromFile(fileSourcesGroups);

				if (UpdateHelper.needFixProjectKeys(lastRpwVersion)) {
					Map<String, String> files_fixed = new HashMap<String, String>(files.size());

					for (Entry<String, String> e : files.entrySet()) {
						files_fixed.put(UpdateHelper.fixProjectKey(e.getKey()), e.getValue());
					}

					files = files_fixed;
				}
			}

			if (fileSounds.exists()) {
				sounds = SoundEntryMap.fromJson(FileUtils.fileToString(fileSounds));
			}

			privateCopiesBase.mkdirs();
			extraIncludesBase.mkdirs();
			customSoundsBase.mkdirs();
			customLanguagesBase.mkdirs();

			// just for convenience, to show how it works
			File tmpFile = new File(extraIncludesBase, "assets/minecraft");
			tmpFile.mkdirs();

			saveToTmp();

		} catch (IOException e) {
			Log.w(getLogPrefix() + "Project data files could not be loaded.");
		}

	}


	public String getLogPrefix() {

		return "Project '" + projectName + "': ";
	}


	public void saveToTmp() throws IOException {

		SimpleConfig.mapToFile(fileSourcesFiles, files, false);
		SimpleConfig.mapToFile(fileSourcesGroups, groups, false);
		FileUtils.stringToFile(fileSounds, sounds.toJson());

		saveProperties();
	}


	public boolean needsSave() {

		Log.f3(getLogPrefix() + "Finding differences TMP:BASE");

		FastRecursiveDiff comparator = new FastRecursiveDiff();

		boolean retval = !comparator.areEqual(getProjectDirectory(), getRealProjectBase());

		Log.f3(getLogPrefix() + (retval ? "Changes detected, needs save." : "No changes found."));

		return retval;
	}


	/**
	 * Save project to real basedir
	 */
	public void save() {

		copyFromTmpToBasedir();
	}


	private void copyFromBasedirToTmp() {

		// delete (if any) workdir in tmp
		FileUtils.delete(tmpBase, true);

		try {

			Log.f2(getLogPrefix() + "Copying BASE->TMP");

			FileUtils.copyDirectory(getRealProjectBase(), tmpBase);

			Log.f2(getLogPrefix() + "Copying - done.");

		} catch (IOException e) {
			Alerts.error(App.getFrame(), "Error", "An error occured while\nopening the project.");
			Log.e(e);
		}
	}


	private void copyFromTmpToBasedir() {


		File realBase = getRealProjectBase();

		FileUtils.delete(realBase, true);

		try {

			Log.f2(getLogPrefix() + "Copying TMP->BASE");

			FileUtils.copyDirectory(tmpBase, realBase);

			Log.f2(getLogPrefix() + "Copying - done.");

		} catch (IOException e) {
			Alerts.error(App.getFrame(), "Error Saving Project", "Failed to save project.\nThe project may have been corrupted.");
			return;
		}
	}


	public void saveProperties() {

		Log.f3(getLogPrefix() + "Saving properties to TMP");

		// all properties
		props.cfgForceSave(true);
		props.setValue("version", Const.VERSION_SERIAL);
		props.setValue("title", projectTitle);
		props.apply();
	}


	public void setSourceForGroup(String groupKey, String source) {

		groups.put(groupKey, source);
	}


	public void setSourceForFile(String groupKey, String source) {

		files.put(groupKey, source);
	}


	public void setTitle(String title) {

		projectTitle = title;
		Projects.markChange();
	}


	public String getTitle() {

		return projectTitle;
	}


	public void installDefaultIcon(boolean force) {

		File img = new File(tmpBase, "pack.png");
		try {
			if (img.exists() && !force) {
				return;
			}

			Log.f3("Adding default pack icon");

			InputStream in = FileUtils.getResource("/data/export/pack.png");
			FileOutputStream out = new FileOutputStream(img);
			FileUtils.copyStream(in, out);
		} catch (IOException e) {
			Log.e(getLogPrefix() + "Error creating pack title image.", e);
		}
	}


	public void installReadme(boolean force) {

		File img = new File(tmpBase, "README.txt");
		try {
			if (img.exists() && !force) {
				return;
			}

			Log.f3("Adding README.txt to the pack");

			InputStream in = FileUtils.getResource("/data/export/project-readme.txt");
			FileOutputStream out = new FileOutputStream(img);
			FileUtils.copyStream(in, out);
		} catch (IOException e) {
			Log.e(getLogPrefix() + "Error creating readme file.", e);
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
			Log.w(getLogPrefix() + "NULL vanilla asset entry for key: " + key);
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
	public File getAssetsDirectory() {

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

		return tmpBase;
	}


	public File getCustomSoundsDirectory() {

		return customSoundsBase;
	}


	public String getName() {

		return projectName;
	}


	public SoundEntryMap getSoundsMap() {

		return sounds;
	}

}
