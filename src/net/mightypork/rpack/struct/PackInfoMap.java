package net.mightypork.rpack.struct;

import java.lang.reflect.Type;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class PackInfoMap extends HashMap<String, PackInfo> {
	
	private static Gson gson = new Gson();
	private static Type type = null;
	
	public static Type getType() {
		if(type==null) {
			type = new TypeToken<PackInfoMap>(){}.getType();
		}
		return type;
	}
	
	public static PackInfoMap fromJson(String json) {
		return gson.fromJson(json, getType());
	}
	
	public String toJson() {
		return gson.toJson(this);
	}
	
	public PackInfo getPackInfo() {
		return get("pack");
	}
	
	public void setPackInfo(PackInfo packInfo) {
		put("pack", packInfo);
	}
}
