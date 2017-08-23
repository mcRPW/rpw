package net.mightypork.rpw.tasks;

import java.io.File;
import java.io.IOException;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Config.FilePath;
import net.mightypork.rpw.gui.helpers.FileChooser;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.tree.filesystem.FileFsTreeNode;
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.logging.Log;


public class TaskImportCustomSoundReplacement {

    public static void run(FileFsTreeNode fileNode, Runnable afterImport) {
        final String title = "Import sound file into sounds/" + fileNode.getPathRelativeToRoot();
        final FileChooser fc = new FileChooser(App.getFrame(), FilePath.IMPORT_SOUND, title, FileChooser.OGG, true, false, false);

        fc.showDialog("Open");

        if (!fc.approved()) {
            return;
        }

        fc.showDialog("Import");
        if (fc.approved()) {
            return;
        }

        final File file = fc.getSelectedFile();

        if (file == null || !file.exists()) {
            Alerts.error(App.getFrame(), "That's not a valid file.");
            return;
        }

        try {
            final File target = fileNode.getPath();
            target.mkdirs();

            FileUtils.copyFile(file, new File(target, file.getName()));

            if (afterImport != null) afterImport.run();

        } catch (final IOException e) {
            Log.e(e);
            Alerts.error(App.getFrame(), "Something went wrong during import.");
        }
    }
}
