package net.mightypork.rpw.struct;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import net.mightypork.rpw.Const;
import com.google.gson.reflect.TypeToken;


public class LangEntryMap extends LinkedHashMap<String, LangEntry> {

    private static Type type = null;


    public static Type getType() {
        if (type == null) {
            type = new TypeToken<LangEntryMap>() {
            }.getType();
        }
        return type;
    }


    public static LangEntryMap fromJson(String json) {
        return Const.GSON.fromJson(json, LangEntryMap.class);
    }


    public String toJson() {
        return Const.GSON.toJson(this);
    }
}
