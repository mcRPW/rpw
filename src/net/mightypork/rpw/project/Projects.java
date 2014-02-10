package net.mightypork.rpw.project;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.mightypork.rpw.Config;
import net.mightypork.rpw.Flags;
import net.mightypork.rpw.Paths;
import net.mightypork.rpw.tasks.Tasks;
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.files.OsUtils;
import net.mightypork.rpw.utils.files.SimpleConfig;
import net.mightypork.rpw.utils.logging.Log;


public class Projects {

	public static Project active = null;


	public static void markChange() {

		Flags.PROJECT_EDITED = true;
	}


	public static boolean isChanged() {

		return isProjectOpen() && Flags.PROJECT_EDITED;
	}


	public static void clearChangeFlag() {

		Flags.PROJECT_EDITED = false;
	}


	public static List<String> getProjectNames() {

		List<File> dirs = FileUtils.listDirectory(OsUtils.getAppDir(Paths.DIR_PROJECTS));
		List<String> names = new ArrayList<String>();

		for (File f : dirs) {
			if (!f.exists()) continue;
			if (!f.isDirectory()) continue;
			names.add(f.getName());
		}

		Collections.sort(names);

		return names;
	}


	public static void openProject(String name) {

		Tasks.taskCloseProjectNoRebuild();

		Project p = new Project(name);
		setActive(p);
	}


	public static void openNewProject(String name) {

		closeProject();
		Tasks.taskTreeRebuild();
		openProject(name);
		Tasks.taskStoreProjectChanges();
		saveProject();

	}


	public static void setActive(Project project) {

		Flags.PROJECT_CHANGED = true;

		active = project;
		markActiveProjectAsRecent();
	}


	public static void saveProject() {

		if (active != null) {
			active.save();
		}
	}


	public static void closeProject() {

		Flags.PROJECT_CHANGED = (active != null);

		active = null;
	}


	public static Project getActive() {

		return active;
	}


	public static boolean isProjectOpen() {

		return active != null;
	}


	/**
	 * Get list of the most recently opened projects.
	 * 
	 * @return the list
	 */
	public static List<String> getRecentProjects() {

		File file = OsUtils.getAppDir(Paths.FILE_RECENTPROJECTS);

		List<String> listFromFile = null;

		try {
			listFromFile = SimpleConfig.listFromFile(file);
		} catch (IOException e) {
			return new ArrayList<String>();
		}

		return processRecentProjectsList(listFromFile);
	}


	/**
	 * Remove duplicates and invalid names from recent projects list
	 * 
	 * @param list the list
	 * @return processed list
	 */
	private static List<String> processRecentProjectsList(List<String> list) {

		List<String> recentProjects = new ArrayList<String>();
		List<String> allValidProjects = getProjectNames();

		for (String proj : list) {
			if (recentProjects.contains(proj)) continue; // already added to list
			if (!allValidProjects.contains(proj)) continue; // not a valid project

			recentProjects.add(proj);
		}

		return recentProjects;
	}


	/**
	 * Put project name at the head of recent projects list
	 * 
	 * @param project project name
	 */
	public static void markProjectAsRecent(String project) {

		File file = OsUtils.getAppDir(Paths.FILE_RECENTPROJECTS);

		List<String> recentList = getRecentProjects();

		recentList.add(0, project);

		List<String> forSave = processRecentProjectsList(recentList);

		try {
			SimpleConfig.listToFile(file, forSave);
		} catch (IOException e) {
			Log.e("Could not save recent projects list.", e);
		}
	}


	public static void markActiveProjectAsRecent() {

		Project proj = getActive();
		if (proj == null) return;
		markProjectAsRecent(proj.getDirName());
	}


	public static void openLastProject() {

		if (!Config.CLOSED_WITH_PROJECT_OPEN) return;

		List<String> recentProjects = Projects.getRecentProjects();
		if (recentProjects.size() == 0) return;

		Tasks.taskOpenProject(recentProjects.get(0));
	}
}
