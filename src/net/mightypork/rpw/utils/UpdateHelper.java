package net.mightypork.rpw.utils;

import net.mightypork.rpw.Config;
import net.mightypork.rpw.Const;


public class UpdateHelper {
	
	public static boolean needFixLibraryKeys() {
		return (Config.LAST_RUN_VERSION <= 383);
	}

	public static boolean needFixProjectKeys(int lastRpwVersion) {
		return (lastRpwVersion <= 383);
	}
	
	public static String fixLibraryKey(String key) {
		if(needFixLibraryKeys()) {
			key = key.replace("%", "%%");
			key = key.replace("{DOT}", "%d");
			key = key.replace("{LBR}", "{");
			key = key.replace("{RBR}", "}");
		}
		return key;
	}
}
