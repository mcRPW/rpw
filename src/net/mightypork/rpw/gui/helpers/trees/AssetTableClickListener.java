package net.mightypork.rpw.gui.helpers.trees;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreePath;

import net.mightypork.rpw.gui.helpers.PopupTriggerListener;
import net.mightypork.rpw.gui.windows.popups.PopupSelectedNodes;
import net.mightypork.rpw.tree.assets.tree.AssetTreeNode;
import net.mightypork.rpw.utils.logging.Log;

import org.jdesktop.swingx.JXTreeTable;


public class AssetTableClickListener extends PopupTriggerListener {

    public AssetTableClickListener(JXTreeTable treeTable) {
        this.treeTable = treeTable;
    }

    JXTreeTable treeTable;


    @Override
    public void onPopupTrigger(MouseEvent e) {
        final TreePath pathUnderMouse = treeTable.getPathForLocation(e.getX(), e.getY());
        if (pathUnderMouse == null) return;

        TreePath[] paths = treeTable.getTreeSelectionModel().getSelectionPaths();

        boolean clickedOnSelected = false;

        if (paths != null) {
            for (final TreePath p : paths) {
                if (p == pathUnderMouse) {
                    clickedOnSelected = true;
                    break;
                }
            }
        }

        if (!clickedOnSelected) {
            treeTable.getTreeSelectionModel().setSelectionPath(pathUnderMouse);
            paths = new TreePath[]{pathUnderMouse};
        }

        if (paths == null) {
            Log.w("Null selection for popup, cancelling.");
            return;
        }

        final List<AssetTreeNode> selectedNodes = new ArrayList<AssetTreeNode>();
        for (final TreePath p : paths) {
            selectedNodes.add((AssetTreeNode) p.getLastPathComponent());
        }

        PopupSelectedNodes.open(treeTable, e.getX(), e.getY(), selectedNodes);

    }

}
