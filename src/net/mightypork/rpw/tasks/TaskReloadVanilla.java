package net.mightypork.rpw.tasks;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;

import net.mightypork.rpw.App;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.tasks.sequences.SequenceReloadVanilla;
import net.mightypork.rpw.utils.FileUtils;
import net.mightypork.rpw.utils.OsUtils;


public class TaskReloadVanilla {

	public static void run(String version) {

		(new SequenceReloadVanilla(version)).run();

	}


	/**
	 * Ask user for the level to use
	 * 
	 * @param isInitial is this the first startup?
	 * @return MC version selected
	 */
	public static String getUserChoice(boolean isInitial) {

		//@formatter:off
		String initial = 
				"To start a ResourcePack Workbench, the\n" +
				"default pack must be extracted from\n" +
				"your Minecraft folder.\n" +
				"\n" +
				"Please, select a Minecraft version to use:";

		String user = 
				"Minecraft assets will be re-extracted.\n" +
				"\n" +
				"If you have any mods installed, you'll be\n" + 
				"offered to extract their assets too.\n" +
				"\n" +
				"Please, select a Minecraft version to use:";
		//@formatter:on

		// obtain applicable versions
		List<File> list = FileUtils.listDirectory(OsUtils.getMcDir("versions"));

		List<String> opts = new ArrayList<String>();

		for (File f : list) {
			if (f.exists() && f.isDirectory()) {
				File jar = new File(f, f.getName() + ".jar");

				if (jar.exists() && jar.isFile()) opts.add(f.getName());
			}
		}

		if (opts.size() == 0) {
			//@formatter:off
			App.die(
				"Your .minecraft/versions folder is empty.\n" +
				"Run Minecraft and try again."
			);
			//@formatter:on
		}

		Collections.sort(opts);
		Collections.reverse(opts);


		// build dialog
		final String[] possibilities = opts.toArray(new String[opts.size()]);

		String defChoice = possibilities[0];

		//@formatter:off
		String s = (String) JOptionPane.showInputDialog(
				App.getFrame(),
				isInitial?initial:user,
				"Extracting Vanilla ResourcePack",
				JOptionPane.QUESTION_MESSAGE,
				Icons.DIALOG_QUESTION,
				possibilities,
				defChoice
			);
		//@formatter:on

		return s;
	}
}
