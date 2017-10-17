package net.mightypork.rpw.gui.widgets;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Config;
import net.mightypork.rpw.Paths;
import net.mightypork.rpw.gui.Gui;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.project.Project;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.tasks.Tasks;
import net.mightypork.rpw.utils.files.OsUtils;


public class MenuMain {
    public JMenuBar menuBar;

    private JMenuItem itemProjectNew;
    private JMenuItem itemProjectOpen;
    private JMenuItem itemProjectSave;
    private JMenuItem itemProjectRevert;
    private JMenuItem itemProjectSaveAs;
    private JMenuItem itemManageMcPacks;
    private JMenuItem itemProjectSetup;
    private JMenuItem itemProjectSummary;
    private JMenuItem itemProjectExport;
    private JMenuItem itemProjectExportStitch;
    private JMenuItem itemProjectImportStitch;
    private JMenuItem itemDeleteVanillaCopies;
    private JMenuItem itemExit;
    private JMenuItem itemProjectClose;
    private JMenuItem itemProjectOpenFolder;
    private JMenuItem itemCustomSounds;
    private JMenuItem itemManageLanguages;

    private JMenuItem itemLibraryManage;
    private JMenuItem itemLibraryConfigureVanilla;
    private JMenuItem itemLibraryManageModGroups;
    private JMenuItem itemLibraryManageModFilters;
    private JMenuItem itemLibraryRefreshSources;
    private JMenuItem itemLibraryImport;

    private JMenuItem itemTreeCollapseAll;
    private JMenuItem itemTreeExpandAll;
    private JMenuItem itemTreeRefreshTree;

    private JCheckBoxMenuItem itemOptionFancyTree;
    private JCheckBoxMenuItem itemOptionAutoSave;
    private JCheckBoxMenuItem itemOptionLangFiles;
    private JCheckBoxMenuItem itemOptionFontFiles;
    private JCheckBoxMenuItem itemOptionSoundFiles;
    private JCheckBoxMenuItem itemOptionTechFiles;
    private JCheckBoxMenuItem itemOptionTextFiles;
    private JCheckBoxMenuItem itemOptionTextureFiles;
    private JCheckBoxMenuItem itemOptionPreviewHover;
    private JCheckBoxMenuItem itemOptionHiddenFiles;
    private JCheckBoxMenuItem itemOptionWarningOrphanedNodes;
    private JCheckBoxMenuItem itemOptionObsoleteDirs;
    private JCheckBoxMenuItem itemUseNimbusTheme;
    private JCheckBoxMenuItem itemUseNativeTheme;
    private JMenuItem itemConfigureEditors;

    private JMenuItem itemHelp;
    private JMenuItem itemRuntimeLog;
    private JMenuItem itemAbout;
    private JMenuItem itemChangelog;

    private JMenu menuProject;
    private JMenu menuLibrary;
    private final JMenu menuRecentProjects;
    private JMenu menuView;
    private JMenu menuOptions;
    private JMenu menuHelp;
    private JMenu menuTools;


    public MenuMain() {
        final int CTRL = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        final int SHIFT = ActionEvent.SHIFT_MASK;

        // Mac doesn't have menu icons in native mode
        final boolean ICNS = !(OsUtils.isMac() && Config.USE_NATIVE_THEME);

        menuBar = new JMenuBar();
        JMenu menu, menu2;
        JMenuItem item;
        JCheckBoxMenuItem ckitem;

// --- PROJECT MENU ---

        menu = menuProject = new JMenu("Project");
        menu.setMnemonic(KeyEvent.VK_P);


        item = itemProjectNew = new JMenuItem("Create new project", KeyEvent.VK_N);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, CTRL));
        if (ICNS) item.setIcon(Icons.MENU_NEW);
        menu.add(item);

        menu.addSeparator();

        item = itemProjectOpen = new JMenuItem("My projects", KeyEvent.VK_L);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, CTRL));
        if (ICNS) item.setIcon(Icons.MENU_OPEN);
        menu.add(item);


        menuRecentProjects = new JMenu("Recent projects");
        menuRecentProjects.setIcon(Icons.MENU_RECENT);
        menu.add(menuRecentProjects);


        menu.addSeparator();

        item = itemProjectClose = new JMenuItem("Close project", KeyEvent.VK_C);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, CTRL));
        if (ICNS) item.setIcon(Icons.MENU_CANCEL);
        menu.add(item);

        menu.addSeparator();

        item = itemProjectSave = new JMenuItem("Save project", KeyEvent.VK_S);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, CTRL));
        if (ICNS) item.setIcon(Icons.MENU_SAVE);
        menu.add(item);

        item = itemProjectSaveAs = new JMenuItem("Save project as...", KeyEvent.VK_A);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, CTRL | SHIFT));
        if (ICNS) item.setIcon(Icons.MENU_SAVE_AS);
        menu.add(item);

        menu.addSeparator();


        item = itemProjectRevert = new JMenuItem("REVERT ALL CHANGES", KeyEvent.VK_R);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, CTRL | SHIFT));
        if (ICNS) item.setIcon(Icons.MENU_REVERT);
        menu.add(item);

        menu.addSeparator();

        item = itemProjectExport = new JMenuItem("Export resourcepack", KeyEvent.VK_M);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, CTRL));
        if (ICNS) item.setIcon(Icons.MENU_EXPORT_BOX);
        menu.add(item);

        menu.addSeparator();

        item = itemProjectExportStitch = new JMenuItem("Export stitched PNG to...", KeyEvent.VK_X);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, CTRL | SHIFT));
        if (ICNS) item.setIcon(Icons.MENU_EXPORT_BOX);
        menu.add(item);

        item = itemProjectImportStitch = new JMenuItem("Import stitched PNG from...", KeyEvent.VK_P);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, CTRL | SHIFT));
        if (ICNS) item.setIcon(Icons.MENU_IMPORT_BOX);
        menu.add(item);

        menu.addSeparator();

        item = itemProjectSetup = new JMenuItem("Project properties", KeyEvent.VK_P);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0));
        if (ICNS) item.setIcon(Icons.MENU_SETUP);
        menu.add(item);

        menu.addSeparator();

        item = itemExit = new JMenuItem("Exit", KeyEvent.VK_X);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, CTRL));
        if (ICNS) item.setIcon(Icons.MENU_EXIT);
        menu.add(item);

        menuBar.add(menu);


// --- TOOLS MENU ---

        menu = menuTools = new JMenu("Tools");
        menu.setMnemonic(KeyEvent.VK_T);

        item = itemProjectSummary = new JMenuItem("Project summary", KeyEvent.VK_J);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, CTRL));
        if (ICNS) item.setIcon(Icons.MENU_INFO);
        menu.add(item);

        item = itemCustomSounds = new JMenuItem("Manage custom sounds", KeyEvent.VK_S);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, CTRL));
        if (ICNS) item.setIcon(Icons.MENU_SOUND_WIZARD);
        menu.add(item);

        item = itemManageLanguages = new JMenuItem("Manage languages", KeyEvent.VK_L);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, CTRL | SHIFT));
        if (ICNS) item.setIcon(Icons.TREE_FILE_TEXT);
        menu.add(item);

        item = itemProjectOpenFolder = new JMenuItem("Open project folder", KeyEvent.VK_I);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, CTRL));
        if (ICNS) item.setIcon(Icons.MENU_OPEN);
        menu.add(item);

        item = itemDeleteVanillaCopies = new JMenuItem("Delete unchanged vanilla copies", KeyEvent.VK_U);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, CTRL));
        if (ICNS) item.setIcon(Icons.MENU_DELETE_ASSET);
        menu.add(item);

        menu.addSeparator();

        item = itemManageMcPacks = new JMenuItem("Manage resoucepacks in Minecraft", KeyEvent.VK_D);
        if (ICNS) item.setIcon(Icons.MENU_MANAGE);
        menu.add(item);

        menuBar.add(menu);


// --- LIBRARY MENU ---

        menu = menuLibrary = new JMenu("Library");
        menu.setMnemonic(KeyEvent.VK_L);

        item = itemLibraryImport = new JMenuItem("Import resourcepack...", KeyEvent.VK_I);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, CTRL));
        if (ICNS) item.setIcon(Icons.MENU_OPEN);
        menu.add(item);

        menu.addSeparator();

        item = itemLibraryManage = new JMenuItem("Manage library", KeyEvent.VK_M);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, CTRL));
        if (ICNS) item.setIcon(Icons.MENU_MANAGE);
        menu.add(item);

        item = itemLibraryRefreshSources = new JMenuItem("Reload library", KeyEvent.VK_B);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, CTRL));
        if (ICNS) item.setIcon(Icons.MENU_RELOAD);
        menu.add(item);

        menu.addSeparator();

        item = itemLibraryConfigureVanilla = new JMenuItem("Select target Minecraft version", KeyEvent.VK_R);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, CTRL | SHIFT));
        if (ICNS) item.setIcon(Icons.MENU_RELOAD2);
        menu.add(item);

        menu.addSeparator();

        menu2 = new JMenu("Fancy Tree mod support");
        menu2.setMnemonic(KeyEvent.VK_T);
        if (ICNS) menu2.setIcon(Icons.MENU_TREE);

        // submenu items

        item = itemLibraryManageModGroups = new JMenuItem("Edit mod Groups", KeyEvent.VK_G);
        if (ICNS) item.setIcon(Icons.MENU_EDIT);
        menu2.add(item);

        item = itemLibraryManageModFilters = new JMenuItem("Edit mod Filters", KeyEvent.VK_F);
        if (ICNS) item.setIcon(Icons.MENU_EDIT);
        menu2.add(item);

        menu.add(menu2);

        menuBar.add(menu);


// --- VIEW MENU ---

        menu = menuView = new JMenu("View");
        menu.setMnemonic(KeyEvent.VK_V);

        ckitem = itemOptionTextureFiles = new JCheckBoxMenuItem("Show textures");
        if (ICNS) ckitem.setIcon(Icons.TREE_FILE_IMAGE);
        ckitem.setMnemonic(KeyEvent.VK_U);
        menu.add(ckitem);

        ckitem = itemOptionSoundFiles = new JCheckBoxMenuItem("Show sounds");
        if (ICNS) ckitem.setIcon(Icons.TREE_FILE_AUDIO);
        ckitem.setMnemonic(KeyEvent.VK_A);
        menu.add(ckitem);

        ckitem = itemOptionTextFiles = new JCheckBoxMenuItem("Show text files");
        if (ICNS) ckitem.setIcon(Icons.TREE_FILE_TEXT);
        ckitem.setMnemonic(KeyEvent.VK_T);
        menu.add(ckitem);

        ckitem = itemOptionLangFiles = new JCheckBoxMenuItem("Show language files");
        if (ICNS) ckitem.setIcon(Icons.TREE_FILE_TEXT);
        ckitem.setMnemonic(KeyEvent.VK_L);
        menu.add(ckitem);

        ckitem = itemOptionFontFiles = new JCheckBoxMenuItem("Show unicode font");
        if (ICNS) ckitem.setIcon(Icons.TREE_FILE_FONT);
        ckitem.setMnemonic(KeyEvent.VK_F);
        menu.add(ckitem);

        ckitem = itemOptionTechFiles = new JCheckBoxMenuItem("Show shaders, models etc.");
        if (ICNS) ckitem.setIcon(Icons.TREE_FILE_TECH);
        ckitem.setMnemonic(KeyEvent.VK_X);
        menu.add(ckitem);

        menu.addSeparator();

        ckitem = itemOptionObsoleteDirs = new JCheckBoxMenuItem("Show obsolete (useless) files");
        if (ICNS) ckitem.setIcon(Icons.TREE_FILE_GENERIC);
        ckitem.setMnemonic(KeyEvent.VK_W);
        ckitem.setToolTipText("Show assets that are no longer used by the game.");
        menu.add(ckitem);

        menu.addSeparator();

        item = itemTreeCollapseAll = new JMenuItem("Collapse tree", KeyEvent.VK_C);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, CTRL));
        if (ICNS) item.setIcon(Icons.TREE_CLOSE);
        menu.add(item);

        item = itemTreeExpandAll = new JMenuItem("Expand tree", KeyEvent.VK_E);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ADD, CTRL));
        if (ICNS) item.setIcon(Icons.TREE_OPEN);
        menu.add(item);

        item = itemTreeRefreshTree = new JMenuItem("Refresh tree display", KeyEvent.VK_T);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        if (ICNS) item.setIcon(Icons.MENU_RELOAD);
        menu.add(item);

        menuBar.add(menu);

// --- OPTIONS MENU ---

        menu = menuOptions = new JMenu("Options");
        menu.setMnemonic(KeyEvent.VK_O);

        ckitem = itemOptionFancyTree = new JCheckBoxMenuItem("Use \"fancy\" tree structure");
        ckitem.setMnemonic(KeyEvent.VK_S);
        ckitem.setToolTipText("Use neat groups instead of the real pack structure.");
        menu.add(ckitem);

        menu.addSeparator();

        ckitem = itemOptionWarningOrphanedNodes = new JCheckBoxMenuItem("Warn about orphaned files");
        ckitem.setMnemonic(KeyEvent.VK_O);
        ckitem.setToolTipText("Show warning when Fancy Tree prevents some files from being displayed.");
        menu.add(ckitem);

        ckitem = itemOptionPreviewHover = new JCheckBoxMenuItem("Preview when mousing over");
        ckitem.setMnemonic(KeyEvent.VK_P);
        menu.add(ckitem);

        ckitem = itemOptionHiddenFiles = new JCheckBoxMenuItem("Show hidden files in file pickers");
        ckitem.setMnemonic(KeyEvent.VK_H);
        menu.add(ckitem);

        menu.addSeparator();

        ckitem = itemUseNimbusTheme = new JCheckBoxMenuItem("Use Nimbus theme (needs restart)");
        ckitem.setMnemonic(KeyEvent.VK_N);
        menu.add(ckitem);

        ckitem = itemUseNativeTheme = new JCheckBoxMenuItem("Try to use system theme (needs restart)");
        ckitem.setMnemonic(KeyEvent.VK_Y);
        menu.add(ckitem);

        menu.addSeparator();

        ckitem = itemOptionAutoSave = new JCheckBoxMenuItem("Auto save on close (don't ask)");
        ckitem.setMnemonic(KeyEvent.VK_A);
        menu.add(ckitem);

        menu.addSeparator();

        item = itemConfigureEditors = new JMenuItem("Configure editors");
        item.setMnemonic(KeyEvent.VK_E);
        if (ICNS) item.setIcon(Icons.MENU_SETUP);
        menu.add(item);

        menuBar.add(menu);


        //menuBar.add(Box.createHorizontalGlue());

// ---- HELP MENU ---

        menu = menuHelp = new JMenu("Help");
        menu.setMnemonic(KeyEvent.VK_H);

        item = itemHelp = new JMenuItem("Guide Book", KeyEvent.VK_G);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        if (ICNS) item.setIcon(Icons.MENU_HELP);
        menu.add(item);

        menu.addSeparator();

        item = itemRuntimeLog = new JMenuItem("Show runtime log", KeyEvent.VK_L);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
        if (ICNS) item.setIcon(Icons.MENU_LOG);
        menu.add(item);

        item = new JMenuItem("Report a bug");
        if (ICNS) item.setIcon(Icons.MENU_BUG);
        item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, CTRL));
        item.addActionListener(Gui.openUrlListener);
        item.setActionCommand(Paths.URL_GITHUB_BUGS);
        menu.add(item);

        menu.addSeparator();

        item = new JMenuItem("RPW website");
        if (ICNS) item.setIcon(Icons.MENU_WEBSITE);
        item.addActionListener(Gui.openUrlListener);
        item.setActionCommand(Paths.URL_RPW_WEB);
        menu.add(item);

        item = new JMenuItem("GitHub");
        if (ICNS) item.setIcon(Icons.MENU_GITHUB);
        item.addActionListener(Gui.openUrlListener);
        item.setActionCommand(Paths.URL_GITHUB_REPO);
        menu.add(item);

        menu.addSeparator();

        item = new JMenuItem("Download latest version");
        if (ICNS) item.setIcon(Icons.MENU_DOWNLOAD);
        item.addActionListener(Gui.openUrlListener);
        item.setActionCommand(Paths.URL_GITHUB_RELEASES);
        menu.add(item);

        item = itemChangelog = new JMenuItem("Changelog");
        if (ICNS) item.setIcon(Icons.TREE_FILE_TEXT);
        menu.add(item);

        menu.addSeparator();

        item = itemAbout = new JMenuItem("About", KeyEvent.VK_A);
        if (ICNS) item.setIcon(Icons.MENU_ABOUT);
        menu.add(item);

        menuBar.add(menu);


        addActions();

        updateOptionCheckboxes();
    }


    public void addActions() {
        itemProjectNew.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskNewProject();
            }
        });

        itemProjectOpen.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskOpenProject();
            }
        });

        itemExit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskExit();
            }
        });

        itemTreeCollapseAll.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskTreeCollapse();
            }
        });

        itemTreeExpandAll.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskTreeExpand();
            }
        });

        itemLibraryConfigureVanilla.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskStoreProjectChanges();
                Tasks.taskConfigureMinecraftAssets();
            }
        });

        itemLibraryRefreshSources.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskStoreProjectChanges();
                Tasks.taskReloadSources(new Runnable() {

                    @Override
                    public void run() {
                        Alerts.info(App.getFrame(), "Library reloaded.");
                    }
                });
            }
        });

        itemProjectSave.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskSaveProject();
            }
        });

        itemProjectRevert.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskAskRevertProject();
            }
        });

        itemProjectExport.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskDialogExport();
            }
        });

        itemProjectExportStitch.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskDialogExportToStitch();
            }
        });

        itemProjectImportStitch.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskDialogImportFromStitch();
            }
        });

        itemProjectClose.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskCloseProject();
            }
        });

        itemManageMcPacks.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskDialogManageMcPacks();
            }
        });

        itemProjectSaveAs.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskDialogSaveAs();
            }
        });

        itemProjectSetup.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskDialogProjectProperties();
            }
        });

        itemProjectSummary.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskDialogProjectSummary();
            }
        });

        itemAbout.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskDialogAbout();
            }
        });

        itemChangelog.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskShowChangelogForced();
            }
        });

        itemRuntimeLog.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskDialogLog();
            }
        });

        itemLibraryManageModFilters.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskEditModFilters();
            }
        });

        itemLibraryManageModGroups.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskEditModGroups();
            }
        });

        itemLibraryImport.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskDialogImportPack();
            }
        });

        itemLibraryManage.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskDialogManageLibrary();
            }
        });

        itemHelp.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskDialogHelp();
            }
        });

        itemTreeRefreshTree.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskTreeSaveAndRebuild();
            }
        });

        itemOptionFancyTree.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final boolean newOpt = itemOptionFancyTree.isSelected();

                if (Config.FANCY_TREE != newOpt) {
                    Config.FANCY_TREE = newOpt;
                    Config.save();
                    Tasks.taskTreeSaveAndRebuild();
                    updateEnabledItems();
                }
            }
        });

        itemOptionWarningOrphanedNodes.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final boolean newOpt = itemOptionWarningOrphanedNodes.isSelected();

                if (Config.WARNING_ORPHANED_NODES != newOpt) {
                    Config.WARNING_ORPHANED_NODES = newOpt;
                    Config.save();
                }
            }
        });

        itemOptionObsoleteDirs.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final boolean newOpt = itemOptionObsoleteDirs.isSelected();

                if (Config.SHOW_OBSOLETE_DIRS != newOpt) {
                    Config.SHOW_OBSOLETE_DIRS = newOpt;
                    Config.save();
                    Tasks.taskTreeSaveAndRebuild();
                }
            }
        });

        itemOptionPreviewHover.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final boolean newOpt = itemOptionPreviewHover.isSelected();

                if (Config.PREVIEW_HOVER != newOpt) {
                    Config.PREVIEW_HOVER = newOpt;
                    Config.save();
                }
            }
        });

        itemOptionFontFiles.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final boolean newOpt = itemOptionFontFiles.isSelected();

                if (Config.SHOW_FONT != newOpt) {
                    Config.SHOW_FONT = newOpt;
                    Config.save();
                    Tasks.taskTreeSaveAndRebuild();
                }
            }
        });

        itemOptionHiddenFiles.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final boolean newOpt = itemOptionHiddenFiles.isSelected();

                if (Config.SHOW_HIDDEN_FILES != newOpt) {
                    Config.SHOW_HIDDEN_FILES = newOpt;
                    Config.save();
                }
            }
        });

        itemOptionLangFiles.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final boolean newOpt = itemOptionLangFiles.isSelected();

                if (Config.SHOW_LANG != newOpt) {
                    Config.SHOW_LANG = newOpt;
                    Config.save();
                    Tasks.taskTreeSaveAndRebuild();
                }
            }
        });

        itemOptionSoundFiles.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final boolean newOpt = itemOptionSoundFiles.isSelected();

                if (Config.SHOW_SOUNDS != newOpt) {
                    Config.SHOW_SOUNDS = newOpt;
                    Config.save();
                    Tasks.taskTreeSaveAndRebuild();
                }
            }
        });

        itemOptionTechFiles.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final boolean newOpt = itemOptionTechFiles.isSelected();

                if (Config.SHOW_TECHNICAL != newOpt) {
                    Config.SHOW_TECHNICAL = newOpt;
                    Config.save();
                    Tasks.taskTreeSaveAndRebuild();
                }
            }
        });

        itemOptionTextFiles.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final boolean newOpt = itemOptionTextFiles.isSelected();

                if (Config.SHOW_TEXTS != newOpt) {
                    Config.SHOW_TEXTS = newOpt;
                    Config.save();
                    Tasks.taskTreeSaveAndRebuild();
                }
            }
        });

        itemOptionTextureFiles.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final boolean newOpt = itemOptionTextureFiles.isSelected();

                if (Config.SHOW_TEXTURES != newOpt) {
                    Config.SHOW_TEXTURES = newOpt;
                    Config.save();
                    Tasks.taskTreeSaveAndRebuild();
                    updateEnabledItems();
                }
            }
        });

        itemUseNimbusTheme.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final boolean newOpt = itemUseNimbusTheme.isSelected();

                if (Config.USE_NIMBUS != newOpt) {
                    Config.USE_NIMBUS = newOpt;

                    if (newOpt) {
                        Config.USE_NATIVE_THEME = false;
                        itemUseNativeTheme.setSelected(false);
                    }

                    Config.save();
                }
            }
        });

        itemUseNativeTheme.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final boolean newOpt = itemUseNativeTheme.isSelected();

                if (Config.USE_NATIVE_THEME != newOpt) {
                    Config.USE_NATIVE_THEME = newOpt;

                    if (newOpt) {
                        Config.USE_NIMBUS = false;
                        itemUseNimbusTheme.setSelected(false);
                    }

                    Config.save();
                }
            }
        });

        itemOptionAutoSave.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final boolean newOpt = itemOptionAutoSave.isSelected();

                if (Config.AUTO_SAVE != newOpt) {
                    Config.AUTO_SAVE = newOpt;
                    Config.save();
                }
            }
        });

        itemConfigureEditors.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskDialogSettings();
            }
        });

        itemProjectOpenFolder.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskOpenProjectFolder();
            }
        });

        itemCustomSounds.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskOpenSoundWizard();
            }
        });

        itemManageLanguages.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskManageLanguages();
            }
        });

        itemDeleteVanillaCopies.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Tasks.taskDeleteIdenticalToVanilla();
            }
        });
    }


    public void updateEnabledItems() {
        final boolean open = Projects.isOpen();

        itemProjectSave.setEnabled(open);
        itemProjectSaveAs.setEnabled(open);
        itemProjectRevert.setEnabled(open);
        itemProjectExport.setEnabled(open);
        itemProjectExportStitch.setEnabled(open);
        itemProjectImportStitch.setEnabled(open);
        itemProjectSetup.setEnabled(open);
        itemProjectSummary.setEnabled(open);
        itemProjectClose.setEnabled(open);
        menuView.setEnabled(open);
        itemProjectOpenFolder.setEnabled(open);
        itemCustomSounds.setEnabled(open);
        itemManageLanguages.setEnabled(open);

        itemOptionFontFiles.setEnabled(Config.SHOW_TEXTURES);
    }

    private final ActionListener openRecentProjectListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            Tasks.taskOpenProject(e.getActionCommand());
        }
    };


    public void updateRecentProjects() {
        menuRecentProjects.removeAll();

        final List<String> recents = Projects.getRecentProjects();

        final Project activeProj = Projects.getActive();
        final String activeName = (activeProj == null ? "" : activeProj.getName());

        JMenuItem item;
        int added = 0;
        for (final String s : recents) {
            if (s.equalsIgnoreCase(activeName)) continue; // dont show current project in the list
            menuRecentProjects.add(item = new JMenuItem(s));
            item.setActionCommand(s);
            item.addActionListener(openRecentProjectListener);
            added++;
        }

        menuRecentProjects.setEnabled(added > 0);
    }


    public void updateOptionCheckboxes() {
        itemOptionFancyTree.setSelected(Config.FANCY_TREE);
        itemOptionAutoSave.setSelected(Config.AUTO_SAVE);
        itemOptionObsoleteDirs.setSelected(Config.SHOW_OBSOLETE_DIRS);
        itemOptionLangFiles.setSelected(Config.SHOW_LANG);
        itemOptionFontFiles.setSelected(Config.SHOW_FONT);
        itemOptionSoundFiles.setSelected(Config.SHOW_SOUNDS);
        itemOptionTechFiles.setSelected(Config.SHOW_TECHNICAL);
        itemOptionTextFiles.setSelected(Config.SHOW_TEXTS);
        itemOptionTextureFiles.setSelected(Config.SHOW_TEXTURES);
        itemOptionWarningOrphanedNodes.setSelected(Config.WARNING_ORPHANED_NODES);
        itemOptionPreviewHover.setSelected(Config.PREVIEW_HOVER);
        itemOptionHiddenFiles.setSelected(Config.SHOW_HIDDEN_FILES);
        itemUseNimbusTheme.setSelected(Config.USE_NIMBUS);
        itemUseNativeTheme.setSelected(Config.USE_NATIVE_THEME);
    }
}
