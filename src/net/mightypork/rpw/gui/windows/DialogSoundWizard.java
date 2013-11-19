package net.mightypork.rpw.gui.windows;


import java.awt.Dimension;
import java.util.Arrays;

import javax.swing.*;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Const;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.helpers.TextInputValidator;
import net.mightypork.rpw.gui.widgets.SimpleStringList;
import net.mightypork.rpw.utils.GuiUtils;

import org.jdesktop.swingx.JXTextField;
import org.jdesktop.swingx.JXTitledSeparator;
import org.jdesktop.swingx.JXTree;


public class DialogSoundWizard extends RpwDialog {

	private JButton buttonOK;
	private JButton buttonNewKey;
	private JButton buttonDeleteKey;
	private JXTextField fieldKey;
	private JComboBox fieldCategory;
	private SimpleStringList keyList;
	private SimpleStringList fileList;
	private JXTree fileTree;


	public DialogSoundWizard() {

		super(App.getFrame(), "Sound Wizard");

		Box hbMain, hb, hb2, vbMain, vb, vb2;
		JLabel label;

		vbMain = Box.createVerticalBox();

		vbMain.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		//@formatter:off
		
		hbMain = Box.createHorizontalBox();
			hbMain.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			
			// box with keys and editing current key
			hb = Box.createHorizontalBox();
			
			hb.setBorder(
					BorderFactory.createCompoundBorder(
							BorderFactory.createTitledBorder("Sound Entries"),
							BorderFactory.createEmptyBorder(5, 5, 5, 5)
					)
			);
				
				// box for the list of keys
				vb = Box.createVerticalBox();
				
					keyList = new SimpleStringList(Arrays.asList("cool.bang","cool.bell","stupid_name.something.hello"), true);
					keyList.setPreferredSize(new Dimension(300, 400));
					vb.add(keyList);
				
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
						GuiUtils.setMinPrefSize(fieldKey, 200, 25);
						fieldKey.setText("cool.bang");
						
					vb.add(hb2);
					
					
					// box for category field
					hb2 = Box.createHorizontalBox();
					hb2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
						
						hb2.add(label = new JLabel("Category:"));
						GuiUtils.setMinPrefSize(label, 70, 25);
						label.setHorizontalAlignment(SwingConstants.RIGHT);
						
						hb2.add(Box.createHorizontalStrut(5));
						
						hb2.add(fieldCategory = new JComboBox(Const.SOUND_CATEGORIES));
						fieldCategory.setEditable(true);
						GuiUtils.setMinPrefSize(fieldCategory, 200, 25);
						
					vb.add(hb2);
					
					vb.add(Box.createVerticalStrut(10));
					vb.add(new JXTitledSeparator("Audio files"));
					vb.add(Box.createVerticalStrut(10));
					
					fileList = new SimpleStringList(Arrays.asList("cool_sounds/boing","cool_sounds/booom"), true);
					fileList.setPreferredSize(new Dimension(300, 300));
					vb.add(fileList);

					vb.add(Box.createVerticalStrut(5));
					
					vb.add(label = new JLabel("Add by dragging from the right panel."));//Box.createVerticalStrut(50));
					label.setHorizontalAlignment(SwingConstants.LEFT);
					label.setAlignmentX(0.5f);
					
					vb.add(Box.createVerticalStrut(22));
					
				hb.add(vb);
				
				
				
			hbMain.add(hb);
			
			
			hb = Box.createHorizontalBox();
			
			hb.setBorder(
					BorderFactory.createCompoundBorder(
							BorderFactory.createTitledBorder("Audio Files"),
							BorderFactory.createEmptyBorder(5, 5, 5, 5)
					)
			);
			
			JScrollPane scrollpane = new JScrollPane(fileTree = new JXTree());			
			scrollpane.setPreferredSize(new Dimension(300, 400));

			hb.add(scrollpane);
			
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


		pupulateLists();

		prepareForDisplay();
	}


	private void pupulateLists() {

		// TODO
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
