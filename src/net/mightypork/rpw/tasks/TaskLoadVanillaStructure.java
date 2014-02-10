package net.mightypork.rpw.tasks;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.mightypork.rpw.Config;
import net.mightypork.rpw.Flags;
import net.mightypork.rpw.Paths;
import net.mightypork.rpw.library.Sources;
import net.mightypork.rpw.tree.assets.AssetEntry;
import net.mightypork.rpw.tree.assets.EAsset;
import net.mightypork.rpw.utils.UpdateHelper;
import net.mightypork.rpw.utils.Utils;
import net.mightypork.rpw.utils.files.OsUtils;
import net.mightypork.rpw.utils.files.SimpleConfig;
import net.mightypork.rpw.utils.logging.Log;


public class TaskLoadVanillaStructure {

	@SuppressWarnings("null")
	public static void run() {

		Flags.VANILLA_STRUCTURE_LOAD_OK = false;
		Log.f2("Loading vanilla structure");


		//List<AssetEntry> assetEntries = new ArrayList<AssetEntry>();
		//List<String> assetKeys = new ArrayList<String>();

		Map<String, AssetEntry> assets = new LinkedHashMap<String, AssetEntry>();

		File structureFile = OsUtils.getAppDir(Paths.FILE_VANILLA_STRUCTURE);
		if (!structureFile.exists()) {
			return; // success == false
		}

		try {
			Map<String, String> saveMap = SimpleConfig.mapFromFile(structureFile);


			// fix changes introduced in 3.8.4
			Map<String, String> fixedMap = null;
			boolean fixing = UpdateHelper.needFixLibraryKeys();

			if (fixing) {
				Log.f2("Library file is outdated.");
				fixedMap = new HashMap<String, String>(saveMap.size());
			}

			for (Entry<String, String> e : saveMap.entrySet()) {

				try {

					String k1 = e.getKey();
					String v = e.getValue();

					String k = k1;

					if (fixing) {
						k = UpdateHelper.fixLibraryKey(k1);
						fixedMap.put(k, v);
					}

					EAsset type = EAsset.valueOf(v);
					AssetEntry ae = new AssetEntry(k, type);
					assets.put(e.getKey(), ae);

					if (Config.LOG_VANILLA_LOAD_STRUCTURE) Log.f3("+ " + ae);

				} catch (IllegalArgumentException iae) {
					Log.w("Unknown asset type " + e.getValue() + " - skipping entry.");
				}
			}

			if (fixing) {
				Log.f2("Saving updated library file.");
				SimpleConfig.mapToFile(structureFile, fixedMap, false);
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