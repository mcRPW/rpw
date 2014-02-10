package net.mightypork.rpw.gui;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.utils.files.DesktopApi;
import net.mightypork.rpw.utils.logging.Log;

import org.jdesktop.swingx.JXTextField;
import org.jdesktop.swingx.prompt.PromptSupport;


public class Gui {

	public static final int PADDING_TEXTFIELD = 3;
	public static final int GAP = 6;
	public static final int GAPL = 12;
	public static final int WINDOW_PADDING = 12;
	private static final int ETCHBDR_PADDING = 3;
	public static final Color HEADING_COLOR = new Color(0x045A80);
	public static final Color SUBHEADING_COLOR = new Color(0x00294F);

	public static final ActionListener openUrlListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			DesktopApi.browse(URI.create(e.getActionCommand()));
		}
	};


	public static void forceSize(Component c, int x, int y) {

		Dimension d = new Dimension(x, y);
		c.setMinimumSize(d);
		c.setMaximumSize(d);
		c.setPreferredSize(d);
	}


	public static void setMinPrefSize(Component c, int x, int y) {

		Dimension d = new Dimension(x, y);
		c.setMinimumSize(d);
		c.setPreferredSize(d);
	}


	public static void centerWindow(Component window, Component parent) {

		try {
			if (parent == null) {
				Point p = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
				window.setLocation(p.x - window.getWidth() / 2, p.y - window.getHeight() / 2);
			} else {
				Point point = parent.getLocationOnScreen();
				Dimension dim = parent.getSize();
				window.setLocation(point.x + (dim.width - window.getWidth()) / 2, point.y + (dim.height - window.getHeight()) / 2);
			}
		} catch (IllegalStateException e) {
			// meh
			Log.e("Failed to center window.");
		}
	}


	public static JComponent createDialogHeading(String text) {

		JLabel title = new JLabel(text);
		title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
		title.setForeground(HEADING_COLOR);
		title.setBorder(BorderFactory.createEmptyBorder(0, GAPL, GAPL, GAPL));
		title.setAlignmentX(0.5f);

		return title;
	}


	public static void open(RpwDialog dialog) {

		dialog.openDialog();
	}


	public static JXTextField textField() {

		return textField(null, null, null);
	}


	public static JXTextField textField(String text) {

		return textField(text, null, null);
	}


	public static JXTextField textField(String text, String placeholder) {

		return textField(text, placeholder, placeholder);
	}


	public static JXTextField textField(String text, String placeholder, String tooltip) {

		int padding = PADDING_TEXTFIELD;

		JXTextField field = new JXTextField();
		Border bdr = BorderFactory.createCompoundBorder(field.getBorder(), BorderFactory.createEmptyBorder(padding, padding, padding, padding));
		field.setBorder(bdr);
		if (text != null) field.setText(text);
		if (tooltip != null) field.setToolTipText(tooltip);
		if (placeholder != null) PromptSupport.setPrompt(placeholder, field);
		return field;
	}


	public static Component hgap_small() {

		return Box.createHorizontalStrut(GAP);
	}


	public static Component vgap_small() {

		return Box.createVerticalStrut(GAP);
	}


	public static Component hgap_large() {

		return Box.createHorizontalStrut(GAPL);
	}


	public static Component vgap_large() {

		return Box.createVerticalStrut(GAPL);
	}


	public static Border winbdr() {

		return BorderFactory.createEmptyBorder(WINDOW_PADDING, WINDOW_PADDING, WINDOW_PADDING, WINDOW_PADDING);
	}


	public static Border etchbdr() {

		//@formatter:off
		return new CompoundBorder(
				BorderFactory.createEtchedBorder(),
				BorderFactory.createEmptyBorder(ETCHBDR_PADDING, ETCHBDR_PADDING, ETCHBDR_PADDING, ETCHBDR_PADDING)
		);
		//@formatter:on
	}


	public static Component hglue() {

		return Box.createHorizontalGlue();
	}


	public static Component vglue() {

		return Box.createVerticalGlue();
	}


	public static JButton sidebarButton(String text, String tooltip, ImageIcon icon) {

		JButton jb = new JButton(text);
		if (icon != null) jb.setIcon(icon);
		if (tooltip != null) jb.setToolTipText(tooltip);

		jb.setHorizontalAlignment(SwingConstants.LEFT);
		jb.setIconTextGap(Gui.GAP);

		return jb;
	}
}
