package net.mightypork.rpw.tasks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;

import net.mightypork.rpw.App;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.struct.VersionInfo;
import net.mightypork.rpw.tasks.sequences.SequenceExtractAssets;
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.files.OsUtils;
import net.mightypork.rpw.utils.logging.Log;


public class TaskExtractAssets {
    public static void run(String version) {
        (new SequenceExtractAssets(version)).run();
    }


    /**
     * Ask user for the level to use
     *
     * @param isInitial is this the first startup?
     * @return MC version selected
     */
    public static String getUserChoice(boolean isInitial) {
        final String initial = "To start a ResourcePack Workbench, the\n" +
                "default pack must be extracted from\n" +
                "your Minecraft folder.\n\n" +
                "Please, select a Minecraft version to use:";

        final String user = "Minecraft assets will be re-extracted.\n\n" +
                "If you have any mods installed, you'll be\n" +
                "offered to extract their assets too.\n\n" +
                "Please, select a Minecraft version to use:";

        // obtain applicable versions
        final List<File> list = FileUtils.listDirectory(OsUtils.getMcDir("versions"));

        final List<String> opts = new ArrayList<String>();

        Log.f2("Looking for installed Minecraft versions.");
        for (final File f : list) {
            if (f.exists() && f.isDirectory()) {
                final String version = f.getName();
                Log.f3("Version " + version);

                final File jar = new File(f, version + ".jar");
                if (!jar.exists() || !jar.isFile()) {
                    Log.w("- no jar, skipping");
                    continue;
                }

                final File json = new File(f, version + ".json");
                if (!json.exists() || !json.isFile()) {
                    Log.w("- no json, skipping");
                    continue;
                }

                String s;
                try {
                    s = FileUtils.fileToString(json);
                } catch (final IOException e) {
                    Log.w("- couldn't load json, skipping");
                    continue;
                }

                if (s.isEmpty()) {
                    Log.w("- Empty or missing version file, skipping.");
                    continue;
                }

                try {
                    final VersionInfo vi = VersionInfo.fromJson(s);
                    if (vi == null || !vi.isReleaseOrSnapshot()) {
                        Log.w("- unsupported type, skipping");
                        continue;
                    }
                } catch (final com.google.gson.JsonSyntaxException e) {
                    Log.w("- JSON SYNTAX ERROR, skipping.");
                    continue;
                }

                Log.f3("- valid");
                opts.add(f.getName());
            }
        }

        if (opts.size() == 0) {
            App.die("Your .minecraft/versions folder is empty.\nRun Minecraft and try again.");
        }

        Collections.sort(opts);
        Collections.reverse(opts);

        // build dialog
        final String[] possibilities = opts.toArray(new String[opts.size()]);

        final String defChoice = possibilities[0];

        final String s = (String) JOptionPane.showInputDialog(
                App.getFrame(),
                isInitial ? initial : user,
                "Extracting Minecraft Assets",
                JOptionPane.QUESTION_MESSAGE,
                Icons.DIALOG_QUESTION,
                possibilities,
                defChoice
        );

        return s;
    }
}
