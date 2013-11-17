package net.mightypork.rpack.gui.windows;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.mightypork.rpack.App;
import net.mightypork.rpack.Paths;
import net.mightypork.rpack.gui.Icons;
import net.mightypork.rpack.gui.widgets.SimpleStringList;
import net.mightypork.rpack.project.Projects;
import net.mightypork.rpack.tasks.Tasks;
import net.mightypork.rpack.utils.FileUtils;
import net.mightypork.rpack.utils.OsUtils;
import net.mightypork.rpack.utils.Utils;

import org.jdesktop.swingx.JXTitledSeparator;


public class DialogManageProjects extends RpwDialog {

	private List<String> options;

	private SimpleStringList list;

	private JButton buttonClose;
	private JButton buttonDelete;
	private JButton buttonRename;
	private JButton buttonOpen;


	private void reloadOptions() {

		list.setOptions(options = Projects.getProjectNames());
	}


	public DialogManageProjects() {

		super(App.getFrame(), "Manage Projects");

		Box hb;
		Box vb = Box.createVerticalBox();
		vb.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		vb.add(new JXTitledSeparator("Your Projects"));

		options = Projects.getProjectNames();

		vb.add(list = new SimpleStringList(options, true));
		list.setMultiSelect(true);

		list.list.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				int[] selected = list.getSelectedIndices();

				buttonDelete.setEnabled(selected != null);
				buttonRename.setEnabled(selected != null && selected.length == 1);
				buttonOpen.setEnabled(selected != null && selected.length == 1);
			}
		});

		vb.add(Box.createVerticalStrut(10));

		//@formatter:off		
		hb = Box.createHorizontalBox();

			buttonDelete = new JButton(Icons.MENU_DELETE);
			buttonDelete.setEnabled(false);
			buttonDelete.setToolTipText("Delete");
			hb.add(buttonDelete);
			
			hb.add(Box.createHorizontalStrut(5));
			
			buttonRename = new JButton(Icons.MENU_RENAME);
			buttonRename.setEnabled(false);
			buttonRename.setToolTipText("Rename");
			hb.add(buttonRename);

			hb.add(Box.createHorizontalStrut(15));
			
			buttonOpen = new JButton(Icons.MENU_OPEN);
			buttonOpen.setEnabled(false);
			buttonOpen.setToolTipText("Open");
			hb.add(buttonOpen);
			
			hb.add(Box.createHorizontalStrut(5));
			
			hb.add(Box.createHorizontalGlue());
			
			buttonClose = new JButton(Icons.MENU_EXIT);
			buttonClose.setToolTipText("Close");
			hb.add(buttonClose);
		vb.add(hb);
		//@formatter:on

		getContentPane().add(vb);

		prepareForDisplay();
	}


	@Override
	public void onClose() {

		Tasks.taskRedrawRecentProjectsMenu();
	}


	@Override
	protected void addActions() {

		buttonRename.addActionListener(renameListener);
		buttonDelete.addActionListener(deleteListener);
		buttonClose.addActionListener(closeListener);
		buttonOpen.addActionListener(openListener);
	}

	private ActionListener renameListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			String projname = list.getSelectedValue();

			if (projname == null) {
				return;
			}

			if (Projects.getActive() != null && Projects.getActive().getDirName().equals(projname)) {
				Alerts.error(self(), "Illegal Operation", "You can't rename an open project.\n\nUse menu Project→Close to close it.");
				return;
			}

			// OK name

			//@formatter:off
			String newName = Alerts.askForInput(
					self(),
					"Rename Project",
					"Please, enter a new name for\n" +
					"the project \"" + projname + "\".\n" +
					"\n" +
					"Do not use /, *, ?, =.",
					projname
			);
			//@formatter:on

			if (newName == null) return;
			newName.trim();
			if (!Utils.isValidFilenameString(newName)) {
				Alerts.error(self(), "\"" + newName + "\" is not a valid name.");
				return;
			}

			if (projname.equals(newName)) return;

			if (options.contains(newName)) {
				Alerts.error(self(), "Name \"" + newName + "\" is already used.");
				return;
			}

			File oldDir = new File(OsUtils.getAppDir(Paths.DIR_PROJECTS), projname);
			File newDir = new File(OsUtils.getAppDir(Paths.DIR_PROJECTS), newName);

			if (!oldDir.renameTo(newDir)) {
				Alerts.error(self(), "Failed to move the project directory.");
				FileUtils.delete(newDir, true); // cleanup
			}

			reloadOptions();
		}
	};

	private ActionListener deleteListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			List<String> choice = list.getSelectedValues();

			if (choice == null) {
				return;
			}

			// OK name

			//@formatter:off
			boolean yes = Alerts.askYesNo(
					self(),
					"Deleting Project",
					"Do you really want to delete\nthe selected project" + (choice.size()>1?"s":"") + "?"
			);
			//@formatter:on

			if (!yes) return;

			if (Projects.getActive() != null) {
				String openProjectDirname = Projects.getActive().getDirName();

				boolean isOpen = false;
				for (String s : choice) {
					if (s.equals(openProjectDirname)) isOpen = true;
				}

				if (isOpen) {
					Alerts.error(self(), "Illegal Operation", "You can't delete an open project.\n\nUse menu Project→Close to close it.");
					return;
				}
			}

			List<Integer> tasks = new ArrayList<Integer>();
			for (String s : choice) {
				tasks.add(Tasks.taskDeleteProject(s));
			}

			while (true) {
				int live = 0;
				for (int task : tasks) {
					if (Tasks.isRunning(task)) live++;
				}
				if (live == 0) break;
			}

			reloadOptions();
		}
	};

	private ActionListener openListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			final String choice = list.getSelectedValue();

			if (choice == null) {
				return;
			}

			// OK name
			Tasks.taskAskToSaveIfChanged(new Runnable() {

				@Override
				public void run() {

					Projects.openProject(choice);
					Tasks.taskOnProjectChanged();
					closeDialog();
				}
			});
		}
	};
}
