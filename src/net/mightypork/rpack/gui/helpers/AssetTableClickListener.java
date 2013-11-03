package net.mightypork.rpack.gui.helpers;


import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreePath;

import net.mightypork.rpack.gui.windows.PopupSelectedNodes;
import net.mightypork.rpack.hierarchy.tree.AssetTreeNode;
import net.mightypork.rpack.utils.Log;

import org.jdesktop.swingx.JXTreeTable;


public class AssetTableClickListener extends PopupTriggerListener {

	public AssetTableClickListener(JXTreeTable treeTable) {

		this.treeTable = treeTable;
	}

	JXTreeTable treeTable;


	@Override
	public void onPopupTrigger(MouseEvent e) {

		TreePath pathUnderMouse = treeTable.getPathForLocation(e.getX(), e.getY());
		if (pathUnderMouse == null) return;

		TreePath[] paths = treeTable.getTreeSelectionModel().getSelectionPaths();


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
			treeTable.getTreeSelectionModel().setSelectionPath(pathUnderMouse);
			paths = new TreePath[] { pathUnderMouse };
		}

		if (paths == null) {
			Log.w("Null selection for popup, cancelling.");
			return;
		}

		List<AssetTreeNode> selectedNodes = new ArrayList<AssetTreeNode>();
		for (TreePath p : paths) {
			selectedNodes.add((AssetTreeNode) p.getLastPathComponent());
		}

		PopupSelectedNodes.open(treeTable, e.getX(), e.getY(), selectedNodes);

	}

}
