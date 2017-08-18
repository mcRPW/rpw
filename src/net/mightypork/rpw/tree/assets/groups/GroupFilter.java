package net.mightypork.rpw.tree.assets.groups;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.mightypork.rpw.Config;
import net.mightypork.rpw.tree.assets.AssetEntry;
import net.mightypork.rpw.utils.logging.Log;


public class GroupFilter {

    // public static GroupFilter DELETE_LANG = new GroupFilter(null,
    // "assets.minecraft.lang.*");
    // public static GroupFilter DELETE_FONT = new GroupFilter(null,
    // "assets.minecraft.textures.font.unicode_*");

    private final ArrayList<Matcher> matchers = new ArrayList<Matcher>();
    public String filterSyntax = null;
    public String groupKey = null;


    /**
     * Construct a group filter
     *
     * @param groupKey     target group key
     * @param filterSyntax matching patterns (* = wild card, | = divider)
     */
    public GroupFilter(String groupKey, String filterSyntax) {
        this.filterSyntax = filterSyntax;
        this.groupKey = groupKey;

        if (filterSyntax == null) return;

        final String[] patterns = filterSyntax.split("[|]");
        for (int i = 0; i < patterns.length; i++) {
            patterns[i] = patterns[i].trim();

            // disallowed things.
            patterns[i] = patterns[i].replace("?", "");
            patterns[i] = patterns[i].replace("+", "");
            patterns[i] = patterns[i].replace("-", "");

            patterns[i] = patterns[i].replace(".", "[.]");
            patterns[i] = patterns[i].replace("*", ".*?");

            if (patterns[i].length() > 0) {
                patterns[i] = "^" + patterns[i] + "$";

                matchers.add(Pattern.compile(patterns[i]).matcher(""));
            }
        }

        if (Config.LOG_FILTERS_DETAILED) {
            for (final Matcher m : matchers) {
                Log.f3(" - " + m.pattern().toString());
            }
        }
    }


    public boolean matches(AssetEntry entry) {
        for (final Matcher m : matchers) {
            if (m.reset(entry.getKey()).matches()) return true;
        }
        return false;
    }


    public String getGroupKey() {
        return this.groupKey;
    }


    public void setGroupKey(String key) {
        this.groupKey = key;
    }


    @Override
    public String toString() {
        return groupKey + " { " + filterSyntax + " }";
    }
}
