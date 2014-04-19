package net.mightypork.rpw.tasks;


import java.io.File;
import java.io.IOException;

import net.mightypork.rpw.Paths;
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.files.OsUtils;
import net.mightypork.rpw.utils.logging.Log;


public class TaskCreateModConfigFiles {
	
	public static void run()
	{
		final File groups = OsUtils.getAppDir(Paths.FILE_CFG_MODGROUPS);
		final File filters = OsUtils.getAppDir(Paths.FILE_CFG_MODFILTERS);
		
		if (!groups.exists()) {
			try {
				FileUtils.resourceToFile("/data/tree/groupsMod.txt", groups);
			} catch (final IOException e) {
				Log.e(e);
			}
		}
		
		if (!filters.exists()) {
			try {
				FileUtils.resourceToFile("/data/tree/filtersMod.txt", filters);
			} catch (final IOException e) {
				Log.e(e);
			}
		}
		
	}
	
}
