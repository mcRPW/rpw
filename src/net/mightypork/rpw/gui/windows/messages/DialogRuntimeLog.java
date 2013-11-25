package net.mightypork.rpw.gui.windows.messages;


import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.*;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Paths;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.utils.FileUtils;
import net.mightypork.rpw.utils.GuiUtils;
import net.mightypork.rpw.utils.OsUtils;


public class DialogRuntimeLog extends RpwDialog {

	private JButton btnClose;


	public DialogRuntimeLog() {

		super(App.getFrame(), "Runtime log");
		
		Box hb;
		Box vb = Box.createVerticalBox();
		vb.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		vb.add(GuiUtils.createDialogHeading("Runtime Log"));


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

		
		vb.add(sp);

		//@formatter:off		
		hb = Box.createHorizontalBox();

			hb.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
			hb.add(Box.createHorizontalGlue());
			
			hb.add(btnClose = new JButton("Close", Icons.MENU_EXIT));
			
			hb.add(Box.createHorizontalGlue());	
			hb.setAlignmentX(0.5f);
		vb.add(hb);
		//@formatter:on

		getContentPane().add(vb);
		
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
