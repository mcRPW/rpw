package net.mightypork.rpw.help;

import net.mightypork.rpw.Config;
import net.mightypork.rpw.Const;
import net.mightypork.rpw.Paths;
import net.mightypork.rpw.utils.HtmlBuilder;
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.logging.Log;

import java.util.ArrayList;


public class VersionUtils {

    public static boolean shouldShowChangelog() {
        // Do not show changelog on first run
        if (Config.LAST_RUN_VERSION == Config.def_LAST_RUN_VERSION) return false;

        // Same version as last time - no show
        if (Config.LAST_RUN_VERSION >= Const.VERSION_SERIAL) return false;

        // verify that there's anything to show
        final String chlg = getChangelogForVersion(Const.VERSION_SERIAL);
        return !chlg.isEmpty();
    }


    public static String buildChangelogMd() {
        // markdown document
        String md = "";


        Log.f3("Building changelog...");

        // Collect all the changelogs
        ArrayList<String> versions = new ArrayList<String>();
        int startVersion = Config.LAST_RUN_VERSION;
        for (int i = startVersion; i <= Const.VERSION_SERIAL; i++) {
            final String chl = getChangelogForVersion(i);
            if (chl.isEmpty()) continue;

            String frag = "\n\n<p class=\"littleHeading\">" + getVersionString(i) + "</p>\n\n";
            frag += chl.trim() + "\n";
            versions.add(frag);
        }

        // Get last 2
        for (int i = Math.max(0, versions.size() - 2); i < versions.size(); i++) {
            md += versions.get(i);
        }

        md = md.trim();

        if (md.length() == 0) {
            md = "\n*No changelog found.*\n";
        }

        return md;
    }


    private static String getChangelogForVersion(int version) {
        final String fname = Paths.DATA_DIR_CHANGELOGS + version + ".md";

        Log.f3("Retrieving changelog fragment " + version);

        return FileUtils.getResourceAsString(fname);
    }


    public static String buildChangelogHtml() {
        final String md = buildChangelogMd();
        return HtmlBuilder.markdownToHtmlChangelog(md);
    }


    public static String getVersionString(int version) {
        //@formatter:off
        return String.format(
                "%d.%d.%d",
                (int) Math.floor(version / 100),
                (int) Math.floor((version % 100) / 10),
                (version % 10));
        //@formatter:on
    }


    public static int getVersionMajor(int version) {
        return (int) (Math.floor(version / 10) * 10);
    }
}
