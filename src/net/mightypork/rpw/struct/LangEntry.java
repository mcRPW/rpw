package net.mightypork.rpw.struct;


public class LangEntry {

	public String region;
	public String name;
	public boolean bidirectional;

	public LangEntry() {}

	public LangEntry(String region, String name, boolean bidirectional) {

		this.region = region;
		this.name = name;
		this.bidirectional = bidirectional;
	}


	@Override
	public String toString() {

		return "LANG[region: " + region + ", name: " + name + ", bidi: " + bidirectional + "]";
	}
}
