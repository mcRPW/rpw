package net.mightypork.rpack.struct;


import java.util.ArrayList;


public class SoundEntry {

	public String category;
	public ArrayList<String> sounds;


	@Override
	public String toString() {

		return "SoundEntry[category: " + category + ", files: " + sounds + "]";
	}
}
