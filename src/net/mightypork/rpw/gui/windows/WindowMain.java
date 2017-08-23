package net.mightypork.rpw.gui.windows;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Config;
import net.mightypork.rpw.gui.Gui;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.helpers.WindowCloseListener;
import net.mightypork.rpw.gui.widgets.MenuMain;
import net.mightypork.rpw.gui.widgets.SidePanel;
import net.mightypork.rpw.gui.widgets.TreeDisplay;
import net.mightypork.rpw.tasks.Tasks;

import net.mightypork.rpw.utils.logging.Log;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXStatusBar;
import org.jdesktop.swingx.plaf.basic.BasicStatusBarUI;


public class WindowMain {
    public JXFrame frame;
    public MenuMain menu;
    public TreeDisplay treeDisplay;
    public SidePanel sidePanel;
    private JXStatusBar statusbar;
    private JXLabel sblabel;

    public WindowMain() {
        frame = new JXFrame(App.getWindowTitle());
        frame.setIconImage(Icons.WINDOW.getImage());

        statusbar  = new JXStatusBar();
        frame.setStatusBar(statusbar);
        statusbar.putClientProperty(BasicStatusBarUI.AUTO_ADD_SEPARATOR, true);
        statusbar.add(sblabel = new JXLabel(""));
        setStatus(null);

        menu = new MenuMain();

        frame.add(buildMainPanel());
        frame.setJMenuBar(menu.menuBar);

        // prevent default close operation
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.pack();

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


    /**
     * Set stayusbar text
     * @param s - text to show
     */
    public void setStatus(String s) {
        if (s == null || s.length()==0) s = " ";
        sblabel.setText(s);
    }

    private Component buildMainPanel() {
        if (Config.USE_NIMBUS) Gui.useMetal();

        treeDisplay = new TreeDisplay();

        if (Config.USE_NIMBUS) Gui.useNimbusLaF();

        final JScrollPane scrollpane = new JScrollPane(treeDisplay.treeTable);

        scrollpane.setPreferredSize(new Dimension(700, 600));
        scrollpane.setMinimumSize(new Dimension(500, 300));

        sidePanel = new SidePanel();

        final JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, scrollpane, sidePanel.panel);

        split.setOneTouchExpandable(true);
        split.setEnabled(false); // no dragging
        split.setResizeWeight(1);

        return split;
    }


    public void setWaiting(boolean state) {
        try {
            App.getFrame().setWaitCursorVisible(state);
            App.getFrame().setWaiting(state);
            App.getFrame().setWaitPaneVisible(state);
        } catch (NullPointerException npe) {
            Log.e(npe);
        }
    }

}
