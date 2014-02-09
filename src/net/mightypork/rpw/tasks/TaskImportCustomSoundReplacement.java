package net.mightypork.rpw.tasks;


import java.io.File;
import java.io.IOException;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Config.FilePath;
import net.mightypork.rpw.gui.helpers.FileChooser;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.tree.filesystem.FileFsTreeNode;
import net.mightypork.rpw.utils.FileUtils;
import net.mightypork.rpw.utils.logging.Log;


public class TaskImportCustomSoundReplacement {

	public static void run(FileFsTreeNode fileNode, Runnable afterImport) {

		String title = "Import sound file into sounds/" + fileNode.getPathRelativeToRoot();
		FileChooser fc = new FileChooser(App.getFrame(), FilePath.IMPORT_SOUND, title, "ogg", "Sound files (*.ogg)", true, false, false);

		fc.showDialog("Open");

		if (!fc.approved()) {
			return;
		}

		fc.showDialog("Import");
		if (fc.approved()) {
			return;
		}

		File file = fc.getSelectedFile();

		if (file == null || !file.exists()) {
			Alerts.error(App.getFrame(), "That's not a valid file.");
			return;
		}


		try {
			File target = fileNode.getPath();
			target.mkdirs();

			FileUtils.copyFile(file, new File(target, file.getName()));

			if (afterImport != null) afterImport.run();

		} catch (IOException e) {
			Log.e(e);
			Alerts.error(App.getFrame(), "Something went wrong during import.");
		}
	}
}
