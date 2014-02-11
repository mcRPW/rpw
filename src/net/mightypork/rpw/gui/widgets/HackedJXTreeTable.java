package net.mightypork.rpw.gui.widgets;


import java.awt.Color;
import java.awt.Component;

import javax.swing.table.TableCellRenderer;

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

		Component returnComp = super.prepareRenderer(renderer, row, column);
		Color alternateColor = new Color(0xF5F9FF);
		Color whiteColor = Color.WHITE;
		if (returnComp.getBackground().equals(whiteColor)) {
			Color bg = (row % 2 == 0 ? alternateColor : whiteColor);
			returnComp.setBackground(bg);
			bg = null;
		}
		return returnComp;
	}
	
	@Override
	public void updateUI() {
		super.updateUI();
		
		// hack for weird UI glitch with close open icons
		try {
			setCollapsedIcon(Icons.TREE_OPEN);
			setExpandedIcon(Icons.TREE_CLOSE);
		} catch(NullPointerException e) {
		}
	}
}
