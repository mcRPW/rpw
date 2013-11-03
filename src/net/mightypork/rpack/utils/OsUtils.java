package net.mightypork.rpack.utils;


import java.io.File;
import java.util.List;

import net.mightypork.rpack.App;
import net.mightypork.rpack.Flags;
import net.mightypork.rpack.Paths;


public class OsUtils {

	public static enum EnumOS {
		linux, macos, solaris, unknown, windows;

		public boolean isLinux() {

			return this == linux || this == solaris;
		}


		public boolean isMac() {

			return this == macos;
		}


		public boolean isWindows() {

			return this == windows;
		}
	}

	private static File appDir = null;

	private static File mcDir = null;


	/**
	 * Get App dir, ensure it exists
	 * 
	 * @return app dir
	 */
	public static File getAppDir() {

		if (OsUtils.appDir == null) OsUtils.appDir = OsUtils.getWorkDir(Paths.APP_DIR, true);
		return OsUtils.appDir;
	}


	/**
	 * Get App sub-folder, don't try to create
	 * 
	 * @param subfolderName
	 * @return the folder
	 */
	public static File getAppDir(String subfolderName) {

		return new File(OsUtils.getAppDir(), subfolderName);
	}


	/**
	 * Get App sub-folder
	 * 
	 * @param subfolderName
	 * @param create whether create it if not exists
	 * @return the folder
	 */
	public static File getAppDir(String subfolderName, boolean create) {

		File f = new File(OsUtils.getAppDir(), subfolderName);

		if (!f.exists() && create) {
			if (!f.mkdirs()) {
				//@formatter:off
				App.die(
						"Could not create directory:\n\n" + 
						f+"\n\n" +
						"Please, check the filesystem."
						);
				//@formatter:on
			}
		}

		return f;
	}


	/**
	 * Get MC dir
	 * 
	 * @return mc dir
	 */
	public static File getMcDir() {

		if (OsUtils.mcDir == null) OsUtils.mcDir = OsUtils.getWorkDir(Paths.MC_DIR, false);
		return OsUtils.mcDir;
	}


	/**
	 * Get MC sub-folder, don't try to create
	 * 
	 * @param subfolderName
	 * @return the folder
	 */
	public static File getMcDir(String subfolderName) {

		return new File(OsUtils.getMcDir(), subfolderName);
	}


	/**
	 * Get MC sub-folder
	 * 
	 * @param subfolderName
	 * @param create whether create it if not exists
	 * @return the folder
	 */
	public static File getMcDir(String subfolderName, boolean create) {

		File f = new File(OsUtils.getMcDir(), subfolderName);

		if (!f.exists() && create) {
			if (!f.mkdirs()) {
				//@formatter:off
				App.die(
						"Could not create directory:\n\n" + 
						f+"\n\n" +
						"Please, check the filesystem."
						);
				//@formatter:on
			}
		}

		return f;
	}


	public static EnumOS getOs() {

		String s = System.getProperty("os.name").toLowerCase();

		if (s.contains("win")) {
			return EnumOS.windows;
		}

		if (s.contains("mac")) {
			return EnumOS.macos;
		}

		if (s.contains("solaris")) {
			return EnumOS.solaris;
		}

		if (s.contains("sunos")) {
			return EnumOS.solaris;
		}

		if (s.contains("linux")) {
			return EnumOS.linux;
		}

		if (s.contains("unix")) {
			return EnumOS.linux;
		} else {
			return EnumOS.unknown;
		}
	}


	private static File getWorkDir(String dirname, boolean create) {

		String userhome = System.getProperty("user.home", ".");
		File file;

		switch (getOs()) {
			case linux:
			case solaris:
				file = new File(userhome, "." + dirname + '/');
				break;

			case windows:
				String appdata = System.getenv("APPDATA");

				if (appdata != null) {
					file = new File(appdata, "." + dirname + '/');
				} else {
					file = new File(userhome, "." + dirname + '/');
				}

				break;

			case macos:
				file = new File(userhome, "Library/Application Support/" + dirname);
				break;

			default:
				file = new File(userhome, dirname + "/");
				break;
		}

		if (!file.exists() || !file.isDirectory()) {
			if (create) {
				if (!file.mkdirs()) {

					//@formatter:off
					App.die(
							"Working directory could not be created:\n\n" + 
							file+"\n\n" +
							"Check filesystem and try again."
							);
					//@formatter:on
				}
			}
		}

		return file;
	}


	private static void checkMinecraft() {

		File mcdir = OsUtils.getMcDir();
		if (!mcdir.exists() || !mcdir.isDirectory()) {

			//@formatter:off
			App.die(
					"Minecraft installation not found,\n" +
					"game directory does not exist:\n\n" +
					mcdir + "\n\n" +
					"Install Minecraft and try again."
					);
			//@formatter:on
		}

		File mcversions = OsUtils.getMcDir("versions");
		if (!mcversions.exists() || !mcversions.isDirectory()) {

			//@formatter:off
			App.die(
					"Minecraft 'versions' folder is missing.\n\n" +
					mcversions + "\n\n" +
					"Your Minecraft is probably too old."
					);
			//@formatter:on
		}

		List<File> versions = FileUtils.listDirectory(mcversions);

		int valid = 0;
		for (File f : versions) {
			if (f.exists() && f.isDirectory()) {
				File jar = new File(f, f.getName() + ".jar");

				if (jar.exists() && jar.isFile()) valid++;
			}
		}

		if (valid == 0) {

			//@formatter:off
			App.die(
					"Minecraft 'versions' folder contains no jar files.\n\n" +
					mcversions + "\n\n" +
					"Run Minecraft and try again."
					);
			//@formatter:on
		}
	}


	public static void initDirs() {

		Log.f2("Checking Minecraft installation");

		OsUtils.checkMinecraft();

		Log.f2("Checking Minecraft installation - done.");


		Log.f2("Checking working directory");

		OsUtils.initWorkdir();

		Log.f2("Checking working directory - done.");
	}


	static void initWorkdir() {

		OsUtils.getAppDir(); // init app dir
		OsUtils.getAppDir(Paths.DIR_LIBRARY, true);
		OsUtils.getAppDir(Paths.DIR_RESOURCEPACKS, true);


		File vanilla = OsUtils.getAppDir(Paths.DIR_VANILLA, true);
		File vanillaAssets = OsUtils.getAppDir(Paths.DIR_VANILLA + "/assets", false);

		if (vanilla.list().length == 0 || !vanillaAssets.exists()) {
			Flags.MUST_RELOAD_VANILLA = true;
		}

		OsUtils.getAppDir("projects", true);
	}

}
