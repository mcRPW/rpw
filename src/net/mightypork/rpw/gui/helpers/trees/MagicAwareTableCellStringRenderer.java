package net.mightypork.rpw.gui.helpers.trees;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

import net.mightypork.rpw.library.MagicSources;
import net.mightypork.rpw.library.Sources;


public class MagicAwareTableCellStringRenderer extends JLabel implements TableCellRenderer {

    public MagicAwareTableCellStringRenderer() {
        setOpaque(true); // MUST do this for background to show up.
    }

    private static final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

    // normal, alt, selected
    private static final Color bgProject[] = {new Color(0xC1EAFF), new Color(0xB0DFF7), new Color(0x73ADC8)};

    private static final Color bgVanilla[] = {new Color(0xFFF3B0), new Color(0xF7EA9E), new Color(0x79D8D7)};

    private static final Color bgSilence[] = {new Color(0xEDD1FF), new Color(0xE1BEF7), new Color(0x867DFF)};

    private static final Color bgSource[] = {new Color(0x9DDBA3), new Color(0x90D696), new Color(0x469580)};

    private static final Color bgInherit[] = {new Color(0xF0F6FF), new Color(0xE1ECFC), null};


    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value == null) {
            setText("");
            return this;
        }
        final String source = value.toString();

        final String display = Sources.processForDisplay(source);

        setFont(table.getFont());

        int index = 0 + (row % 2 == 0 ? 0 : 1);
        if (isSelected) index = 2;

        setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());

        if (MagicSources.isProject(source)) {
            setBackground(bgProject[index]);
        } else if (MagicSources.isVanilla(source)) {
            setBackground(bgVanilla[index]);
        } else if (MagicSources.isSilence(source)) {
            setBackground(bgSilence[index]);
        } else if (!MagicSources.isMagic(source)) {
            setBackground(bgSource[index]);
        } else {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(bgInherit[index]);
            }
        }

        if (hasFocus) {
            setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
        } else {
            setBorder(noFocusBorder);
        }

        setFont(table.getFont());
        setText(display);

        return this;
    }
}
