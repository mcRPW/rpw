package net.mightypork.rpw.gui.widgets;


import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.jdesktop.swingx.JXList;


public class SimpleStringList extends JScrollPane {

	public JXList list;
	private boolean selectable;


	public void setOptions(List<String> options) {

		list.removeAll();
		list.setListData(options.toArray());
		list.validate();
		validate();
	}


	public SimpleStringList(List<String> options, boolean selectable) {

		this.selectable = selectable;
		list = new JXList(options.toArray());
//		list.setVisibleRowCount(10);

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
		setPreferredSize(new Dimension(250, 200));

		setBorder(BorderFactory.createEtchedBorder());
	}


	public void setMultiSelect(boolean flag) {

		int multi = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
		int single = ListSelectionModel.SINGLE_SELECTION;

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
		Object[] foo = list.getSelectedValues();

		List<String> selected = new ArrayList<String>();

		for (Object o : foo) {
			selected.add((String) o);
		}

		return selected;
	}

}
