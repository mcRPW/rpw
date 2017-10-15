package net.mightypork.rpw;

import net.mightypork.rpw.gui.widgets.MenuMain;
import net.mightypork.rpw.utils.files.OsUtils;
import net.mightypork.rpw.utils.files.PropertyManager;
import net.mightypork.rpw.utils.logging.Log;


/**
 * Main Config class
 *
 * @author Ondřej Hruška (MightyPork)
 */
public class Config {
    public enum FilePath {
        IMPORT_FILE, IMPORT_SOUND, IMPORT_PACK, EXPORT, DEFAULT;

        public String getPath() {
            switch (this) {
                case IMPORT_FILE:
                    return Config.FILECHOOSER_PATH_IMPORT_FILE;

                case IMPORT_SOUND:
                    return Config.FILECHOOSER_PATH_IMPORT_SOUND;

                case IMPORT_PACK:
                    return Config.FILECHOOSER_PATH_IMPORT_PACK;

                case EXPORT:
                    return Config.FILECHOOSER_PATH_EXPORT;

                default:
                    return System.getProperty("user.home");
            }
        }


        public void savePath(String path) {
            switch (this) {
                case IMPORT_FILE:
                    Config.FILECHOOSER_PATH_IMPORT_FILE = path;
                    break;

                case IMPORT_SOUND:
                    Config.FILECHOOSER_PATH_IMPORT_SOUND = path;
                    break;

                case IMPORT_PACK:
                    Config.FILECHOOSER_PATH_IMPORT_PACK = path;
                    break;

                case EXPORT:
                    Config.FILECHOOSER_PATH_EXPORT = path;
                    break;
                default:
                    // can't do anything
            }

            Config.save();
        }
    }

    // opts
    public static final boolean def_FANCY_GROUPS = true;
    public static final boolean def_SHOW_FONT = true;
    public static final boolean def_SHOW_OBSOLETE_DIRS = false;
    public static final boolean def_SHOW_LANG = true;
    public static final boolean def_SHOW_SOUNDS = true;
    public static final boolean def_SHOW_TEXTS = true;
    public static final boolean def_SHOW_TEXTURES = true;
    public static final boolean def_SHOW_TECHNICAL = true;
    public static final boolean def_PREVIEW_HOVER = false;
    public static final boolean def_AUTO_SAVE = false;
    public static final boolean def_SHOW_HIDDEN_IN_FILEPICKER = true;
    public static final boolean def_CLOSED_WITH_PROJECT_OPEN = false;
    public static final int def_LAST_RUN_VERSION = 0;
    public static final int def_CHOICE_EXPORT_TO_MC = 0;
    public static final String def_LIBRARY_VERSION = "UNKNOWN";
    public static final boolean def_USE_INTERNAL_META_EDITOR = true;
    public static final boolean def_USE_INTERNAL_TEXT_EDITOR = true;
    public static final boolean def_WARNING_ORPHANED_NODES = true;
    public static final boolean def_USE_NIMBUS = true;
    public static final boolean def_USE_NATIVE_THEME = false;
    public static boolean FANCY_TREE;
    public static boolean SHOW_FONT;
    public static boolean SHOW_SOUNDS;
    public static boolean SHOW_TECHNICAL;
    public static boolean SHOW_TEXTS;
    public static boolean SHOW_TEXTURES;
    public static boolean SHOW_LANG;
    public static boolean SHOW_OBSOLETE_DIRS;
    public static boolean AUTO_SAVE;
    public static boolean PREVIEW_HOVER;
    public static boolean SHOW_HIDDEN_FILES;
    public static boolean USE_INTERNAL_META_EDITOR;
    public static boolean USE_INTERNAL_TEXT_EDITOR;
    public static boolean WARNING_ORPHANED_NODES;
    public static boolean CLOSED_WITH_PROJECT_OPEN;
    public static int LAST_RUN_VERSION;
    public static int CHOICE_EXPORT_TO_MC;
    public static String LIBRARY_VERSION;
    public static boolean USE_NIMBUS;
    public static boolean USE_NATIVE_THEME;

    // filechooser paths
    public static final String def_FILECHOOSER_PATH_IMPORT_FILE = System.getProperty("user.home");
    public static final String def_FILECHOOSER_PATH_IMPORT_SOUND = System.getProperty("user.home");
    public static final String def_FILECHOOSER_PATH_IMPORT_PACK = System.getProperty("user.home");
    public static final String def_FILECHOOSER_PATH_EXPORT = System.getProperty("user.home");

    private static String FILECHOOSER_PATH_IMPORT_FILE;
    private static String FILECHOOSER_PATH_IMPORT_SOUND;
    private static String FILECHOOSER_PATH_IMPORT_PACK;
    private static String FILECHOOSER_PATH_EXPORT;

    // editors
    public static final boolean def_USE_IMAGE_EDITOR = true;
    public static final String def_IMAGE_EDITOR = "/usr/bin/gimp";
    public static final String def_IMAGE_EDITOR_ARGS = "%s";
    public static boolean USE_IMAGE_EDITOR = true;
    public static String IMAGE_EDITOR;
    public static String IMAGE_EDITOR_ARGS;

    public static final boolean def_USE_TEXT_EDITOR = true;
    public static final String def_TEXT_EDITOR = "/usr/bin/kwrite";
    public static final String def_TEXT_EDITOR_ARGS = "%s";
    public static boolean USE_TEXT_EDITOR = true;
    public static String TEXT_EDITOR;
    public static String TEXT_EDITOR_ARGS;

    public static final boolean def_USE_AUDIO_EDITOR = true;
    public static final String def_AUDIO_EDITOR = "/usr/bin/audacity";
    public static final String def_AUDIO_EDITOR_ARGS = "%s";
    public static boolean USE_AUDIO_EDITOR = true;
    public static String AUDIO_EDITOR;
    public static String AUDIO_EDITOR_ARGS;

    public static final boolean def_USE_MODEL_EDITOR = true;
    public static final String def_MODEL_EDITOR = "/usr/bin/blockbench";
    public static final String def_MODEL_EDITOR_ARGS = "%s";
    public static boolean USE_MODEL_EDITOR = true;
    public static String MODEL_EDITOR;
    public static String MODEL_EDITOR_ARGS;

    private static PropertyManager mgr;

    private static final String PK_FANCY_GROUPS = "display.fancyTree";
    private static final String PK_SHOW_FONT = "assets.showFont";
    private static final String PK_SHOW_LANG = "assets.showLang";
    private static final String PK_SHOW_SOUNDS = "assets.showSounds";
    private static final String PK_SHOW_TECHNICAL = "assets.showTechnical";
    private static final String PK_SHOW_TEXTS = "assets.showText";
    private static final String PK_SHOW_TEXTURES = "assets.showTextures";
    private static final String PK_SHOW_OBSOLETE_DIRS = "assets.showObsoleteDirs";
    private static final String PK_PREVIEW_HOVER = "display.previewOnHover";
    private static final String PK_SHOW_HIDDEN_IN_FILEPICKER = "system.showHiddenFiles";
    private static final String PK_USE_INTERNAL_META_EDITOR = "system.editor.useInternalMetaEditor";
    private static final String PK_USE_INTERNAL_TEXT_EDITOR = "system.editor.useInternalTextEditor";
    private static final String PK_WARNING_ORPHANED_NODES = "display.warning.orphanedNodes";

    private static final String PK_IMAGE_EDITOR = "system.editor.image.command";
    private static final String PK_IMAGE_EDITOR_ARGS = "system.editor.image.args";
    private static final String PK_USE_IMAGE_EDITOR = "system.editor.image.enabled";

    private static final String PK_TEXT_EDITOR = "system.editor.text.command";
    private static final String PK_TEXT_EDITOR_ARGS = "system.editor.text.args";
    private static final String PK_USE_TEXT_EDITOR = "system.editor.text.enabled";

    private static final String PK_USE_NIMBUS = "display.nimbusTheme";
    private static final String PK_USE_NATIVE_THEME = "display.nativeTheme";
    private static final String PK_AUTO_SAVE = "system.autoSave";

    private static final String PK_AUDIO_EDITOR = "system.editor.audio.command";
    private static final String PK_AUDIO_EDITOR_ARGS = "system.editor.audio.command.args";
    private static final String PK_USE_AUDIO_EDITOR = "system.editor.audio.enabled";

    private static final String PK_MODEL_EDITOR = "system.editor.model.command";
    private static final String PK_MODEL_EDITOR_ARGS = "system.editor.model.command.args";
    private static final String PK_USE_MODEL_EDITOR = "system.editor.model.enabled";

    public static final String PK_FILECHOOSER_PATH_IMPORT_FILE = "status.path.importFile";
    public static final String PK_FILECHOOSER_PATH_IMPORT_SOUND = "status.path.importCustomSound";
    public static final String PK_FILECHOOSER_PATH_IMPORT_PACK = "status.path.importPack";
    public static final String PK_FILECHOOSER_PATH_EXPORT = "status.path.exportPack";
    private static final String PK_CLOSED_WITH_PROJECT_OPEN = "status.closedWithProjectOpen";
    private static final String PK_LAST_RUN_VERSION = "status.lastRunVersion";
    private static final String PK_LIBRARY_VERSION = "status.libraryVersion";
    private static final String PK_CHOICE_EXPORT_TO_MC = "status.exportToMcChoice";


    /**
     * Prepare config manager and load user settings
     */
    public static void init() {
        Log.f2("Initializing configuration manager.");

        //@formatter:off
        final String comment =
                Const.APP_NAME + " config file\n\n" +
                        "RPW *must* be closed while you modify the settings,\n" +
                        "otherwise they will be overwritten.";
        //@formatter:on

        mgr = new PropertyManager(OsUtils.getAppDir(Paths.FILE_CONFIG), comment);

        mgr.cfgNewlineBeforeComments(true);
        mgr.cfgSeparateSections(true);

        mgr.putBoolean(PK_FANCY_GROUPS, def_FANCY_GROUPS, "Show assets using human-readable groups");
        mgr.putBoolean(PK_SHOW_FONT, def_SHOW_FONT, "Show unicode font textures (the ugly thin font)");
        mgr.putBoolean(PK_SHOW_LANG, def_SHOW_LANG, "Show translation files (*.lang)");
        mgr.putBoolean(PK_SHOW_SOUNDS, def_SHOW_SOUNDS, "Show sound files");
        mgr.putBoolean(PK_SHOW_TEXTS, def_SHOW_TEXTS, "Show text files in tree view");
        mgr.putBoolean(PK_SHOW_TEXTURES, def_SHOW_TEXTURES, "Show textures in tree view");
        mgr.putBoolean(PK_SHOW_TECHNICAL, def_SHOW_TECHNICAL, "Show shaders, blockstates and other weird things");
        mgr.putBoolean(PK_SHOW_OBSOLETE_DIRS, def_SHOW_OBSOLETE_DIRS, "Show obsolete directories and groups (eg. the pre-1.7 sounds)");
        mgr.putBoolean(PK_PREVIEW_HOVER, def_PREVIEW_HOVER, "Display preview of item under mouse");
        mgr.putBoolean(PK_SHOW_HIDDEN_IN_FILEPICKER, def_SHOW_HIDDEN_IN_FILEPICKER, "Show hidden files in file pickers (import, export)");

        mgr.putBoolean(PK_USE_INTERNAL_META_EDITOR, def_USE_INTERNAL_META_EDITOR, "Use internal editor, ignore configured command.");
        mgr.putBoolean(PK_USE_INTERNAL_TEXT_EDITOR, def_USE_INTERNAL_TEXT_EDITOR, "Use internal editor, ignore configured command.");
        mgr.putBoolean(PK_WARNING_ORPHANED_NODES, def_WARNING_ORPHANED_NODES, "Warn when some assets could not be displayed.");

        mgr.putBoolean(PK_AUTO_SAVE, def_AUTO_SAVE, "Save automatically on close, don't ask.");

        mgr.putString(PK_IMAGE_EDITOR, def_IMAGE_EDITOR);
        mgr.putString(PK_IMAGE_EDITOR_ARGS, def_IMAGE_EDITOR_ARGS);
        mgr.putBoolean(PK_USE_IMAGE_EDITOR, def_USE_IMAGE_EDITOR, "Use command instead of system default.");

        mgr.putString(PK_AUDIO_EDITOR, def_AUDIO_EDITOR);
        mgr.putString(PK_AUDIO_EDITOR_ARGS, def_AUDIO_EDITOR_ARGS);
        mgr.putBoolean(PK_USE_AUDIO_EDITOR, def_USE_AUDIO_EDITOR, "Use command instead of system default.");

        mgr.putString(PK_MODEL_EDITOR, def_MODEL_EDITOR);
        mgr.putString(PK_MODEL_EDITOR_ARGS, def_MODEL_EDITOR_ARGS);
        mgr.putBoolean(PK_USE_MODEL_EDITOR, def_USE_MODEL_EDITOR, "Use command instead of system default.");

        mgr.putString(PK_TEXT_EDITOR, def_TEXT_EDITOR);
        mgr.putString(PK_TEXT_EDITOR_ARGS, def_TEXT_EDITOR_ARGS);
        mgr.putBoolean(PK_USE_TEXT_EDITOR, def_USE_TEXT_EDITOR, "Use command instead of system default.");

        mgr.putBoolean(PK_CLOSED_WITH_PROJECT_OPEN, def_CLOSED_WITH_PROJECT_OPEN);
        mgr.putInteger(PK_LAST_RUN_VERSION, def_LAST_RUN_VERSION);
        mgr.putString(PK_LIBRARY_VERSION, def_LIBRARY_VERSION);
        mgr.putInteger(PK_CHOICE_EXPORT_TO_MC, def_CHOICE_EXPORT_TO_MC);

        mgr.putString(PK_FILECHOOSER_PATH_IMPORT_FILE, def_FILECHOOSER_PATH_IMPORT_FILE);
        mgr.putString(PK_FILECHOOSER_PATH_IMPORT_SOUND, def_FILECHOOSER_PATH_IMPORT_SOUND);
        mgr.putString(PK_FILECHOOSER_PATH_IMPORT_PACK, def_FILECHOOSER_PATH_IMPORT_PACK);
        mgr.putString(PK_FILECHOOSER_PATH_EXPORT, def_FILECHOOSER_PATH_EXPORT);

        mgr.putBoolean(PK_USE_NIMBUS, def_USE_NIMBUS, "Use the Nimbus theme instead of Metal (default).");
        mgr.putBoolean(PK_USE_NATIVE_THEME, def_USE_NATIVE_THEME, "Use the system native theme if available (if Nimbus is not selected).");
        mgr.putInteger(PK_CHOICE_EXPORT_TO_MC, def_CHOICE_EXPORT_TO_MC);

        load(); // load what has been "put"
    }


    /**
     * Save changed fields to config file
     */
    public static void save() {
        mgr.setValue(PK_FANCY_GROUPS, FANCY_TREE);
        mgr.setValue(PK_SHOW_FONT, SHOW_FONT);
        mgr.setValue(PK_SHOW_LANG, SHOW_LANG);
        mgr.setValue(PK_SHOW_SOUNDS, SHOW_SOUNDS);
        mgr.setValue(PK_SHOW_TECHNICAL, SHOW_TECHNICAL);
        mgr.setValue(PK_SHOW_TEXTS, SHOW_TEXTS);
        mgr.setValue(PK_SHOW_TEXTURES, SHOW_TEXTURES);
        mgr.setValue(PK_SHOW_OBSOLETE_DIRS, SHOW_OBSOLETE_DIRS);
        mgr.setValue(PK_PREVIEW_HOVER, PREVIEW_HOVER);
        mgr.setValue(PK_SHOW_HIDDEN_IN_FILEPICKER, SHOW_HIDDEN_FILES);
        mgr.setValue(PK_USE_INTERNAL_META_EDITOR, USE_INTERNAL_META_EDITOR);
        mgr.setValue(PK_USE_INTERNAL_TEXT_EDITOR, USE_INTERNAL_TEXT_EDITOR);
        mgr.setValue(PK_WARNING_ORPHANED_NODES, WARNING_ORPHANED_NODES);

        mgr.setValue(PK_AUTO_SAVE, AUTO_SAVE);

        mgr.setValue(PK_IMAGE_EDITOR, IMAGE_EDITOR);
        mgr.setValue(PK_IMAGE_EDITOR_ARGS, IMAGE_EDITOR_ARGS);
        mgr.setValue(PK_USE_IMAGE_EDITOR, USE_IMAGE_EDITOR);

        mgr.setValue(PK_AUDIO_EDITOR, AUDIO_EDITOR);
        mgr.setValue(PK_AUDIO_EDITOR_ARGS, AUDIO_EDITOR_ARGS);
        mgr.setValue(PK_USE_AUDIO_EDITOR, USE_AUDIO_EDITOR);

        mgr.setValue(PK_TEXT_EDITOR, TEXT_EDITOR);
        mgr.setValue(PK_TEXT_EDITOR_ARGS, TEXT_EDITOR_ARGS);
        mgr.setValue(PK_USE_TEXT_EDITOR, USE_TEXT_EDITOR);

        mgr.setValue(PK_MODEL_EDITOR, MODEL_EDITOR);
        mgr.setValue(PK_MODEL_EDITOR_ARGS, MODEL_EDITOR_ARGS);
        mgr.setValue(PK_USE_MODEL_EDITOR, USE_MODEL_EDITOR);

        mgr.setValue(PK_USE_NIMBUS, USE_NIMBUS);
        mgr.setValue(PK_USE_NATIVE_THEME, USE_NATIVE_THEME);

        mgr.setValue(PK_FILECHOOSER_PATH_IMPORT_FILE, FILECHOOSER_PATH_IMPORT_FILE);
        mgr.setValue(PK_FILECHOOSER_PATH_IMPORT_SOUND, FILECHOOSER_PATH_IMPORT_SOUND);
        mgr.setValue(PK_FILECHOOSER_PATH_IMPORT_PACK, FILECHOOSER_PATH_IMPORT_PACK);
        mgr.setValue(PK_FILECHOOSER_PATH_EXPORT, FILECHOOSER_PATH_EXPORT);
        mgr.setValue(PK_CLOSED_WITH_PROJECT_OPEN, CLOSED_WITH_PROJECT_OPEN);
        mgr.setValue(PK_LAST_RUN_VERSION, Const.VERSION_SERIAL); // LAST_RUN_VERSION field stays unchanged until restart.
        mgr.setValue(PK_LIBRARY_VERSION, LIBRARY_VERSION);
        mgr.setValue(PK_CHOICE_EXPORT_TO_MC, CHOICE_EXPORT_TO_MC);

        mgr.apply();

        MenuMain m = App.getMenu();
        if (m != null) {
            m.updateOptionCheckboxes();
        }
    }


    /**
     * Load config file and assign values to fields
     */
    public static void load() {
        mgr.apply();

        FANCY_TREE = mgr.getBoolean(PK_FANCY_GROUPS);
        SHOW_FONT = mgr.getBoolean(PK_SHOW_FONT);
        SHOW_LANG = mgr.getBoolean(PK_SHOW_LANG);
        SHOW_SOUNDS = mgr.getBoolean(PK_SHOW_SOUNDS);
        SHOW_TECHNICAL = mgr.getBoolean(PK_SHOW_TECHNICAL);
        SHOW_TEXTS = mgr.getBoolean(PK_SHOW_TEXTS);
        SHOW_TEXTURES = mgr.getBoolean(PK_SHOW_TEXTURES);
        SHOW_OBSOLETE_DIRS = mgr.getBoolean(PK_SHOW_OBSOLETE_DIRS);
        PREVIEW_HOVER = mgr.getBoolean(PK_PREVIEW_HOVER);
        SHOW_HIDDEN_FILES = mgr.getBoolean(PK_SHOW_HIDDEN_IN_FILEPICKER);
        CLOSED_WITH_PROJECT_OPEN = mgr.getBoolean(PK_CLOSED_WITH_PROJECT_OPEN);
        USE_INTERNAL_META_EDITOR = mgr.getBoolean(PK_USE_INTERNAL_META_EDITOR);
        USE_INTERNAL_TEXT_EDITOR = mgr.getBoolean(PK_USE_INTERNAL_TEXT_EDITOR);
        WARNING_ORPHANED_NODES = mgr.getBoolean(PK_WARNING_ORPHANED_NODES);

        AUTO_SAVE = mgr.getBoolean(PK_AUTO_SAVE);

        IMAGE_EDITOR = mgr.getString(PK_IMAGE_EDITOR);
        IMAGE_EDITOR_ARGS = mgr.getString(PK_IMAGE_EDITOR_ARGS);
        USE_IMAGE_EDITOR = mgr.getBoolean(PK_USE_IMAGE_EDITOR);

        AUDIO_EDITOR = mgr.getString(PK_AUDIO_EDITOR);
        AUDIO_EDITOR_ARGS = mgr.getString(PK_AUDIO_EDITOR_ARGS);
        USE_AUDIO_EDITOR = mgr.getBoolean(PK_USE_AUDIO_EDITOR);

        TEXT_EDITOR = mgr.getString(PK_TEXT_EDITOR);
        TEXT_EDITOR_ARGS = mgr.getString(PK_TEXT_EDITOR_ARGS);
        USE_TEXT_EDITOR = mgr.getBoolean(PK_USE_TEXT_EDITOR);

        MODEL_EDITOR = mgr.getString(PK_MODEL_EDITOR);
        MODEL_EDITOR_ARGS = mgr.getString(PK_MODEL_EDITOR_ARGS);
        USE_MODEL_EDITOR = mgr.getBoolean(PK_USE_MODEL_EDITOR);

        USE_NIMBUS = mgr.getBoolean(PK_USE_NIMBUS);
        USE_NATIVE_THEME = mgr.getBoolean(PK_USE_NATIVE_THEME);

        FILECHOOSER_PATH_IMPORT_FILE = mgr.getString(PK_FILECHOOSER_PATH_IMPORT_FILE);
        FILECHOOSER_PATH_IMPORT_SOUND = mgr.getString(PK_FILECHOOSER_PATH_IMPORT_SOUND);
        FILECHOOSER_PATH_IMPORT_PACK = mgr.getString(PK_FILECHOOSER_PATH_IMPORT_PACK);
        FILECHOOSER_PATH_EXPORT = mgr.getString(PK_FILECHOOSER_PATH_EXPORT);

        LAST_RUN_VERSION = mgr.getInteger(PK_LAST_RUN_VERSION);
        LIBRARY_VERSION = mgr.getString(PK_LIBRARY_VERSION);
        CHOICE_EXPORT_TO_MC = mgr.getInteger(PK_CHOICE_EXPORT_TO_MC);
    }

    // options that can't be configured via config file

    public static final boolean LOGGING_ENABLED = true;

    public static final boolean LOG_TO_STDOUT = true;

    public static final boolean LOG_FILTERS = false;
    public static final boolean LOG_GROUPS = false;

    public static final boolean LOG_FILTERS_DETAILED = false;
    public static final boolean LOG_VANILLA_LOAD_STRUCTURE = false;

    public static final boolean LOG_HELP_LOADING = false;

    public static final boolean LOG_EXPORT_FILES = true;

    public static final boolean LOG_EXTRACTED_ASSETS = true;
}
