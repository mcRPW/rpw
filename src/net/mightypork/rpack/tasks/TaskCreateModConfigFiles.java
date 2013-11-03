package net.mightypork.rpack.tasks;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import net.mightypork.rpack.Paths;
import net.mightypork.rpack.utils.FileUtils;
import net.mightypork.rpack.utils.Log;
import net.mightypork.rpack.utils.OsUtils;


public class TaskCreateModConfigFiles {

	public static void run() {

		try {

			File groups = OsUtils.getAppDir(Paths.FILE_CFG_MODGROUPS);
			File filters = OsUtils.getAppDir(Paths.FILE_CFG_MODFILTERS);


			if (!groups.exists()) {
				InputStream in = FileUtils.getResource("/data/tree/groupsMod.txt");

				OutputStream out = new FileOutputStream(groups);

				FileUtils.copyStream(in, out);
			}


			if (!filters.exists()) {
				InputStream in = FileUtils.getResource("/data/tree/filtersMod.txt");

				OutputStream out = new FileOutputStream(filters);

				FileUtils.copyStream(in, out);
			}

		} catch (Exception e) {
			Log.e("Error while creating config files for mod assets.", e);
		}
	}

}
