package net.mightypork.rpack.struct;


public class PackInfo {
	public int pack_format;
	public String description;
	
	public PackInfo(int format, String desc) {
		this.pack_format = format;
		this.description = desc;
	}
	
	@Override
	public String toString() {
	
		return "rev: "+pack_format+", name: "+description;
	}
}
