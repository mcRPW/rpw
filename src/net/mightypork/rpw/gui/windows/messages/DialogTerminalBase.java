package net.mightypork.rpw.gui.windows.messages;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;

import javax.swing.*;

import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.utils.GuiUtils;


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

		Box hb;
		Box vb = Box.createVerticalBox();
		vb.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		vb.add(GuiUtils.createDialogHeading(getHeadingText()));


		textArea = new JTextArea(getLogText(), 25, 80);
		textArea.setFont(new Font(Font.MONOSPACED, 0, 14));
		textArea.setMargin(new Insets(10, 10, 10, 10));
		textArea.setEditable(true);
		textArea.setLineWrap(false);
		textArea.setBackground(Color.BLACK);
		textArea.setForeground(Color.LIGHT_GRAY);

		JScrollPane sp = new JScrollPane(textArea);

		//@formatter:off
		sp.setBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createEmptyBorder(10, 10, 10, 10),
						BorderFactory.createEtchedBorder()
				)
		);
		//@formatter:on

		sp.setWheelScrollingEnabled(true);
		sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		addStuffAboveTextarea(vb);

		vb.add(sp);

		if (hasButtons()) {

			//@formatter:off		
			hb = Box.createHorizontalBox();
	
				hb.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
				hb.add(Box.createHorizontalGlue());
				
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
					    separator.getMaximumSize().height);
						separator.setMaximumSize(size);
						
						hb.add(separator);
						
					} else {
						hb.add(btn);
					}
					i++;
				}
				
				hb.add(Box.createHorizontalGlue());	
				hb.setAlignmentX(0.5f);
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
