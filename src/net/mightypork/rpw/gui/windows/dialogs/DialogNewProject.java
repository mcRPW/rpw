package net.mightypork.rpw.gui.windows.dialogs;

import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Config;
import net.mightypork.rpw.Config.FilePath;
import net.mightypork.rpw.gui.Gui;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.helpers.CharInputListener;
import net.mightypork.rpw.gui.helpers.FileChooser;
import net.mightypork.rpw.gui.helpers.TextInputValidator;
import net.mightypork.rpw.gui.widgets.FileInput;
import net.mightypork.rpw.gui.widgets.FileInput.FilePickListener;
import net.mightypork.rpw.gui.widgets.HBox;
import net.mightypork.rpw.gui.widgets.VBox;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.tasks.Tasks;
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.files.OsUtils;
import net.mightypork.rpw.utils.logging.Log;


public class DialogNewProject extends RpwDialog {

    private final List<String> projectNames;

    private JTextField nameField;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JRadioButton radioBlank;
    private JRadioButton radioResourcePack;
    private JComboBox assetVersion;

    private final List<JComponent> nameFieldGroup = new ArrayList<JComponent>();

    private boolean usePackFile = false;

    private final List<JComponent> respackGroup = new ArrayList<JComponent>();

    private JCheckBox ckKeepTitle;

    private FileInput filepicker;

    /**
     * Name has not been changed manually yet
     */
    private boolean nameIsPristine;


    public DialogNewProject() {
        super(App.getFrame(), "New Project");

        projectNames = Projects.getProjectNames();

        nameIsPristine = true;

        createDialog();
    }


    @Override
    protected JComponent buildGui() {
        HBox hb, hb2;
        final VBox vbox = new VBox();
        vbox.windowPadding();

        vbox.heading("Create New Project");

        vbox.titsep("Project type");
        vbox.gap();

        //@formatter:off

        hb = new HBox();
        hb.add(radioBlank = new JRadioButton("Blank project"));
        radioBlank.setForeground(Gui.SUBHEADING_COLOR);
        radioBlank.setSelected(true);
        hb.glue();
        vbox.add(hb);

        vbox.gapl();

        hb = new HBox();
        hb.add(radioResourcePack = new JRadioButton("Project from resourcepack"));
        radioResourcePack.setForeground(Gui.SUBHEADING_COLOR);
        hb.glue();
        vbox.add(hb);

        vbox.gap();
        hb = new HBox();
        hb.gapl();

        hb.add(filepicker = new FileInput(
                        this,
                        "Select resoucepack file...",
                        FilePath.IMPORT_PACK,
                        "Select resourcepack to import as project",
                        FileChooser.FOLDERS_ZIP,
                        true
                )
        );

        vbox.add(hb);

        respackGroup.add(hb);
        respackGroup.add(filepicker);

        hb2 = new HBox();
        hb2.gapl();
        hb2.add(ckKeepTitle = new JCheckBox("Keep original title", true));
        hb2.glue();
        vbox.add(hb2);

        respackGroup.add(hb2);
        respackGroup.add(ckKeepTitle);

        final ButtonGroup group = new ButtonGroup();
        group.add(radioBlank);
        group.add(radioResourcePack);

        //@formatter:on

        vbox.gapl();
        vbox.titsep("Project settings");
        vbox.gap();

        nameField = Gui.textField("", "Project folder name", "Project folder name - avoid special characters");
        nameFieldGroup.add(nameField);
        nameField.addKeyListener(TextInputValidator.strictFilenames(new CharInputListener() {
            @Override
            public void onCharTyped(char c) {
                nameIsPristine = nameField.getText().isEmpty();
            }
        }));

        radioResourcePack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (radioResourcePack.isSelected()) {
                    enableNameField(false);
                }
            }
        });

        radioBlank.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (radioBlank.isSelected()) {
                    enableNameField(true);
                }
            }
        });

        vbox.springForm(new String[]{"Name:"}, new JComponent[]{nameField});

        vbox.add(Gui.commentLine("Name - folder name."));
        vbox.gap();

        assetVersion = new JComboBox();
        File file = new File(OsUtils.getAppDir().getPath() + "/library/vanilla/");
        for (int i = 0; i < file.list().length; i++) {
            assetVersion.addItem(file.list()[i]);
        }
        vbox.add(assetVersion);
        vbox.gap();
        vbox.add(Gui.commentLine("Asset version"));
        vbox.gapl();

        buttonOK = new JButton("Create", Icons.MENU_NEW);
        buttonCancel = new JButton("Cancel", Icons.MENU_CANCEL);
        vbox.buttonRow(Gui.RIGHT, buttonOK, buttonCancel);

        return vbox;
    }


    private void enableFilePicker(boolean enable) {
        for (final JComponent j : respackGroup) {
            j.setEnabled(enable);
        }
    }

    @Override
    protected void initGui() {
        enableFilePicker(false);
    }


    @Override
    public void onClose() {
        // do nothing
    }

    private void enableNameField(boolean enable) {
        for (final JComponent j : nameFieldGroup) {
            j.setEnabled(enable);
        }
    }

    @Override
    protected void addActions() {
        buttonOK.addActionListener(createListener);
        buttonCancel.addActionListener(closeListener);

        ckKeepTitle.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                enableNameField(e.getStateChange() == ItemEvent.DESELECTED);
            }
        });

        radioBlank.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    enableFilePicker(false);
                    usePackFile = false;
                }
            }
        });

        radioResourcePack.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    enableFilePicker(true);
                    usePackFile = true;
                }
            }
        });

        filepicker.setListener(new FilePickListener() {

            @Override
            public void onFileSelected(File f) {
                if (f.exists()) {
                    try {
                        final String[] parts = FileUtils.getFilenameParts(f);

                        if (nameField.getText().trim().length() == 0) {
                            nameField.setText(parts[0]);
                        }
                    } catch (final Throwable t) {
                        Log.e(t);
                    }
                }

            }
        });
    }

    private final ActionListener createListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText();

            if (name == null) {
                name = "";
            }

            name = name.trim();
            if (name.length() == 0) {
                Alerts.error(self(), "Invalid name", "Missing project name!");
                return;
            }

            if (usePackFile && !filepicker.hasFile()) {
                Alerts.error(self(), "Missing file", "The selected file does not exist.");
                return;
            }

            if (projectNames.contains(name)) {
                Alerts.error(self(), "Name already used", "Project named \"" + name + "\" already exists!");
                return;
            }

            final File file = filepicker.getFile();

            // create the project

            final String projname = name;

            Tasks.taskAskToSaveIfChanged(new Runnable() {

                @Override
                public void run() {
                    // OK name
                    closeDialog();

                    Alerts.loading(true);
                    Projects.openNewProject(projname);
                    Projects.getActive().setCurrentMcVersion(assetVersion.getSelectedItem().toString());
                    Config.LIBRARY_VERSION = assetVersion.getSelectedItem().toString();

                    Tasks.taskStoreProjectChanges();

                    //Projects.getActive().revert();

                    Projects.markProjectAsRecent(Projects.getActive().getName());

                    if (usePackFile) {
                        Tasks.taskPopulateProjectFromPack(file, new Runnable() {

                            @Override
                            public void run() {
                                //Projects.getActive().revert();

                                Tasks.taskOnProjectChanged();
                                Alerts.loading(false);
                            }
                        });
                    } else {
                        Tasks.taskOnProjectChanged();
                        Alerts.loading(false);
                    }

                }
            });

        }
    };

}
