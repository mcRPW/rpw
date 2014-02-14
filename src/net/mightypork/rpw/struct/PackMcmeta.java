package net.mightypork.rpw.struct;


import java.lang.reflect.Type;

import net.mightypork.rpw.Const;

import com.google.gson.reflect.TypeToken;


public class PackMcmeta {

	private static Type type = null;

	public PackInfo pack = null;
	public LangEntryMap language = new LangEntryMap();


	public static Type getType() {

		if (type == null) {
			type = new TypeToken<PackMcmeta>() {}.getType();
		}
		return type;
	}


	public static PackMcmeta fromJson(String json) {

		return Const.GSON.fromJson(json, getType());
	}


	public String toJson() {

		return Const.GSON.toJson(this);
	}


	public PackInfo getPackInfo() {

		return pack;
	}


	public void setPackInfo(PackInfo packInfo) {

		pack = packInfo;
	}
}
