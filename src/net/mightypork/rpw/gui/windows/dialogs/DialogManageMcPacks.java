package net.mightypork.rpw.gui.windows.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.mightypork.rpw.App;
import net.mightypork.rpw.gui.Gui;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.helpers.KeyPressListener;
import net.mightypork.rpw.gui.widgets.ManagerLayout;
import net.mightypork.rpw.gui.widgets.SimpleStringList;
import net.mightypork.rpw.gui.widgets.VBox;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.files.OsUtils;


public class DialogManageMcPacks extends RpwDialog {

    private List<String> mcPacks;

    private SimpleStringList list;

    private JButton buttonClose;
    private JButton buttonDelete;


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


    private void reloadOptions() {
        list.setItems(mcPacks = getOptions());
    }


    public DialogManageMcPacks() {
        super(App.getFrame(), "Manage packs in MC");

        mcPacks = getOptions();

        createDialog();
    }


    @Override
    protected JComponent buildGui() {
        final VBox vbox = new VBox();

        vbox.windowPadding();
        vbox.heading("Manage MC resource packs");

        vbox.titsep("Installed Packs");
        vbox.gap();

        list = new SimpleStringList(mcPacks, true);
        list.setMultiSelect(true);
        list.getList().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                final int[] selected = list.getSelectedIndices();

                buttonDelete.setEnabled(selected != null);
            }
        });

        // buttons
        buttonDelete = Gui.sidebarButton("Delete", "Delete pack from library", Icons.MENU_DELETE);
        buttonDelete.setEnabled(false);

        buttonClose = Gui.sidebarButton("Close", "Close dialog", Icons.MENU_EXIT);

        final ManagerLayout ml = new ManagerLayout();
        ml.setMainComponent(list);
        ml.setTopButtons(buttonDelete);
        ml.setBottomButtons(buttonClose);
        ml.build();
        vbox.add(ml);

        return vbox;

    }


    @Override
    protected void addActions() {
        list.addKeyListener(new KeyPressListener() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    deleteListener.actionPerformed(null);
                }
            }
        });

        setEnterButton(buttonClose);
        buttonClose.addActionListener(closeListener);
        buttonDelete.addActionListener(deleteListener);
    }

    private final ActionListener deleteListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            final List<String> choice = list.getSelectedValues();

            if (choice == null) {
                return;
            }

            // OK name

            final String trailS = (choice.size() > 1 ? "s" : "");

            //@formatter:off
            final boolean yes = Alerts.askYesNo(
                    DialogManageMcPacks.this,
                    "Deleting Installed Pack" + trailS,
                    "Do you really want to delete the selected\n" +
                            "resource pack" + trailS + " from your Minecraft folder?"
            );
            //@formatter:on

            if (!yes) return;

            for (final String s : choice) {
                final File f = new File(OsUtils.getMcDir("resourcepacks"), s + ".zip");
                f.delete();
            }

            reloadOptions();
        }
    };
}
