package net.mightypork.rpack.hierarchy;


import java.util.HashMap;
import java.util.Map;

import net.mightypork.rpack.Config;
import net.mightypork.rpack.hierarchy.groups.AssetGrouper;
import net.mightypork.rpack.hierarchy.groups.AssetGrouperFancy;
import net.mightypork.rpack.hierarchy.groups.AssetGrouperRaw;
import net.mightypork.rpack.hierarchy.groups.GroupFilter;
import net.mightypork.rpack.hierarchy.groups.GroupInfo;
import net.mightypork.rpack.hierarchy.tree.AssetTreeGroup;
import net.mightypork.rpack.hierarchy.tree.AssetTreeLeaf;
import net.mightypork.rpack.library.MagicSources;
import net.mightypork.rpack.library.Sources;
import net.mightypork.rpack.project.NodeSourceProvider;
import net.mightypork.rpack.utils.Log;


public class TreeBuilder {

	public static GroupFilter DELETE_FONT = new GroupFilter(null, "assets.minecraft.textures.font.unicode_*");

	private Map<String, AssetTreeGroup> groups = new HashMap<String, AssetTreeGroup>();
	private AssetTreeGroup rootGroup = null;


	public TreeBuilder() {

	}


	public AssetTreeGroup buildTree(NodeSourceProvider project) {

		groups.clear();
		rootGroup = new AssetTreeGroup(null, null, MagicSources.VANILLA);

		AssetGrouper grouper = (Config.FANCY_GROUPS ? new AssetGrouperFancy() : new AssetGrouperRaw());


		// build group structure
		for (GroupInfo gi : grouper.groups) {
			String groupKey = gi.getKey();
			String label = gi.getLabel();
			String source = project.getSourceForGroup(groupKey);
			String parent = gi.getParent();

			AssetTreeGroup group = new AssetTreeGroup(groupKey, label, source);

			if (parent == null) {
				rootGroup.addChild(group);
			} else {
				AssetTreeGroup parentGroup = groups.get(parent);

				if (parentGroup == null) {
					Log.w("Missing parent group for group " + groupKey + "\n\t" + parent);
				} else {
					parentGroup.addChild(group);
				}
			}

			groups.put(groupKey, group);
		}


		for (AssetEntry ae : Sources.vanilla.getAssetEntries()) {

			if (!Config.SHOW_FONT) {
				if (DELETE_FONT.matches(ae)) continue; // skip fonts				
			}

			if (!Config.SHOW_LANG) {
				if (ae.getType() == EAsset.LANG) continue; // skip lang files				
			}

			boolean success = false;
			for (GroupFilter gf : grouper.filters) {
				if (gf.matches(ae)) {
					if (gf.getGroupKey() == null) {
						// "delete" filter
						success = true;
						break;
					}

					AssetTreeGroup group = groups.get(gf.getGroupKey());

					String source = project.getSourceForFile(ae.getKey());

					if (group == null) {
						Log.w("Missing parent group for file " + ae + "\n\t" + gf.getGroupKey());
					} else {
						group.addChild(new AssetTreeLeaf(ae, source));
						success = true;
					}

					break;
				}
			}

			if (success == false) {
				Log.e("Orphaned file " + ae);
			}


		}

		if (rootGroup == null) {
			Log.e("MISSING ROOT GROUP!");
		}

		return rootGroup;

	}


}
