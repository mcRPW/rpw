package net.mightypork.rpw.gui.windows.dialogs;

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
import net.mightypork.rpw.gui.Gui;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.widgets.VBox;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.library.Sources;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.tree.assets.TreeBuilder;
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

        final VBox vbox = new VBox();
        vbox.windowPadding();

        vbox.heading("Project Assets Overview");

        final JXTable table = new JXTable(getData(), getColumns());
        table.setEditable(false);
        table.setColumnSelectionAllowed(false);
        table.setFillsViewportHeight(true);
        table.getColumnModel().getColumn(0).setPreferredWidth(600);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        table.setRowHeight(22);

        final JScrollPane sp = new JScrollPane(table);
        sp.setPreferredSize(new Dimension(700, 500));
        sp.setBorder(BorderFactory.createEtchedBorder());
        sp.setWheelScrollingEnabled(true);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        vbox.add(sp);

        vbox.gapl();

        btnClose = new JButton("Close", Icons.MENU_EXIT);
        vbox.buttonRow(Gui.CENTER, btnClose);

        return vbox;
    }


    private Object[] getColumns() {
        return new String[]{"Asset key", "Source"};
    }


    private Object[][] getData() {
        final GetProjectSummaryProcessor proc = new GetProjectSummaryProcessor();
        final AssetTreeNode root = new TreeBuilder().buildTreeForExport(Projects.getActive());

        root.processThisAndChildren(proc);

        final Map<String, String> summary = proc.getSummary();

        final List<Object[]> rows = new ArrayList<Object[]>();

        for (final Entry<String, String> e : summary.entrySet()) {
            rows.add(new Object[]{e.getKey(), Sources.processForDisplay(e.getValue())});
        }

        for (final String s : Projects.getActive().getSoundsMap().keySet()) {
            rows.add(new Object[]{s, "CUSTOM_SOUND"});
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
