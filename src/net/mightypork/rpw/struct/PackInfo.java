package net.mightypork.rpw.struct;

public class PackInfo
{

	public int pack_format;
	public String description;
	public String title;

	public PackInfo() {
	}

	public PackInfo(int format, String title, String desc) {
		this.pack_format = format;
		this.title = title;
		this.description = desc;
	}

	@Override
	public String toString()
	{
		return "PACK[format: " + pack_format + ", title: " + title + ", description: " + description + "]";
	}
}
