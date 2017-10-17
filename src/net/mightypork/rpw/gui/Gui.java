package net.mightypork.rpw.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import net.mightypork.rpw.Config;
import net.mightypork.rpw.Const;
import net.mightypork.rpw.gui.widgets.HBox;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.utils.SpringUtilities;
import net.mightypork.rpw.utils.files.DesktopApi;
import net.mightypork.rpw.utils.files.OsUtils;
import net.mightypork.rpw.utils.logging.Log;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXTextField;
import org.jdesktop.swingx.prompt.PromptSupport;


public class Gui {

    public static final int PADDING_TEXTFIELD = 3;
    public static final int GAP = 6;
    public static final int GAPL = 14;
    public static final int WINDOW_PADDING = 12;
    private static final int ETCHBDR_PADDING = 3;
    public static final Color HEADING_COLOR = new Color(0x045A80);
    public static final Color SUBHEADING_COLOR = new Color(0x00294F);
    public static final Color READONLYBG_COLOR = new Color(0xeeeeee);

    public static final ActionListener openUrlListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            DesktopApi.browse(URI.create(e.getActionCommand()));
        }
    };

    public static final int LEFT = -1;
    public static final int CENTER = 0;
    public static final int RIGHT = 1;


    public static void forceSize(Component c, int x, int y) {
        final Dimension d = new Dimension(x, y);
        c.setMinimumSize(d);
        c.setMaximumSize(d);
        c.setPreferredSize(d);
    }


    public static void setPrefWidth(JComponent component, int width) {
        final Dimension d = component.getPreferredSize();
        d.width = width;
        component.setPreferredSize(d);
    }


    public static void setPrefHeight(JComponent component, int height) {
        final Dimension d = component.getPreferredSize();
        d.height = height;
        component.setPreferredSize(d);
    }


    public static void setMinWidth(JComponent component, int width) {
        final Dimension d = component.getMinimumSize();
        d.width = width;
        component.setMinimumSize(d);
    }


    public static void setMinHeight(JComponent component, int height) {
        final Dimension d = component.getMinimumSize();
        d.width = height;
        component.setMinimumSize(d);
    }


    public static void setMaxWidth(JComponent component, int width) {
        final Dimension d = component.getMaximumSize();
        d.width = width;
        component.setMaximumSize(d);
    }


    public static void setMaxHeight(JComponent component, int height) {
        final Dimension d = component.getMaximumSize();
        d.width = height;
        component.setMaximumSize(d);
    }


    public static void centerWindow(Component window, Component parent) {
        try {
            if (parent == null) {
                final Point p = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
                window.setLocation(p.x - window.getWidth() / 2, p.y - window.getHeight() / 2);
            } else {
                final Point point = parent.getLocationOnScreen();
                final Dimension dim = parent.getSize();
                window.setLocation(point.x + (dim.width - window.getWidth()) / 2, point.y + (dim.height - window.getHeight()) / 2);
            }
        } catch (final IllegalStateException e) {
            // meh
            Log.e("Failed to center window.", e);
        }
    }


    public static JComponent createDialogHeading(String text) {
        final JLabel title = new JLabel(text);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        title.setForeground(HEADING_COLOR);
        title.setBorder(BorderFactory.createEmptyBorder(0, GAPL, GAPL, GAPL));
        title.setAlignmentX(0.5f);

        return title;
    }


    public static void open(RpwDialog dialog) {
        dialog.openDialog();
    }


    public static JTextField textField() {
        return textField(null, null, null);
    }


    public static JTextField textField(String text) {
        return textField(text, null, null);
    }


    public static JTextField textField(String text, String placeholder) {
        return textField(text, placeholder, placeholder);
    }


    public static JTextField textField(String text, String placeholder, String tooltip) {
        final int padding = PADDING_TEXTFIELD;

        final JXTextField field = new JXTextField();

        if (!Config.USE_NIMBUS) {
            final Border bdr = BorderFactory.createCompoundBorder(field.getBorder(), BorderFactory.createEmptyBorder(padding, padding, padding, padding));

            field.setBorder(bdr);
        }
        if (text != null) field.setText(text);
        if (tooltip != null) field.setToolTipText(tooltip);
        if (placeholder != null) PromptSupport.setPrompt(placeholder, field);
        return field;
    }

    public static JCheckBox checkbox(boolean selected) {
        return checkbox(selected, "");
    }

    public static JCheckBox checkbox(boolean selected, String text) {
        return checkbox(selected, text, true);
    }

    public static JCheckBox checkbox(boolean selected, String text, boolean enabled) {
        final JCheckBox checkbox = new JCheckBox();
        checkbox.setSelected(selected);
        checkbox.setText(text);
        checkbox.setEnabled(enabled);

        return checkbox;
    }

    public static JXLabel label(String text){
        final JXLabel label = new JXLabel();
        label.setText(text);

        return label;
    }

    public static JXLabel label(String text, int size, int style){
        final JXLabel label = new JXLabel();
        label.setText(text);
        label.setFont(new Font(label.getFont().getName(), style, size));
        label.setAlignmentX(0.5f);

        return label;
    }

    public static Component hgap_small() {
        return Box.createHorizontalStrut(GAP);
    }


    public static Component vgap_small() {
        return Box.createVerticalStrut(GAP);
    }


    public static Component hgap_large() {
        return Box.createHorizontalStrut(GAPL);
    }


    public static Component vgap_large() {
        return Box.createVerticalStrut(GAPL);
    }


    public static Border winbdr() {
        return BorderFactory.createEmptyBorder(WINDOW_PADDING, WINDOW_PADDING, WINDOW_PADDING, WINDOW_PADDING);
    }


    public static Border etchbdr() {
        //@formatter:off
        return new CompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(ETCHBDR_PADDING, ETCHBDR_PADDING, ETCHBDR_PADDING, ETCHBDR_PADDING)
        );
        //@formatter:on
    }


    public static Component hglue() {
        return Box.createHorizontalGlue();
    }


    public static Component vglue() {
        return Box.createVerticalGlue();
    }


    public static JButton sidebarButton(String text, String tooltip, ImageIcon icon) {
        final JButton jb = new JButton(text);
        if (icon != null) jb.setIcon(icon);
        if (tooltip != null) jb.setToolTipText(tooltip);

        jb.setHorizontalAlignment(SwingConstants.LEFT);
        jb.setIconTextGap(Gui.GAP);

        return jb;
    }


    public static JComponent commentLine(String text) {
        final HBox hb = new HBox();
        JLabel l;
        hb.add(l = new JLabel(text));
        l.setFont(l.getFont().deriveFont(11));
        l.setHorizontalAlignment(SwingConstants.CENTER);
        l.setForeground(Color.GRAY);
        return hb;
    }


    public static void useNimbusLaF() {
        try {
            for (final LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    final UIDefaults defaults = UIManager.getLookAndFeelDefaults();
                    defaults.put("Table.alternateRowColor", Const.TABLE_ALT_COLOR);
                    defaults.put("Table.focusCellHighlightBorder", Const.TABLE_CELL_INSETS);
                    defaults.put("TableHeader.cellBorder", Const.TABLE_HEADER_BORDERS);
                    defaults.put("nimbusOrange", new Color(0x1F87B5)); // override for progressbar
                    break;
                }
            }
        } catch (final Exception e) {
            Log.e("Error selecting Nimbus Look&Feel.", e);
        }
    }

    public static void useNativeLaF() {
        try {
            if (OsUtils.isLinux()) {
                // Use GTK
                System.setProperty("swing.defaultlaf", "com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
                System.setProperty("swing.crossplatformlaf", "com.sun.java.swing.plaf.gtk.GTKLookAndFee");
            }

            if (OsUtils.isMac()) {
                // Use the global menubar
                System.setProperty("apple.laf.useScreenMenuBar", "true");
            }

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            Log.e("Error selecting native Look&Feel.");
        }
    }


    public static void useMetal() {
        try {
            for (final LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Metal".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (final Exception e) {
            Log.e("Could not select Look&Feel: Metal", e);
        }
    }


    public static JPanel springForm(Object[] labels, JComponent[] fields) {
        final JPanel p = new JPanel(new SpringLayout());

        if (labels.length != fields.length)
            throw new IllegalArgumentException("Nr. of labels doesn't match nr. of fields.");

        for (int i = 0; i < labels.length; i++) {
            JLabel l = null;

            if (labels[i] instanceof JLabel) {
                l = ((JLabel) labels[i]);
                l.setHorizontalAlignment(SwingConstants.RIGHT);
            } else {
                l = new JXLabel(labels[i].toString(), SwingConstants.RIGHT);
            }

            p.add(l);
            l.setLabelFor(fields[i]);
            p.add(fields[i]);
        }

        SpringUtilities.makeCompactGrid(p, labels.length, 2, 0, 0, Gui.GAP, Gui.GAP);

        return p;
    }


    public static void readonly(JTextField textfeld, boolean readonly) {
        textfeld.setEditable(!readonly);

        if (readonly) {
            textfeld.putClientProperty("rpw.originalBackground", textfeld.getBackground());
            textfeld.setBackground(READONLYBG_COLOR);
        } else {
            Color c = (Color) textfeld.getClientProperty("rpw.originalBackground");

            if (c == null) c = Color.WHITE;
            textfeld.setBackground(c);
        }
    }


    /**
     * Create a button row
     *
     * @param align   align (Gui.LEFT, Gui.CENTER, Gui.RIGHT)
     * @param buttons buttons, null for separator
     * @return the box
     */
    public static HBox buttonRow(int align, JButton... buttons) {
        final HBox hb = new HBox();

        if (buttons == null) return hb;

        if (align == RIGHT || align == CENTER) hb.glue();

        for (int i = 0; i < buttons.length; i++) {
            if (i > 0) hb.gap();

            if (buttons[i] == null) {
                hb.gap();
                hb.sep();
                hb.gap();

            } else {
                hb.add(buttons[i]);
            }
        }

        if (align == LEFT || align == CENTER) hb.glue();

        return hb;
    }


    public static void titledBorder(JComponent c, String title, int padding) {
        //@formatter:off
        c.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder(title),
                        BorderFactory.createEmptyBorder(padding, padding, padding, padding)
                )
        );
        //@formatter:on
    }

}