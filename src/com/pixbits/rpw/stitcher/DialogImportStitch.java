package com.pixbits.rpw.stitcher;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;

import javax.swing.*;

import com.google.gson.GsonBuilder;
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
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.logging.Log;


public class DialogImportStitch extends RpwDialog {
	private ArrayList<JCheckBox> selection;
	private JScrollPane scrollPane;
	private JPanel checkboxPanel;
	private JCheckBox selectAll, selectAllBlocks, selectAllItems, selectAllEntities, selectAllGuis, selectAllFonts;

	private FileInput filepicker;

	private JButton buttonOK;
	private JButton buttonCancel;


	public DialogImportStitch() {
		super(App.getFrame(), "Import Stitch");

		createDialog();
	}


	@Override
	protected JComponent buildGui() {
		checkboxPanel = new JPanel();
		checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
		scrollPane = new JScrollPane(checkboxPanel);
		scrollPane.setPreferredSize(new Dimension(200, 300));

		VanillaPack vanilla = Sources.vanilla;
		Collection<AssetEntry> totalEntries = vanilla.getAssetEntries();

		selection = new ArrayList<JCheckBox>();

		final VBox vbox = new VBox();
		vbox.windowPadding();

		vbox.heading("Import Stitched PNGs");

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

		vbox.titsep("Resources to import");
		vbox.gap_small();

		vbox.add(scrollPane);

		vbox.gap();

		selectAll = Gui.checkbox(true, "Select All", false);
		selectAll.addActionListener(checkboxListener);
		vbox.add(selectAll);

		selectAllBlocks = Gui.checkbox(true, "Select All Blocks", false);
		selectAllBlocks.addActionListener(checkboxListener);
		vbox.add(selectAllBlocks);

		selectAllItems = Gui.checkbox(true, "Select All Items", false);
		selectAllItems.addActionListener(checkboxListener);
		vbox.add(selectAllItems);

		selectAllEntities = Gui.checkbox(true, "Select All Entities", false);
		selectAllEntities.addActionListener(checkboxListener);
		vbox.add(selectAllEntities);

		selectAllGuis = Gui.checkbox(true, "Select All Guis", false);
		selectAllGuis.addActionListener(checkboxListener);
		vbox.add(selectAllGuis);

		selectAllFonts = Gui.checkbox(true, "Select All Fonts", false);
		selectAllFonts.addActionListener(checkboxListener);
		vbox.add(selectAllFonts);

		vbox.gapl();

		vbox.gapl();

		vbox.titsep("Import");
		vbox.gap();

		buttonOK = new JButton("Import", Icons.MENU_IMPORT_BOX);
		buttonCancel = new JButton("Cancel", Icons.MENU_CANCEL);
		vbox.buttonRow(Gui.RIGHT, buttonOK, buttonCancel);

		return vbox;
	}


	@Override
	protected void addActions() {
		setEnterButton(buttonOK);

		filepicker.setListener(filepickerListener);

		buttonCancel.addActionListener(closeListener);

		buttonOK.addActionListener(exportListener);
	}


	@Override
	protected void onShown() {

	}

	private final ActionListener checkboxListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent evt) {
			JCheckBox checkBox = (JCheckBox) evt.getSource();

			if (checkBox == selectAll) {
				for (int i = 0; i < selection.size(); ++i) {
					selection.get(i).setSelected(checkBox.isSelected());
				}

				selectAll.setSelected(checkBox.isSelected());
				selectAllBlocks.setSelected(checkBox.isSelected());
				selectAllItems.setSelected(checkBox.isSelected());
				selectAllEntities.setSelected(checkBox.isSelected());
				selectAllGuis.setSelected(checkBox.isSelected());
				selectAllFonts.setSelected(checkBox.isSelected());
			} else if (checkBox == selectAllBlocks) {
				for (int i = 0; i < selection.size(); ++i) {
					if (selection.get(i).getText().startsWith("blocks/")) {
						selection.get(i).setSelected(checkBox.isSelected());
					}
				}

				selectAllBlocks.setSelected(checkBox.isSelected());
			} else if (checkBox == selectAllItems) {
				for (int i = 0; i < selection.size(); ++i) {
					if (selection.get(i).getText().startsWith("items/")) {
						selection.get(i).setSelected(checkBox.isSelected());
					}
				}

				selectAllItems.setSelected(checkBox.isSelected());
			} else if (checkBox == selectAllEntities) {
				for (int i = 0; i < selection.size(); ++i) {
					if (selection.get(i).getText().startsWith("entity/")) {
						selection.get(i).setSelected(checkBox.isSelected());
					}
				}

				selectAllEntities.setSelected(checkBox.isSelected());
			} else if (checkBox == selectAllGuis) {
				for (int i = 0; i < selection.size(); ++i) {
					if (selection.get(i).getText().startsWith("gui/")) {
						selection.get(i).setSelected(checkBox.isSelected());
					}
				}

				selectAllGuis.setSelected(checkBox.isSelected());
			} else if (checkBox == selectAllFonts) {
				for (int i = 0; i < selection.size(); ++i) {
					if (selection.get(i).getText().startsWith("font/")) {
						selection.get(i).setSelected(checkBox.isSelected());
					}
				}

				selectAllFonts.setSelected(checkBox.isSelected());
			} else {
				boolean allSelected = true;

				for (int i = 0; i < selection.size(); ++i) {
					allSelected &= selection.get(i).isSelected();
				}

				selectAll.setSelected(allSelected);
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
			Set<AssetEntry> entries = new HashSet<AssetEntry>();

			for (int i = 0; i < selection.size(); ++i) {
				if (selection.get(i).isSelected()) entries.add(totalEntries.get(i));
			}

			if (entries.isEmpty()) {
				Alerts.error(self(), "Texture Required", "At least one texture is required");
				return;
			}

			final File file = filepicker.getFile();
			final Project project = Projects.getActive();

			Tasks.importPackFromStitchedPng(file, project, entries);

			closeDialog();
		}
	};

	private final FileInput.FilePickListener filepickerListener = new FileInput.FilePickListener() {

		@Override
		public void onFileSelected(File file) {
			try {
				File jsonInput = new File(filepicker.getFile().getAbsolutePath() + File.separator + "textures.json");
				StitchJson.Category json = new GsonBuilder().create().fromJson(FileUtils.fileToString(jsonInput), StitchJson.Category.class);

				for (StitchJson.Element element : json.elements) {
					JCheckBox checkBox = new JCheckBox(element.key.substring(26).replace('.', '/'));
					selection.add(checkBox);
					checkBox.setSelected(true);
					checkBox.addActionListener(checkboxListener);
					checkboxPanel.add(checkBox, BorderLayout.NORTH);
				}

				boolean blocks = false;
				boolean items = false;
				boolean entities = false;
				boolean guis = false;
				boolean fonts = false;

				selectAll.setEnabled(true);
				for (int i = 0; i < selection.size(); i++){
					selection.get(i).updateUI();

					if  (selection.get(i).getText().startsWith("blocks/")){
						blocks = true;
					}

					if  (selection.get(i).getText().startsWith("items/")){
						items = true;
					}

					if  (selection.get(i).getText().startsWith("entity/")){
						entities = true;
					}

					if  (selection.get(i).getText().startsWith("gui/")){
						guis = true;
					}

					if  (selection.get(i).getText().startsWith("font/")){
						fonts = true;
					}

					if (blocks == true){
						selectAllBlocks.setEnabled(true);
					}

					if (items == true){
						selectAllItems.setEnabled(true);
					}

					if (entities == true){
						selectAllEntities.setEnabled(true);
					}

					if (guis == true){
						selectAllGuis.setEnabled(true);
					}

					if (fonts == true){
						selectAllFonts.setEnabled(true);
					}
				}

			} catch(Exception e){
				Log.e(e);
			}
		}
	};
}