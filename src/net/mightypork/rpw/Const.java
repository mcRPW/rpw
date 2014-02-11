package net.mightypork.rpw;


import net.mightypork.rpw.help.VersionUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class Const {

	public static final int VERSION_SERIAL = 384;

	public static final String VERSION = VersionUtils.getVersionString(VERSION_SERIAL);
	public static final int VERSION_MAJOR = VersionUtils.getVersionMajor(VERSION_SERIAL);

	public static final String APP_NAME = "ResourcePack Workbench";

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
