package net.mightypork.rpw.gui.widgets;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.tree.TreeSelectionModel;

import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.helpers.trees.SoundFileTreeClickListener;
import net.mightypork.rpw.gui.windows.dialogs.DialogSoundWizard;
import net.mightypork.rpw.tree.TreeIconProvider;
import net.mightypork.rpw.tree.filesystem.DirectoryFsTreeNode;
import net.mightypork.rpw.tree.filesystem.FsTreeModel;

import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.renderer.DefaultTreeRenderer;


public class SoundFileTreeDisplay {

    public JScrollPane scrollPane;
    public JXTree tree;
    public FsTreeModel model;


    public SoundFileTreeDisplay(DirectoryFsTreeNode root, DialogSoundWizard wizard) {
        model = new FsTreeModel(root);

        tree = new JXTree(model);
        tree.setRowHeight(20);
        tree.setShowsRootHandles(true);
        tree.setRootVisible(false);

        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

        tree.setCellRenderer(new DefaultTreeRenderer(new TreeIconProvider()));

        tree.addMouseListener(new SoundFileTreeClickListener(tree, wizard));

        tree.setCollapsedIcon(Icons.TREE_OPEN);
        tree.setExpandedIcon(Icons.TREE_CLOSE);

        scrollPane = new JScrollPane(tree);
    }


    public JComponent getComponent() {
        return scrollPane;
    }


    public void setRoot(DirectoryFsTreeNode root) {
        model.setRoot(root);
    }

}
