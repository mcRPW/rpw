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

    private final DialogSoundWizard wizard;


    public SoundFileTreeClickListener(JTree tree, DialogSoundWizard wizard) {
        this.tree = tree;
        this.wizard = wizard;
    }

    private final JTree tree;


    @Override
    public void onPopupTrigger(MouseEvent e) {
        final TreePath pathUnderMouse = tree.getPathForLocation(e.getX(), e.getY());
        if (pathUnderMouse == null) return;

        TreePath[] paths = tree.getSelectionPaths();

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
            tree.getSelectionModel().setSelectionPath(pathUnderMouse);
            paths = new TreePath[]{pathUnderMouse};
        }

        if (paths == null) {
            Log.w("Null selection for popup, cancelling.");
            return;
        }

        final List<AbstractFsTreeNode> tmpNodeList = new ArrayList<AbstractFsTreeNode>();

        for (final TreePath path : paths) {
            final AbstractFsTreeNode fsnode = (AbstractFsTreeNode) path.getLastPathComponent();
            tmpNodeList.add(fsnode);
        }

        if (tmpNodeList.size() == 0) return;

        PopupSoundFsTreeNode.open(tree, e.getX(), e.getY(), tmpNodeList, wizard);

    }

}
