package net.mightypork.rpw.gui.windows.dialogs;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;

import com.sun.prism.paint.Color;
import net.mightypork.rpw.App;
import net.mightypork.rpw.Const;
import net.mightypork.rpw.Paths;
import net.mightypork.rpw.gui.Gui;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.widgets.VBox;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.utils.files.DesktopApi;
import net.mightypork.rpw.utils.logging.Log;


public class DialogAbout extends RpwDialog {

    private JButton buttonOK;
    private JButton buttonWebsite;
    private JButton buttonGitHub;
    private JButton buttonTwitterRpw;


    public DialogAbout() {
        super(App.getFrame(), "About");

        createDialog();
    }


    @Override
    protected JComponent buildGui() {
        final VBox vb = new VBox();
        vb.windowPadding();

        vb.heading(Const.APP_NAME + " v" + Const.VERSION);

        final JLabel image = new JLabel(Icons.ABOUT);
        image.setAlignmentX(0.5f);
        vb.add(image);

        vb.add(Gui.label("Credits", 15, Font.BOLD));
        vb.add(Gui.label("MightyPork, creator of the app", 13, Font.PLAIN));
        vb.add(Gui.label("MCrafterzz, developer", 13, Font.PLAIN));
        vb.add(Gui.label("AkTheKnight, contributor", 13, Font.PLAIN));
        vb.add(Gui.label("Jakz, creator of the texture (un)stitcher", 13, Font.PLAIN));
        vb.gapl();

        buttonTwitterRpw = new JButton("@RPWapp", Icons.MENU_TWITTER);
        buttonWebsite = new JButton("Website", Icons.MENU_WEBSITE);
        buttonGitHub = new JButton("GitHub", Icons.MENU_GITHUB);
        buttonOK = new JButton("Close", Icons.MENU_YES);

        vb.buttonRow(Gui.CENTER, buttonTwitterRpw, buttonGitHub, buttonWebsite, buttonOK);
        buttonOK.requestFocusInWindow();

        return vb;
    }


    @Override
    protected void addActions() {
        buttonOK.addActionListener(closeListener);

        buttonWebsite.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    DesktopApi.browse(new URL(Paths.URL_RPW_WEB).toURI());
                } catch (final Exception err) {
                    Log.e(err);
                }
            }
        });

        buttonGitHub.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    DesktopApi.browse(new URL(Paths.URL_GITHUB_REPO).toURI());
                } catch (final Exception err) {
                    Log.e(err);
                }
            }
        });

        buttonTwitterRpw.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    DesktopApi.browse(new URL("https://twitter.com/RPWapp").toURI());
                } catch (final Exception err) {
                    Log.e(err);
                }
            }
        });

        setEnterButton(buttonOK);
    }
}
