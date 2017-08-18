package net.mightypork.rpw.gui.helpers;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;


public abstract class WindowCloseListener implements WindowListener {

    @Override
    public void windowOpened(WindowEvent e) {
    }


    @Override
    public void windowIconified(WindowEvent e) {
    }


    @Override
    public void windowDeiconified(WindowEvent e) {
    }


    @Override
    public void windowDeactivated(WindowEvent e) {
    }


    @Override
    public void windowClosing(WindowEvent e) {
        onClose(e);
    }


    public abstract void onClose(WindowEvent e);


    @Override
    public void windowClosed(WindowEvent e) {
        onClose(e);
    }


    @Override
    public void windowActivated(WindowEvent e) {
    }
}
