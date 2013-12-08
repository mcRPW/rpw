package net.mightypork.rpw.gui.windows.messages;


import java.awt.Dimension;

import javax.swing.*;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Paths;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.utils.GuiUtils;
import net.mightypork.rpw.utils.HtmlBuilder;

import org.jdesktop.swingx.JXTitledSeparator;


public class DialogUpdateNotify extends RpwDialog {

	private JButton buttonOK;
	private String version;
	private String textMd;


	public DialogUpdateNotify(String version, String textMd) {

		super(App.getFrame(), "Your RPW is outdated");
		
		this.version = version;
		this.textMd = textMd;
		
		createDialog();
	}
	
	@Override
	protected JComponent buildGui() {
		
		Box hb;
		Box vb = Box.createVerticalBox();
		vb.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		vb.add(GuiUtils.createDialogHeading("RPW update v" + version + " available!"));

		String text = HtmlBuilder.markdownToHtmlBase(textMd);

		JLabel content = new JLabel(text);
		content.setAlignmentX(0.5f);
		content.setMinimumSize(new Dimension(200, 100));
		content.setBorder(BorderFactory.createEmptyBorder(5, 30, 5, 30));
		vb.add(content);

		JXTitledSeparator jxt;

		vb.add(Box.createVerticalStrut(5));
		vb.add(jxt = new JXTitledSeparator("Get it!"));
		jxt.setHorizontalAlignment(SwingConstants.CENTER);

		vb.add(Box.createVerticalStrut(5));

		hb = Box.createHorizontalBox();

		hb.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		hb.add(Box.createHorizontalGlue());

		JButton btn;

		hb.add(btn = new JButton("PMC", Icons.MENU_PMC));
		btn.setActionCommand(Paths.URL_PLANETMINECRAFT_WEB);
		btn.addActionListener(GuiUtils.openUrlListener);
		btn.addActionListener(closeListener);

		hb.add(Box.createHorizontalStrut(5));

		hb.add(btn = new JButton("MC Forum", Icons.MENU_MCF));
		btn.setActionCommand(Paths.URL_MINECRAFTFORUM_WEB);
		btn.addActionListener(GuiUtils.openUrlListener);
		btn.addActionListener(closeListener);

		hb.add(Box.createHorizontalStrut(5));

		hb.add(btn = new JButton("Direct", Icons.MENU_DOWNLOAD));
		btn.setActionCommand(Paths.URL_LATEST_DOWNLOAD);
		btn.addActionListener(closeListener);
		btn.addActionListener(GuiUtils.openUrlListener);

		hb.add(Box.createHorizontalGlue());

		vb.add(hb);

		vb.add(Box.createVerticalStrut(5));
		vb.add(new JSeparator());
		vb.add(Box.createVerticalStrut(20));

		//@formatter:off		
		hb = Box.createHorizontalBox();

			hb.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
			hb.add(Box.createHorizontalGlue());

			hb.add(buttonOK = new JButton("Close", Icons.MENU_EXIT));
			
			hb.add(Box.createHorizontalGlue());	
			hb.setAlignmentX(0.5f);
		vb.add(hb);
		//@formatter:on

		return vb;
	}


	@Override
	protected void addActions() {

		buttonOK.addActionListener(closeListener);
	}

}
