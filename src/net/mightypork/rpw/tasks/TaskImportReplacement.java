package net.mightypork.rpw.tasks;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Config.FilePath;
import net.mightypork.rpw.gui.helpers.FileChooser;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.tree.assets.AssetEntry;
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.logging.Log;


public class TaskImportReplacement {

    public static void run(final AssetEntry entry, final Runnable afterImport) {
        final String title = "Replace " + entry.getLabel() + "." + entry.getType().getExtension();

        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                final FileChooser fc = new FileChooser(App.getFrame(), FilePath.IMPORT_FILE, title, entry.getType().getFilter(), true, false, false);

                fc.showDialog("Import");
                if (!fc.approved()) {
                    return;
                }

                final File f = fc.getSelectedFile();

                if (f == null || !f.exists()) {
                    Log.w("Problem accessing file:\n" + f);
                    Alerts.error(App.getFrame(), "That's not a valid file.");
                    return;
                }

                try {
                    final File target = new File(Projects.getActive().getAssetsDirectory(), entry.getPath());

                    target.getParentFile().mkdirs();

                    FileUtils.copyFile(f, target);

                    afterImport.run();

                } catch (final IOException e) {
                    Log.e(e);
                    Alerts.error(App.getFrame(), "Something went wrong during import.");
                }
            }
        });

    }
}
