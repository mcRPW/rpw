package net.mightypork.rpw.gui.helpers.trees;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.UIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;


/**
 * Checkbox renderer for boolean cells, rendering null as empty cell.
 *
 * @author Ondřej Hruška (MightyPork)
 */
public class NullAwareTableCellBooleanRenderer extends JCheckBox implements TableCellRenderer, UIResource {

    private final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

    private final DefaultTableCellRenderer cdr = new DefaultTableCellRenderer();


    public NullAwareTableCellBooleanRenderer() {
        super();
        setHorizontalAlignment(SwingConstants.CENTER);
        setBorderPainted(true);
    }


    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value == null) return cdr.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);

        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }
        setSelected((((Boolean) value).booleanValue()));

        if (hasFocus) {
            setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
        } else {
            setBorder(noFocusBorder);
        }

        return this;
    }
}
