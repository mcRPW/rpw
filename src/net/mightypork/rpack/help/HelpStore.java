package net.mightypork.rpack.help;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.mightypork.rpack.Config;
import net.mightypork.rpack.Paths;
import net.mightypork.rpack.utils.FileUtils;
import net.mightypork.rpack.utils.Log;
import net.mightypork.rpack.utils.SimpleConfig;

import org.markdown4j.Markdown4jProcessor;


public class HelpStore {

	private static List<HelpPage> pages = new ArrayList<HelpPage>();
	static Markdown4jProcessor md = new Markdown4jProcessor();

	static String htmlTop;
	static String htmlBottom;


	public static void load() {
		
		Log.f2("Loading help pages");

		InputStream in;

		in = FileUtils.getResource(Paths.DATA_DIR_HELP + "index.txt");
		String text = FileUtils.streamToString(in);
		Map<String, String> pageMap = SimpleConfig.mapFromString(text);

		in = FileUtils.getResource(Paths.DATA_DIR_HELP + "html_top.html");
		htmlTop = FileUtils.streamToString(in);

		in = FileUtils.getResource(Paths.DATA_DIR_HELP + "html_bottom.html");
		htmlBottom = FileUtils.streamToString(in);

		for (Entry<String, String> entry : pageMap.entrySet()) {
			
			if(Config.LOG_HELP_LOADING) Log.f3("Loading file: "+entry.getKey()+" (\""+entry.getValue()+"\")");
				
			try {
				pages.add(new HelpPage(entry.getValue(), entry.getKey()));
				
			} catch (Exception e) {
				Log.w("Error while loading a help page " + entry.getKey());
			}

		}

		Log.f2("Loading help pages - done.");
	}
	
	
	public static List<HelpPage> getPages() {

		return pages;
	}
}
