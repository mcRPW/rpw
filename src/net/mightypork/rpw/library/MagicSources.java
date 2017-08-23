package net.mightypork.rpw.library;

public class MagicSources {

    public static final String INHERIT = "";
    public static final String VANILLA = "*vanilla*";
    public static final String PROJECT = "*project*";
    public static final String SILENCE = "*silence*";


    public static boolean isMagic(String source) {
        boolean isMagic = false;

        isMagic |= source.equals(INHERIT);
        isMagic |= source.equals(VANILLA);
        isMagic |= source.equals(SILENCE);
        isMagic |= source.equals(PROJECT);

        return isMagic;
    }


    public static boolean isInherit(String source) {
        return source.equals(INHERIT);
    }


    public static boolean isVanilla(String source) {
        return source.equals(VANILLA);
    }


    public static boolean isProject(String source) {
        return source.equals(PROJECT);
    }


    public static boolean isSilence(String source) {
        return source.equals(SILENCE);
    }
}
