package net.mightypork.rpack;


public class Paths {

	// in user home
	public static final String APP_DIR = "mcRPW";
	public static final String MC_DIR = "minecraft";


	// file and directory names
	public static final String DIRNAME_VANILLA = "vanilla";

	public static final String DIRNAME_PROJECT_PRIVATE = "project_files";
	public static final String DIRNAME_PROJECT_INCLUDE = "included_files";
	public static final String DIRNAME_PROJECT_SOUNDS = "custom_sounds";
	public static final String FILENAME_PROJECT_FILES = "sources_files.dat";
	public static final String FILENAME_PROJECT_GROUPS = "sources_groups.dat";
	public static final String FILENAME_PROJECT_CONFIG = "properties.cfg";
	public static final String FILENAME_LOG = "Runtime.log";

	// paths based in workdir	
	public static final String DIR_LIBRARY = "library";
	public static final String DIR_VANILLA = DIR_LIBRARY + "/" + DIRNAME_VANILLA;
	public static final String DIR_RESOURCEPACKS = DIR_LIBRARY + "/" + "resourcepacks";
	public static final String FILE_VANILLA_STRUCTURE = DIR_VANILLA + "/structure.dat";

	public static final String DIR_PROJECTS = "projects";

	public static final String DIR_CONFIG = "config";
	public static final String FILE_CONFIG = DIR_CONFIG + "/settings.cfg";
	public static final String FILE_CFG_MODGROUPS = DIR_CONFIG + "/modGroups.cfg";
	public static final String FILE_CFG_MODFILTERS = DIR_CONFIG + "/modFilters.cfg";
	public static final String FILE_RECENTPROJECTS = DIR_CONFIG + "/recentProjects.dat";

	public static final String FILE_LOG = FILENAME_LOG;

	// paths used for internal resources
	public static final String DATA_DIR_HELP = "/data/help/";
	public static final String DATA_DIR_IMAGES = "/images/";

}
