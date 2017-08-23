package net.mightypork.rpw.gui.windows.dialogs;

import net.mightypork.rpw.App;
import net.mightypork.rpw.gui.Gui;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.widgets.HBox;
import net.mightypork.rpw.gui.widgets.VBox;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.struct.LangEntry;
import net.mightypork.rpw.tasks.Tasks;
import net.mightypork.rpw.utils.files.OsUtils;
import net.mightypork.rpw.utils.logging.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.List;

public class DialogNewLanguage extends RpwDialog {

    private JTextField nameField;
    private JTextField regionField;
    private JTextField codeField;
    private JLabel bidirectionalLabel;
    private JCheckBox bidirectionalCheckbox;
    private JButton buttonOK;
    private JButton buttonCancel;

    public DialogNewLanguage() {
        super(App.getFrame(), "Create New Language");

        createDialog();
    }


    @Override
    protected JComponent buildGui() {
        VBox vbox;
        vbox = new VBox();

        vbox.windowPadding();

        vbox.heading("New Language");

        vbox.gap();

        vbox.add(nameField = Gui.textField("", "Language name", "Name of the language"));
        vbox.add(regionField = Gui.textField("", "Language region", "Region of the language"));
        vbox.add(codeField = Gui.textField("", "Language code", "Code of the language"));

        HBox hbox = new HBox();
        hbox.add(bidirectionalLabel = Gui.label("Bidirectional"));
        hbox.add(bidirectionalCheckbox = Gui.checkbox(false));
        vbox.add(hbox);

        vbox.gapl();

        vbox.add(buttonOK = new JButton("OK", Icons.MENU_YES));
        vbox.add(buttonCancel = new JButton("Cancel", Icons.MENU_CANCEL));
        vbox.buttonRow(Gui.CENTER, buttonOK, buttonCancel);

        return vbox;
    }


    @Override
    public void onClose() {
        Tasks.taskOnProjectPropertiesChanged();
    }


    @Override
    protected void addActions() {
        setEnterButton(buttonOK);

        buttonOK.addActionListener(okListener);
        buttonCancel.addActionListener(cancelListener);
    }


    private final ActionListener okListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!nameField.getText().equals("") && !codeField.getText().equals("") && !regionField.getText().equals("")) {
                LangEntry language = new LangEntry(nameField.getText(), regionField.getText(), codeField.getText(), bidirectionalCheckbox.isSelected());
                Projects.getActive().addToCustomLanguages(language);
                File file = new File(Projects.getActive().getProjectDirectory().getPath() + "/custom_languages/" + language.code + ".lang");

                try {
                    file.createNewFile();
                    List<String> lines = Files.readAllLines(Paths.get(OsUtils.getAppDir().getPath() + "/library/vanilla/assets/minecraft/lang/en_us.lang"));
                    lines.set(1, "language.name=" + language.name);
                    lines.set(2, "language.region=" + language.region);
                    lines.set(3, "language.code=" + language.code);
                    Files.write(file.toPath(), lines, StandardCharsets.UTF_8);
                    Projects.getActive().saveConfigFiles();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
            closeDialog();
        }
    };


    private final ActionListener cancelListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
               closeDialog();
        }
    };

}
