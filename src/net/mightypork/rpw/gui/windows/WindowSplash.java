package net.mightypork.rpw.gui.windows;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Config;
import net.mightypork.rpw.Const;
import net.mightypork.rpw.gui.Gui;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.helpers.WindowCloseListener;
import net.mightypork.rpw.gui.widgets.*;
import net.mightypork.rpw.tasks.Tasks;
import net.mightypork.rpw.utils.logging.Log;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

/**
 * Splash frame, used to claim taskbar entry & root dialogs.
 */
public class WindowSplash {
    public JXFrame frame;

    public WindowSplash() {
        frame = new JXFrame(Const.APP_NAME_SHORT + " " + Const.VERSION);
        frame.setIconImage(Icons.WINDOW.getImage());

        // prevent default close operation
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setResizable(false);

        frame.getContentPane().setBackground(new Color(60, 60, 60));

        // Create the splash stuff
        final VBox vb = new VBox();
        JXLabel icn = new JXLabel(Icons.WINDOW);
        icn.setAlignmentX(.5f);
        vb.add(icn);
        vb.add(Box.createVerticalStrut(6));
        JXLabel lbl = new JXLabel("RPW is loading...");
        Font f = lbl.getFont();
        lbl.setFont(f.deriveFont(f.getStyle() | Font.BOLD));
        lbl.setForeground(new Color(192, 192, 192));
        lbl.setAlignmentX(.5f);
        vb.add(lbl);
        vb.setBorder(BorderFactory.createEmptyBorder(10, 10, 6, 10));
        frame.add(vb);

        // pack frame
        frame.pack();

        // Center
        final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((dim.width - frame.getWidth()) / 2, (dim.height - frame.getHeight()) / 2);

        frame.setVisible(true);

        frame.addWindowListener(new WindowCloseListener() {
            @Override
            public void onClose(WindowEvent e) {
                Tasks.taskExit();
            }
        });

        App.inst.mainFrame = frame;
    }

    public void hide() {
        frame.setVisible(false);
    }
}
