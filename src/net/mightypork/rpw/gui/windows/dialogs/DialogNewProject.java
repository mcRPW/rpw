package net.mightypork.rpw.gui.windows.dialogs;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
import net.mightypork.rpw.gui.widgets.HBox;
import net.mightypork.rpw.gui.widgets.VBox;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.tasks.Tasks;
import net.mightypork.rpw.utils.SpringUtilities;
import net.mightypork.rpw.utils.Utils;
import net.mightypork.rpw.utils.files.FileUtils;

import org.jdesktop.swingx.JXLabel;


public class DialogNewProject extends RpwDialog {

	private List<String> projectNames;

	private JTextField nameField;
	private JTextField titleField;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JXLabel respackUrlLabel;
	private JButton respackPickButton;
	private JRadioButton radioBlank;
	private JRadioButton radioResourcePack;

	private File selectedFile = null;
	private boolean usePackFile = false;
	private FileChooser fc;


	private List<JComponent> respackGroup = new ArrayList<JComponent>();
	private List<JComponent> titleFieldGroup = new ArrayList<JComponent>();

	private JCheckBox ckKeepTitle;


	public DialogNewProject() {

		super(App.getFrame(), "New Project");

		projectNames = Projects.getProjectNames();

		createDialog();
	}


	@Override
	protected JComponent buildGui() {

		HBox hb, hb2;
		JXLabel label;
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
		
		vbox.gap();
		
		hb = new HBox();	
			hb.add(radioResourcePack = new JRadioButton("Project from resource pack"));
			radioResourcePack.setForeground(Gui.SUBHEADING_COLOR);
			hb.glue();
		vbox.add(hb);
		
		hb2 = new HBox();
			hb2.gapl();
			hb = new HBox();
				hb.etchbdr();
		
				respackUrlLabel = new JXLabel("Select pack file.");
				respackUrlLabel.setToolTipText("Resource pack file.");
				respackUrlLabel.setForeground(new Color(0x111111));
				respackUrlLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
				respackUrlLabel.setHorizontalAlignment(SwingConstants.LEFT);
				respackUrlLabel.setPreferredSize(new Dimension(200, 25));
		
				respackPickButton = new JButton(Icons.MENU_OPEN);
				respackPickButton.requestFocusInWindow();
		
				hb.add(respackPickButton);
				hb.gap();
				hb.add(respackUrlLabel);
				hb.glue();
			hb2.add(hb);
		vbox.add(hb2);
		
		respackGroup.add(hb2);
		respackGroup.add(hb);
		respackGroup.add(respackUrlLabel);
		respackGroup.add(respackPickButton);

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

		JPanel p = new JPanel(new SpringLayout());

		label = new JXLabel("Name:", SwingConstants.TRAILING);
		p.add(label);
		nameField = Gui.textField("", "Project folder name", "Project folder name - avoid special characters");
		nameField.addKeyListener(TextInputValidator.filenames());
		label.setLabelFor(nameField);
		p.add(nameField);

		label = new JXLabel("Title:", SwingConstants.TRAILING);
		p.add(label);
		titleField = Gui.textField("", "Resource pack title", "Pack title, shown in Minecraft");
		label.setLabelFor(titleField);
		p.add(titleField);

		titleFieldGroup.add(label);
		titleFieldGroup.add(titleField);

		SpringUtilities.makeCompactGrid(p, 2, 2, 0, 0, Gui.GAP, Gui.GAP);

		vbox.add(p);
		//vbox.add(Gui.commentLine("<html><center>Project created from existing pack<br>will keep the original title.</center></html>"));
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
		fc = new FileChooser(this, FilePath.IMPORT_PACK, "Select pack to import as project", "zip", "ZIP archives (*.zip)", true, false, false);
	}


	@Override
	public void onClose() {

		// do nothing
	}


	@Override
	protected void addActions() {

		buttonOK.addActionListener(createListener);
		buttonCancel.addActionListener(closeListener);
		respackPickButton.addActionListener(pickFileListener);

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

			if (usePackFile && (selectedFile == null || !selectedFile.exists())) {
				Alerts.error(self(), "No file selected", "Missing pack file to import!");
				return;
			}

			if (projectNames.contains(name)) {
				Alerts.error(self(), "Name already used", "Project named \"" + name + "\" already exists!");
			} else {

				final String projname = name;

				// pack project starts with name = title, title is replaced during import.
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
							Tasks.taskPopulateProjectFromPack(selectedFile, new Runnable() {

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

		}
	};


	private ActionListener pickFileListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			fc.showOpenDialog();

			if (fc.approved()) {
				File f = fc.getSelectedFile();

				if (f == null) return;

				if (f.exists()) {
					selectedFile = f;

					String path = f.getPath();
					int length = 24;
					path = Utils.cropStringAtStart(path, length);

					respackUrlLabel.setText(path);

					try {
						String[] parts = FileUtils.getFilenameParts(f);

						if (nameField.getText().trim().length() == 0) {
							nameField.setText(parts[0]);
						}
					} catch (Throwable t) {}
				}
			}
		}
	};
}
