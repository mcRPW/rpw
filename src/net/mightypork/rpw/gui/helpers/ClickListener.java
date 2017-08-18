package net.mightypork.rpw.gui.helpers;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public abstract class ClickListener implements MouseListener {

    @Override
    public abstract void mouseClicked(MouseEvent e);


    @Override
    public final void mousePressed(MouseEvent e) {
    }


    @Override
    public final void mouseReleased(MouseEvent e) {
    }


    @Override
    public final void mouseEntered(MouseEvent e) {
    }


    @Override
    public final void mouseExited(MouseEvent e) {
    }

}
