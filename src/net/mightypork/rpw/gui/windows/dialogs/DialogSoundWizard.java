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
import net.mightypork.rpw.Config;
import net.mightypork.rpw.Const;
import net.mightypork.rpw.Paths;
import net.mightypork.rpw.gui.Gui;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.helpers.ClickListener;
import net.mightypork.rpw.gui.helpers.TextInputValidator;
import net.mightypork.rpw.gui.widgets.HBox;
import net.mightypork.rpw.gui.widgets.SimpleStringList;
import net.mightypork.rpw.gui.widgets.SoundFileTreeDisplay;
import net.mightypork.rpw.gui.widgets.VBox;
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
import net.mightypork.rpw.utils.Utils;
import net.mightypork.rpw.utils.files.OsUtils;

import org.jdesktop.swingx.JXTitledSeparator;


public class DialogSoundWizard extends RpwDialog {

	private static final DataFlavor FLAVOR_FSTREE_FILE = new DataFlavor(FileFsTreeNode.class, "FSTREE_FILE_NODE");

	private JButton buttonDeleteKey;
	private JButton buttonDiscard;
	private JButton buttonNewKey;
	private JButton buttonOK;
	private JButton buttonSave;

	private JComboBox fieldCategory;
	private JTextField fieldKey;

	private SimpleStringList fileList;
	private SimpleStringList keyList;
	private SoundFileTreeDisplay treeDisplay;

	private String editedKey = null;
	private boolean flagEntryChanged = false;
	private boolean suppressEditCheck = false;
	protected boolean editStateValid = false;

	private SoundEntryMap soundMap;
	private ArrayList<Component> middlePanelComponents;


	public DialogSoundWizard() {

		super(App.getFrame(), "Sound Wizard");

		createDialog();
	}


	@Override
	protected JComponent buildGui() {

		HBox hbMain, hb;
		VBox vbMain;

		vbMain = new VBox();

		vbMain.windowPadding();
		vbMain.heading("Sound Wizard");

		//@formatter:off
		hbMain = new HBox();
	
			// box with keys and editing current key
			hb = new HBox();
		
				Gui.titledBorder(hb, "Sound Entries", Gui.GAP);

				hb.add(createLeftPanel());		
				hb.gapl();
				
				hb.sep();
				
				hb.gapl();		
				hb.add(createMiddlePanel());
	
			hbMain.add(hb);
	
			hbMain.add(createRightPanel());
		vbMain.add(hbMain);
		//@formatter:on

		vbMain.gap();

		// buttons row		
		buttonOK = new JButton("Close", Icons.MENU_EXIT);
		vbMain.buttonRow(Gui.RIGHT, buttonOK);

		fileList.setMultiSelect(true);
		fileList.getList().setDragEnabled(true);
		treeDisplay.tree.setDragEnabled(true);

		return vbMain;
	}


	private Component createLeftPanel() {

		VBox vb = new VBox();

		// box for the list of keys
		keyList = new SimpleStringList();
		keyList.setPreferredSize(new Dimension(300, 400));

		vb.glue();
		vb.add(keyList);

		// box with buttons under the list
		buttonNewKey = new JButton("New", Icons.MENU_NEW);
		buttonDeleteKey = new JButton("Delete", Icons.MENU_DELETE);

		vb.gap();
		vb.buttonRow(Gui.LEFT, buttonNewKey, buttonDeleteKey);

		return vb;
	}


	private Component createMiddlePanel() {

		middlePanelComponents = new ArrayList<Component>();

		// box for editing a key

		VBox vb = new VBox();


		// fields
		JLabel l1 = new JLabel("Name:");
		JLabel l2 = new JLabel("Category:");
		middlePanelComponents.add(l1);
		middlePanelComponents.add(l2);

		fieldKey = Gui.textField();
		fieldKey.setDragEnabled(false);
		fieldKey.addKeyListener(TextInputValidator.identifiers());
		fieldKey.setTransferHandler(new TransferHandler() {});
		middlePanelComponents.add(fieldKey);

		fieldCategory = new JComboBox(Const.SOUND_CATEGORIES);
		middlePanelComponents.add(fieldCategory);

		vb.springForm(new Object[] { l1, l2 }, new JComponent[] { fieldKey, fieldCategory });


		// file list
		JXTitledSeparator sep;
		vb.glue();
		sep = vb.titsep("Selected files");
		middlePanelComponents.add(sep);

		fileList = new SimpleStringList();
		fileList.setPreferredSize(new Dimension(300, 300));

		vb.add(fileList);
		middlePanelComponents.add(fileList);


		// box with buttons under the list
		buttonSave = new JButton("Save", Icons.MENU_SAVE);
		buttonDiscard = new JButton("Discard", Icons.MENU_CANCEL);

		middlePanelComponents.add(buttonSave);
		middlePanelComponents.add(buttonDiscard);

		vb.gap();
		vb.buttonRow(Gui.RIGHT, buttonSave, buttonDiscard);


		return vb;
	}


	private Component createRightPanel() {

		VBox vb = new VBox();

		// file list
		Gui.titledBorder(vb, "All Audio Files", Gui.GAP);

		if (Config.USE_NIMBUS) Gui.useMetal();
		treeDisplay = new SoundFileTreeDisplay(null, this);
		if (Config.USE_NIMBUS) Gui.useNimbus();

		vb.glue();
		JComponent c = treeDisplay.getComponent();
		c.setPreferredSize(new Dimension(300, 400));
		vb.add(c);

		// comment
		vb.gap();
		vb.add(Gui.commentLine("<html><center>Drag to <i>Selected Files</i> to select.<br>Right-click for options.</center></html>"));

		return vb;
	}


	@Override
	protected void addActions() {

		setEnterButton(buttonOK);


		// DEL key handler
		fileList.list.addKeyListener(new KeyListener() {

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


			@Override
			public void keyTyped(KeyEvent e) {

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

				if (key.equals(editedKey)) {
					clearChange();
					enableMiddlePanel(false);
				}

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
			public void changedUpdate(DocumentEvent e) {

				String txt = fieldKey.getText().trim();

				editStateValid = txt.length() > 0 && (!keyList.contains(txt) || txt.equals(editedKey));

				if (!txt.equals(editedKey)) markChange();

			}


			@Override
			public void insertUpdate(DocumentEvent e) {

				changedUpdate(e);
			}


			@Override
			public void removeUpdate(DocumentEvent e) {

				changedUpdate(e);
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


	private void clearChange() {

		flagEntryChanged = false;
		editStateValid = false;
		editedKey = null;
	}


	private boolean hasChange() {

		return editedKey != null && flagEntryChanged;
	}


	@Override
	protected void initGui() {

		fileList.setTransferHandler(new FileListTransferHandler());
		treeDisplay.tree.setTransferHandler(new FileTreeTransferHandler());

		initLists();
	}


	private void initLists() {

		rebuildFileTree();

		soundMap = Projects.getActive().getSoundsMap();

		rebuildEntryList();

		enableMiddlePanel(false);

		buttonDeleteKey.setEnabled(false);

	}


	private void markChange() {

		if (suppressEditCheck) {
			return;
		}

		flagEntryChanged = true;

		buttonDiscard.setEnabled(true);
		buttonSave.setEnabled(editStateValid);
	}


	public void nodeRemoved(AbstractFsTreeNode node) {

		node.getParent().reload();
		treeDisplay.model.nodeStructureChanged(node.getParent());
	}


	@Override
	public void onClose() {

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


	public void pathChanged(AbstractFsTreeNode node) {

		if (node instanceof DirectoryFsTreeNode) ((DirectoryFsTreeNode) node).reload();
		treeDisplay.model.nodeStructureChanged(node);
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


	private class FileListTransferHandler extends TransferHandler {

		@Override
		public boolean canImport(TransferSupport support) {

			if (!support.isDrop()) return false;

			if (!fileList.isEnabled()) return false;

			if (!support.isDataFlavorSupported(FLAVOR_FSTREE_FILE)) return false;

			support.setShowDropLocation(true);

			return true;
		}


		@Override
		public int getSourceActions(JComponent comp) {

			return COPY;
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

	private class FileTreeTransferHandler extends TransferHandler {

		private List<FileFsTreeNode> tmpNodeList;


		@Override
		public boolean canImport(TransferSupport support) {

			return !support.isDataFlavorSupported(FLAVOR_FSTREE_FILE);
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
				public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {

					if (!isDataFlavorSupported(flavor)) throw new UnsupportedFlavorException(flavor);

					if (flavor.equals(FLAVOR_FSTREE_FILE)) return nodes;

					return null;
				}


				@Override
				public DataFlavor[] getTransferDataFlavors() {

					return new DataFlavor[] { FLAVOR_FSTREE_FILE };
				}


				@Override
				public boolean isDataFlavorSupported(DataFlavor flavor) {

					return flavor.equals(FLAVOR_FSTREE_FILE);
				}
			};
		}


		@Override
		public int getSourceActions(JComponent comp) {

			return COPY;
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
	}
}
