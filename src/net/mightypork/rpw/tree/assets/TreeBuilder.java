package net.mightypork.rpw.tree.assets;


import java.util.HashMap;
import java.util.Map;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Config;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.library.MagicSources;
import net.mightypork.rpw.library.Sources;
import net.mightypork.rpw.project.NodeSourceProvider;
import net.mightypork.rpw.tree.assets.groups.AssetGrouper;
import net.mightypork.rpw.tree.assets.groups.AssetGrouperFancy;
import net.mightypork.rpw.tree.assets.groups.AssetGrouperRaw;
import net.mightypork.rpw.tree.assets.groups.GroupFilter;
import net.mightypork.rpw.tree.assets.groups.GroupInfo;
import net.mightypork.rpw.tree.assets.tree.AssetTreeGroup;
import net.mightypork.rpw.tree.assets.tree.AssetTreeLeaf;
import net.mightypork.rpw.utils.Log;


public class TreeBuilder {

	public static GroupFilter DELETE_FONT = new GroupFilter(null, "assets.minecraft.textures.font.unicode_*");

	private Map<String, AssetTreeGroup> groups = new HashMap<String, AssetTreeGroup>();
	private AssetTreeGroup rootGroup = null;


	public TreeBuilder() {

	}


	public AssetTreeGroup buildTree(NodeSourceProvider project) {

		groups.clear();
		rootGroup = new AssetTreeGroup(null, null, MagicSources.VANILLA);

		AssetGrouper grouper = (Config.FANCY_TREE ? new AssetGrouperFancy() : new AssetGrouperRaw());

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


		boolean orphans = false;

		for (AssetEntry ae : Sources.vanilla.getAssetEntries()) {

			if (!Config.SHOW_FONT) {
				if (DELETE_FONT.matches(ae)) continue; // skip fonts				
			}

			if (!Config.SHOW_OBSOLETE_DIRS) {
				boolean del = false;
				if (ae.getKey().startsWith("assets.minecraft.sound") && !ae.getKey().startsWith("assets.minecraft.sounds")) del = true; // sound dir
				if (ae.getKey().startsWith("assets.minecraft.music")) del = true; // music dir	
				if (ae.getKey().startsWith("assets.minecraft.records")) del = true; // records dir	
				if (del) {
					//if(Config.LOG_GROUPS) Log.f3("IGNORE OBSOLETE: "+ae.getKey());
					continue;
				}
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
				orphans = true;
			}

		}

		if (rootGroup == null) {
			Log.e("MISSING ROOT GROUP!");
		}

		if (Config.FANCY_TREE && orphans && Config.WARNING_ORPHANED_NODES) {
			//@formatter:off
			boolean yeah = Alerts.askYesNo(
					App.getFrame(),
					"Orphaned nodes",
					"Some asset files could not be shown, because\n" +
					"their parent groups are missing.\n" +
					"\n" +
					"This often happens when mods are installed and\n" +
					"Fancy Tree is enabled.\n" +
					"\n" +
					"Disable Fancy Tree now?\n"
			);
			//@formatter:on

			if (yeah) {
				Config.FANCY_TREE = false;
				Config.save();
				return buildTree(project); // RISC: Recursion
			}
		}

		return rootGroup;

	}


}
