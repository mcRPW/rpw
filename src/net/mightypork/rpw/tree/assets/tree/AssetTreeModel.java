package net.mightypork.rpw.tree.assets.tree;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.treetable.AbstractTreeTableModel;


public class AssetTreeModel extends AbstractTreeTableModel {

    public AssetTreeModel(AssetTreeNode root) {
        super(root);
    }


    @Override
    public int getColumnCount() {
        return 5;
    }


    @Override
    public Object getValueAt(Object node, int column) {
        if (node == null) return null;

        if (node instanceof AssetTreeNode) {
            final AssetTreeNode atn = (AssetTreeNode) node;

            switch (column) {
                case 0:
                    return atn.getLabel();
                case 1:
                    return new SourceName(atn.getLibrarySource());
                case 2:
                    return new SourceName(atn.resolveAssetSource());
                case 3:
                    if (atn instanceof AssetTreeLeaf) {
                        return ((AssetTreeLeaf) atn).isAssetProvidedByProject();
                    } else {
                        return null;
                    }
                case 4:
                    if (atn.canHaveMeta()) {
                        return ((AssetTreeLeaf) atn).isMetaProvidedByProject();
                    } else {
                        return null;
                    }
            }
        }

        return null;
    }


    @Override
    public Object getChild(Object parent, int index) {
        if (parent == null) return null;

        if (parent instanceof AssetTreeNode) {
            final AssetTreeNode atn = (AssetTreeNode) parent;

            return atn.getChildAt(index);
        }

        return null;
    }


    @Override
    public int getChildCount(Object parent) {
        if (parent == null) return 0;

        if (parent instanceof AssetTreeNode) {
            final AssetTreeNode atn = (AssetTreeNode) parent;

            return atn.getChildCount();
        }

        return 0;
    }


    @Override
    public int getIndexOfChild(Object parent, Object child) {
        if (parent == null || child == null) return -1;

        if (parent instanceof AssetTreeNode && child instanceof AssetTreeNode) {
            final AssetTreeNode atn = (AssetTreeNode) parent;
            final AssetTreeNode childAtn = (AssetTreeNode) child;

            return atn.getIndex(childAtn);
        }

        return -1;
    }


    @Override
    public Class<?> getColumnClass(int column) {
        switch (column) {
            case 0:
                return String.class;
            case 1:
                return SourceName.class;
            case 2:
                return SourceName.class;
            case 3:
                return Boolean.class;
            case 4:
                return Boolean.class;
            default:
                return super.getColumnClass(column);
        }
    }


    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Resource File";
            case 1:
                return "Assigned";
            case 2:
                return "Resolved";
            case 3:
                return "PROJ";
            case 4:
                return "META";
            default:
                return super.getColumnName(column);
        }
    }


    public void setRoot(AssetTreeNode root) {
        this.root = root;

        modelSupport.fireNewRoot();
    }


    @Override
    public AssetTreeNode getRoot() {
        return (AssetTreeNode) this.root;
    }


    public void notifyNodeChanged(AssetTreeNode node) {
        modelSupport.firePathChanged(new TreePath(getPathToRoot(node)));
    }


    public TreeNode[] getPathToRoot(TreeNode aNode) {
        return getPathToRoot(aNode, 0);
    }


    protected TreeNode[] getPathToRoot(TreeNode aNode, int depth) {
        TreeNode[] retNodes;

        if (aNode == null) {
            if (depth == 0) return null;
            else retNodes = new TreeNode[depth];
        } else {
            depth++;
            if (aNode == root) retNodes = new TreeNode[depth];
            else retNodes = getPathToRoot(aNode.getParent(), depth);
            retNodes[retNodes.length - depth] = aNode;
        }
        return retNodes;
    }
}
