package net.mightypork.rpw.gui.widgets;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import net.mightypork.rpw.utils.AlphanumComparator;

import org.jdesktop.swingx.JXList;


public class SimpleStringList extends JScrollPane {

    public JXList list;
    private boolean selectable;
    private DefaultListModel model;

    private final ArrayList<String> items = new ArrayList<String>();


    public void setItems(List<String> options) {
        items.clear();
        items.addAll(options);

        sortAndUpdate();
    }


    public SimpleStringList() {
        this(null, true);
    }


    public SimpleStringList(boolean selectable) {
        this(null, selectable);
    }


    public SimpleStringList(List<String> options, boolean selectable) {
        this.selectable = selectable;

        model = new DefaultListModel();

        list = new JXList(model);

        if (options != null) {
            setItems(options);
        }

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);

        // hide selection
        if (!selectable) {
            list.setCellRenderer(new DefaultListCellRenderer() {

                @Override
                public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected, boolean focus) {
                    super.getListCellRendererComponent(list, value, index, false, false);
                    return this;
                }
            });
        }

        setViewportView(list);
        setPreferredSize(new Dimension(250, 220));

        setBorder(BorderFactory.createEtchedBorder());
    }


    public void setMultiSelect(boolean flag) {
        final int multi = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
        final int single = ListSelectionModel.SINGLE_SELECTION;

        list.setSelectionMode(flag ? multi : single);
    }


    public int getSelectedIndex() {
        if (!selectable) return -1;
        return list.getSelectedIndex();
    }


    public int[] getSelectedIndices() {
        if (!selectable) return null;
        return list.getSelectedIndices();
    }


    public String getSelectedValue() {
        if (!selectable) return null;
        return (String) list.getSelectedValue();
    }


    public List<String> getSelectedValues() {
        if (!selectable) return null;
        final Object[] foo = list.getSelectedValues();

        final List<String> selected = new ArrayList<String>();

        for (final Object o : foo) {
            selected.add((String) o);
        }

        return selected;
    }


    public void sortAndUpdate() {
        Collections.sort(items, AlphanumComparator.instance);

        model.removeAllElements();

        for (final String item : items) {
            model.addElement(item);
        }

        list.validate();
        validate();
    }


    public void addItemNoSort(String item) {
        if (!items.contains(item)) {
            items.add(item);
        }
    }


    public void addItem(String item) {
        if (!items.contains(item)) {
            items.add(item);
            sortAndUpdate();
        }
    }


    public void removeItemNoSort(String item) {
        items.remove(item);
    }


    public void removeItem(String item) {
        items.remove(item);
        sortAndUpdate();
    }


    public void removeItem(int index) {
        items.remove(index);
        sortAndUpdate();
    }


    public ArrayList<String> getItems() {
        return items;
    }


    public String getItemAt(int where) {
        return items.get(where);
    }


    public boolean contains(String data) {
        return items.contains(data);
    }


    public JXList getList() {
        return list;
    }


    public void empty() {
        items.clear();
        sortAndUpdate();
    }

}
