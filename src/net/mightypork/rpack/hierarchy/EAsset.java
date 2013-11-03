package net.mightypork.rpack.hierarchy;


/**
 * Enum of asset filetypes
 * 
 * @author MightyPork
 */
public enum EAsset {
	SOUND("ogg"), IMAGE("png"), TEXT("txt"), LANG("lang"), PROPERTIES("properties"), INI("ini"), CFG("cfg");

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


	public static EAsset forExtension(String ext) {

		for (EAsset a : EAsset.values()) {
			if (a.extension.equalsIgnoreCase(ext)) {
				return a;
			}
		}

		return null;
	}
}
