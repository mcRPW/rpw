package net.mightypork.rpw.struct;


import java.lang.reflect.Type;
import java.util.ArrayList;

import net.mightypork.rpw.Const;

import com.google.gson.reflect.TypeToken;


public class ModEntryList extends ArrayList<ModEntry> {
	
	private static Type type = null;
	
	
	public static Type getType()
	{
		if (type == null) {
			type = new TypeToken<ModEntryList>() {}.getType();
		}
		return type;
	}
	
	
	public static ModEntryList fromJson(String json)
	{
		return Const.GSON.fromJson(json, getType());
	}
	
	
	public String toJson()
	{
		return Const.GSON.toJson(this);
	}
	
	
	public String getModListName()
	{
		for (final ModEntry e : this) {
			if (e.parent.trim().length() == 0) {
				return e.name + " (MC " + e.mcversion + ")";
			}
		}
		
		if (size() > 0) return get(0).name + " (MC " + get(0).mcversion + ")";
		
		return null;
	}
}
