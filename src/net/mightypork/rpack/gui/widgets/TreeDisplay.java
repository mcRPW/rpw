package net.mightypork.rpack.gui.widgets;


import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import net.mightypork.rpack.App;
import net.mightypork.rpack.Config;
import net.mightypork.rpack.gui.Icons;
import net.mightypork.rpack.gui.helpers.*;
import net.mightypork.rpack.hierarchy.TreeBuilder;
import net.mightypork.rpack.hierarchy.tree.AssetTreeGroup;
import net.mightypork.rpack.hierarchy.tree.AssetTreeModel;
import net.mightypork.rpack.hierarchy.tree.AssetTreeNode;
import net.mightypork.rpack.hierarchy.tree.SourceName;
import net.mightypork.rpack.project.Project;
import net.mightypork.rpack.project.Projects;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.renderer.DefaultTreeRenderer;


public class TreeDisplay {

	public JXTreeTable treeTable;
	public AssetTreeModel treeModel;

	public static final AssetTreeGroup EMPTY_ROOT = new AssetTreeGroup(null, "", "");


	private AssetTreeGroup buildProjectRoot() {

		AssetTreeGroup root = null;

		Project p = Projects.getActive();

		if (p == null) {
			root = EMPTY_ROOT;
		} else {
			TreeBuilder tb = new TreeBuilder();
			root = tb.buildTree(p);
		}

		root.prepareForDisplay();

		return root;
	}


	public TreeDisplay() {

		treeModel = new AssetTreeModel(buildProjectRoot()); // filler empty node
		treeTable = new HackedJXTreeTable(treeModel);

		treeTable.setAutoCreateColumnsFromModel(false);
		treeTable.setRowHeight(20);
		treeTable.addMouseListener(new AssetTableClickListener(treeTable));
		treeTable.setTreeCellRenderer(new DefaultTreeRenderer(new TreeIconProvider()));
		treeTable.getTableHeader().setReorderingAllowed(false);

		treeTable.setDefaultRenderer(Boolean.class, new NullAwareCellBooleanRenderer());
		treeTable.setDefaultRenderer(SourceName.class, new MagicAwareCellStringRenderer());

		final TreeSelectionListener tsl = new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent e) {

				int count = treeTable.getTreeSelectionModel().getSelectionCount();

				if (count == 0) {
					App.getSidePanel().updatePreview(null);
				} else {

					TreePath[] paths = treeTable.getTreeSelectionModel().getSelectionPaths();
					TreePath path = paths[paths.length - 1];

					if (path == null) return;

					AssetTreeNode node = (AssetTreeNode) path.getLastPathComponent();

					App.getSidePanel().updatePreview(node);
				}
			}
		};

		treeTable.addTreeSelectionListener(tsl);


		treeTable.addMouseListener(new MouseLeaveListener() {

			@Override
			public void mouseExited(MouseEvent e) {

				// this fix is useful only when mouse-preview is enabled
				if (Config.PREVIEW_HOVER) tsl.valueChanged(null);
			}

		});

		treeTable.addMouseMotionListener(new MouseMotionListener() {

			private long lastEvent = 0;


			@Override
			public void mouseMoved(MouseEvent e) {

				if (System.currentTimeMillis() - lastEvent < 200) {
					return;
				}

				if (!Config.PREVIEW_HOVER) return;

				lastEvent = System.currentTimeMillis();

				int row = treeTable.rowAtPoint(e.getPoint());
				TreePath path = treeTable.getPathForRow(row);
				if (path == null) return;

				AssetTreeNode node = (AssetTreeNode) path.getLastPathComponent();
				App.getSidePanel().updatePreview(node);
			}


			@Override
			public void mouseDragged(MouseEvent e) {

			}
		});

		//@formatter:off
		String[] tooltips = {
				"Resource pack hierarchy",
				"Your assigned asset source",
				"Asset source used for export",
				"Asset is copied to project directory",
				"Asset has a \"McMeta\" file in the project directory."
		};
		//@formatter:on

		JTableHeader header = treeTable.getTableHeader();
		ColumnHeaderToolTipsMouseListener tips = new ColumnHeaderToolTipsMouseListener();
		for (int c = 0; c < treeTable.getColumnCount(); c++) {
			TableColumn col = treeTable.getColumnModel().getColumn(c);
			tips.setToolTip(col, tooltips[c]);
		}
		header.addMouseMotionListener(tips);


		adjustColumns();

		treeTable.expandRow(0);

		// assign custom icons
		treeTable.setCollapsedIcon(Icons.TREE_OPEN);
		treeTable.setExpandedIcon(Icons.TREE_CLOSE);
	}


	public void updateRoot() {

		treeModel.setRoot(buildProjectRoot());
		treeTable.expandRow(0);

		adjustColumns();
	}


	private void adjustColumns() {

		int columns = 5;
		int[] pref = { 150, 100, 100, 50, 50 };
		int[] min = { 150, 60, 60, 30, 30 };
		int[] max = { 2000, 250, 250, 60, 60 };

		for (int i = 0; i < columns; i++) {
			treeTable.getColumnModel().getColumn(i).setMinWidth(pref[i]);
			treeTable.getColumnModel().getColumn(i).setPreferredWidth(min[i]);
			treeTable.getColumnModel().getColumn(i).setMaxWidth(max[i]);
		}
	}


	public void togglePathRecursively(AssetTreeNode node, boolean expand) {

		if (node.isLeaf()) return;

		TreeNode[] pathObjs = treeModel.getPathToRoot(node);

		TreePath path = new TreePath(pathObjs);

		expandAll((JTree) treeTable.getCellRenderer(0, 0), path, expand);
	}


	public void togglePathSimple(AssetTreeNode node, boolean expand) {

		if (node.isLeaf()) return;

		TreeNode[] pathObjs = treeModel.getPathToRoot(node);

		TreePath path = new TreePath(pathObjs);

		if (expand) {
			((JTree) treeTable.getCellRenderer(0, 0)).expandPath(path);
		} else {
			((JTree) treeTable.getCellRenderer(0, 0)).collapsePath(path);
		}
	}


	/**
	 * Expand all tree nodes
	 * 
	 * @param tree subject tree
	 * @param parent parent tree path
	 * @param expand expand or collapse
	 */
	private static void expandAll(JTree tree, TreePath parent, boolean expand) {

		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node == null) return;
		if (node.getChildCount() > 0) {
			for (Enumeration<TreeNode> e = node.children(); e.hasMoreElements();) {
				TreeNode treeNode = e.nextElement();
				TreePath path = parent.pathByAddingChild(treeNode);
				expandAll(tree, path, expand);
			}
		}

		if (expand) {
			tree.expandPath(parent);
		} else {
			tree.collapsePath(parent);
		}
	}

}
