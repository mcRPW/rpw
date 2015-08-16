package net.mightypork.rpw;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import net.mightypork.rpw.gui.Gui;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.widgets.MenuMain;
import net.mightypork.rpw.gui.widgets.SidePanel;
import net.mightypork.rpw.gui.widgets.TreeDisplay;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.gui.windows.WindowMain;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.gui.windows.messages.DialogCrash;
import net.mightypork.rpw.help.VersionUtils;
import net.mightypork.rpw.library.Sources;
import net.mightypork.rpw.project.NodeSourceProvider;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.tasks.Tasks;
import net.mightypork.rpw.utils.HtmlBuilder;
import net.mightypork.rpw.utils.files.OsUtils;
import net.mightypork.rpw.utils.logging.Log;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.jdesktop.swingx.JXFrame;


public class App
{
	public static App inst;

	public volatile static RpwDialog activeDialog;

	/** Main App Why does eWindow */
	public WindowMain window;
	public MenuMain menu;

	public NodeSourceProvider activeProject;


	public static void main(String[] args)
	{
		Thread.setDefaultUncaughtExceptionHandler(new CrashHandler());

		inst = new App();
		inst.start();
	}


	public void start()
	{
		init();
	}


	public void init()
	{
		if (!lockInstance()) {
			//@formatter:off
			Alerts.error(
					null,
					"Couldn't lock workdir",
					"The application is already running.\n" +
					"\n" +
					"No more than one instance can run at a time."
			);
			//@formatter:on

			System.exit(1);
		}

		Log.init();

		Log.i("ResourcePack Workbench v." + Const.VERSION + " (#" + Const.VERSION_SERIAL + ")");

		Log.f1("Init started...");
		OsUtils.initDirs();
		Config.init();

		// attempt to enable anti-aliasing
		try {
			System.setProperty("awt.useSystemAAFontSettings", "on");
			System.setProperty("swing.aatext", "true");
			System.setProperty("sun.java2d.xrender", "true");
		} catch (Exception e) {
			Log.e(e);
		}

		// attempt to enable NUMBUS
		if (Config.USE_NIMBUS) {
			Gui.useNimbus();
		}

		Log.f3("Last run version: " + VersionUtils.getVersionString(Config.LAST_RUN_VERSION) + " (#" + Config.LAST_RUN_VERSION + ")");
		Log.i("Using library version: " + Config.LIBRARY_VERSION);

		HtmlBuilder.init();

		Icons.init();

		// preload RSyntaxTextArea (it's big and causes lag otherwise)
		Log.f3("Initializing RSyntaxTextArea");
		@SuppressWarnings("unused")
		RSyntaxTextArea rsa = new RSyntaxTextArea();

		Tasks.taskLoadHelp();
		Tasks.taskCreateModConfigFiles();

		Sources.init();

		Log.f2("Building main window.");

		final int t = Tasks.taskBuildMainWindow();
		while (Tasks.isRunning(t)) {
		} // wait for completion on EDT

		Tasks.checkUpdate();

		Log.f2("Opening last project (if any).");

		Projects.openLastProject();

		Tasks.taskShowChangelog();
	}


	public void deinit()
	{
		try {
			window.frame.dispose();
		} catch (final Throwable t) {
			Log.e(t);
		}
	}


	public static void die(String message)
	{
		Alerts.error(null, "Fatal Error", message);
		App.inst.deinit();
		System.exit(1);
	}


	private static boolean lockInstance()
	{
		final File lockFile = OsUtils.getAppDir(".lock");

		try {
			final RandomAccessFile randomAccessFile = new RandomAccessFile(lockFile, "rw");
			final FileLock fileLock = randomAccessFile.getChannel().tryLock();
			if (fileLock != null) {
				Runtime.getRuntime().addShutdownHook(new Thread() {

					@Override
					public void run()
					{
						try {
							fileLock.release();
							randomAccessFile.close();
							lockFile.delete();
						} catch (final Exception e) {
							System.out.println("Unable to remove lock file.");
							e.printStackTrace();
						}
					}
				});
				return true;
			}
		} catch (final Exception e) {
			System.out.println("Unable to create lock file.");
			e.printStackTrace();
		}
		return false;
	}


	/**
	 * Show crash report dialog with error stack trace.
	 * 
	 * @param error
	 */
	public static void onCrash(final Throwable error)
	{
		final RpwDialog dialog = new DialogCrash(error);
		dialog.setVisible(true);
	}


	public static SidePanel getSidePanel()
	{
		if (inst == null || inst.window == null) return null;

		return inst.window.sidePanel;
	}


	public static TreeDisplay getTreeDisplay()
	{
		if (inst == null || inst.window == null) return null;

		return inst.window.treeDisplay;
	}


	public static JXFrame getFrame()
	{
		if (inst == null || inst.window == null) return null;

		return inst.window.frame;
	}


	public static WindowMain getWindow()
	{
		if (inst == null) return null;

		return inst.window;
	}


	public static MenuMain getMenu()
	{
		if (inst == null || inst.window == null) return null;

		return inst.window.menu;
	}


	public static void setWaiting(boolean state)
	{
		if (inst == null || inst.window == null) return;

		inst.window.setWaiting(state);

	}


	public static String getWindowTitle()
	{
		String wt = "";
		if (Projects.isOpen()) wt += Projects.getActive().getName() + "  \u2022  ";
		wt += Const.APP_NAME + " v" + Const.VERSION + "  \u2022  mc [ " + Config.LIBRARY_VERSION + " ]  \u2022  App by @MightyPork";
		return wt;
	}


	public static void setTitle(String windowTitle)
	{
		if (inst == null || inst.window == null) return;

		inst.window.frame.setTitle(windowTitle);
	}
}
