package net.mightypork.rpw.struct;


public class PackInfo {

	public int pack_format;
	public String description;


	public PackInfo() {

	}


	public PackInfo(int format, String desc) {

		this.pack_format = format;
		this.description = desc;
	}


	@Override
	public String toString() {

		return "PACK[format: " + pack_format + ", description: " + description + "]";
	}
}
