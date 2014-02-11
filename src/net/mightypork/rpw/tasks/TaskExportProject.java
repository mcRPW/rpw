package net.mightypork.rpw.tasks;


import java.io.File;

import javax.swing.JOptionPane;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Config.FilePath;
import net.mightypork.rpw.gui.helpers.FileChooser;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.project.Project;
import net.mightypork.rpw.project.Projects;


public class TaskExportProject {

	public static void showDialog() {

		if (!Projects.isOpen()) return;

		Project project = Projects.getActive();

		FileChooser fc = new FileChooser(App.getFrame(), FilePath.EXPORT, "Export project", "zip", "ZIP archives", true, false, false);

		File dir = fc.getCurrentDirectory();
		File file = new File(dir, project.getName() + ".zip");
		fc.setSelectedFile(file);

		fc.showDialog("Export");
		if (!fc.approved()) {
			return;
		}

		File f = fc.getSelectedFile();

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
}
