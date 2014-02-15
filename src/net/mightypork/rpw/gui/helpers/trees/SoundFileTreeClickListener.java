package net.mightypork.rpw.gui.helpers.trees;


import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import net.mightypork.rpw.gui.helpers.PopupTriggerListener;
import net.mightypork.rpw.gui.windows.dialogs.DialogSoundWizard;
import net.mightypork.rpw.gui.windows.popups.PopupSoundFsTreeNode;
import net.mightypork.rpw.tree.filesystem.AbstractFsTreeNode;
import net.mightypork.rpw.utils.logging.Log;


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


		boolean clickedOnSelected = false;

		if (paths != null) {
			for (TreePath p : paths) {
				if (p == pathUnderMouse) {
					clickedOnSelected = true;
					break;
				}
			}
		}

		if (!clickedOnSelected) {
			tree.getSelectionModel().setSelectionPath(pathUnderMouse);
			paths = new TreePath[] { pathUnderMouse };
		}

		if (paths == null) {
			Log.w("Null selection for popup, cancelling.");
			return;
		}

		List<AbstractFsTreeNode> tmpNodeList = new ArrayList<AbstractFsTreeNode>();

		for (TreePath path : paths) {

			AbstractFsTreeNode fsnode = (AbstractFsTreeNode) path.getLastPathComponent();
			tmpNodeList.add(fsnode);
		}

		if (tmpNodeList.size() == 0) return;

		PopupSoundFsTreeNode.open(tree, e.getX(), e.getY(), tmpNodeList, wizard);

	}

}
