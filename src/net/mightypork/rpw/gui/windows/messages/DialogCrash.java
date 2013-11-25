package net.mightypork.rpw.gui.windows.messages;


import java.awt.Color;
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
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.utils.FileUtils;
import net.mightypork.rpw.utils.GuiUtils;
import net.mightypork.rpw.utils.Log;
import net.mightypork.rpw.utils.OsUtils;


public class DialogCrash extends RpwDialog {


	private JButton buttonQuit;
	private JButton buttonContinue;


	public DialogCrash(Throwable error) {

		super(App.getFrame(), Const.APP_NAME + " has crashed!");

		Box hb;
		Box vb = Box.createVerticalBox();
		vb.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		vb.add(GuiUtils.createDialogHeading("Crash Report"));

		Log.e("Unhandled error, opening error screen.", error);

		setModal(true);
		setModalityType(ModalityType.APPLICATION_MODAL);

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

		vb.add(sbrText);

		//@formatter:off		
		hb = Box.createHorizontalBox();

			hb.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
			hb.add(Box.createHorizontalGlue());

			hb.add(buttonContinue = new JButton("Carry On", Icons.MENU_CANCEL));

			hb.add(Box.createHorizontalStrut(5));
			
			hb.add(buttonQuit = new JButton("Terminate", Icons.MENU_EXIT));
			
			hb.add(Box.createHorizontalGlue());	
			hb.setAlignmentX(0.5f);
		vb.add(hb);
		//@formatter:on

		getContentPane().add(vb);

		prepareForDisplay();
	}


	@Override
	protected void addActions() {

		buttonContinue.addActionListener(closeListener);
		buttonQuit.addActionListener(closeListener);
		buttonQuit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				try {
					App.inst.deinit();
					System.exit(1);
				} catch (Throwable t) {}
			}
		});
	}


	@Override
	public void onClose() {

		// do nothing
	}

}
