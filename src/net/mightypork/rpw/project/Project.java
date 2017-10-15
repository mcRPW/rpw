package net.mightypork.rpw.project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Config;
import net.mightypork.rpw.Const;
import net.mightypork.rpw.Paths;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.library.MagicSources;
import net.mightypork.rpw.library.Source;
import net.mightypork.rpw.library.Sources;
import net.mightypork.rpw.struct.LangEntry;
import net.mightypork.rpw.struct.LangEntryMap;
import net.mightypork.rpw.struct.SoundEntryMap;
import net.mightypork.rpw.tasks.sequences.SequenceExportProject;
import net.mightypork.rpw.tree.assets.AssetEntry;
import net.mightypork.rpw.utils.Fixins;
import net.mightypork.rpw.utils.files.DirectoryTreeDifferenceFinder;
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.files.PropertyManager;
import net.mightypork.rpw.utils.files.SimpleConfig;
import net.mightypork.rpw.utils.logging.Log;


public class Project extends Source implements NodeSourceProvider
{
	private Map<String, String> files = new LinkedHashMap<String, String>();
	private Map<String, String> groups = new LinkedHashMap<String, String>();
	private SoundEntryMap sounds = new SoundEntryMap();
	private LangEntryMap langs = new LangEntryMap();

	private PropertyManager props;

	private final File projectBase;
	private final File backupBase;
	private File assetsBase;
	private File extrasBase;
	private File customSoundsBase;
	private File customLangsBase;
	private File fileSourcesFiles;
	private File fileSourcesGroups;
	private File fileSounds;
	private File fileLangs;
	private File fileConfig;

	private final String projectName;
	private String projectTitle;
	private String projectDescription;

	private Integer lastRpwVersion;

	private int exportPackVersion;
	private boolean unZip;
	private String currentMcVersion;

	public Project(String identifier) {
		projectName = identifier;
		projectTitle = identifier; // by default
		projectDescription = " ";
		currentMcVersion = " ";

		backupBase = Paths.getProjectBackupFolder(identifier);
		projectBase = Paths.getProjectFolder(identifier);

		init();
	}


	/**
	 * Initialize fields and (if needed) convert project to new format
	 */
	private void init()
	{
		createBackup();

		reload();
	}


	/**
	 * Load from workdir (discard changes not flushed to disk)
	 */
	public void reload()
	{
		Log.f2(getLogPrefix() + "Loading from workdir");

		fileConfig = new File(projectBase, Paths.FILENAME_PROJECT_CONFIG);

		assetsBase = new File(projectBase, Paths.DIRNAME_PROJECT_PRIVATE);
		extrasBase = new File(projectBase, Paths.DIRNAME_PROJECT_EXTRA);
		customSoundsBase = new File(projectBase, Paths.DIRNAME_PROJECT_SOUNDS);
		customLangsBase = new File(projectBase, Paths.DIRNAME_PROJECT_LANGUAGES);

		fileSourcesFiles = new File(projectBase, Paths.FILENAME_PROJECT_FILES);
		fileSourcesGroups = new File(projectBase, Paths.FILENAME_PROJECT_GROUPS);
		fileSounds = new File(projectBase, Paths.FILENAME_PROJECT_SOUNDS);
		fileLangs = new File(projectBase, Paths.FILENAME_PROJECT_LANGS);

		loadProps();

		try {
			fixProjectStructure();

			// Add new readme and icon
			installDefaultIcon(false);
			installReadme(true);

			// Read extra config files
			if (fileSourcesFiles.exists()) {
				files = SimpleConfig.mapFromFile(fileSourcesFiles);
			}

			if (fileSourcesGroups.exists()) {
				groups = SimpleConfig.mapFromFile(fileSourcesGroups);
			}

			if (fileSounds.exists()) {
				sounds = SoundEntryMap.fromJson(FileUtils.fileToString(fileSounds));
			}

			if (fileLangs.exists()) {
				langs = LangEntryMap.fromJson(FileUtils.fileToString(fileLangs));
			}

			assetsBase.mkdirs();
			extrasBase.mkdirs();
			customSoundsBase.mkdirs();
			customLangsBase.mkdirs();

			// Placeholder to show how the folder works
			new File(extrasBase, "assets/minecraft").mkdirs();

			// Flush config files in case they changed
			saveConfigFiles();

		} catch (final Exception e) {
			Log.w(getLogPrefix() + "Project data files could not be loaded.");
			Alerts.error(App.getFrame(), "An arror occured while loading the project.\nPlease, check the log for details.");
		}

	}


	private void loadProps()
	{
		props = new PropertyManager(fileConfig, "Project '" + projectName + "' config file");
		props.cfgNewlineBeforeComments(false);
		props.cfgSeparateSections(false);

		props.putString("title", projectTitle);
		props.putString("description", projectDescription);
        props.putString("currentMcVersion", currentMcVersion);
		props.putInteger("version", Const.VERSION_SERIAL);

		props.renameKey("name", "title"); // change 3.8.3 -> 3.8.4

		props.apply();

		projectTitle = props.getString("title");
		projectDescription = props.getString("description");
		currentMcVersion = props.getString("currentMcVersion");
		lastRpwVersion = props.getInteger("version");
	}


	private void fixFileKeys()
	{
		if (Fixins.needFixProjectKeys(lastRpwVersion)) {
			final Map<String, String> files_fixed = new HashMap<String, String>(files.size());

			for (final Entry<String, String> e : files.entrySet()) {
				files_fixed.put(Fixins.fixProjectKey(e.getKey()), e.getValue());
			}

			this.files = files_fixed;
		}
	}


	/**
	 * Fix changes in project structure
	 */
	private void fixProjectStructure()
	{
		final List<File[]> oldnew = new ArrayList<File[]>();

		if (lastRpwVersion < 384) {
			// changed in 3.8.4 to "cfg"
			oldnew.add(new File[] { new File(projectBase, "sources_files.dat"), fileSourcesFiles });
			oldnew.add(new File[] { new File(projectBase, "sources_groups.dat"), fileSourcesGroups });

			for (final File[] ff : oldnew) {
				if (ff[0].exists()) {
					try {
						FileUtils.copyFile(ff[0], ff[1]);
						FileUtils.delete(ff[0], false);
					} catch (final IOException e) {
						Log.e(e);
					}
				}
			}
		}

		if (lastRpwVersion <= 400) {

			// Delete old readme, so new readme can be created with changed name
			FileUtils.delete(new File(projectBase, "README.txt"), false);

			// Renamed included_files/ to extra_files/
			File f = new File(projectBase, "included_files");
			if (f.exists() && f.isDirectory()) {
				f.renameTo(extrasBase);
			}
		}
	}


	public String getLogPrefix()
	{
		return "Project '" + projectName + "': ";
	}


	/**
	 * Flush project metadata to workdir
	 *
	 * @throws IOException
	 */
	public void saveConfigFiles() throws IOException
	{
		try {
			SimpleConfig.mapToFile(fileSourcesFiles, files, false);
			SimpleConfig.mapToFile(fileSourcesGroups, groups, false);
			FileUtils.stringToFile(fileSounds, sounds.toJson()); // NPE here. Why, how??? #45
            FileUtils.stringToFile(fileLangs, langs.toJson());
		} catch (NullPointerException e) {
			Log.e("NPE from issue #45 happened again", e);
		}

		// all properties
		props.cfgForceSave(true);
		props.setValue("version", Const.VERSION_SERIAL);
		props.setValue("title", projectTitle);
		props.setValue("description", projectDescription);
        props.setValue("currentMcVersion", currentMcVersion);

		props.apply();
	}


	/**
	 * Check if there's a difference between the working directory and the
	 * backup folder.
	 *
	 * @return
	 */
	public boolean isWorkdirDirty()
	{
		// Flush metadata, which may be the only change
		try {
			saveConfigFiles();
		} catch (final IOException e) {
			Log.e(e);
		}

		// Compare with backup
		Log.f3(getLogPrefix() + "Finding differences BACKUP:WORKDIR");

		final DirectoryTreeDifferenceFinder comparator = new DirectoryTreeDifferenceFinder(FileUtils.NoGitFilter);

		final boolean retval = !comparator.areEqual(backupBase, projectBase);

		Log.f3(getLogPrefix() + (retval ? "Changes detected" : "No changes found."));

		return retval;
	}


	/**
	 * Revert all project changes
	 */
	public void revert()
	{
		Log.f2("Reverting project changes, restoring from backup.");
		restoreFromBackup();
	}


	/**
	 * Copy workdir to a backup folder
	 */
	public void createBackup()
	{
		// clean target
		FileUtils.delete(backupBase, true);

		try {
			Log.f2(getLogPrefix() + "Creating backup copy...");

			FileUtils.copyDirectory(projectBase, backupBase, FileUtils.NoGitFilter, null);

			Log.f2(getLogPrefix() + "Copying - done.");

		} catch (final IOException e) {
			Alerts.error(App.getFrame(), "Error", "An error occured while\ncopying to backup folder.");
			Log.e(e);
		}
	}


	private void restoreFromBackup()
	{
		// Delete all but the git folder, keep the folder itself.
		FileUtils.delete(projectBase, true, FileUtils.NoGitFilter, false);

		try {
			Log.f2(getLogPrefix() + "Restoring project files from backup.");

			FileUtils.copyDirectory(backupBase, projectBase);

			Log.f2(getLogPrefix() + "Restoring - done.");

		} catch (final IOException e) {
			Alerts.error(App.getFrame(), "Error Reverting Changes", "Failed to revert project from backup.\nThe project may have been corrupted.");
			return;
		}

		// Reload project data
		reload();
	}


	public void setSourceForGroup(String groupKey, String source)
	{
		groups.put(groupKey, source);
	}


	public void setSourceForFile(String fileKey, String source)
	{
		files.put(fileKey, source);
	}


	/**
	 * @param title
	 *            visual project title
	 */
	public void setTitle(String title)
	{
		projectTitle = title;
		Projects.markChange();
	}


	/**
	 * @return visual project title
	 */
	public String getTitle()
	{
		return projectTitle;
	}

	public void setDescription(String description){
		projectDescription = description;
		Projects.markChange();
	}

	public String getDescription(){
		return projectDescription;
	}

	public void setExportPackVersion(int packVersion){
		exportPackVersion = packVersion;
	}

	public int getExportPackVersion(){
		return exportPackVersion;
	}

	public void setUnZip(boolean unzip){
	    unZip = unzip;
	}

	public boolean getUnzip(){return unZip;}

	public String getCurrentMcVersion(){
	    return currentMcVersion;
	}

	public void setCurrentMcVersion(String currentMcVersion){this.currentMcVersion = currentMcVersion;}

	public LangEntryMap getCustomLanguages() { return langs; }

    public void addToCustomLanguages(LangEntry language) {
	    if(langs == null){
	        langs = new LangEntryMap();
        }

	    langs.put(language.name, language);
    }

	public void installDefaultIcon(boolean force)
	{
		final File img = new File(projectBase, "pack.png");
		try {
			if (img.exists() && !force) {
				return;
			}

			Log.f3("Adding default pack icon");

			FileUtils.resourceToFile("/data/export/pack.png", img);
		} catch (final IOException e) {
			Log.e(getLogPrefix() + "Error creating pack title image.", e);
		}
	}


	private void installReadme(boolean force)
	{
		final File file = new File(projectBase, "RPW_README.txt");
		try {
			if (file.exists() && !force) {
				return;
			}

			Log.f3("Adding RPW_README.txt to the pack");

			FileUtils.resourceToFile("/data/export/project-readme.txt", file);
		} catch (final IOException e) {
			Log.e(getLogPrefix() + "Error creating readme file.", e);
		}
	}


	// NodeSourceProvider
	@Override
	public String getSourceForGroup(String groupKey)
	{
		final String source = groups.get(groupKey);
		if (source == null) return MagicSources.INHERIT;

		return source;
	}


	// NodeSourceProvider
	@Override
	public String getSourceForFile(String assetKey)
	{
		final String source = files.get(assetKey);
		if (source == null) return MagicSources.INHERIT;

		return source;
	}


	// ISource
	@Override
	public File getAssetFile(String key)
	{
		final AssetEntry ae = Sources.vanilla.getAssetForKey(key);

		if (ae == null) {
			Log.w(getLogPrefix() + "NULL vanilla asset entry for key: " + key);
			return null;
		}

		final String path = ae.getPath();

		final File file = new File(assetsBase, path);

		if (!file.exists()) return null;

		return file;
	}


	// ISource
	@Override
	public File getAssetsDirectory()
	{
		return assetsBase;
	}


	public File getExtrasDirectory()
	{
		return extrasBase;
	}


	/**
	 * Project main directory
	 *
	 * @return workdir
	 */
	public File getProjectDirectory()
	{
		return projectBase;
	}


	public File getCustomSoundsDirectory()
	{
		return customSoundsBase;
	}


	public File getCustomLangDirectory()
	{
		return customLangsBase;
	}


	/**
	 * @return project name (save directory name)
	 */
	public String getName()
	{
		return projectName;
	}


	public SoundEntryMap getSoundsMap()
	{
		return sounds;
	}


	public LangEntryMap getLangMap()
	{
		return langs;

	}


	public void setSoundMap(SoundEntryMap soundmap)
	{
		this.sounds = soundmap;
	}


	public void setLangMap(LangEntryMap langmap)
	{
		this.langs = langmap;
	}

}

