package net.mightypork.rpw.gui.windows;


import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Const;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.utils.DesktopApi;
import net.mightypork.rpw.utils.Log;


public class DialogAbout extends RpwDialog {

	private JButton buttonOK;
	private JButton buttonTwitter;


	public DialogAbout() {

		super(App.getFrame(), "About");

		Box hb;
		Box vb = Box.createVerticalBox();
		vb.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		JLabel title = new JLabel(Const.APP_NAME + " v." + Const.VERSION);
		title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
		title.setForeground(new Color(0x045A80));
		title.setBorder(BorderFactory.createEmptyBorder(10, 5, 0, 5));
		title.setAlignmentX(0.5f);
		vb.add(title);

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

		getContentPane().add(vb);

		prepareForDisplay();
	}


	@Override
	protected void addActions() {

		buttonOK.addActionListener(closeListener);


		buttonTwitter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				try {
					if (!DesktopApi.browse(new URL("https://twitter.com/MightyPork").toURI())) {
						//@formatter:off
						Alerts.error(
								App.getFrame(),
								"Could not open the URL, your\n" +
								"platform is not supported.\n" +
								"\n" +
								"Check log for details."
						);
						//@formatter:on
					}
				} catch (Exception err) {
					Log.e(err);
					//@formatter:off
					Alerts.error(
							App.getFrame(),
							"Malformed URL error.\n" +
							"\n" +
							"Please, check log and report it."
					);
					//@formatter:on
				}
			}
		});
	}


	@Override
	public void onClose() {

		// do nothing
	}
}
