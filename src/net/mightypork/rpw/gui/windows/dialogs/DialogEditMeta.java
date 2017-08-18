package net.mightypork.rpw.gui.windows.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.helpers.ClickListener;
import net.mightypork.rpw.gui.widgets.HBox;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.tree.assets.tree.AssetTreeLeaf;
import net.mightypork.rpw.utils.Utils;
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.logging.Log;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import com.google.gson.JsonParser;


public class DialogEditMeta extends DialogEditorBase {

    private JButton btnCancel;
    private JButton btnSave;
    private JButton btnPresets;
    private JButton btnCheck;
    private JPopupMenu presetsPopup;
    protected AssetTreeLeaf editedNode;
    private String dlgHeading;


    public DialogEditMeta(AssetTreeLeaf node) {
        this.editedNode = node;

        createDialog();
    }


    @Override
    protected String getTitleText() {
        final String path = Utils.fromLastChar(editedNode.getAssetEntry().getPath(), '/');

        dlgHeading = path + ".mcmeta";

        return path + " - RPW McMeta editor";
    }


    @Override
    protected String getInitialText() {
        try {
            final InputStream in = Projects.getActive().getAssetMetaStream(editedNode.getAssetKey());
            return FileUtils.streamToString(in);
        } catch (final IOException e) {
            Log.e(e);
            return "";
        }
    }


    @Override
    protected void buildButtons(HBox buttons) {
        btnPresets = new JButton("Templates", Icons.MENU_GENERATE);
        btnCheck = new JButton("Check JSON", Icons.MENU_INFO);
        btnCancel = new JButton("Discard", Icons.MENU_CANCEL);
        btnSave = new JButton("Save", Icons.MENU_YES);

        buttons.add(btnPresets);
        buttons.gap();
        buttons.add(btnCheck);
        buttons.glue();
        buttons.add(btnSave);
        buttons.gap();
        buttons.add(btnCancel);

        presetsPopup = buildPresetsPopup();
        buttons.add(presetsPopup);
    }


    private JPopupMenu buildPresetsPopup() {
        final JPopupMenu popup = new JPopupMenu();

        JMenu menu2, menu3;

        JMenuItem item;

        popup.add(menu2 = new JMenu("Animation"));

        final int[] frames = {8, 16, 20, 32, 64};

        for (final int i : frames) {
            menu2.add(menu3 = new JMenu(i + " frames"));

            menu3.add(item = new JMenuItem("Linear"));
            item.setActionCommand("animation/" + i + "linear.txt");
            item.addActionListener(loadTemplateListener);

            menu3.add(item = new JMenuItem("Reverse"));
            item.setActionCommand("animation/" + i + "reverse.txt");
            item.addActionListener(loadTemplateListener);

            menu3.add(item = new JMenuItem("ZigZag"));
            item.setActionCommand("animation/" + i + "zigzag.txt");
            item.addActionListener(loadTemplateListener);
        }

        menu2.add(menu3 = new JMenu("Vanilla"));

        //@formatter:off
        final String[] anims = {
                "clock",
                "compass",
                "fire_layer_0",
                "fire_layer_1",
                "lava_flow",
                "lava_still",
                "portal",
                "water_flow",
                "water_still"
        };
        //@formatter:on

        for (final String a : anims) {
            menu3.add(item = new JMenuItem(a));
            item.setActionCommand("animation/" + a + ".txt");
            item.addActionListener(loadTemplateListener);
        }

        menu2.addSeparator();

        menu2.add(item = new JMenuItem("Default"));
        item.setActionCommand("animation/default_animation.txt");
        item.addActionListener(loadTemplateListener);

        popup.add(menu2 = new JMenu("Texture"));

        menu2.add(menu3 = new JMenu("Vanilla"));

        //@formatter:off
        final String[] textures = {
                "enchanted_item_glint",
                "pumpkinblur",
                "shadow",
                "vignette"
        };
        //@formatter:on

        for (final String t : textures) {
            menu3.add(item = new JMenuItem(t));
            item.setActionCommand("texture/" + t + ".txt");
            item.addActionListener(loadTemplateListener);
        }

        menu2.addSeparator();

        menu2.add(item = new JMenuItem("Default"));
        item.setActionCommand("texture/default_texture.txt");
        item.addActionListener(loadTemplateListener);

        menu2.addSeparator();

        menu2.add(item = new JMenuItem("Blur"));
        item.setActionCommand("texture/blur.txt");
        item.addActionListener(loadTemplateListener);

        menu2.add(item = new JMenuItem("Clamp"));
        item.setActionCommand("texture/clamp.txt");
        item.addActionListener(loadTemplateListener);

        return popup;
    }


    @Override
    protected void addActions() {
        btnCancel.addActionListener(closeListener);

        btnCheck.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final String text = getTextArea().getText();

                final JsonParser jp = new JsonParser();

                try {
                    jp.parse(text);
                    Alerts.info(DialogEditMeta.this, "Check JSON", "Entered code is (probably) valid.");
                } catch (final Exception er) {
                    Alerts.warning(DialogEditMeta.this, "Check JSON", "Entered code contains\n a SYNTAX ERROR!");
                }

            }
        });

        btnSave.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final String text = getTextArea().getText();

                final File file = Projects.getActive().getAssetMetaFile(editedNode.getAssetKey());

                try {
                    FileUtils.stringToFile(file, text);
                    closeDialog();
                } catch (final IOException e1) {
                    Log.e(e1);

                    Alerts.error(self(), "Could not save file.");

                }
            }
        });

        btnPresets.addMouseListener(new ClickListener() {

            boolean first = true;


            @Override
            public void mouseClicked(MouseEvent e) {
                if (first) {
                    presetsPopup.show(getButtonsBox(), 0, 0);
                    presetsPopup.setVisible(false);
                    first = false;
                }

                presetsPopup.show(getButtonsBox(), btnPresets.getBounds().x, btnPresets.getBounds().y - presetsPopup.getHeight());
            }
        });
    }

    private final ActionListener loadTemplateListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            final String res = e.getActionCommand();

            final String text = FileUtils.resourceToString("/data/mcmeta/" + res);

            setTextareaText(text);
        }
    };


    @Override
    protected void configureTextarea(RSyntaxTextArea ta) {
        configureTextareaJSON(ta);
    }


    @Override
    protected String getFileName() {
        return dlgHeading;
    }

}
