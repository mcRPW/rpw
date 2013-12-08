package net.mightypork.rpw.gui.windows.dialogs;


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.TreePath;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Const;
import net.mightypork.rpw.Paths;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.helpers.ClickListener;
import net.mightypork.rpw.gui.helpers.TextInputValidator;
import net.mightypork.rpw.gui.widgets.SimpleStringList;
import net.mightypork.rpw.gui.widgets.SoundFileTreeDisplay;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.struct.SoundEntry;
import net.mightypork.rpw.struct.SoundEntryMap;
import net.mightypork.rpw.tree.assets.EAsset;
import net.mightypork.rpw.tree.filesystem.AbstractFsTreeNode;
import net.mightypork.rpw.tree.filesystem.DirectoryFsTreeNode;
import net.mightypork.rpw.tree.filesystem.FileFsTreeNode;
import net.mightypork.rpw.utils.AlphanumComparator;
import net.mightypork.rpw.utils.GuiUtils;
import net.mightypork.rpw.utils.OsUtils;
import net.mightypork.rpw.utils.Utils;

import org.jdesktop.swingx.JXTextField;
import org.jdesktop.swingx.JXTitledSeparator;


public class DialogSoundWizard extends RpwDialog {

	private JButton buttonOK;
	private JButton buttonNewKey;
	private JButton buttonDeleteKey;
	private JXTextField fieldKey;
	private JComboBox fieldCategory;
	private SimpleStringList keyList;
	private SimpleStringList fileList;
	private SoundFileTreeDisplay treeDisplay;
	private SoundEntryMap soundMap;
	private JButton buttonDiscard;
	private JButton buttonSave;
	private ArrayList<Component> middlePanelComponents;

	private String editedKey = null;
	private boolean flagEntryChanged = false;
	protected boolean editStateValid;
	private boolean suppressEditCheck = false;

	private static final DataFlavor FLAVOR_FSTREE_FILE = new DataFlavor(FileFsTreeNode.class, "FSTREE_FILE_NODE");


	public DialogSoundWizard() {

		super(App.getFrame(), "Sound Wizard");

		createDialog();
	}


	@Override
	protected JComponent buildGui() {

		Box hbMain, hb, vbMain;

		vbMain = Box.createVerticalBox();

		vbMain.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		vbMain.add(GuiUtils.createDialogHeading("Sound Wizard"));

		hbMain = Box.createHorizontalBox();
		hbMain.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// box with keys and editing current key
		hb = Box.createHorizontalBox();

		//@formatter:off
		hb.setBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createTitledBorder("Sound Entries"),
						BorderFactory.createEmptyBorder(5, 10, 5, 5)
				)
		);
		//@formatter:on

		hb.add(createLeftPanel());

		hb.add(Box.createHorizontalStrut(10));
		hb.add(new JSeparator(SwingConstants.VERTICAL));
		hb.add(Box.createHorizontalStrut(5));

		hb.add(createMiddlePanel());

		hbMain.add(hb);

		hbMain.add(createRightPanel());
		vbMain.add(hbMain);


		// buttons row		
		hb = Box.createHorizontalBox();
		hb.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		hb.add(Box.createHorizontalGlue());
		hb.add(buttonOK = new JButton("Close", Icons.MENU_EXIT));
		vbMain.add(hb);

		fileList.setMultiSelect(true);
		fileList.getList().setDragEnabled(true);
		treeDisplay.tree.setDragEnabled(true);

		return vbMain;
	}


	@Override
	protected void initGui() {

		fileList.setTransferHandler(new FileListTransferHandler());
		treeDisplay.tree.setTransferHandler(new FileTreeTransferHandler());

		initLists();
	}


	private Component createRightPanel() {

		Box vb = Box.createVerticalBox();

		//@formatter:off
		vb.setBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createTitledBorder("All Audio Files"),
						BorderFactory.createEmptyBorder(5, 10, 5, 10)
				)
		);
		//@formatter:on

		treeDisplay = new SoundFileTreeDisplay(null, this);

		JComponent c = treeDisplay.getComponent();
		c.setPreferredSize(new Dimension(300, 396));

		vb.add(c);

		vb.add(Box.createVerticalStrut(5));

		JLabel label;
		vb.add(label = new JLabel("<html><center>Drag to <i>Selected Files</i> to select.<br>Right-click for options.</center></html>"));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setAlignmentX(0.5f);

		vb.add(Box.createVerticalGlue());

		return vb;
	}


	private Component createMiddlePanel() {

		middlePanelComponents = new ArrayList<Component>();

		JLabel label;

		// box for editing a key

		Box vb = Box.createVerticalBox();
		vb.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

		// box for key edit field
		Box hb = Box.createHorizontalBox();

		//@formatter:off
		
		hb.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
			
			hb.add(label = new JLabel("Name:"));
			GuiUtils.setMinPrefSize(label, 70, 25);
			label.setHorizontalAlignment(SwingConstants.RIGHT);
			middlePanelComponents.add(label);
			
			hb.add(Box.createHorizontalStrut(5));
			
			hb.add(fieldKey = new JXTextField());
			fieldKey.setBorder(BorderFactory.createCompoundBorder(
					fieldKey.getBorder(),
					BorderFactory.createEmptyBorder(3,3,3,3)
			));
			fieldKey.addKeyListener(TextInputValidator.identifiers());
			
			fieldKey.setDragEnabled(false);						
			fieldKey.setTransferHandler(new TransferHandler() {}); // disable dnd
			middlePanelComponents.add(fieldKey);
			
			GuiUtils.setMinPrefSize(fieldKey, 200, 25);
			
		vb.add(hb);
		
		
		// box for category field
		hb = Box.createHorizontalBox();
		hb.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
			
			hb.add(label = new JLabel("Category:"));
			GuiUtils.setMinPrefSize(label, 70, 25);
			label.setHorizontalAlignment(SwingConstants.RIGHT);
			middlePanelComponents.add(label);
			
			hb.add(Box.createHorizontalStrut(5));
			
			hb.add(fieldCategory = new JComboBox(Const.SOUND_CATEGORIES));
			//fieldCategory.setEditable(true); // No, causes error in MC
			middlePanelComponents.add(fieldCategory);
			
			GuiUtils.setMinPrefSize(fieldCategory, 200, 25);
			
		vb.add(hb);
		
		vb.add(Box.createVerticalStrut(10));
		JXTitledSeparator sep;
		vb.add(sep = new JXTitledSeparator("Selected files"));
		middlePanelComponents.add(sep);
		vb.add(Box.createVerticalStrut(10));
		
		fileList = new SimpleStringList();
		fileList.setPreferredSize(new Dimension(300, 300));
							
		vb.add(fileList);
		middlePanelComponents.add(fileList);		

		vb.add(Box.createVerticalStrut(5));
		
		// box with buttons under the list
		hb = Box.createHorizontalBox();
		hb.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		hb.add(Box.createHorizontalGlue());
		hb.add(buttonSave = new JButton("Save", Icons.MENU_SAVE));
		hb.add(Box.createHorizontalStrut(5));
		hb.add(buttonDiscard = new JButton("Discard", Icons.MENU_CANCEL));
		
		middlePanelComponents.add(buttonSave);	
		middlePanelComponents.add(buttonDiscard);	
		
		vb.add(hb);
		
		//@formatter:on

		return vb;
	}


	private void enableMiddlePanel(boolean yes) {

		suppressEditCheck = true;

		for (Component c : middlePanelComponents) {
			c.setEnabled(yes);
			if (c instanceof JXTitledSeparator) c.setForeground(yes ? Color.BLACK : Color.GRAY);
			if (c instanceof JTextField) ((JTextField) c).setText("");
			if (c instanceof JComboBox) ((JComboBox) c).setSelectedItem("");
			if (c instanceof SimpleStringList) ((SimpleStringList) c).empty();
		}

		suppressEditCheck = false;

	}


	private Component createLeftPanel() {

		// box for the list of keys
		Box vb = Box.createVerticalBox();

		keyList = new SimpleStringList();
		keyList.setPreferredSize(new Dimension(300, 400));

		vb.add(keyList);
		vb.add(Box.createVerticalStrut(5));

		// box with buttons under the list
		Box hb = Box.createHorizontalBox();
		hb.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		hb.add(buttonNewKey = new JButton("New", Icons.MENU_NEW));
		hb.add(Box.createHorizontalStrut(5));
		hb.add(buttonDeleteKey = new JButton("Delete", Icons.MENU_DELETE));
		hb.add(Box.createHorizontalGlue());

		vb.add(hb);

		return vb;
	}


	private void initLists() {

		rebuildFileTree();

		soundMap = Projects.getActive().getSoundsMap();

		rebuildEntryList();

		enableMiddlePanel(false);

		buttonDeleteKey.setEnabled(false);

	}


	private void rebuildEntryList() {

		List<String> opts = new ArrayList<String>();

		for (String key : soundMap.keySet()) {
			opts.add(key);
		}

		Collections.sort(opts, AlphanumComparator.instance);

		keyList.setItems(opts);
	}


	private void rebuildFileTree() {

		DirectoryFsTreeNode root = new DirectoryFsTreeNode("(root)");

		File f;
		DirectoryFsTreeNode dir;

		FileFilter soundOnlyFileFilter = new FileFilter() {

			@Override
			public boolean accept(File file) {

				if (file.isDirectory()) return true;

				if (!EAsset.forFile(file).isSound()) return false;

				return true;
			}
		};

		f = new File(OsUtils.getAppDir(Paths.DIR_VANILLA), "assets/minecraft/sounds");
		root.addChild(dir = new DirectoryFsTreeNode("Vanilla sounds", f, soundOnlyFileFilter));
		dir.setPathRoot(true);
		dir.setMark(1);

		f = Projects.getActive().getCustomSoundsDirectory();
		root.addChild(dir = new DirectoryFsTreeNode("Custom sounds", f, soundOnlyFileFilter));
		dir.setPathRoot(true);
		dir.setMark(2);

		treeDisplay.setRoot(root);
	}


	@Override
	protected void addActions() {


		// DEL key handler
		fileList.list.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

			}


			@Override
			public void keyPressed(KeyEvent e) {

				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					for (String val : fileList.getSelectedValues()) {
						fileList.removeItemNoSort(val);
						if (val.equals(editedKey)) enableMiddlePanel(false);
					}
					fileList.sortAndUpdate();
				}
			}


			@Override
			public void keyReleased(KeyEvent e) {

			}
		});


		// create key (not save yet)
		buttonNewKey.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				fileList.empty();

				editedKey = "";

				enableMiddlePanel(true);
				clearChange();

				keyList.list.setSelectedIndices(new int[] {});

				fieldKey.requestFocusInWindow();
			}
		});

		// delete button
		buttonDeleteKey.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				String key = keyList.getSelectedValue();

				if (key == null) return;

				//@formatter:off
				boolean y = Alerts.askYesNo(
						DialogSoundWizard.this,
						"Delete Entry",
						"Really want to to delete\n"
						+ "sound entry \"" + editedKey + "\"?"
				);
				//@formatter:on

				if (!y) return;

				keyList.removeItem(key);

				soundMap.remove(key);

				Projects.markChange();

			}
		});

		keyList.list.addMouseListener(new ClickListener() {

			@Override
			public void mouseClicked(MouseEvent e) {

				onKeyListSelection();
			}
		});


		// disable "delete" for zero selection
		keyList.list.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				onKeyListSelection();
			}
		});

		fieldKey.getDocument().addDocumentListener(new DocumentListener() {


			@Override
			public void insertUpdate(DocumentEvent e) {

				changedUpdate(e);
			}


			@Override
			public void removeUpdate(DocumentEvent e) {

				changedUpdate(e);
			}


			@Override
			public void changedUpdate(DocumentEvent e) {

				String txt = fieldKey.getText().trim();

				editStateValid = txt.length() > 0 && (!keyList.contains(txt) || txt.equals(editedKey));

				if (!txt.equals(editedKey)) markChange();

			}
		});

		fieldCategory.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				markChange();
			}
		});

		buttonSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				String key = fieldKey.getText().trim();
				String ctg = (String) fieldCategory.getSelectedItem();

				if (!editStateValid) return;

				keyList.removeItem(editedKey);
				keyList.addItem(key);

				SoundEntry newSoundEntry = new SoundEntry(ctg, fileList.getItems());

				soundMap.remove(editedKey);
				soundMap.put(key, newSoundEntry);

				keyList.list.setSelectedValue(editedKey, true);

				Projects.markChange();

				clearChange();

				enableMiddlePanel(false);

			}
		});

		buttonDiscard.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				clearChange();
				enableMiddlePanel(false);
			}
		});

		buttonOK.addActionListener(closeListener);
	}


	protected void onKeyListSelection() {

		int i = keyList.getSelectedIndex();
		buttonDeleteKey.setEnabled(i != -1);

		if (i != -1) {

			String sel = keyList.getSelectedValue();

			if (sel.equals(editedKey)) return;

			if (hasChange()) {
				boolean y = Alerts.askYesNo(DialogSoundWizard.this, "Discard Changes", "Discard changes in \"" + editedKey + "\"?");

				if (!y) {
					keyList.list.setSelectedValue(editedKey, true);
					return;
				}
			}

			clearChange();

			suppressEditCheck = true;

			editedKey = keyList.getSelectedValue();

			SoundEntry se = soundMap.get(editedKey);

			enableMiddlePanel(true);

			fieldKey.setText(editedKey);
			fieldCategory.setSelectedItem(se.category);

			fileList.setItems(se.sounds);

			suppressEditCheck = false;

			flagEntryChanged = false;

			editStateValid = true;
		}
	}


	private void markChange() {

		if (suppressEditCheck) {
			return;
		}

		flagEntryChanged = true;

		buttonDiscard.setEnabled(true);
		buttonSave.setEnabled(editStateValid);
	}


	private void clearChange() {

		flagEntryChanged = false;
		editStateValid = false;
		editedKey = null;
	}


	private boolean hasChange() {

		return editedKey != null && flagEntryChanged;
	}


	public void pathChanged(AbstractFsTreeNode node) {

		if (node instanceof DirectoryFsTreeNode) ((DirectoryFsTreeNode) node).reload();
		treeDisplay.model.nodeStructureChanged(node);
	}


	public void nodeRemoved(AbstractFsTreeNode node) {

		node.getParent().reload();
		treeDisplay.model.nodeStructureChanged(node.getParent());
	}


	@Override
	public void onClose() {

	}


	private class FileTreeTransferHandler extends TransferHandler {

		@Override
		public int getSourceActions(JComponent comp) {

			return COPY;
		}


		@Override
		public boolean canImport(TransferSupport support) {

			return !support.isDataFlavorSupported(FLAVOR_FSTREE_FILE);
		}


		@Override
		public boolean importData(TransferSupport support) {

			try {

				if (support.isDataFlavorSupported(DataFlavor.stringFlavor)) {

					String str = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);

					String[] lines = str.split("\n");

					List<String> sel = fileList.getSelectedValues();

					for (String line : lines) {
						if (line != null && sel.contains(line)) {
							fileList.removeItemNoSort(line);

							markChange();
						}
					}

					fileList.sortAndUpdate();
				}

			} catch (Exception e) {}

			return true;
		}

		private List<FileFsTreeNode> tmpNodeList;


		private void recursiveAddChildrenToTmpList(DirectoryFsTreeNode fsnode) {

			for (int i = 0; i < fsnode.getChildCount(); i++) {
				AbstractFsTreeNode fsn = fsnode.getChildAt(i);
				if (fsn.isFile()) {
					tmpNodeList.add((FileFsTreeNode) fsn);
				} else {
					recursiveAddChildrenToTmpList((DirectoryFsTreeNode) fsn);
				}
			}
		}


		@Override
		protected Transferable createTransferable(JComponent c) {

			JTree tree = (JTree) c;

			tmpNodeList = new ArrayList<FileFsTreeNode>();

			TreePath[] paths = tree.getSelectionPaths();
			for (TreePath path : paths) {
				AbstractFsTreeNode fsnode = (AbstractFsTreeNode) path.getLastPathComponent();
				if (fsnode.isDirectory()) {
					recursiveAddChildrenToTmpList((DirectoryFsTreeNode) fsnode);
				} else {
					tmpNodeList.add((FileFsTreeNode) fsnode);
				}
			}

			if (tmpNodeList.size() == 0) return null;

			return new Transferable() {

				private List<FileFsTreeNode> nodes = tmpNodeList;


				@Override
				public boolean isDataFlavorSupported(DataFlavor flavor) {

					return flavor.equals(FLAVOR_FSTREE_FILE);
				}


				@Override
				public DataFlavor[] getTransferDataFlavors() {

					return new DataFlavor[] { FLAVOR_FSTREE_FILE };
				}


				@Override
				public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {

					if (!isDataFlavorSupported(flavor)) throw new UnsupportedFlavorException(flavor);

					if (flavor.equals(FLAVOR_FSTREE_FILE)) return nodes;

					return null;
				}
			};
		}
	}

	private class FileListTransferHandler extends TransferHandler {

		@Override
		public int getSourceActions(JComponent comp) {

			return COPY;
		}


		@Override
		public boolean canImport(TransferSupport support) {

			if (!support.isDrop()) return false;

			if (!fileList.isEnabled()) return false;

			if (!support.isDataFlavorSupported(FLAVOR_FSTREE_FILE)) return false;

			support.setShowDropLocation(true);

			return true;
		}


		@Override
		public boolean importData(TransferSupport support) {

			if (!canImport(support)) return false;

			try {

				Transferable trans = support.getTransferable();

				List<FileFsTreeNode> nodes = (List<FileFsTreeNode>) trans.getTransferData(FLAVOR_FSTREE_FILE);

				boolean changed = false;
				for (FileFsTreeNode node : nodes) {
					String path = Utils.toLastDot(node.getPathRelativeToRoot().getPath());
					if (!fileList.contains(path)) {
						fileList.addItemNoSort(path);
						changed = true;
					}
				}

				if (changed) {
					fileList.sortAndUpdate();
					markChange();
				}

			} catch (UnsupportedFlavorException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			} catch (RuntimeException e) {
				e.printStackTrace();
			}


			return true;
		}

	}
}
