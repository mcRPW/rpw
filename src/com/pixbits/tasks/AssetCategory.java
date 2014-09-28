package com.pixbits.tasks;

public enum AssetCategory
{
  BLOCKS("Blocks", "assets.minecraft.textures.blocks.*"),
  ITEMS("Items", "assets.minecraft.textures.items.*")

  ;
  
  AssetCategory(String name, String prefix)
  {
    this.name = name;
    this.prefix = prefix;
  }
  
  public final String name;
  public final String prefix;
}
