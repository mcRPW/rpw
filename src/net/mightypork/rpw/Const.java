package net.mightypork.rpw;


import net.mightypork.rpw.help.VersionUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class Const {

	public static final int VERSION_SERIAL = 383;

	public static final String VERSION = VersionUtils.getVersionString(VERSION_SERIAL);
	public static final int VERSION_MAJOR = VersionUtils.getVersionMajor(VERSION_SERIAL);

	public static final String APP_NAME = "ResourcePack Workbench";
	public static final String WINDOW_TITLE = APP_NAME + " \u2022 v" + VERSION + "  \u2022  Crafted by MightyPork";

	//@formatter:off
	public static final String[] SOUND_CATEGORIES = new String[] {
		"ambient",
		"block",
		"hostile",
		"master",
		"music",
		"neutral",
		"player",
		"record",
		"weather"
	};
	//@formatter:on

	public static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();
	public static final Gson UGLY_GSON = new GsonBuilder().create();

}
