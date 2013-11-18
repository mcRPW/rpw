package net.mightypork.rpw.gui.windows;


import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.KeyStroke;

import net.mightypork.rpw.gui.helpers.WindowCloseListener;
import net.mightypork.rpw.utils.GuiUtils;


public abstract class RpwDialog extends JDialog {

	private boolean onCloseCalled = false;

	private Runnable closeHook = null;


	public void addCloseHook(Runnable hook) {

		this.closeHook = hook;
	}

	protected final ActionListener closeListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			closeDialog();
		}
	};

	private WindowCloseListener closeWindowListener = new WindowCloseListener() {

		@Override
		public void onClose(WindowEvent e) {

			if (!onCloseCalled) {
				onCloseCalled = true;
				RpwDialog.this.onClose();
				afterOnClose();
			}
		}
	};


	public RpwDialog(Frame parent, String title) {

		super(parent, title);
		setModal(true);
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		addWindowListener(closeWindowListener);
	}


	public final void closeDialog() {

		dispose();

		if (!onCloseCalled) {
			onCloseCalled = true;
			onClose();
			afterOnClose();
		}
	}


	public final void prepareForDisplay() {

		pack();

		GuiUtils.centerWindow(this, getParent());
		addActions();

		getRootPane().registerKeyboardAction(closeListener, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
	}


	@Override
	public void setVisible(boolean b) {

		super.setVisible(b);

		onShown();
	}


	protected void onShown() {

	}


	protected abstract void addActions();


	/**
	 * To be used in enclosed types instead of weird stuff with
	 * DialogXXX.this.something()
	 * 
	 * @return this
	 */
	public final RpwDialog self() {

		return this;
	}


	private void afterOnClose() {

		if (closeHook != null) closeHook.run();
	}


	public abstract void onClose();
}
