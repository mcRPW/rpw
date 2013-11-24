# Adding custom sounds

As of version 1.7, you can add brand new sounds into your packs.


## About sounds.json

This file contains information about all the Minecraft sounds, which you can 
play using the `/playsound` command.

Since 1.7, all the sounds are loaded based on information in this file. If a 
resource pack contains a `sounds.json` file, it is loaded as well and can be 
used to add *custom sounds*.


## Sound Wizard

RPW provides a convenient tool for managing custom sounds and your `sounds.json` 
file. To open it, go to `Tools > Manage custom sounds`.

On the **left**, you will see *sound names* - those are used when using 
`/playsound`.

In the **middle area**, you can then edit the selected (or just created) sound 
entry - choose it's category and change it's name.

The **right panel** is used to show and manage your custom sound files.


### Add / Delete a sound entry

To add an entry, click the `Add` button on the left. The new entry will then 
open for editing in the middle area, and you will have give it a name before 
you save it.

Select one of the categories from the drop-down menu. This doesn't matter much, 
so if you're not sure, just select whichever you like most, for example 
`neutral`.


### Adding sounds to an entry

To add a file to an entry, simply drag it from the file tree into the file list 
in the middle.

Each entry can hold multiple sound files, and Minecraft will randomly select one 
each time you play the sound.


### Managing sound files

To import / edit / delete sounds, right click them in the file tree and select 
the action you want. You can select multiple files using `ctrl+click`.
