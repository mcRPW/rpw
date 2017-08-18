package net.mightypork.rpw.struct;

import java.lang.Character.Subset;
import java.util.ArrayList;
import java.util.List;


public class SoundEntry {

    public String category;
    public ArrayList<SoundSubEntry> sounds;


    public SoundEntry() {
    }


    public SoundEntry(String ctg, List<SoundSubEntry> files) {
        category = ctg;
        sounds = new ArrayList<SoundSubEntry>();

        for (SoundSubEntry sse : files) {
            sse.name = sse.name.replace('\\', '/'); // WINDOWS SUX
        }
        sounds.addAll(files);
    }


    @Override
    public String toString() {
        return "SoundEntry[category: " + category + ", sounds: " + sounds + "]";
    }
}
