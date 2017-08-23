package net.mightypork.rpw.struct;

public class LangEntry {

    public String region;
    public String name;
    public String code;
    public boolean bidirectional;


    public LangEntry() {
    }

    public LangEntry(String name, String region, String code, boolean bidirectional) {
        this.name = name;
        this.region = region;
        this.code = code;
        this.bidirectional = bidirectional;
    }


    @Override
    public String toString() {
        return "LANG[region: " + region + ", name: " + name + ", code: " + code + ", bidirectional: " + bidirectional + "]";
    }
}
