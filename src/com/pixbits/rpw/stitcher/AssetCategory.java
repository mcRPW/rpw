package com.pixbits.rpw.stitcher;

public enum AssetCategory {
	BLOCKS("Blocks", "assets.minecraft.textures.blocks.*"), ITEMS("Items",
			"assets.minecraft.textures.items.*"), ENTITIES("Entities",
			"assets.minecraft.textures.entity.*"), GUI("GUI",
			"assets.minecraft.textures.gui.*"),

	;

	AssetCategory(String name, String prefix) {
		this.name = name;
		this.prefix = prefix;
	}

	public final String name;
	public final String prefix;
}
