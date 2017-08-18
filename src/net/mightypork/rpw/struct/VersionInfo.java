package net.mightypork.rpw.struct;

import java.lang.reflect.Type;

import net.mightypork.rpw.Const;

import com.google.gson.reflect.TypeToken;


public class VersionInfo {

    private static Type versionInfoType = null;

    public String id = null;
    public String type = null;
    public String assets = null;


    public static Type getType() {
        if (versionInfoType == null) {
            versionInfoType = new TypeToken<VersionInfo>() {
            }.getType();
        }
        return versionInfoType;
    }


    public static VersionInfo fromJson(String json) {
        return Const.GSON.fromJson(json, getType());
    }


    public String toJson() {
        return Const.GSON.toJson(this);
    }


    public boolean isReleaseOrSnapshot() {
        return type != null && (type.equals("release") || type.equals("snapshot"));
    }

}
