package net.mightypork.rpw.tasks;


import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Config;
import net.mightypork.rpw.Const;
import net.mightypork.rpw.Flags;
import net.mightypork.rpw.Paths;
import net.mightypork.rpw.gui.Gui;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.gui.windows.WindowMain;
import net.mightypork.rpw.gui.windows.dialogs.DialogAbout;
import net.mightypork.rpw.gui.windows.dialogs.DialogConfigureEditors;
import net.mightypork.rpw.gui.windows.dialogs.DialogEditText;
import net.mightypork.rpw.gui.windows.dialogs.DialogExportToMc;
import net.mightypork.rpw.gui.windows.dialogs.DialogHelp;
import net.mightypork.rpw.gui.windows.dialogs.DialogImportPack;
import net.mightypork.rpw.gui.windows.dialogs.DialogManageLibrary;
import net.mightypork.rpw.gui.windows.dialogs.DialogManageMcPacks;
import net.mightypork.rpw.gui.windows.dialogs.DialogNewProject;
import net.mightypork.rpw.gui.windows.dialogs.DialogOpenProject;
import net.mightypork.rpw.gui.windows.dialogs.DialogProjectProperties;
import net.mightypork.rpw.gui.windows.dialogs.DialogProjectSummary;
import net.mightypork.rpw.gui.windows.dialogs.DialogSaveAs;
import net.mightypork.rpw.gui.windows.dialogs.DialogSoundWizard;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.gui.windows.messages.DialogChangelog;
import net.mightypork.rpw.gui.windows.messages.DialogRuntimeLog;
import net.mightypork.rpw.help.HelpStore;
import net.mightypork.rpw.library.MagicSources;
import net.mightypork.rpw.library.Sources;
import net.mightypork.rpw.project.Project;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.tasks.sequences.SequenceExportProject;
import net.mightypork.rpw.tasks.sequences.SequencePopulateProjectFromPack;
import net.mightypork.rpw.tree.assets.processors.RenameSourceProcessor;
import net.mightypork.rpw.tree.assets.processors.SaveToProjectNodeProcessor;
import net.mightypork.rpw.tree.assets.tree.AssetTreeLeaf;
import net.mightypork.rpw.tree.assets.tree.AssetTreeNode;
import net.mightypork.rpw.tree.assets.tree.AssetTreeProcessor;
import net.mightypork.rpw.utils.files.DesktopApi;
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.files.OsUtils;
import net.mightypork.rpw.utils.logging.Log;


/**
 * Tasks class<br>
 * Each task returns ID, to be checked with isRunning().
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class Tasks {
	
	private static int LAST_TASK = 0;
	private static HashMap<Integer, Boolean> TASKS_RUNNING = new HashMap<Integer, Boolean>();
	
	
	private static int startTask()
	{
		final int task = LAST_TASK++;
		TASKS_RUNNING.put(task, true);
		
		return task;
	}
	
	
	private static int stopTask(int task)
	{
		TASKS_RUNNING.put(task, false);
		return task;
	}
	
	
	/**
	 * Is task still running?
	 * 
	 * @param task task ID
	 * @return is running
	 */
	public static boolean isRunning(int task)
	{
		if (task == -1) return false;
		
		final Boolean state = TASKS_RUNNING.get(task);
		if (state == null || state == false) return false;
		
		return true;
	}
	
	
	public static int taskBuildMainWindow()
	{
		final int task = Tasks.startTask();
		
		new Thread(new Runnable() {
			
			@Override
			public void run()
			{
				try {
					EventQueue.invokeLater(new Runnable() {
						
						@Override
						public void run()
						{
							// -- task begin --
							
							App.inst.window = new WindowMain();
							
							Flags.PROJECT_CHANGED = !Config.CLOSED_WITH_PROJECT_OPEN;
							Tasks.taskOnProjectChanged();
							
							Tasks.stopTask(task);
							
							// -- task end --
						}
					});
				} catch (final Exception e) {
					Log.e(e);
				}
			}
		}).start();
		
		return task;
	}
	
	
	public static int taskReloadSources(final Runnable after)
	{
		final int task = Tasks.startTask();
		
		new Thread(new Runnable() {
			
			@Override
			public void run()
			{
				// -- task begin --
				
				Tasks.taskStoreProjectChanges();
				Sources.initLibrary();
				Tasks.taskTreeRedraw();
				
				if (after != null) after.run();
				
				Tasks.stopTask(task);
				
				// -- task end --
			}
		}).start();
		
		return task;
	}
	
	
	public static void taskTreeCollapse()
	{
		App.getTreeDisplay().treeTable.collapseAll();
	}
	
	
	public static void taskTreeExpand()
	{
		App.getTreeDisplay().treeTable.expandAll();
	}
	
	
	public static void taskDialogAbout()
	{
		Gui.open(new DialogAbout());
	}
	
	
	public static void taskDialogExportToMc()
	{
		Gui.open(new DialogExportToMc());
	}
	
	
	public static void taskDialogHelp()
	{
		Alerts.loading(true);
		final JDialog dialog = new DialogHelp();
		Alerts.loading(false);
		dialog.setVisible(true);
	}
	
	
	public static void taskDialogLog()
	{
		Gui.open(new DialogRuntimeLog());
	}
	
	
	public static void taskDialogImportPack()
	{
		Gui.open(new DialogImportPack());
	}
	
	
	public static void taskTreeSourceRename(String oldSource, String newSource)
	{
		final AssetTreeNode root = App.getTreeDisplay().treeModel.getRoot();
		final AssetTreeProcessor processor = new RenameSourceProcessor(oldSource, newSource);
		root.processThisAndChildren(processor);
		
		taskStoreProjectChanges();
		taskTreeRedraw();
		Projects.markChange();
	}
	
	
	public static void taskTreeRedraw()
	{
		final AssetTreeNode root = App.getTreeDisplay().treeModel.getRoot();
		App.getTreeDisplay().treeModel.notifyNodeChanged(root);
	}
	
	
	public static void taskStoreProjectChanges()
	{
		taskStoreProjectChanges(Projects.getActive());
	}
	
	
	public static void taskStoreProjectChanges(Project proj)
	{
		if (proj == null) return;
		
		final AssetTreeProcessor proc = new SaveToProjectNodeProcessor(proj);
		final AssetTreeNode root = App.getTreeDisplay().treeModel.getRoot();
		
		if (root != null) {
			root.processThisAndChildren(proc);
		}
		
		try {
			proj.saveToTmp();
		} catch (final IOException e) {
			Log.e(e);
		}
		
		Projects.clearChangeFlag();
	}
	
	
	public static void taskDialogExportProject()
	{
		if (!Projects.isOpen()) return;
		
		TaskExportProject.showDialog();
		
	}
	
	
	public static int taskSaveProject(final Runnable afterSave)
	{
		final int task = Tasks.startTask();
		
		new Thread(new Runnable() {
			
			@Override
			public void run()
			{
				// -- task begin --
				
				Alerts.loading(true);
				Tasks.taskStoreProjectChanges();
				Projects.saveProject();
				Alerts.loading(false);
				Projects.clearChangeFlag();
				
				if (afterSave != null) afterSave.run();
				
				Tasks.stopTask(task);
				
				// -- task end --
			}
		}).start();
		
		return task;
	}
	
	
	public static int taskExportProject(final File target, final Runnable afterExport)
	{
		final int task = Tasks.startTask();
		
		new Thread(new Runnable() {
			
			@Override
			public void run()
			{
				// -- task begin --
				
				Tasks.taskStoreProjectChanges();
				
				(new SequenceExportProject(target, afterExport)).run();
				
				Tasks.stopTask(task);
				
				// -- task end --
			}
		}).start();
		
		return task;
	}
	
	
	public static void taskAskToSaveIfChanged(Runnable afterSave)
	{
		if (Flags.GOING_FOR_HALT) {
			if (afterSave != null) afterSave.run();
			return;
		}
		
		if (Projects.isChanged()) {
			taskStoreProjectChanges();
		}
		
		boolean needsSave = false;
		
		if (Projects.isOpen()) {
			Log.f2("Checking project for unsaved changes.");
			
			Alerts.loading(true);
			needsSave = Projects.getActive().needsSave();
			Alerts.loading(false);
		}
		
		if (needsSave) {
			Log.f3("Asking to save.");
			
			//@formatter:off
			final int choice = Alerts.askYesNoCancel(
					App.getFrame(),
					"Unsaved Changes", 
					"There are some unsaved changes\n" + 
					"in the current project.\n" +
					"\n" +
					"Save it now?\n"
			);
			//@formatter:on
			
			if (choice == JOptionPane.CANCEL_OPTION) {
				Log.f3("- User choice CANCEL");
				return; // cancelled
			} else if (choice == JOptionPane.OK_OPTION) {
				Log.f3("- User choice SAVE");
				Tasks.taskSaveProject(afterSave);
			} else {
				Log.f3("- User choice DISCARD");
				if (afterSave != null) afterSave.run();
			}
		} else {
			if (afterSave != null) afterSave.run();
		}
	}
	
	
	public static int taskImportReplacement(final AssetTreeLeaf node)
	{
		final int task = Tasks.startTask();
		
		new Thread(new Runnable() {
			
			@Override
			public void run()
			{
				// -- task begin --
				TaskImportReplacement.run(node.getAssetEntry(), new Runnable() {
					
					@Override
					public void run()
					{
						node.setLibrarySourceIfNeeded(MagicSources.PROJECT);
						App.getSidePanel().redrawPreview();
						Tasks.taskTreeRedraw();
						Projects.markChange();
					}
				});
				
				Tasks.stopTask(task);
				
				// -- task end --
			}
		}).start();
		
		return task;
	}
	
	
	public static void taskCloseProject()
	{
		taskAskToSaveIfChanged(new Runnable() {
			
			@Override
			public void run()
			{
				Projects.closeProject();
				taskOnProjectChanged();
			}
		});
	}
	
	
	public static void taskCloseProjectNoRebuild()
	{
		taskAskToSaveIfChanged(new Runnable() {
			
			@Override
			public void run()
			{
				Projects.closeProject();
			}
		});
	}
	
	
	public static void taskOnProjectChanged()
	{
		if (!Flags.PROJECT_CHANGED) {
			return;
		}
		Flags.PROJECT_CHANGED = false;
		
		taskTreeRebuild();
		
		taskOnProjectPropertiesChanged();
		App.getMenu().updateEnabledItems();
		taskRedrawRecentProjectsMenu();
	}
	
	
	public static void taskEditModFilters()
	{
		final File f = OsUtils.getAppDir(Paths.FILE_CFG_MODFILTERS);
		
		editTextFile(f);
	}
	
	
	public static void taskEditModGroups()
	{
		final File f = OsUtils.getAppDir(Paths.FILE_CFG_MODGROUPS);
		
		editTextFile(f);
	}
	
	
	private static void editTextFile(File f)
	{
		if (Config.USE_INTERNAL_META_EDITOR) {
			RpwDialog dlg;
			try {
				Alerts.loading(true);
				dlg = new DialogEditText(f);
				Alerts.loading(false);
				dlg.setVisible(true);
			} catch (final IOException e) {
				Log.e(e);
			}
		} else {
			DesktopApi.editText(f);
		}
	}
	
	
	public static void taskTreeSaveAndRebuild()
	{
		Tasks.taskStoreProjectChanges();
		Tasks.taskTreeRebuild();
	}
	
	
	public static void taskTreeRebuild()
	{
		if (App.getTreeDisplay() != null) App.getTreeDisplay().updateRoot();
		if (App.getSidePanel() != null) App.getSidePanel().updatePreview(null);
	}
	
	
	public static void taskDeleteEmptyDirsFromProject()
	{
		FileUtils.deleteEmptyDirs(Projects.getActive().getAssetsDirectory());
	}
	
	
	public static int taskDeleteProject(final String identifier)
	{
		final int task = Tasks.startTask();
		
		new Thread(new Runnable() {
			
			@Override
			public void run()
			{
				// -- task begin --
				
				final File dir = new File(OsUtils.getAppDir(Paths.DIR_PROJECTS), identifier);
				FileUtils.delete(dir, true);
				
				Tasks.stopTask(task);
				// -- task end --
			}
		}).start();
		
		return task;
	}
	
	
	public static int taskCreateModConfigFiles()
	{
		final int task = Tasks.startTask();
		
		new Thread(new Runnable() {
			
			@Override
			public void run()
			{
				// -- task begin --
				
				TaskCreateModConfigFiles.run();
				Tasks.stopTask(task);
				
				// -- task end --
			}
		}).start();
		
		return task;
	}
	
	
	public static int taskLoadVanillaStructure()
	{
		final int task = Tasks.startTask();
		
		new Thread(new Runnable() {
			
			@Override
			public void run()
			{
				// -- task begin --
				
				TaskLoadVanillaStructure.run();
				Tasks.stopTask(task);
				
				// -- task end --
			}
		}).start();
		
		return task;
	}
	
	
	public static int taskReloadVanilla()
	{
		final String version = TaskReloadVanilla.getUserChoice(false);
		
		if (version == null) return -1;
		
		final int task = Tasks.startTask();
		
		new Thread(new Runnable() {
			
			@Override
			public void run()
			{
				// -- task begin --
				
				TaskReloadVanilla.run(version);
				Tasks.taskTreeSaveAndRebuild();
				
				Tasks.stopTask(task);
				
				// -- task end --
			}
			
		}).start();
		
		return task;
	}
	
	
	public static int taskReloadVanillaOrDie()
	{
		final String version = TaskReloadVanilla.getUserChoice(true);
		
		if (version == null || version.length() == 0) {
			//@formatter:off
			App.die(
				"RPW cannot run without Minecraft assets.\n" +
				"Aborting."
			);
			//@formatter:on
			
			return -1;
		}
		
		final int task = Tasks.startTask();
		
		new Thread(new Runnable() {
			
			@Override
			public void run()
			{
				// -- task begin --
				
				TaskReloadVanilla.run(version);
				
				Tasks.stopTask(task);
				
				// -- task end --
			}
			
		}).start();
		
		return task;
	}
	
	
	public static void taskExit()
	{
		taskAskToSaveIfChanged(new Runnable() {
			
			@Override
			public void run()
			{
				Config.CLOSED_WITH_PROJECT_OPEN = Projects.isOpen();
				Flags.GOING_FOR_HALT = true;
				Config.save();
				App.inst.deinit();
				System.exit(0);
			}
		});
		
	}
	
	
	public static void taskNewProject()
	{
		taskDialogNewProject();
		
	}
	
	
	public static void taskOpenProject()
	{
		taskDialogManageProjects();
	}
	
	
	public static void taskOpenProject(final String name)
	{
		taskAskToSaveIfChanged(new Runnable() {
			
			@Override
			public void run()
			{
				new Thread(new Runnable() {
					
					@Override
					public void run()
					{
						// -- task begin --
						
						Log.f1("Loading project " + name);
						Alerts.loading(true);
						Projects.closeProject();
						Projects.openProject(name);
						Flags.PROJECT_CHANGED = true;
						Tasks.taskOnProjectChanged();
						Alerts.loading(false);
						
						// -- task end --
					}
					
				}).start();
				
			}
		});
	}
	
	
	public static void taskSaveProjectAs(String name, String title)
	{
		Log.f1("Saving project '" + Projects.getActive().getName() + "' as '" + name + "'");
		
		taskStoreProjectChanges(); // save current project to TMP
		
		final Project newProject = new Project(name);
		
		// copy old project TMP to new project TMP
		final File currentDir = Projects.getActive().getProjectDirectory();
		final File targetDir = newProject.getProjectDirectory();
		
		try {
			FileUtils.copyDirectory(currentDir, targetDir);
		} catch (final IOException e) {
			Log.e("An error occured while\ncopying project files.");
			FileUtils.delete(targetDir, true); // cleanup
			return;
		}
		
		newProject.reload(); // load from new project TMP
		newProject.setTitle(title); // set a new title
		
		// mark new project as active
		Projects.setActive(newProject);
		
		taskOnProjectChanged();
	}
	
	
	public static void taskDeleteResourcepack(String packname)
	{
		final File f = new File(OsUtils.getAppDir(Paths.DIR_RESOURCEPACKS), packname);
		FileUtils.delete(f, true);
	}
	
	
	public static void taskDialogProjectProperties()
	{
		Gui.open(new DialogProjectProperties());
	}
	
	
	public static void taskDialogProjectSummary()
	{
		Gui.open(new DialogProjectSummary());
	}
	
	
	public static void taskDialogManageLibrary()
	{
		Gui.open(new DialogManageLibrary());
	}
	
	
	public static void taskDialogManageProjects()
	{
		Gui.open(new DialogOpenProject());
	}
	
	
	public static void taskDialogNewProject()
	{
		Gui.open(new DialogNewProject());
	}
	
	
	public static void taskDialogSaveAs()
	{
		Gui.open(new DialogSaveAs());
	}
	
	
	public static void taskDialogSettings()
	{
		Gui.open(new DialogConfigureEditors());
	}
	
	
	public static void taskRedrawRecentProjectsMenu()
	{
		App.getMenu().updateRecentProjects();
	}
	
	
	public static void taskOnProjectPropertiesChanged()
	{
		taskUpdateTitlebar();
		App.getSidePanel().updateProjectInfo();
	}
	
	
	public static void taskUpdateTitlebar()
	{
		App.setTitle(App.getWindowTitle());
	}
	
	
	public static void taskEditAsset(AssetTreeLeaf node)
	{
		TaskModifyAsset.edit(node, false);
	}
	
	
	public static void taskEditMeta(AssetTreeLeaf node)
	{
		TaskModifyAsset.edit(node, true);
	}
	
	
	public static void taskDialogManageMcPacks()
	{
		Gui.open(new DialogManageMcPacks());
	}
	
	
	public static void taskOpenProjectFolder()
	{
		if (Projects.isOpen() && !DesktopApi.open(Projects.getActive().getProjectDirectory())) {
			//@formatter:off
			Alerts.error(
					App.getFrame(),
					"Could not open directory, your\n" +
					"platform is not supported.\n" +
					"\n" +
					"Check log file for details."
			);
			//@formatter:on
		}
		
	}
	
	
	public static void checkUpdate()
	{
		TaskCheckUpdate.run();
	}
	
	
	public static void taskLoadHelp()
	{
		(new Thread(new Runnable() {
			
			@Override
			public void run()
			{
				HelpStore.load();
				
			}
		})).start();
		
	}
	
	
	public static void taskOpenSoundWizard()
	{
		if (!Projects.isOpen()) return;
		
		Alerts.loading(true);
		final DialogSoundWizard dlg = new DialogSoundWizard();
		Alerts.loading(false);
		dlg.setVisible(true);
		
	}
	
	
	public static void taskShowChangelog()
	{
		if (Config.LAST_RUN_VERSION != Const.VERSION_SERIAL) {
			Gui.open(new DialogChangelog());
		}
	}
	
	
	public static int taskPopulateProjectFromPack(final File pack, final Runnable after)
	{
		final int task = Tasks.startTask();
		
		new Thread(new Runnable() {
			
			@Override
			public void run()
			{
				// -- task begin --
				
				(new SequencePopulateProjectFromPack(pack, after)).run();
				
				Tasks.stopTask(task);
				
				// -- task end --
			}
		}).start();
		
		return task;
		
	}
	
	public static void taskDialogExportToStitch()
	{
	  Gui.open(new com.pixbits.rpw.gui.windows.dialogs.DialogExportStitch());
	}
	
	public static void taskDialogImportFromStitch()
	{
	   Gui.open(new com.pixbits.rpw.gui.windows.dialogs.DialogImportStitch());

	}
}
