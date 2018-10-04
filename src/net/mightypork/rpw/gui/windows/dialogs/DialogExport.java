package net.mightypork.rpw.gui.windows.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
import net.mightypork.rpw.gui.helpers.FileChooser;
import net.mightypork.rpw.gui.helpers.TextInputValidator;
import net.mightypork.rpw.gui.widgets.FileInput;
import net.mightypork.rpw.gui.widgets.SimpleStringList;
import net.mightypork.rpw.gui.widgets.VBox;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.tasks.Tasks;
import net.mightypork.rpw.tasks.sequences.SequenceExportProject;
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.files.OsUtils;
import net.mightypork.rpw.utils.files.SimpleConfig;
import net.mightypork.rpw.utils.logging.Log;

import com.google.gson.reflect.TypeToken;


public class DialogExport extends RpwDialog {

    private final List<String> installedPackNames;

    private JTextField nameField;
    private JTextField descriptionField;
    private JButton buttonOK;
    private SimpleStringList list;
    private JButton buttonCancel;
    private JCheckBox exportToMc;
    private FileInput filepicker;

    private JComboBox mcOptsCombo;
    private JComboBox packMeta;
    private JCheckBox unZip;

    private static final int MC_ALONE = 0;
    private static final int MC_ADD = 1;
    private static final int MC_NO_CHANGE = 2;


    public DialogExport() {
        super(App.getFrame(), "Export");

        installedPackNames = getOptions();

        createDialog();
    }


    @Override
    protected JComponent buildGui() {
        final VBox vbox = new VBox();
        vbox.windowPadding();

        vbox.heading("Export");

        vbox.titsep("Installed Resourcepacks");
        vbox.gap();

        vbox.add(list = new SimpleStringList(installedPackNames, true));
        list.getList().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                final String s = list.getSelectedValue();
                if (s != null) nameField.setText(s);
            }
        });

        vbox.gapl();

        vbox.titsep("Export options");
        vbox.gap();

        nameField = Gui.textField("", "Output file name", "Output file name (without extension)");
        nameField.addKeyListener(TextInputValidator.filenames());
        descriptionField = Gui.textField("", "Output file description", "Output file description");

        exportToMc = Gui.checkbox(true);

        final String[] choices = new String[3];
        choices[MC_ALONE] = "Use this pack alone";
        choices[MC_ADD] = "Add pack to selected (on top)";
        choices[MC_NO_CHANGE] = "Don't change settings";

        Config.CHOICE_EXPORT_TO_MC = Math.max(0, Math.min(Config.CHOICE_EXPORT_TO_MC, choices.length - 1));

        mcOptsCombo = new JComboBox(choices);
        mcOptsCombo.setSelectedIndex(Config.CHOICE_EXPORT_TO_MC);
        packMeta = new JComboBox(new String[]{"1", "2", "3", "4"});
        packMeta.setSelectedIndex(SequenceExportProject.getPackMetaNumber() - 1);
        unZip = Gui.checkbox(false);

        vbox.springForm(new String[]{"Resourcepack Name:", "Resourcepack Description:", "Export To Minecraft:"}, new JComponent[]{nameField, descriptionField, exportToMc});

        filepicker = new FileInput(this, "Select folder to export to...", Config.FilePath.EXPORT, "Select folder to export to", FileChooser.FOLDERS, true);
        filepicker.setEnabled(false);
        vbox.add(filepicker);

        vbox.springForm(new String[]{"Resourcepack Format:", "In Minecraft:", "Unzip:"}, new JComponent[]{packMeta, mcOptsCombo, unZip});

        vbox.gapl();

        buttonOK = new JButton("Export", Icons.MENU_EXPORT);
        buttonCancel = new JButton("Cancel", Icons.MENU_CANCEL);
        vbox.buttonRow(Gui.RIGHT, buttonOK, buttonCancel);

        return vbox;
    }


    @Override
    protected void addActions() {
        setEnterButton(buttonOK);

        buttonCancel.addActionListener(closeListener);

        buttonOK.addActionListener(exportListener);

        exportToMc.addItemListener(exportToMcListener);
    }


    private List<String> getOptions() {
        final List<File> aList = FileUtils.listDirectory(OsUtils.getMcDir("resourcepacks"));
        final List<String> options = new ArrayList<String>();

        for (final File f : aList) {
            if (f.isDirectory()) continue;
            final String[] parts = FileUtils.getFilenameParts(f);

            if (parts[1].equalsIgnoreCase("zip")) {
                options.add(parts[0]);
            }
        }

        Collections.sort(options);

        return options;
    }


    @Override
    protected void onShown() {
        nameField.setText(Projects.getActive().getTitle());
        descriptionField.setText(Projects.getActive().getDescription());
    }

    private final ActionListener exportListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent evt) {
            Projects.getActive().setExportPackVersion(packMeta.getSelectedIndex() + 1);
            Projects.getActive().setUnZip(unZip.isSelected());
            Projects.getActive().setTitle(nameField.getText());
            Projects.getActive().setDescription(descriptionField.getText());

            final String name = nameField.getText().trim();
            if (name.length() == 0) {
                Alerts.error(self(), "Invalid name", "Missing file name!");
                return;
            }

            if (installedPackNames.contains(name)) {
                //@formatter:off
                final boolean overwrite = Alerts.askYesNo(
                        App.getFrame(),
                        "File Exists",
                        "File named \"" + name + ".zip\" already exists in the output folder.\n" +
                                "Do you want to overwrite it?"
                );
                //@formatter:on

                if (!overwrite) return;

            }
            File file = null;
            if (exportToMc.isSelected()) {
                file = OsUtils.getMcDir("resourcepacks/" + name + ".zip");
            } else if (filepicker.getFile() != null){
                file = new File(filepicker.getFile().getPath() + "/" + name + ".zip");
            }

            try {
                closeDialog();

                if(file != null){
                Tasks.taskExportProject(file, new Runnable() {

                    @Override
                    public void run() {
                        // 0 - replace
                        // 1 - put on top
                        // 2 - don't change settings

                        final int choice = Config.CHOICE_EXPORT_TO_MC = mcOptsCombo.getSelectedIndex();
                        Config.save();

                        if (choice == MC_NO_CHANGE) return;

                        // TODO take action based on choice

                        // set as default now.

                        final File f = OsUtils.getMcDir("options.txt");
                        if (!f.exists()) {
                            Log.w("MC options file not found.");
                            return;
                        }

                        try {
                            final List<String> lines = SimpleConfig.listFromFile(f);

                            boolean a = false, b = false;

                            final String fname = name + ".zip";

                            final String optOld = "skin:" + fname;

                            final String optNew = "resourcePacks:[" + Const.GSON_UGLY.toJson(fname) + "]";

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

                                                final List<String> list = Const.GSON.fromJson(orig, new TypeToken<List<String>>() {
                                                }.getType());

                                                list.remove(fname);
                                                list.add(0, fname);

                                                final String packs_new = Const.GSON_UGLY.toJson(list);

                                                Log.f3("Writing to MC options: " + packs_new);

                                                lines.set(i, "resourcePacks:" + packs_new);
                                                b = true;
                                            } catch (final Exception e) {
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

                            // add the unused one (make sure both will be
                            // present when MC starts)
                            if (!b) lines.add(optNew);
                            if (!a) lines.add(optOld);

                            SimpleConfig.listToFile(f, lines);
                            Log.i("Minecraft config file was changed.");

                        } catch (final IOException e) {
                            Log.e(e);
                        }
                    }
                });

            }} catch (final Exception e) {
                Alerts.error(self(), "An error occured while exporting.");
                Log.e(e);
            }
        }
    };

    private final ItemListener exportToMcListener = new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                mcOptsCombo.setEnabled(true);
                filepicker.setEnabled(false);
            } else {
                mcOptsCombo.setEnabled(false);
                filepicker.setEnabled(true);
            }
        }

    };

}
