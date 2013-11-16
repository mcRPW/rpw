package net.mightypork.rpack;


import net.mightypork.rpack.utils.Log;
import net.mightypork.rpack.utils.OsUtils;
import net.mightypork.rpack.utils.PropertyManager;


/**
 * Main Config class
 * 
 * @author MightyPork
 */
public class Config {

	public static final boolean def_FANCY_GROUPS = true;
	public static final boolean def_SHOW_FONT = false;
	public static final boolean def_SHOW_LANG = false;
	public static final boolean def_PREVIEW_HOVER = true;
	public static final boolean def_SHOW_HIDDEN_IN_FILEPICKER = true;
	public static final boolean def_CLOSED_WITH_PROJECT_OPEN = false;
	public static final boolean def_USE_INTERNAL_META_EDITOR = true;
	public static final boolean def_USE_INTERNAL_TEXT_EDITOR = true;
	public static final boolean def_WARNING_ORPHANED_NODES = true;
	public static boolean FANCY_GROUPS;
	public static boolean SHOW_FONT;
	public static boolean SHOW_LANG;
	public static boolean PREVIEW_HOVER;
	public static boolean SHOW_HIDDEN_FILES;
	public static boolean CLOSED_WITH_PROJECT_OPEN;
	public static boolean USE_INTERNAL_META_EDITOR;
	public static boolean USE_INTERNAL_TEXT_EDITOR;
	public static boolean WARNING_ORPHANED_NODES;

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

	private static PropertyManager mgr;

	private static final String PK_FANCY_GROUPS = "display.fancyTree";
	private static final String PK_CLOSED_WITH_PROJECT_OPEN = "status.closedWithProjectOpen";
	private static final String PK_SHOW_FONT = "assets.showFont";
	private static final String PK_SHOW_LANG = "assets.showLang";
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

	private static final String PK_AUDIO_EDITOR = "system.editor.audio.command";
	private static final String PK_AUDIO_EDITOR_ARGS = "system.editor.audio.command.args";
	private static final String PK_USE_AUDIO_EDITOR = "system.editor.audio.enabled";


	/**
	 * Prepare config manager and load user settings
	 */
	public static void init() {

		Log.f2("Initializing configuration manager");

		//@formatter:off
		String comment = 
				Const.APP_NAME + " config file\n" +
				"\n" +
				"This app was designed to work well on KDE and linux in general.\n" +
				"It's not tested, but it should work just fine on MacOS, too.\n" +
				"\n" +
				"To get it work right on Windows, replace the system.editor.* commands\n" +
				"with paths to EXE files of your favorite applications.";
		//@formatter:on
		mgr = new PropertyManager(OsUtils.getAppDir(Paths.FILE_CONFIG), comment);

		mgr.cfgNewlineBeforeComments(true);
		mgr.cfgSeparateSections(true);

		mgr.putBoolean(PK_FANCY_GROUPS, def_FANCY_GROUPS, "Show assets using human-readable groups");
		mgr.putBoolean(PK_SHOW_FONT, def_SHOW_FONT, "Show unicode font textures (the ugly thin font)");
		mgr.putBoolean(PK_SHOW_LANG, def_SHOW_LANG, "Show translation files (*.lang)");
		mgr.putBoolean(PK_PREVIEW_HOVER, def_PREVIEW_HOVER, "Display preview of item under mouse");
		mgr.putBoolean(PK_SHOW_HIDDEN_IN_FILEPICKER, def_SHOW_HIDDEN_IN_FILEPICKER, "Show hidden files in file pickers (import, export)");
		mgr.putBoolean(PK_CLOSED_WITH_PROJECT_OPEN, def_CLOSED_WITH_PROJECT_OPEN);
		mgr.putBoolean(PK_USE_INTERNAL_META_EDITOR, def_USE_INTERNAL_META_EDITOR, "Use internal editor, ignore configured command.");
		mgr.putBoolean(PK_USE_INTERNAL_TEXT_EDITOR, def_USE_INTERNAL_TEXT_EDITOR, "Use internal editor, ignore configured command.");
		mgr.putBoolean(PK_WARNING_ORPHANED_NODES, def_WARNING_ORPHANED_NODES, "Warn when some nodes could not be displayed.");

		mgr.putString(PK_IMAGE_EDITOR, def_IMAGE_EDITOR);
		mgr.putString(PK_IMAGE_EDITOR_ARGS, def_IMAGE_EDITOR_ARGS);
		mgr.putBoolean(PK_USE_IMAGE_EDITOR, def_USE_IMAGE_EDITOR, "Use command instead of system default.");

		mgr.putString(PK_AUDIO_EDITOR, def_AUDIO_EDITOR);
		mgr.putString(PK_AUDIO_EDITOR_ARGS, def_AUDIO_EDITOR_ARGS);
		mgr.putBoolean(PK_USE_AUDIO_EDITOR, def_USE_AUDIO_EDITOR, "Use command instead of system default.");

		mgr.putString(PK_TEXT_EDITOR, def_TEXT_EDITOR);
		mgr.putString(PK_TEXT_EDITOR_ARGS, def_TEXT_EDITOR_ARGS);
		mgr.putBoolean(PK_USE_TEXT_EDITOR, def_USE_TEXT_EDITOR, "Use command instead of system default.");

		load(); // load what has been "put"

		Log.f2("Initializing configuration manager - done.");
	}


	/**
	 * Save changed fields to config file
	 */
	public static void save() {

		Log.f3("Saving configuration file.");

		mgr.setValue(PK_FANCY_GROUPS, FANCY_GROUPS);
		mgr.setValue(PK_SHOW_FONT, SHOW_FONT);
		mgr.setValue(PK_SHOW_LANG, SHOW_LANG);
		mgr.setValue(PK_PREVIEW_HOVER, PREVIEW_HOVER);
		mgr.setValue(PK_SHOW_HIDDEN_IN_FILEPICKER, SHOW_HIDDEN_FILES);
		mgr.setValue(PK_CLOSED_WITH_PROJECT_OPEN, CLOSED_WITH_PROJECT_OPEN);
		mgr.setValue(PK_USE_INTERNAL_META_EDITOR, USE_INTERNAL_META_EDITOR);
		mgr.setValue(PK_USE_INTERNAL_TEXT_EDITOR, USE_INTERNAL_TEXT_EDITOR);
		mgr.setValue(PK_WARNING_ORPHANED_NODES, WARNING_ORPHANED_NODES);

		mgr.setValue(PK_IMAGE_EDITOR, IMAGE_EDITOR);
		mgr.setValue(PK_IMAGE_EDITOR_ARGS, IMAGE_EDITOR_ARGS);
		mgr.setValue(PK_USE_IMAGE_EDITOR, USE_IMAGE_EDITOR);

		mgr.setValue(PK_AUDIO_EDITOR, AUDIO_EDITOR);
		mgr.setValue(PK_AUDIO_EDITOR_ARGS, AUDIO_EDITOR_ARGS);
		mgr.setValue(PK_USE_AUDIO_EDITOR, USE_AUDIO_EDITOR);

		mgr.setValue(PK_TEXT_EDITOR, TEXT_EDITOR);
		mgr.setValue(PK_TEXT_EDITOR_ARGS, TEXT_EDITOR_ARGS);
		mgr.setValue(PK_USE_TEXT_EDITOR, USE_TEXT_EDITOR);

		mgr.apply();
	}


	/**
	 * Load config file and assign values to fields
	 */
	public static void load() {

		mgr.apply();
		FANCY_GROUPS = mgr.getBoolean(PK_FANCY_GROUPS);
		SHOW_FONT = mgr.getBoolean(PK_SHOW_FONT);
		SHOW_LANG = mgr.getBoolean(PK_SHOW_LANG);
		PREVIEW_HOVER = mgr.getBoolean(PK_PREVIEW_HOVER);
		SHOW_HIDDEN_FILES = mgr.getBoolean(PK_SHOW_HIDDEN_IN_FILEPICKER);
		CLOSED_WITH_PROJECT_OPEN = mgr.getBoolean(PK_CLOSED_WITH_PROJECT_OPEN);
		USE_INTERNAL_META_EDITOR = mgr.getBoolean(PK_USE_INTERNAL_META_EDITOR);
		USE_INTERNAL_TEXT_EDITOR = mgr.getBoolean(PK_USE_INTERNAL_TEXT_EDITOR);
		WARNING_ORPHANED_NODES = mgr.getBoolean(PK_WARNING_ORPHANED_NODES);

		IMAGE_EDITOR = mgr.getString(PK_IMAGE_EDITOR);
		IMAGE_EDITOR_ARGS = mgr.getString(PK_IMAGE_EDITOR_ARGS);
		USE_IMAGE_EDITOR = mgr.getBoolean(PK_USE_IMAGE_EDITOR);

		AUDIO_EDITOR = mgr.getString(PK_AUDIO_EDITOR);
		AUDIO_EDITOR_ARGS = mgr.getString(PK_AUDIO_EDITOR_ARGS);
		USE_AUDIO_EDITOR = mgr.getBoolean(PK_USE_AUDIO_EDITOR);

		TEXT_EDITOR = mgr.getString(PK_TEXT_EDITOR);
		TEXT_EDITOR_ARGS = mgr.getString(PK_TEXT_EDITOR_ARGS);
		USE_TEXT_EDITOR = mgr.getBoolean(PK_USE_TEXT_EDITOR);
	}


	public static final boolean LOG_TO_STDOUT = true;
	public static final boolean LOG_ON = true;
	public static final boolean LOG_FILTERS = false;
	public static final boolean LOG_GROUPS = false;
	public static final boolean LOG_FILTERS_DETAILED = false;
	public static final boolean LOG_ZIP_EXTRACTING = false;
	public static final boolean LOG_VANILLA_LOAD_STRUCTURE = false;
	public static final boolean LOG_EXPORT = true;

	public static final boolean USE_LOADER_WINDOW = false;
	public static final boolean LOG_HELP_LOADING = false;

}
