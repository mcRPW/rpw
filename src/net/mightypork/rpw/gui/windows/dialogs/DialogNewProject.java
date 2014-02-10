package net.mightypork.rpw.gui.windows.dialogs;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.*;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Config.FilePath;
import net.mightypork.rpw.gui.Gui;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.helpers.FileChooser;
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
import org.jdesktop.swingx.prompt.PromptSupport;


public class DialogNewProject extends RpwDialog {

	private List<String> options;

	private JTextField nameField;
	private JTextField titleField;
	private JButton buttonOK;
	private JButton buttonCancel;

	private JXLabel importUrl;

	private JButton buttonPickFile;

	private boolean projectFromPack;
	private File selectedFile = null;
	private FileChooser fc;


	public DialogNewProject(boolean imported) {

		super(App.getFrame(), "New Project");

		this.projectFromPack = imported;

		createDialog();
	}


	@Override
	protected JComponent buildGui() {

		HBox hb;
		JXLabel label;
		VBox vbox = new VBox();
		vbox.windowPadding();

		vbox.titsep("Pack to import");
		vbox.gap();

		if (projectFromPack) {
			vbox.gap();
			hb = new HBox();
			hb.etchbdr();

			importUrl = new JXLabel();
			importUrl.setToolTipText("Imported pack ZIP file");
			importUrl.setForeground(new Color(0x111111));
			importUrl.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
			importUrl.setHorizontalAlignment(SwingConstants.LEFT);
			importUrl.setPreferredSize(new Dimension(200, 25));

			buttonPickFile = new JButton(Icons.MENU_OPEN);
			buttonPickFile.requestFocusInWindow();

			hb.add(buttonPickFile);
			hb.gap();
			hb.add(importUrl);
			hb.glue();

			vbox.add(hb);
			vbox.gap();
		}

		options = Projects.getProjectNames();

		vbox.gapl();
		vbox.titsep("Project settings");
		vbox.gap();

		JPanel p = new JPanel(new SpringLayout());

		label = new JXLabel("Name:", SwingConstants.TRAILING);
		p.add(label);
		nameField = Gui.textField("", "Project folder name", "Project folder name - avoid special characters");
		label.setLabelFor(nameField);
		p.add(nameField);

		PromptSupport.setPrompt("Project folder name", nameField);

		label = new JXLabel("Title:", SwingConstants.TRAILING);
		p.add(label);
		titleField = Gui.textField("", "Resource pack title", "Pack title, shown in Minecraft");
		label.setLabelFor(titleField);
		p.add(titleField);

		SpringUtilities.makeCompactGrid(p, 2, 2, 0, 0, Gui.GAP, Gui.GAP);

		vbox.add(p);
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


	@Override
	protected void initGui() {

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
		if (projectFromPack) buttonPickFile.addActionListener(pickFileListener);
	}

	private ActionListener createListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			String name = nameField.getText();
			if (name == null) name = "";

			System.out.println(name);

			name = name.trim();
			if (name.length() == 0) {
				Alerts.error(self(), "Invalid name", "Missing project name!");
				return;
			}

			if (options.contains(name)) {
				Alerts.error(self(), "Invalid name", "Project named \"" + name + "\" already exists!");
			} else {
				// OK name
				closeDialog();

				Alerts.loading(true);
				Projects.openNewProject(name);

				if (projectFromPack) {
					// TODO import selected resource pack
				}

				Tasks.taskOnProjectChanged();
				Alerts.loading(false);
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

					importUrl.setText(path);

					try {
						String[] parts = FileUtils.getFilenameParts(f);
						nameField.setText(parts[0]);
					} catch (Throwable t) {}
				}
			}
		}
	};
}
