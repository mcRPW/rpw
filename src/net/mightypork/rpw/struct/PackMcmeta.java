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
        Pattern pattern = Pattern.compile("name");
        Matcher matcher = pattern.matcher(json);
        MatchResult matchResult;
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> codes = new ArrayList<String>();
        ArrayList<String> regions = new ArrayList<String>();
        ArrayList<Boolean> biDirectional = new ArrayList<Boolean>();

        while(matcher.find()){
            matchResult = matcher.toMatchResult();
            names.add(json.substring(matchResult.start() + 8, json.indexOf("\"", matchResult.start() + 8)));
            codes.add(json.substring(json.lastIndexOf("\"", matchResult.start() - 6) + 1, json.indexOf("\"", json.lastIndexOf("\"", matchResult.start() - 6) + 1)));
            languages += 1;
        }

        pattern = Pattern.compile("region");
        matcher = pattern.matcher(json);
        while(matcher.find()){
            matchResult = matcher.toMatchResult();
            regions.add(json.substring(matchResult.start() + 10, json.indexOf("\"", matchResult.start() + 10)));
        }

        pattern = Pattern.compile("bidirectional");
        matcher = pattern.matcher(json);
        while(matcher.find()){
            matchResult = matcher.toMatchResult();
            biDirectional.add(Boolean.parseBoolean(json.substring(matchResult.start() + 19, json.indexOf("}", matchResult.start() + 19))));
        }

        for(int i = 0; i < languages; i++){
            String code = codes.get(i);
            String name = names.get(i);
            String region = regions.get(i);
            boolean bidirectional = biDirectional.get(i);
            LangEntry language = new LangEntry(name, region, code, bidirectional);
            packMcmeta.languages.put(name, language);
        }
        return packMcmeta;
    }


    public String toJson() {
        String json = "{" + pack.toString();

        if(languages.size() > 0){
            json += ", \"language\": {";
        }

        for (int i = 0; i < languages.size(); i++){
            LangEntry language = (LangEntry) Projects.getActive().getCustomLanguages().values().toArray()[i];
            if(i > 0){
                json += ",";
            }
            json += "\"" + language.code + "\": {\"name\": \"" + language.name + "\", \"region\": \"" + language.region + "\", \"bidirectional\": " + language.bidirectional + "}";
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
