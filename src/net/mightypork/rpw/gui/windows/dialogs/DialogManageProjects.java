package net.mightypork.rpw.gui.windows.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
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
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.tasks.Tasks;
import net.mightypork.rpw.utils.Utils;
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.files.OsUtils;


public class DialogManageProjects extends RpwDialog {

    private List<String> projectNames;

    private SimpleStringList list;

    private JButton buttonClose;
    private JButton buttonDelete;
    private JButton buttonRename;
    private JButton buttonOpen;


    private void reloadOptions() {
        list.setItems(projectNames = Projects.getProjectNames());
    }


    public DialogManageProjects() {
        super(App.getFrame(), "Manage Projects");

        projectNames = Projects.getProjectNames();

        createDialog();
    }


    @Override
    protected JComponent buildGui() {
        final VBox vbox = new VBox();

        vbox.windowPadding();
        vbox.heading("My Projects");

        vbox.titsep("RPW projects");
        vbox.gap();

        list = new SimpleStringList(projectNames, true);
        list.setMultiSelect(true);
        list.getList().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                final int[] selected = list.getSelectedIndices();

                buttonDelete.setEnabled(selected != null);
                buttonRename.setEnabled(selected != null && selected.length == 1);
                buttonOpen.setEnabled(selected != null && selected.length == 1);
            }
        });

        // buttons
        buttonDelete = Gui.sidebarButton("Delete", "Delete resource pack", Icons.MENU_DELETE);
        buttonDelete.setEnabled(false);

        buttonRename = Gui.sidebarButton("Rename", "Rename project folder", Icons.MENU_RENAME);
        buttonRename.setEnabled(false);

        buttonOpen = Gui.sidebarButton("Open", "Open in RPW", Icons.MENU_OPEN);
        buttonOpen.setEnabled(false);

        buttonClose = Gui.sidebarButton("Close", "Close dialog", Icons.MENU_EXIT);

        final ManagerLayout ml = new ManagerLayout();
        ml.setMainComponent(list);
        ml.setTopButtons(buttonDelete, buttonRename);
        ml.setBottomButtons(buttonOpen, buttonClose);
        ml.build();
        vbox.add(ml);

        return vbox;
    }


    @Override
    public void onClose() {
        Tasks.taskRedrawRecentProjectsMenu();
    }


    @Override
    protected void addActions() {
        buttonRename.addActionListener(renameListener);
        buttonDelete.addActionListener(deleteListener);
        buttonClose.addActionListener(closeListener);
        buttonOpen.addActionListener(openListener);
    }

    private final ActionListener renameListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            final String projname = list.getSelectedValue();

            if (projname == null) {
                return;
            }

            if (Projects.isOpen() && Projects.getActive().getName().equals(projname)) {
                final boolean agree = Alerts.askOkCancel(self(), "Project is open", "RPW can't RENAME an open project.\n\nDo you want to close it?");

                if (!agree) return;

                Tasks.taskCloseProject();
            }

            // OK name

            //@formatter:off
            final String newName = Alerts.askForInput(
                    self(),
                    "Rename Project",
                    "Please, enter a new name for\n" +
                            "the project \"" + projname + "\".\n" +
                            "\n" +
                            "Do not use /, *, ?, =.",
                    projname
            );
            //@formatter:on

            if (newName == null) return;
            newName.trim();
            if (!newName.matches("^[a-zA-Z0-9_-]+$")) {
                Alerts.error(self(), "\"" + newName + "\" is not a valid name.");
                return;
            }

            if (projname.equals(newName)) return;

            if (projectNames.contains(newName)) {
                Alerts.error(self(), "Name \"" + newName + "\" is already used.");
                return;
            }

            final File oldDir = new File(OsUtils.getAppDir(Paths.DIR_PROJECTS), projname);
            final File newDir = new File(OsUtils.getAppDir(Paths.DIR_PROJECTS), newName);

            if (!oldDir.renameTo(newDir)) {
                Alerts.error(self(), "Failed to move the project directory.");
                FileUtils.delete(newDir, true); // cleanup
            }

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

            //@formatter:off
            final boolean yes = Alerts.askYesNo(
                    self(),
                    "Deleting Project",
                    "Do you really want to delete\nthe selected project" + (choice.size() > 1 ? "s" : "") + "?"
            );
            //@formatter:on

            if (!yes) return;

            if (Projects.isOpen()) {
                final String openProjectDirname = Projects.getActive().getName();

                boolean isOpen = false;
                for (final String s : choice) {
                    if (s.equals(openProjectDirname)) isOpen = true;
                }

                if (isOpen) {
                    final boolean agree = Alerts.askOkCancel(self(), "Project is open", "RPW can't DELETE an open project.\n\nDo you want to close it?");

                    if (!agree) return;

                    Projects.closeProject();
                    Tasks.taskOnProjectChanged();
                }
            }

            final List<Integer> tasks = new ArrayList<Integer>();
            for (final String s : choice) {
                tasks.add(Tasks.taskDeleteProject(s));
            }

            while (true) {
                int live = 0;
                for (final int task : tasks) {
                    if (Tasks.isRunning(task)) live++;
                }
                if (live == 0) break;
            }

            reloadOptions();
        }
    };

    private final ActionListener openListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            final String choice = list.getSelectedValue();

            if (choice == null) {
                Alerts.error(self(), "Nothing selected", "Select a project!");
                return;
            }

            // OK name
            Tasks.taskAskToSaveIfChanged(new Runnable() {

                @Override
                public void run() {
                    Projects.openProject(choice);
                    Tasks.taskOnProjectChanged();
                    closeDialog();
                }
            });
        }
    };
}
