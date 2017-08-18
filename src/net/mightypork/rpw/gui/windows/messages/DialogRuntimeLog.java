package net.mightypork.rpw.gui.windows.messages;

import javax.swing.JButton;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Paths;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.files.OsUtils;
import net.mightypork.rpw.utils.logging.Log;


public class DialogRuntimeLog extends DialogTerminalBase {

    private JButton btnClose;


    public DialogRuntimeLog() {
        super(App.getFrame(), "Runtime log");

        createDialog();
    }


    @Override
    protected void addActions() {
        btnClose.addActionListener(closeListener);
    }


    @Override
    protected String getHeadingText() {
        return "Runtime Log";
    }


    @Override
    protected String getLogText() {
        String txt = "Not found.";

        try {
            txt = FileUtils.fileToString(OsUtils.getAppDir(Paths.FILE_LOG));
        } catch (final Exception e) {
            Log.e("Error getting log.", e);
        }
        return txt;
    }


    @Override
    protected boolean hasButtons() {
        return true;
    }


    @Override
    protected JButton[] makeButtons() {
        btnClose = new JButton("Close", Icons.MENU_EXIT);

        return new JButton[]{btnClose};
    }

}
