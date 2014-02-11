package net.mightypork.rpw.gui.windows.dialogs;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Config.FilePath;
import net.mightypork.rpw.Paths;
import net.mightypork.rpw.gui.Gui;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.helpers.FileChooser;
import net.mightypork.rpw.gui.helpers.TextInputValidator;
import net.mightypork.rpw.gui.widgets.HBox;
import net.mightypork.rpw.gui.widgets.VBox;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.library.Sources;
import net.mightypork.rpw.tasks.Tasks;
import net.mightypork.rpw.tree.assets.EAsset;
import net.mightypork.rpw.utils.Utils;
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.files.OsUtils;
import net.mightypork.rpw.utils.files.ZipUtils;
import net.mightypork.rpw.utils.validation.StringFilter;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXTextField;


public class DialogImportPack extends RpwDialog {

	private List<String> libPackNames;

	private JXTextField field;

	private JButton buttonPickFile;
	private JButton buttonOK;
	private JButton buttonCancel;

	private JXLabel importUrl;
	private FileChooser fc;
	private File selectedFile;


	public DialogImportPack() {

		super(App.getFrame(), "Import");

		libPackNames = Sources.getResourcepackNames();

		createDialog();
	}


	@Override
	protected JComponent buildGui() {

		HBox hb;
		VBox vb = new VBox();
		vb.windowPadding();

		vb.heading("Import resource pack");

		vb.titsep("File to import");
		vb.gap();

		//@formatter:off
		hb = new HBox();
			importUrl = new JXLabel("Select file to import...");
			importUrl.setToolTipText("Imported ZIP file");
			importUrl.setForeground(new Color(0x111111));
			importUrl.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
			importUrl.setHorizontalAlignment(SwingConstants.LEFT);
			importUrl.setMaximumSize(new Dimension(300, 25));
	
			buttonPickFile = new JButton(Icons.MENU_OPEN);
			buttonPickFile.requestFocusInWindow();
	
			hb.add(buttonPickFile);
			hb.gap();
			hb.add(importUrl);
			hb.glue();
	
			hb.etchbdr();
		vb.add(hb);
		//@formatter:on

		vb.gapl();

		//@formatter:off
		hb = new HBox();
			hb.add(new JXLabel("Name:"));
			hb.gap();
	
			field = Gui.textField("","Pack name","Name used in RPW");
						
			field.addKeyListener(TextInputValidator.filenames());
			
			hb.add(field);
		vb.add(hb);

		
		vb.gapl();
		
		hb = new HBox();
			hb.glue();	
			buttonOK = new JButton("Import", Icons.MENU_YES);
			hb.add(buttonOK);	
			hb.gap();	
			buttonCancel = new JButton("Cancel", Icons.MENU_CANCEL);
			hb.add(buttonCancel);
		vb.add(hb);
		//@formatter:on

		return vb;
	}


	@Override
	protected void initGui() {

		fc = new FileChooser(this, FilePath.IMPORT_PACK, "Import resource pack", "zip", "ZIP archives", true, false, false);

	}


	@Override
	protected void addActions() {

		setEnterButton(buttonOK);
		buttonPickFile.addActionListener(pickFileListener);
		buttonOK.addActionListener(importListener);
		buttonCancel.addActionListener(closeListener);
	}


	@Override
	public void onClose() {

		Tasks.taskReloadSources(null);
	}

	private ActionListener importListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			if (selectedFile == null) {
				Alerts.error(DialogImportPack.this, "No file selected", "Select file to import!");
				return;
			}

			if (!selectedFile.exists()) {
				Alerts.error(DialogImportPack.this, "Invalid file", "The selected file does not exist!");
				return;
			}

			String name = field.getText().trim();
			if (name.length() == 0) {
				Alerts.error(DialogImportPack.this, "Invalid name", "Missing source name!");
				return;
			}

			if (libPackNames.contains(name)) {
				Alerts.error(DialogImportPack.this, "Invalid name", "Source named \"" + name + "\" already exists!");
			} else {

				File out = OsUtils.getAppDir(Paths.DIR_RESOURCEPACKS + "/" + name, true);

				StringFilter filter = new StringFilter() {

					@Override
					public boolean accept(String path) {

						boolean ok = false;

						String ext = FileUtils.getExtension(path);
						EAsset type = EAsset.forExtension(ext);

						ok |= path.startsWith("assets");
						ok &= type.isAsset();

						return ok;
					}
				};

				try {
					ZipUtils.extractZip(selectedFile, out, filter);
					closeDialog();
					Alerts.info(App.getFrame(), "Resource Pack \"" + name + "\" was imported.");

				} catch (Exception exc) {
					Alerts.error(DialogImportPack.this, "Error while extracting ResourcePack zip.");
					FileUtils.delete(out, true); // cleanup
				}

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
					int length = 26;
					path = Utils.cropStringAtStart(path, length);

					importUrl.setText(path);

					try {
						String[] parts = FileUtils.getFilenameParts(f);
						if (field.getText().trim().length() == 0) {
							field.setText(parts[0]);
						}

					} catch (Throwable t) {}
				}
			}
		}
	};
}
