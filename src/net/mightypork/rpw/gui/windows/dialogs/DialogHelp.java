package net.mightypork.rpw.gui.windows.dialogs;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.*;

import net.mightypork.rpw.App;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.widgets.HBox;
import net.mightypork.rpw.gui.widgets.VBox;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.help.HelpPage;
import net.mightypork.rpw.help.HelpStore;


public class DialogHelp extends RpwDialog {

	private JButton buttonOK;


	public DialogHelp() {

		super(App.getFrame(), "Quick Guide");

		createDialog();
	}


	@Override
	protected JComponent buildGui() {

		setResizable(true);
		setPreferredSize(new Dimension(900, 600));

		HBox hb;
		VBox vb = new VBox();
		vb.windowPadding();

		vb.heading("RPW Guide Book");

		vb.titsep("Help Topics");
		vb.gap();

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

		vb.gapl();

		//@formatter:off		
		hb = new HBox();

			hb.glue();
			
			hb.add(buttonOK = new JButton("Close", Icons.MENU_EXIT));
			
		vb.add(hb);
		//@formatter:on

		return vb;
	}


	@Override
	protected void addActions() {

		buttonOK.addActionListener(closeListener);
	}

}
