package net.mightypork.rpw.gui.windows.messages;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;

import javax.swing.*;

import net.mightypork.rpw.gui.widgets.HBox;
import net.mightypork.rpw.gui.widgets.VBox;
import net.mightypork.rpw.gui.windows.RpwDialog;


public abstract class DialogTerminalBase extends RpwDialog {

	protected JTextArea textArea;


	protected abstract String getHeadingText();


	protected abstract String getLogText();


	protected abstract boolean hasButtons();


	protected abstract JButton[] makeButtons();


	public DialogTerminalBase(Frame parent, String title) {

		super(parent, title);

	}


	@Override
	protected final JComponent buildGui() {

		HBox hb;
		VBox vb = new VBox();
		vb.windowPadding();

		vb.heading(getHeadingText());

		textArea = new JTextArea(getLogText(), 25, 80);
		textArea.setFont(new Font(Font.MONOSPACED, 0, 14));
		textArea.setMargin(new Insets(10, 10, 10, 10));
		textArea.setEditable(false);
		textArea.setLineWrap(false);
		textArea.setBackground(Color.BLACK);
		textArea.setForeground(Color.LIGHT_GRAY);

		JScrollPane sp = new JScrollPane(textArea);

		//@formatter:off
		sp.setBorder(BorderFactory.createEtchedBorder());
		//@formatter:on

		sp.setWheelScrollingEnabled(true);
		sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		addStuffAboveTextarea(vb);

		vb.add(sp);

		vb.gapl();

		if (hasButtons()) {

			//@formatter:off		
			hb = new HBox();
			
				hb.glue();
				
				int i=0;
				for(JButton btn : makeButtons()) {
					if(i>0) hb.add(Box.createHorizontalStrut(5));
					if(btn == null) {
						
						// BEHOLD, MAGIC
						// ugly fix for Swing bug
						// http://stackoverflow.com/a/7515903/2180189
						JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
						Dimension size = new Dimension(
								separator.getPreferredSize().width,
								separator.getMaximumSize().height
						);
						separator.setMaximumSize(size);
						
						hb.gapl();
						hb.add(separator);
						hb.gapl();
						
					} else {
						hb.add(btn);
					}
					i++;
				}
				
				hb.glue();
			vb.add(hb);
			//@formatter:on

		}

		return vb;
	}


	/**
	 * Add optional stuff above the textarea
	 * 
	 * @param vb main vertical box
	 */
	protected void addStuffAboveTextarea(Box vb) {

	}


	@Override
	protected abstract void addActions();

}
