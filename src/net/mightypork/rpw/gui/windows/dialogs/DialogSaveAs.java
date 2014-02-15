package net.mightypork.rpw.gui.windows.dialogs;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;

import net.mightypork.rpw.App;
import net.mightypork.rpw.gui.Gui;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.helpers.TextInputValidator;
import net.mightypork.rpw.gui.widgets.HBox;
import net.mightypork.rpw.gui.widgets.VBox;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.tasks.Tasks;


public class DialogSaveAs extends RpwDialog {

	private List<String> projectNames;

	private JTextField nameField;
	private JButton buttonOK;
	//private SimpleStringList list;

	private JButton buttonCancel;

	private JTextField titleField;


	public DialogSaveAs() {

		super(App.getFrame(), "Save As...");

		projectNames = Projects.getProjectNames();

		createDialog();
	}


	@Override
	protected JComponent buildGui() {

		HBox hb;
		VBox vb = new VBox();
		vb.windowPadding();

		vb.heading("Save Project As...");

		/*vb.titsep("Your Projects");
		vb.gap();

		vb.add(list = new SimpleStringList(projectNames, true));
		list.getList().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				String s = list.getSelectedValue();
				if (s != null && nameField.getText().length() == 0) nameField.setText(s);
			}
		});

		vb.gapl();
		
		*/

		vb.titsep("New project settings");
		vb.gap();


		nameField = Gui.textField("", "Project folder name", "Project folder name - avoid special characters");
		nameField.addKeyListener(TextInputValidator.filenames());

		titleField = Gui.textField("", "Resource pack title", "Pack title, shown in Minecraft");

		vb.add(Gui.springForm(new String[] { "Name:", "Title:" }, new JComponent[] { nameField, titleField }));

		vb.gapl();

		hb = new HBox();
		hb.glue();

		buttonOK = new JButton("Save", Icons.MENU_SAVE_AS);
		hb.add(buttonOK);

		hb.gap();

		buttonCancel = new JButton("Cancel", Icons.MENU_CANCEL);
		hb.add(buttonCancel);
		vb.add(hb);
		//@formatter:on

		return vb;
	}


	@Override
	public void onClose() {

		// do nothing
	}


	@Override
	protected void addActions() {

		setEnterButton(buttonOK);

		buttonOK.addActionListener(saveListener);
		buttonCancel.addActionListener(closeListener);
	}


	private ActionListener saveListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			String name = nameField.getText().trim();
			if (name.length() == 0) {
				Alerts.error(self(), "Invalid name", "Missing project name!");
				return;
			}

			String title = titleField.getText().trim();
			if (name.length() == 0) {
				Alerts.error(self(), "Invalid title", "Missing project title!");
				return;
			}

			if (projectNames.contains(name)) {
				Alerts.error(self(), "Invalid name", "Project named \"" + name + "\" already exists!");
				return;
			} else {
				// OK name				
				Tasks.taskSaveProjectAs(name, title);
				closeDialog();
			}

		}
	};
}
