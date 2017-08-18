package net.mightypork.rpw.gui.windows.dialogs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.ScrollPaneConstants;

import net.mightypork.rpw.App;
import net.mightypork.rpw.gui.widgets.HBox;
import net.mightypork.rpw.gui.widgets.VBox;
import net.mightypork.rpw.gui.windows.RpwDialog;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Style;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.TokenTypes;
import org.fife.ui.rtextarea.RTextScrollPane;


public abstract class DialogEditorBase extends RpwDialog {

    private RSyntaxTextArea ta;
    private HBox buttonsBox;


    public DialogEditorBase() {
        super(App.getFrame(), "Text Editor"); // dummy title

    }


    @Override
    protected final JComponent buildGui() {
        setTitle(getTitleText());

        setResizable(true);

        final VBox vb = new VBox();
        vb.windowPadding();

        vb.heading(getFileName());

        ta = buildTextArea();
        final RTextScrollPane sp = new RTextScrollPane(ta);

        sp.setPreferredSize(new Dimension(800, 600));

        sp.setBorder(BorderFactory.createEtchedBorder());

        sp.setWheelScrollingEnabled(true);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        vb.add(sp);

        buttonsBox = new HBox();
        buttonsBox.setMaximumSize(new Dimension(9999, 30));

        buildButtons(buttonsBox);

        vb.gap();

        vb.add(buttonsBox);
        vb.doLayout();

        return vb;
    }


    protected abstract String getFileName();


    public Box getButtonsBox() {
        return buttonsBox;
    }


    protected abstract String getTitleText();


    protected abstract void buildButtons(HBox buttons);


    @Override
    protected final void initGui() {
        setTextareaText(getInitialText());
    }


    protected RSyntaxTextArea getTextArea() {
        return ta;
    }


    protected abstract String getInitialText();


    private RSyntaxTextArea buildTextArea() {
        final RSyntaxTextArea ta = new RSyntaxTextArea(20, 60);
        ta.setCodeFoldingEnabled(true);
        ta.setAntiAliasingEnabled(true);

        configureTextarea(ta);

        return ta;
    }


    protected abstract void configureTextarea(RSyntaxTextArea textarea);


    protected void configureTextareaJSON(RSyntaxTextArea ta) {
        ta.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
        final Font font = new Font(Font.MONOSPACED, Font.PLAIN, 16);

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

        final Color commentColor = new Color(0x646464);

        ss.getStyle(TokenTypes.COMMENT_EOL).foreground = commentColor;
        ss.getStyle(TokenTypes.COMMENT_DOCUMENTATION).foreground = commentColor;
        ss.getStyle(TokenTypes.COMMENT_KEYWORD).foreground = commentColor;
        ss.getStyle(TokenTypes.COMMENT_MARKUP).foreground = commentColor;
        ss.getStyle(TokenTypes.COMMENT_MULTILINE).foreground = commentColor;

        ta.setSyntaxScheme(ss);
        ta.setFont(font);
    }


    protected void configureTextareaPlain(RSyntaxTextArea ta) {
        configureTextareaBasic(ta, SyntaxConstants.SYNTAX_STYLE_NONE);
    }


    protected void configureTextareaConfig(RSyntaxTextArea ta) {
        configureTextareaBasic(ta, SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE);
    }


    private void configureTextareaBasic(RSyntaxTextArea ta, String mime) {
        ta.setSyntaxEditingStyle(mime);
        ta.setCodeFoldingEnabled(false);

        final Font font = new Font(Font.MONOSPACED, Font.PLAIN, 16);

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

        final Color commentColor = new Color(0x646464);

        ss.getStyle(TokenTypes.COMMENT_EOL).foreground = commentColor;
        ss.getStyle(TokenTypes.COMMENT_DOCUMENTATION).foreground = commentColor;
        ss.getStyle(TokenTypes.COMMENT_KEYWORD).foreground = commentColor;
        ss.getStyle(TokenTypes.COMMENT_MARKUP).foreground = commentColor;
        ss.getStyle(TokenTypes.COMMENT_MULTILINE).foreground = commentColor;

        ta.setSyntaxScheme(ss);
        ta.setFont(font);
    }


    @Override
    protected abstract void addActions();


    protected final void setTextareaText(String text) {
        ta.setText(text);
        ta.revalidate();

        ta.setCaretPosition(0);

        ta.requestFocusInWindow();
    }

}
