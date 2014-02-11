package net.mightypork.rpw;


public class Paths {

	// in user home
	public static final String APP_DIR = "mcRPW";
	public static final String MC_DIR = "minecraft";


	// file and directory names
	public static final String DIRNAME_VANILLA = "vanilla";

	public static final String DIRNAME_PROJECT_PRIVATE = "project_files";
	public static final String DIRNAME_PROJECT_INCLUDE = "included_files";
	public static final String DIRNAME_PROJECT_SOUNDS = "custom_sounds";
	public static final String DIRNAME_PROJECT_LANGUAGES = "custom_languages";
	public static final String FILENAME_PROJECT_FILES = "sources_files.dat";
	public static final String FILENAME_PROJECT_GROUPS = "sources_groups.dat";
	public static final String FILENAME_PROJECT_SOUNDS = "sounds.json";
	public static final String FILENAME_PROJECT_LANGUAGES = "languages.json";
	public static final String FILENAME_PROJECT_CONFIG = "properties.cfg";

	public static final String FILENAME_LOG = "Runtime.log";

	// paths based in workdir	
	public static final String DIR_TMP = "tmp";
	public static final String DIR_LOGS = "logs";
	public static final String DIR_PROJECT_WORKING_COPY_TMP = DIR_TMP + "/workdir";
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
	public static final String DATA_DIR = "/data";
	public static final String DATA_DIR_CHANGELOGS = DATA_DIR + "/changelog/";
	public static final String DATA_DIR_HELP = DATA_DIR + "/help/";
	public static final String DATA_DIR_HTML = DATA_DIR + "/html/";
	public static final String DATA_DIR_IMAGES = "/images/";


	public static final String URL_DONATE = "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=B9X6Q5QU7FKPC";
	public static final String URL_UPDATE_FILE = "http://dl.dropboxusercontent.com/u/64454818/RPW/version.txt";
	public static final String URL_PLANETMINECRAFT_WEB = "http://www.planetminecraft.com/mod/tool-resourcepack-workbench---the-ultimate-pack-creator/";
	public static final String URL_MINECRAFTFORUM_WEB = "http://www.minecraftforum.net/topic/1897669-tool-resourcepack-workbench-v1-the-ultimate-composition-tool/";
	public static final String URL_UPDATE_WEB = URL_PLANETMINECRAFT_WEB;
	public static final String URL_GITHUB_WEB = "https://github.com/MightyPork/rpw/";
	public static final String URL_GITHUB_BUGS = "https://github.com/MightyPork/rpw/issues/new";
	
	public static final String URL_LATEST_DOWNLOAD = "https://dl.dropboxusercontent.com/u/64454818/RPW/RPW-LATEST.jar";


}
