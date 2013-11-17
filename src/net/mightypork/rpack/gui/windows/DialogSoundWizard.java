package net.mightypork.rpack.gui.windows;


import java.awt.Dimension;

import javax.swing.*;

import net.mightypork.rpack.App;
import net.mightypork.rpack.Const;
import net.mightypork.rpack.gui.Icons;
import net.mightypork.rpack.gui.helpers.TextInputValidator;
import net.mightypork.rpack.gui.widgets.SimpleStringList;
import net.mightypork.rpack.library.Sources;
import net.mightypork.rpack.utils.GuiUtils;

import org.jdesktop.swingx.JXTextField;
import org.jdesktop.swingx.JXTitledSeparator;


public class DialogSoundWizard extends RpwDialog {

	private JButton buttonOK;
	private JButton buttonNewKey;
	private JButton buttonDeleteKey;
	private JXTextField fieldKey;
	private JComboBox<String> fieldCategory;


	public DialogSoundWizard() {

		super(App.getFrame(), "Sound Wizard");

		Box hbMain, hb, hb2, vbMain, vb, vb2;
		SimpleStringList simpleList;
		JLabel label;

		setResizable(true);
		vbMain = Box.createVerticalBox();

		vbMain.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		//@formatter:off
		
		hbMain = Box.createHorizontalBox();
			hbMain.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			
			// box with keys and editing current key
			hb = Box.createHorizontalBox();
			
				hb.setBorder(
						BorderFactory.createCompoundBorder(
								BorderFactory.createTitledBorder("Custom sounds"),
								BorderFactory.createEmptyBorder(5, 5, 5, 5)
						)
				);
				
				// box for the list of keys
				vb = Box.createVerticalBox();
				
					simpleList = new SimpleStringList(Sources.getResourcepackNames(), true);
					simpleList.setPreferredSize(new Dimension(250, 400));
					vb.add(simpleList);
				
					// box with buttons under the list
					hb2 = Box.createHorizontalBox();
		
						hb2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
						
						hb2.add(buttonNewKey = new JButton("New", Icons.MENU_NEW));
						hb2.add(Box.createHorizontalStrut(5));
						hb2.add(buttonDeleteKey = new JButton("Delete", Icons.MENU_DELETE));
						
						hb2.add(Box.createHorizontalGlue());
						
					vb.add(hb2);
				
				hb.add(vb);
				
				hb.add(Box.createHorizontalStrut(10));
				hb.add(new JSeparator(SwingConstants.VERTICAL));
				hb.add(Box.createHorizontalStrut(10));
				
				// box for editing a key
				vb = Box.createVerticalBox();
				
					// box for key edit field
					hb2 = Box.createHorizontalBox();
					hb2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
						
						hb2.add(label = new JLabel("Name:"));
						GuiUtils.setMinPrefSize(label, 70, 25);
						label.setHorizontalAlignment(SwingConstants.RIGHT);
						
						hb2.add(Box.createHorizontalStrut(5));
						
						hb2.add(fieldKey = new JXTextField());
						fieldKey.setBorder(BorderFactory.createCompoundBorder(
								fieldKey.getBorder(),
								BorderFactory.createEmptyBorder(3,3,3,3)
						));
						fieldKey.addKeyListener(TextInputValidator.identifiers());
						GuiUtils.setMinPrefSize(fieldKey, 140, 25);
						fieldKey.setMaximumSize(new Dimension(1000, 25));
						
					vb.add(hb2);
					
					
					// box for category field
					hb2 = Box.createHorizontalBox();
					hb2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
						
						hb2.add(label = new JLabel("Category:"));
						GuiUtils.setMinPrefSize(label, 70, 25);
						label.setHorizontalAlignment(SwingConstants.RIGHT);
						
						hb2.add(Box.createHorizontalStrut(5));
						
						hb2.add(fieldCategory = new JComboBox<String>(Const.SOUND_CATEGORIES));
						fieldCategory.setEditable(true);
						GuiUtils.setMinPrefSize(fieldCategory, 140, 25);
						
					vb.add(hb2);
					
					vb.add(Box.createVerticalStrut(10));
					vb.add(new JXTitledSeparator("Included sounds (random)"));
					vb.add(Box.createVerticalStrut(10));
					
					simpleList = new SimpleStringList(Sources.getResourcepackNames(), true);
					simpleList.setPreferredSize(new Dimension(250, 300));
					vb.add(simpleList);
					
					vb.add(Box.createVerticalStrut(50));
				
				hb.add(vb);
				
				
				
			hbMain.add(hb);
			
			
			
		vbMain.add(hbMain);
		
		// buttons row		
		hb = Box.createHorizontalBox();

			hb.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
			hb.add(Box.createHorizontalGlue());
			
			hb.add(buttonOK = new JButton("Close", Icons.MENU_YES));
		vbMain.add(hb);
		
		
		//@formatter:on

		getContentPane().add(vbMain);

		prepareForDisplay();
	}


	@Override
	protected void addActions() {

		buttonOK.addActionListener(closeListener);
	}


	@Override
	public void onClose() {

		System.exit(0);//TMP
		// do nothing
	}
}
