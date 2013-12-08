package net.mightypork.rpw.gui.windows.dialogs;


import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.*;

import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.helpers.ClickListener;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.tree.assets.tree.AssetTreeLeaf;
import net.mightypork.rpw.utils.FileUtils;
import net.mightypork.rpw.utils.Utils;
import net.mightypork.rpw.utils.logging.Log;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Style;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.TokenTypes;
import com.google.gson.JsonParser;


public class DialogEditMeta extends DialogEditorBase {

	private JButton btnCancel;
	private JButton btnSave;
	private JButton btnPresets;
	private JButton btnCheck;
	private JPopupMenu presetsPopup;
	protected AssetTreeLeaf editedNode;


	public DialogEditMeta(AssetTreeLeaf node) {

		this.editedNode = node;

		createDialog();
	}


	@Override
	protected String getTitleText() {

		String path = Utils.fromLastChar(editedNode.getAssetEntry().getPath(), '/');

		return path + ".mcmeta - RPW McMeta editor";
	}


	@Override
	protected String getInitialText() {

		InputStream in;
		try {
			in = Projects.getActive().getAssetMetaStream(editedNode.getAssetKey());
		} catch (IOException e) {
			Log.e(e);
			return "";
		}

		return FileUtils.streamToString(in);
	}


	@Override
	protected void buildButtons(Box buttons) {

		btnPresets = new JButton("Templates", Icons.MENU_GENERATE);
		btnCheck = new JButton("Check JSON", Icons.MENU_INFO);
		btnCancel = new JButton("Discard", Icons.MENU_CANCEL);
		btnSave = new JButton("Save", Icons.MENU_YES);

		buttons.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		buttons.add(btnPresets);
		buttons.add(Box.createHorizontalStrut(5));
		buttons.add(btnCheck);
		buttons.add(Box.createHorizontalGlue());
		buttons.add(btnSave);
		buttons.add(Box.createHorizontalStrut(5));
		buttons.add(btnCancel);

		presetsPopup = buildPresetsPopup();
		buttons.add(presetsPopup);
	}


	private JPopupMenu buildPresetsPopup() {

		JPopupMenu popup = new JPopupMenu();

		JMenu menu2, menu3;

		JMenuItem item;

		popup.add(menu2 = new JMenu("Animation"));

		int[] frames = { 8, 16, 20, 32, 64 };

		for (int i : frames) {
			menu2.add(menu3 = new JMenu(i + " frames"));

			menu3.add(item = new JMenuItem("Linear"));
			item.setActionCommand("animation/" + i + "linear.txt");
			item.addActionListener(loadTemplateListener);

			menu3.add(item = new JMenuItem("Reverse"));
			item.setActionCommand("animation/" + i + "reverse.txt");
			item.addActionListener(loadTemplateListener);

			menu3.add(item = new JMenuItem("ZigZag"));
			item.setActionCommand("animation/" + i + "zigzag.txt");
			item.addActionListener(loadTemplateListener);
		}

		menu2.add(menu3 = new JMenu("Vanilla"));

		//@formatter:off
		String[] anims = { 
				"clock",
				"compass",
				"fire_layer_0",
				"fire_layer_1",
				"lava_flow",
				"lava_still",
				"portal",
				"water_flow",
				"water_still"
		};
		//@formatter:on

		for (String a : anims) {
			menu3.add(item = new JMenuItem(a));
			item.setActionCommand("animation/" + a + ".txt");
			item.addActionListener(loadTemplateListener);
		}

		menu2.addSeparator();

		menu2.add(item = new JMenuItem("Default"));
		item.setActionCommand("animation/default_animation.txt");
		item.addActionListener(loadTemplateListener);


		popup.add(menu2 = new JMenu("Texture"));

		menu2.add(menu3 = new JMenu("Vanilla"));

		//@formatter:off
		String[] textures = {
				"enchanted_item_glint",
				"pumpkinblur",
				"shadow",
				"vignette"
		};
		//@formatter:on

		for (String t : textures) {
			menu3.add(item = new JMenuItem(t));
			item.setActionCommand("texture/" + t + ".txt");
			item.addActionListener(loadTemplateListener);
		}

		menu2.addSeparator();

		menu2.add(item = new JMenuItem("Default"));
		item.setActionCommand("texture/default_texture.txt");
		item.addActionListener(loadTemplateListener);

		menu2.addSeparator();

		menu2.add(item = new JMenuItem("Blur"));
		item.setActionCommand("texture/blur.txt");
		item.addActionListener(loadTemplateListener);

		menu2.add(item = new JMenuItem("Clamp"));
		item.setActionCommand("texture/clamp.txt");
		item.addActionListener(loadTemplateListener);

		return popup;
	}


	@Override
	protected void addActions() {

		btnCancel.addActionListener(closeListener);


		btnCheck.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				String text = getTextArea().getText();

				JsonParser jp = new JsonParser();

				try {
					jp.parse(text);
					Alerts.info(DialogEditMeta.this, "Check JSON", "Entered code is (probably) valid.");
				} catch (Exception er) {
					Alerts.warning(DialogEditMeta.this, "Check JSON", "Entered code contains\n a SYNTAX ERROR!");
				}

			}
		});

		btnSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				String text = getTextArea().getText();

				File file = Projects.getActive().getAssetMetaFile(editedNode.getAssetKey());

				try {
					FileUtils.stringToFile(file, text);
					closeDialog();
				} catch (IOException e1) {
					Log.e(e1);

					Alerts.error(self(), "Could not save file.");

				}
			}
		});

		btnPresets.addMouseListener(new ClickListener() {

			boolean first = true;


			@Override
			public void mouseClicked(MouseEvent e) {

				if (first) {
					presetsPopup.show(getButtonsBox(), 0, 0);
					presetsPopup.setVisible(false);
					first = false;
				}

				presetsPopup.show(getButtonsBox(), btnPresets.getBounds().x, btnPresets.getBounds().y - presetsPopup.getHeight());
			}
		});
	}

	private ActionListener loadTemplateListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			String res = e.getActionCommand();

			InputStream in = FileUtils.getResource("/data/mcmeta/" + res);

			String text = FileUtils.streamToString(in);

			setTextareaText(text);
		}
	};


	@Override
	protected void configureTextarea(RSyntaxTextArea ta) {

		ta.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
		Font font = new Font(Font.MONOSPACED, Font.PLAIN, 16);

		// destroy all styles
		SyntaxScheme ss = ta.getSyntaxScheme();
		ss = (SyntaxScheme) ss.clone();

		for (int i = 0; i < ss.getStyleCount(); i++) {
			if (ss.getStyle(i) != null) {
				ss.getStyle(i).font = font;
				ss.getStyle(i).foreground = Color.black;
				ss.getStyle(i).background = null;
				ss.getStyle(i).underline = false;
			}
		}

		Style s;

		s = ss.getStyle(TokenTypes.ERROR_CHAR);
		s.foreground = Color.RED;
		s.underline = true;

		s = ss.getStyle(TokenTypes.ERROR_STRING_DOUBLE);
		s.foreground = Color.RED;
		s.underline = true;

		s = ss.getStyle(TokenTypes.ERROR_NUMBER_FORMAT);
		s.foreground = Color.RED;
		s.underline = true;

		s = ss.getStyle(TokenTypes.ERROR_IDENTIFIER);
		s.foreground = Color.RED;
		s.underline = true;


		s = ss.getStyle(TokenTypes.WHITESPACE);
		s.foreground = null;
		s.background = null;


		s = ss.getStyle(TokenTypes.LITERAL_STRING_DOUBLE_QUOTE);
		s.foreground = new Color(0x0000FF);

		s = ss.getStyle(TokenTypes.LITERAL_NUMBER_DECIMAL_INT);
		s.foreground = new Color(0xB08000);

		s = ss.getStyle(TokenTypes.SEPARATOR);
		s.foreground = Color.black;
		s.font = font.deriveFont(Font.BOLD);

		s = ss.getStyle(TokenTypes.OPERATOR);
		s.foreground = Color.black;
		s.font = font.deriveFont(Font.BOLD);

		s = ss.getStyle(TokenTypes.LITERAL_BOOLEAN);
		s.foreground = new Color(0x006e28);
		s.font = font.deriveFont(Font.BOLD);


		Color commentColor = new Color(0x646464);

		ss.getStyle(TokenTypes.COMMENT_EOL).foreground = commentColor;
		ss.getStyle(TokenTypes.COMMENT_DOCUMENTATION).foreground = commentColor;
		ss.getStyle(TokenTypes.COMMENT_KEYWORD).foreground = commentColor;
		ss.getStyle(TokenTypes.COMMENT_MARKUP).foreground = commentColor;
		ss.getStyle(TokenTypes.COMMENT_MULTILINE).foreground = commentColor;

		ta.setSyntaxScheme(ss);
		ta.setFont(font);

	}

}
