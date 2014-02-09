package net.mightypork.rpw.gui.windows.dialogs;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Config.FilePath;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.helpers.CharInputListener;
import net.mightypork.rpw.gui.helpers.FileChooser;
import net.mightypork.rpw.gui.helpers.TextInputValidator;
import net.mightypork.rpw.gui.widgets.SimpleStringList;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.tasks.Tasks;
import net.mightypork.rpw.utils.FileUtils;
import net.mightypork.rpw.utils.Utils;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXTextField;
import org.jdesktop.swingx.JXTitledSeparator;


public class DialogNewProject extends RpwDialog {

	private List<String> options;

	private JXTextField field;
	private JButton buttonOK;
	private JButton buttonCancel;
	private SimpleStringList list;

	private JXLabel importUrl;

	private JButton buttonPickFile;

	private boolean projectFromPack;	
	private File selectedFile;
	private FileChooser fc;


	public DialogNewProject(boolean imported) {

		super(App.getFrame(), "New Project");

		this.projectFromPack = imported;

		createDialog();
	}


	@Override
	protected JComponent buildGui() {

		Box hb;
		JXLabel label;
		Box vb = Box.createVerticalBox();
		vb.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		

		vb.add(new JXTitledSeparator("Pack to import"));
		
		if(projectFromPack) {
			vb.add(Box.createVerticalStrut(4));
			hb = Box.createHorizontalBox();
				importUrl = new JXLabel("Select pack file");
				importUrl.setToolTipText("Imported pack ZIP file");
				importUrl.setForeground(new Color(0x111111));
				importUrl.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
				importUrl.setHorizontalAlignment(SwingConstants.RIGHT);
				importUrl.setMaximumSize(new Dimension(300, 25));
		
				buttonPickFile = new JButton(Icons.MENU_OPEN);
				buttonPickFile.requestFocusInWindow();
		
				hb.add(buttonPickFile);
				hb.add(Box.createHorizontalStrut(5));
				hb.add(importUrl);
				hb.add(Box.createHorizontalGlue());
		
				hb.setBorder(new CompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(3, 3, 3, 3)));
			vb.add(hb);
			vb.add(Box.createVerticalStrut(8));
		}

		vb.add(new JXTitledSeparator("Your Projects"));
		vb.add(Box.createVerticalStrut(4));

		options = Projects.getProjectNames();

		vb.add(list = new SimpleStringList(options, true));
		list.getList().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				String s = list.getSelectedValue();
				if (s != null) field.setText(s);
			}
		});

		vb.add(Box.createVerticalStrut(10));

		//@formatter:off
		hb = Box.createHorizontalBox();
			label = new JXLabel("Name:");
			hb.add(label);
			hb.add(Box.createHorizontalStrut(5));
	
			field = new JXTextField();
			Border bdr = BorderFactory.createCompoundBorder(field.getBorder(), BorderFactory.createEmptyBorder(3,3,3,3));
			field.setBorder(bdr);
			field.requestFocusInWindow();
			
			CharInputListener listener = new CharInputListener() {
				
				@Override
				public void onCharTyped(char c) {
				
					String s = (field.getText() + c).trim();
					
					boolean ok = true;
					ok &= (s.length() > 0);
					ok &= !options.contains(s);
					
					buttonOK.setEnabled(ok);	
				}
			};
			
			field.addKeyListener(TextInputValidator.filenames(listener));
			
			
			hb.add(field);
		vb.add(hb);
		
		vb.add(Box.createVerticalStrut(8));
		
		hb = Box.createHorizontalBox();
			hb.add(Box.createHorizontalGlue());
	
			buttonOK = new JButton("Create", Icons.MENU_NEW);
			buttonOK.setEnabled(false);
			hb.add(buttonOK);
	
			hb.add(Box.createHorizontalStrut(5));
	
			buttonCancel = new JButton("Cancel", Icons.MENU_CANCEL);
			hb.add(buttonCancel);			
		vb.add(hb);
		//@formatter:on

		return vb;
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
		if(projectFromPack) buttonPickFile.addActionListener(pickFileListener);
	}

	private ActionListener createListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			String name = field.getText().trim();
			if (name.length() == 0) {
				Alerts.error(self(), "Invalid name", "Missing project name!");
			}

			if (options.contains(name)) {
				Alerts.error(self(), "Invalid name", "Project named \"" + name + "\" already exists!");
			} else {
				// OK name
				closeDialog();

				Alerts.loading(true);
				Projects.openNewProject(name);
				
				if(projectFromPack) {
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
					int length = 27;
					path = Utils.cropStringAtStart(path, length);

					importUrl.setText(path);

					try {
						String[] parts = FileUtils.getFilenameParts(f);
						field.setText(parts[0]);

						buttonOK.setEnabled(!options.contains(parts[0]));
					} catch (Throwable t) {}
				}
			}
		}
	};
}
