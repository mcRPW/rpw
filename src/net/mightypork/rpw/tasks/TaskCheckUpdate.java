package net.mightypork.rpw.tasks;


import java.net.URI;
import java.net.URL;
import java.util.Scanner;

import javax.swing.JOptionPane;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Const;
import net.mightypork.rpw.Paths;
import net.mightypork.rpw.utils.DesktopApi;
import net.mightypork.rpw.utils.Log;


public class TaskCheckUpdate {

	public static void run() {

		Log.f2("Downloading update info");

		(new Thread(new Runnable() {

			@Override
			public void run() {

				Scanner sc = null;
				try {

					URL u = new URL(Paths.URL_UPDATE_FILE);

					sc = new Scanner(u.openStream(), "UTF-8");

					String v, msg;
					int vs = 0;

					if (!sc.hasNext()) return;

					v = sc.nextLine().trim();

					if (!sc.hasNext()) return;

					vs = Integer.valueOf(sc.nextLine().trim());

					if (!sc.hasNext()) return;
					msg = "";
					while (sc.hasNext()) {
						msg += sc.nextLine() + "\n";
					}
					msg = msg.trim();

					Log.f2("Downloading update info - done.");

					if (vs <= Const.VERSION_SERIAL) {
						Log.i("Your version is up-to-date.");
						return;
					}

					String yesBtn = "Show on web", noBtn = "Later";

					String dlgMsg = "New version of RPW (" + v + ") is available!\n" + "\n" + "\"" + msg + "\"";

					Log.i("New version available: " + v);

					//@formatter:off
					int value = JOptionPane.showOptionDialog
							(App.getFrame(),
									dlgMsg,
							"New RPW update available: " + v,
							JOptionPane.YES_NO_OPTION,
							JOptionPane.INFORMATION_MESSAGE,
							null,
							new Object[] { yesBtn, noBtn },
							yesBtn
					);
					//@formatter:on

					if (value == JOptionPane.YES_OPTION) {
						DesktopApi.browse(URI.create(Paths.URL_UPDATE_WEB));
					}

				} catch (Throwable t) {
					Log.e("Could not download update info file.");
				} finally {
					if (sc != null) sc.close();
				}
			}
		})).start();

	}
}
