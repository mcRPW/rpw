package net.mightypork.rpw.gui.windows.dialogs;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Config;
import net.mightypork.rpw.gui.Gui;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.helpers.ClickListener;
import net.mightypork.rpw.gui.widgets.SimpleStringList;
import net.mightypork.rpw.gui.widgets.VBox;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.library.Sources;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.struct.LangEntry;
import net.mightypork.rpw.tasks.Tasks;
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.files.OsUtils;
import net.mightypork.rpw.utils.logging.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DialogConfigureMinecraftAssets extends RpwDialog {

    private SimpleStringList installedAssets;
    private JButton selectAssets;
    private JButton newAssets;
    private JButton deleteAssets;
    private JButton cancel;

    public DialogConfigureMinecraftAssets() {
        super(App.getFrame(), "Select target Minecraft version");

        createDialog();
    }


    @Override
    protected JComponent buildGui() {
        final VBox vb = new VBox();
        vb.windowPadding();
        vb.heading("Select target Minecraft version");

        // box for the list of keys
        installedAssets = new SimpleStringList();
        installedAssets.setPreferredSize(new Dimension(300, 400));
        updateAssets();

        vb.glue();
        vb.add(installedAssets);

        // box with buttons under the list
        selectAssets = new JButton("Select", Icons.MENU_YES);
        newAssets = new JButton("New", Icons.MENU_NEW);
        deleteAssets = new JButton("Delete", Icons.MENU_DELETE);
        cancel = new JButton("Cancel", Icons.MENU_CANCEL);

        vb.gap();
        vb.buttonRow(Gui.CENTER, selectAssets, newAssets, deleteAssets, cancel);
        vb.gap();

        return vb;
    }


    private void updateAssets(){
        File file = new File(OsUtils.getAppDir().getPath() + "/library/vanilla/");
        String currentVersion = "";
        for (int i = 0; i < file.list().length; i++) {
            if (file.list()[i].equals("assets")){
                try {
                    FileUtils.delete(file.listFiles()[i], true);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            if (new File(file.listFiles()[i].getPath() + "/structure.dat").exists()) {
                installedAssets.addItem(file.list()[i]);

                if (file.list()[i].toString().equals(Config.LIBRARY_VERSION)) {
                    currentVersion = file.list()[i].toString();
                }
            }
        }
        installedAssets.list.setSelectedValue(currentVersion, true);
    }


    @Override
    protected void addActions() {
        setEnterButton(newAssets);

        selectAssets.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!Config.LIBRARY_VERSION.equals(installedAssets.getSelectedValue())){
                    Projects.getActive().setCurrentMcVersion(installedAssets.getSelectedValue());
                    Config.LIBRARY_VERSION = installedAssets.getSelectedValue();
                    Sources.initVanilla();
                    Tasks.taskLoadVanillaStructure();
                    Tasks.taskTreeRebuild();
                    App.getSidePanel().setMcVersion();
                }
                closeDialog();
            }
        });

        newAssets.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskExtractAssets();
                updateAssets();
                onAssetsSelection();
                closeDialog();
            }
        });

        deleteAssets.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                deleteAssets();
                onAssetsSelection();
            }
        });

        cancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                closeDialog();
            }
        });

        installedAssets.list.addMouseListener(new ClickListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                onAssetsSelection();
            }
        });

        installedAssets.list.addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    deleteAssets();
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


    protected void onAssetsSelection() {
        if (installedAssets.getSelectedValue() != null) {
            deleteAssets.setEnabled(true);
            selectAssets.setEnabled(true);
        }else{
            deleteAssets.setEnabled(false);
            selectAssets.setEnabled(false);
        }
    }


    protected void deleteAssets(){
        final String assets = installedAssets.getSelectedValue();

        if (assets == null) return;

        if (assets.equals(Projects.getActive().getCurrentMcVersion())){
            Alerts.error(self(), "You can't delete the asset directory that you are using.\nChange it or inport another one before trying again");
            return;
        }

        //@formatter:off
        final boolean y = Alerts.askYesNo(
                DialogConfigureMinecraftAssets.this,
                "Delete Entry",
                "Really want to to delete\n"
                        + "assets \"" + assets + "\"?"
        );
        //@formatter:on

        if (!y) return;

        try {
            FileUtils.delete(OsUtils.getAppDir(net.mightypork.rpw.Paths.DIR_VANILLA + "/" + Projects.getActive().getCurrentMcVersion()), true);
        } catch(Exception e) {
            e.printStackTrace();
        }
        installedAssets.removeItem(assets);
        installedAssets.sortAndUpdate();
        Projects.markChange();
    }

}
