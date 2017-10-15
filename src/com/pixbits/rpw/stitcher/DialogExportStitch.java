package com.pixbits.rpw.stitcher;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;

import javax.swing.*;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Config.FilePath;
import net.mightypork.rpw.gui.Gui;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.helpers.FileChooser;
import net.mightypork.rpw.gui.widgets.FileInput;
import net.mightypork.rpw.gui.widgets.VBox;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.library.Sources;
import net.mightypork.rpw.library.VanillaPack;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.project.Project;
import net.mightypork.rpw.tree.assets.AssetEntry;


public class DialogExportStitch extends RpwDialog
{
	private ArrayList<JCheckBox> selection;
	private JScrollPane scrollPane;
	private JPanel checkboxPanel;
	private JCheckBox selectAll, selectAllBlocks, selectAllItems, selectAllEntities, selectAllGuis, selectAllFonts;

	private FileInput filepicker;

	private JButton buttonOK;
	private JButton buttonCancel;

	private JComboBox textureSource;
	private JComboBox scale;


	public DialogExportStitch() {
		super(App.getFrame(), "Export Stitch");

		createDialog();
	}


	@Override
	protected JComponent buildGui()
	{
		scale = new JComboBox(Scale.values());
		scale.setSelectedItem(Scale.ONE);
		textureSource = new JComboBox(new String[]{"Vanilla", "Project"});
		textureSource.setSelectedIndex(1);
		checkboxPanel = new JPanel();
		checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
		scrollPane = new JScrollPane(checkboxPanel);
		scrollPane.setPreferredSize(new Dimension(200, 300));

		VanillaPack vanilla = Sources.vanilla;
		Collection<AssetEntry> totalEntries = vanilla.getAssetEntries();

		selection = new ArrayList<JCheckBox>();

		final VBox vbox = new VBox();
		vbox.windowPadding();

		vbox.heading("Export Stitched PNGs");

		vbox.titsep("Resources to export");
		vbox.gap_small();

		vbox.add(scrollPane);

		for (AssetEntry e : totalEntries) {
			if (e.getPath().startsWith("assets/minecraft/textures")) {
				JCheckBox checkBox = new JCheckBox(e.getPath().substring(26));
				selection.add(checkBox);
				checkBox.setSelected(true);
				checkBox.addActionListener(checkboxListener);
				checkboxPanel.add(checkBox, BorderLayout.NORTH);
			}
		}

		vbox.gap();

		selectAll = Gui.checkbox(true, "Select All");
		selectAll.addActionListener(checkboxListener);
		vbox.add(selectAll);

		selectAllBlocks = Gui.checkbox(true, "Select All Blocks");
		selectAllBlocks.addActionListener(checkboxListener);
		vbox.add(selectAllBlocks);

		selectAllItems = Gui.checkbox(true, "Select All Items");
		selectAllItems.addActionListener(checkboxListener);
		vbox.add(selectAllItems);

		selectAllEntities = Gui.checkbox(true, "Select All Entities");
		selectAllEntities.addActionListener(checkboxListener);
		vbox.add(selectAllEntities);

		selectAllGuis = Gui.checkbox(true, "Select All Guis");
		selectAllGuis.addActionListener(checkboxListener);
		vbox.add(selectAllGuis);

		selectAllFonts = Gui.checkbox(true, "Select All Fonts");
		selectAllFonts.addActionListener(checkboxListener);
		vbox.add(selectAllFonts);

		vbox.gapl();

		vbox.titsep("Texture source");
		vbox.add(textureSource);

		vbox.gap();

		vbox.titsep("Scale");
		vbox.add(scale);

		vbox.gap();

		vbox.titsep("Folder to export to");
		vbox.gap();

		//@formatter:off
		filepicker = new FileInput(
				this,
				"Select folder to export to...",
				FilePath.EXPORT,
				"Export stitched pack",
				FileChooser.FOLDERS,
				true
		);
		//@formatter:on

		vbox.add(filepicker);

		vbox.gapl();

		vbox.titsep("Export");
		vbox.gap();

		buttonOK = new JButton("Export", Icons.MENU_IMPORT_BOX);
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
			JCheckBox checkBox = (JCheckBox) evt.getSource();

			if (checkBox == selectAll) {
				for (int i = 0; i < selection.size(); ++i) {
					selection.get(i).setSelected(checkBox.isSelected());
				}
				selectAllBlocks.setSelected(checkBox.isSelected());
				selectAllItems.setSelected(checkBox.isSelected());
				selectAllEntities.setSelected(checkBox.isSelected());
				selectAllGuis.setSelected(checkBox.isSelected());
				selectAllFonts.setSelected(checkBox.isSelected());
			} else if (checkBox == selectAllBlocks) {
				for (int i = 0; i < selection.size(); ++i) {
					if(selection.get(i).getText().startsWith("blocks/")) {
						selection.get(i).setSelected(checkBox.isSelected());
					}
				}

				selectAllBlocks.setSelected(checkBox.isSelected());
			} else if (checkBox == selectAllItems) {
				for (int i = 0; i < selection.size(); ++i) {
					if(selection.get(i).getText().startsWith("items/")) {
						selection.get(i).setSelected(checkBox.isSelected());
					}
				}

				selectAllItems.setSelected(checkBox.isSelected());
			} else if (checkBox == selectAllEntities) {
				for (int i = 0; i < selection.size(); ++i) {
					if(selection.get(i).getText().startsWith("entity/")) {
						selection.get(i).setSelected(checkBox.isSelected());
					}
				}

				selectAllEntities.setSelected(checkBox.isSelected());
			} else if (checkBox == selectAllGuis) {
				for (int i = 0; i < selection.size(); ++i) {
					if(selection.get(i).getText().startsWith("gui/")) {
						selection.get(i).setSelected(checkBox.isSelected());
					}
				}

				selectAllGuis.setSelected(checkBox.isSelected());
			} else if (checkBox == selectAllFonts) {
				for (int i = 0; i < selection.size(); ++i) {
					if(selection.get(i).getText().startsWith("font/")) {
						selection.get(i).setSelected(checkBox.isSelected());
					}
				}

				selectAllFonts.setSelected(checkBox.isSelected());
			} else {
				boolean allSelected = true;
				boolean allBlocksSelected = true;
				boolean allItemsSelected = true;
				boolean allEntitiesSelected = true;
				boolean allGuisSelected = true;
				boolean allFontsSelected = true;

				for (int i = 0; i < selection.size(); ++i) {
					allSelected &= selection.get(i).isSelected();

					if(selection.get(i).getText().startsWith("blocks/")){
						allBlocksSelected &= selection.get(i).isSelected();
					}

					if(selection.get(i).getText().startsWith("items/")){
						allItemsSelected &= selection.get(i).isSelected();
					}

					if(selection.get(i).getText().startsWith("entity/")){
						allEntitiesSelected &= selection.get(i).isSelected();
					}

					if(selection.get(i).getText().startsWith("gui/")){
						allGuisSelected &= selection.get(i).isSelected();
					}

					if(selection.get(i).getText().startsWith("font/")){
						allFontsSelected &= selection.get(i).isSelected();
					}
				}

				selectAll.setSelected(allSelected);
				selectAllBlocks.setSelected(allBlocksSelected);
				selectAllItems.setSelected(allItemsSelected);
				selectAllEntities.setSelected(allEntitiesSelected);
				selectAllGuis.setSelected(allGuisSelected);
				selectAllFonts.setSelected(allFontsSelected);
			}
		}
	};

	private final ActionListener exportListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent evt) {
			if (!filepicker.hasFile()) {
				Alerts.error(self(), "Missing folder", "The selected folder does not exist.");
				return;
			}

			VanillaPack vanilla = Sources.vanilla;
			ArrayList<AssetEntry> totalEntries = new ArrayList<AssetEntry>(vanilla.getAssetEntries());
			ArrayList<AssetEntry> entries = new ArrayList<AssetEntry>();

			for (int i = 0; i < selection.size(); ++i){
				if (selection.get(i).isSelected()){
					for (int j = 0; j < totalEntries.size(); j++) {
						if (totalEntries.get(j).getPath().substring(26).matches(selection.get(i).getText())) {
							entries.add(totalEntries.get(j));
						}
					}
				}
			}

			if (entries.isEmpty()) {
				Alerts.error(self(), "Texture Required", "At least one texture is required");
				return;
			}

			final File file = filepicker.getFile();
			final Project project = Projects.getActive();

			Tasks.exportPackToStitchedPng(file, project, entries, (String)textureSource.getSelectedItem(), (Scale) scale.getSelectedItem());

			closeDialog();
		}
	};

}