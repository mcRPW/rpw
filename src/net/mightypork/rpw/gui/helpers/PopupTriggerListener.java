package net.mightypork.rpw.gui.helpers;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


/**
 * Listens only to right-click & similar
 *
 * @author Ondřej Hruška (MightyPork)
 */
public abstract class PopupTriggerListener implements MouseListener {

    @Override
    public final void mouseClicked(MouseEvent e) {
        if (e.isPopupTrigger()) {
            onPopupTrigger(e);
        }
    }


    public abstract void onPopupTrigger(MouseEvent e);


    @Override
    public final void mousePressed(MouseEvent e) {
        if (e.isPopupTrigger()) {
            onPopupTrigger(e);
        }
    }


    @Override
    public final void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            onPopupTrigger(e);
        }
    }


    @Override
    public final void mouseEntered(MouseEvent e) {
    }


    @Override
    public final void mouseExited(MouseEvent e) {
    }

}
