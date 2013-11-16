package net.mightypork.rpack.gui.windows;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.mightypork.rpack.App;
import net.mightypork.rpack.gui.Icons;
import net.mightypork.rpack.gui.helpers.CharInputListener;
import net.mightypork.rpack.gui.helpers.FilenameKeyAdapter;
import net.mightypork.rpack.gui.widgets.FileNameList;
import net.mightypork.rpack.project.Projects;
import net.mightypork.rpack.tasks.Tasks;
import net.mightypork.rpack.utils.FileUtils;
import net.mightypork.rpack.utils.Log;
import net.mightypork.rpack.utils.OsUtils;
import net.mightypork.rpack.utils.SimpleConfig;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXTextField;
import org.jdesktop.swingx.JXTitledSeparator;


public class DialogExportToMc extends RpwDialog {

	private List<String> options;

	private JXTextField field;

	private ActionListener exportListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent evt) {

			final String name = field.getText().trim();
			if (name.length() == 0) {
				Alerts.error(self(), "Invalid name", "Missing file name!");
			}

			if (options.contains(name)) {

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

						Alerts.info(App.getFrame(), "Export successful.");


						// set as default now.

						File f = OsUtils.getMcDir("options.txt");
						if (!f.exists()) {
							Log.w("MC options file not found.");
							return;
						}

						try {
							List<String> lines = SimpleConfig.listFromFile(f);

							for (int i = 0; i < lines.size(); i++) {

								// 1.7+
								if (lines.get(i).startsWith("resourcePacks:")) {
									lines.set(i, "resourcePacks:[\"" + name + ".zip\"]");
								}

								// 1.6-
								if (lines.get(i).startsWith("skin:")) {
									lines.set(i, "skin:" + name + ".zip");
								}
							}

							SimpleConfig.listToFile(f, lines);
							Log.i("Pack set as Minecraft's skin.");

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


	private JButton buttonOK;


	private FileNameList list;

	private JButton buttonCancel;


	public DialogExportToMc() {

		super(App.getFrame(), "Export To Minecraft");

		Box hb;
		Box vb = Box.createVerticalBox();
		vb.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		vb.add(new JXTitledSeparator("Installed ResourcePacks"));

		options = getOptions();

		vb.add(list = new FileNameList(options, true));
		list.list.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				String s = list.getSelectedValue();
				if (s != null) field.setText(s);
				buttonOK.setEnabled(true);
			}
		});

		vb.add(Box.createVerticalStrut(10));

		//@formatter:off
		hb = Box.createHorizontalBox();
			JXLabel label = new JXLabel("Name:");
			hb.add(label);
			hb.add(Box.createHorizontalStrut(5));
	
			field = new JXTextField();
			field.setText(Projects.getActive().getDirName());
			Border bdr = BorderFactory.createCompoundBorder(field.getBorder(), BorderFactory.createEmptyBorder(3,3,3,3));
			field.setBorder(bdr);
			
			CharInputListener listener = new CharInputListener() {
				
				@Override
				public void onCharTyped(char c) {
				
					String s = (field.getText() + c).trim();
					
					boolean ok = true;
					ok &= (s.length() > 0);
					
					buttonOK.setEnabled(ok);	
				}
			};
			
			field.addKeyListener(new FilenameKeyAdapter(listener));
			
			
			hb.add(field);
		vb.add(hb);

		
		vb.add(Box.createVerticalStrut(8));

		
		hb = Box.createHorizontalBox();
			hb.add(Box.createHorizontalGlue());
	
			buttonOK = new JButton("Export", Icons.MENU_EXPORT);
			buttonOK.setEnabled(true);
			hb.add(buttonOK);
	
			hb.add(Box.createHorizontalStrut(5));
	
			buttonCancel = new JButton("Cancel", Icons.MENU_CANCEL);
			hb.add(buttonCancel);
		vb.add(hb);
		//@formatter:on


		getContentPane().add(vb);

		prepareForDisplay();
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
			String[] parts = FileUtils.removeExtension(f);

			if (parts[1].equalsIgnoreCase("zip")) {
				options.add(parts[0]);
			}
		}

		Collections.sort(options);

		return options;
	}


	@Override
	public void onClose() {

		// do nothing
	}
}
