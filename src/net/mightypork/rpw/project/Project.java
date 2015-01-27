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

	private final File tmpBase;
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


	public Project(String identifier) {
		projectName = identifier;

		projectTitle = identifier; // by default

		tmpBase = OsUtils.getAppDir(Paths.DIR_PROJECT_WORKING_COPY_TMP + "-" + identifier, true);

		init();
	}


	private File getRealProjectBase()
	{
		return OsUtils.getAppDir(Paths.DIR_PROJECTS + "/" + projectName, false);
	}


	/**
	 * Initialize fields and (if needed) convert project to new format
	 */
	private void init()
	{
		copyFromBasedirToTmp();

		reload();
	}


	/**
	 * Load from workdir (discard changes not flushed to disk)
	 */
	public void reload()
	{
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
		fileLangs = new File(tmpBase, Paths.FILENAME_PROJECT_LANGS);

		updateProjectStructure();

		installDefaultIcon(false);
		installReadme(true);

		try {
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

			saveToTmp();

		} catch (final Exception e) {
			Log.w(getLogPrefix() + "Project data files could not be loaded.");
			Alerts.error(App.getFrame(), "An arror occured while loading the project.\nPlease, check the log for details.");
		}

	}


	private void updateProjectStructure()
	{
		final List<File[]> oldnew = new ArrayList<File[]>();

		// changed in 3.8.4 to "cfg"
		oldnew.add(new File[] { new File(tmpBase, "sources_files.dat"), fileSourcesFiles });
		oldnew.add(new File[] { new File(tmpBase, "sources_groups.dat"), fileSourcesGroups });

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


	public String getLogPrefix()
	{
		return "Project '" + projectName + "': ";
	}


	public void saveToTmp() throws IOException
	{
		SimpleConfig.mapToFile(fileSourcesFiles, files, false);
		SimpleConfig.mapToFile(fileSourcesGroups, groups, false);
		FileUtils.stringToFile(fileSounds, sounds.toJson());
		FileUtils.stringToFile(fileLangs, langs.toJson());

		saveProperties();
	}


	public boolean needsSave()
	{
		Log.f3(getLogPrefix() + "Finding differences TMP:BASE");

		final DirectoryTreeDifferenceFinder comparator = new DirectoryTreeDifferenceFinder();

		final boolean retval = !comparator.areEqual(getProjectDirectory(), getRealProjectBase());

		Log.f3(getLogPrefix() + (retval ? "Changes detected, needs save." : "No changes found."));

		return retval;
	}


	/**
	 * Save project to real basedir
	 */
	public void save()
	{
		try {
			saveToTmp();
		} catch (final IOException e) {
			Log.e(e);
		}

		copyFromTmpToBasedir();
	}


	private void copyFromBasedirToTmp()
	{
		// delete (if any) workdir in tmp
		FileUtils.delete(tmpBase, true);

		try {
			Log.f2(getLogPrefix() + "Copying BASE->TMP");

			FileUtils.copyDirectory(getRealProjectBase(), tmpBase);

			Log.f2(getLogPrefix() + "Copying - done.");

		} catch (final IOException e) {
			Alerts.error(App.getFrame(), "Error", "An error occured while\nopening the project.");
			Log.e(e);
		}
	}


	private void copyFromTmpToBasedir()
	{
		final File realBase = getRealProjectBase();

		FileUtils.delete(realBase, true);

		try {
			Log.f2(getLogPrefix() + "Copying TMP->BASE");

			FileUtils.copyDirectory(tmpBase, realBase);

			Log.f2(getLogPrefix() + "Copying - done.");

		} catch (final IOException e) {
			Alerts.error(App.getFrame(), "Error Saving Project", "Failed to save project.\nThe project may have been corrupted.");
			return;
		}
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
		final File img = new File(tmpBase, "pack.png");
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
		final File img = new File(tmpBase, "README.txt");
		try {
			if (img.exists() && !force) {
				return;
			}

			Log.f3("Adding README.txt to the pack");

			FileUtils.resourceToFile("/data/export/project-readme.txt", img);
		} catch (final IOException e) {
			Log.e(getLogPrefix() + "Error creating readme file.", e);
		}
	}


	// NodeSourceProvider
	@Override
	public String getSourceForGroup(String groupKey)
	{
		final String source = groups.get(groupKey);
		if (source == null)
			return MagicSources.INHERIT;

		return source;
	}


	// NodeSourceProvider
	@Override
	public String getSourceForFile(String assetKey)
	{
		final String source = files.get(assetKey);
		if (source == null)
			return MagicSources.INHERIT;

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

		if (!file.exists())
			return null;

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
	 * Working copy directory
	 * 
	 * @return workdir
	 */
	public File getProjectDirectory()
	{
		return tmpBase;
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
