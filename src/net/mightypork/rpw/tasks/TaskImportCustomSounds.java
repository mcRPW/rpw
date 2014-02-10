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

		String title = "Import sound files into sounds/" + dirNode.getPathRelativeToRoot();
		FileChooser fc = new FileChooser(App.getFrame(), FilePath.IMPORT_SOUND, title, "ogg", "Sound files (*.ogg)", true, false, true);

		fc.showDialog("Open");

		if (!fc.approved()) {
			return;
		}

		File[] files = fc.getSelectedFiles();

		if (files == null || files.length == 0) {
			Alerts.error(App.getFrame(), "That's not a valid file.");
			return;
		}

		try {

			File target = dirNode.getPath();

			target.mkdirs();

			for (File f : files) {
				FileUtils.copyFile(f, new File(target, f.getName()));
			}

			if (afterImport != null) afterImport.run();

		} catch (IOException e) {
			Log.e(e);
			Alerts.error(App.getFrame(), "Something went wrong during import.");
		}
	}
}
