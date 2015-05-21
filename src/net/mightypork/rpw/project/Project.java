package net.mightypork.rpw.project;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Const;
import net.mightypork.rpw.Paths;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.library.MagicSources;
import net.mightypork.rpw.library.Source;
import net.mightypork.rpw.library.Sources;
import net.mightypork.rpw.struct.LangEntryMap;
import net.mightypork.rpw.struct.SoundEntryMap;
import net.mightypork.rpw.tree.assets.AssetEntry;
import net.mightypork.rpw.utils.UpdateHelper;
import net.mightypork.rpw.utils.files.DirectoryTreeDifferenceFinder;
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.files.OsUtils;
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

	private final File backupBase;
	private File privateCopiesBase;
	private File extraIncludesBase;
	private File customSoundsBase;
	private File customLanguagesBase;

	private File fileSourcesFiles;
	private File fileSourcesGroups;
	private File fileSounds;
	private File fileLangs;
	private File fileConfig;

	private final String projectName;
	private String projectTitle;

	private Integer lastRpwVersion;

	private final File projDir;


	public Project(String identifier) {
		projectName = identifier;

		projectTitle = identifier; // by default

		backupBase = OsUtils.getAppDir(Paths.DIR_PROJECT_BACKUP_TMP + "-" + identifier, true);

		projDir = OsUtils.getAppDir(Paths.DIR_PROJECTS + "/" + projectName, false);

		init();
	}


	private File getRealProjectBase()
	{
		return projDir;
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
		Log.f2(getLogPrefix() + " Loading from TMP");

		fileConfig = new File(projDir, Paths.FILENAME_PROJECT_CONFIG);

		props = new PropertyManager(fileConfig, "Project '" + projectName + "' config file");
		props.cfgNewlineBeforeComments(false);
		props.cfgSeparateSections(false);

		props.putString("title", projectTitle);
		props.putInteger("version", Const.VERSION_SERIAL);

		props.renameKey("name", "title"); // change 3.8.3 -> 3.8.4

		props.apply();

		projectTitle = props.getString("title");
		lastRpwVersion = props.getInteger("version");

		privateCopiesBase = new File(projDir, Paths.DIRNAME_PROJECT_PRIVATE);
		extraIncludesBase = new File(projDir, Paths.DIRNAME_PROJECT_EXTRA);
		customSoundsBase = new File(projDir, Paths.DIRNAME_PROJECT_SOUNDS);
		customLanguagesBase = new File(projDir, Paths.DIRNAME_PROJECT_LANGUAGES);

		fileSourcesFiles = new File(projDir, Paths.FILENAME_PROJECT_FILES);
		fileSourcesGroups = new File(projDir, Paths.FILENAME_PROJECT_GROUPS);
		fileSounds = new File(projDir, Paths.FILENAME_PROJECT_SOUNDS);
		fileLangs = new File(projDir, Paths.FILENAME_PROJECT_LANGS);

		try {
			fixProjectStructure();

			// Add new readme and icon
			installDefaultIcon(false);
			installReadme(true);

			if (fileSourcesFiles.exists() && fileSourcesGroups.exists()) {
				files = SimpleConfig.mapFromFile(fileSourcesFiles);
				groups = SimpleConfig.mapFromFile(fileSourcesGroups);

				if (UpdateHelper.needFixProjectKeys(lastRpwVersion)) {
					final Map<String, String> files_fixed = new HashMap<String, String>(files.size());

					for (final Entry<String, String> e : files.entrySet()) {
						files_fixed.put(UpdateHelper.fixProjectKey(e.getKey()), e.getValue());
					}

					files = files_fixed;
				}
			}

			if (fileSounds.exists()) {
				sounds = SoundEntryMap.fromJson(FileUtils.fileToString(fileSounds));
			}

			if (fileLangs.exists()) {
				langs = LangEntryMap.fromJson(FileUtils.fileToString(fileLangs));
			}

			privateCopiesBase.mkdirs();
			extraIncludesBase.mkdirs();
			customSoundsBase.mkdirs();
			customLanguagesBase.mkdirs();

			// just for convenience, to show how it works
			final File tmpFile = new File(extraIncludesBase, "assets/minecraft");
			tmpFile.mkdirs();

			flushMetadata();

		} catch (final Exception e) {
			Log.w(getLogPrefix() + "Project data files could not be loaded.");
			Alerts.error(App.getFrame(), "An arror occured while loading the project.\nPlease, check the log for details.");
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
			oldnew.add(new File[] { new File(projDir, "sources_files.dat"), fileSourcesFiles });
			oldnew.add(new File[] { new File(projDir, "sources_groups.dat"), fileSourcesGroups });

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
			FileUtils.delete(new File(projDir, "README.txt"), false);

			// Renamed included_files/ to extra_files/
			File f = new File(projDir, "included_files");
			if (f.exists() && f.isDirectory()) {
				f.renameTo(extraIncludesBase);
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
	public void flushMetadata() throws IOException
	{
		SimpleConfig.mapToFile(fileSourcesFiles, files, false);
		SimpleConfig.mapToFile(fileSourcesGroups, groups, false);
		FileUtils.stringToFile(fileSounds, sounds.toJson());
		FileUtils.stringToFile(fileLangs, langs.toJson());

		saveProperties();
	}


	public boolean isWorkdirDirty()
	{
		// Flush metadata, which may be the only change
		try {
			flushMetadata();
		} catch (final IOException e) {
			Log.e(e);
		}

		// Compare with backup
		Log.f3(getLogPrefix() + "Finding differences BACKUP:WORKDIR");

		final DirectoryTreeDifferenceFinder comparator = new DirectoryTreeDifferenceFinder(FileUtils.NoGitFilter);

		final boolean retval = !comparator.areEqual(backupBase, projDir);

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


	public void createBackup()
	{
		// clean target
		FileUtils.delete(backupBase, true);

		try {
			Log.f2(getLogPrefix() + "Creating backup copy...");

			FileUtils.copyDirectory(projDir, backupBase, FileUtils.NoGitFilter, null);

			Log.f2(getLogPrefix() + "Copying - done.");

		} catch (final IOException e) {
			Alerts.error(App.getFrame(), "Error", "An error occured while\ncopying to backup folder.");
			Log.e(e);
		}
	}


	private void restoreFromBackup()
	{
		// Delete all but the git folder, keep the folder itself.
		FileUtils.delete(projDir, true, FileUtils.NoGitFilter, false);

		try {
			Log.f2(getLogPrefix() + "Restoring project files from backup.");

			FileUtils.copyDirectory(backupBase, projDir);

			Log.f2(getLogPrefix() + "Restoring - done.");

		} catch (final IOException e) {
			Alerts.error(App.getFrame(), "Error Reverting Changes", "Failed to revert project from backup.\nThe project may have been corrupted.");
			return;
		}

		// Reload project data
		reload();
	}


	/**
	 * Save project properties (title and RPW version)
	 */
	public void saveProperties()
	{
		// all properties
		props.cfgForceSave(true);
		props.setValue("version", Const.VERSION_SERIAL);
		props.setValue("title", projectTitle);
		props.apply();
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


	public void installDefaultIcon(boolean force)
	{
		final File img = new File(projDir, "pack.png");
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


	public void installReadme(boolean force)
	{
		final File file = new File(projDir, "RPW_README.txt");
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

		final File file = new File(privateCopiesBase, path);

		if (!file.exists()) return null;

		return file;
	}


	// ISource
	@Override
	public File getAssetsDirectory()
	{
		return privateCopiesBase;
	}


	public File getExtrasDirectory()
	{
		return extraIncludesBase;
	}


	/**
	 * Project main directory
	 * 
	 * @return workdir
	 */
	public File getProjectDirectory()
	{
		return projDir;
	}


	public File getCustomSoundsDirectory()
	{
		return customSoundsBase;
	}


	public File getCustomLangDirectory()
	{
		return customLanguagesBase;
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
