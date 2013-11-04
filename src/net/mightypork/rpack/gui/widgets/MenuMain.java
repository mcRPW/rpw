package net.mightypork.rpack.gui.widgets;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import net.mightypork.rpack.App;
import net.mightypork.rpack.Config;
import net.mightypork.rpack.gui.Icons;
import net.mightypork.rpack.gui.windows.Alerts;
import net.mightypork.rpack.project.Project;
import net.mightypork.rpack.project.Projects;
import net.mightypork.rpack.tasks.Tasks;


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
	private JMenuItem itemProjectExit;
	private JMenuItem itemProjectClose;

	private JMenuItem itemLibraryManage;
	private JMenuItem itemLibraryRefreshVanilla;
	private JMenuItem itemLibraryManageModGroups;
	private JMenuItem itemLibraryManageModFilters;
	private JMenuItem itemLibraryRefreshSources;
	private JMenuItem itemLibraryImport;

	private JMenuItem itemTreeCollapseAll;
	private JMenuItem itemTreeExpandAll;
	private JMenuItem itemTreeRefreshTree;

	private JCheckBoxMenuItem itemOptionsFancyTree;
	private JCheckBoxMenuItem itemOptionsLangFiles;
	private JCheckBoxMenuItem itemOptionsFontFiles;
	private JCheckBoxMenuItem itemOptionsPreviewHover;
	private JCheckBoxMenuItem itemOptionsHiddenFiles;
	private JMenuItem itemOptionsSettings;

	private JMenuItem itemHelpGuide;
	private JMenuItem itemHelpLog;
	private JMenuItem itemHelpAbout;

	@SuppressWarnings("unused")
	private JMenu menuProject;
	@SuppressWarnings("unused")
	private JMenu menuLibrary;
	private JMenu menuTree;
	@SuppressWarnings("unused")
	private JMenu menuOptions;
	@SuppressWarnings("unused")
	private JMenu menuHelp;

	private JMenu menuRecentProjects;

	private JMenuItem itemProjectOpenFolder;


	public MenuMain() {

		menuBar = new JMenuBar();
		JMenu menu;
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
			item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK));
			item.setIcon(Icons.MENU_EXPORT_BOX);
			menu.add(item);
			
			
			item = itemProjectManageMcPacks = new JMenuItem("Manage packs in MC", KeyEvent.VK_D);
			item.setIcon(Icons.MENU_MANAGE);
			menu.add(item);
			
			menu.addSeparator();
			
			item = itemProjectSummary = new JMenuItem("Project summary", KeyEvent.VK_J);
			item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
			item.setIcon(Icons.MENU_INFO);
			menu.add(item);
			
			item = itemProjectOpenFolder = new JMenuItem("Open project folder", KeyEvent.VK_I);
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
			
			item = itemProjectExit = new JMenuItem("Exit", KeyEvent.VK_X);
			item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
			item.setIcon(Icons.MENU_EXIT);
			menu.add(item);
			
		menuBar.add(menu);

		menu = menuTree = new JMenu("Tree");
		menu.setMnemonic(KeyEvent.VK_T);			
				
			item = itemTreeCollapseAll = new JMenuItem("Collapse all", KeyEvent.VK_C);
			item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, ActionEvent.CTRL_MASK));
			item.setIcon(Icons.TREE_CLOSE);
			menu.add(item);
		
			item = itemTreeExpandAll = new JMenuItem("Expand all", KeyEvent.VK_E);
			item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ADD, ActionEvent.CTRL_MASK));
			item.setIcon(Icons.TREE_OPEN);
			menu.add(item);
			
			menu.addSeparator();
			
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
			item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
			item.setIcon(Icons.MENU_RELOAD);
			menu.add(item);
			
			menu.addSeparator();		
			
			item = itemLibraryRefreshVanilla = new JMenuItem("Re-extract Vanilla pack", KeyEvent.VK_R);
			item.setIcon(Icons.MENU_RELOAD2);
			menu.add(item);
			
			menu.addSeparator();
		
			item = itemLibraryManageModGroups = new JMenuItem("Edit mod Groups", KeyEvent.VK_G);
			item.setIcon(Icons.MENU_EDIT);
			menu.add(item);
		
			item = itemLibraryManageModFilters = new JMenuItem("Edit mod Filters", KeyEvent.VK_F);
			item.setIcon(Icons.MENU_EDIT);
			menu.add(item);
		
		menuBar.add(menu);
		

		menu = menuOptions = new JMenu("Options");
		menu.setMnemonic(KeyEvent.VK_E);
			
			ckitem = itemOptionsFancyTree = new JCheckBoxMenuItem("Use \"fancy\" tree structure");
			ckitem.setMnemonic(KeyEvent.VK_S);
			ckitem.setToolTipText("Use neat groups instead of the real pack structure.");
			ckitem.setSelected(Config.FANCY_GROUPS);
			menu.add(ckitem);
			
			
			ckitem = itemOptionsPreviewHover = new JCheckBoxMenuItem("Preview when mousing over");
			ckitem.setMnemonic(KeyEvent.VK_P);
			ckitem.setSelected(Config.PREVIEW_HOVER);
			menu.add(ckitem);
			
			menu.addSeparator();
			
			ckitem = itemOptionsLangFiles = new JCheckBoxMenuItem("Show translation files (*.lang)");
			ckitem.setMnemonic(KeyEvent.VK_L);
			ckitem.setSelected(Config.SHOW_LANG);
			menu.add(ckitem);
			
			ckitem = itemOptionsFontFiles = new JCheckBoxMenuItem("Show unicode font textures");
			ckitem.setMnemonic(KeyEvent.VK_F);	
			ckitem.setSelected(Config.SHOW_FONT);		
			menu.add(ckitem);
			
			menu.addSeparator();
						
			ckitem = itemOptionsHiddenFiles = new JCheckBoxMenuItem("Show hidden files in file pickers");
			ckitem.setMnemonic(KeyEvent.VK_H);	
			ckitem.setSelected(Config.SHOW_HIDDEN_FILES);		
			menu.add(ckitem);
			
			menu.addSeparator();
						
			item = itemOptionsSettings = new JMenuItem("Configure editors");
			item.setMnemonic(KeyEvent.VK_E);	
			item.setIcon(Icons.MENU_SETUP);
			menu.add(item);
			
		menuBar.add(menu);
		
		
		//menuBar.add(Box.createHorizontalGlue());

		menu = menuHelp = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_H);
		
			item = itemHelpLog = new JMenuItem("Show runtime log", KeyEvent.VK_L);
			item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
			item.setIcon(Icons.MENU_LOG);
			menu.add(item);
			
			menu.addSeparator();
			
			item = itemHelpGuide = new JMenuItem("Quick Guide", KeyEvent.VK_G);
			item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
			item.setIcon(Icons.MENU_HELP);
			menu.add(item);
			
			menu.addSeparator();
		
		
			item = itemHelpAbout = new JMenuItem("About", KeyEvent.VK_A);
			item.setIcon(Icons.MENU_ABOUT);
			menu.add(item);
		
		menuBar.add(menu);
		//@formatter:on

		addActions();
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


		itemProjectExit.addActionListener(new ActionListener() {

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

				Tasks.taskPushTreeToProject();
				Tasks.taskReloadVanilla();
			}
		});

		itemLibraryRefreshSources.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Tasks.taskPushTreeToProject();
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

		itemLibraryManageModFilters.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Tasks.taskEditModFilters();
			}
		});

		itemHelpAbout.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Tasks.taskDialogAbout();
			}
		});

		itemHelpLog.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Tasks.taskDialogLog();
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

		itemHelpGuide.addActionListener(new ActionListener() {

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

		itemOptionsFancyTree.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				boolean newOpt = itemOptionsFancyTree.isSelected();

				if (Config.FANCY_GROUPS != newOpt) {
					Config.FANCY_GROUPS = newOpt;
					Config.save();
					Tasks.taskTreeSaveAndRebuild();
					updateEnabledItems();
				}

			}
		});

		itemOptionsPreviewHover.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				boolean newOpt = itemOptionsPreviewHover.isSelected();

				if (Config.PREVIEW_HOVER != newOpt) {
					Config.PREVIEW_HOVER = newOpt;
					Config.save();
				}

			}
		});


		itemOptionsFontFiles.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				boolean newOpt = itemOptionsFontFiles.isSelected();

				if (Config.SHOW_FONT != newOpt) {
					Config.SHOW_FONT = newOpt;
					Config.save();
					Tasks.taskTreeSaveAndRebuild();
				}
			}
		});


		itemOptionsHiddenFiles.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				boolean newOpt = itemOptionsHiddenFiles.isSelected();

				if (Config.SHOW_HIDDEN_FILES != newOpt) {
					Config.SHOW_HIDDEN_FILES = newOpt;
					Config.save();
				}
			}
		});

		itemOptionsLangFiles.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {


				boolean newOpt = itemOptionsLangFiles.isSelected();

				if (Config.SHOW_LANG != newOpt) {
					Config.SHOW_LANG = newOpt;
					Config.save();
					Tasks.taskTreeSaveAndRebuild();
				}
			}
		});

		itemOptionsSettings.addActionListener(new ActionListener() {

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
		menuTree.setEnabled(open);
		itemProjectOpenFolder.setEnabled(open);

		itemLibraryManageModFilters.setEnabled(Config.FANCY_GROUPS);
		itemLibraryManageModGroups.setEnabled(Config.FANCY_GROUPS);

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
}
