package net.mightypork.rpw.struct;

import java.lang.reflect.Type;
import java.util.ArrayList;

import net.mightypork.rpw.Const;

import com.google.gson.reflect.TypeToken;


public class ModEntryList extends ArrayList<ModEntry> {

    private static Type type = null;


    public static Type getType() {
        if (type == null) {
            type = new TypeToken<ModEntryList>() {
            }.getType();
        }
        return type;
    }


    public static ModEntryList fromJson(String json) {
        return Const.GSON.fromJson(json, getType());
    }


    public String toJson() {
        return Const.GSON.toJson(this);
    }


    public String getModListName() {
        for (final ModEntry e : this) {
            if (e.parent == null || e.parent.trim().length() == 0) {
                String name = e.name;
                if (e.mcversion != null && e.mcversion.trim().length() > 0) {
                    name += " [" + e.mcversion + "]";
                }
                return name;
            }
        }

        if (size() > 0) {
            String name = get(0).name;
            if (get(0).mcversion != null && get(0).mcversion.trim().length() > 0) {
                name += " [" + get(0).mcversion + "]";
            }
            return name;
        }

        return null;
    }
}
