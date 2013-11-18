package net.mightypork.rpw.tasks;


import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Config;
import net.mightypork.rpw.gui.windows.Alerts;
import net.mightypork.rpw.hierarchy.AssetEntry;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.utils.FileUtils;
import net.mightypork.rpw.utils.Log;
import net.mightypork.rpw.utils.validation.FileSuffixFilter;


public class TaskImportReplacement {

	private static AssetEntry replacedEntry;


	public static void run(AssetEntry entry, Runnable afterImport) {

		replacedEntry = entry;

		initFileChooser();

		int opt = fc.showDialog(App.getFrame(), "Import");
		if (opt != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File f = fc.getSelectedFile();

		if (f == null || !f.exists()) {
			Log.w("Problem accessing file:\n" + f);
			Alerts.error(App.getFrame(), "That's not a valid file.");
			return;
		}

		try {

			File target = new File(Projects.getActive().getAssetsBaseDirectory(), replacedEntry.getPath());

			target.getParentFile().mkdirs();

			FileUtils.copyFile(f, target);

			afterImport.run();

		} catch (IOException e) {
			Log.e(e);
			Alerts.error(App.getFrame(), "Somethign went wrong during import.");
		}
	}

	private static JFileChooser fc = null;


	private static void initFileChooser() {

		if (fc == null) fc = new JFileChooser(); // keep last path

		fc.setAcceptAllFileFilterUsed(false);
		fc.setDialogTitle("Replace " + replacedEntry.getLabel() + "." + replacedEntry.getType().getExtension());
		fc.setFileFilter(new FileFilter() {

			FileSuffixFilter fsf = new FileSuffixFilter(replacedEntry.getType().getExtension());


			@Override
			public String getDescription() {

				return replacedEntry.getType() + " *." + replacedEntry.getType().getExtension();
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
	}
}
