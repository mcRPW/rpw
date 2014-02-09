package net.mightypork.rpw.gui.windows.messages;


import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JButton;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Const;
import net.mightypork.rpw.Paths;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.utils.FileUtils;
import net.mightypork.rpw.utils.OsUtils;
import net.mightypork.rpw.utils.logging.Log;


public class DialogCrash extends DialogTerminalBase {

	private JButton buttonQuit;
	private JButton buttonContinue;
	private String reportText;
	private JButton buttonCopy;


	public DialogCrash(Throwable error) {

		super(App.getFrame(), Const.APP_NAME + " has crashed!");

		Log.e("Unhandled error, opening error screen.", error);

		this.reportText = createReport(error);

		createDialog();
	}


	@Override
	protected void initGui() {

		setModal(true);
		setModalityType(ModalityType.APPLICATION_MODAL);
	}


	private String createReport(Throwable error) {

		StringWriter sw = new StringWriter();
		error.printStackTrace(new PrintWriter(sw));
		String exceptionAsString = sw.toString();

		String wholeLogAsString = "Not found.";

		try {
			wholeLogAsString = FileUtils.fileToString(OsUtils.getAppDir(Paths.FILE_LOG)).replace("\n\n", "\n");
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

		return txt;
	}


	@Override
	protected void addActions() {

		buttonCopy.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				try {
					StringSelection selection = new StringSelection(reportText);
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					clipboard.setContents(selection, selection);
					Alerts.info(App.getFrame(), "Text copied to clipboard.");
				} catch (Exception e) {
					Alerts.error(App.getFrame(), "Sorry, it didn't work.\nCheck runtime log for details.");
					Log.e("Error copying to clipboard", e);
				}

			}
		});

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
	protected String getHeadingText() {

		return "Crash Report";
	}


	@Override
	protected String getLogText() {

		return reportText;
	}


	@Override
	protected boolean hasButtons() {

		return true;
	}


	@Override
	protected JButton[] makeButtons() {

		buttonCopy = new JButton("Copy text", Icons.MENU_COPY);

		buttonContinue = new JButton("Carry On", Icons.MENU_CANCEL);

		buttonQuit = new JButton("Terminate", Icons.MENU_EXIT);

		return new JButton[] { buttonCopy, null, buttonContinue, buttonQuit };
	}

}
