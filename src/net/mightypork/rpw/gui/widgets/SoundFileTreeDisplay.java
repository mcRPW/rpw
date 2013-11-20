package net.mightypork.rpw.gui.widgets;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.tree.TreeIconProvider;
import net.mightypork.rpw.tree.filesystem.DirectoryFsTreeNode;
import net.mightypork.rpw.tree.filesystem.FsTreeModel;
import net.mightypork.rpw.utils.OsUtils;

import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.renderer.DefaultTreeRenderer;


public class SoundFileTreeDisplay {
	
	public JScrollPane scrollPane;
	public JXTree tree;
	private DirectoryFsTreeNode rootNode;
	private FsTreeModel model;
	
	public SoundFileTreeDisplay() {
		
		rootNode = new DirectoryFsTreeNode(OsUtils.getAppDir());
		
		model = new FsTreeModel(rootNode);
		
		tree = new JXTree(model);
		tree.setRowHeight(20);
		tree.setShowsRootHandles(true);
		tree.setRootVisible(false);

		
		tree.setCellRenderer(new DefaultTreeRenderer(new TreeIconProvider()));
		
		tree.setCollapsedIcon(Icons.TREE_OPEN);
		tree.setExpandedIcon(Icons.TREE_CLOSE);
		
		scrollPane = new JScrollPane(tree);
	}
	
	public JComponent getComponent() {
		return scrollPane;
	}
	
}
