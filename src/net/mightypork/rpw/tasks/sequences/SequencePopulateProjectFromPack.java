package net.mightypork.rpw.tasks.sequences;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Config;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.library.MagicSources;
import net.mightypork.rpw.library.Sources;
import net.mightypork.rpw.project.Project;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.struct.LangEntry;
import net.mightypork.rpw.struct.PackMcmeta;
import net.mightypork.rpw.struct.SoundEntry;
import net.mightypork.rpw.struct.SoundEntryMap;
import net.mightypork.rpw.struct.SoundSubEntry;
import net.mightypork.rpw.tree.assets.AssetEntry;
import net.mightypork.rpw.tree.assets.EAsset;
import net.mightypork.rpw.utils.Utils;
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.files.ZipBuilder;
import net.mightypork.rpw.utils.files.ZipUtils;
import net.mightypork.rpw.utils.logging.Log;


/**
 * Import pack as current project (assuming the project is newly created)
 *
 * @author Ondřej Hruška (MightyPork)
 */
public class SequencePopulateProjectFromPack extends AbstractMonitoredSequence {

    private File packFile;
    private final Runnable after;
    private List<String> zipEntries;
    private ZipFile zip;
    private final Project project;
    private final HashSet<String> alreadyExtracted = new HashSet<String>();


    /**
     * @param packFile file to load
     * @param after    runnable executed after it's done
     */
    public SequencePopulateProjectFromPack(File packFile, Runnable after) {
        this.packFile = packFile;
        this.after = after;
        this.project = Projects.getActive();
    }


    @Override
    public int getStepCount() {
        return 4; // TODO
    }


    @Override
    public String getStepName(int step) {
        //@formatter:off
        switch (step) {
            case 0:
                return "Listing pack file";
            case 1:
                return "Loading custom languages";
            case 2:
                return "Loading custom sounds";
            case 3:
                return "Loading project files";
        }
        //@formatter:on

        return null;
    }


    @Override
    protected boolean step(int step) {
        //@formatter:off
        switch (step) {
            case 0:
                return stepListFile();
            case 1:
                return stepMcmetaAndLanguages();
            case 2:
                return stepCustomSounds();
            case 3:
                return stepOtherAssets();
        }
        //@formatter:on

        return false;
    }


    private boolean stepListFile() {
        if (!packFile.getPath().endsWith(".zip")){
            try {
                ZipUtils.zipFolder(packFile.getPath(), packFile.getPath() + ".zip");
            } catch (Exception e){
                e.printStackTrace();
                return false;
            }
            packFile = new File(packFile.getPath() + ".zip");
        }

        try {
            zip = new ZipFile(packFile);
            zipEntries = ZipUtils.listZip(zip);
        } catch (final Exception e) {
            Log.e(e);
            return false;
        }

        return true;
    }


    private boolean stepMcmetaAndLanguages() {
        File target;

        try {
            // pack icon
            final ZipEntry ze_icon = zip.getEntry("pack.png");
            if (ze_icon != null) {
                target = new File(project.getProjectDirectory(), "pack.png");
                ZipUtils.extractZipEntry(zip, ze_icon, target);
                if (Config.LOG_EXTRACTED_ASSETS) Log.f3("+ pack.png");
                alreadyExtracted.add("pack.png");
            }

            // get title and custom languages
            final ZipEntry ze_mcmeta = zip.getEntry("pack.mcmeta");
            if (ze_mcmeta != null) {
                final String json_mcmeta = ZipUtils.zipEntryToString(zip, ze_mcmeta);
                final PackMcmeta mcmeta = PackMcmeta.fromJson(json_mcmeta);

                alreadyExtracted.add("pack.mcmeta");

                project.setDescription(mcmeta.pack.description.replace("\"", ""));

                if (mcmeta.languages != null) {
                    // copy custom languages
                    for (final Entry<String, LangEntry> entry : mcmeta.languages.entrySet()) {
                        final String key = entry.getKey();

                        final String assetKey = "assets.minecraft.lang." + key;

                        final AssetEntry ae = new AssetEntry(assetKey, EAsset.LANG);

                        if (Sources.vanilla.doesProvideAsset(ae.getKey())) {
                            // vanilla language, skip (why was it there anyway?)
                        } else {
                            // new language
                            final String entryname = ae.getPath();
                            final ZipEntry ze_langfile = zip.getEntry(entryname);

                            if (ze_langfile != null) {
                                // copy lang to langs folder
                                target = new File(project.getCustomLangDirectory(), key + ".lang");
                                ZipUtils.extractZipEntry(zip, ze_langfile, target);

                                // register in project
                                // doing this here ensures there's no crap in
                                // the lang map
                                project.getLangMap().put(key, entry.getValue());

                                // mark as extracted
                                alreadyExtracted.add(entryname);

                                if (Config.LOG_EXTRACTED_ASSETS) Log.f3("+ LANG " + entryname);
                            }
                        }
                    }
                    try {
                        Projects.getActive().saveConfigFiles();
                    }catch (IOException exception){
                        exception.printStackTrace();
                    }
                }
            }

        } catch (final Exception e) {
            Log.e(e);
            return false;
        }
        return true;
    }


    private boolean stepCustomSounds() {
        File target;

        try {
            // get title and custom languages
            final String sndfile = "assets/minecraft/sounds.json";
            final ZipEntry ze_sounds = zip.getEntry(sndfile);

            if (ze_sounds != null) {
                final String json_sounds = ZipUtils.zipEntryToString(zip, ze_sounds);
                final SoundEntryMap soundmap = SoundEntryMap.fromJson(json_sounds);

                alreadyExtracted.add(sndfile); // don't extract again

                project.setSoundMap(soundmap); // add to the project

                for (final Entry<String, SoundEntry> entry : soundmap.entrySet()) {
                    // got through entry sounds
                    for (final SoundSubEntry s : entry.getValue().sounds) {
                        // s = relative path to sound file from "sounds"
                        // directory, without suffix

                        final String assetKey = "assets.minecraft.sounds." + s.name.replace('/', '.');

                        final AssetEntry ae = new AssetEntry(assetKey, EAsset.SOUND);

                        if (Sources.vanilla.doesProvideAsset(ae.getKey())) {
                            // vanilla sound, skip
                        } else {
                            // new sound

                            final String entryname = ae.getPath();

                            final ZipEntry ze_soundfile = zip.getEntry(entryname);

                            if (ze_soundfile != null) {
                                // copy to sounds folder
                                target = new File(project.getCustomSoundsDirectory(), s + ".ogg");
                                ZipUtils.extractZipEntry(zip, ze_soundfile, target);

                                // mark as extracted
                                alreadyExtracted.add(entryname);

                                if (Config.LOG_EXTRACTED_ASSETS) Log.f3("+ SOUND " + entryname);
                            }

                        }
                    }
                }
            }

        } catch (final Exception e) {
            Log.e(e);
            return false;
        }
        return true;
    }


    private boolean stepOtherAssets() {
        File target;

        try {
            for (final String sorig : zipEntries) {
                String s = sorig;

                if (s.contains("__MACOSX") || s.contains(".DS_Store") || s.contains(".fsevents")) {
                    if (Config.LOG_EXTRACTED_ASSETS) Log.f3("x OSX junk: " + s);
                    continue;
                }

                if (alreadyExtracted.contains(s)) continue;

                final ZipEntry ze = zip.getEntry(s);

                if (ze == null) continue; // garbage

                alreadyExtracted.add(s);

                boolean mcmeta = false;

                if (s.endsWith(".mcmeta")) {
                    mcmeta = true;
                    s = Utils.toLastDot(s);
                }

                final String s2 = FileUtils.escapeFilename(s);
                final String[] parts = FileUtils.getFilenameParts(s2);
                final String key = parts[0].replace('\\', '.').replace('/', '.');

                if (Sources.vanilla.doesProvideAsset(key)) {
                    // override for vanilla

                    target = new File(project.getAssetsDirectory(), sorig);

                    ZipUtils.extractZipEntry(zip, ze, target);
                    if (!mcmeta) project.setSourceForFile(key, MagicSources.PROJECT);

                    if (Config.LOG_EXTRACTED_ASSETS) Log.f3("+ PROJECT " + (mcmeta ? "M " : "") + s);

                } else {
                    // extra included file

                    target = new File(project.getExtrasDirectory(), s);
                    ZipUtils.extractZipEntry(zip, ze, target);

                    if (Config.LOG_EXTRACTED_ASSETS) Log.f3("+ EXTRA " + s);
                }
            }

        } catch (final Exception e) {
            Log.e(e);
            return false;
        }
        return true;
    }


    @Override
    protected String getMonitorHeading() {
        return "Loading Resource Pack";
    }


    @Override
    protected void doBefore() {
        Log.f1("Loading resource pack to project");
        Log.f3("Pack file: " + packFile);
    }


    @Override
    protected void doAfter(boolean success) {
        Log.f1("Loading resource pack to project - done.");

        Utils.close(zip);

        after.run();

        Alerts.info(App.getFrame(), "Import successful.");
    }

}
