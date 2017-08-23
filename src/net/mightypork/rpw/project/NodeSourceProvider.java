package net.mightypork.rpw.project;

public interface NodeSourceProvider {
    String getSourceForGroup(String groupKey);


    String getSourceForFile(String assetKey);
}
