package net.mightypork.rpw.gui.windows.dialogs;

import net.mightypork.rpw.App;
import net.mightypork.rpw.gui.Gui;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.widgets.VBox;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.library.Sources;
import net.mightypork.rpw.tree.assets.AssetEntry;
import net.mightypork.rpw.tree.assets.tree.AssetTreeGroup;
import net.mightypork.rpw.utils.logging.Log;
import org.jdesktop.swingx.JXTreeTable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DialogSearchAssets extends RpwDialog {

    private JTextField searchField;
    private JComboBox resultsBox;
    private JButton buttonFind;
    private JButton buttonCancel;

    public DialogSearchAssets() {
        super(App.getFrame(), "Search Assets");

        createDialog();
    }

    @Override
    protected JComponent buildGui() {
        final VBox vbox = new VBox();
        vbox.windowPadding();

        vbox.heading("Search Assets");

        vbox.gap();

        vbox.add(searchField = Gui.textField("", "Search", "Search for assets"));

        vbox.gap();

        vbox.titsep("Results");

        resultsBox = new JComboBox();
        resultsBox.setPreferredSize(new Dimension(400, (int)resultsBox.getPreferredSize().getHeight()));
        vbox.add(resultsBox);

        vbox.gap_large();

        buttonFind = new JButton("Find", Icons.MENU_SEARCH);
        buttonCancel = new JButton("Cancel", Icons.MENU_CANCEL);
        vbox.buttonRow(Gui.RIGHT, buttonFind, buttonCancel);

        return vbox;
    }

    @Override
    protected void addActions() {
        buttonFind.addActionListener(findListener);
        buttonCancel.addActionListener(closeListener);
        searchField.addActionListener(searchListener);
    }

    private final ActionListener searchListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            String text = searchField.getText();
            resultsBox.removeAllItems();

            for (int k = 0; k < Sources.vanilla.getAssetEntries().size(); k++) {
                AssetEntry asset = (AssetEntry) Sources.vanilla.getAssetEntries().toArray()[k];

                if (asset.getPath().indexOf(text) > -1) {
                    resultsBox.addItem(asset.getPath());
                }
            }

            resultsBox.updateUI();
        }
    };

    private final ActionListener findListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            JXTreeTable treeTable = App.getTreeDisplay().treeTable;

            if (resultsBox.getSelectedIndex() != -1) {
                AssetTreeGroup treeGroup = (AssetTreeGroup)treeTable.getTreeTableModel().getRoot();

                for (int i = 0; i < treeGroup.children.size(); i++){
                    Log.w(i + ", " + treeGroup.children.get(i).getLabel());
                    if (treeGroup.children.get(i).getLabel().indexOf(resultsBox.getSelectedItem().toString()) > -1){
                        treeTable.setRowSelectionInterval(i, i);
                    }
                }
            }
        }

    };

}
