package net.mightypork.rpw.help;

import net.mightypork.rpw.Paths;
import net.mightypork.rpw.utils.HtmlBuilder;
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.logging.Log;


public class HelpPage {

    private final String name;
    private String content;


    public HelpPage(String title, String filename) {
        this.name = title;

        final String str = FileUtils.resourceToString(Paths.DATA_DIR_HELP + filename);

        if (str.length() == 0) {
            Log.w("Missing help page " + filename);
            content = "Page not found.";
        } else {
            content = HtmlBuilder.markdownToHtmlHelp(str);
        }
    }


    public String getName() {
        return name;
    }


    public String getContent() {
        return content;
    }

}
