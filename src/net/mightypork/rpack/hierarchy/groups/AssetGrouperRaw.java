package net.mightypork.rpack.hierarchy.groups;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.mightypork.rpack.Config;
import net.mightypork.rpack.library.Sources;
import net.mightypork.rpack.utils.Log;
import net.mightypork.rpack.utils.Utils;


public class AssetGrouperRaw extends AssetGrouper {


	public AssetGrouperRaw() {

		groups.clear();
		filters.clear();

		// load filters
//		if (!Config.SHOW_FONT) addFilter(GroupFilter.DELETE_FONT);
//		if (!Config.SHOW_LANG) addFilter(GroupFilter.DELETE_LANG);

		List<String> entries = new ArrayList<String>();
		List<String> createdGroups = new ArrayList<String>();

		for (String s : Sources.vanilla.getAssetKeys()) {
			entries.add(s);
		}

		// sort so that entries with most dots go last
		Collections.sort(entries, new DotComparator<String>());


		for (String s : entries) {

			String gn = Utils.toLastDot(s);

			if (createdGroups.contains(gn)) {
				continue; // group already exists
			}

			String gf = gn + ".*";

			addGroup(gn, Utils.fromLastDot(gn));
			addFilter(gn, gf);

			createdGroups.add(gn);

		}


		ArrayList<GroupInfo> toAdd = new ArrayList<GroupInfo>();

		do {
			toAdd.clear();
			for (GroupInfo g : groups) {
				String parent = g.getParent();

				if (parent == null) continue;
				if (!createdGroups.contains(parent)) {

					GroupInfo parentGroup = new GroupInfo(parent, Utils.fromLastDot(parent));
					toAdd.add(parentGroup);
					if (Config.LOG_GROUPS) Log.f3("Group: " + parentGroup);

					createdGroups.add(parent);
				}
			}
			groups.addAll(toAdd);
		} while (toAdd.size() > 0);


		Collections.sort(groups, new DotComparator<GroupInfo>());

	}

}
