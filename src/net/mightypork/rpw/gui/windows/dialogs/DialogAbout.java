package net.mightypork.rpw.gui.windows.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Const;
import net.mightypork.rpw.gui.Gui;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.widgets.VBox;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.utils.files.DesktopApi;
import net.mightypork.rpw.utils.logging.Log;


public class DialogAbout extends RpwDialog
{

	private JButton buttonOK;
	private JButton buttonTwitter;


	public DialogAbout() {
		super(App.getFrame(), "About");

		createDialog();
	}


	@Override
	protected JComponent buildGui()
	{
		final VBox vb = new VBox();
		vb.windowPadding();

		vb.heading(Const.APP_NAME + " v" + Const.VERSION);

		final JLabel image = new JLabel(Icons.ABOUT);
		image.setAlignmentX(0.5f);
		vb.add(image);
		vb.gapl();

		buttonTwitter = new JButton("@MightyPork", Icons.MENU_TWITTER);
		buttonOK = new JButton("Close", Icons.MENU_YES);

		vb.buttonRow(Gui.CENTER, buttonTwitter, buttonOK);
		buttonOK.requestFocusInWindow();

		return vb;
	}


	@Override
	protected void addActions()
	{
		buttonOK.addActionListener(closeListener);

		buttonTwitter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				try {
					DesktopApi.browse(new URL("https://twitter.com/MightyPork")
							.toURI());
				} catch (final Exception err) {
					Log.e(err);
				}
			}
		});

		setEnterButton(buttonOK);
	}
}
