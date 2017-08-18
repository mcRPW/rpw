package net.mightypork.rpw.gui.helpers;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import net.mightypork.rpw.utils.Utils;
import net.mightypork.rpw.utils.validation.CharValidator;


public class TextInputValidator extends KeyAdapter {

    private final CharInputListener listener;
    private final CharValidator validator;


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
        final char c = e.getKeyChar();
        if (!isCharControl(c) && !validator.isValid(c)) {
            e.consume(); // ignore event
            return;
        }

        if (listener != null) listener.onCharTyped(c);
    }


    private boolean isCharControl(char c) {
        if (c == KeyEvent.VK_BACK_SPACE) return true;
        if (c == KeyEvent.VK_LEFT) return true;
        if (c == KeyEvent.VK_RIGHT) return true;
        if (c == KeyEvent.VK_DELETE) return true;
        if (c == KeyEvent.VK_HOME) return true;
        if (c == KeyEvent.VK_END) return true;

        return false;
    }


    public static TextInputValidator filenames(CharInputListener listener) {
        return new TextInputValidator(filenameCharValidator, listener);
    }


    public static TextInputValidator filenames() {
        return new TextInputValidator(filenameCharValidator);
    }


    public static TextInputValidator strictFilenames() {
        return new TextInputValidator(strictFilenameCharValidator);
    }


    public static TextInputValidator strictFilenames(CharInputListener listener) {
        return new TextInputValidator(strictFilenameCharValidator, listener);
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

    private static final CharValidator strictFilenameCharValidator = new CharValidator() {

        @Override
        public boolean isValid(char c) {
            return String.valueOf(c).matches("[a-zA-Z0-9_-]+");
        }

    };

    private static final CharValidator identifierCharValidator = new CharValidator() {

        @Override
        public boolean isValid(char c) {
            return Utils.isValidIdentifierChar(c);
        }

    };
}
