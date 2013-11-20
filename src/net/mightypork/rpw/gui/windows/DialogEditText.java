package net.mightypork.rpw.gui.windows;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.*;

import net.mightypork.rpw.App;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.helpers.ClickListener;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.tree.assets.tree.AssetTreeLeaf;
import net.mightypork.rpw.utils.FileUtils;
import net.mightypork.rpw.utils.Log;
import net.mightypork.rpw.utils.SimpleConfig;
import net.mightypork.rpw.utils.Utils;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Style;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.TokenTypes;
import org.fife.ui.rtextarea.RTextScrollPane;


public class DialogEditText extends RpwDialog {

	private JButton btnCancel;
	private RSyntaxTextArea textArea;
	private JButton btnSave;
	private JButton btnFormatCodes;
	private JPopupMenu formatCodesPopup;
	private Box buttons;
	private AssetTreeLeaf editedNode;


	public DialogEditText(AssetTreeLeaf node) throws IOException {

		super(App.getFrame(), "TEXT EDITOR");

		String path = Utils.fromLastChar(node.getAssetEntry().getPath(), '/');

		setTitle(path + " - RPW Text editor");

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


		btnFormatCodes = new JButton("Formatting codes", Icons.MENU_GENERATE);
		btnCancel = new JButton("Discard", Icons.MENU_CANCEL);
		btnSave = new JButton("Save", Icons.MENU_YES);

		buttons = Box.createHorizontalBox();

		buttons.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		buttons.add(btnFormatCodes);
		buttons.add(Box.createHorizontalGlue());
		buttons.add(btnSave);
		buttons.add(Box.createHorizontalStrut(5));
		buttons.add(btnCancel);

		formatCodesPopup = buildCodesPopup();
		buttons.add(formatCodesPopup);

		getContentPane().add(sp);
		getContentPane().add(buttons);
		getContentPane().doLayout();


		InputStream in = Projects.getActive().getAssetStream(node.getAssetKey());
		String text = FileUtils.streamToString(in);

		setTextareaText(text);

		prepareForDisplay();
	}


	private JPopupMenu buildCodesPopup() {

		JPopupMenu popup = new JPopupMenu();

		InputStream in = FileUtils.getResource("/data/misc/colorcodes.txt");
		String text = FileUtils.streamToString(in);
		Map<String, String> map = SimpleConfig.mapFromString(text);

		JMenuItem item;

		for (Entry<String, String> e : map.entrySet()) {
			if (e.getKey() == null || e.getValue() == null) {
				popup.addSeparator();
				continue;
			}

			String code = e.getKey();
			String label = e.getValue();

			String[] parts = label.split("[|]");

			String labelText = parts[0];
			String colorText = parts[1];

			int colorCode = Integer.parseInt(colorText, 16);

			popup.add(item = new JMenuItem(code.replace("\u00A7", "\u00A7 ") + " - " + labelText));

			item.setForeground(new Color(colorCode));
			item.setActionCommand(code);
			item.addActionListener(insertFormattingCodeListener);
		}

		return popup;
	}


	private RSyntaxTextArea buildTextArea() {

		RSyntaxTextArea ta = new RSyntaxTextArea(20, 60);

		String mime;

		switch (editedNode.getAssetType()) {
			case CFG:
			case INI:
			case LANG:
			case PROPERTIES:
				mime = SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE;
				break;

			default:
				mime = SyntaxConstants.SYNTAX_STYLE_NONE;
		}


		ta.setSyntaxEditingStyle(mime);
		ta.setCodeFoldingEnabled(false);
		ta.setAntiAliasingEnabled(true);

		Font font = new Font(Font.MONOSPACED, Font.PLAIN, 16);

		// destroy all styles
		SyntaxScheme ss = ta.getSyntaxScheme();
		ss = (SyntaxScheme) ss.clone();
		ss.restoreDefaults(font);

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
		s.foreground = new Color(0xbf030c);

		s = ss.getStyle(TokenTypes.OPERATOR);
		s.foreground = new Color(0x006e28);

		s = ss.getStyle(TokenTypes.RESERVED_WORD);
		s.foreground = new Color(0x0057ae);
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

				File file = Projects.getActive().getAssetFile(editedNode.getAssetKey());

				try {
					FileUtils.stringToFile(file, text);
					closeDialog();
				} catch (IOException e1) {
					Log.e(e1);

					Alerts.error(self(), "Could not save file.");

				}
			}
		});

		btnFormatCodes.addMouseListener(new ClickListener() {

			boolean first = true;


			@Override
			public void mouseClicked(MouseEvent e) {

				if (first) {
					formatCodesPopup.show(buttons, 0, 0);
					formatCodesPopup.setVisible(false);
					first = false;
				}

				formatCodesPopup.show(buttons, btnFormatCodes.getBounds().x, btnFormatCodes.getBounds().y - formatCodesPopup.getHeight());
			}
		});
	}

	private ActionListener insertFormattingCodeListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			String code = e.getActionCommand();

			textArea.insert(code, textArea.getCaretPosition());

			textArea.requestFocusInWindow();
		}
	};


	private void setTextareaText(String text) {

		textArea.setText(text);
		textArea.revalidate();

		textArea.setCaretPosition(0);
	}

}
