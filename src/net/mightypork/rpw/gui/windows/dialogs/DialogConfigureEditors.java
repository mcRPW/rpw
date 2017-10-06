package net.mightypork.rpw.gui.windows.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Config;
import net.mightypork.rpw.gui.Gui;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.widgets.HBox;
import net.mightypork.rpw.gui.widgets.VBox;
import net.mightypork.rpw.gui.windows.RpwDialog;


public class DialogConfigureEditors extends RpwDialog {

    private JFileChooser fc;

    private JButton btnOK;
    private JButton btnCancel;
    private JButton btnDefaults;

    private JCheckBox ckI;
    private HBox boxI;
    private JLabel labelExI;
    private JLabel labelArI;
    private JTextField fieldCommandI;
    private JButton btnBrowseI;
    private JTextField fieldArgsI;

    private JCheckBox ckT;
    private HBox boxT;
    private JLabel labelExT;
    private JLabel labelArT;
    private JTextField fieldCommandT;
    private JButton btnBrowseT;
    private JTextField fieldArgsT;

    private JCheckBox ckA;
    private HBox boxA;
    private JLabel labelExA;
    private JLabel labelArA;
    private JTextField fieldCommandA;
    private JButton btnBrowseA;
    private JTextField fieldArgsA;

    private JCheckBox ckM;
    private HBox boxM;
    private JLabel labelExM;
    private JLabel labelArM;
    private JTextField fieldCommandM;
    private JButton btnBrowseM;
    private JTextField fieldArgsM;

    private JCheckBox ckInternalMeta;
    private JCheckBox ckInternalText;


    public DialogConfigureEditors() {
        super(App.getFrame(), "Configure Editors");

        createDialog();
    }


    @Override
    protected JComponent buildGui() {
        HBox hb;
        VBox vb;
        VBox section_vb;
        final VBox vbox = new VBox();
        vbox.windowPadding();

        final int fieldW = 300;
        final int argsW = 100;

        //@formatter:off

        final String exe_placeholder = "Path to editor binary";
        final String exe_tooltip = "Path to executable file (exe, sh), or terminal command.";
        final String args_placeholder = "CLI arguments";
        final String args_tooltip = "%s = path to file";

        vbox.heading("Configure Editors");

        vbox.titsep("Custom External Editors");

        vbox.gapl();

        section_vb = new VBox();
        section_vb.padding(0, Gui.GAPL, 0, Gui.GAPL);

        hb = new HBox();
        hb.add(ckI = new JCheckBox("Custom Image Editor"));
        ckI.setForeground(Gui.SUBHEADING_COLOR);
        hb.glue();
        section_vb.add(hb);
        section_vb.gap();

        boxI = new HBox();
        boxI.padding(0, Gui.GAPL, 0, 0);

        boxI.add(labelExI = new JLabel("Exec. file:"));
        labelExI.setHorizontalAlignment(SwingConstants.RIGHT);
        boxI.gap();

        boxI.add(fieldCommandI = Gui.textField("", exe_placeholder, exe_tooltip));
        Gui.setPrefWidth(fieldCommandI, fieldW);
        boxI.gap();
        boxI.add(btnBrowseI = new JButton(Icons.MENU_OPEN));
        btnBrowseI.setToolTipText("Browse files");
        boxI.gapl();

        boxI.add(labelArI = new JLabel("Args:"));
        labelArI.setHorizontalAlignment(SwingConstants.RIGHT);
        boxI.gap();
        boxI.add(fieldArgsI = Gui.textField("", args_placeholder, args_tooltip));
        Gui.setPrefWidth(fieldArgsI, argsW);

        section_vb.add(boxI);

        section_vb.gapl();
        section_vb.gapl();

        hb = new HBox();
        hb.add(ckT = new JCheckBox("Custom Text Editor"));
        ckT.setForeground(Gui.SUBHEADING_COLOR);
        hb.glue();
        section_vb.add(hb);
        section_vb.gap();

        boxT = new HBox();
        boxT.padding(0, Gui.GAPL, 0, 0);
        boxT.add(labelExT = new JLabel("Exec. file:"));
        labelExT.setHorizontalAlignment(SwingConstants.RIGHT);
        boxT.gap();

        boxT.add(fieldCommandT = Gui.textField("", exe_placeholder, exe_tooltip));
        Gui.setPrefWidth(fieldCommandT, fieldW);
        boxT.gap();
        boxT.add(btnBrowseT = new JButton(Icons.MENU_OPEN));
        btnBrowseT.setToolTipText("Browse files");
        boxT.gapl();

        boxT.add(labelArT = new JLabel("Args:"));
        labelArT.setHorizontalAlignment(SwingConstants.RIGHT);
        boxT.gap();
        boxT.add(fieldArgsT = Gui.textField("", args_placeholder, args_tooltip));
        Gui.setPrefWidth(fieldArgsT, argsW);

        section_vb.add(boxT);

        section_vb.gapl();
        section_vb.gapl();

        hb = new HBox();
        hb.add(ckA = new JCheckBox("Custom Audio Editor"));
        ckA.setForeground(Gui.SUBHEADING_COLOR);
        hb.glue();
        section_vb.add(hb);
        section_vb.gap();

        boxA = new HBox();
        boxA.padding(0, Gui.GAPL, 0, 0);
        boxA.add(labelExA = new JLabel("Exec. file:"));
        labelExA.setHorizontalAlignment(SwingConstants.RIGHT);
        boxA.gap();

        boxA.add(fieldCommandA = Gui.textField("", exe_placeholder, exe_tooltip));
        Gui.setPrefWidth(fieldCommandA, fieldW);
        boxA.gap();
        boxA.add(btnBrowseA = new JButton(Icons.MENU_OPEN));
        btnBrowseA.setToolTipText("Browse files");
        boxA.gapl();

        boxA.add(labelArA = new JLabel("Args:"));
        labelArA.setHorizontalAlignment(SwingConstants.RIGHT);
        boxA.gap();
        boxA.add(fieldArgsA = Gui.textField("", args_placeholder, args_tooltip));
        Gui.setPrefWidth(fieldArgsA, argsW);

        section_vb.add(boxA);

        section_vb.gapl();
        section_vb.gapl();

        hb = new HBox();
        hb.add(ckM = new JCheckBox("Custom Model Editor"));
        ckM.setForeground(Gui.SUBHEADING_COLOR);
        hb.glue();
        section_vb.add(hb);
        section_vb.gap();

        boxM = new HBox();
        boxM.padding(0, Gui.GAPL, 0, 0);
        boxM.add(labelExM = new JLabel("Exec. file:"));
        labelExM.setHorizontalAlignment(SwingConstants.RIGHT);
        boxM.gap();

        boxM.add(fieldCommandM = Gui.textField("", exe_placeholder, exe_tooltip));
        Gui.setPrefWidth(fieldCommandM, fieldW);
        boxM.gap();
        boxM.add(btnBrowseM = new JButton(Icons.MENU_OPEN));
        btnBrowseM.setToolTipText("Browse files");
        boxM.gapl();

        boxM.add(labelArM = new JLabel("Args:"));
        labelArM.setHorizontalAlignment(SwingConstants.RIGHT);
        boxM.gap();
        boxM.add(fieldArgsM = Gui.textField("", args_placeholder, args_tooltip));
        Gui.setPrefWidth(fieldArgsM, argsW);

        section_vb.add(boxM);

        section_vb.gapl();
        section_vb.gapl();

        vbox.add(section_vb);


        vbox.gapl();
        vbox.gapl();

        vbox.titsep("Built-in editors");
        vbox.gapl();

        hb = new HBox();
        hb.padding(0, Gui.GAPL, 0, Gui.GAPL);
        vb = new VBox();

        vb.add(ckInternalMeta = new JCheckBox("Use built-in McMeta editor"));
        vb.add(ckInternalText = new JCheckBox("Use built-in Text editor"));

        hb.add(vb);
        hb.glue();
        vbox.add(hb);

        vbox.gapl();

        btnDefaults = new JButton("Defaults", Icons.MENU_DELETE);
        btnOK = new JButton("OK", Icons.MENU_YES);
        btnCancel = new JButton("Cancel", Icons.MENU_CANCEL);
        vbox.buttonRow(Gui.RIGHT, btnDefaults, null, btnOK, btnCancel);

        //@formatter:on

        return vbox;

    }


    @Override
    protected void initGui() {
        initFileChooser();

        initFields();
    }


    @Override
    protected void addActions() {
        setEnterButton(btnOK);

        btnOK.addActionListener(saveListener);
        btnCancel.addActionListener(closeListener);

        ckA.addActionListener(ckListener);
        ckI.addActionListener(ckListener);
        ckT.addActionListener(ckListener);
        ckM.addActionListener(ckListener);

        ckInternalMeta.addActionListener(ckListener);
        ckInternalText.addActionListener(ckListener);

        btnBrowseA.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                fc.setCurrentDirectory(new File(fieldCommandA.getText()));
                final int res = fc.showDialog(self(), "Select");

                if (res == JFileChooser.APPROVE_OPTION) {
                    fieldCommandA.setText(fc.getSelectedFile().getPath());
                }
            }
        });

        btnBrowseI.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                fc.setCurrentDirectory(new File(fieldCommandI.getText()));
                final int res = fc.showDialog(self(), "Select");

                if (res == JFileChooser.APPROVE_OPTION) {
                    fieldCommandI.setText(fc.getSelectedFile().getPath());
                }
            }
        });

        btnBrowseT.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                fc.setCurrentDirectory(new File(fieldCommandT.getText()));
                final int res = fc.showDialog(self(), "Select");

                if (res == JFileChooser.APPROVE_OPTION) {
                    fieldCommandT.setText(fc.getSelectedFile().getPath());
                }
            }
        });

        btnBrowseM.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                fc.setCurrentDirectory(new File(fieldCommandM.getText()));
                final int res = fc.showDialog(self(), "Select");

                if (res == JFileChooser.APPROVE_OPTION) {
                    fieldCommandM.setText(fc.getSelectedFile().getPath());
                }
            }
        });

        btnDefaults.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                initFieldsDef();
            }
        });
    }


    private void initFields() {
        ckInternalMeta.setSelected(Config.USE_INTERNAL_META_EDITOR);
        ckInternalText.setSelected(Config.USE_INTERNAL_TEXT_EDITOR);

        ckI.setSelected(Config.USE_IMAGE_EDITOR);
        ckT.setSelected(Config.USE_TEXT_EDITOR);
        ckA.setSelected(Config.USE_AUDIO_EDITOR);
        ckM.setSelected(Config.USE_MODEL_EDITOR);

        fieldCommandA.setText(Config.AUDIO_EDITOR);
        fieldArgsA.setText(Config.AUDIO_EDITOR_ARGS);

        fieldCommandI.setText(Config.IMAGE_EDITOR);
        fieldArgsI.setText(Config.IMAGE_EDITOR_ARGS);

        fieldCommandT.setText(Config.TEXT_EDITOR);
        fieldArgsT.setText(Config.TEXT_EDITOR_ARGS);

        fieldCommandM.setText(Config.MODEL_EDITOR);
        fieldArgsM.setText(Config.MODEL_EDITOR_ARGS);

        ckListener.actionPerformed(null);
    }


    private void initFieldsDef() {
        ckInternalMeta.setSelected(Config.def_USE_INTERNAL_META_EDITOR);
        ckInternalText.setSelected(Config.def_USE_INTERNAL_TEXT_EDITOR);

        ckI.setSelected(Config.def_USE_IMAGE_EDITOR);
        ckT.setSelected(Config.def_USE_TEXT_EDITOR);
        ckA.setSelected(Config.def_USE_AUDIO_EDITOR);
        ckM.setSelected(Config.def_USE_MODEL_EDITOR);

        fieldCommandA.setText(Config.def_AUDIO_EDITOR);
        fieldArgsA.setText(Config.def_AUDIO_EDITOR_ARGS);

        fieldCommandI.setText(Config.def_IMAGE_EDITOR);
        fieldArgsI.setText(Config.def_IMAGE_EDITOR_ARGS);

        fieldCommandT.setText(Config.def_TEXT_EDITOR);
        fieldArgsT.setText(Config.def_TEXT_EDITOR_ARGS);

        fieldCommandM.setText(Config.def_MODEL_EDITOR);
        fieldArgsM.setText(Config.def_MODEL_EDITOR_ARGS);

        ckListener.actionPerformed(null);
    }


    private void initFileChooser() {
        fc = new JFileChooser();
        fc.setAcceptAllFileFilterUsed(false);
        fc.setDialogTitle("Select executable");
        fc.setFileFilter(new FileFilter() {

            @Override
            public String getDescription() {
                return "All files";
            }


            @Override
            public boolean accept(File f) {
                return true;
            }
        });

        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(false);
        fc.setFileHidingEnabled(!Config.SHOW_HIDDEN_FILES);
    }

    private final ActionListener ckListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            final boolean internalText = ckInternalText.isSelected();
            final boolean internalMeta = ckInternalMeta.isSelected();

            final boolean cmdI = ckI.isSelected();
            final boolean cmdT = ckT.isSelected() && (!internalMeta || !internalText);
            final boolean cmdA = ckA.isSelected();
            final boolean cmdM = ckM.isSelected();

            ckT.setEnabled(!internalMeta || !internalText);

            boxI.setEnabled(cmdI);
            boxT.setEnabled(cmdT);
            boxA.setEnabled(cmdA);
            boxA.setEnabled(cmdM);

            fieldCommandI.setEnabled(cmdI);
            fieldCommandT.setEnabled(cmdT);
            fieldCommandA.setEnabled(cmdA);
            fieldCommandM.setEnabled(cmdM);

            fieldArgsI.setEnabled(cmdI);
            fieldArgsT.setEnabled(cmdT);
            fieldArgsA.setEnabled(cmdA);
            fieldArgsM.setEnabled(cmdM);

            btnBrowseI.setEnabled(cmdI);
            btnBrowseT.setEnabled(cmdT);
            btnBrowseA.setEnabled(cmdA);
            btnBrowseM.setEnabled(cmdM);

            labelExI.setEnabled(cmdI);
            labelExT.setEnabled(cmdT);
            labelExA.setEnabled(cmdA);
            labelExM.setEnabled(cmdM);

            labelArI.setEnabled(cmdI);
            labelArT.setEnabled(cmdT);
            labelArA.setEnabled(cmdA);
            labelArM.setEnabled(cmdM);
        }
    };

    private final ActionListener saveListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            Config.USE_AUDIO_EDITOR = ckA.isSelected();
            Config.AUDIO_EDITOR = fieldCommandA.getText();
            Config.AUDIO_EDITOR_ARGS = fieldArgsA.getText();

            Config.USE_TEXT_EDITOR = ckT.isSelected();
            Config.TEXT_EDITOR = fieldCommandT.getText();
            Config.TEXT_EDITOR_ARGS = fieldArgsT.getText();

            Config.USE_IMAGE_EDITOR = ckI.isSelected();
            Config.IMAGE_EDITOR = fieldCommandI.getText();
            Config.IMAGE_EDITOR_ARGS = fieldArgsI.getText();

            Config.USE_MODEL_EDITOR = ckM.isSelected();
            Config.MODEL_EDITOR = fieldCommandM.getText();
            Config.MODEL_EDITOR_ARGS = fieldArgsM.getText();

            Config.USE_INTERNAL_META_EDITOR = ckInternalMeta.isSelected();
            Config.USE_INTERNAL_TEXT_EDITOR = ckInternalText.isSelected();

            Config.save();

            closeDialog();
        }
    };

}
