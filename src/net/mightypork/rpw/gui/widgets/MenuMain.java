package net.mightypork.rpw.gui.widgets;


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
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.project.Project;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.tasks.Tasks;
import net.mightypork.rpw.utils.GuiUtils;


public class MenuMain {

	public JMenuBar menuBar;

	private JMenuItem itemProjectNew;
	private JMenuItem itemProjectOpen;
	private JMenuItem itemProjectSave;
	private JMenuItem itemProjectSaveAs;
	private JMenuItem itemProjectManage;
	private JMenuItem itemProjectManageMcPacks;
	private JMenuItem itemProjectSetup;
	private JMenuItem itemProjectSummary;
	private JMenuItem itemProjectExportMc;
	private JMenuItem itemProjectExport;
	private JMenuItem itemExit;
	private JMenuItem itemProjectClose;
	private JMenuItem itemProjectOpenFolder;
	private JMenuItem itemCustomSounds;

	private JMenuItem itemLibraryManage;
	private JMenuItem itemLibraryRefreshVanilla;
	private JMenuItem itemLibraryManageModGroups;
	private JMenuItem itemLibraryManageModFilters;
	private JMenuItem itemLibraryRefreshSources;
	private JMenuItem itemLibraryImport;

	private JMenuItem itemTreeCollapseAll;
	private JMenuItem itemTreeExpandAll;
	private JMenuItem itemTreeRefreshTree;

	private JCheckBoxMenuItem itemOptionFancyTree;
	private JCheckBoxMenuItem itemOptionLangFiles;
	private JCheckBoxMenuItem itemOptionFontFiles;
	private JCheckBoxMenuItem itemOptionPreviewHover;
	private JCheckBoxMenuItem itemOptionHiddenFiles;
	private JCheckBoxMenuItem itemOptionWarningOrphanedNodes;
	private JCheckBoxMenuItem itemOptionObsoleteDirs;
	private JCheckBoxMenuItem itemOptionShowLogTerminal;
	private JMenuItem itemConfigureEditors;

	private JMenuItem itemHelp;
	private JMenuItem itemRuntimeLog;
	private JMenuItem itemAbout;


	@SuppressWarnings("unused")
	private JMenu menuProject;
	@SuppressWarnings("unused")
	private JMenu menuLibrary;
	private JMenu menuRecentProjects;
	private JMenu menuView;
	@SuppressWarnings("unused")
	private JMenu menuOptions;
	@SuppressWarnings("unused")
	private JMenu menuHelp;
	@SuppressWarnings("unused")
	private JMenu menuTools;


	public MenuMain() {

		menuBar = new JMenuBar();
		JMenu menu, menu2;
		JCheckBoxMenuItem ckitem;

		//@formatter:off
		menu = menuProject = new JMenu("Project");
		menu.setMnemonic(KeyEvent.VK_P);
		
			JMenuItem item;
		
			item = itemProjectNew = new JMenuItem("New blank project", KeyEvent.VK_N);
			item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
			item.setIcon(Icons.MENU_NEW);
			menu.add(item);		
			
			menu.addSeparator();			
			
			item = itemProjectOpen = new JMenuItem("Open project...", KeyEvent.VK_L);
			item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
			item.setIcon(Icons.MENU_OPEN);
			menu.add(item);
			
			
			menuRecentProjects = new JMenu("Recent projects");
			menuRecentProjects.setIcon(Icons.MENU_RECENT);
			menu.add(menuRecentProjects);
			
			
			menu.addSeparator();
			
			item = itemProjectClose = new JMenuItem("Close project", KeyEvent.VK_C);
			item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
			item.setIcon(Icons.MENU_CANCEL);
			menu.add(item);
			
			menu.addSeparator();
			
			item = itemProjectSave = new JMenuItem("Save project", KeyEvent.VK_S);
			item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
			item.setIcon(Icons.MENU_SAVE);
			menu.add(item);
			
			item = itemProjectSaveAs = new JMenuItem("Save project as...", KeyEvent.VK_A);
			item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.SHIFT_MASK + ActionEvent.CTRL_MASK));
			item.setIcon(Icons.MENU_SAVE_AS);
			menu.add(item);
						
			menu.addSeparator();
			
			item = itemProjectExport = new JMenuItem("Export to...", KeyEvent.VK_E);
			item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
			item.setIcon(Icons.MENU_EXPORT_BOX);
			menu.add(item);		
						
			item = itemProjectExportMc = new JMenuItem("Export to Minecraft", KeyEvent.VK_M);
			item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
			item.setIcon(Icons.MENU_EXPORT_BOX);
			menu.add(item);
			
			menu.addSeparator();
						
			item = itemProjectOpenFolder = new JMenuItem("Open project folder", KeyEvent.VK_I);
			item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK));
			item.setIcon(Icons.MENU_OPEN);
			menu.add(item);
			
			item = itemProjectSetup = new JMenuItem("Project properties", KeyEvent.VK_P);
			item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0));
			item.setIcon(Icons.MENU_SETUP);
			menu.add(item);
			
			menu.addSeparator();
			
			item = itemProjectManage = new JMenuItem("Manage projects", KeyEvent.VK_G);
			item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
			item.setIcon(Icons.MENU_MANAGE);
			menu.add(item);
			
			menu.addSeparator();
			
			item = itemExit = new JMenuItem("Exit", KeyEvent.VK_X);
			item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
			item.setIcon(Icons.MENU_EXIT);
			menu.add(item);
			
		menuBar.add(menu);
		

		menu = menuTools = new JMenu("Tools");
		menu.setMnemonic(KeyEvent.VK_T);
		
			item = itemProjectSummary = new JMenuItem("Project summary", KeyEvent.VK_J);
			item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
			item.setIcon(Icons.MENU_INFO);
			menu.add(item);
			
			item = itemCustomSounds = new JMenuItem("Manage custom sounds", KeyEvent.VK_S);
			item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK));
			item.setIcon(Icons.MENU_SOUND_WIZARD);
			menu.add(item);
			
			menu.addSeparator();
			
			item = itemProjectManageMcPacks = new JMenuItem("Manage packs in MC", KeyEvent.VK_D);
			item.setIcon(Icons.MENU_MANAGE);
			menu.add(item);
			

		menuBar.add(menu);	
		

		menu = menuView = new JMenu("View");
		menu.setMnemonic(KeyEvent.VK_V);
		
			item = itemRuntimeLog = new JMenuItem("Show runtime log", KeyEvent.VK_L);
			item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
			item.setIcon(Icons.MENU_LOG);
			menu.add(item);
			
			menu.addSeparator();

			ckitem = itemOptionFancyTree = new JCheckBoxMenuItem("Use \"fancy\" tree structure");
			ckitem.setMnemonic(KeyEvent.VK_S);
			ckitem.setToolTipText("Use neat groups instead of the real pack structure.");
			menu.add(ckitem);
			
			menu.addSeparator();
			
			ckitem = itemOptionObsoleteDirs = new JCheckBoxMenuItem("Show obsolete files");
			ckitem.setMnemonic(KeyEvent.VK_W);
			ckitem.setToolTipText("Show assets that are no longer used by the game.");
			menu.add(ckitem);
						
			ckitem = itemOptionLangFiles = new JCheckBoxMenuItem("Show translation files (*.lang)");
			ckitem.setMnemonic(KeyEvent.VK_L);	
			menu.add(ckitem);
			
			ckitem = itemOptionFontFiles = new JCheckBoxMenuItem("Show unicode font textures");
			ckitem.setMnemonic(KeyEvent.VK_F);		
			menu.add(ckitem);
		
			menu.addSeparator();
		
			item = itemTreeCollapseAll = new JMenuItem("Collapse tree", KeyEvent.VK_C);
			item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, ActionEvent.CTRL_MASK));
			item.setIcon(Icons.TREE_CLOSE);
			menu.add(item);
		
			item = itemTreeExpandAll = new JMenuItem("Expand tree", KeyEvent.VK_E);
			item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ADD, ActionEvent.CTRL_MASK));
			item.setIcon(Icons.TREE_OPEN);
			menu.add(item);
						
			item = itemTreeRefreshTree = new JMenuItem("Refresh tree display", KeyEvent.VK_T);
			item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
			item.setIcon(Icons.MENU_RELOAD);
			menu.add(item);			
			
		menuBar.add(menu);	


		menu = menuLibrary = new JMenu("Library");
		menu.setMnemonic(KeyEvent.VK_L);
			
			item = itemLibraryImport = new JMenuItem("Import resource pack...", KeyEvent.VK_I);
			item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
			item.setIcon(Icons.MENU_OPEN);
			menu.add(item);
			
			menu.addSeparator();
		
			item = itemLibraryManage = new JMenuItem("Manage library", KeyEvent.VK_M);
			item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
			item.setIcon(Icons.MENU_MANAGE);
			menu.add(item);	
			
			item = itemLibraryRefreshSources = new JMenuItem("Reload library", KeyEvent.VK_B);
			item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, ActionEvent.CTRL_MASK));
			item.setIcon(Icons.MENU_RELOAD);
			menu.add(item);
			
			menu.addSeparator();		
			
			item = itemLibraryRefreshVanilla = new JMenuItem("Re-extract Minecraft assets", KeyEvent.VK_R);
			item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
			item.setIcon(Icons.MENU_RELOAD2);
			menu.add(item);
			
			menu.addSeparator();
			
			menu2 = new JMenu("Fancy Tree mod support");
			menu2.setMnemonic(KeyEvent.VK_T);
			menu2.setIcon(Icons.MENU_TREE);
		
				item = itemLibraryManageModGroups = new JMenuItem("Edit mod Groups", KeyEvent.VK_G);
				item.setIcon(Icons.MENU_EDIT);
				menu2.add(item);
			
				item = itemLibraryManageModFilters = new JMenuItem("Edit mod Filters", KeyEvent.VK_F);
				item.setIcon(Icons.MENU_EDIT);
				menu2.add(item);
			
			menu.add(menu2);
		
		menuBar.add(menu);
		

		menu = menuOptions = new JMenu("Options");
		menu.setMnemonic(KeyEvent.VK_O);
			
			
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
									
			ckitem = itemOptionShowLogTerminal = new JCheckBoxMenuItem("Show log for lengthy operations");
			ckitem.setMnemonic(KeyEvent.VK_L);	
			menu.add(ckitem);
			
			menu.addSeparator();
						
			item = itemConfigureEditors = new JMenuItem("Configure editors");
			item.setMnemonic(KeyEvent.VK_E);
			item.setIcon(Icons.MENU_SETUP);		
			menu.add(item);
			
		menuBar.add(menu);
		
		
		//menuBar.add(Box.createHorizontalGlue());

		menu = menuHelp = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_H);
					
			item = itemHelp = new JMenuItem("Guide Book", KeyEvent.VK_G);
			item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
			item.setIcon(Icons.MENU_HELP);
			menu.add(item);
			
			menu.addSeparator();
			
			item = new JMenuItem("RPW on GitHub");
			item.setIcon(Icons.MENU_GITHUB);
			item.addActionListener(GuiUtils.openUrlListener);
			item.setActionCommand(Paths.URL_GITHUB_WEB);
			menu.add(item);	
			
			item = new JMenuItem("RPW on Planet Minecraft");
			item.setIcon(Icons.MENU_PMC);
			item.addActionListener(GuiUtils.openUrlListener);
			item.setActionCommand(Paths.URL_PLANETMINECRAFT_WEB);
			menu.add(item);	
			
			item = new JMenuItem("RPW on Minecraft Forum");
			item.setIcon(Icons.MENU_MCF);
			item.addActionListener(GuiUtils.openUrlListener);
			item.setActionCommand(Paths.URL_MINECRAFTFORUM_WEB);
			menu.add(item);	
			
			menu.addSeparator();
			
			item = new JMenuItem("Download latest version");
			item.setIcon(Icons.MENU_DOWNLOAD);
			item.addActionListener(GuiUtils.openUrlListener);
			item.setActionCommand(Paths.URL_LATEST_DOWNLOAD);
			menu.add(item);
			
			menu.addSeparator();
			
			item = new JMenuItem("Donate (PayPal)", KeyEvent.VK_D);
			item.setIcon(Icons.MENU_DONATE);
			item.addActionListener(GuiUtils.openUrlListener);
			item.setActionCommand(Paths.URL_DONATE);
			menu.add(item);
			
			menu.addSeparator();
		
			item = itemAbout = new JMenuItem("About", KeyEvent.VK_A);
			item.setIcon(Icons.MENU_ABOUT);
			menu.add(item);
		
		menuBar.add(menu);
		//@formatter:on

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


		itemLibraryRefreshVanilla.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Tasks.taskStoreProjectChanges();
				Tasks.taskReloadVanilla();
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

				Tasks.taskSaveProject(null);
			}
		});

		itemProjectExport.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Tasks.taskDialogExportProject();
			}
		});

		itemProjectExportMc.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Tasks.taskDialogExportToMc();
			}
		});

		itemProjectClose.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Tasks.taskCloseProject();
			}
		});

		itemProjectManage.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Tasks.taskDialogManageProjects();
			}
		});

		itemProjectManageMcPacks.addActionListener(new ActionListener() {

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

				boolean newOpt = itemOptionFancyTree.isSelected();

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

				boolean newOpt = itemOptionWarningOrphanedNodes.isSelected();

				if (Config.WARNING_ORPHANED_NODES != newOpt) {
					Config.WARNING_ORPHANED_NODES = newOpt;
					Config.save();
				}

			}
		});

		itemOptionObsoleteDirs.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				boolean newOpt = itemOptionObsoleteDirs.isSelected();

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

				boolean newOpt = itemOptionPreviewHover.isSelected();

				if (Config.PREVIEW_HOVER != newOpt) {
					Config.PREVIEW_HOVER = newOpt;
					Config.save();
				}

			}
		});


		itemOptionFontFiles.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				boolean newOpt = itemOptionFontFiles.isSelected();

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

				boolean newOpt = itemOptionHiddenFiles.isSelected();

				if (Config.SHOW_HIDDEN_FILES != newOpt) {
					Config.SHOW_HIDDEN_FILES = newOpt;
					Config.save();
				}
			}
		});

		itemOptionShowLogTerminal.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				boolean newOpt = itemOptionShowLogTerminal.isSelected();

				if (Config.SHOW_LOG_TERMINAL != newOpt) {
					Config.SHOW_LOG_TERMINAL = newOpt;
					Config.save();
				}
			}
		});

		itemOptionLangFiles.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {


				boolean newOpt = itemOptionLangFiles.isSelected();

				if (Config.SHOW_LANG != newOpt) {
					Config.SHOW_LANG = newOpt;
					Config.save();
					Tasks.taskTreeSaveAndRebuild();
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

	}


	public void updateEnabledItems() {

		boolean open = (Projects.getActive() != null);

		itemProjectExport.setEnabled(open);
		itemProjectSave.setEnabled(open);
		itemProjectSaveAs.setEnabled(open);
		itemProjectExportMc.setEnabled(open);
		itemProjectSetup.setEnabled(open);
		itemProjectSummary.setEnabled(open);
		itemProjectClose.setEnabled(open);
		menuView.setEnabled(open);
		itemProjectOpenFolder.setEnabled(open);
		itemCustomSounds.setEnabled(open);

	}

	private ActionListener openRecentProjectListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			Tasks.taskOpenProject(e.getActionCommand());
		}
	};


	public void updateRecentProjects() {

		menuRecentProjects.removeAll();

		List<String> recents = Projects.getRecentProjects();

		menuRecentProjects.setEnabled(recents.size() > 1);

		Project activeProj = Projects.getActive();
		String activeName = (activeProj == null ? "" : activeProj.getDirName());

		JMenuItem item;
		for (String s : recents) {
			if (s.equalsIgnoreCase(activeName)) continue; // dont show current project in the list
			menuRecentProjects.add(item = new JMenuItem(s));
			item.setActionCommand(s);
			item.addActionListener(openRecentProjectListener);
		}
	}


	public void updateOptionCheckboxes() {

		itemOptionFancyTree.setSelected(Config.FANCY_TREE);
		itemOptionObsoleteDirs.setSelected(Config.SHOW_OBSOLETE_DIRS);
		itemOptionLangFiles.setSelected(Config.SHOW_LANG);
		itemOptionFontFiles.setSelected(Config.SHOW_FONT);
		itemOptionWarningOrphanedNodes.setSelected(Config.WARNING_ORPHANED_NODES);
		itemOptionPreviewHover.setSelected(Config.PREVIEW_HOVER);
		itemOptionHiddenFiles.setSelected(Config.SHOW_HIDDEN_FILES);
		itemOptionShowLogTerminal.setSelected(Config.SHOW_LOG_TERMINAL);
	}
}
