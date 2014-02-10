package net.mightypork.rpw.gui.windows.dialogs;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.mightypork.rpw.App;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.helpers.TextInputValidator;
import net.mightypork.rpw.gui.widgets.HBox;
import net.mightypork.rpw.gui.widgets.SimpleStringList;
import net.mightypork.rpw.gui.widgets.VBox;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.tasks.Tasks;
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.files.OsUtils;
import net.mightypork.rpw.utils.files.SimpleConfig;
import net.mightypork.rpw.utils.logging.Log;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXTextField;


public class DialogExportToMc extends RpwDialog {

	private List<String> installedPackNames;

	private JXTextField field;
	private JButton buttonOK;
	private SimpleStringList list;
	private JButton buttonCancel;


	public DialogExportToMc() {

		super(App.getFrame(), "Export");

		createDialog();
	}


	@Override
	protected JComponent buildGui() {

		HBox hb;
		VBox vb = new VBox();
		vb.windowPadding();

		vb.heading("Export to Minecraft");

		installedPackNames = getOptions();

		vb.titsep("Installed ResourcePacks");
		vb.gap();

		vb.add(list = new SimpleStringList(installedPackNames, true));
		list.getList().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				String s = list.getSelectedValue();
				if (s != null) field.setText(s);
			}
		});

		vb.gapl();

		vb.titsep("Export options");
		vb.gap();

		//@formatter:off
		hb = new HBox();
			hb.add(new JXLabel("Pack name:"));
			hb.gap();
	
			field = new JXTextField();
			field.setText(Projects.getActive().getDirName());
			Border bdr = BorderFactory.createCompoundBorder(field.getBorder(), BorderFactory.createEmptyBorder(3,3,3,3));
			field.setBorder(bdr);
						
			field.addKeyListener(TextInputValidator.filenames(null));
			
			
			hb.add(field);
		vb.add(hb);

		
		vb.gapl();

		
		hb = new HBox();			
			hb.glue();
			buttonOK = new JButton("Export", Icons.MENU_EXPORT);
			hb.add(buttonOK);	
			hb.gap();	
			buttonCancel = new JButton("Cancel", Icons.MENU_CANCEL);
			hb.add(buttonCancel);
		vb.add(hb);
		//@formatter:on


		return vb;
	}


	@Override
	protected void addActions() {

		buttonCancel.addActionListener(closeListener);

		buttonOK.addActionListener(exportListener);
	}


	private List<String> getOptions() {

		List<File> aList = FileUtils.listDirectory(OsUtils.getMcDir("resourcepacks"));
		List<String> options = new ArrayList<String>();

		for (File f : aList) {
			if (f.isDirectory()) continue;
			String[] parts = FileUtils.getFilenameParts(f);

			if (parts[1].equalsIgnoreCase("zip")) {
				options.add(parts[0]);
			}
		}

		Collections.sort(options);

		return options;
	}


	private ActionListener exportListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent evt) {

			final String name = field.getText().trim();
			if (name.length() == 0) {
				Alerts.error(self(), "Invalid name", "Missing file name!");
			}

			if (installedPackNames.contains(name)) {

				//@formatter:off
				boolean overwrite = Alerts.askYesNo(
						App.getFrame(),
						"File Exists",
						"File named \"" + name + ".zip\" already exists in the output folder.\n" +
						"Do you want to overwrite it?"
				);
				//@formatter:on

				if (!overwrite) return;

			}

			// OK name		

			File file = OsUtils.getMcDir("resourcepacks/" + name + ".zip");

			try {
				closeDialog();

				Tasks.taskExportProject(file, new Runnable() {

					@Override
					public void run() {

						// set as default now.

						File f = OsUtils.getMcDir("options.txt");
						if (!f.exists()) {
							Log.w("MC options file not found.");
							return;
						}

						try {
							List<String> lines = SimpleConfig.listFromFile(f);

							boolean a = false, b = false;

							String optA = "skin:" + name + ".zip";
							String optB = "resourcePacks:[\"" + name + ".zip\"]";

							for (int i = 0; i < lines.size(); i++) {

								// 1.7+
								if (lines.get(i).startsWith("resourcePacks:")) {
									b = true;
									lines.set(i, optB);
								}

								// 1.6-
								if (lines.get(i).startsWith("skin:")) {
									a = true;
									lines.set(i, optA);
								}
							}

							// add the unused one (make sure both will be present when MC starts)
							if (!b) lines.add(optB);
							if (!a) lines.add(optA);

							SimpleConfig.listToFile(f, lines);
							Log.i("Minecraft config file was changed.");

						} catch (IOException e) {
							Log.e(e);
						}
					}
				});


			} catch (Exception e) {
				Alerts.error(self(), "An error occured while exporting.");
				Log.e(e);
			}
		}
	};
}
