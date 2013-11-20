package net.mightypork.rpw.tree.filesystem;


import javax.swing.tree.DefaultTreeModel;


public class FsTreeModel extends DefaultTreeModel {

	public FsTreeModel(DirectoryFsTreeNode rootDirectory) {

		super(rootDirectory);
		
		rootDirectory.sort();
	}

}
