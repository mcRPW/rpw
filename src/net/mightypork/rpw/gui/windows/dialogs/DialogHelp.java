package net.mightypork.rpw.gui.windows.dialogs;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import net.mightypork.rpw.App;
import net.mightypork.rpw.gui.Gui;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.widgets.VBox;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.help.HelpPage;
import net.mightypork.rpw.help.HelpStore;


public class DialogHelp extends RpwDialog {

    private JButton buttonOK;


    public DialogHelp() {
        super(App.getFrame(), "Quick Guide");

        createDialog();
    }


    @Override
    protected JComponent buildGui() {
        setResizable(true);
        setPreferredSize(900, 600);

        final VBox vb = new VBox();
        vb.windowPadding();

        vb.heading("RPW Guide Book");

        vb.titsep("Help Topics");
        vb.gap();

        final JTabbedPane tabPane = new JTabbedPane(SwingConstants.LEFT);
        tabPane.setAlignmentX(0.5f);

        // build help pages

        for (final HelpPage hp : HelpStore.getPages()) {
            // build one page
            final JScrollPane panel = new JScrollPane();
            panel.getVerticalScrollBar().setUnitIncrement(16);
            panel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            panel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            panel.setAlignmentY(0);

            final JTextPane page = new JTextPane();
            page.setBackground(Color.WHITE);
            page.setOpaque(true);
            page.setAlignmentY(0);
            page.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            page.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
            page.setContentType("text/html");
            page.setEditable(false);

            page.setText(hp.getContent());
            panel.setViewportView(page);

            tabPane.add(hp.getName(), panel);

            // move the scrollbar to top
            // http://stackoverflow.com/questions/1166072/setting-scroll-bar-on-a-jscrollpane
            javax.swing.SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    panel.getVerticalScrollBar().setValue(0);
                }
            });

        }

        vb.add(tabPane);

        vb.gapl();

        buttonOK = new JButton("Close", Icons.MENU_EXIT);
        vb.buttonRow(Gui.RIGHT, buttonOK);

        return vb;
    }


    @Override
    protected void addActions() {
        setEnterButton(buttonOK);
        buttonOK.addActionListener(closeListener);
    }

}
