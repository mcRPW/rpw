package net.mightypork.rpw.tasks;

import java.io.File;
import java.io.IOException;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Config.FilePath;
import net.mightypork.rpw.gui.helpers.FileChooser;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.tree.filesystem.DirectoryFsTreeNode;
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.logging.Log;


public class TaskImportCustomSounds {

    public static void run(DirectoryFsTreeNode dirNode, Runnable afterImport) {
        final String title = "Import sound files into sounds/" + dirNode.getPathRelativeToRoot();
        final FileChooser fc = new FileChooser(App.getFrame(), FilePath.IMPORT_SOUND, title, FileChooser.OGG, true, false, true);

        fc.showDialog("Open");

        if (!fc.approved()) {
            return;
        }

        final File[] files = fc.getSelectedFiles();

        if (files == null || files.length == 0) {
            Alerts.error(App.getFrame(), "That's not a valid file.");
            return;
        }

        try {
            final File target = dirNode.getPath();

            target.mkdirs();

            for (final File f : files) {
                FileUtils.copyFile(f, new File(target, f.getName()));
            }

            if (afterImport != null) afterImport.run();

        } catch (final IOException e) {
            Log.e(e);
            Alerts.error(App.getFrame(), "Something went wrong during import.");
        }
    }
}
