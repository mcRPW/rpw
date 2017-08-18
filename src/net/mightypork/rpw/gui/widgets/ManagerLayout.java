package net.mightypork.rpw.gui.widgets;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.mightypork.rpw.gui.Gui;


public class ManagerLayout extends HBox {

    private VBox left = null;
    private JPanel right = null;
    private JButton[] topButtons = new JButton[0];
    private JButton[] bottomButtons = new JButton[0];
    private int maxButtons = 0;


    public ManagerLayout() {
        this(6);
    }


    public ManagerLayout(int maxButtons) {
        left = new VBox();

        right = new JPanel();
        right.setLayout(new GridLayout(maxButtons, 1, Gui.GAP, Gui.GAP));

        this.maxButtons = maxButtons;

        add(left);
        gapl();
        add(right);
    }


    public void setMainComponent(Component c) {
        left.add(c);
    }


    public void setTopButtons(JButton... btns) {
        topButtons = btns;
    }


    public void setBottomButtons(JButton... btns) {
        bottomButtons = btns;
    }


    public void build() {
        if (topButtons.length + bottomButtons.length > maxButtons) {
            throw new IllegalArgumentException("Buttons can't fit into the layout!");
        }

        final Component[] bb = new Component[maxButtons];

        // add top buttons
        if (topButtons != null) {
            int cnt = 0;
            for (final JButton b : topButtons) {
                bb[cnt++] = b;
            }
        }

        // add bottom buttons
        if (bottomButtons != null) {
            int cnt = maxButtons - bottomButtons.length;
            for (final JButton b : bottomButtons) {
                bb[cnt++] = b;
            }
        }

        // add gaps to remaining slots
        for (int i = 0; i < bb.length; i++) {
            if (bb[i] == null) bb[i] = Gui.hgap_large();

            right.add(bb[i]);
        }
    }

}
