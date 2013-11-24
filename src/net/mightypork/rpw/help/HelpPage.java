package net.mightypork.rpw.help;


import java.io.IOException;
import java.io.InputStream;

import net.mightypork.rpw.Paths;
import net.mightypork.rpw.utils.FileUtils;
import net.mightypork.rpw.utils.HtmlBuilder;
import net.mightypork.rpw.utils.Log;


public class HelpPage {

	private String name;
	private String content;


	public HelpPage(String title, String filename) {

		this.name = title;

		InputStream in = null;

		try {
			in = FileUtils.getResource(Paths.DATA_DIR_HELP + filename);

			if (in == null) {
				Log.w("Missing help page " + filename);
				content = "Page not found.";
			} else {
				String str = FileUtils.streamToString(in);
				content = HtmlBuilder.markdownToHtmlHelp(str);
			}

		} finally {
			if (in != null) try {
				in.close();
			} catch (IOException e) {
				Log.e(e);
			}
		}
	}


	public String getName() {

		return name;
	}


	public String getContent() {

		return content;
	}

}
