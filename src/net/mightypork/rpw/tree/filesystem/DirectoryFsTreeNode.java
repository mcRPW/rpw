package net.mightypork.rpw.tree.filesystem;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;

import net.mightypork.rpw.tree.assets.EAsset;
import net.mightypork.rpw.utils.FileUtils;


/**
 * Directory filesystem tree node
 * 
 * @author MightyPork
 */
public class DirectoryFsTreeNode extends AbstractFsTreeNode {

	private File path = null;
	private String name = null;
	private ArrayList<AbstractFsTreeNode> children = new ArrayList<AbstractFsTreeNode>();


	/**
	 * @param path represented folder
	 */
	public DirectoryFsTreeNode(File path) {
		
		this(path.getName(), FileUtils.listDirectory(path));

		this.path = path;
	}


	/**
	 * @param name display name
	 * @param childPaths paths to children
	 */
	public DirectoryFsTreeNode(String name, List<File> childPaths) {

		this.name = name;

		for (File f : childPaths) {
			AbstractFsTreeNode node = makeChildForFile(f);
			
			if(node != null) children.add(node);
		}
	}


	/**
	 * @param name display name
	 * @param childPaths paths to children
	 */
	public DirectoryFsTreeNode(String name, File... childPaths) {

		this.name = name;

		for (File f : childPaths) {

			AbstractFsTreeNode node = makeChildForFile(f);
			
			if(node != null) children.add(node);
		}
	}


	private AbstractFsTreeNode makeChildForFile(File f) {

		if (!f.exists()) return null;
		if (f.isDirectory()) return new DirectoryFsTreeNode(f);
		if(EAsset.forFile(f) == null) return null;
		if (f.isFile()) return new FileFsTreeNode(f);
		return null;
	}


	@Override
	public Enumeration children() {

		return Collections.enumeration(children);
	}


	@Override
	public TreeNode getChildAt(int childIndex) {

		return children.get(childIndex);
	}


	@Override
	public int getChildCount() {

		return children.size();
	}


	@Override
	public int getIndex(TreeNode node) {

		for (int i = 0; i < children.size(); i++) {
			if (children.get(i) == node) return i;
		}

		return -1;
	}


	@Override
	public boolean isLeaf() {

		return false;
	}


	@Override
	public File getPath() {

		return path;
	}


	@Override
	public void sort() {
		Collections.sort(children);
		for(AbstractFsTreeNode n: children) {
			n.sort();
		}
	}


	@Override
	public String getName() {

		return name;
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
}
