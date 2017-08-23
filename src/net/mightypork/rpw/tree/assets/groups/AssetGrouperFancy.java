package net.mightypork.rpw.tree.assets.groups;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.mightypork.rpw.Config;
import net.mightypork.rpw.Paths;
import net.mightypork.rpw.utils.Utils;
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.files.OsUtils;
import net.mightypork.rpw.utils.files.SimpleConfig;
import net.mightypork.rpw.utils.logging.Log;


public class AssetGrouperFancy extends AssetGrouper {

    public AssetGrouperFancy() {
        groups.clear();
        filters.clear();

        String text;
        Map<String, String> pairs;

        // order MATTERS!

        // load groups
        final List<String> createdGroups = new ArrayList<String>();

        // mod groups
        try {
            pairs = SimpleConfig.mapFromFile(OsUtils.getAppDir(Paths.FILE_CFG_MODGROUPS));

            for (final Entry<String, String> pair : pairs.entrySet()) {
                createdGroups.add(pair.getKey());
                addGroup(pair.getKey(), pair.getValue());
            }
        } catch (final IOException e) {
            Log.e("Failed to load mod group list.");
        }

        // vanilla groups

        text = FileUtils.resourceToString("/data/tree/groupsVanilla.ini");
        if (text.length() == 0) throw new RuntimeException("Failed to load group list.");

        pairs = SimpleConfig.mapFromString(text);

        for (final Entry<String, String> pair : pairs.entrySet()) {
            createdGroups.add(pair.getKey());
            addGroup(pair.getKey(), pair.getValue());
        }

        // check orphaned groups
        final ArrayList<GroupInfo> toAdd = new ArrayList<GroupInfo>();

        do {
            for (final GroupInfo g : groups) {
                final String parent = g.getParent();
                if (parent == null) continue;
                if (!createdGroups.contains(parent)) {
                    final GroupInfo parentGroup = new GroupInfo(parent, Utils.fromLastDot(parent));
                    toAdd.add(parentGroup);
                    if (Config.LOG_GROUPS) Log.f3("Group: " + parentGroup);

                    createdGroups.add(parent);
                }
            }
            groups.addAll(toAdd);
        } while (toAdd.size() > 0);

        Collections.sort(groups, new DotComparator<GroupInfo>());

        // load filters

        // mod filters
        try {
            pairs = SimpleConfig.mapFromFile(OsUtils.getAppDir(Paths.FILE_CFG_MODFILTERS));

            for (final Entry<String, String> pair : pairs.entrySet()) {
                addFilter(pair.getKey(), pair.getValue());
            }
        } catch (final IOException e) {
            Log.e("Failed to load group list.");
        }

        // vanilla filters
        text = FileUtils.resourceToString("/data/tree/filtersVanilla.ini");
        if (text.length() == 0) throw new RuntimeException("Failed to load filter list.");

        pairs = SimpleConfig.mapFromString(text);

        for (final Entry<String, String> pair : pairs.entrySet()) {

            String v = pair.getValue();

            if (v != null) {
                // handle abbreviations
                v = v.replace("^tb", "assets.minecraft.textures.blocks");
                v = v.replace("^ti", "assets.minecraft.textures.items");
                v = v.replace("^te", "assets.minecraft.textures.entity");
                v = v.replace("^t", "assets.minecraft.textures");
                v = v.replace("^s", "assets.minecraft.sounds");
                v = v.replace("^", "assets.minecraft");
            }

            addFilter(pair.getKey(), v);
        }
    }
}
