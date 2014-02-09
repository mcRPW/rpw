package net.mightypork.rpw.gui.windows.dialogs;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.mightypork.rpw.App;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.widgets.SimpleStringList;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.utils.FileUtils;
import net.mightypork.rpw.utils.OsUtils;

import org.jdesktop.swingx.JXTitledSeparator;


public class DialogManageMcPacks extends RpwDialog {

	private List<String> options;

	private SimpleStringList list;

	private JButton buttonClose;
	private JButton buttonDelete;


	private List<String> getOptions() {

		List<File> aList = FileUtils.listDirectory(OsUtils.getMcDir("resourcepacks"));
		List<String> options = new ArrayList<String>();

		for (File f : aList) {
			if (f.isDirectory()) continue;
			String[] parts = FileUtils.getFilenameParts(f);

			if (parts[1].equalsIgnoreCase("zip")) {
				options.add(parts[0]);
			}
		}

		Collections.sort(options);

		return options;
	}


	private void reloadOptions() {

		list.setItems(options = getOptions());
	}


	public DialogManageMcPacks() {

		super(App.getFrame(), "Manage packs in MC");

		createDialog();
	}


	@Override
	protected JComponent buildGui() {

		Box hb;
		Box vb = Box.createVerticalBox();
		vb.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		vb.add(new JXTitledSeparator("Installed Packs"));

		options = getOptions();

		vb.add(list = new SimpleStringList(options, true));
		list.setMultiSelect(true);

		list.getList().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				int[] selected = list.getSelectedIndices();

				buttonDelete.setEnabled(selected != null);
			}
		});

		vb.add(Box.createVerticalStrut(10));

		//@formatter:off		
		hb = Box.createHorizontalBox();

			hb.add(buttonDelete = new JButton(Icons.MENU_DELETE));
			buttonDelete.setToolTipText("Delete");
			buttonDelete.setEnabled(false);
			
			hb.add(Box.createHorizontalGlue());
			
			hb.add(buttonClose = new JButton(Icons.MENU_EXIT));
			buttonClose.setToolTipText("Close");
		vb.add(hb);
		//@formatter:on

		return vb;

	}


	@Override
	protected void addActions() {

		buttonClose.addActionListener(closeListener);
		buttonDelete.addActionListener(deleteListener);
	}


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
					DialogManageMcPacks.this,
					"Deleting Installed Pack" + trailS,
					"Do you really want to delete the selected\n" +
					"resource pack" + trailS + " from your Minecraft folder?"
			);
			//@formatter:on

			if (!yes) return;

			for (String s : choice) {
				File f = new File(OsUtils.getMcDir("resourcepacks"), s + ".zip");
				f.delete();
			}

			reloadOptions();
		}
	};
}
