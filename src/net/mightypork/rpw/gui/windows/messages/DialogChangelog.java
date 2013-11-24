package net.mightypork.rpw.gui.windows.messages;


import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Const;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.utils.FileUtils;
import net.mightypork.rpw.utils.GuiUtils;
import net.mightypork.rpw.utils.HtmlBuilder;


public class DialogChangelog extends RpwDialog {

	private JButton buttonOK;


	public DialogChangelog() {

		super(App.getFrame(), "What's new");

		Box hb;
		Box vb = Box.createVerticalBox();
		vb.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		vb.add(GuiUtils.createDialogHeading("What's new in v" + Const.VERSION));

		String text = FileUtils.streamToString(FileUtils.getResource("/changelog.md"));

		text = HtmlBuilder.markdownToHtmlBase(text);

		JLabel content = new JLabel(text);
		content.setAlignmentX(0.5f);
		content.setMinimumSize(new Dimension(200, 100));
		content.setBorder(BorderFactory.createEmptyBorder(5, 30, 10, 30));
		vb.add(content);

		//@formatter:off		
		hb = Box.createHorizontalBox();

			hb.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
			hb.add(Box.createHorizontalGlue());
				
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
	}


	@Override
	public void onClose() {

		// do nothing
	}
}
