package net.mightypork.rpw.gui.helpers;


import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import net.mightypork.rpw.gui.windows.DialogSoundWizard;
import net.mightypork.rpw.gui.windows.PopupSoundFsTreeNode;
import net.mightypork.rpw.tree.filesystem.AbstractFsTreeNode;


public class SoundFileTreeClickListener extends PopupTriggerListener {

	private DialogSoundWizard wizard;


	public SoundFileTreeClickListener(JTree tree, DialogSoundWizard wizard) {

		this.tree = tree;
		this.wizard = wizard;
	}

	private JTree tree;


	@Override
	public void onPopupTrigger(MouseEvent e) {

		TreePath pathUnderMouse = tree.getPathForLocation(e.getX(), e.getY());
		if (pathUnderMouse == null) return;

		TreePath[] paths = tree.getSelectionPaths();

		if (paths.length == 0) paths = new TreePath[] { pathUnderMouse };

		List<AbstractFsTreeNode> tmpNodeList = new ArrayList<AbstractFsTreeNode>();

		for (TreePath path : paths) {

			AbstractFsTreeNode fsnode = (AbstractFsTreeNode) path.getLastPathComponent();
			tmpNodeList.add(fsnode);
		}

		if (tmpNodeList.size() == 0) return;

		PopupSoundFsTreeNode.open(tree, e.getX(), e.getY(), tmpNodeList, wizard);

	}

}
