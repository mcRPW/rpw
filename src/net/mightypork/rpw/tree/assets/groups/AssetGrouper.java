package net.mightypork.rpw.tree.assets.groups;

import java.util.ArrayList;

import net.mightypork.rpw.Config;
import net.mightypork.rpw.tree.assets.AssetEntry;
import net.mightypork.rpw.utils.logging.Log;


public abstract class AssetGrouper {

    public ArrayList<GroupInfo> groups = new ArrayList<GroupInfo>();
    public ArrayList<GroupFilter> filters = new ArrayList<GroupFilter>();


    protected void addGroup(String groupKey, String label) {
        addGroup(new GroupInfo(groupKey, label));
    }


    protected void addGroup(GroupInfo group) {
        if (Config.LOG_GROUPS) Log.f3("Group: " + group);
        groups.add(group);
    }


    protected void addFilter(String groupKey, String patterns) {
        addFilter(new GroupFilter(groupKey, patterns));
    }


    protected void addFilter(GroupFilter filter) {
        if (Config.LOG_FILTERS) Log.f3("Filter: " + filter);
        filters.add(filter);
    }


    public GroupInfo findGroupForAssetEntry(AssetEntry asset) {
        for (final GroupFilter filter : filters) {
            if (filter.matches(asset)) return findGroupForGroupKey(filter.getGroupKey());
        }

        Log.w("No group matches asset '" + asset + "', fallback to root group.");

        for (final GroupInfo g : groups) {
            if (g.getParent() == null) return g;
        }

        Log.w("No root group found! This is a bug!");
        return null; // well, that sucks
    }


    public GroupInfo findGroupForGroupKey(String groupKey) {
        for (final GroupInfo g : groups) {
            if (g.getKey().equals(groupKey)) return g;
        }

        Log.w("No group matches key '" + groupKey + "'! This is a bug!");

        return null; // looks like a bug
    }
}
