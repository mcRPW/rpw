package net.mightypork.rpw.tree.assets.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;


public class AssetTreeGroup extends AssetTreeNode {

    public AssetTreeGroup(String groupKey, String label, String librarySource) {
        super(label, librarySource);
        this.groupKey = groupKey;
    }

    public ArrayList<AssetTreeNode> children = new ArrayList<AssetTreeNode>();
    public String groupKey;


    /**
     * Get asset key
     *
     * @return asset key
     */
    public String getGroupKey() {
        return groupKey;
    }


    @Override
    public boolean isLeaf() {
        return false;
    }


    @Override
    public int getChildCount() {
        return children.size();
    }


    @Override
    public int getIndex(TreeNode child) {
        return children.indexOf(child);
    }


    @Override
    public AssetTreeNode getChildAt(int index) {
        return children.get(index);
    }


    /**
     * Add a child to this group
     *
     * @param child
     */
    public void addChild(AssetTreeNode child) {
        this.children.add(child);
        child.setParent(this);
    }


    @Override
    public void prepareForDisplay() {
        Collections.sort(children);

        for (final AssetTreeNode node : children) {
            node.prepareForDisplay();
        }

        for (int i = children.size() - 1; i >= 0; i--) {
            final AssetTreeNode child = children.get(i);
            if (!child.isLeaf() && child.getChildCount() == 0) {
                children.remove(i);
            }
        }
    }


    @Override
    public void processThisAndChildren(AssetTreeProcessor processor) {
        processor.process(this);
        for (final AssetTreeNode node : children) {
            node.processThisAndChildren(processor);
        }
    }


    @Override
    public Enumeration children() {
        return Collections.enumeration(children);
    }


    @Override
    public List<AssetTreeNode> getChildrenList() {
        return children;
    }


    @Override
    public boolean isDirectory() {
        return true;
    }


    @Override
    public boolean isFile() {
        return false;
    }


    @Override
    public boolean isSound() {
        return false;
    }


    @Override
    public boolean isImage() {
        return false;
    }


    @Override
    public boolean isText() {
        return false;
    }


    @Override
    public boolean isJson() {
        return false;
    }
}
