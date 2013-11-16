# Purpose of RPW

This tool is designed to provide a convenient way of creating, editing and 
remixing resource packs.


## Understanding resource packs

To use RPW efficiently, it's important to know how resource packs work.

All the resources in Minecraft are organized in directories, either in the 
jar file, or in the `assets` folder. It's divided into those two simply because 
sound files are HUGE and they don't need to be re-downloaded each time a new 
version is released. Since resource packs can contain both sounds and textures, 
RPW treats them as equal and shows them together.


### What's in a pack

Each pack contains:

* Info file `pack.mcmeta`
* PNG icon `pack.png`
* `assets` folder with the actual resources

Files in the `assets` folder are structured exactly like those in Minecraft, so 
when a pack is in use, it essentially replaces some of the Vanilla resources by 
the custom ones.


### Using multiple packs

When you use more than one pack at once, Minecraft first tries to get the 
assets from the topmost pack, then proceeds to the second and so on until it 
reaches Vanilla again. 

That's why you *shouldn't* put copies of everything into your packs - it would 
make it hard to combine your pack with others.


### About sounds.json

This file contains information about all the Minecraft sounds, which you can 
play using the `/playsound` command.

Since 1.7, all the sounds are dynamically loaded based on information in this 
file. If a resource pack contains a `sounds.json` file, it is loaded as well and 
can be used to add *custom sounds*.

To manage custom sounds, use the "Custom Sounds" dialog.
