package net.mightypork.rpw.gui.windows.dialogs;

import net.mightypork.rpw.App;
import net.mightypork.rpw.gui.Gui;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.helpers.ClickListener;
import net.mightypork.rpw.gui.widgets.SimpleStringList;
import net.mightypork.rpw.gui.widgets.TreeDisplay;
import net.mightypork.rpw.gui.widgets.VBox;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.struct.LangEntry;
import net.mightypork.rpw.tasks.Tasks;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DialogManageLanguages extends RpwDialog {

    private SimpleStringList languages;
    private JButton newLanguage;
    private JButton editLanguage;
    private JButton deleteLanguage;

    public DialogManageLanguages() {
        super(App.getFrame(), "Manage Languages");

        createDialog();
    }


    @Override
    protected JComponent buildGui() {
        final VBox vb = new VBox();
        vb.windowPadding();
        vb.heading("Manage Languages");

        // box for the list of keys
        languages = new SimpleStringList();
        languages.setPreferredSize(new Dimension(300, 400));
        //languages.setBorder(BorderFactory.createEmptyBorder(15, 15,15,15));
        updateLanguages();

        vb.glue();
        vb.add(languages);

        // box with buttons under the list
        newLanguage = new JButton("New", Icons.MENU_NEW);
        editLanguage = new JButton("Edit", Icons.TREE_FILE_TEXT);
        editLanguage.setEnabled(false);
        deleteLanguage = new JButton("Delete", Icons.MENU_DELETE);
        deleteLanguage.setEnabled(false);

        vb.gap();
        vb.buttonRow(Gui.CENTER, newLanguage, editLanguage, deleteLanguage);
        vb.gap();

        return vb;
    }


    private void updateLanguages(){
        if(Projects.getActive().getCustomLanguages() != null){
            for(int i = 0; i < Projects.getActive().getCustomLanguages().size(); i++){
                languages.addItem(((LangEntry)Projects.getActive().getCustomLanguages().values().toArray()[i]).name);
            }
        }
    }


    @Override
    protected void addActions() {
        setEnterButton(newLanguage);

        newLanguage.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                languages.list.setSelectedIndices(new int[]{});
                Tasks.taskCreateLanguage();
                updateLanguages();
                onLanguageSelection();
            }
        });

        editLanguage.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Gui.open(new DialogEditText(new File(Projects.getActive().getProjectDirectory().getPath() + "/custom_languages/" + Projects.getActive().getCustomLanguages().get(languages.getSelectedValue()).code + ".lang")));
                } catch(IOException exception){
                    exception.printStackTrace();
                }
            }
        });

        deleteLanguage.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                deleteLanguage();
                onLanguageSelection();
            }
        });

        languages.list.addMouseListener(new ClickListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                onLanguageSelection();
            }
        });

        languages.list.addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    deleteLanguage();
                }
            }


            @Override
            public void keyReleased(KeyEvent e) {
            }


            @Override
            public void keyTyped(KeyEvent e) {
            }
        });
    }


    protected void onLanguageSelection() {
        if (languages.getSelectedValue() != null) {
            editLanguage.setEnabled(true);
            deleteLanguage.setEnabled(true);
        }else{
            editLanguage.setEnabled(false);
            deleteLanguage.setEnabled(false);
        }
    }


    protected void deleteLanguage(){
        final String language = languages.getSelectedValue();

        if (language == null) return;

        //@formatter:off
        final boolean y = Alerts.askYesNo(
                DialogManageLanguages.this,
                "Delete Entry",
                "Really want to to delete\n"
                        + "language \"" + language + "\"?"
        );
        //@formatter:on

        if (!y) return;

        try {
            if(Files.exists(Paths.get(Projects.getActive().getProjectDirectory().getPath() + "/custom_languages/" + Projects.getActive().getCustomLanguages().get(language).name + ".lang"))) {
                Files.delete(Paths.get(Projects.getActive().getProjectDirectory().getPath() + "/custom_languages/" + Projects.getActive().getCustomLanguages().get(language).name + ".lang"));
            }
        }catch(IOException exception){
            exception.printStackTrace();
        }
        Projects.getActive().getCustomLanguages().remove(language);
        languages.removeItem(language);
        languages.sortAndUpdate();
        Projects.markChange();
    }

}
