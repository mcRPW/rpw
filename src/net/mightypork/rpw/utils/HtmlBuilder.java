package net.mightypork.rpw.utils;


import java.io.InputStream;

import net.mightypork.rpw.Paths;

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

		InputStream in;

		in = FileUtils.getResource(Paths.DATA_DIR_HTML + "html_base_top.html");
		htmlBaseTop = FileUtils.streamToString(in);

		in = FileUtils.getResource(Paths.DATA_DIR_HTML + "html_help_top.html");
		htmlHelpTop = FileUtils.streamToString(in);

		in = FileUtils.getResource(Paths.DATA_DIR_HTML + "html_bottom.html");
		htmlBottom = FileUtils.streamToString(in);
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
