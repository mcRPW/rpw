package net.mightypork.rpw.gui.windows.messages;


import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.*;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Const;
import net.mightypork.rpw.Paths;
import net.mightypork.rpw.utils.FileUtils;
import net.mightypork.rpw.utils.GuiUtils;
import net.mightypork.rpw.utils.Log;
import net.mightypork.rpw.utils.OsUtils;


public class WindowCrash extends JFrame {

	public WindowCrash(Throwable error) {

		super(Const.APP_NAME + " has crashed!");

		Log.e(error);

		try {
			App.inst.deinit();
		} catch (Throwable t) {}

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		StringWriter sw = new StringWriter();
		error.printStackTrace(new PrintWriter(sw));
		String exceptionAsString = sw.toString();

		String wholeLogAsString = "Not found.";

		try {
			wholeLogAsString = FileUtils.fileToString(OsUtils.getAppDir(Paths.FILE_LOG));
		} catch (Exception e) {
			e.printStackTrace();
		}

		String txt = "";
		txt += "~~~ " + Const.APP_NAME + " v." + Const.VERSION + " CRASH REPORT ~~~\n";
		txt += "\n";
		txt += "Please send THIS WHOLE REPORT to MightyPork:\n";
		txt += "\tE-mail: ondra@ondrovo.com\n";
		txt += "\tTwitter: @MightyPork (post log via pastebin.com)\n";
		txt += "\n";
		txt += "\n";
		txt += "# STACK TRACE #\n";
		txt += "\n";
		txt += exceptionAsString + "\n";
		txt += "\n";
		txt += "\n";
		txt += "# FULL LOG #\n";
		txt += "\n";
		txt += wholeLogAsString + "\n";
		txt += "\n";
		txt += "\n";
		txt += "~~~ END OF REPORT ~~~";


		// Create Scrolling Text Area in Swing
		JTextArea ta = new JTextArea(txt, 25, 80);
		ta.setFont(new Font(Font.MONOSPACED, 0, 14));
		ta.setMargin(new Insets(10, 10, 10, 10));
		ta.setEditable(false);
		ta.setLineWrap(false);
		ta.setBackground(Color.BLACK);
		ta.setForeground(Color.LIGHT_GRAY);
		JScrollPane sbrText = new JScrollPane(ta);
		sbrText.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), BorderFactory.createEtchedBorder()));
		sbrText.setWheelScrollingEnabled(true);
		sbrText.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		sbrText.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);


		// Create Quit Button
		JButton btnQuit = new JButton("Quit");
		btnQuit.setAlignmentX(Component.CENTER_ALIGNMENT);

		btnQuit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				System.exit(0);
			}
		});

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		buttonPane.add(btnQuit);


		getContentPane().add(sbrText);
		getContentPane().add(buttonPane);

		// Close when the close button is clicked
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Display Frame
		pack(); // Adjusts frame to size of components

		GuiUtils.centerWindow(this, null);
	}

}
