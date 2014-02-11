package net.mightypork.rpw.gui.widgets;


import javax.swing.BoxLayout;

import net.mightypork.rpw.gui.Gui;

import org.jdesktop.swingx.JXTitledSeparator;


public class VBox extends RpwBox {

	public VBox() {

		super(BoxLayout.Y_AXIS);
	}


	@Override
	public void gap_small() {

		add(Gui.vgap_small());
	}


	@Override
	public void gap_large() {

		add(Gui.vgap_large());
	}


	@Override
	public void glue() {

		add(Gui.vglue());
	}


	/**
	 * Add titled separator
	 * 
	 * @param string title text
	 * @return titled separator (for further tweaks)
	 */
	public JXTitledSeparator titsep(String string) {

		JXTitledSeparator s;
		add(s = new JXTitledSeparator(string));
		s.setForeground(Gui.HEADING_COLOR);
		return s;
	}


	public void heading(String string) {

		add(Gui.createDialogHeading(string));
	}

}
