package net.mightypork.rpw.help;


import net.mightypork.rpw.Config;
import net.mightypork.rpw.Const;
import net.mightypork.rpw.Paths;
import net.mightypork.rpw.utils.HtmlBuilder;
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.logging.Log;


public class VersionUtils {

	public static boolean shouldShowChangelog() {

		return Config.LAST_RUN_VERSION < Const.VERSION_SERIAL;
	}


	public static String buildChangelogMd() {

		String document = "";

		int from = Const.VERSION_MAJOR;

		for (int i = Math.max(Const.VERSION_SERIAL - 2, Math.max(Config.LAST_RUN_VERSION, from) + 1); i <= Const.VERSION_SERIAL; i++) {
			String chl = getChangelogForVersion(i);
			if (chl == null) continue;

			document += "\n\n<p class=\"littleHeading\">" + getVersionString(i) + "</p>\n\n";

			document += chl.trim() + "\n";
		}

		document = document.trim();

		if (document.length() == 0) {
			document = "\n*No changelog found.*\n";
		}

		return document;
	}


	private static String getChangelogForVersion(int version) {


		String fname = Paths.DATA_DIR_CHANGELOGS + version + ".md";

		Log.f3("Retrieving changelog fragment " + version);

		return FileUtils.getResourceAsString(fname);
	}


	public static String buildChangelogHtml() {

		String md = buildChangelogMd();
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
