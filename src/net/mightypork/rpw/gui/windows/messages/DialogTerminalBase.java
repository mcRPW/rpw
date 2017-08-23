package net.mightypork.rpw.gui.windows.messages;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import net.mightypork.rpw.gui.Gui;
import net.mightypork.rpw.gui.widgets.VBox;
import net.mightypork.rpw.gui.windows.RpwDialog;


public abstract class DialogTerminalBase extends RpwDialog {

    protected JTextArea textArea;


    protected abstract String getHeadingText();


    protected abstract String getLogText();


    protected abstract boolean hasButtons();


    protected abstract JButton[] makeButtons();


    public DialogTerminalBase(Frame parent, String title) {
        super(parent, title);

    }


    @Override
    protected final JComponent buildGui() {
        final VBox vb = new VBox();
        vb.windowPadding();

        vb.heading(getHeadingText());

        textArea = new JTextArea(getLogText(), 25, 80);
        textArea.setFont(new Font(Font.MONOSPACED, 0, 14));
        textArea.setMargin(new Insets(10, 10, 10, 10));
        textArea.setEditable(false);
        textArea.setLineWrap(false);
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.LIGHT_GRAY);

        final JScrollPane sp = new JScrollPane(textArea);

        //@formatter:off
        sp.setBorder(BorderFactory.createEtchedBorder());
        //@formatter:on

        sp.setWheelScrollingEnabled(true);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        addStuffAboveTextarea(vb);

        vb.add(sp);

        if (hasButtons()) {
            vb.gapl();
            vb.buttonRow(Gui.CENTER, makeButtons());
        }

        return vb;
    }


    /**
     * Add optional stuff above the textarea
     *
     * @param vb main vertical box
     */
    protected void addStuffAboveTextarea(Box vb) {
    }


    @Override
    protected abstract void addActions();

}
