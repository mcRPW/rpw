package net.mightypork.rpw.gui.windows.dialogs;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Const;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.utils.DesktopApi;
import net.mightypork.rpw.utils.GuiUtils;
import net.mightypork.rpw.utils.logging.Log;


public class DialogAbout extends RpwDialog {

	private JButton buttonOK;
	private JButton buttonTwitter;


	public DialogAbout() {

		super(App.getFrame(), "About");

		createDialog();
	}
	
	
	@Override
	protected JComponent buildGui() {
		
		Box hb;
		Box vb = Box.createVerticalBox();
		vb.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		vb.add(GuiUtils.createDialogHeading(Const.APP_NAME + " v" + Const.VERSION));

		JLabel image = new JLabel(Icons.ABOUT);
		image.setAlignmentX(0.5f);
		vb.add(image);

		//@formatter:off		
		hb = Box.createHorizontalBox();

			hb.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
			hb.add(Box.createHorizontalGlue());
	
			hb.add(buttonTwitter = new JButton("@MightyPork", Icons.MENU_TWITTER));
			
			hb.add(Box.createHorizontalStrut(5));
			
			hb.add(buttonOK = new JButton("Close", Icons.MENU_YES));
			
			hb.add(Box.createHorizontalGlue());	
			hb.setAlignmentX(0.5f);
		vb.add(hb);
		//@formatter:on

		return vb;
	}


	@Override
	protected void addActions() {

		buttonOK.addActionListener(closeListener);

		buttonTwitter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				try {
					DesktopApi.browse(new URL("https://twitter.com/MightyPork").toURI());
				} catch (Exception err) {
					Log.e(err);
				}
			}
		});
	}
}
