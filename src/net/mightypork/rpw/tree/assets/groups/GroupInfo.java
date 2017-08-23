package net.mightypork.rpw.tree.assets.groups;

/**
 * Object describing group to be created.
 *
 * @author Ondřej Hruška (MightyPork)
 */
public class GroupInfo implements Comparable<GroupInfo> {

    public String key = null;
    public String label = null;


    /**
     * Group Info
     *
     * @param key   group key - must NOT end with a dot; dots are delimiters
     * @param label shown label
     */
    public GroupInfo(String key, String label) {
        this.key = key;
        this.label = label;
    }


    /**
     * Get group key
     *
     * @return key
     */
    public String getKey() {
        return key;
    }


    /**
     * Get group label for displau
     *
     * @return label
     */
    public String getLabel() {
        return label;
    }


    /**
     * Get parent group's key
     *
     * @return parent's key
     */
    public String getParent() {
        if (key.indexOf('.') == -1) return null; // root group
        return key.substring(0, key.lastIndexOf('.'));
    }


    @Override
    public String toString() {
        return key;
    }


    @Override
    public int compareTo(GroupInfo o) {
        return o.key.compareToIgnoreCase(key);
    }
}
