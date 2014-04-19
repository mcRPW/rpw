package net.mightypork.rpw.project;


public interface NodeSourceProvider {
	
	public String getSourceForGroup(String groupKey);
	
	
	public String getSourceForFile(String assetKey);
}
