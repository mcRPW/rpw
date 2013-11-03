package net.mightypork.rpack.gui.windows;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.mightypork.rpack.App;
import net.mightypork.rpack.Paths;
import net.mightypork.rpack.gui.Icons;
import net.mightypork.rpack.gui.widgets.FileNameList;
import net.mightypork.rpack.library.Sources;
import net.mightypork.rpack.tasks.Tasks;
import net.mightypork.rpack.utils.FileUtils;
import net.mightypork.rpack.utils.OsUtils;
import net.mightypork.rpack.utils.Utils;

import org.jdesktop.swingx.JXTitledSeparator;


public class DialogManageLibrary extends RpwDialog {

	private List<String> options;

	private FileNameList list;

	private JButton buttonClose;
	private JButton buttonDelete;
	private JButton buttonRename;
	private JButton buttonImport;


	private void reloadOptions() {

		list.setOptions(options = Sources.getResourcepackNames());
	}


	public DialogManageLibrary() {

		super(App.getFrame(), "Manage Library");

		Box hb;
		Box vb = Box.createVerticalBox();
		vb.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		vb.add(new JXTitledSeparator("Library Resource Packs"));

		options = Sources.getResourcepackNames();

		vb.add(list = new FileNameList(options, true));
		list.setMultiSelect(true);

		list.list.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				int[] selected = list.getSelectedIndices();

				buttonDelete.setEnabled(selected != null);
				buttonRename.setEnabled(selected != null && selected.length == 1);
			}
		});

		vb.add(Box.createVerticalStrut(10));

		//@formatter:off		
		hb = Box.createHorizontalBox();

			hb.add(buttonDelete = new JButton(Icons.MENU_DELETE));
			buttonDelete.setEnabled(false);
			
			hb.add(Box.createHorizontalStrut(5));
			
			hb.add(buttonRename = new JButton(Icons.MENU_RENAME));
			buttonRename.setEnabled(false);
			
			hb.add(Box.createHorizontalStrut(5));
			
			hb.add(buttonImport = new JButton(Icons.MENU_IMPORT_BOX));
			
			hb.add(Box.createHorizontalGlue());
			
			hb.add(buttonClose = new JButton(Icons.MENU_EXIT));
		vb.add(hb);
		//@formatter:on

		getContentPane().add(vb);

		prepareForDisplay();
	}


	@Override
	public void onClose() {

		Tasks.taskReloadSources(null);
	}


	@Override
	protected void addActions() {


		buttonClose.addActionListener(closeListener);
		buttonDelete.addActionListener(deleteListener);
		buttonRename.addActionListener(renameListener);
		buttonImport.addActionListener(importListener);
	}

	private ActionListener renameListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			String oldName = list.getSelectedValue(); // just 1

			if (oldName == null) {
				return;
			}

			// OK name

			//@formatter:off
			String newName = Alerts.askForInput(
					DialogManageLibrary.this,
					"Rename Library Source",
					"Please, enter a new name for the\n" +
					"resource pack \"" + oldName + "\".\n" +
					"\n" +
					"Do not use /, *, ?, =.",
					oldName
			);
			//@formatter:on

			if (newName == null) return;
			newName.trim();
			if (!Utils.isValidFilenameString(newName)) {
				Alerts.error(DialogManageLibrary.this, "\"" + newName + "\" is not a valid name.");
				return;
			}

			if (oldName.equals(newName)) return;

			if (options.contains(newName)) {
				Alerts.error(DialogManageLibrary.this, "Name \"" + newName + "\" is already used.");
				return;
			}

			File oldDir = new File(OsUtils.getAppDir(Paths.DIR_RESOURCEPACKS), oldName);
			File newDir = new File(OsUtils.getAppDir(Paths.DIR_RESOURCEPACKS), newName);

			if (!oldDir.renameTo(newDir)) {
				Alerts.error(DialogManageLibrary.this, "Failed to move the pack.");
				FileUtils.delete(newDir, true); // cleanup
			}

			Tasks.taskTreeSourceRename(oldName, newName);

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

			String trailS = (choice.size() > 1 ? "s" : "");

			//@formatter:off
			boolean yes = Alerts.askYesNo(
					DialogManageLibrary.this,
					"Deleting Library Source"+trailS,
					"Do you really want to delete the\n" +
					"selected resource pack" + trailS + "?"
			);
			//@formatter:on

			if (!yes) return;

			for (String s : choice) {
				Tasks.taskDeleteResourcepack(s);
			}

			reloadOptions();
		}
	};

	private ActionListener importListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			RpwDialog importDialog = new DialogImportPack();
			importDialog.addCloseHook(new Runnable() {

				@Override
				public void run() {

					reloadOptions();
				}
			});

			importDialog.setVisible(true);

		}
	};
}
