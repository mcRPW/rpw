package net.mightypork.rpack.utils;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;


public class GuiUtils {

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
}
