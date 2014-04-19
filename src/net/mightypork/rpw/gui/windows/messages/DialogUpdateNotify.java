package net.mightypork.rpw.gui.windows.messages;


import java.awt.Dimension;

import javax.swing.*;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Paths;
import net.mightypork.rpw.gui.Gui;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.widgets.HBox;
import net.mightypork.rpw.gui.widgets.VBox;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.utils.HtmlBuilder;


public class DialogUpdateNotify extends RpwDialog {
	
	private JButton buttonOK;
	private final String version;
	private final String textMd;
	
	
	public DialogUpdateNotify(String version, String textMd) {
		super(App.getFrame(), "Your RPW is outdated");
		
		this.version = version;
		this.textMd = textMd;
		
		createDialog();
	}
	
	
	@Override
	protected JComponent buildGui()
	{
		HBox hb;
		final VBox vb = new VBox();
		vb.windowPadding();
		
		vb.heading("RPW update v" + version + " available!");
		
		final String text = HtmlBuilder.markdownToHtmlBase(textMd);
		
		final JLabel content = new JLabel(text);
		content.setAlignmentX(0.5f);
		content.setMinimumSize(new Dimension(200, 100));
		content.setBorder(BorderFactory.createEmptyBorder(Gui.GAP, Gui.GAPL * 2, Gui.GAP, Gui.GAPL * 2));
		vb.add(content);
		
		vb.gapl();
		vb.gap();
		vb.titsep("Get it!").setHorizontalAlignment(SwingConstants.CENTER);
		vb.gap();
		
		hb = new HBox();
		
		hb.glue();
		
		JButton btn;
		
		hb.add(btn = new JButton("PMC", Icons.MENU_PMC));
		btn.setActionCommand(Paths.URL_PLANETMINECRAFT_WEB);
		btn.addActionListener(Gui.openUrlListener);
		btn.addActionListener(closeListener);
		
		hb.gap();
		
		hb.add(btn = new JButton("MC Forum", Icons.MENU_MCF));
		btn.setActionCommand(Paths.URL_MINECRAFTFORUM_WEB);
		btn.addActionListener(Gui.openUrlListener);
		btn.addActionListener(closeListener);
		
		hb.gap();
		
		hb.add(btn = new JButton("Direct", Icons.MENU_DOWNLOAD));
		btn.setActionCommand(Paths.URL_LATEST_DOWNLOAD);
		btn.addActionListener(closeListener);
		btn.addActionListener(Gui.openUrlListener);
		
		hb.glue();
		
		vb.add(hb);
		
		vb.gap();
		vb.add(new JSeparator());
		vb.gapl();
		vb.gap();
		
		buttonOK = new JButton("Close", Icons.MENU_EXIT);
		vb.buttonRow(Gui.CENTER, buttonOK);
		
		return vb;
	}
	
	
	@Override
	protected void addActions()
	{
		buttonOK.addActionListener(closeListener);
		
		setEnterButton(buttonOK);
	}
	
}
