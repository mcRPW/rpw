package net.mightypork.rpw.gui.windows.messages;


import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;

import net.mightypork.rpw.App;
import net.mightypork.rpw.gui.Gui;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.widgets.HBox;
import net.mightypork.rpw.gui.widgets.VBox;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.help.VersionUtils;


public class DialogChangelog extends RpwDialog {

	private JButton buttonOK;


	public DialogChangelog() {

		super(App.getFrame(), "What's new");

		createDialog();
	}


	@Override
	protected JComponent buildGui() {

		HBox hb;
		VBox vbox = new VBox();
		
		vbox.windowPadding();

		vbox.heading("What's new in RPW");

		String text = VersionUtils.buildChangelogHtml();

		JLabel content = new JLabel(text);
		content.setAlignmentX(0.5f);
		content.setMinimumSize(new Dimension(200, 100));
		content.setBorder(BorderFactory.createEmptyBorder(Gui.GAP, Gui.GAPL*2, Gui.GAP, Gui.GAPL*2));
		vbox.add(content);

		//@formatter:off		
		hb = new HBox();

			hb.glue();
				
			hb.add(buttonOK = new JButton("Close", Icons.MENU_YES));
			
			hb.glue();
			hb.setAlignmentX(0.5f);
		vbox.add(hb);
		//@formatter:on

		return vbox;
	}


	@Override
	protected void addActions() {

		buttonOK.addActionListener(closeListener);

		setEnterButton(buttonOK);
	}

}
