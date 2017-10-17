package net.mightypork.rpw;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.mightypork.rpw.gui.Gui;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.widgets.MenuMain;
import net.mightypork.rpw.gui.widgets.SidePanel;
import net.mightypork.rpw.gui.widgets.TreeDisplay;
import net.mightypork.rpw.gui.windows.WindowSplash;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.gui.windows.WindowMain;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.gui.windows.messages.DialogCrash;
import net.mightypork.rpw.help.VersionUtils;
import net.mightypork.rpw.library.Sources;
import net.mightypork.rpw.project.Project;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.tasks.Tasks;
import net.mightypork.rpw.utils.HtmlBuilder;
import net.mightypork.rpw.utils.Utils;
import net.mightypork.rpw.utils.files.OsUtils;
import net.mightypork.rpw.utils.logging.Log;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.jdesktop.swingx.JXFrame;


public class App {
    public static App inst;

    public volatile static RpwDialog activeDialog;

    public WindowMain window;
    public WindowSplash splashWindow;
    public JXFrame mainFrame; // always the active frame


    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler());

        // Fix the menubar "name" on OSX
        try {
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", Const.APP_NAME_SHORT);
        } catch (Exception e) {
            /* dontGiveAFuck() */
        }

        inst = new App();
        inst.start();
    }


    public void start() {
        init();
    }


    public void init() {
        if (!lockInstance()) {
            Alerts.error(
                    null,
                    "Couldn't lock workdir",
                    "The application is already running.\n\nNo more than one instance can run at a time."
            );

            System.exit(1);
        }

        Log.init();

        Log.i("ResourcePack Workbench v." + Const.VERSION + " (#" + Const.VERSION_SERIAL + ")");

        Log.f1("Init started...");
        if (Projects.getRecentProjects().size() > 0) {
            Config.LIBRARY_VERSION = new Project(Projects.getRecentProjects().get(0)).getCurrentMcVersion();
        }

        OsUtils.initDirs();
        Config.init();
        OsUtils.initWorkdir();

        // attempt to enable anti-aliasing
        try {
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");
            System.setProperty("sun.java2d.xrender", "true");
        } catch (Exception e) {
            Log.e(e);
        }

        // attempt to enable NIMBUS / NATIVE
        if (Config.USE_NIMBUS) {
            Gui.useNimbusLaF();
        } else if (Config.USE_NATIVE_THEME) {
            Gui.useNativeLaF();
        }

        // Placeholder main frame
        Icons.initWindowIcon();
        splashWindow = new WindowSplash();

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

        // Build main window, hide placeholder & set mainFrame
        final int t = Tasks.taskBuildMainWindow();
        while (Tasks.isRunning(t)) {
            Utils.sleep(10);
        } // wait for completion on EDT

        Tasks.checkUpdate();

        Log.f2("Opening last project (if any).");

        Projects.openLastProject();

        Tasks.taskShowChangelog();
    }


    public void deinit() {
        try {
            splashWindow.frame.dispose();
            window.frame.dispose();
        } catch (final Throwable t) {
            Log.e(t);
        }
    }


    public static void die(String message) {
        Alerts.error(null, "Fatal Error", message);
        App.inst.deinit();
        System.exit(1);
    }


    private static boolean lockInstance() {
        final File lockFile = OsUtils.getAppDir(".lock");

        try {
            final RandomAccessFile randomAccessFile = new RandomAccessFile(lockFile, "rw");
            final FileLock fileLock = randomAccessFile.getChannel().tryLock();
            if (fileLock != null) {
                Runtime.getRuntime().addShutdownHook(new Thread() {

                    @Override
                    public void run() {
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
    public static void onCrash(final Throwable error) {
        final RpwDialog dialog = new DialogCrash(error);
        dialog.setVisible(true);
    }


    public static SidePanel getSidePanel() {
        if (inst == null || inst.window == null) return null;

        return inst.window.sidePanel;
    }


    public static TreeDisplay getTreeDisplay() {
        if (inst == null || inst.window == null) return null;

        return inst.window.treeDisplay;
    }


    public static JXFrame getFrame() {
        if (inst == null || inst.mainFrame == null) return null;
        return inst.mainFrame;
    }


    public static MenuMain getMenu() {
        if (inst == null || inst.window == null) return null;

        return inst.window.menu;
    }


    public static void setWaiting(boolean state) {
        if (inst == null || inst.window == null) return;

        inst.window.setWaiting(state);
    }


    public static String getWindowTitle() {
        String wt = "";
        if (Projects.isOpen()) wt += Projects.getActive().getName() + "  \u2022  ";
        wt += Const.APP_NAME + " v" + Const.VERSION;
        return wt;
    }


    public static void setTitle(String windowTitle) {
        if (inst == null || inst.window == null) return;

        inst.window.frame.setTitle(windowTitle);
    }


    /** Set status text */
    public static void setStatus(String s) {
        if (inst == null || inst.window == null) return;

        inst.window.setStatus(s);
    }
}
