package net.mightypork.rpw.struct;

import java.util.List;


/*
 {
 "modid": "cfm",
 "name": "\u00A74MrCrayfish's Furniture Mod",
 "description": "\u00A7eAdds over 30 pieces of furniture to the game!",
 "version": "3.3",
 "mcversion": "1.6.4",
 "authors": [ "\u00A7aMrCrayfish" ],
 "url": "http://www.mrcrayfish.com/",
 "parent":"",
 "screenshots": [],
 "dependencies": ["MinecraftForge"]
 }
 */

public class ModEntry {

    public String modid;
    public String name;
    public String description;
    public String version;
    public String mcversion;
    public List<String> authors;
    public String url;
    public String parent;
    public List<String> screenshots;
    public List<String> dependencies;

}
