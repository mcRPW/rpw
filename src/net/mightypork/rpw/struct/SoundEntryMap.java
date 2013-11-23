package net.mightypork.rpw.struct;


import java.lang.reflect.Type;
import java.util.LinkedHashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


public class SoundEntryMap extends LinkedHashMap<String, SoundEntry> {

	private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private static Type type = null;


	public static Type getType() {

		if (type == null) {
			type = new TypeToken<SoundEntryMap>() {}.getType();
		}
		return type;
	}


	public static SoundEntryMap fromJson(String json) {

		return gson.fromJson(json, getType());
	}


	public String toJson() {

		return gson.toJson(this);
	}
}
