package net.mightypork.rpw.struct;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.mightypork.rpw.Const;

import com.google.gson.reflect.TypeToken;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.utils.logging.Log;


public class PackMcmeta {

    private static Type type = null;

    public PackInfo pack = null;
    public LangEntryMap languages = Projects.getActive().getCustomLanguages();


    public static Type getType() {
        if (type == null) {
            type = new TypeToken<PackMcmeta>() {
            }.getType();
        }
        return type;
    }


    public static PackMcmeta fromJson(String json) {
        PackMcmeta packMcmeta = new PackMcmeta();
        packMcmeta.languages = new LangEntryMap();
        int packFormat = Integer.parseInt(json.substring(json.indexOf("pack_format\": ") + 14, json.indexOf(",", json.indexOf("pack_format\": ") + 14)));
        String description = json.substring(json.indexOf("description\": ") + 14, json.indexOf("},", json.indexOf("description\": ") + 14));
        packMcmeta.setPackInfo(new PackInfo(packFormat, description));

        int languages = 0;
        Pattern patternName = Pattern.compile("name");
        Matcher matcherName = patternName.matcher(json);
        MatchResult matchResult;
        Pattern patternRegion = Pattern.compile("region");
        Matcher matcherRegion = patternRegion.matcher(json);
        Pattern patternBidirectional = Pattern.compile("bidirectional");
        Matcher matcherBidirectional = patternBidirectional.matcher(json);
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> codes = new ArrayList<String>();
        ArrayList<String> regions = new ArrayList<String>();
        ArrayList<Boolean> biDirectional = new ArrayList<Boolean>();

        while(matcherName.find()){
            matchResult = matcherName.toMatchResult();
            names.add(json.substring(matchResult.start() + 7, json.indexOf("\"", matchResult.start() + 7)));
            codes.add(json.substring(matchResult.start() - 5, json.indexOf("\"", matchResult.start() - 5)));
            languages += 1;
        }

        while(matcherRegion.find()){
            matchResult = matcherRegion.toMatchResult();
            regions.add(json.substring(matchResult.start() + 9, json.indexOf("\"", matchResult.start() + 9)));
        }

        while(matcherBidirectional.find()){
            matchResult = matcherBidirectional.toMatchResult();
            biDirectional.add(Boolean.parseBoolean(json.substring(matchResult.start() + 18, json.indexOf("}", matchResult.start() + 18))));
        }

        for(int i = 0; i < languages; i++){
            String code = codes.get(i);
            String name = names.get(i);
            String region = regions.get(i);
            boolean bidirectional = biDirectional.get(i);
            LangEntry language = new LangEntry(name, code, description, bidirectional);
            packMcmeta.languages.put(name, language);
        }

        try {
            Projects.getActive().saveConfigFiles();
        }catch (IOException exception){
            exception.printStackTrace();
        }
        return packMcmeta;
    }


    public String toJson() {
        String json = "{" + pack.toString();

        if(languages.size() > 0){
            json += ", \"language\":{";
        }

        for (int i = 0; i < languages.size(); i++){
            LangEntry language = (LangEntry) Projects.getActive().getCustomLanguages().values().toArray()[i];
            if(i > 0){
                json += ",";
            }
            json += "\"" + language.code + "\":{\"name\":\"" + language.name + "\", \"region\":\"" + language.region + "\", \"bidirectional\":" + language.bidirectional + "}";
        }

        if(languages.size() > 0){
            json += "}";
        }

        json += "}";
        return json;
    }


    public PackInfo getPackInfo() {
        return pack;
    }


    public void setPackInfo(PackInfo packInfo) {
        pack = packInfo;
    }
}
