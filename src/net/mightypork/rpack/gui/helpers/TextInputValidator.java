package net.mightypork.rpack.gui.helpers;


import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import net.mightypork.rpack.utils.Utils;
import net.mightypork.rpack.utils.validation.CharValidator;


public class TextInputValidator extends KeyAdapter {

	private CharInputListener listener;
	private CharValidator validator;


	public TextInputValidator(CharValidator validator, CharInputListener listener) {

		this.listener = listener;
		this.validator = validator;
	}


	public TextInputValidator(CharValidator validator) {

		this.listener = null;
		this.validator = validator;
	}


	@Override
	public void keyTyped(KeyEvent e) {

		char c = e.getKeyChar();
		if (!isCharOK(c)) {
			e.consume();  // ignore event
			return;
		}

		if (listener != null) listener.onCharTyped(c);
	}


	private boolean isCharOK(char c) {

		if (c == KeyEvent.VK_BACK_SPACE) return true;
		if (c == KeyEvent.VK_LEFT) return true;
		if (c == KeyEvent.VK_RIGHT) return true;
		if (c == KeyEvent.VK_DELETE) return true;
		if (c == KeyEvent.VK_HOME) return true;
		if (c == KeyEvent.VK_END) return true;

		return validator.isValid(c);
	}


	public static TextInputValidator filenames(CharInputListener listener) {

		return new TextInputValidator(filenameCharValidator, listener);
	}


	public static TextInputValidator filenames() {

		return new TextInputValidator(filenameCharValidator);
	}


	public static TextInputValidator identifiers(CharInputListener listener) {

		return new TextInputValidator(identifierCharValidator, listener);
	}


	public static TextInputValidator identifiers() {

		return new TextInputValidator(identifierCharValidator);
	}


	private static final CharValidator filenameCharValidator = new CharValidator() {

		@Override
		public boolean isValid(char c) {

			return Utils.isValidFilenameChar(c);
		}

	};

	private static final CharValidator identifierCharValidator = new CharValidator() {

		@Override
		public boolean isValid(char c) {

			return Utils.isValidIdentifierChar(c);
		}

	};
}
