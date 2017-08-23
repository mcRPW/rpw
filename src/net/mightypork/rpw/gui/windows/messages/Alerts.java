package net.mightypork.rpw.gui.windows.messages;

import java.awt.Component;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;

import net.mightypork.rpw.App;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.utils.logging.Log;


public class Alerts {

    private static boolean loadingState = false;
    private static boolean loadingStateDisplayed = false;
    private static Timer t = new Timer();


    public static void error(Component c, String message) {
        error(c, "Error", message);
    }


    public static void error(Component c, String title, String message) {
        if (c == null) c = App.getFrame();

        Log.e(message);

        //@formatter:off
        JOptionPane.showMessageDialog(
                c,
                message,
                title,
                JOptionPane.ERROR_MESSAGE,
                Icons.DIALOG_ERROR
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
                JOptionPane.WARNING_MESSAGE,
                Icons.DIALOG_WARNING
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
                JOptionPane.INFORMATION_MESSAGE,
                Icons.DIALOG_INFORMATION
        );
        //@formatter:on
    }


    public static void loading(final boolean waiting) {
        loadingState = waiting;

        if (loadingStateDisplayed == loadingState) return; // no change.

        t.schedule(new TimerTask() {

            @Override
            public void run() {
                updateLoadingVisuals();
            }
        }, 60);
    }


    private static void updateLoadingVisuals() {
        if (loadingStateDisplayed == loadingState) {
            return; // nothing to change
        }

        App.setWaiting(loadingState);

        loadingStateDisplayed = loadingState;
    }


    public static boolean askYesNo(Component c, String title, String message) {
        //@formatter:off
        return JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(
                c,
                message,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                Icons.DIALOG_QUESTION
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
                JOptionPane.QUESTION_MESSAGE,
                Icons.DIALOG_QUESTION
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
                JOptionPane.QUESTION_MESSAGE,
                Icons.DIALOG_QUESTION
        );
        //@formatter:on
    }


    public static String askForInput(Component c, String title, String message, String initial) {
        //@formatter:off
        return (String) JOptionPane.showInputDialog(
                c,
                message,
                title,
                JOptionPane.QUESTION_MESSAGE,
                Icons.DIALOG_QUESTION,
                null,
                initial
        );
        //@formatter:on

    }
}
