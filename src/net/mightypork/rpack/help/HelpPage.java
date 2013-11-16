package net.mightypork.rpack.help;


import java.io.IOException;
import java.io.InputStream;

import net.mightypork.rpack.Paths;
import net.mightypork.rpack.utils.FileUtils;
import net.mightypork.rpack.utils.Log;


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
				try {
					content = HelpStore.htmlTop + HelpStore.md.process(in) + HelpStore.htmlBottom;
					
				} catch (IOException e) {
					Log.e("Failed to load a help page " + filename, e);
					content = "Failed to load.";
				}
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
