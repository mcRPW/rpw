package net.mightypork.rpw.tasks;


import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Config;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.project.Project;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.utils.validation.FileSuffixFilter;


public class TaskExportProject {

	public static void showDialog() {

		if (Projects.getActive() == null) return;

		initFileChooser();

		int opt = fc.showDialog(App.getFrame(), "Export");
		if (opt != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File f = fc.getSelectedFile();

		Config.FILECHOOSER_PATH_EXPORT = fc.getCurrentDirectory().getPath();
		Config.save();

		if (f.exists()) {
			//@formatter:off
			int overwrite = Alerts.askYesNoCancel(
					App.getFrame(),
					"File Exists",
					"File \"" + f.getName() + "\" already exists.\n" +
					"Do you want to overwrite it?"
			);
			//@formatter:on

			if (overwrite != JOptionPane.YES_OPTION) return;
		}

		Tasks.taskExportProject(f, null);
	}

	private static JFileChooser fc = null;


	private static void initFileChooser() {

		Project project = Projects.getActive();

		if (fc == null) fc = new JFileChooser();

		fc.setCurrentDirectory(new File(Config.FILECHOOSER_PATH_EXPORT));

		fc.setAcceptAllFileFilterUsed(false);
		fc.setDialogTitle("Export project");
		fc.setFileFilter(new FileFilter() {

			FileSuffixFilter fsf = new FileSuffixFilter("zip");


			@Override
			public String getDescription() {

				return "ZIP archives";
			}


			@Override
			public boolean accept(File f) {

				if (f.isDirectory()) return true;
				return fsf.accept(f);
			}
		});

		fc.setSelectedFile(null);
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(false);
		fc.setFileHidingEnabled(!Config.SHOW_HIDDEN_FILES);

		File dir = fc.getCurrentDirectory();
		File file = new File(dir, project.getDirName() + ".zip");
		fc.setSelectedFile(file);
	}
}
