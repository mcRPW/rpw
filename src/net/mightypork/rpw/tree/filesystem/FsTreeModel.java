package net.mightypork.rpw.tree.filesystem;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;


public class FsTreeModel extends DefaultTreeModel {

    public FsTreeModel(DirectoryFsTreeNode rootDirectory) {
        super(null);

        setRoot(rootDirectory);
    }


    @Override
    public void setRoot(TreeNode root) {
        if (root != null) {
            if (!(root instanceof DirectoryFsTreeNode)) {
                throw new IllegalArgumentException("Invalid type of fstree root node.");
            }

            ((DirectoryFsTreeNode) root).sort();
        }
        super.setRoot(root);
    }

}
