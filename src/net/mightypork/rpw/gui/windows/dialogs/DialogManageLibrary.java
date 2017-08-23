package net.mightypork.rpw.gui.windows.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Paths;
import net.mightypork.rpw.gui.Gui;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.widgets.ManagerLayout;
import net.mightypork.rpw.gui.widgets.SimpleStringList;
import net.mightypork.rpw.gui.widgets.VBox;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.library.Sources;
import net.mightypork.rpw.tasks.Tasks;
import net.mightypork.rpw.utils.Utils;
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.files.OsUtils;


public class DialogManageLibrary extends RpwDialog {

    private List<String> packNames;

    private SimpleStringList list;

    private JButton buttonClose;
    private JButton buttonDelete;
    private JButton buttonRename;
    private JButton buttonImport;

    private boolean changedAnything;


    private void reloadOptions() {
        list.setItems(packNames = Sources.getResourcepackNames());
    }


    public DialogManageLibrary() {
        super(App.getFrame(), "Manage Library");

        packNames = Sources.getResourcepackNames();

        createDialog();
    }


    @Override
    protected JComponent buildGui() {
        final VBox vbox = new VBox();

        vbox.windowPadding();

        vbox.heading("Manage Library");

        vbox.titsep("Library Resource Packs");
        vbox.gap();

        list = new SimpleStringList(packNames, true);
        list.setMultiSelect(true);
        list.getList().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                final int[] selected = list.getSelectedIndices();

                buttonDelete.setEnabled(selected != null);
                buttonRename.setEnabled(selected != null && selected.length == 1);
            }
        });

        // buttons
        buttonDelete = Gui.sidebarButton("Delete", "Delete resource pack", Icons.MENU_DELETE);
        buttonDelete.setEnabled(false);

        buttonRename = Gui.sidebarButton("Rename", "Rename resource pack", Icons.MENU_RENAME);
        buttonRename.setEnabled(false);

        buttonImport = Gui.sidebarButton("Import", "Import pack (zip)", Icons.MENU_IMPORT_BOX);

        buttonClose = Gui.sidebarButton("Close", "Close dialog", Icons.MENU_EXIT);

        final ManagerLayout ml = new ManagerLayout();
        ml.setMainComponent(list);
        ml.setTopButtons(buttonDelete, buttonRename, buttonImport);
        ml.setBottomButtons(buttonClose);
        ml.build();
        vbox.add(ml);

        return vbox;
    }


    @Override
    public void onClose() {
        if (changedAnything) Tasks.taskReloadSources(null);
    }


    @Override
    protected void addActions() {
        setEnterButton(buttonClose);
        buttonClose.addActionListener(closeListener);
        buttonDelete.addActionListener(deleteListener);
        buttonRename.addActionListener(renameListener);
        buttonImport.addActionListener(importListener);
    }

    private final ActionListener renameListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            final String oldName = list.getSelectedValue(); // just 1

            if (oldName == null) {
                return;
            }

            // OK name

            //@formatter:off
            final String newName = Alerts.askForInput(
                    DialogManageLibrary.this,
                    "Rename Library Source",
                    "Please, enter a new name for the\n" +
                            "resource pack \"" + oldName + "\".\n" +
                            "\n" +
                            "Do not use /, *, ?, =.",
                    oldName
            );
            //@formatter:on

            if (newName == null) return;
            newName.trim();
            if (!Utils.isValidFilenameString(newName)) {
                Alerts.error(DialogManageLibrary.this, "\"" + newName + "\" is not a valid name.");
                return;
            }

            if (oldName.equals(newName)) return;

            if (packNames.contains(newName)) {
                Alerts.error(DialogManageLibrary.this, "Name \"" + newName + "\" is already used.");
                return;
            }

            final File oldDir = new File(OsUtils.getAppDir(Paths.DIR_RESOURCEPACKS), oldName);
            final File newDir = new File(OsUtils.getAppDir(Paths.DIR_RESOURCEPACKS), newName);

            if (!oldDir.renameTo(newDir)) {
                Alerts.error(DialogManageLibrary.this, "Failed to move the pack.");
                FileUtils.delete(newDir, true); // cleanup
            }

            Tasks.taskTreeSourceRename(oldName, newName);

            changedAnything = true;
            reloadOptions();
        }
    };

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
                    DialogManageLibrary.this,
                    "Deleting Library Source" + trailS,
                    "Do you really want to delete the\n" +
                            "selected resource pack" + trailS + "?"
            );
            //@formatter:on

            if (!yes) return;

            for (final String s : choice) {
                Tasks.taskDeleteResourcepack(s);
            }

            changedAnything = true;
            reloadOptions();
        }
    };

    private final ActionListener importListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            final RpwDialog importDialog = new DialogImportPack();
            importDialog.addCloseHook(new Runnable() {

                @Override
                public void run() {
                    changedAnything = true;
                    reloadOptions();
                }
            });

            importDialog.setVisible(true);

        }
    };
}
