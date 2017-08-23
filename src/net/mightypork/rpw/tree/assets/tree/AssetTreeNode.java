package net.mightypork.rpw.tree.assets.tree;

import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;

import net.mightypork.rpw.library.MagicSources;
import net.mightypork.rpw.library.Sources;
import net.mightypork.rpw.tree.IFileTreeNode;
import net.mightypork.rpw.utils.AlphanumComparator;


public abstract class AssetTreeNode implements Comparable<AssetTreeNode>, TreeNode, IFileTreeNode {

    protected String librarySource = MagicSources.INHERIT;
    protected String label = "#NoLabel#";
    protected AssetTreeNode parent = null;


    public AssetTreeNode(String label, String librarySource) {
        this.label = label;
        this.librarySource = librarySource;
    }


    /**
     * Set parent node (a group, or null)
     *
     * @param parent parent node
     */
    public void setParent(AssetTreeNode parent) {
        if (parent == null || parent.isLeaf()) throw new IllegalArgumentException("Invalid parent node " + parent);
        this.parent = parent;
    }


    /**
     * Get parent node
     *
     * @return parent node
     */
    @Override
    public AssetTreeNode getParent() {
        return parent;
    }


    /**
     * Get if node is a file and not directory
     *
     * @return is file = leaf
     */
    @Override
    public abstract boolean isLeaf();


    /**
     * Count children in this group
     *
     * @return children count
     */
    @Override
    public abstract int getChildCount();


    /**
     * Find index of child
     *
     * @param child child to search
     * @return child index
     */
    @Override
    public abstract int getIndex(TreeNode child);


    /**
     * Get child at index
     *
     * @param index child index
     * @return child at index
     */
    @Override
    public abstract AssetTreeNode getChildAt(int index);


    /**
     * Get name of source to take asset from when exporting
     *
     * @return asset source
     */
    public String getLibrarySource() {
        return librarySource;
    }


    /**
     * Set name of source to take asset from when exporting
     *
     * @param source source name
     */
    public void setLibrarySource(String source) {
        this.librarySource = source;
    }


    /**
     * Set name of source to take asset from when exporting
     *
     * @param source source name
     */
    public void setLibrarySourceIfNeeded(String source) {
        final String resolved = resolveAssetSource();
        if (resolved.equals(source)) return;

        setLibrarySource(source);
    }


    /**
     * Get displayed label
     *
     * @return label
     */
    public String getLabel() {
        return label;
    }


    // /**
    // * Set displayed label
    // *
    // * @param label label
    // */
    // public void setLabel(String label) {
    //
    // this.label = label;
    // }

    /**
     * Resolve library source, considering "INHERIT" magic source
     *
     * @return resolved source
     */
    public String resolveAssetSource() {
        String source = librarySource;

        if (!Sources.doesSourceExist(source)) {
            source = MagicSources.INHERIT;
        }

        if (MagicSources.isInherit(source)) {
            if (parent != null) {
                return parent.resolveAssetSource();
            } else {
                return MagicSources.VANILLA;
            }
        }

        return source;
    }


    /**
     * Resolve library source for meta
     *
     * @return resolved source
     */
    public String resolveAssetMetaSource() {
        return resolveAssetSource();
    }


    @Override
    public String toString() {
        return getLabel();
    }


    @Override
    public int compareTo(AssetTreeNode o) {
        int i = compareTo_2(o);

        if (i < 0) i = -1;
        if (i > 0) i = 1;

        return i;

    }


    private int compareTo_2(AssetTreeNode o) {
        if (this.isLeaf() && !o.isLeaf()) {
            return 1;
        }

        if (!this.isLeaf() && o.isLeaf()) {
            return -1;
        }

        return compareTo_3(o);
    }


    private int compareTo_3(AssetTreeNode o) {
        return AlphanumComparator.instance.compare(label.toLowerCase(), o.label.toLowerCase());
    }


    public abstract void prepareForDisplay();


    public abstract void processThisAndChildren(AssetTreeProcessor processor);


    @Override
    public boolean getAllowsChildren() {
        return !isLeaf();
    }


    @Override
    public abstract Enumeration children();


    public abstract List<AssetTreeNode> getChildrenList();


    public boolean canHaveMeta() {
        return isLeaf();
    }

}
