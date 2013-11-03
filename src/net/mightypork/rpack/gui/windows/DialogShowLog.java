package net.mightypork.rpack.gui.windows;


import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.*;

import net.mightypork.rpack.App;
import net.mightypork.rpack.Paths;
import net.mightypork.rpack.gui.Icons;
import net.mightypork.rpack.utils.FileUtils;
import net.mightypork.rpack.utils.OsUtils;


public class DialogShowLog extends RpwDialog {

	private JButton btnClose;


	public DialogShowLog() {

		super(App.getFrame(), "Runtime log");

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));


		String wholeLogAsString = "Not found.";

		try {
			wholeLogAsString = FileUtils.fileToString(OsUtils.getAppDir(Paths.FILE_LOG));
		} catch (Exception e) {
			e.printStackTrace();
		}

		String txt = wholeLogAsString;


		// Create Scrolling Text Area in Swing
		JTextArea textArea = new JTextArea(txt, 25, 80);
		textArea.setFont(new Font(Font.MONOSPACED, 0, 14));
		textArea.setMargin(new Insets(10, 10, 10, 10));
		textArea.setEditable(true);
		textArea.setLineWrap(false);

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

		btnClose = new JButton("Close", Icons.MENU_EXIT);
		btnClose.setAlignmentX(Component.CENTER_ALIGNMENT);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		buttonPane.add(btnClose);

		getContentPane().add(sp);
		getContentPane().add(buttonPane);

		prepareForDisplay();
	}


	@Override
	public void onClose() {

		// nothing
	}


	@Override
	protected void addActions() {

		btnClose.addActionListener(closeListener);
	}

}
