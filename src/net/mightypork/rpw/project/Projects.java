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

    private static Project active = null;

    public static Project getActive() {
        return active;
    }


    public static boolean isOpen() {
        return active != null;
    }


    /**
     * Mark project change
     */
    public static void markChange() {
        Tasks.taskStoreProjectChanges();
        Flags.PROJECT_EDITED = true;
    }


    /**
     * @return true if change was marked
     */
    public static boolean isChanged() {
        return isOpen() && Flags.PROJECT_EDITED;
    }


    /**
     * Reset change flag
     */
    public static void clearChangeFlag() {
        Flags.PROJECT_EDITED = false;
    }


    public static List<String> getProjectNames() {
        final List<File> dirs = FileUtils.listDirectory(OsUtils.getAppDir(Paths.DIR_PROJECTS));
        final List<String> names = new ArrayList<String>();

        for (final File f : dirs) {
            if (!f.exists()) continue;
            if (!f.isDirectory()) continue;
            names.add(f.getName());
        }

        Collections.sort(names);

        return names;
    }


    public static void openProject(String name) {
        Tasks.taskCloseProjectNoRebuild();

        final Project p = new Project(name);
        Config.LIBRARY_VERSION = p.getCurrentMcVersion();
        setActive(p);
    }


    public static void openNewProject(String name) {
        closeProject();
        Tasks.taskTreeRebuild();
        openProject(name);
        Tasks.taskStoreProjectChanges();
    }


    public static void setActive(Project project) {
        Flags.PROJECT_CHANGED = true;

        active = project;
        markActiveProjectAsRecent();
    }


    public static void revertProject() {
        if (active != null) {
            active.revert();
        }
    }


    public static void closeProject() {
        Flags.PROJECT_CHANGED = (active != null);

        active = null;
    }


    /**
     * Get list of the most recently opened projects.
     *
     * @return the list
     */
    public static List<String> getRecentProjects() {
        final File file = OsUtils.getAppDir(Paths.FILE_RECENTPROJECTS);

        List<String> listFromFile = null;

        try {
            listFromFile = SimpleConfig.listFromFile(file);
        } catch (final IOException e) {
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
        final List<String> recentProjects = new ArrayList<String>();
        final List<String> allValidProjects = getProjectNames();

        for (final String proj : list) {
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
        final File file = OsUtils.getAppDir(Paths.FILE_RECENTPROJECTS);

        final List<String> recentList = getRecentProjects();

        recentList.add(0, project);

        final List<String> forSave = processRecentProjectsList(recentList);

        try {
            SimpleConfig.listToFile(file, forSave);
        } catch (final IOException e) {
            Log.e("Could not save recent projects list.", e);
        }
    }


    public static void markActiveProjectAsRecent() {
        final Project proj = getActive();
        if (proj == null) return;
        markProjectAsRecent(proj.getName());
    }


    public static void openLastProject() {
        if (!Config.CLOSED_WITH_PROJECT_OPEN) {
            return;
        }

        final List<String> recentProjects = Projects.getRecentProjects();
        if (recentProjects.size() == 0) return;

        Tasks.taskOpenProject(recentProjects.get(0));
    }
}
