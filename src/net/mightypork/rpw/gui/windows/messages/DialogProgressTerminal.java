package net.mightypork.rpw.gui.windows.messages;

import java.util.logging.Level;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import net.mightypork.rpw.App;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.widgets.HBox;
import net.mightypork.rpw.tasks.sequences.ProgressListener;
import net.mightypork.rpw.utils.logging.Log;
import net.mightypork.rpw.utils.logging.LogMonitor;


public class DialogProgressTerminal extends DialogTerminalBase implements LogMonitor, ProgressListener {

    private int monitorId;
    private final String heading;
    private JButton btnClose;
    private JProgressBar progress;
    private JLabel stepName;


    public DialogProgressTerminal(String title, String heading) {
        super(App.getFrame(), title);

        this.heading = heading;

        setModalityType(ModalityType.MODELESS);

        setCloseable(false);

        createDialog();
    }


    @Override
    protected void addStuffAboveTextarea(Box vb) {
        vb.add(Box.createVerticalStrut(10));

        vb.add(stepName = new JLabel("Doing some stuff."));
        stepName.setHorizontalAlignment(0);
        stepName.setAlignmentX(CENTER_ALIGNMENT);

        vb.add(Box.createVerticalStrut(5));

        final HBox hb = new HBox();
        hb.padding(5, 5, 5, 5);
        hb.add(progress = new JProgressBar());
        progress.setStringPainted(true);
        progress.setAlignmentX(CENTER_ALIGNMENT);

        vb.add(hb);

    }


    @Override
    protected void onShown() {
        Alerts.loading(true);

        monitorId = Log.addMonitor(this);
    }


    @Override
    protected void addActions() {
        addCloseHook(new Runnable() {

            @Override
            public void run() {
                Log.removeMonitor(monitorId);
                Alerts.loading(false);
            }
        });

        btnClose.addActionListener(closeListener);
    }


    @Override
    protected String getHeadingText() {
        return heading;
    }


    @Override
    protected String getLogText() {
        return "";
    }


    @Override
    protected boolean hasButtons() {
        return true;
    }


    public void allowClose(boolean state, boolean success) {
        btnClose.setEnabled(state);
        setCloseable(state);
        stepName.setText(success ? "Done." : "Failed.");
    }


    @Override
    protected JButton[] makeButtons() {
        btnClose = new JButton("Close", Icons.MENU_EXIT);
        btnClose.setEnabled(false);

        return new JButton[]{btnClose};
    }


    @Override
    public void log(Level level, String message) {
        textArea.append(message);
        textArea.setCaretPosition(textArea.getDocument().getLength());
        textArea.setFocusable(false);
        textArea.setFocusable(true);
    }


    @Override
    public void onStepStarted(int index, int total, String name) {
        stepName.setText(name);
        progress.setValue((int) Math.round(((double) index / (double) (total - 1)) * 100));
    }


    public void stopMonitoring() {
        Log.removeMonitor(monitorId);
    }

}
