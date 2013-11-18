package net.mightypork.rpw.gui.helpers.fstree;


import java.io.File;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;


public class DirectoryNode implements TreeNode {


	public DirectoryNode(File path) {

		// TODO Auto-generated constructor stub
	}


	public DirectoryNode(List<File> childPaths) {

		// TODO Auto-generated constructor stub
	}


	@Override
	public Enumeration children() {

		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean getAllowsChildren() {

		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public TreeNode getChildAt(int childIndex) {

		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int getChildCount() {

		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int getIndex(TreeNode node) {

		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public TreeNode getParent() {

		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean isLeaf() {

		// TODO Auto-generated method stub
		return false;
	}


}
