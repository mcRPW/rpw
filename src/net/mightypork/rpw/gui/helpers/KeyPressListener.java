package net.mightypork.rpw.gui.helpers;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


/**
 * @author Ondřej Hruška (MightyPork)
 */
public abstract class KeyPressListener implements KeyListener {

    @Override
    public void keyTyped(KeyEvent e) {
    }


    @Override
    public abstract void keyPressed(KeyEvent e);


    @Override
    public void keyReleased(KeyEvent e) {
    }

}
