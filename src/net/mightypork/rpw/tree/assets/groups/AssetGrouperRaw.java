package net.mightypork.rpw.tree.assets.groups;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.mightypork.rpw.library.Sources;
import net.mightypork.rpw.utils.Utils;


public class AssetGrouperRaw extends AssetGrouper {

    public AssetGrouperRaw() {
        groups.clear();
        filters.clear();

        // load filters
        // if (!Config.SHOW_FONT) addFilter(GroupFilter.DELETE_FONT);
        // if (!Config.SHOW_LANG) addFilter(GroupFilter.DELETE_LANG);

        final List<String> entries = new ArrayList<String>();
        final List<String> createdGroups = new ArrayList<String>();

        for (final String s : Sources.vanilla.getAssetKeys()) {
            entries.add(s);
        }

        // sort so that entries with most dots go last
        Collections.sort(entries, new DotComparator<String>());

        for (final String s : entries) {
            final String gn = Utils.toLastDot(s);

            if (createdGroups.contains(gn)) {
                continue; // group already exists
            }

            final String gf = gn + ".*";

            addGroup(gn, Utils.fromLastDot(gn));
            addFilter(gn, gf);

            // if (Config.LOG_GROUPS) Log.f3("Group: " + gn + " | "+gf);

            createdGroups.add(gn);

        }

        // if (Config.LOG_GROUPS) Log.f3("---");

        final ArrayList<GroupInfo> toAdd = new ArrayList<GroupInfo>();

        do {
            toAdd.clear();
            for (final GroupInfo g : groups) {
                final String parent = g.getParent();

                if (parent == null) continue;
                if (!createdGroups.contains(parent)) {
                    final GroupInfo parentGroup = new GroupInfo(parent, Utils.fromLastDot(parent));
                    toAdd.add(parentGroup);
                    // if (Config.LOG_GROUPS) Log.f3("Group: " + parentGroup);

                    createdGroups.add(parent);
                }
            }
            groups.addAll(toAdd);
        } while (toAdd.size() > 0);

        Collections.sort(groups, new DotComparator<GroupInfo>());

        Collections.reverse(filters); // important!
    }

}
