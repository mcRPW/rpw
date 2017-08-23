package net.mightypork.rpw.gui.helpers.trees;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;


public class ColumnHeaderToolTipsMouseListener extends MouseMotionAdapter {

    TableColumn curCol;
    Map<TableColumn, String> tips = new HashMap<TableColumn, String>();


    public void setToolTip(TableColumn col, String tooltip) {
        if (tooltip == null) {
            tips.remove(col);
        } else {
            tips.put(col, tooltip);
        }
    }


    @Override
    public void mouseMoved(MouseEvent evt) {
        final JTableHeader header = (JTableHeader) evt.getSource();
        final JTable table = header.getTable();
        final TableColumnModel colModel = table.getColumnModel();
        final int vColIndex = colModel.getColumnIndexAtX(evt.getX());
        TableColumn col = null;
        if (vColIndex >= 0) {
            col = colModel.getColumn(vColIndex);
        }
        if (col != curCol) {
            header.setToolTipText(tips.get(col));
            curCol = col;
        }
    }
}
