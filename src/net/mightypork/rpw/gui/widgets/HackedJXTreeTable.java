package net.mightypork.rpw.gui.widgets;

import java.awt.Color;
import java.awt.Component;

import javax.swing.table.TableCellRenderer;

import net.mightypork.rpw.Const;
import net.mightypork.rpw.gui.Icons;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.TreeTableModel;


public class HackedJXTreeTable extends JXTreeTable {

    public HackedJXTreeTable(TreeTableModel treeModel) {
        super(treeModel);
    }


    @Override
    protected TreeTableHacker getTreeTableHacker() {
        return new TreeTableHackerExt999();
    }

    public class TreeTableHackerExt999 extends TreeTableHackerExt2 {
    }


    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        final Component returnComp = super.prepareRenderer(renderer, row, column);

        final Color alternateColor = Const.TABLE_ALT_COLOR;
        final Color whiteColor = Color.WHITE;
        if (returnComp.getBackground().equals(whiteColor)) {
            Color bg = (row % 2 == 0 ? whiteColor : alternateColor);
            returnComp.setBackground(bg);
        }

        return returnComp;
    }


    @Override
    public void updateUI() {
        super.updateUI();

        // hack for UI glitch with close open icons
        try {
            setCollapsedIcon(Icons.TREE_OPEN);
            setExpandedIcon(Icons.TREE_CLOSE);
        } catch (final NullPointerException e) {
        }

    }
}
