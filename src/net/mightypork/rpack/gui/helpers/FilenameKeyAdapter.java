package net.mightypork.rpack.gui.helpers;


import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import net.mightypork.rpack.utils.Utils;


public class FilenameKeyAdapter extends KeyAdapter {

	private CharInputListener listener;


	public FilenameKeyAdapter(CharInputListener listener) {

		this.listener = listener;
	}


	@Override
	public void keyTyped(KeyEvent e) {

		char c = e.getKeyChar();
		if (!isCharOK(c)) {
			e.consume();  // ignore event
			return;
		}

		listener.onCharTyped(c);
	}


	private boolean isCharOK(char c) {

		if (c == KeyEvent.VK_BACK_SPACE) return true;
		if (c == KeyEvent.VK_LEFT) return true;
		if (c == KeyEvent.VK_RIGHT) return true;
		if (c == KeyEvent.VK_DELETE) return true;
		if (c == KeyEvent.VK_HOME) return true;
		if (c == KeyEvent.VK_END) return true;

		return Utils.isValidFilenameChar(c);
	}
}
