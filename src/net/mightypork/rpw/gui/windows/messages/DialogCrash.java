package net.mightypork.rpw.gui.windows.messages;


import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JButton;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Config;
import net.mightypork.rpw.Const;
import net.mightypork.rpw.Paths;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.utils.files.DesktopApi;
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.files.OsUtils;
import net.mightypork.rpw.utils.logging.Log;


public class DialogCrash extends DialogTerminalBase {

	private JButton buttonQuit;
	private JButton buttonContinue;
	private String reportText;
	private JButton buttonCopy;
	private JButton buttonGH;


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
		txt += "## RPW " + Const.VERSION + " CRASH REPORT ##\n";

		txt += "\n";

		txt += "Please, report this issue on GitHub (use the button below).\n";
		txt += "If possible, include THIS WHOLE LOG in the report.\n";

		txt += "\n\n";

		txt += "Alternatively, you can send this log to MightyPork.\n";
		txt += "\tE-mail: ondra@ondrovo.com\n";

		txt += "\n\n";
		txt += "### SYSTEM INFO ###\n";
		txt += "\n\n";

		txt += " Key               | Value \n";
		txt += "-------------------|-------------------------------\n";
		txt += " Runtime name      | " + System.getProperty("java.runtime.name") + "\n";
		txt += " Java version      | " + System.getProperty("java.version") + "\n";
		txt += " OS name           | " + System.getProperty("os.name") + "\n";
		txt += " File encoding     | " + System.getProperty("file.encoding") + "\n";
		txt += " Launch dir        | " + System.getProperty("user.dir") + "\n";
		txt += " RPW version       | " + Const.VERSION + "\n";
		txt += " Library version   | " + Config.LIBRARY_VERSION + "\n";
		txt += " RPW path          | " + OsUtils.getAppDir() + "\n";
		txt += " Minecraft path    | " + OsUtils.getMcDir() + "\n";

		txt += "\n\n";
		txt += "### STACK TRACE ###\n";
		txt += "\n\n";

		txt += "```\n";
		txt += exceptionAsString + "\n";
		txt += "```\n";

		txt += "\n\n";
		txt += "### FULL LOG ###\n";
		txt += "\n\n";

		txt += "```\n";
		txt += wholeLogAsString + "\n";
		txt += "```\n";

		txt += "\n\n";
		txt += "~~~ END OF REPORT ~~~";

		return txt;
	}


	@Override
	protected void addActions() {

		buttonGH.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				try {
					DesktopApi.browse(new URL("https://github.com/MightyPork/rpw/issues/new").toURI());
				} catch (Exception exc) {
					Alerts.error(self(), "Sorry, something went wrong.");
				}

			}
		});

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

		buttonGH = new JButton("Report issue", Icons.MENU_GITHUB);
		buttonCopy = new JButton("Copy text", Icons.MENU_COPY);
		buttonContinue = new JButton("Carry On", Icons.MENU_CANCEL);
		buttonQuit = new JButton("Close RPW", Icons.MENU_EXIT);

		return new JButton[] { buttonCopy, buttonGH, null, buttonContinue, buttonQuit };
	}

}
