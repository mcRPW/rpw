package net.mightypork.rpw.gui.helpers;


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


public class MagicAwareCellStringRenderer extends JLabel implements TableCellRenderer {

	public MagicAwareCellStringRenderer() {

		setOpaque(true); //MUST do this for background to show up.
	}

	private final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);


	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

		if (value == null) {
			setText("");
			return this;
		}
		String source = value.toString();

		String display = Sources.processForDisplay(source);

		Color bgProjectUnsel = new Color(0xC1EAFF);
		Color bgProjectSel = new Color(0x73ADC8);

		Color bgVanillaUnsel = new Color(0xF7F3DC);
		Color bgVanillaSel = new Color(0x79D8D7);

		Color bgSilenceUnsel = new Color(0xEDD1FF);
		Color bgSilenceSel = new Color(0x867DFF);

		Color bgSourceUnsel = new Color(0x9DDBA3);
		Color bgSourceSel = new Color(0x469580);

		setFont(table.getFont());

		if (isSelected) {
			setForeground(table.getSelectionForeground());

			if (MagicSources.isProject(source)) {
				setBackground(bgProjectSel);
			} else if (MagicSources.isVanilla(source)) {
				setBackground(bgVanillaSel);
			} else if (MagicSources.isSilence(source)) {
				setBackground(bgSilenceSel);
			} else if (!MagicSources.isMagic(source)) {
				setBackground(bgSourceSel);
			} else {
				setBackground(table.getSelectionBackground());
			}
		} else {
			setForeground(table.getForeground());

			if (MagicSources.isProject(source)) {
				setBackground(bgProjectUnsel);
			} else if (MagicSources.isVanilla(source)) {
				setBackground(bgVanillaUnsel);
			} else if (MagicSources.isSilence(source)) {
				setBackground(bgSilenceUnsel);
			} else if (!MagicSources.isMagic(source)) {
				setBackground(bgSourceUnsel);
			} else {
				setBackground(table.getBackground());
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