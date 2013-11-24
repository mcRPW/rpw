package net.mightypork.rpw.gui.windows.dialogs;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.*;

import net.mightypork.rpw.App;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.library.Sources;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.tree.assets.processors.GetProjectSummaryProcessor;
import net.mightypork.rpw.tree.assets.tree.AssetTreeNode;
import net.mightypork.rpw.utils.GuiUtils;

import org.jdesktop.swingx.JXTable;


public class DialogProjectSummary extends RpwDialog {

	private JButton btnClose;


	public DialogProjectSummary() {

		super(App.getFrame(), "Project summary");
		setResizable(true);

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));


		// Create Scrolling Text Area in Swing
		final String[] columns = { "Asset key", "Source" };


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

		JXTable table = new JXTable(data, columns);
		table.setEditable(false);

		table.setColumnSelectionAllowed(false);
		table.setFillsViewportHeight(true);

		table.getColumnModel().getColumn(0).setPreferredWidth(600);
		table.getColumnModel().getColumn(1).setPreferredWidth(200);

		table.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
		table.setRowHeight(22);


		JScrollPane sp = new JScrollPane(table);
		sp.setPreferredSize(new Dimension(700, 500));

		//@formatter:off
		sp.setBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createEmptyBorder(10, 10, 10, 10),
						BorderFactory.createEtchedBorder()
				)
		);
		//@formatter:on

		sp.setWheelScrollingEnabled(true);
		sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		btnClose = new JButton("Close", Icons.MENU_EXIT);
		btnClose.setAlignmentX(Component.CENTER_ALIGNMENT);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		buttonPane.add(btnClose);

		getContentPane().add(GuiUtils.createDialogHeading("Project Summary"));
		getContentPane().add(sp);
		getContentPane().add(buttonPane);

		prepareForDisplay();
	}


	@Override
	public void onClose() {

		// nothing
	}


	@Override
	protected void addActions() {

		btnClose.addActionListener(closeListener);
	}

}
