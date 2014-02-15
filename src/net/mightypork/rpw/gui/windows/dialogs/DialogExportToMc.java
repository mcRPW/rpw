package net.mightypork.rpw.gui.windows.dialogs;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Config;
import net.mightypork.rpw.Const;
import net.mightypork.rpw.gui.Gui;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.helpers.TextInputValidator;
import net.mightypork.rpw.gui.widgets.HBox;
import net.mightypork.rpw.gui.widgets.SimpleStringList;
import net.mightypork.rpw.gui.widgets.VBox;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.tasks.Tasks;
import net.mightypork.rpw.utils.SpringUtilities;
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.files.OsUtils;
import net.mightypork.rpw.utils.files.SimpleConfig;
import net.mightypork.rpw.utils.logging.Log;

import org.jdesktop.swingx.JXLabel;

import com.google.gson.reflect.TypeToken;


public class DialogExportToMc extends RpwDialog {

	private List<String> installedPackNames;

	private JTextField field;
	private JButton buttonOK;
	private SimpleStringList list;
	private JButton buttonCancel;

	private JComboBox mcOptsCombo;

	private static final int MC_ALONE = 0;
	private static final int MC_ADD = 1;
	private static final int MC_NO_CHANGE = 2;


	public DialogExportToMc() {

		super(App.getFrame(), "Export");

		installedPackNames = getOptions();

		createDialog();
	}


	@Override
	protected JComponent buildGui() {

		HBox hb;
		VBox vbox = new VBox();
		vbox.windowPadding();

		vbox.heading("Export to Minecraft");


		vbox.titsep("Installed ResourcePacks");
		vbox.gap();

		vbox.add(list = new SimpleStringList(installedPackNames, true));
		list.getList().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				String s = list.getSelectedValue();
				if (s != null) field.setText(s);
			}
		});

		vbox.gapl();

		vbox.titsep("Export options");
		vbox.gap();


		
		field = Gui.textField("", "Output file name", "Output file name (without extension)");
		field.addKeyListener(TextInputValidator.filenames());
			
		String[] choices = new String[3];
		choices[MC_ALONE] = "Use this pack alone";
		choices[MC_ADD] = "Add pack to selected (on top)";
		choices[MC_NO_CHANGE] = "Don't change settings";
		
		Config.CHOICE_EXPORT_TO_MC = Math.max(0, Math.min(Config.CHOICE_EXPORT_TO_MC, choices.length-1));
		
		mcOptsCombo = new JComboBox(choices);
		mcOptsCombo.setSelectedIndex(Config.CHOICE_EXPORT_TO_MC);		

		vbox.add(Gui.springForm(new String[] {"Pack name:", "In Minecraft:"}, new JComponent[] {field, mcOptsCombo}));
		
		
		
		vbox.gapl();
		
		hb = new HBox();			
			hb.glue();
			buttonOK = new JButton("Export", Icons.MENU_EXPORT);
			hb.add(buttonOK);	
			hb.gap();	
			buttonCancel = new JButton("Cancel", Icons.MENU_CANCEL);
			hb.add(buttonCancel);
		vbox.add(hb);
		//@formatter:on


		return vbox;
	}


	@Override
	protected void addActions() {

		setEnterButton(buttonOK);

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
				return;
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

						// 0 - replace
						// 1 - put on top
						// 2 - don't change settings

						int choice = Config.CHOICE_EXPORT_TO_MC = mcOptsCombo.getSelectedIndex();
						Config.save();

						if (choice == MC_NO_CHANGE) return;


						// TODO take action based on choice

						// set as default now.

						File f = OsUtils.getMcDir("options.txt");
						if (!f.exists()) {
							Log.w("MC options file not found.");
							return;
						}

						try {
							List<String> lines = SimpleConfig.listFromFile(f);

							boolean a = false, b = false;

							String fname = name + ".zip";

							String optOld = "skin:" + fname;

							String optNew = "resourcePacks:[" + Const.GSON_UGLY.toJson(fname) + "]";

							for (int i = 0; i < lines.size(); i++) {


								// 1.6-
								if (lines.get(i).startsWith("skin:")) {
									a = true;
									Log.f3("Writing to MC options: " + optOld);
									lines.set(i, optOld);
								} else
								// 1.7+
								if (lines.get(i).startsWith("resourcePacks:")) {

									if (choice == MC_ADD) {
										try {

											String orig = lines.get(i).substring("resourcePacks:".length());
											orig = orig.trim();

											List<String> list = Const.GSON.fromJson(orig, new TypeToken<List<String>>() {}.getType());

											list.remove(fname);
											list.add(0, fname);

											String packs_new = Const.GSON_UGLY.toJson(list);

											Log.f3("Writing to MC options: " + packs_new);

											lines.set(i, "resourcePacks:" + packs_new);
											b = true;
										} catch (Exception e) {
											Log.e(e);
										}
									}

									if (!b || choice == MC_ALONE) {
										lines.set(i, optNew);
										Log.f3("Writing to MC options: " + optNew);
										b = true;
									}
								}
							}

							// add the unused one (make sure both will be present when MC starts)
							if (!b) lines.add(optNew);
							if (!a) lines.add(optOld);

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
