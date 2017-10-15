package net.mightypork.rpw.gui.windows;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;

import net.mightypork.rpw.App;
import net.mightypork.rpw.gui.Gui;
import net.mightypork.rpw.gui.helpers.WindowCloseListener;
import net.mightypork.rpw.utils.logging.Log;


public abstract class RpwDialog extends JDialog {

    private boolean onCloseCalled = false;
    private boolean closingByCommand = false;

    private boolean closeable = true;

    private final List<Runnable> closeHooks = new ArrayList<Runnable>();


    public void addCloseHook(Runnable hook) {
        this.closeHooks.add(hook);
    }

    protected final ActionListener closeListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            closeDialog();
        }
    };

    private final WindowCloseListener closeWindowListener = new WindowCloseListener() {

        @Override
        public void onClose(WindowEvent e) {
            if (!onCloseCalled && (closeable || closingByCommand)) {
                onCloseCalled = true;
                RpwDialog.this.onClose();
                afterOnClose();
            }
        }
    };
    private final Frame dlgParent;


    public void setCloseable(boolean closeable) {
        this.closeable = closeable;

        setDefaultCloseOperation(closeable ? DISPOSE_ON_CLOSE : DO_NOTHING_ON_CLOSE);
    }


    public boolean isCloseable() {
        return closeable;
    }


    public void forceGetFocus() {
        requestFocus();

        ModalityType mt = getModalityType();

        setModalityType(ModalityType.APPLICATION_MODAL);
        setModalityType(mt);

        if (!isFocused()) {
            setVisible(false);
            setVisible(true);
        }
    }


    /**
     * @param parent parent frame
     * @param title  window title
     */
    public RpwDialog(Frame parent, String title) {
        super(parent, title);

        this.dlgParent = parent;

        // don't use -> fix weird windoze bug (?)
        //setAlwaysOnTop(parent == null); // initial load dialog

        setModal(true);
        setResizable(false);
        setLocationRelativeTo(parent);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        addWindowListener(closeWindowListener);

        App.activeDialog = this;
    }


    /**
     * Close it
     */
    public final void closeDialog() {
        closingByCommand = true;

        dispose();

        if (!onCloseCalled) {
            onCloseCalled = true;
            onClose();
            afterOnClose();
        }

        App.activeDialog = null;
    }


    /**
     * Convention method to set visible
     */
    public final void openDialog() {
        setVisible(true);
    }


    /**
     * Create dialog: build GUI, center dialog, set visible etc
     */
    public final void createDialog() {
        final JComponent jc = buildGui();

        if (jc != null) getContentPane().add(jc);

        initGui();

        pack();

        Gui.centerWindow(this, dlgParent);

        addActions();

        getRootPane().registerKeyboardAction(closeListener, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }


    /**
     * Build the GUI
     *
     * @return root component (typically JPanel or Box), or null if the GUI
     * elements were already added to the content pane
     */
    protected abstract JComponent buildGui();


    /**
     * Called after buildGui() and before pack()
     */
    protected void initGui() {
    }


    /**
     * Set button pressed on ENTER press
     *
     * @param button btn
     */
    protected void setEnterButton(JButton button) {
        getRootPane().setDefaultButton(button);
    }


    @Override
    public void setVisible(boolean b) {
        onShown();

        Log.f3("Dialog open: " + getTitle());
        super.setVisible(b);
    }


    /**
     * Called after setVisible was executed
     */
    protected void onShown() {
    }


    /**
     * Here the dialog should add actions to the UI components
     */
    protected abstract void addActions();


    /**
     * To be used in enclosed types instead of weird stuff with
     * DialogXXX.this.something()
     *
     * @return this
     */
    protected final RpwDialog self() {
        return this;
    }


    /**
     * Called after onClose - run the hooks
     */
    private void afterOnClose() {
        Log.f3("Dialog closed: " + getTitle());

        for (final Runnable hook : closeHooks) {
            hook.run();
        }
    }


    /**
     * Called right after dialog closes
     */
    protected void onClose() {
    }


    protected void setPreferredSize(int i, int j) {
        setPreferredSize(new Dimension(i, j));
    }

}
