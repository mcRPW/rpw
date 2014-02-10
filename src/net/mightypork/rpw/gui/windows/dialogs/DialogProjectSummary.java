package net.mightypork.rpw.gui.windows.dialogs;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import net.mightypork.rpw.App;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.widgets.HBox;
import net.mightypork.rpw.gui.widgets.VBox;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.library.Sources;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.tree.assets.processors.GetProjectSummaryProcessor;
import net.mightypork.rpw.tree.assets.tree.AssetTreeNode;

import org.jdesktop.swingx.JXTable;


public class DialogProjectSummary extends RpwDialog {

	private JButton btnClose;


	public DialogProjectSummary() {

		super(App.getFrame(), "Project summary");

		createDialog();
	}


	@Override
	protected JComponent buildGui() {

		setResizable(true);

		HBox hb;
		VBox vbox = new VBox();
		vbox.windowPadding();

		vbox.heading("Project Assets Overview");

		JXTable table = new JXTable(getData(), getColumns());
		table.setEditable(false);
		table.setColumnSelectionAllowed(false);
		table.setFillsViewportHeight(true);
		table.getColumnModel().getColumn(0).setPreferredWidth(600);
		table.getColumnModel().getColumn(1).setPreferredWidth(200);
		table.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
		table.setRowHeight(22);

		JScrollPane sp = new JScrollPane(table);
		sp.setPreferredSize(new Dimension(700, 500));
		sp.setBorder(BorderFactory.createEtchedBorder());
		sp.setWheelScrollingEnabled(true);
		sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		vbox.add(sp);

		vbox.gapl();

		hb = new HBox();
		hb.glue();
		btnClose = new JButton("Close", Icons.MENU_EXIT);
		btnClose.setAlignmentX(Component.CENTER_ALIGNMENT);
		hb.add(btnClose);
		hb.glue();
		vbox.add(hb);

		return vbox;
	}


	private Object[] getColumns() {

		return new String[] { "Asset key", "Source" };
	}


	private Object[][] getData() {

		GetProjectSummaryProcessor proc = new GetProjectSummaryProcessor();
		AssetTreeNode root = App.getTreeDisplay().treeModel.getRoot();

		root.processThisAndChildren(proc);

		Map<String, String> summary = proc.getSummary();

		List<Object[]> rows = new ArrayList<Object[]>();

		for (Entry<String, String> e : summary.entrySet()) {
			rows.add(new Object[] { e.getKey(), Sources.processForDisplay(e.getValue()) });
		}

		for (String s : Projects.getActive().getSoundsMap().keySet()) {
			rows.add(new Object[] { s, "CUSTOM_SOUND" });
		}

		final Object[][] data = new Object[rows.size()][2];

		rows.toArray(data);

		return data;
	}


	@Override
	protected void addActions() {

		btnClose.addActionListener(closeListener);
	}

}
