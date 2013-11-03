package net.mightypork.rpack.library;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import net.mightypork.rpack.hierarchy.AssetEntry;
import net.mightypork.rpack.hierarchy.EAsset;
import net.mightypork.rpack.utils.FileUtils;


public class SilenceSource implements ISource {

	@Override
	public boolean doesProvideAsset(String key) {

		AssetEntry ae = Sources.vanilla.getAssetForKey(key);
		return ae.getType() == EAsset.SOUND;
	}


	@Override
	public InputStream getAssetStream(String key) throws IOException {

		if (!doesProvideAsset(key)) return null;

		return FileUtils.getResource("/data/export/silence.ogg");
	}


	@Override
	public File getAssetFile(String key) {

		return null;
	}


	@Override
	public File getAssetMetaFile(String key) {

		return null;
	}


	@Override
	public File getAssetsBaseDirectory() {

		return null;
	}


	@Override
	public InputStream getAssetMetaStream(String key) throws IOException {

		return null;
	}


	@Override
	public boolean doesProvideAssetMeta(String key) {

		return false;
	}

}
