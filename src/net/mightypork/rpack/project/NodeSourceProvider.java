package net.mightypork.rpack.project;


public interface NodeSourceProvider {

	public String getSourceForGroup(String groupKey);


	public String getSourceForFile(String assetKey);
}
