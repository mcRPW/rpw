package net.mightypork.rpw.gui.windows.dialogs;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Config.FilePath;
import net.mightypork.rpw.gui.Gui;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.helpers.FileChooser;
import net.mightypork.rpw.gui.helpers.TextInputValidator;
import net.mightypork.rpw.gui.widgets.FileInput;
import net.mightypork.rpw.gui.widgets.FileInput.FilePickListener;
import net.mightypork.rpw.gui.widgets.HBox;
import net.mightypork.rpw.gui.widgets.VBox;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.tasks.Tasks;
import net.mightypork.rpw.utils.files.FileUtils;


public class DialogNewProject extends RpwDialog {

	private List<String> projectNames;

	private JTextField nameField;
	private JTextField titleField;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JRadioButton radioBlank;
	private JRadioButton radioResourcePack;

	private boolean usePackFile = false;


	private List<JComponent> respackGroup = new ArrayList<JComponent>();
	private List<JComponent> titleFieldGroup = new ArrayList<JComponent>();

	private JCheckBox ckKeepTitle;

	private FileInput filepicker;


	public DialogNewProject() {

		super(App.getFrame(), "New Project");

		projectNames = Projects.getProjectNames();

		createDialog();
	}


	@Override
	protected JComponent buildGui() {

		HBox hb, hb2;
		VBox vbox = new VBox();
		vbox.windowPadding();

		vbox.heading("Create New Project");

		vbox.titsep("Project type");
		vbox.gap();

		//@formatter:off
		hb = new HBox();	
			hb.add(radioBlank = new JRadioButton("Blank project"));
			radioBlank.setForeground(Gui.SUBHEADING_COLOR);
			radioBlank.setSelected(true);
			hb.glue();
		vbox.add(hb);
		
		vbox.gapl();
		
		hb = new HBox();	
			hb.add(radioResourcePack = new JRadioButton("Project from resource pack"));
			radioResourcePack.setForeground(Gui.SUBHEADING_COLOR);
			hb.glue();
		vbox.add(hb);
		
		vbox.gap();
		hb = new HBox();
			hb.gapl();
			
			hb.add(filepicker = new FileInput(
					this,
					"Select pack file...",
					FilePath.IMPORT_PACK,
					"Select pack to import as project",
					FileChooser.ZIP,
					true
				)
			);
			
		vbox.add(hb);
		
		respackGroup.add(hb);
		respackGroup.add(filepicker);

		hb2 = new HBox();
			hb2.gapl();
			hb2.add(ckKeepTitle = new JCheckBox("Keep original title", true));
			hb2.glue();
		vbox.add(hb2);
		
		respackGroup.add(hb2);
		respackGroup.add(ckKeepTitle);
			
		ButtonGroup group = new ButtonGroup();
		group.add(radioBlank);
		group.add(radioResourcePack);
		
		//@formatter:on

		vbox.gapl();
		vbox.titsep("Project settings");
		vbox.gap();


		nameField = Gui.textField("", "Project folder name", "Project folder name - avoid special characters");
		nameField.addKeyListener(TextInputValidator.filenames());

		titleField = Gui.textField("", "Resource pack title", "Pack title, shown in Minecraft");
		titleFieldGroup.add(titleField);

		vbox.add(Gui.springForm(new String[] { "Name:", "Title:" }, new JComponent[] { nameField, titleField }));

		vbox.gapl();

		hb = new HBox();
		hb.glue();

		buttonOK = new JButton("Create", Icons.MENU_NEW);
		hb.add(buttonOK);

		hb.gap();

		buttonCancel = new JButton("Cancel", Icons.MENU_CANCEL);
		hb.add(buttonCancel);
		hb.glue();
		vbox.add(hb);
		//@formatter:on

		return vbox;
	}


	private void enableFilePicker(boolean enable) {

		for (JComponent j : respackGroup) {
			j.setEnabled(enable);
		}
	}


	private void enableTitleField(boolean enable) {

		for (JComponent j : titleFieldGroup) {
			j.setEnabled(enable);
		}
	}


	@Override
	protected void initGui() {

		enableFilePicker(false);


	}


	@Override
	public void onClose() {

		// do nothing
	}


	@Override
	protected void addActions() {

		buttonOK.addActionListener(createListener);
		buttonCancel.addActionListener(closeListener);

		ckKeepTitle.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {

				enableTitleField(e.getStateChange() == ItemEvent.DESELECTED);
			}
		});

		radioBlank.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {

				if (e.getStateChange() == ItemEvent.SELECTED) {
					enableFilePicker(false);
					enableTitleField(true);
					usePackFile = false;
				}
			}
		});

		radioResourcePack.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {

				if (e.getStateChange() == ItemEvent.SELECTED) {
					enableFilePicker(true);
					enableTitleField(!ckKeepTitle.isSelected());
					usePackFile = true;
				}
			}
		});

		filepicker.setListener(new FilePickListener() {

			@Override
			public void onFileSelected(File f) {

				if (f.exists()) {
					try {
						String[] parts = FileUtils.getFilenameParts(f);

						if (nameField.getText().trim().length() == 0) {
							nameField.setText(parts[0]);
						}
					} catch (Throwable t) {}
				}

			}
		});
	}

	private ActionListener createListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {


			String name = nameField.getText();
			if (name == null) name = "";

			String title = titleField.getText();
			if (title == null) title = "";

			name = name.trim();
			title = title.trim();
			if (name.length() == 0) {
				Alerts.error(self(), "Invalid name", "Missing project name!");
				return;
			}

			if ((!usePackFile || !ckKeepTitle.isSelected()) && title.length() == 0) {
				Alerts.error(self(), "Invalid title", "Missing project title!");
				return;
			}

			if (usePackFile && !filepicker.hasFile()) {
				Alerts.error(self(), "Missing file", "The selected file does not exist.");
				return;
			}

			if (projectNames.contains(name)) {
				Alerts.error(self(), "Name already used", "Project named \"" + name + "\" already exists!");
				return;
			}

			final File file = filepicker.getFile();

			// create the project

			final String projname = name;

			final String projtitle = (usePackFile && ckKeepTitle.isSelected()) ? "" : title;

			Tasks.taskAskToSaveIfChanged(new Runnable() {

				@Override
				public void run() {

					// OK name
					closeDialog();

					Alerts.loading(true);
					Projects.openNewProject(projname);
					Projects.getActive().setTitle(projtitle);

					Tasks.taskStoreProjectChanges();

					Projects.getActive().save();

					Projects.markProjectAsRecent(Projects.getActive().getName());


					if (usePackFile) {
						Tasks.taskPopulateProjectFromPack(file, new Runnable() {

							@Override
							public void run() {

								Projects.getActive().save();

								Tasks.taskOnProjectChanged();
								Alerts.loading(false);
							}
						});
					} else {
						Tasks.taskOnProjectChanged();
						Alerts.loading(false);
					}

				}
			});

		}
	};
}
