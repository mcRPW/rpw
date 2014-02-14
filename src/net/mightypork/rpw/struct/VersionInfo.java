package net.mightypork.rpw.struct;


import java.lang.reflect.Type;

import net.mightypork.rpw.Config;
import net.mightypork.rpw.Const;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class VersionInfo {

	private static Gson gson = Config.PRETTY_JSON ? Const.PRETTY_GSON : Const.UGLY_GSON;
	private static Type versionInfoType = null;

	public String id = null;
	public String type = null;
	public String assets = null;


	public static Type getType() {

		if (versionInfoType == null) {
			versionInfoType = new TypeToken<VersionInfo>() {}.getType();
		}
		return versionInfoType;
	}


	public static VersionInfo fromJson(String json) {

		return gson.fromJson(json, getType());
	}


	public String toJson() {

		return gson.toJson(this);
	}


	public boolean isReleaseOrSnapshot() {

		return type != null && (type.equals("release") || type.equals("snapshot"));
	}

}
