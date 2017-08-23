package net.mightypork.rpw.utils;

import net.mightypork.rpw.Config;


/**
 * Project and library backward compatibility fixes
 *
 * @author ondra
 */
public class Fixins {
    public static boolean needFixLibraryKeys() {
        return (Config.LAST_RUN_VERSION <= 383);
    }


    public static boolean needFixProjectKeys(int lastRpwVersion) {
        return (lastRpwVersion <= 383);
    }


    public static String fixLibraryKey(String key) {
        key = key.replace("%", "%%");
        key = key.replace("{DOT}", "%d");
        key = key.replace("{LBR}", "{");
        key = key.replace("{RBR}", "}");

        return key;
    }


    public static String fixProjectKey(String key) {
        return fixLibraryKey(key); // same functionality
    }
}
