package net.mightypork.rpw.gui.widgets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JSeparator;

import net.mightypork.rpw.gui.Gui;


public abstract class RpwBox extends Box {

    public RpwBox(int axis) {
        super(axis);
    }


    public void gap() {
        gap_small();
    }


    public void gapl() {
        gap_large();
    }


    public abstract void gap_small();


    public abstract void gap_large();


    public abstract void glue();


    public void windowPadding() {
        setBorder(Gui.winbdr());
    }


    public void etchbdr() {
        setBorder(Gui.etchbdr());
    }


    public void padding(int top, int left, int bottom, int right) {
        setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
    }


    public abstract JSeparator sep();
}
