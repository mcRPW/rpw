package net.mightypork.rpw;

import java.io.File;

import net.mightypork.rpw.utils.files.OsUtils;


public class Paths {
    // in user home
    public static final String APP_DIR = "mcRPW";
    public static final String MC_DIR = "minecraft";

    // file and directory names
    public static final String DIRNAME_VANILLA = "vanilla";

    public static final String DIRNAME_PROJECT_PRIVATE = "project_files";
    public static final String DIRNAME_PROJECT_EXTRA = "extra_files";
    public static final String DIRNAME_PROJECT_SOUNDS = "custom_sounds";
    public static final String DIRNAME_PROJECT_LANGUAGES = "custom_languages";
    public static final String FILENAME_PROJECT_CONFIG = "properties.cfg";
    public static final String FILENAME_PROJECT_FILES = "sources_files.cfg";
    public static final String FILENAME_PROJECT_GROUPS = "sources_groups.cfg";
    public static final String FILENAME_PROJECT_SOUNDS = "sounds.json";
    public static final String FILENAME_PROJECT_LANGS = "languages.json";

    public static final String FILENAME_LOG = "Runtime.log";

    // paths based in workdir
    public static final String DIR_TMP = "tmp";
    public static final String DIR_LOGS = "logs";
    public static final String DIR_PROJECT_BACKUP_TMP = DIR_TMP + "/backup";
    public static final String DIR_LIBRARY = "library";
    public static final String DIR_VANILLA = DIR_LIBRARY + "/" + DIRNAME_VANILLA;
    public static final String DIR_RESOURCEPACKS = DIR_LIBRARY + "/" + "resourcepacks";

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

//    public static final String URL_DONATE = "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=B9X6Q5QU7FKPC";
    public static final String URL_UPDATE_FILE = "https://raw.githubusercontent.com/MightyPork/rpw/master/changelog.txt";
    public static final String URL_RPW_WEB = "https://mcrpw.github.io/";
    public static final String URL_GITHUB_REPO = "https://github.com/mcRPW/rpw/";
    public static final String URL_GITHUB_BUGS = "https://github.com/mcRPW/rpw/issues/new";

    public static final String URL_GITHUB_RELEASES = "https://github.com/mcRPW/rpw/releases";

    public static File getProjectBackupFolder(String identifier) {
        return OsUtils.getAppDir(Paths.DIR_PROJECT_BACKUP_TMP + "-" + identifier, true);
    }


    public static File getProjectFolder(String identifier) {
        return OsUtils.getAppDir(Paths.DIR_PROJECTS + "/" + identifier, true);
    }

}
