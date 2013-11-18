package net.mightypork.rpw.hierarchy.tree;


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

		for (AssetTreeNode node : children) {
			node.prepareForDisplay();
		}

		for (int i = children.size() - 1; i >= 0; i--) {
			AssetTreeNode child = children.get(i);
			if (!child.isLeaf() && child.getChildCount() == 0) {
				children.remove(i);
			}
		}
	}


	@Override
	public void processThisAndChildren(AssetTreeProcessor processor) {

		processor.process(this);
		for (AssetTreeNode node : children) {
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

}
