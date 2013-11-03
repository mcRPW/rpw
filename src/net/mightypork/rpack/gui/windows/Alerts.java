package net.mightypork.rpack.gui.windows;


import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import net.mightypork.rpack.App;
import net.mightypork.rpack.gui.Icons;
import net.mightypork.rpack.utils.GuiUtils;
import net.mightypork.rpack.utils.Log;
import net.mightypork.rpack.utils.Utils;

import org.jdesktop.swingx.JXFrame;


public class Alerts {

	private static JXFrame loaderFrame;


	public static void error(Component c, String message) {

		error(c, "Error", message);
	}


	public static void error(Component c, String title, String message) {

		Log.e(message);

		//@formatter:off
		JOptionPane.showMessageDialog(
				c,
				message,
				title,
				JOptionPane.ERROR_MESSAGE
			);
		//@formatter:on
	}


	public static void warning(Component c, String message) {

		warning(c, "Warning", message);
	}


	public static void warning(Component c, String title, String message) {

		Log.w(message);

		//@formatter:off
		JOptionPane.showMessageDialog(
				c,
				message,
				title,
				JOptionPane.WARNING_MESSAGE
			);
		//@formatter:on
	}


	public static void info(Component c, String message) {

		info(c, "Information", message);
	}


	public static void info(Component c, String title, String message) {

		Log.i(message);

		//@formatter:off
		JOptionPane.showMessageDialog(
				c,
				message,
				title,
				JOptionPane.INFORMATION_MESSAGE
			);
		//@formatter:on
	}


	public static void loading(final boolean waiting) {

		if (loaderFrame == null || loaderFrame.getParent() != App.getFrame()) {
			if (loaderFrame != null) loaderFrame.dispose();

			// init loader
			loaderFrame = new JXFrame("Working..."); //App.getFrame(), 

			Box b = Box.createVerticalBox();
			b.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

			JLabel label = new JLabel(Icons.LOADING);

			b.add(label);

			loaderFrame.add(b);
			loaderFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			loaderFrame.setResizable(false);
			loaderFrame.setAlwaysOnTop(true);
			loaderFrame.pack();

			loaderFrame.setWaitCursorVisible(true);
			loaderFrame.setWaiting(true);
			loaderFrame.setWaitPaneVisible(true);
		}

		if (!waiting) {
			Utils.sleep(80); // to show the loading dialog even the minimal length
		}

		if (App.getFrame() == null || !waiting) {
			GuiUtils.centerWindow(loaderFrame, App.getFrame());
			loaderFrame.setVisible(waiting);
		}

		App.setWaiting(waiting);
	}


	public static boolean askYesNo(Component c, String title, String message) {

		//@formatter:off
		return JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(
				c,
				message,
				title,
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE
		);
		//@formatter:on
	}


	public static boolean askOkCancel(Component c, String title, String message) {

		//@formatter:off
		return JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(
				c,
				message,
				title,
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE
		);
		//@formatter:on
	}


	public static int askYesNoCancel(Component c, String title, String message) {

		//@formatter:off
		return JOptionPane.showConfirmDialog(
				c,
				message,
				title,
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE
		);
		//@formatter:on
	}


	public static String askForInput(Component c, String title, String message, String initial) {

		return (String) JOptionPane.showInputDialog(c, message, title, JOptionPane.QUESTION_MESSAGE, null, null, initial);

//		//@formatter:off
//		return JOptionPane.showInputDialog(
//				c,
//				message,
//				title,
//				JOptionPane.QUESTION_MESSAGE
//		);
//		//@formatter:on
	}
}
