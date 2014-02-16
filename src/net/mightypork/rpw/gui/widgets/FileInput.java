package net.mightypork.rpw.gui.widgets;


import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.SwingConstants;

import net.mightypork.rpw.Config.FilePath;
import net.mightypork.rpw.gui.Gui;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.helpers.FileChooser;
import net.mightypork.rpw.gui.helpers.FileChooser.FileChooserFilter;
import net.mightypork.rpw.utils.Utils;

import org.jdesktop.swingx.JXLabel;


/**
 * File picker field with file chooser dialog
 * 
 * @author MightyPork
 */
public class FileInput extends HBox {

	private File file;
	private JButton buttonPickFile;

	private FileChooser fc = null;
	private JXLabel importUrl;
	private FilePickListener filePickListener;


	/**
	 * File picker field with file chooser<br>
	 * After instantiating, use "setListener" to listen for set files.
	 * 
	 * @param parent parent dialog (for opening the filepicker)
	 * @param placeholder field placeholder (shown before file is chosen)
	 * @param pathEnum path slot (for saving to config)s
	 * @param title filechooser dialog title
	 * @param filter file filter
	 */
	public FileInput(Component parent, String placeholder, FilePath pathEnum, String title, FileChooserFilter filter) {

		importUrl = new JXLabel(placeholder);
		importUrl.setToolTipText(placeholder);
		importUrl.setForeground(new Color(0x111111));
		importUrl.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
		importUrl.setHorizontalAlignment(SwingConstants.LEFT);
		Gui.setPrefWidth(importUrl, 250);

		buttonPickFile = new JButton(Icons.MENU_OPEN);
		buttonPickFile.setToolTipText("Browse");
		
		buttonPickFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				fc.showOpenDialog();

				if (fc.approved()) {
					File f = fc.getSelectedFile();

					if (f == null) return;

					FileInput.this.file = f;
					
					String path = file.getPath();
					int length = 26;
					path = Utils.cropStringAtStart(path, length);

					importUrl.setText(path);

					if (FileInput.this.filePickListener != null) {
						FileInput.this.filePickListener.onFileSelected(f);
					}
				}

			}
		});

		// can be changed  by setFileChooser()
		this.fc = new FileChooser(parent, pathEnum, title, filter, true, false, false);


		// build the GUI
		add(buttonPickFile);
		gap();
		add(importUrl);
		glue();

		etchbdr();
	}


	public void setFileChooser(FileChooser fc) {

		this.fc = fc;
	}


	public JButton getButton() {

		return buttonPickFile;
	}


	@Override
	public void setEnabled(boolean state) {

		importUrl.setEnabled(state);
		buttonPickFile.setEnabled(state);
		super.setEnabled(state);
	}


	public void setListener(FilePickListener filePickListener) {

		this.filePickListener = filePickListener;
	}


	public File getFile() {

		return file;
	}

	public static interface FilePickListener {

		public void onFileSelected(File file);
	}
}
