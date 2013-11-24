package net.mightypork.rpw.tasks;


import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Config;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.tree.filesystem.DirectoryFsTreeNode;
import net.mightypork.rpw.utils.FileUtils;
import net.mightypork.rpw.utils.Log;
import net.mightypork.rpw.utils.validation.FileSuffixFilter;


public class TaskImportCustomSounds {

	private static DirectoryFsTreeNode dirNode;


	public static void run(DirectoryFsTreeNode node, Runnable afterImport) {

		dirNode = node;

		initFileChooser();

		int opt = fc.showDialog(App.getFrame(), "Import");
		if (opt != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File[] files = fc.getSelectedFiles();

		Config.FILECHOOSER_PATH_IMPORT_FILE = fc.getCurrentDirectory().getPath();
		Config.save();

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

	private static JFileChooser fc = null;


	private static void initFileChooser() {

		if (fc == null) fc = new JFileChooser(); // keep last path

		fc.setCurrentDirectory(new File(Config.FILECHOOSER_PATH_IMPORT_FILE));
		fc.setAcceptAllFileFilterUsed(false);
		fc.setDialogTitle("Import sound files into sounds/" + dirNode.getPathRelativeToRoot());
		fc.setFileFilter(new FileFilter() {

			FileSuffixFilter fsf = new FileSuffixFilter("ogg");


			@Override
			public String getDescription() {

				return "OGG files, *.ogg";
			}


			@Override
			public boolean accept(File f) {

				if (f.isDirectory()) return true;
				return fsf.accept(f);
			}
		});

		fc.setSelectedFile(null);
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(true);
		fc.setFileHidingEnabled(!Config.SHOW_HIDDEN_FILES);
	}
}
