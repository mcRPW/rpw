package net.mightypork.rpack.gui.windows;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.*;

import net.mightypork.rpack.App;
import net.mightypork.rpack.gui.Icons;
import net.mightypork.rpack.gui.helpers.ClickListener;
import net.mightypork.rpack.hierarchy.tree.AssetTreeLeaf;
import net.mightypork.rpack.project.Projects;
import net.mightypork.rpack.utils.FileUtils;
import net.mightypork.rpack.utils.Log;
import net.mightypork.rpack.utils.Utils;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Style;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.TokenTypes;
import org.fife.ui.rtextarea.RTextScrollPane;


public class DialogEditMeta extends RpwDialog {

	private JButton btnCancel;
	private RSyntaxTextArea textArea;
	private JButton btnSave;
	private JButton btnPresets;
	private JPopupMenu presetsPopup;
	private Box buttons;
	private AssetTreeLeaf editedNode;


	public DialogEditMeta(AssetTreeLeaf node) throws IOException {

		super(App.getFrame(), "MCMETA EDITOR");

		String path = Utils.fromLastChar(node.getAssetEntry().getPath(), '/');

		setTitle(path + ".mcmeta - RPW McMeta editor");

		setResizable(true);

		editedNode = node;

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		textArea = buildTextArea();
		RTextScrollPane sp = new RTextScrollPane(textArea);

		sp.setPreferredSize(new Dimension(800, 600));

		//@formatter:off
		sp.setBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createEmptyBorder(10, 10, 10, 10),
						BorderFactory.createEtchedBorder()
				)
		);		
		//@formatter:on

		sp.setWheelScrollingEnabled(true);
		sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);


		btnPresets = new JButton("Templates", Icons.MENU_GENERATE);
		btnCancel = new JButton("Discard", Icons.MENU_CANCEL);
		btnSave = new JButton("Save", Icons.MENU_YES);

		buttons = Box.createHorizontalBox();

		buttons.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		buttons.add(btnPresets);
		buttons.add(Box.createHorizontalGlue());
		buttons.add(btnSave);
		buttons.add(Box.createHorizontalStrut(5));
		buttons.add(btnCancel);

		presetsPopup = buildPresetsPopup();
		buttons.add(presetsPopup);

		getContentPane().add(sp);
		getContentPane().add(buttons);
		getContentPane().doLayout();


		InputStream in = Projects.getActive().getAssetMetaStream(node.getAssetKey());
		String text = FileUtils.streamToString(in);

		setTextareaText(text);

		prepareForDisplay();
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

		String[] anims = { "clock", "compass", "fire_layer_0", "fire_layer_1", "lava_flow", "lava_still", "portal", "water_flow", "water_still" };

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

		String[] textures = { "enchanted_item_glint", "pumpkinblur", "shadow", "vignette" };

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


	private RSyntaxTextArea buildTextArea() {

		RSyntaxTextArea ta = new RSyntaxTextArea(20, 60);
		ta.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
		ta.setCodeFoldingEnabled(true);
		ta.setAntiAliasingEnabled(true);

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

		return ta;
	}


	@Override
	protected void onShown() {

	}


	@Override
	public void onClose() {

		// nothing
	}


	@Override
	protected void addActions() {

		btnCancel.addActionListener(closeListener);

		btnSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				String text = textArea.getText();

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
					presetsPopup.show(buttons, 0, 0);
					presetsPopup.setVisible(false);
					first = false;
				}

				presetsPopup.show(buttons, btnPresets.getBounds().x, btnPresets.getBounds().y - presetsPopup.getHeight());
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


	private void setTextareaText(String text) {

		textArea.setText(text);
		textArea.revalidate();

		textArea.setCaretPosition(0);

		textArea.requestFocusInWindow();
	}

}
