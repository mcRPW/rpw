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
	
	public static void showDialog()
	{
		if (!Projects.isOpen()) return;
		
		final Project project = Projects.getActive();
		
		final FileChooser fc = new FileChooser(App.getFrame(), FilePath.EXPORT, "Export project", FileChooser.ZIP, true, false, false);
		
		final File dir = fc.getCurrentDirectory();
		final File file = new File(dir, project.getName() + ".zip");
		fc.setSelectedFile(file);
		
		fc.showDialog("Export");
		if (!fc.approved()) {
			return;
		}
		
		final File f = fc.getSelectedFile();
		
		if (f.exists()) {
			//@formatter:off
			final int overwrite = Alerts.askYesNoCancel(
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
