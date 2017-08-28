package com.pixbits.rpw.stitcher;

import java.io.File;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.*;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.*;
import java.awt.Graphics2D;
import java.security.*;

import javax.imageio.ImageIO;

import com.google.gson.*;

import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.library.Sources;
import net.mightypork.rpw.project.Project;
import net.mightypork.rpw.library.*;
import net.mightypork.rpw.tree.assets.*;
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.logging.Log;


public class Tasks
{

	public static void importPackFromStitchedPng(File inputFolder, Project project, Set<AssetEntry> entries)
	{
		File input = new File(inputFolder.getAbsolutePath() + File.separator + "textures.png");
		File jsonInput = new File(inputFolder.getAbsolutePath() + File.separator + "textures.json");

		if (!input.exists()) {
			Alerts.error(null, "Image not found", "File " + input.getName() + " not found in specified folder.");
			return;
		}

		if (!jsonInput.exists()) {
			Alerts.error(null, "Json not found", "File " + input.getName() + " not found in specified folder.");
            return;
		}

		try {
			StitchJson.Category json = new GsonBuilder().create().fromJson(FileUtils.fileToString(jsonInput), StitchJson.Category.class);
			BufferedImage image = ImageIO.read(input);

			MessageDigest digest = MessageDigest.getInstance("MD5");

			for (StitchJson.Element element : json.elements) {
			    digest.reset();

				BigInteger savedHash = new BigInteger(element.hashCode, 16);
				BigInteger computedHash = new BigInteger(1, computeHashcodeForSprite(image, element.x, element.y, element.w, element.h, digest));

				if (!savedHash.equals(computedHash)) {
					// overwrite existing asset to new one
					File outputAsset = project.getAssetFile(element.key);
					if (outputAsset != null) FileUtils.delete(outputAsset, false);
					else outputAsset = new File(project.getAssetsDirectory() + File.separator + element.key.replaceAll("\\.", File.separator) + ".png");

					BufferedImage sprite = new BufferedImage(element.w, element.h, image.getType());
					sprite.getGraphics().drawImage(image, 0, 0, element.w, element.h, element.x, element.y, element.x + element.w, element.y + element.h, null);

					project.setSourceForFile(element.key, MagicSources.PROJECT);

					outputAsset.mkdirs();
					ImageIO.write(sprite, "PNG", outputAsset);
					Log.i("Stitcher import, updating " + element.key + " to " + outputAsset.getAbsolutePath());
				}

			}

			project.saveConfigFiles();
			net.mightypork.rpw.tasks.Tasks.taskTreeRebuild();
			return;
		} catch (Exception e) {
			Log.e(e);
			return;
		}

	}


	public static void exportPackToStitchedPng(File outputFolder, Project project, List<AssetEntry> entries, String textureSource, Scale scale)
	{
		VanillaPack vanilla = Sources.vanilla;

		File output = new File(outputFolder.getAbsolutePath() + File.separator + "textures.png");

		try {
			StitchJson.Category json = new StitchJson.Category();
			json.elements = new ArrayList<StitchJson.Element>();

			MessageDigest digest = MessageDigest.getInstance("MD5");

			AssetLayout layout = new AssetLayout();

			for (AssetEntry entry : entries) {
				boolean hasCustom = project.getAssetFile(entry.getKey()) != null;
				File file = null;

				if (textureSource == "Project" && hasCustom) {
					file = project.getAssetFile(entry.getKey());
				} else if (hasCustom == false) {
					continue;
				} else {
					file = vanilla.getAssetFile(entry.getKey());
				}

				if (file != null) {
					AssetImage iasset = new AssetImage(file, entry);

					iasset.cacheImage(scale.scale, scale.scale);
					layout.add(iasset);
				}
			}

			Point size = layout.computeLayout();
			BufferedImage image = new BufferedImage(size.x, size.y, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = (Graphics2D) image.getGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

			for (AssetImage e : layout) {
				digest.reset();

				BufferedImage img = e.image;

				g2d.drawImage(img, e.x(), e.y(), e.width(), e.height(), null);

				byte[] hashCode = computeHashcodeForSprite(image, e.x(), e.y(), e.width(), e.height(), digest);
				BigInteger bi = new BigInteger(1, hashCode);
				e.element.hashCode = String.format("%0" + (hashCode.length << 1) + "X", bi);

				json.elements.add(e.element);
			}

			ImageIO.write(image, "png", output);
			Log.i("Stiched " + layout.getEntries().size() + " textures into " + output.getPath());

			File gsonOutput = new File(outputFolder.getAbsolutePath() + File.separator + "textures.json");
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			FileUtils.stringToFile(gsonOutput, gson.toJson(json, StitchJson.Category.class));
		} catch (Exception exception) {
			Log.e(exception);
		}
	}


	public static byte[] computeHashcodeForSprite(BufferedImage image, final int x, final int y, final int w, final int h, final MessageDigest digest)
	{
		ByteBuffer b = ByteBuffer.allocate(w * h * 4);
		for (int ix = x; ix < x + w; ++ix)
			for (int iy = y; iy < y + h; ++iy) {
				b.putInt(image.getRGB(ix, iy));
			}

		return digest.digest(b.array());
	}
}