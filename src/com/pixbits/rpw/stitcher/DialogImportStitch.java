package com.pixbits.rpw.stitcher;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Set;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Config.FilePath;
import net.mightypork.rpw.gui.Gui;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.helpers.FileChooser;
import net.mightypork.rpw.gui.widgets.FileInput;
import net.mightypork.rpw.gui.widgets.VBox;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.project.Project;


public class DialogImportStitch extends RpwDialog
{
	private JCheckBox[] selection;

	private FileInput filepicker;

	private JButton buttonOK;
	private JButton buttonCancel;


	public DialogImportStitch() {
		super(App.getFrame(), "Import Stitch");

		createDialog();
	}


	@Override
	protected JComponent buildGui()
	{
		selection = new JCheckBox[AssetCategory.values().length + 1];
		for (int i = 0; i < AssetCategory.values().length; ++i)
			selection[i] = new JCheckBox(AssetCategory.values()[i].name);

		selection[selection.length - 1] = new JCheckBox("Select All");
		selection[selection.length - 1].addActionListener(checkboxListener);

		for (JCheckBox cb : selection) {
			cb.setSelected(true);
			cb.addActionListener(checkboxListener);
		}

		final VBox vbox = new VBox();
		vbox.windowPadding();

		vbox.heading("Import Stitched PNGs");

		vbox.titsep("Resources to import");
		vbox.gap_small();

		for (JCheckBox cb : selection)
			vbox.add(cb);

		vbox.gapl();

		vbox.titsep("Folder to import from");
		vbox.gap();

		//@formatter:off
    filepicker = new FileInput(
        this,
        "Select folder to import to...",
        FilePath.EXPORT,
        "Import stitched pack",
        FileChooser.FOLDERS,
        true        
    );
    //@formatter:on

		vbox.add(filepicker);

		vbox.gapl();

		vbox.titsep("Import");
		vbox.gap();

		buttonOK = new JButton("Import", Icons.MENU_IMPORT_BOX);
		buttonCancel = new JButton("Cancel", Icons.MENU_CANCEL);
		vbox.buttonRow(Gui.RIGHT, buttonOK, buttonCancel);

		return vbox;
	}


	@Override
	protected void addActions()
	{
		setEnterButton(buttonOK);

		buttonCancel.addActionListener(closeListener);

		buttonOK.addActionListener(exportListener);
	}


	@Override
	protected void onShown()
	{

	}

	private final ActionListener checkboxListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent evt)
		{
			JCheckBox src = (JCheckBox) evt.getSource();

			if (src == selection[selection.length - 1]) {
				for (int i = 0; i < selection.length - 1; ++i)
					selection[i].setSelected(src.isSelected());
			} else {
				boolean allSelected = true;

				for (int i = 0; i < selection.length - 1; ++i)
					allSelected &= selection[i].isSelected();

				selection[selection.length - 1].setSelected(allSelected);
			}
		}
	};

	private final ActionListener exportListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent evt)
		{
			if (!filepicker.hasFile()) {
				Alerts.error(self(), "Missing folder",
						"The selected folder does not exist.");
				return;
			}

			Set<AssetCategory> categories = new HashSet<AssetCategory>();

			for (int i = 0; i < AssetCategory.values().length; ++i)
				if (selection[i].isSelected())
					categories.add(AssetCategory.values()[i]);

			if (categories.isEmpty()) {
				Alerts.error(self(), "Category Required",
						"At least one category is required");
				return;
			}

			final File file = filepicker.getFile();
			final Project project = Projects.getActive();

			Tasks.importPackFromStitchedPng(file, project, categories);

			closeDialog();
		}
	};

}
