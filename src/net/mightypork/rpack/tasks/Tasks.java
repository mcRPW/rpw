package net.mightypork.rpack.tasks;


import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import net.mightypork.rpack.App;
import net.mightypork.rpack.Config;
import net.mightypork.rpack.Const;
import net.mightypork.rpack.Flags;
import net.mightypork.rpack.Paths;
import net.mightypork.rpack.gui.windows.*;
import net.mightypork.rpack.help.HelpStore;
import net.mightypork.rpack.hierarchy.processors.RenameSourceProcessor;
import net.mightypork.rpack.hierarchy.processors.SaveToProjectNodeProcessor;
import net.mightypork.rpack.hierarchy.tree.AssetTreeLeaf;
import net.mightypork.rpack.hierarchy.tree.AssetTreeNode;
import net.mightypork.rpack.hierarchy.tree.AssetTreeProcessor;
import net.mightypork.rpack.library.MagicSources;
import net.mightypork.rpack.library.Sources;
import net.mightypork.rpack.project.Project;
import net.mightypork.rpack.project.Projects;
import net.mightypork.rpack.utils.DesktopApi;
import net.mightypork.rpack.utils.FileUtils;
import net.mightypork.rpack.utils.Log;
import net.mightypork.rpack.utils.OsUtils;


/**
 * Tasks class<br>
 * Each task returns ID, to be checked with isRunning().
 * 
 * @author MightyPork
 */
public class Tasks {

	private static int LAST_TASK = 0;
	private static HashMap<Integer, Boolean> TASKS_RUNNING = new HashMap<Integer, Boolean>();


	private static int startTask() {

		int task = LAST_TASK++;
		TASKS_RUNNING.put(task, true);

		return task;
	}


	private static int stopTask(int task) {

		TASKS_RUNNING.put(task, false);
		return task;
	}


	/**
	 * Is task still running?
	 * 
	 * @param task task ID
	 * @return is running
	 */
	public static boolean isRunning(int task) {

		if (task == -1) return false;

		Boolean state = TASKS_RUNNING.get(task);
		if (state == null || state == false) return false;

		return true;
	}


	public static int taskBuildMainWindow() {

		final int task = Tasks.startTask();

		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					EventQueue.invokeLater(new Runnable() {

						@Override
						public void run() {

							// -- task begin --

							App.inst.window = new WindowMain();

							Flags.PROJECT_CHANGED = !Config.CLOSED_WITH_PROJECT_OPEN;
							Tasks.taskOnProjectChanged();

							Tasks.stopTask(task);

							// -- task end --
						}
					});
				} catch (Exception e) {
					Log.e(e);
				}
			}
		}).start();

		return task;
	}


	public static int taskReloadSources(final Runnable after) {

		final int task = Tasks.startTask();

		new Thread(new Runnable() {

			@Override
			public void run() {

				// -- task begin --

				Tasks.taskPushTreeToProject();
				Sources.initLibrary();
				Tasks.taskTreeRedraw();

				if (after != null) after.run();

				Tasks.stopTask(task);

				// -- task end --
			}
		}).start();

		return task;
	}


	public static void taskTreeCollapse() {

		App.getTreeDisplay().treeTable.collapseAll();
	}


	public static void taskTreeExpand() {

		App.getTreeDisplay().treeTable.expandAll();
	}


	public static void taskDialogAbout() {

		JDialog dialog = new DialogAbout();

		dialog.setVisible(true);
	}


	public static void taskDialogExportToMc() {

		JDialog dialog = new DialogExportToMc();
		dialog.setVisible(true);
	}


	public static void taskDialogHelp() {

		Alerts.loading(true);
		JDialog dialog = new DialogHelp();
		Alerts.loading(false);
		dialog.setVisible(true);
	}


	public static void taskDialogLog() {

		JDialog dialog = new DialogShowLog();
		dialog.setVisible(true);
	}


	public static void taskDialogImportPack() {

		JDialog dialog = new DialogImportPack();
		dialog.setVisible(true);
	}


	public static void taskTreeSourceRename(String oldSource, String newSource) {

		AssetTreeNode root = App.getTreeDisplay().treeModel.getRoot();
		AssetTreeProcessor processor = new RenameSourceProcessor(oldSource, newSource);
		root.processThisAndChildren(processor);

		taskPushTreeToProject();
		taskTreeRedraw();
		Projects.markChange();
	}


	public static void taskTreeRedraw() {

		Log.f3("Redrawing tree");

		AssetTreeNode root = App.getTreeDisplay().treeModel.getRoot();
		App.getTreeDisplay().treeModel.notifyNodeChanged(root);
	}


	public static void taskPushTreeToProject() {

		Log.f3("Pushing tree data to project");
		taskPushTreeToProject(Projects.getActive());
	}


	public static void taskPushTreeToProject(Project proj) {

		if (proj == null) return;

		AssetTreeProcessor proc = new SaveToProjectNodeProcessor(proj);
		AssetTreeNode root = App.getTreeDisplay().treeModel.getRoot();
		root.processThisAndChildren(proc);
	}


	public static void taskDialogExportProject() {

		if (Projects.getActive() == null) return;

		TaskExportProject.showDialog();

	}


	public static int taskSaveProject(final Runnable afterSave) {

		final int task = Tasks.startTask();

		new Thread(new Runnable() {

			@Override
			public void run() {

				// -- task begin --

				Alerts.loading(true);
				Tasks.taskPushTreeToProject();
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


	public static int taskExportProject(final File target, final Runnable afterExport) {

		final int task = Tasks.startTask();

		new Thread(new Runnable() {

			@Override
			public void run() {

				// -- task begin --

				Alerts.loading(true);
				Tasks.taskPushTreeToProject();

				try {
					TaskExportProject.doExport(target);
				} catch (Exception e) {

					Log.e(e);

					//@formatter:off
					Alerts.error(
							App.getFrame(),
							"An error occured while exporting.\n" +
							"Check log file for details."
					);
					//@formatter:on					
				}

				Alerts.loading(false);

				if (afterExport != null) afterExport.run();

				Tasks.stopTask(task);

				// -- task end --
			}
		}).start();

		return task;
	}


	public static void taskAskToSaveIfChanged(Runnable afterSave) {

		if (Projects.getActive() != null && Projects.isChanged()) {
			Log.f3("Project is marked as edited. Asking to save.");

			//@formatter:off
			int choice = Alerts.askYesNoCancel(
					App.getFrame(),
					"Project Changed", 
					"There are some unsaved changes\n" +
					"in the current project.\n" +
					"\n" +
					"Do you want to save it?"
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


	public static int taskImportReplacement(final AssetTreeLeaf node) {

		final int task = Tasks.startTask();

		new Thread(new Runnable() {

			@Override
			public void run() {

				// -- task begin --
				TaskImportReplacement.run(node.getAssetEntry(), new Runnable() {

					@Override
					public void run() {

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


	public static void taskCloseProject() {

		taskAskToSaveIfChanged(new Runnable() {

			@Override
			public void run() {

				Projects.closeProject();
				taskOnProjectChanged();
			}
		});
	}


	public static void taskCloseProjectNoRebuild() {

		taskAskToSaveIfChanged(new Runnable() {

			@Override
			public void run() {

				Projects.closeProject();
			}
		});
	}


	public static void taskOnProjectChanged() {

		if (!Flags.PROJECT_CHANGED) {
			return;
		}
		Flags.PROJECT_CHANGED = false;

		taskTreeRebuild();

		taskOnProjectPropertiesChanged();
		App.getMenu().updateEnabledItems();
		taskRedrawRecentProjectsMenu();
	}


	public static void taskEditModFilters() {

		if (!DesktopApi.editText(OsUtils.getAppDir(Paths.FILE_CFG_MODFILTERS))) {
			//@formatter:off
			Alerts.error(
					App.getFrame(),
					"Could not edit file, your\n" +
					"platform is not supported.\n" +
					"\n" +
					"Check log file for details."
			);
			//@formatter:on
		}
	}


	public static void taskEditModGroups() {

		if (!DesktopApi.editText(OsUtils.getAppDir(Paths.FILE_CFG_MODGROUPS))) {
			//@formatter:off
			Alerts.error(
					App.getFrame(),
					"Could not edit file, your\n" +
					"platform is not supported.\n" +
					"\n" +
					"Check log file for details."
			);
			//@formatter:on
		}
	}


	public static void taskTreeSaveAndRebuild() {

		Tasks.taskPushTreeToProject();
		Tasks.taskTreeRebuild();
	}


	public static void taskTreeRebuild() {

		Log.f3("Rebuilding tree.");

		if (App.getTreeDisplay() != null) App.getTreeDisplay().updateRoot();
		if (App.getSidePanel() != null) App.getSidePanel().updatePreview(null);
	}


	public static void taskDeleteEmptyDirsFromProject() {

		FileUtils.deleteEmptyDirs(Projects.getActive().getAssetsBaseDirectory());
	}


	public static int taskDeleteProject(final String identifier) {

		final int task = Tasks.startTask();

		new Thread(new Runnable() {

			@Override
			public void run() {

				// -- task begin --

				File dir = new File(OsUtils.getAppDir(Paths.DIR_PROJECTS), identifier);
				FileUtils.delete(dir, true);

				Tasks.stopTask(task);
				// -- task end --
			}
		}).start();

		return task;
	}


	public static int taskCreateModConfigFiles() {

		final int task = Tasks.startTask();

		new Thread(new Runnable() {

			@Override
			public void run() {

				// -- task begin --

				TaskCreateModConfigFiles.run();
				Tasks.stopTask(task);

				// -- task end --
			}
		}).start();

		return task;
	}


	public static int taskLoadVanillaStructure() {

		final int task = Tasks.startTask();

		new Thread(new Runnable() {

			@Override
			public void run() {

				// -- task begin --

				TaskLoadVanillaStructure.run();
				Tasks.stopTask(task);

				// -- task end --
			}
		}).start();

		return task;
	}


	public static int taskReloadVanilla() {

		final String version = TaskReloadVanilla.getUserChoice(false);

		if (version == null) return -1;

		final int task = Tasks.startTask();

		new Thread(new Runnable() {

			@Override
			public void run() {

				// -- task begin --

				TaskReloadVanilla.run(version);
				Tasks.taskTreeSaveAndRebuild();

				Tasks.stopTask(task);
				Alerts.info(App.getFrame(), "Vanilla Pack reloaded.");

				// -- task end --
			}

		}).start();

		return task;
	}


	public static int taskReloadVanillaOrDie() {

		final String version = TaskReloadVanilla.getUserChoice(true);

		if (version == null || version.length() == 0) {

			//@formatter:off
			App.die(
				"Application cannot run without a\n" +
				"Vanilla resource pack. Aborting."
			);
			//@formatter:on

			return -1;
		}

		final int task = Tasks.startTask();

		new Thread(new Runnable() {

			@Override
			public void run() {

				// -- task begin --

				TaskReloadVanilla.run(version);

				Tasks.stopTask(task);

				// -- task end --
			}

		}).start();

		return task;
	}


	public static void taskExit() {

		taskAskToSaveIfChanged(new Runnable() {

			@Override
			public void run() {

				Config.CLOSED_WITH_PROJECT_OPEN = Projects.isProjectOpen();
				Config.save();
				App.inst.deinit();
				System.exit(0);
			}
		});

	}


	public static void taskNewProject() {

		taskAskToSaveIfChanged(new Runnable() {

			@Override
			public void run() {

				taskDialogNewProject();
			}
		});
	}


	public static void taskOpenProject() {

		taskAskToSaveIfChanged(new Runnable() {

			@Override
			public void run() {

				taskDialogOpenProject();
			}
		});
	}


	public static void taskOpenProject(final String name) {

		taskAskToSaveIfChanged(new Runnable() {

			@Override
			public void run() {

				new Thread(new Runnable() {

					@Override
					public void run() {

						// -- task begin --

						Log.f2("Loading project " + name);
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


	public static void taskSaveProjectAs(String name) {

		Project newProject = new Project(name);

		File currentDir = Projects.getActive().getProjectDirectory();
		File targetDir = newProject.getProjectDirectory();

		try {
			FileUtils.copyDirectory(currentDir, targetDir);
		} catch (IOException e) {
			Log.e("An error occured while\ncopying project files.");
			FileUtils.delete(targetDir, true); // cleanup
			return;
		}

		// property file was copied over, must re-build it
		newProject.saveProperties();

		Projects.setActive(newProject);
		taskOnProjectChanged();
	}


	public static void taskDeleteResourcepack(String packname) {

		File f = new File(OsUtils.getAppDir(Paths.DIR_RESOURCEPACKS), packname);
		FileUtils.delete(f, true);
	}


	public static void taskDialogProjectProperties() {

		JDialog dialog = new DialogProjectProperties();
		dialog.setVisible(true);
	}


	public static void taskDialogProjectSummary() {

		JDialog dialog = new DialogProjectSummary();
		dialog.setVisible(true);
	}


	public static void taskDialogManageLibrary() {

		JDialog dialog = new DialogManageLibrary();
		dialog.setVisible(true);
	}


	public static void taskDialogManageProjects() {

		JDialog dialog = new DialogManageProjects();
		dialog.setVisible(true);
	}


	private static void taskDialogOpenProject() {

		JDialog dialog = new DialogOpenProject();
		dialog.setVisible(true);
	}


	public static void taskDialogNewProject() {

		JDialog dialog = new DialogNewProject();
		dialog.setVisible(true);
	}


	public static void taskDialogSaveAs() {

		JDialog dialog = new DialogSaveAs();
		dialog.setVisible(true);
	}


	public static void taskDialogSettings() {

		JDialog dialog = new DialogConfigureEditors();
		dialog.setVisible(true);
	}


	public static void taskRedrawRecentProjectsMenu() {

		App.getMenu().updateRecentProjects();
	}


	public static void taskOnProjectPropertiesChanged() {

		String title = Const.WINDOW_TITLE;

		if (Projects.getActive() != null) {
			title = Projects.getActive().getProjectName() + "  •  " + title;
		}

		App.getFrame().setTitle(title);
		App.getSidePanel().updateProjectInfo();
	}


	public static void taskEditAsset(AssetTreeLeaf node) {

		TaskModifyAsset.edit(node, false);
	}


	public static void taskEditMeta(AssetTreeLeaf node) {

		TaskModifyAsset.edit(node, true);
	}


	public static void taskDialogManageMcPacks() {


		JDialog dialog = new DialogManageMcPacks();
		dialog.setVisible(true);
	}


	public static void taskOpenProjectFolder() {

		DesktopApi.open(Projects.getActive().getProjectDirectory());

	}


	public static void checkUpdate() {

		TaskCheckUpdate.run();
	}


	public static void taskLoadHelp() {

		(new Thread(new Runnable() {
			
			@Override
			public void run() {
			
				HelpStore.load();
				
			}
		})).start();
		
	}
}
