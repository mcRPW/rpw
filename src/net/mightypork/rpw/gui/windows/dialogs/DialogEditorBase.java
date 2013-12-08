package net.mightypork.rpw.gui.windows.dialogs;


import java.awt.Dimension;

import javax.swing.*;

import net.mightypork.rpw.App;
import net.mightypork.rpw.gui.windows.RpwDialog;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;


public abstract class DialogEditorBase extends RpwDialog {

	private RSyntaxTextArea ta;
	private Box buttonsBox;
	
	public DialogEditorBase() {

		super(App.getFrame(), "Text Editor"); // dummy title
		
	}
	
	@Override
	protected final JComponent buildGui() {

		setTitle(getTitleText());

		setResizable(true);

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		ta = buildTextArea();
		RTextScrollPane sp = new RTextScrollPane(ta);

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

		getContentPane().add(sp);
		
		buttonsBox = Box.createHorizontalBox();
		
		buildButtons(buttonsBox);

		buttonsBox.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		
		getContentPane().add(buttonsBox);
		getContentPane().doLayout();

		return null;
	}
	
	
	public Box getButtonsBox() {

		return buttonsBox;
	}
	
	protected abstract String getTitleText();

	protected abstract void buildButtons(Box buttons);

	@Override
	protected final void initGui() {

		setTextareaText(getInitialText());
	}
	
	
	protected RSyntaxTextArea getTextArea() {
		return ta;
	}
	
	
	protected abstract String getInitialText();


	private RSyntaxTextArea buildTextArea() {

		RSyntaxTextArea ta = new RSyntaxTextArea(20, 60);
		ta.setCodeFoldingEnabled(true);
		ta.setAntiAliasingEnabled(true);
		
		configureTextarea(ta);
		
		return ta;
	}


	protected abstract void configureTextarea(RSyntaxTextArea textarea);

	@Override
	protected abstract void addActions();


	protected final void setTextareaText(String text) {

		ta.setText(text);
		ta.revalidate();

		ta.setCaretPosition(0);

		ta.requestFocusInWindow();
	}

}
