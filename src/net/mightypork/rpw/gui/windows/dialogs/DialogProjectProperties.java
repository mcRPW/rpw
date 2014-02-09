package net.mightypork.rpw.gui.windows.dialogs;


import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.*;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Config.FilePath;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.helpers.FileChooser;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.project.Project;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.tasks.Tasks;
import net.mightypork.rpw.utils.DesktopApi;
import net.mightypork.rpw.utils.FileUtils;
import net.mightypork.rpw.utils.GuiUtils;

import org.jdesktop.swingx.JXTextField;
import org.jdesktop.swingx.JXTitledSeparator;


public class DialogProjectProperties extends RpwDialog {


	private FileChooser fc;
	private JXTextField titleField;
	private JButton buttonOK;
	private JLabel imageView;

	private File iconFile;
	private JButton btnIconEdit;
	private JButton btnIconImport;
	private JButton btnIconDefault;
	private JButton btnIconRefresh;


	private void redrawIcon() {

		imageView.setIcon(getProjectIcon());
	}


	public DialogProjectProperties() {

		super(App.getFrame(), "Project Properties");

		iconFile = new File(Projects.getActive().getProjectDirectory(), "pack.png");


		createDialog();
	}


	@Override
	protected JComponent buildGui() {

		Project proj = Projects.getActive();

		//@formatter:off
		Box hb;
		Box vb, vb2;
		vb = Box.createVerticalBox();
		
		vb.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

		vb.add(new JXTitledSeparator("Properties"));
		vb.add(Box.createVerticalStrut(5));

		hb = Box.createHorizontalBox();
			hb.add(new JLabel("Title:"));
			hb.add(Box.createHorizontalStrut(5));
			String name = proj.getProjectName();
			hb.add(titleField = new JXTextField());
			titleField.setText(name);
		vb.add(hb);

		vb.add(Box.createVerticalStrut(8));

		vb.add(new JXTitledSeparator("Icon"));
		vb.add(Box.createVerticalStrut(5));

		hb = Box.createHorizontalBox();

			imageView = new JLabel(getProjectIcon());
			hb.add(imageView);
	
			hb.add(Box.createHorizontalStrut(8));
	
			vb2 = Box.createVerticalBox();
			
				vb2.add(btnIconEdit = new JButton("Edit", Icons.MENU_EDIT));
				btnIconEdit.setHorizontalAlignment(SwingConstants.LEFT);
				GuiUtils.forceSize(btnIconEdit, 115, 25);			
		
				vb2.add(Box.createVerticalStrut(3));
				
				vb2.add(btnIconImport = new JButton("Import", Icons.MENU_IMPORT_BOX));
				btnIconImport.setHorizontalAlignment(SwingConstants.LEFT);
				GuiUtils.forceSize(btnIconImport, 115, 25);
		
				vb2.add(Box.createVerticalStrut(3));
				
				vb2.add(btnIconDefault = new JButton("Default", Icons.MENU_DELETE));
				btnIconDefault.setHorizontalAlignment(SwingConstants.LEFT);
				GuiUtils.forceSize(btnIconDefault, 115, 25);
							
				vb2.add(Box.createVerticalStrut(10));
				
				vb2.add(btnIconRefresh = new JButton("Refresh", Icons.MENU_RELOAD));
				btnIconRefresh.setHorizontalAlignment(SwingConstants.LEFT);
				GuiUtils.forceSize(btnIconRefresh, 115, 25);
				
				vb2.add(Box.createVerticalGlue());
	
			hb.add(vb2);

		vb.add(hb);


		vb.add(Box.createVerticalStrut(8));
		
		hb = Box.createHorizontalBox();
			hb.add(Box.createHorizontalGlue());
	
			buttonOK = new JButton("OK", Icons.MENU_YES);
			hb.add(buttonOK);
		vb.add(hb);
		//@formatter:on

		return vb;
	}


	@Override
	protected void initGui() {

		fc = new FileChooser(this, FilePath.IMPORT_FILE, "Import Project Icon (128x128 PNG)", "png", "PNG images", true, false, false);
	}


	private Icon getProjectIcon() {

		return Icons.getIconFromFile(iconFile, new Dimension(128, 128));
	}


	@Override
	public void onClose() {

		Tasks.taskOnProjectPropertiesChanged();
	}


	@Override
	protected void addActions() {

		btnIconEdit.addActionListener(imgEditListener);
		btnIconImport.addActionListener(imgImportListener);
		btnIconDefault.addActionListener(imgDefaultListener);
		btnIconRefresh.addActionListener(imgRedrawListener);
		buttonOK.addActionListener(okListener);
	}

	private ActionListener okListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			String name = titleField.getText().trim();

			Projects.getActive().setProjectTitle(name);

			closeDialog();
		}
	};

	private ActionListener imgEditListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			if (!DesktopApi.editImage(iconFile)) {
				//@formatter:off
				Alerts.error(
						App.getFrame(),
						"Could not edit file, your\n" +
						"platform is not supported.\n" +
						"\n" +
						"Check log file for details."
				);
				//@formatter:on
			}
		}
	};

	private ActionListener imgImportListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			fc.showOpenDialog();
			if (fc.approved()) {
				File f = fc.getSelectedFile();

				if (f == null) return;

				if (f.exists()) {
					try {
						FileUtils.copyFile(f, iconFile);
						redrawIcon();
					} catch (IOException e1) {
						//@formatter:off
						Alerts.error(
								self(),
								"Error Copying File",
								"An error occured while copying\n" +
								"selected icon file to project\n" +
								"directory."
						);
						//@formatter:on
					}
				}
			}
		}

	};


	private ActionListener imgDefaultListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			Projects.getActive().copyInDefaultIcon(true);
			redrawIcon();
		}
	};

	private ActionListener imgRedrawListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			redrawIcon();
		}
	};

}
