package net.mightypork.rpw.gui.widgets;


import javax.swing.BoxLayout;

import net.mightypork.rpw.gui.Gui;


public class HBox extends RpwBox {

	public HBox() {

		super(BoxLayout.X_AXIS);
	}


	@Override
	public void gap_small() {

		add(Gui.hgap_small());
	}


	@Override
	public void gap_large() {

		add(Gui.hgap_large());
	}


	@Override
	public void glue() {

		add(Gui.hglue());
	}

}
