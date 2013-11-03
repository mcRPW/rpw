package net.mightypork.rpack.gui.windows;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.filechooser.FileFilter;

import net.mightypork.rpack.App;
import net.mightypork.rpack.Config;
import net.mightypork.rpack.gui.Icons;

import org.jdesktop.swingx.JXTitledSeparator;


public class DialogConfigureEditors extends RpwDialog {

	private JFileChooser fc;

	private JButton btnOK;
	private JButton btnCancel;
	private JButton btnDefaults;

	private JCheckBox ckI;
	private Box boxI;
	private JLabel labelExI;
	private JLabel labelArI;
	private JTextField fieldCommandI;
	private JButton btnBrowseI;
	private JTextField fieldArgsI;

	private JCheckBox ckT;
	private Box boxT;
	private JLabel labelExT;
	private JLabel labelArT;
	private JTextField fieldCommandT;
	private JButton btnBrowseT;
	private JTextField fieldArgsT;

	private JCheckBox ckA;
	private Box boxA;
	private JLabel labelExA;
	private JLabel labelArA;
	private JTextField fieldCommandA;
	private JButton btnBrowseA;
	private JTextField fieldArgsA;

	private JCheckBox ckInternalMeta;
	private JCheckBox ckInternalText;


	public DialogConfigureEditors() {

		super(App.getFrame(), "Configure Editors");

		Box box, box2;
		Box vb = Box.createVerticalBox();
		vb.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		Dimension fieldSize = new Dimension(300, 20);
		Dimension argsSize = new Dimension(100, 20);
		Border fieldBorder = BorderFactory.createEmptyBorder(3, 3, 3, 3);
		JXTitledSeparator sep;

		Color mainSepColor = new Color(200, 0, 200);

		//@formatter:off

			vb.add(sep = new JXTitledSeparator("External Editor Commands"));
				sep.setForeground(mainSepColor);
			vb.add(Box.createVerticalStrut(10));
		
			vb.add(sep = new JXTitledSeparator("Image Editor"));
				sep.setForeground(Color.GRAY);
				sep.add(ckI = new JCheckBox("Use custom command"));	
			vb.add(Box.createVerticalStrut(10));
	
			boxI = Box.createHorizontalBox();		
				boxI.add(labelExI = new JLabel("Executable:"));
					labelExI.setHorizontalAlignment(SwingConstants.RIGHT);		
				boxI.add(Box.createHorizontalStrut(5));		
				boxI.add(fieldCommandI = new JTextField());
					fieldCommandI.setPreferredSize(fieldSize);
					fieldCommandI.setBorder(new CompoundBorder(fieldCommandI.getBorder(), fieldBorder));		
				boxI.add(Box.createHorizontalStrut(5));		
				boxI.add(btnBrowseI = new JButton(Icons.MENU_OPEN));		
				boxI.add(Box.createHorizontalStrut(8));		
				boxI.add(labelArI = new JLabel("Args:"));
					labelArI.setHorizontalAlignment(SwingConstants.RIGHT);		
				boxI.add(Box.createHorizontalStrut(5));		
				boxI.add(fieldArgsI = new JTextField());
					fieldArgsI.setPreferredSize(argsSize);
					fieldArgsI.setBorder(new CompoundBorder(fieldArgsI.getBorder(), fieldBorder));		
			vb.add(boxI);
			vb.add(Box.createVerticalStrut(35));
	
	
			vb.add(sep = new JXTitledSeparator("Text Editor"));
				sep.setForeground(Color.GRAY);
				sep.add(ckT = new JCheckBox("Use custom command"));	
			vb.add(Box.createVerticalStrut(10));
	
			boxT = Box.createHorizontalBox();		
				boxT.add(labelExT = new JLabel("Executable:"));
					labelExT.setHorizontalAlignment(SwingConstants.RIGHT);		
				boxT.add(Box.createHorizontalStrut(5));		
				boxT.add(fieldCommandT = new JTextField());
					fieldCommandT.setPreferredSize(fieldSize);
					fieldCommandT.setBorder(new CompoundBorder(fieldCommandT.getBorder(), fieldBorder));		
				boxT.add(Box.createHorizontalStrut(5));		
				boxT.add(btnBrowseT = new JButton(Icons.MENU_OPEN));		
				boxT.add(Box.createHorizontalStrut(8));		
				boxT.add(labelArT = new JLabel("Args:"));
					labelArT.setHorizontalAlignment(SwingConstants.RIGHT);		
				boxT.add(Box.createHorizontalStrut(5));		
				boxT.add(fieldArgsT = new JTextField());
					fieldArgsT.setPreferredSize(argsSize);
					fieldArgsT.setBorder(new CompoundBorder(fieldArgsT.getBorder(), fieldBorder));	
			vb.add(boxT);
			vb.add(Box.createVerticalStrut(35));
	
	
			vb.add(sep = new JXTitledSeparator("Sound Editor"));
				sep.setForeground(Color.GRAY);
				sep.add(ckA = new JCheckBox("Use custom command"));
			vb.add(Box.createVerticalStrut(10));
	
			boxA = Box.createHorizontalBox();
				boxA.add(labelExA = new JLabel("Executable:"));
					labelExA.setHorizontalAlignment(SwingConstants.RIGHT);	
				boxA.add(Box.createHorizontalStrut(5));		
				boxA.add(fieldCommandA = new JTextField());
					fieldCommandA.setPreferredSize(fieldSize);
					fieldCommandA.setBorder(new CompoundBorder(fieldCommandA.getBorder(), fieldBorder));		
				boxA.add(Box.createHorizontalStrut(5));		
				boxA.add(btnBrowseA = new JButton(Icons.MENU_OPEN));		
				boxA.add(Box.createHorizontalStrut(8));		
				boxA.add(labelArA = new JLabel("Args:"));
					labelArA.setHorizontalAlignment(SwingConstants.RIGHT);		
				boxA.add(Box.createHorizontalStrut(5));		
				boxA.add(fieldArgsA = new JTextField());
					fieldArgsA.setPreferredSize(argsSize);
					fieldArgsA.setBorder(new CompoundBorder(fieldArgsA.getBorder(), fieldBorder));		
			vb.add(boxA);
			vb.add(Box.createVerticalStrut(35));
			
			
			vb.add(sep = new JXTitledSeparator("Built-in editors"));
				sep.setForeground(mainSepColor);
			vb.add(Box.createVerticalStrut(10));
			
			box = Box.createHorizontalBox();
				box2 = Box.createVerticalBox();
					box2.add(ckInternalMeta = new JCheckBox("Use built-in McMeta editor"));
					box2.add(ckInternalText = new JCheckBox("Use built-in Text editor"));
					
					ckInternalMeta.setAlignmentX(0);
					ckInternalText.setAlignmentX(0);
				box.add(box2);
				box.add(Box.createHorizontalGlue());		
			vb.add(box);
			
			vb.add(Box.createVerticalStrut(16));
		
			box = Box.createHorizontalBox();
				box.add(Box.createHorizontalGlue());
				box.add(btnDefaults = new JButton("Defaults", Icons.MENU_DELETE));
				box.add(Box.createHorizontalStrut(10));
				box.add(btnOK = new JButton("OK", Icons.MENU_YES));
				box.add(Box.createHorizontalStrut(5));
				box.add(btnCancel = new JButton("Cancel", Icons.MENU_CANCEL));			
			vb.add(box);

		getContentPane().add(vb);
		
		//@formatter:on

		prepareForDisplay();

		initFileChooser();

		initFields();
	}


	@Override
	protected void addActions() {

		btnOK.addActionListener(saveListener);
		btnCancel.addActionListener(closeListener);

		ckA.addActionListener(ckListener);
		ckI.addActionListener(ckListener);
		ckT.addActionListener(ckListener);

		ckInternalMeta.addActionListener(ckListener);
		ckInternalText.addActionListener(ckListener);


		btnBrowseA.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				fc.setCurrentDirectory(new File(fieldCommandA.getText()));
				int res = fc.showDialog(self(), "Select");

				if (res == JFileChooser.APPROVE_OPTION) {
					fieldCommandA.setText(fc.getSelectedFile().getPath());
				}
			}
		});


		btnBrowseI.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				fc.setCurrentDirectory(new File(fieldCommandI.getText()));
				int res = fc.showDialog(self(), "Select");

				if (res == JFileChooser.APPROVE_OPTION) {
					fieldCommandI.setText(fc.getSelectedFile().getPath());
				}
			}
		});


		btnBrowseT.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				fc.setCurrentDirectory(new File(fieldCommandT.getText()));
				int res = fc.showDialog(self(), "Select");

				if (res == JFileChooser.APPROVE_OPTION) {
					fieldCommandT.setText(fc.getSelectedFile().getPath());
				}
			}
		});


		btnDefaults.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				initFieldsDef();
			}
		});
	}


	private void initFields() {

		ckInternalMeta.setSelected(Config.USE_INTERNAL_META_EDITOR);
		ckInternalText.setSelected(Config.USE_INTERNAL_TEXT_EDITOR);

		ckI.setSelected(Config.USE_IMAGE_EDITOR);
		ckT.setSelected(Config.USE_TEXT_EDITOR);
		ckA.setSelected(Config.USE_AUDIO_EDITOR);

		fieldCommandA.setText(Config.AUDIO_EDITOR);
		fieldArgsA.setText(Config.AUDIO_EDITOR_ARGS);

		fieldCommandI.setText(Config.IMAGE_EDITOR);
		fieldArgsI.setText(Config.IMAGE_EDITOR_ARGS);

		fieldCommandT.setText(Config.TEXT_EDITOR);
		fieldArgsT.setText(Config.TEXT_EDITOR_ARGS);

		ckListener.actionPerformed(null);
	}


	private void initFieldsDef() {

		ckInternalMeta.setSelected(Config.def_USE_INTERNAL_META_EDITOR);
		ckInternalText.setSelected(Config.def_USE_INTERNAL_TEXT_EDITOR);

		ckI.setSelected(Config.def_USE_IMAGE_EDITOR);
		ckT.setSelected(Config.def_USE_TEXT_EDITOR);
		ckA.setSelected(Config.def_USE_AUDIO_EDITOR);

		fieldCommandA.setText(Config.def_AUDIO_EDITOR);
		fieldArgsA.setText(Config.def_AUDIO_EDITOR_ARGS);

		fieldCommandI.setText(Config.def_IMAGE_EDITOR);
		fieldArgsI.setText(Config.def_IMAGE_EDITOR_ARGS);

		fieldCommandT.setText(Config.def_TEXT_EDITOR);
		fieldArgsT.setText(Config.def_TEXT_EDITOR_ARGS);

		ckListener.actionPerformed(null);
	}


	private void initFileChooser() {

		fc = new JFileChooser();
		fc.setAcceptAllFileFilterUsed(false);
		fc.setDialogTitle("Select executable");
		fc.setFileFilter(new FileFilter() {

			@Override
			public String getDescription() {

				return "All files";
			}


			@Override
			public boolean accept(File f) {

				return true;
			}
		});

		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(false);
		fc.setFileHidingEnabled(!Config.SHOW_HIDDEN_FILES);
	}


	@Override
	public void onClose() {

		// do nothing
	}


	private ActionListener ckListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			boolean internalText = ckInternalText.isSelected();
			boolean internalMeta = ckInternalMeta.isSelected();

			boolean cmdI = ckI.isSelected();
			boolean cmdT = ckT.isSelected() && (!internalMeta || !internalText);
			boolean cmdA = ckA.isSelected();

			ckT.setEnabled(!internalMeta || !internalText);

			boxI.setEnabled(cmdI);
			boxT.setEnabled(cmdT);
			boxA.setEnabled(cmdA);

			fieldCommandI.setEnabled(cmdI);
			fieldCommandT.setEnabled(cmdT);
			fieldCommandA.setEnabled(cmdA);

			fieldArgsI.setEnabled(cmdI);
			fieldArgsT.setEnabled(cmdT);
			fieldArgsA.setEnabled(cmdA);

			btnBrowseI.setEnabled(cmdI);
			btnBrowseT.setEnabled(cmdT);
			btnBrowseA.setEnabled(cmdA);

			labelExI.setEnabled(cmdI);
			labelExT.setEnabled(cmdT);
			labelExA.setEnabled(cmdA);

			labelArI.setEnabled(cmdI);
			labelArT.setEnabled(cmdT);
			labelArA.setEnabled(cmdA);
		}
	};

	private ActionListener saveListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			Config.USE_AUDIO_EDITOR = ckA.isSelected();
			Config.AUDIO_EDITOR = fieldCommandA.getText();
			Config.AUDIO_EDITOR_ARGS = fieldArgsA.getText();

			Config.USE_TEXT_EDITOR = ckT.isSelected();
			Config.TEXT_EDITOR = fieldCommandT.getText();
			Config.TEXT_EDITOR_ARGS = fieldArgsT.getText();

			Config.USE_IMAGE_EDITOR = ckI.isSelected();
			Config.IMAGE_EDITOR = fieldCommandI.getText();
			Config.IMAGE_EDITOR_ARGS = fieldArgsI.getText();

			Config.USE_INTERNAL_META_EDITOR = ckInternalMeta.isSelected();
			Config.USE_INTERNAL_TEXT_EDITOR = ckInternalText.isSelected();

			Config.save();

			closeDialog();
		}
	};

}
