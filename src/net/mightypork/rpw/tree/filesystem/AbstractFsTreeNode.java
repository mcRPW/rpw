package net.mightypork.rpw.tree.filesystem;


import java.io.File;
import java.util.Enumeration;

import javax.swing.tree.TreeNode;

import net.mightypork.rpw.tree.IFileTreeNode;


/**
 * Abstract filesystem tree node
 * 
 * @author MightyPork
 */
public abstract class AbstractFsTreeNode implements TreeNode, Comparable<AbstractFsTreeNode>, IFileTreeNode {
	
	/** Reference to a parent node */
	protected AbstractFsTreeNode parent;


	@Override
	public abstract Enumeration children();


	@Override
	public boolean getAllowsChildren() {

		return !isLeaf();
	}


	@Override
	public abstract TreeNode getChildAt(int childIndex);


	@Override
	public abstract int getChildCount();


	@Override
	public abstract int getIndex(TreeNode node);


	@Override
	public TreeNode getParent() {

		return parent;
	}


	@Override
	public boolean isLeaf() {
		return !isDirectory();
	}

	/**
	 * Get name for display
	 * @return name
	 */
	public abstract String getName();


	@Override
	public int compareTo(AbstractFsTreeNode o) {

		// dirs on top
		if(o.isDirectory() && !this.isDirectory()) return 1;
		if(!o.isDirectory() && this.isDirectory()) return -1;
		
		// sort by name
		return this.getName().compareTo(o.getName());
	}


	/**
	 * Sort children
	 */
	public void sort() {}


	/**
	 * Get represented path. Null for virtual directory nodes.
	 * @return the path
	 */
	public abstract File getPath();


	@Override
	public String toString() {
	
		return getName();
	}
}
