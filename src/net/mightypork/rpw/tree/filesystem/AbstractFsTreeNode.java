package net.mightypork.rpw.tree.filesystem;

import java.io.File;
import java.util.Enumeration;

import javax.swing.tree.TreeNode;

import net.mightypork.rpw.tree.IFileTreeNode;
import net.mightypork.rpw.utils.AlphanumComparator;


/**
 * Abstract filesystem tree node
 *
 * @author Ondřej Hruška (MightyPork)
 */
public abstract class AbstractFsTreeNode implements TreeNode, Comparable<AbstractFsTreeNode>, IFileTreeNode {

    /**
     * Reference to a parent node
     */
    protected DirectoryFsTreeNode parent;

    /**
     * Mark used to identify different file trees
     */
    protected int mark;

    /**
     * Path (may or may not be used
     */
    protected File path;


    @Override
    public abstract Enumeration children();


    @Override
    public boolean getAllowsChildren() {
        return !isLeaf();
    }


    @Override
    public abstract AbstractFsTreeNode getChildAt(int childIndex);


    @Override
    public abstract int getChildCount();


    @Override
    public abstract int getIndex(TreeNode node);


    @Override
    public DirectoryFsTreeNode getParent() {
        return parent;
    }


    @Override
    public boolean isLeaf() {
        return !isDirectory() || getChildCount() == 0;
    }


    /**
     * Get name for display
     *
     * @return name
     */
    public abstract String getName();


    @Override
    public int compareTo(AbstractFsTreeNode o) {
        // dirs on top
        if (o.isDirectory() && !this.isDirectory()) return 1;
        if (!o.isDirectory() && this.isDirectory()) return -1;

        // sort by name
        return AlphanumComparator.instance.compare(this.getName(), o.getName());
    }


    /**
     * Sort children
     */
    public void sort() {
    }


    /**
     * Get represented path. Null for virtual directory nodes.
     *
     * @return the path
     */
    public abstract File getPath();


    @Override
    public String toString() {
        return getName();
    }


    /**
     * Set a mark to this and children
     *
     * @param newMark the new mark
     */
    public void setMark(int newMark) {
        this.mark = newMark;

        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setMark(newMark);
        }
    }


    /**
     * Get inherited or assigned mark
     *
     * @return mark
     */
    public int getMark() {
        return mark;
    }


    /**
     * Get path from the root path (relative)
     *
     * @return relative path from root
     */
    public File getPathRelativeToRoot() {
        if (isRoot() || getParent() == null) return new File("");

        final File root = getParent().getRoot();

        return new File(root.toURI().relativize(getPath().toURI()).getPath());
    }


    /**
     * @return if this is the root node
     */
    public boolean isRoot() {
        return false;
    }
}
