package net.mightypork.rpw.utils;


import net.mightypork.rpw.Paths;
import net.mightypork.rpw.utils.files.FileUtils;

import org.markdown4j.CodeBlockEmitter;
import org.markdown4j.ExtDecorator;

import com.github.rjeschke.txtmark.Configuration;
import com.github.rjeschke.txtmark.Configuration.Builder;
import com.github.rjeschke.txtmark.Processor;


public class HtmlBuilder {

	static Builder mdBuilder;

	static String htmlBaseTop;
	static String htmlHelpTop;
	static String htmlBottom;


	public static void init() {

		mdBuilder = Configuration.builder();
		mdBuilder.forceExtentedProfile();
		mdBuilder.setDecorator(new ExtDecorator());
		mdBuilder.setCodeBlockEmitter(new CodeBlockEmitter());

		htmlBaseTop = FileUtils.resourceToString(Paths.DATA_DIR_HTML + "html_base_top.html");
		htmlHelpTop = FileUtils.resourceToString(Paths.DATA_DIR_HTML + "html_help_top.html");
		htmlBottom = FileUtils.resourceToString(Paths.DATA_DIR_HTML + "html_bottom.html");
	}


	public static String markdownToHtmlBase(String markdown) {

		return markdownToHtml(markdown, htmlBaseTop);
	}


	public static String markdownToHtmlHelp(String markdown) {

		return markdownToHtml(markdown, htmlHelpTop);
	}


	public static String markdownToHtml(String markdown, String top) {

		String md = Processor.process(markdown, mdBuilder.build());
		return top + md + htmlBottom;
	}
}
