package net.mightypork.rpw.gui.widgets;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import net.mightypork.rpw.gui.Gui;

import org.jdesktop.swingx.JXTitledSeparator;


public class VBox extends RpwBox {

    public VBox() {
        super(BoxLayout.Y_AXIS);
    }


    @Override
    public void gap_small() {
        add(Gui.vgap_small());
    }


    @Override
    public void gap_large() {
        add(Gui.vgap_large());
    }


    @Override
    public void glue() {
        add(Gui.vglue());
    }


    /**
     * Add titled separator
     *
     * @param string title text
     * @return titled separator (for further tweaks)
     */
    public JXTitledSeparator titsep(String string) {
        JXTitledSeparator s;
        add(s = new JXTitledSeparator(string));
        s.setForeground(Gui.HEADING_COLOR);
        return s;
    }


    @Override
    public JSeparator sep() {
        JSeparator sep;
        add(sep = new JSeparator(SwingConstants.HORIZONTAL));
        return sep;
    }


    public void heading(String string) {
        add(Gui.createDialogHeading(string));
    }


    public void buttonRow(int align, JButton... buttons) {
        if (buttons == null) return;

        add(Gui.buttonRow(align, buttons));

    }


    public void springForm(Object[] strings, JComponent[] jComponents) {
        add(Gui.springForm(strings, jComponents));

    }

}
