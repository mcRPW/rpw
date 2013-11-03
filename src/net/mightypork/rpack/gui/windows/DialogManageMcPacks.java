package net.mightypork.rpack.gui.windows;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.mightypork.rpack.App;
import net.mightypork.rpack.gui.Icons;
import net.mightypork.rpack.gui.widgets.FileNameList;
import net.mightypork.rpack.utils.FileUtils;
import net.mightypork.rpack.utils.OsUtils;

import org.jdesktop.swingx.JXTitledSeparator;


public class DialogManageMcPacks extends RpwDialog {

	private List<String> options;

	private FileNameList list;

	private JButton buttonClose;
	private JButton buttonDelete;


	private List<String> getOptions() {

		List<File> aList = FileUtils.listDirectory(OsUtils.getMcDir("resourcepacks"));
		List<String> options = new ArrayList<String>();

		for (File f : aList) {
			if (f.isDirectory()) continue;
			String[] parts = FileUtils.removeExtension(f);

			if (parts[1].equalsIgnoreCase("zip")) {
				options.add(parts[0]);
			}
		}

		Collections.sort(options);

		return options;
	}


	private void reloadOptions() {

		list.setOptions(options = getOptions());
	}


	public DialogManageMcPacks() {

		super(App.getFrame(), "Manage packs in MC");

		Box hb;
		Box vb = Box.createVerticalBox();
		vb.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		vb.add(new JXTitledSeparator("Installed Packs"));

		options = getOptions();

		vb.add(list = new FileNameList(options, true));
		list.setMultiSelect(true);

		list.list.addListSelectionListener(new ListSelectionListener() {

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
			buttonDelete.setEnabled(false);
			
			hb.add(Box.createHorizontalGlue());
			
			hb.add(buttonClose = new JButton(Icons.MENU_EXIT));
		vb.add(hb);
		//@formatter:on

		getContentPane().add(vb);

		prepareForDisplay();
	}


	@Override
	public void onClose() {

		// nothing
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
