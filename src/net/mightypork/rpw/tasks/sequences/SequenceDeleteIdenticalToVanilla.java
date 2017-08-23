package net.mightypork.rpw.tasks.sequences;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import net.mightypork.rpw.App;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.library.MagicSources;
import net.mightypork.rpw.library.Sources;
import net.mightypork.rpw.project.Project;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.tasks.Tasks;
import net.mightypork.rpw.tree.assets.AssetEntry;
import net.mightypork.rpw.utils.files.DirectoryTreeDifferenceFinder;
import net.mightypork.rpw.utils.logging.Log;


public class SequenceDeleteIdenticalToVanilla extends AbstractMonitoredSequence {
    private final Project project;

    private int count = 0;


    public SequenceDeleteIdenticalToVanilla() {
        this.project = Projects.getActive();
    }


    @Override
    protected String getMonitorHeading() {
        return "Deleting unchanged copies of vanilla assets";
    }


    @Override
    public int getStepCount() {
        return 2;
    }


    @Override
    public String getStepName(int step) {
        //@formatter:off
        switch (step) {
            case 0:
                return "Finding and deleting vanilla copies";
            case 1:
                return "Done";
        }
        //@formatter:on

        return null;
    }


    @Override
    protected boolean step(int step) {
        //@formatter:off
        switch (step) {
            case 0:
                return stepDeleteCrap();
            case 1:
                return true;
        }
        //@formatter:on

        return false;
    }


    private boolean stepDeleteCrap() {
        Collection<AssetEntry> entries = Sources.vanilla.getAssetEntries();

        for (AssetEntry e : entries) {
            String key = e.getKey();

            File pro = project.getAssetFile(key);
            File van = Sources.vanilla.getAssetFile(key);

            try {
                if (DirectoryTreeDifferenceFinder.areFilesEqual(pro, van)) {
                    Log.f3("Deleting: " + key);
                    pro.delete();
                    project.setSourceForFile(key, MagicSources.INHERIT);
                    count++;
                }
            } catch (IOException e1) {
                Log.e(e1);
            }
        }

        Tasks.taskTreeRedraw();
        Projects.markChange();

        return true;
    }


    @Override
    protected void doBefore() {
    }


    @Override
    protected void doAfter(boolean success) {
        if (success) Alerts.info(App.getFrame(), "Successfully removed " + count + " Vanilla duplicates from project.");
    }
}
