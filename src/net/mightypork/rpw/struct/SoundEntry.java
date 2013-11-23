package net.mightypork.rpw.struct;


import java.util.ArrayList;


public class SoundEntry {

	public String category;
	public ArrayList<String> sounds;


	public SoundEntry() {

	}


	public SoundEntry(String ctg, ArrayList<String> files) {

		category = ctg;
		sounds = new ArrayList<String>();
		sounds.addAll(files);
	}


	@Override
	public String toString() {

		return "SoundEntry[category: " + category + ", files: " + sounds + "]";
	}
}
