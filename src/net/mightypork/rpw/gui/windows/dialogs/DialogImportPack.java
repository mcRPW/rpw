package net.mightypork.rpw.gui.windows.dialogs;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Config.FilePath;
import net.mightypork.rpw.Paths;
import net.mightypork.rpw.gui.Gui;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.helpers.FileChooser;
import net.mightypork.rpw.gui.helpers.TextInputValidator;
import net.mightypork.rpw.gui.widgets.FilepickerField;
import net.mightypork.rpw.gui.widgets.FilepickerField.FilePickListener;
import net.mightypork.rpw.gui.widgets.HBox;
import net.mightypork.rpw.gui.widgets.VBox;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.library.Sources;
import net.mightypork.rpw.tasks.Tasks;
import net.mightypork.rpw.tree.assets.EAsset;
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.files.OsUtils;
import net.mightypork.rpw.utils.files.ZipUtils;
import net.mightypork.rpw.utils.validation.StringFilter;


public class DialogImportPack extends RpwDialog {

	private List<String> libPackNames;

	private JTextField field;

	private JButton buttonOK;
	private JButton buttonCancel;

	private FilepickerField filepicker;


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

		filepicker = new FilepickerField(this, "Select file to import...", FilePath.IMPORT_PACK, "Import resource pack", FileChooser.ZIP);
		vb.add(filepicker);

		vb.gapl();

		field = Gui.textField("", "Pack name", "Name used in RPW");
		field.addKeyListener(TextInputValidator.filenames());

		vb.add(Gui.springForm(new String[] { "Name:" }, new JComponent[] { field }));

		vb.gapl();

		//@formatter:off
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

		filepicker.setListener(new FilePickListener() {
			
			@Override
			public void onFileSelected(File file) {
				try {
					String[] parts = FileUtils.getFilenameParts(file);
					if (field.getText().trim().length() == 0) {
						field.setText(parts[0]);
					}

				} catch (Throwable t) {}
			}
		});
	}


	@Override
	protected void addActions() {

		setEnterButton(buttonOK);
		buttonOK.addActionListener(submitListener);
		buttonCancel.addActionListener(closeListener);
	}


	@Override
	public void onClose() {

		Tasks.taskReloadSources(null);
	}

	private ActionListener submitListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			File file = filepicker.getFile();

			if (file == null) {
				Alerts.error(self(), "No file selected", "Select file to import!");
				return;
			}

			if (!file.exists()) {
				Alerts.error(self(), "Invalid file", "The selected file does not exist!");
				return;
			}

			String name = field.getText().trim();
			if (name.length() == 0) {
				Alerts.error(self(), "Invalid name", "Missing source name!");
				return;
			}

			if (libPackNames.contains(name)) {
				Alerts.error(self(), "Invalid name", "Source named \"" + name + "\" already exists!");
				return;
			}

			
			// do the import
			
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

				if (!ZipUtils.entryExists(file, "pack.mcmeta")) {
					Alerts.error(self(), "Invalid format", "Selected ZIP file isn't\na valid resource pack!");
					return;
				}


				ZipUtils.extractZip(file, out, filter);
				closeDialog();
				Alerts.info(App.getFrame(), "Resource Pack \"" + name + "\" was imported.");

			} catch (Exception exc) {
				Alerts.error(DialogImportPack.this, "Error while extracting ResourcePack zip.");
				FileUtils.delete(out, true); // cleanup
			}

		}
	};
}
