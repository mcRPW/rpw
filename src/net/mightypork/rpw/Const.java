package net.mightypork.rpw;

import java.awt.Color;

import javax.swing.BorderFactory;

import net.mightypork.rpw.help.VersionUtils;
import net.mightypork.rpw.struct.SoundSubEntry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class Const
{

	public static final int VERSION_SERIAL = 391;

	public static final String VERSION = VersionUtils.getVersionString(VERSION_SERIAL);
	public static final int VERSION_MAJOR = VersionUtils.getVersionMajor(VERSION_SERIAL);

	public static final String APP_NAME = "ResourcePack Workbench";

	//@formatter:off
	public static final String[] SOUND_CATEGORIES = new String[] {
		"ambient",
		"block",
		"hostile",
		"master",
		"music",
		"neutral",
		"player",
		"record",
		"weather"
	};
	//@formatter:on

	private static final GsonBuilder GSB = new GsonBuilder().registerTypeAdapter(SoundSubEntry.class, new SoundSubEntry.Deserializer()).registerTypeAdapter(SoundSubEntry.class,
			new SoundSubEntry.Serializer());
	public static final Gson GSON = GSB.setPrettyPrinting().create();
	public static final Gson GSON_UGLY = GSB.create();

	public static final Color TABLE_ALT_COLOR = new Color(0xF5F9FF);// F5F9FF

	public static final Object TABLE_CELL_INSETS = new javax.swing.plaf.BorderUIResource(BorderFactory.createEmptyBorder(0, 1, 0, 1));
	//@formatter:off
	public static final Object TABLE_HEADER_BORDERS = new javax.swing.plaf.BorderUIResource(
			BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(0, 0, 1, 1, Color.GRAY),
					BorderFactory.createEmptyBorder(3, 5, 3, 5)
			)
	);
	//@formatter:on
}
