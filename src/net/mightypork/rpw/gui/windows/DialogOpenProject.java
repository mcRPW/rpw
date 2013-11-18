package net.mightypork.rpw.gui.windows;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.mightypork.rpw.App;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.widgets.SimpleStringList;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.tasks.Tasks;

import org.jdesktop.swingx.JXTitledSeparator;


public class DialogOpenProject extends RpwDialog {

	private List<String> options;

	private SimpleStringList list;
	private JButton buttonOK;
	private JButton buttonCancel;


	public DialogOpenProject() {

		super(App.getFrame(), "Open Project");

		Box hb;
		Box vb = Box.createVerticalBox();
		vb.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		vb.add(new JXTitledSeparator("Your Projects"));

		options = Projects.getProjectNames();

		vb.add(list = new SimpleStringList(options, true));
		list.list.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				buttonOK.setEnabled(list.getSelectedIndex() != -1);
			}
		});

		vb.add(Box.createVerticalStrut(8));

		//@formatter:off		
		hb = Box.createHorizontalBox();
			hb.add(Box.createHorizontalGlue());
	
			hb.add(buttonOK = new JButton("Open", Icons.MENU_OPEN));
			buttonOK.setEnabled(false);
	
			hb.add(Box.createHorizontalStrut(5));
			
			hb.add(buttonCancel = new JButton("Cancel", Icons.MENU_CANCEL));
		vb.add(hb);
		//@formatter:on

		getContentPane().add(vb);

		prepareForDisplay();
	}


	@Override
	public void onClose() {

		// do nothing
	}


	@Override
	protected void addActions() {

		buttonOK.addActionListener(openListener);
		buttonCancel.addActionListener(closeListener);
	}

	private ActionListener openListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			String choice = list.getSelectedValue();

			if (choice == null) {
				Alerts.error(self(), "Nothing selected", "Select a project!");
				return;
			}

			// OK name
			closeDialog();
			Tasks.taskOpenProject(choice);

		}
	};
}
