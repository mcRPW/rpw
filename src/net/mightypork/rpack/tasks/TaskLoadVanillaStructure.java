package net.mightypork.rpack.tasks;


import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.mightypork.rpack.Config;
import net.mightypork.rpack.Flags;
import net.mightypork.rpack.Paths;
import net.mightypork.rpack.hierarchy.AssetEntry;
import net.mightypork.rpack.hierarchy.EAsset;
import net.mightypork.rpack.library.Sources;
import net.mightypork.rpack.utils.Log;
import net.mightypork.rpack.utils.OsUtils;
import net.mightypork.rpack.utils.SimpleConfig;
import net.mightypork.rpack.utils.Utils;


public class TaskLoadVanillaStructure {

	public static void run() {

		Flags.VANILLA_STRUCTURE_LOAD_OK = false;
		Log.f2("Loading vanilla structure");


		//List<AssetEntry> assetEntries = new ArrayList<AssetEntry>();
		//List<String> assetKeys = new ArrayList<String>();

		Map<String, AssetEntry> assets = new LinkedHashMap<String, AssetEntry>();

		File structureFile = OsUtils.getAppDir(Paths.FILE_VANILLA_STRUCTURE);
		if (!structureFile.exists()) {
			return; // success = false
		}

		try {
			Map<String, String> saveMap = SimpleConfig.mapFromFile(structureFile);

			for (Entry<String, String> e : saveMap.entrySet()) {


				AssetEntry ae = new AssetEntry(e.getKey(), EAsset.valueOf(e.getValue()));

				assets.put(e.getKey(), ae);

				if (Config.LOG_VANILLA_LOAD_STRUCTURE) Log.f3("- " + ae);
			}

		} catch (IOException e) {
			Log.e(e);
			return; // success = false
		}

		assets = Utils.sortByKeys(assets);

		Sources.vanilla.setAssets(assets);

		Log.f2("Loading vanilla structure - done.");
		Flags.VANILLA_STRUCTURE_LOAD_OK = true;
	}
}
