package net.mightypork.rpack.gui.widgets;


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
}
