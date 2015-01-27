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
import net.mightypork.rpw.tree.assets.groups.*;
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.logging.Log;


public class Tasks
{
	public static void importPackFromStitchedPng(File inputFolder, Project project, Set<AssetCategory> categories)
	{
		boolean keep = true;

		for (AssetCategory c : categories)
			if (keep)
				keep = importPackFromStitchedPng(inputFolder, project, c);
	}


	private static boolean importPackFromStitchedPng(File inputFolder, Project project, AssetCategory category)
	{
		File input = new File(inputFolder.getAbsolutePath() + File.separator + category.name.toLowerCase() + ".png");
		File jsonInput = new File(inputFolder.getAbsolutePath() + File.separator + category.name.toLowerCase() + ".json");

		if (!input.exists()) {
			Alerts.error(null, "Image not found", "File " + input.getName() + " not found in specified folder.");
			return false;
		}

		if (!jsonInput.exists()) {
			Alerts.error(null, "Json not found", "File " + input.getName() + " not found in specified folder.");
			return false;
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

					if (outputAsset != null)
						FileUtils.delete(outputAsset, false);
					else
						outputAsset = new File(project.getAssetsDirectory() + File.separator + element.key.replaceAll("\\.", File.separator) + ".png");

					BufferedImage sprite = new BufferedImage(element.w, element.h, image.getType());
					sprite.getGraphics().drawImage(image, 0, 0, element.w, element.h, element.x, element.y, element.x + element.w, element.y + element.h, null);

					project.setSourceForFile(element.key, MagicSources.PROJECT);

					outputAsset.mkdirs();
					ImageIO.write(sprite, "PNG", outputAsset);
					Log.i("Stitcher import, updating " + element.key + " to " + outputAsset.getAbsolutePath());

				}

			}

			project.saveToTmp();
			net.mightypork.rpw.tasks.Tasks.taskTreeRebuild();
			return true;
		} catch (Exception e) {
			Log.e(e);
			return false;
		}

	}


	public static void exportPackToStitchedPng(File outputFolder, Project project, Set<AssetCategory> categories, boolean exportMissing, boolean exportExisting, BlockSize blockSize)
	{
		for (AssetCategory c : categories)
			exportPackToStitchedPng(outputFolder, project, c, exportMissing, exportExisting, blockSize);
	}


	private static void exportPackToStitchedPng(File outputFolder, Project project, AssetCategory category, boolean exportMissing, boolean exportExisting, BlockSize blockSize)
	{
		VanillaPack vanilla = Sources.vanilla;

		File output = new File(outputFolder.getAbsolutePath() + File.separator + category.name.toLowerCase() + ".png");

		Collection<AssetEntry> totalEntries = vanilla.getAssetEntries();
		List<AssetEntry> entries = new ArrayList<AssetEntry>();

		GroupFilter filter = new GroupFilter(null, category.prefix);
		// GroupFilter fontFilter = new GroupFilter(null,
		// "assets.minecraft.textures.font.*");

		for (AssetEntry e : totalEntries) {
			if (filter.matches(e) /* && !fontFilter.matches(e) */)
				entries.add(e);
		}

		try {
			StitchJson.Category json = new StitchJson.Category();
			json.category = category;
			json.elements = new ArrayList<StitchJson.Element>();

			// final int w = dimension.width, h = dimension.height;
			// final int tw = imageSize.width, th = imageSize.height;
			// int x = 0, y = 0;

			MessageDigest digest = MessageDigest.getInstance("MD5");

			AssetLayout layout = new AssetLayout();

			for (AssetEntry e : entries) {
				boolean hasCustom = project.getAssetFile(e.getKey()) != null;
				File file = null;

				if (exportExisting && hasCustom)
					file = project.getAssetFile(e.getKey());
				else if (hasCustom)
					continue;

				if (file == null && exportMissing)
					file = vanilla.getAssetFile(e.getKey());

				if (file != null) {
					AssetImage iasset = new AssetImage(file, e);

					if (category == AssetCategory.BLOCKS && blockSize != BlockSize.NO_CHANGE)
						iasset.cacheImage(blockSize.size, blockSize.size);
					else
						iasset.cacheImage();
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

			File gsonOutput = new File(outputFolder.getAbsolutePath() + File.separator + category.name.toLowerCase() + ".json");
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
