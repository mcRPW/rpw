package net.mightypork.rpw.gui.widgets;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Box.Filler;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;

import net.mightypork.rpw.Config;
import net.mightypork.rpw.gui.Gui;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.helpers.ClickListener;
import net.mightypork.rpw.library.Sources;
import net.mightypork.rpw.project.Project;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.tasks.Tasks;
import net.mightypork.rpw.tree.assets.EAsset;
import net.mightypork.rpw.tree.assets.tree.AssetTreeGroup;
import net.mightypork.rpw.tree.assets.tree.AssetTreeLeaf;
import net.mightypork.rpw.tree.assets.tree.AssetTreeNode;
import net.mightypork.rpw.utils.Utils;
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.logging.Log;

import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTitledSeparator;


public class SidePanel {

    public JXPanel panel;

    private Box infoBox;
    private JXLabel projectName;
    private JXLabel projectBase;
    private JXLabel mcVersion;
    private JButton buttonOpenBase;
    private JButton buttonEditProps;
    private JLabel projectIconLabel;

    private Box previewBox;
    private JPanel previewPanel;
    private CardLayout previewCardLayout;

    private JTextArea previewText;
    private TitledBorder previewTextBorder;

    private JTextArea previewModel;
    private TitledBorder previewModelBorder;

    private JLabel previewImage;
    private TitledBorder previewImageBorder;
    private JPanelWithBackground previewImageBg;

    private JButton btnEditI;
    private JButton btnMetaI;
    private JButton btnReplaceI;

    private JButton btnEditT;
    private JButton btnReplaceT;

    private JButton btnEditA;
    private JButton btnReplaceA;

    private JButton btnEditM;

    private static final String IMAGE = "IMAGE";
    private static final String TEXT = "TEXT";
    private static final String AUDIO = "AUDIO";
    private static final String MODEL = "MODEL";

    private AssetTreeLeaf displayedLeaf;

    private TitledBorder previewAudioBorder;

    private Border previewBorder;


    public SidePanel() {
        JXPanel projectCard = new JXPanel();

        // project-info card
        projectCard.setBorder(BorderFactory.createEmptyBorder(Gui.GAP, Gui.GAPL, Gui.GAP, Gui.GAPL));

        projectCard.setPreferredSize(new Dimension(320, 700));
        projectCard.setMinimumSize(new Dimension(320, 300));

        projectCard.add(infoBox = createProjectInfoBox());
        infoBox.setAlignmentX(0.5f);

        projectCard.add(Box.createRigidArea(new Dimension(300, 20)));

        projectCard.add(previewBox = createPreviewBox());
        previewBox.setAlignmentX(0.5f);

        projectCard.add(Box.createVerticalGlue());

        // no-project-open card
        JXPanel empty = new JXPanel();
        VBox vb = new VBox();

        JLabel l1 = new JLabel("No project active.");
        l1.setFont(l1.getFont().deriveFont(20F));

        JLabel l2 = new JLabel("Use the menu to open\n or create a project.");
        vb.add(l1);
        vb.add(l2);

        empty.add(vb);

        panel = new JXPanel();
        panel.setLayout(new CardLayout());
        panel.add(projectCard, "project");
        panel.add(empty, "empty");

        addActions();

        updateProjectInfo();
        redrawPreview();
    }


    public void showCard(String name) {
        CardLayout cl = (CardLayout) (panel.getLayout());
        cl.show(panel, name);
    }


    private Box createPreviewBox() {
        previewBorder = new CompoundBorder(BorderFactory.createLineBorder(new Color(0x666666)), BorderFactory.createEmptyBorder(5, 5, 5, 5));

        //@formatter:off
        VBox vbox, vb2;

        vbox = new VBox();
        final JXTitledSeparator s = new JXTitledSeparator("Selected Item Preview");
        s.setAlignmentX(0);
        vbox.add(s);

        final Filler f = (Filler) Box.createRigidArea(new Dimension(305, 1));
        f.setAlignmentX(0);
        vbox.add(f);

        HBox hb;

        previewPanel = new JPanel(previewCardLayout = new CardLayout());
        previewPanel.setAlignmentX(0);
        previewCardLayout.setVgap(0);


        vb2 = new VBox();
        vb2.setAlignmentX(0);

        vb2.gap();

        hb = new HBox();
        hb.setAlignmentX(0);

        btnEditI = new JButton("Edit", Icons.MENU_EDIT);
        btnMetaI = new JButton("Meta", Icons.MENU_EDIT);
        btnReplaceI = new JButton("Replace", Icons.MENU_IMPORT_BOX);

        hb.glue();
        hb.add(btnEditI);
        hb.gap();
        hb.add(btnMetaI);
        hb.gap();
        hb.add(btnReplaceI);
        hb.glue();

        vb2.add(hb);
        vb2.gap();

        hb = new HBox();
        hb.setAlignmentX(0);
        hb.glue();

        final JPanel p = new JPanel();
        previewImageBg = new JPanelWithBackground(Icons.TRANSPARENT.getImage());

        previewImageBg.add(previewImage = new JLabel());
        previewImageBg.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

        p.setBorder(
                previewImageBorder = BorderFactory.createTitledBorder(
                        previewBorder,
                        "%Texture%"
                )
        );

        previewImage.setHorizontalAlignment(SwingConstants.CENTER);
        previewImage.setVerticalAlignment(SwingConstants.CENTER);

        previewImage.setBorder(BorderFactory.createEmptyBorder());
        previewImageBg.setBorder(BorderFactory.createEmptyBorder());

        previewImageBg.setOpaque(true);

        previewImageBg.setPreferredSize(new Dimension(256, 256));

        previewImageBg.setBackground(new Color(0x333333));
        p.add(previewImageBg);

        Gui.forceSize(p, 290, 300);

        hb.add(p);

        hb.glue();
        vb2.add(hb);
        vb2.glue();

        previewPanel.add(vb2, IMAGE);


        vb2 = new VBox();
        vb2.setAlignmentX(0);

        vb2.gap();

        hb = new HBox();
        hb.setAlignmentX(0);

        btnEditT = new JButton("Edit text", Icons.MENU_EDIT);
        btnReplaceT = new JButton("Replace", Icons.MENU_IMPORT_BOX);

        hb.glue();
        hb.add(btnEditT);
        hb.gap();
        hb.add(btnReplaceT);
        hb.glue();

        vb2.add(hb);
        vb2.gap();


        hb = new HBox();
        hb.setAlignmentX(0);
        hb.glue();

        JScrollPane sp = new JScrollPane();

        previewText = new JTextArea();
        previewText.setEditable(false);
        previewText.setFont(new Font(Font.MONOSPACED, 0, 10));
        previewText.setMargin(new Insets(5, 5, 5, 5));
        previewText.setEditable(false);
        previewText.setLineWrap(false);

        sp.setViewportView(previewText);

        Gui.forceSize(sp, 290, 300);

        sp.setBorder(
                previewTextBorder = BorderFactory.createTitledBorder(
                        previewBorder,
                        "%Text%"
                )
        );

        sp.setAlignmentX(0);

        hb.add(sp);

        hb.glue();

        vb2.add(hb);

        vb2.glue();
        previewPanel.add(vb2, TEXT);


        vb2 = new VBox();
        vb2.setAlignmentX(0);

        vb2.gap();

        hb = new HBox();
        hb.setAlignmentX(0);

        btnEditM = new JButton ("Edit model", Icons.MENU_EDIT);
        btnEditT = new JButton("Edit text", Icons.MENU_EDIT);
        btnReplaceT = new JButton("Replace", Icons.MENU_IMPORT_BOX);

        hb.glue();
        hb.add(btnEditM);
        hb.gap();
        hb.add(btnEditT);
        hb.gap();
        hb.add(btnReplaceT);
        hb.glue();

        vb2.add(hb);
        vb2.gap();


        hb = new HBox();
        hb.setAlignmentX(0);
        hb.glue();

        JScrollPane sp2 = new JScrollPane();

        previewModel = new JTextArea();
        previewModel.setEditable(false);
        previewModel.setFont(new Font(Font.MONOSPACED, 0, 10));
        previewModel.setMargin(new Insets(5, 5, 5, 5));
        previewModel.setEditable(false);
        previewModel.setLineWrap(false);

        sp2.setViewportView(previewModel);

        Gui.forceSize(sp2, 290, 300);

        sp2.setBorder(
                previewModelBorder = BorderFactory.createTitledBorder(
                        previewBorder,
                        "%Model%"
                )
        );

        sp2.setAlignmentX(0);

        hb.add(sp2);

        hb.glue();

        vb2.add(hb);

        vb2.glue();
        previewPanel.add(vb2, MODEL);


        vb2 = new VBox();
        vb2.setAlignmentX(0);

        vb2.gap();

        hb = new HBox();
        hb.setAlignmentX(0);

        btnEditA = new JButton("Edit", Icons.MENU_EDIT);
        btnReplaceA = new JButton("Replace", Icons.MENU_IMPORT_BOX);

        hb.glue();
        hb.add(btnEditA);
        hb.gap();
        hb.add(btnReplaceA);
        hb.glue();

        vb2.add(hb);
        vb2.gap();

        hb = new HBox();
        hb.setAlignmentX(0);
        hb.glue();
        JLabel imageIcon;
        hb.add(imageIcon = new JLabel(Icons.AUDIO));
        Gui.forceSize(imageIcon, 290, 300);
        imageIcon.setHorizontalAlignment(SwingConstants.CENTER);
        imageIcon.setVerticalAlignment(SwingConstants.CENTER);

        imageIcon.setBorder(
                previewAudioBorder = BorderFactory.createTitledBorder(
                        previewBorder,
                        "%Audio%"
                )
        );

        hb.glue();
        vb2.add(hb);

        vb2.glue();
        previewPanel.add(vb2, AUDIO);


        vbox.add(previewPanel);

        //@formatter:on

        return vbox;
    }


    private Box createProjectInfoBox() {
        //@formatter:off
        final VBox vb = new VBox();
        final Filler f = (Filler) Box.createRigidArea(new Dimension(305, 1));
        f.setAlignmentX(0);
        vb.add(f);

        vb.titsep("Project Info").setAlignmentX(0);

        vb.gap();

        vb.add(mcVersion = Gui.label("Minecraft version: " + Config.LIBRARY_VERSION));

        HBox hb;

        hb = new HBox();

        buttonEditProps = new JButton(Icons.MENU_SETUP);
        hb.add(buttonEditProps);
        hb.gap();

        projectName = new JXLabel(" ");
        projectName.setToolTipText("Project name");
        projectName.setForeground(new Color(0x333366));
        projectName.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        projectName.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        projectName.setAlignmentX(0);
        hb.add(projectName);

        hb.padding(5, 5, 5, 5);
        hb.glue();
        hb.setAlignmentX(0);
        vb.add(hb);

        hb = new HBox();
        hb.glue();

        final JPanel jPanel = new JPanel();

        hb.add(jPanel);

        jPanel.add(projectIconLabel = new JXLabel());

        //@formatter:off
        jPanel.setBorder(
                BorderFactory.createTitledBorder(
                        new CompoundBorder(
                                BorderFactory.createLineBorder(new Color(0x666666)),
                                BorderFactory.createEmptyBorder(5, 5, 5, 5)
                        ),
                        "Pack Icon"
                )
        );

        jPanel.setMaximumSize(new Dimension(145, 165));
        jPanel.setPreferredSize(new Dimension(145, 165));

        projectIconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        hb.glue();
        hb.setAlignmentX(0);
        vb.add(hb);

        hb = new HBox();
        buttonOpenBase = new JButton(Icons.MENU_OPEN);
        hb.add(buttonOpenBase);
        hb.gap();

        projectBase = new JXLabel("Open in file manager...");
        projectBase.setForeground(new Color(0x333333));
        projectBase.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        hb.add(projectBase);

        hb.padding(5, 5, 5, 5);
        hb.glue();
        hb.setAlignmentX(0);
        vb.add(hb);
        vb.glue();
        //@formatter:on

        return vb;
    }


    public void updateProjectInfo() {
        final Project p = Projects.getActive();

        if (p != null) {
            showCard("project");
            String name = p.getName();
            final int length_name = (panel.getWidth() - 75) / 11;
            name = Utils.cropStringAtEnd(name, length_name);
            projectName.setText(name);

            final File iconFile = new File(Projects.getActive().getProjectDirectory(), "pack.png");

            final ImageIcon ic = Icons.getIconFromFile(iconFile, new Dimension(128, 128));
            projectIconLabel.setIcon(ic);

            infoBox.setVisible(true);

        } else {
            showCard("empty");
            // infoBox.setVisible(false);
            // previewBox.setVisible(false);
            // updatePreview(null);
        }
    }


    public void redrawPreview() {
        updatePreview(displayedLeaf);
    }


    public void setMcVersion () {
        mcVersion.setText("Minecraft version: " + Config.LIBRARY_VERSION);
    }


    public void updatePreview(AssetTreeNode selected) {
        if (selected == null || selected instanceof AssetTreeGroup) {
            previewBox.setVisible(false);
            displayedLeaf = null;

        } else {
            previewBox.setVisible(true);

            final AssetTreeLeaf leaf = (AssetTreeLeaf) selected;
            final String source = leaf.resolveAssetSource();

            final String path = leaf.getAssetEntry().getPath();
            final String fname = FileUtils.getFilename(path);

            displayedLeaf = leaf;

            InputStream in = null;

            final EAsset type = leaf.getAssetType();

            if (type.isImage()) {
                // image asset
                final String key = leaf.getAssetKey();
                if (key.startsWith("assets.minecraft.textures.font.")) {
                    previewImageBg.setBackground(Icons.TRANSPARENT_FONTS.getImage());
                } else {
                    previewImageBg.setBackground(Icons.TRANSPARENT.getImage());
                }

                try {

                    in = Sources.getAssetStream(source, leaf.getAssetKey());
                    if (in == null) {
                        previewImage.setIcon(null);
                    } else {
                        final ImageIcon i = Icons.getIconFromStream(in, new Dimension(256, 256));

                        previewImage.setIcon(i);

                        final String fn = Utils.cropStringAtEnd(fname, 25);

                        previewImageBorder.setTitle(fn + " (" + i.getDescription() + ")");
                    }

                    final boolean metaInProj = leaf.isMetaProvidedByProject();
                    btnMetaI.setIcon(metaInProj ? Icons.MENU_EDIT : Icons.MENU_NEW);

                    previewCardLayout.show(previewPanel, IMAGE);

                } catch (final IOException e) {
                    Log.e(e);
                    return;
                } finally {
                    try {
                        if (in != null) in.close();
                    } catch (final IOException e1) {
                        e1.printStackTrace();
                    }
                }

            } else if (type.isText()) {
                // text asset
                String text;

                try {
                    in = Sources.getAssetStream(leaf.resolveAssetSource(), leaf.getAssetKey());
                    text = FileUtils.streamToString(in, 100);

                    if (path.contains("models") && path.endsWith(".json")){
                        if (in == null) {
                            previewModel.setText("");
                        } else {
                            previewModel.setText(text);

                            previewModelBorder.setTitle(fname);
                        }

                        previewModel.setCaretPosition(0); // scroll to top

                        previewCardLayout.show(previewPanel, MODEL);
                    } else {
                        if (in == null) {
                            previewText.setText("");
                        } else {
                            previewText.setText(text);

                            previewTextBorder.setTitle(fname);
                        }

                        previewText.setCaretPosition(0); // scroll to top

                        previewCardLayout.show(previewPanel, TEXT);
                    }
                } catch (final IOException e) {
                    Log.e(e);
                    return;
                } finally {
                    try {
                        if (in != null) in.close();
                    } catch (final IOException e1) {
                        e1.printStackTrace();
                    }
                }

            } else if (type.isSound()) {
                // sound asset
                previewAudioBorder.setTitle(fname);

                previewCardLayout.show(previewPanel, AUDIO);

            } else {
                // undisplayable
                previewBox.setVisible(false);
            }

            previewBox.repaint();
        }
    }


    private void addActions() {
        buttonOpenBase.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskOpenProjectFolder();
            }
        });

        buttonEditProps.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskDialogProjectProperties();
            }
        });

        projectIconLabel.addMouseListener(new ClickListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                Tasks.taskDialogProjectProperties();
            }
        });

        final ActionListener listenerReplace = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskImportReplacement(displayedLeaf);
            }
        };

        btnReplaceI.addActionListener(listenerReplace);
        btnReplaceT.addActionListener(listenerReplace);
        btnReplaceA.addActionListener(listenerReplace);

        btnEditI.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskEditAsset(displayedLeaf);
            }
        });

        btnMetaI.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskEditMeta(displayedLeaf);
            }
        });

        btnEditT.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskEditAsset(displayedLeaf);
            }
        });

        btnEditA.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskEditAsset(displayedLeaf);
            }
        });

        btnEditM.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskEditModel(displayedLeaf);
            }
        });
    }
}
