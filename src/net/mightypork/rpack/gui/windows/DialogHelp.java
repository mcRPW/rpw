package net.mightypork.rpack.gui.windows;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.*;

import net.mightypork.rpack.App;
import net.mightypork.rpack.gui.Icons;
import net.mightypork.rpack.help.HelpPage;
import net.mightypork.rpack.help.HelpStore;

import org.jdesktop.swingx.JXTitledSeparator;


public class DialogHelp extends RpwDialog {

	private JButton buttonOK;


	public DialogHelp() {

		super(App.getFrame(), "Quick Guide");

		setResizable(true);
		setPreferredSize(new Dimension(800, 600));

		Box hb;
		Box vb = Box.createVerticalBox();
		vb.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		//@formatter:off
		JLabel title = new JLabel("Quick Guide");
			title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
			title.setForeground(new Color(0x222222));
			title.setBorder(BorderFactory.createEmptyBorder(10, 5, 0, 5));
			title.setAlignmentX(0.5f);
		vb.add(title);
		//@formatter:on

		vb.add(Box.createVerticalStrut(10));
		vb.add(new JXTitledSeparator("Help Topics"));
		vb.add(Box.createVerticalStrut(10));

		JTabbedPane tabPane = new JTabbedPane(SwingConstants.LEFT);
		tabPane.setAlignmentX(0.5f);

		// build help pages

		for (HelpPage hp : HelpStore.getPages()) {

			// build one page
			final JScrollPane panel = new JScrollPane();
			panel.getVerticalScrollBar().setUnitIncrement(16);
			panel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			panel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			panel.setAlignmentY(0);

			JTextPane page = new JTextPane();//new JLabel();
			page.setBackground(Color.WHITE);
			page.setOpaque(true);
			page.setAlignmentY(0);
			page.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			page.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
			page.setContentType("text/html"); // let the text pane know this is what you want
			page.setEditable(false); // as before

			page.setText(hp.getContent());
			panel.setViewportView(page);

			tabPane.add(hp.getName(), panel);

			// move the scrollbar to top
			// http://stackoverflow.com/questions/1166072/setting-scroll-bar-on-a-jscrollpane
			javax.swing.SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {

					panel.getVerticalScrollBar().setValue(0);
				}
			});

		}

		vb.add(tabPane);

		vb.add(Box.createVerticalStrut(10));
		vb.add(new JSeparator(SwingConstants.HORIZONTAL));
		vb.add(Box.createVerticalStrut(10));

		//@formatter:off		
		hb = Box.createHorizontalBox();

			hb.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
			hb.add(Box.createHorizontalGlue());
			
			hb.add(buttonOK = new JButton("Close", Icons.MENU_EXIT));
			
			//hb.add(Box.createHorizontalGlue());	
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
