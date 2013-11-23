package net.mightypork.rpw;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class Const {

	public static final String VERSION = "3.8";
	public static final int VERSION_SERIAL = 380;

	public static final String APP_NAME = "ResourcePack Workbench";
	public static final String WINDOW_TITLE = APP_NAME + "  \u2022  v." + VERSION + "  \u2022  Crafted by MightyPork";

	public static final String UPDATE_URL = "http://dl.dropboxusercontent.com/u/64454818/RPW/version.txt";
	public static final String WEB_URL = "http://www.planetminecraft.com/mod/tool-resourcepack-workbench---the-ultimate-pack-creator/";

	public static final String[] SOUND_CATEGORIES = new String[] { "ambient", "block", "hostile", "master", "music", "neutral", "player", "record", "weather" };

	public static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();
	public static final Gson UGLY_GSON = new GsonBuilder().create();

}
