package net.mightypork.rpw.gui.windows.popups;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import net.mightypork.rpw.App;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.windows.dialogs.DialogSoundWizard;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.tasks.TaskImportCustomSoundReplacement;
import net.mightypork.rpw.tasks.TaskImportCustomSounds;
import net.mightypork.rpw.tree.filesystem.AbstractFsTreeNode;
import net.mightypork.rpw.tree.filesystem.DirectoryFsTreeNode;
import net.mightypork.rpw.tree.filesystem.FileFsTreeNode;
import net.mightypork.rpw.utils.files.DesktopApi;
import net.mightypork.rpw.utils.files.FileUtils;


public class PopupSoundFsTreeNode {

    public static PopupSoundFsTreeNode open(Container c, int x, int y, List<AbstractFsTreeNode> nodes, DialogSoundWizard wizard) {
        return new PopupSoundFsTreeNode(c, x, y, nodes, wizard);
    }

    private List<AbstractFsTreeNode> nodes = null;

    private JMenuItem itemImport;
    private JMenuItem itemDelete;
    private JMenuItem itemEdit;
    private JMenuItem itemNewDir;

    private DialogSoundWizard wizard;

    protected Runnable pathChangedRunnable = new Runnable() {

        @Override
        public void run() {
            wizard.pathChanged(node);
        }
    };

    protected Runnable pathRemovedRunnable = new Runnable() {

        @Override
        public void run() {
            wizard.nodeRemoved(node);
        }
    };

    private Container container;

    private AbstractFsTreeNode node;


    private void errorMessage(Container c, int x, int y, String msg) {
        final JPopupMenu popup = new JPopupMenu("Error");

        JComponent label;

        popup.add(label = new JLabel(msg));
        label.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        label.setForeground(Color.GRAY);

        popup.pack();
        popup.show(c, x, y);
    }


    public PopupSoundFsTreeNode(Container c, int x, int y, List<AbstractFsTreeNode> nodes, DialogSoundWizard wizard) {
        if (nodes == null || nodes.size() == 0) return;

        this.container = c;

        this.nodes = nodes;

        int cnt_mark1 = 0;
        int cnt_dir = 0;
        int cnt_file = 0;
        int cnt_root = 0;
        final int cnt = nodes.size();

        for (final AbstractFsTreeNode node : nodes) {
            if (node.getMark() == 1) cnt_mark1++;
            if (node.isFile()) cnt_file++;
            if (node.isDirectory()) cnt_dir++;
            if (node.isRoot()) cnt_root++;
        }

        if (cnt_root > 1) {
            errorMessage(c, x, y, "Invalid selection!");
            return;
        }

        if (cnt_mark1 > 0) {
            errorMessage(c, x, y, "Can't manipulate Vanilla files!");
            return;
        }

        node = nodes.get(0);

        this.wizard = wizard;

        JMenuItem item;
        JPopupMenu popup;

        popup = new JPopupMenu("Selected item");

        item = itemDelete = new JMenuItem("Delete selected");
        item.setIcon(Icons.MENU_DELETE_ASSET);
        popup.add(item);

        item = itemImport = new JMenuItem();
        item.setIcon(Icons.MENU_IMPORT_BOX);
        popup.add(item);

        if (cnt == 1 && cnt_file == 1) {
            item.setText("Import replacement");
        } else if (cnt == 1 && cnt_dir == 1) {
            item.setText("Import files to folder");
        } else {
            item.setVisible(false);
        }

        item = itemEdit = new JMenuItem();
        item.setIcon(Icons.MENU_EDIT);
        popup.add(item);

        if (cnt == 1 && cnt_file == 1) {
            item.setText("Open in editor");
        } else if (cnt == 1 && cnt_dir == 1) {
            item.setText("Open in navigator");
        } else {
            item.setVisible(false);
        }

        item = itemNewDir = new JMenuItem("New Folder");
        item.setIcon(Icons.MENU_NEW);
        item.setVisible(cnt == 1 && cnt_dir == 1);
        popup.add(item);

        if (cnt == 1 && cnt_dir == 1 && nodes.get(0).isRoot()) {
            itemDelete.setText("Delete all files");
        }

        addActions();

        popup.pack();
        popup.show(c, x, y);

    }


    private void addActions() {
        itemDelete.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //@formatter:off
                final boolean really = Alerts.askYesNo(
                        App.getFrame(),
                        "Delete File",
                        "Are you sure to  permanently\n" +
                                "delete the selected items?"
                );
                //@formatter:on

                if (!really) return;

                for (final AbstractFsTreeNode aNode : nodes) {
                    if (aNode.isFile()) {
                        FileUtils.delete(aNode.getPath(), false);
                    } else {
                        FileUtils.delete(aNode.getPath(), true);
                    }
                    wizard.nodeRemoved(aNode);
                }

            }
        });

        itemImport.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (node.isFile()) {
                    TaskImportCustomSoundReplacement.run((FileFsTreeNode) node, pathChangedRunnable);
                } else {
                    TaskImportCustomSounds.run((DirectoryFsTreeNode) node, pathChangedRunnable);
                }
            }
        });

        itemEdit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (node.isFile()) {
                    DesktopApi.editAudio(node.getPath());
                } else {
                    DesktopApi.open(node.getPath());
                }
            }
        });

        itemNewDir.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (node.isDirectory()) {
                    final String name = Alerts.askForInput(container, "New Folder", "Enter name for the new folder\ncreated in " + node.getPathRelativeToRoot(), "");

                    if (name == null || name.length() == 0) return;

                    final File newDir = new File(node.getPath(), name);

                    if (newDir.exists()) {
                        Alerts.warning(container, "The directory already exists.");
                    } else {
                        newDir.mkdirs();
                        pathChangedRunnable.run();
                    }
                }
            }
        });
    }
}
