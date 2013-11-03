package net.mightypork.rpack.project;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import net.mightypork.rpack.Paths;
import net.mightypork.rpack.hierarchy.AssetEntry;
import net.mightypork.rpack.library.MagicSources;
import net.mightypork.rpack.library.Source;
import net.mightypork.rpack.library.Sources;
import net.mightypork.rpack.utils.FileUtils;
import net.mightypork.rpack.utils.Log;
import net.mightypork.rpack.utils.OsUtils;
import net.mightypork.rpack.utils.PropertyManager;
import net.mightypork.rpack.utils.SimpleConfig;


public class Project extends Source implements NodeSourceProvider {

	private Map<String, String> files = new LinkedHashMap<String, String>();
	private Map<String, String> groups = new LinkedHashMap<String, String>();
	private PropertyManager cfg;

	private File projectBase;
	private File privateCopiesBase;
	private File extraIncludesBase;
	private File fileSourcesFiles;
	private File fileSourcesGroups;
	private File fileConfig;
	private String dirName;

	private String projectName;


	public Project(String identifier) {

		dirName = identifier;

		// make project directory
		projectBase = OsUtils.getAppDir(Paths.DIR_PROJECTS + "/" + identifier, true);
		privateCopiesBase = new File(projectBase, Paths.DIRNAME_PROJECT_PRIVATE);
		extraIncludesBase = new File(projectBase, Paths.DIRNAME_PROJECT_INCLUDE);

		fileSourcesFiles = new File(projectBase, Paths.FILENAME_PROJECT_FILES);
		fileSourcesGroups = new File(projectBase, Paths.FILENAME_PROJECT_GROUPS);

		fileConfig = new File(projectBase, Paths.FILENAME_PROJECT_CONFIG);

		cfg = new PropertyManager(fileConfig, "Project config file");
		cfg.putString("name", identifier);
		cfg.apply();
		projectName = cfg.getString("name");

		copyInDefaultIcon(false);

		try {
			load();
		} catch (IOException e) {
			Log.w("Project data files could not be loaded: " + identifier);
		}
	}


	public void copyInDefaultIcon(boolean force) {

		File img = new File(projectBase, "pack.png");
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


	public void load() throws IOException {

		if (fileSourcesFiles.exists() && fileSourcesGroups.exists()) {
			files = SimpleConfig.mapFromFile(fileSourcesFiles);
			groups = SimpleConfig.mapFromFile(fileSourcesGroups);
		}

		privateCopiesBase.mkdirs();
		extraIncludesBase.mkdirs();
		
		File asmc = new File(extraIncludesBase, "assets/minecraft");
		asmc.mkdirs();
	}


	public void save() throws IOException {

		files.remove(null);
		groups.remove(null);

		SimpleConfig.mapToFile(fileSourcesFiles, files);
		SimpleConfig.mapToFile(fileSourcesGroups, groups);

		saveProperties();
	}


	public void saveProperties() {

		// all properties
		cfg.setValue("name", projectName);
		cfg.apply();
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


	// ISource
	@Override
	public File getAssetFile(String key) {

		AssetEntry ae = Sources.vanilla.getAssetForKey(key);

		if (ae == null) {
			Log.w("NULL vanilla asset entry for key: " + key);
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


	public File getProjectDirectory() {

		return projectBase;
	}


	public String getDirName() {

		return dirName;
	}

//
//	@Override
//	public InputStream getAssetMetaStream(String key) throws IOException {
//
//		File f = getAssetFile(key);
//		if(f == null) return null;
//		
//		String p = f.getPath();
//		p += ".mcmeta";
//		f = new File(p);
//		
//		if(!f.exists()) return null;
//		
//		return new FileInputStream(f);
//	}

}
