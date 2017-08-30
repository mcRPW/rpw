package net.mightypork.rpw.gui.windows.dialogs;

import net.mightypork.rpw.App;
import net.mightypork.rpw.gui.Gui;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.widgets.VBox;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.library.Sources;
import net.mightypork.rpw.tree.assets.AssetEntry;
import net.mightypork.rpw.tree.assets.tree.AssetTreeGroup;
import net.mightypork.rpw.tree.assets.tree.AssetTreeNode;
import net.mightypork.rpw.utils.logging.Log;
import org.jdesktop.swingx.JXTreeTable;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Enumeration;

public class DialogSearchAssets extends RpwDialog {

    private JTextField searchField;
    private JComboBox resultsBox;
    private JButton buttonFind;
    private JButton buttonCancel;

    private TreeNode treeNode;

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
                AssetTreeGroup treeGroup = (AssetTreeGroup) treeTable.getTreeTableModel().getRoot();

                String name = resultsBox.getSelectedItem().toString();
                TreeNode node = searchGroup(treeGroup, name.substring(0, name.lastIndexOf(".")));

                TreePath treePath = new TreePath(node);
                treeTable.scrollPathToVisible(treePath);
                treeTable.getTreeSelectionModel().setSelectionPath(treePath);

            }
        }
    };

    public TreeNode searchGroup(AssetTreeGroup group, String result){
        String result2 = result.split("/")[result.split("/").length - 1];
        for (int i = 0; i < group.children.size(); i++) {
            TreeNode node = group.children.get(i);
            searchNode(node, result2);
        }

        if (treeNode != null) {
            return treeNode;
        }else {
            return null;
        }
    }

    private void searchNode(TreeNode treeNode, String result) {
        for (int i = 0; i < Collections.list(treeNode.children()).size(); i++){
            TreeNode treeNode2 = (TreeNode)Collections.list(treeNode.children()).get(i);
            if (treeNode2.toString().indexOf(result) != -1) {
                this.treeNode = treeNode2;
            }

            if (treeNode2.children() != null) {
                searchNode(treeNode2, result);
            }
        }
    }

}
