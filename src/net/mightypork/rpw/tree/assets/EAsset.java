package net.mightypork.rpw.tree.assets;


import java.io.File;

import net.mightypork.rpw.utils.FileUtils;


/**
 * Enum of asset filetypes
 * 
 * @author MightyPork
 */
public enum EAsset {

	//@formatter:off
	SOUND("ogg"), IMAGE("png"), TEXT("txt"), LANG("lang"),
	PROPERTIES("properties"), INI("ini"), MCMETA("mcmeta"),
	CFG("cfg"), UNKNOWN("");
	//@formatter:on

	private EAsset(String extension) {

		this.extension = extension;
	}

	private String extension;


	public String getExtension() {

		return this.extension;
	}


	public boolean isText() {

		switch (this) {
			case TEXT:
			case LANG:
			case PROPERTIES:
			case INI:
			case CFG:
				return true;
			default:
				return false;
		}
	}


	public boolean isImage() {

		return this == IMAGE;
	}


	public boolean isSound() {

		return this == SOUND;
	}


	public boolean isMeta() {

		return this == MCMETA;
	}


	public boolean isAsset() {

		return isText() || isImage() || isSound();
	}


	public boolean isAssetOrMeta() {

		return isMeta() || isAsset();
	}


	public boolean isUnknown() {

		return this == UNKNOWN;
	}


	public static EAsset forExtension(String ext) {

		for (EAsset a : EAsset.values()) {
			if (a.extension.equalsIgnoreCase(ext)) {
				return a;
			}
		}

		return UNKNOWN;
	}


	public static EAsset forFile(File file) {

		return forExtension(FileUtils.getExtension(file));
	}


	public static EAsset forFile(String file) {

		return forExtension(FileUtils.getExtension(file));
	}


}
