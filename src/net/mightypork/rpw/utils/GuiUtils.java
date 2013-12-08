package net.mightypork.rpw.utils;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;

import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.utils.logging.Log;


public class GuiUtils {

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
		title.setForeground(new Color(0x045A80));
		title.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
		title.setAlignmentX(0.5f);

		return title;
	}


	public static void open(RpwDialog dialog) {

		dialog.openDialog();
	}
}
