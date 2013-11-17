package net.mightypork.rpack.struct;

import net.mightypork.rpack.utils.Utils;


public class SoundEntry {
	public String category;
	public String[] sounds;
	
	
	
	@Override
	public String toString() {
	
		return "category: " + category + ", files: " + Utils.arrayToString(sounds);
	}
}
