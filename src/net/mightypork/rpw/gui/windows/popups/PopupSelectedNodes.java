package net.mightypork.rpw.gui.windows.popups;


import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import net.mightypork.rpw.App;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.library.MagicSources;
import net.mightypork.rpw.library.Sources;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.tasks.Tasks;
import net.mightypork.rpw.tree.assets.EAsset;
import net.mightypork.rpw.tree.assets.processors.*;
import net.mightypork.rpw.tree.assets.tree.AssetTreeLeaf;
import net.mightypork.rpw.tree.assets.tree.AssetTreeNode;
import net.mightypork.rpw.tree.assets.tree.AssetTreeProcessor;
import net.mightypork.rpw.utils.logging.Log;


public class PopupSelectedNodes {

	public static PopupSelectedNodes open(Container c, int x, int y, List<AssetTreeNode> nodes) {

		return new PopupSelectedNodes(c, x, y, nodes);
	}

	private List<AssetTreeNode> nodes = null;

	private JMenuItem itemCopyToProject;
	private JMenuItem itemDeleteFromProject;
	private JMenuItem itemDeleteMetaFromProject;
	private JMenuItem itemEditAsset;
	private JMenuItem itemEditMeta;
	private JMenuItem itemCollapse;
	private JMenuItem itemExpand;
	private JMenuItem itemImportReplacement;
	private JMenuItem itemApplyResolved;
	private JMenuItem itemApplyResolvedOrInherit;

	private JMenuItem itemCopyToProjectSkipVanilla;


	private JLabel makeLabel(String text) {

		JLabel label = new JLabel(text);
		Font f = new Font(Font.SANS_SERIF, Font.BOLD | Font.ITALIC, 12);
		label.setFont(f);
		label.setForeground(new Color(0x5555cc));
		label.setBorder(BorderFactory.createEmptyBorder(3, 7, 3, 7));

		return label;
	}


	public PopupSelectedNodes(Container c, int x, int y, List<AssetTreeNode> nodes) {


		if (nodes == null) {
			Log.w("Popup for NULL node array, cancelling.");
			return;
		}

		// decide
		CountNodesInProjectProcessor proc = new CountNodesInProjectProcessor();
		for (AssetTreeNode n : nodes) {
			n.processThisAndChildren(proc);
		}

		int groups = proc.getGroups();
		int inProject = proc.getInProject();
		int inProjectMeta = proc.getInProjectMeta();
		int allLeaves = proc.getLeaves();
		int vanillaLeaves = proc.getVanillaLeaves();
		int metaLeaves = proc.getMetaLeaves();

		JMenuItem item;
		JPopupMenu popup;
		JMenu submenu;

		this.nodes = nodes;

		popup = new JPopupMenu("Selected items");

		popup.add(makeLabel("Modify selected"));

		popup.addSeparator();

		item = itemCopyToProject = new JMenuItem("Copy to project");
		item.setIcon(Icons.MENU_COPY);
		item.setEnabled(inProject < allLeaves);
		popup.add(item);

		item = itemCopyToProjectSkipVanilla = new JMenuItem("Copy to project, skip VANILLA");
		item.setIcon(Icons.MENU_COPY);
		item.setEnabled(inProject < allLeaves && vanillaLeaves != allLeaves);
		popup.add(item);

		popup.addSeparator();

		item = itemDeleteFromProject = new JMenuItem("Delete copies in project");
		item.setIcon(Icons.MENU_DELETE_ASSET);
		item.setEnabled(inProject > 0);
		popup.add(item);

		if (metaLeaves > 0) {
			item = itemDeleteMetaFromProject = new JMenuItem("Delete \".mcmeta\" files in project");
			item.setIcon(Icons.MENU_DELETE_ASSET);
			item.setEnabled(inProjectMeta > 0);
			popup.add(item);
		}


		popup.addSeparator();

		item = itemEditAsset = new JMenuItem("Open in editor");
		item.setIcon(Icons.MENU_EDIT);
		item.setEnabled(allLeaves == 1 && nodes.size() == 1);
		popup.add(item);

		if (metaLeaves > 0) {
			item = itemEditMeta = new JMenuItem("Edit \".mcmeta\"");

			item.setIcon(Icons.MENU_EDIT);
			item.setEnabled(allLeaves == 1 && nodes.size() == 1);

			if (nodes.get(0).isLeaf()) {
				AssetTreeLeaf leaf = (AssetTreeLeaf) nodes.get(0);
				if (!leaf.isMetaProvidedByProject()) item.setIcon(Icons.MENU_NEW);
			}
			popup.add(item);
		}

		item = itemImportReplacement = new JMenuItem("Import replacement");
		item.setIcon(Icons.MENU_IMPORT_BOX);
		item.setEnabled(allLeaves == 1 && nodes.size() == 1);
		popup.add(item);


		if (groups > 0) {
			popup.addSeparator();

			submenu = new JMenu("Tree structure");
			submenu.setIcon(Icons.MENU_TREE);

			item = itemCollapse = new JMenuItem("Collapse recursively");
			item.setIcon(Icons.TREE_CLOSE);
			submenu.add(item);

			item = itemExpand = new JMenuItem("Expand recursively");
			item.setIcon(Icons.TREE_OPEN);
			submenu.add(item);

			popup.add(submenu);
		}


		popup.addSeparator();

		popup.add(makeLabel("Assign source"));

		popup.addSeparator();

		item = itemApplyResolved = new JMenuItem("Assign resolved");
		item.setIcon(Icons.MENU_RESOLVE);
		popup.add(item);

		item = itemApplyResolvedOrInherit = new JMenuItem("Assign resolved, skip VANILLA");
		item.setIcon(Icons.MENU_RESOLVE);
		popup.add(item);

		popup.addSeparator();

		if (groups > 0) {
			List<Component> list;

			submenu = new JMenu("Recursive edit");
			submenu.setIcon(Icons.MENU_RECURSION);

			list = buildMenuItems(true);
			for (Component cmp : list) {
				if (cmp == null) {
					submenu.addSeparator();
					continue;
				} else {
					submenu.add(cmp);
				}
			}

			popup.add(submenu);
			popup.addSeparator();
		}


		// direct menu for nodes
		List<Component> list = buildMenuItems(false);
		for (Component cmp : list) {
			if (cmp == null) {
				popup.addSeparator();
				continue;
			} else {
				popup.add(cmp);
			}
		}

		addActions();

		popup.pack();
		popup.show(c, x - 15, y - 15);

	}


	private void addActions() {

		// selected
		if (itemCollapse != null) itemCollapse.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				for (AssetTreeNode node : PopupSelectedNodes.this.nodes) {
					if (node.isLeaf()) continue;
					App.getTreeDisplay().togglePathRecursively(node, false);
				}
				Tasks.taskTreeRedraw();
			}
		});

		if (itemExpand != null) itemExpand.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				for (AssetTreeNode node : PopupSelectedNodes.this.nodes) {
					if (node.isLeaf()) continue;
					App.getTreeDisplay().togglePathRecursively(node, true);
				}
				Tasks.taskTreeRedraw();
			}
		});


		itemCopyToProject.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				(new Thread(new Runnable() {

					@Override
					public void run() {

						Alerts.loading(true);
						AssetTreeProcessor procCopyToProject = new CopyToProjectProcessor();
						AssetTreeProcessor procSetToInherit = new SetToSourceProcessor(MagicSources.INHERIT);
						for (AssetTreeNode node : PopupSelectedNodes.this.nodes) {
							node.processThisAndChildren(procCopyToProject);
							node.processThisAndChildren(procSetToInherit);
							node.setLibrarySourceIfNeeded(MagicSources.PROJECT);
						}

						Tasks.taskTreeRedraw();
						App.getSidePanel().redrawPreview();
						Projects.markChange();
						Alerts.loading(false);
					}
				})).start();
			}
		});


		itemCopyToProjectSkipVanilla.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				(new Thread(new Runnable() {

					@Override
					public void run() {

						Alerts.loading(true);

						CopyToProjectProcessor procCopyToProject = new CopyToProjectProcessor();
						procCopyToProject.addIgnoredSource(MagicSources.VANILLA);

						AssetTreeProcessor procSetToInherit = new SetToSourceProcessor(MagicSources.INHERIT);

						for (AssetTreeNode node : PopupSelectedNodes.this.nodes) {

							node.processThisAndChildren(procCopyToProject);
							node.processThisAndChildren(procSetToInherit);
							node.setLibrarySourceIfNeeded(MagicSources.PROJECT);

							//if(MagicSources.isVanilla(node.resolveAssetSource())) continue; // is ignored
						}

						Tasks.taskTreeRedraw();
						App.getSidePanel().redrawPreview();
						Projects.markChange();
						Alerts.loading(false);
					}
				})).start();
			}
		});


		itemDeleteFromProject.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				//@formatter:off
				boolean really = Alerts.askYesNo(
						App.getFrame(),
						"Deletion",
						"Are you sure to delete the\n" +
						"selected files from project?"
				);
				//@formatter:on

				if (!really) return;

				(new Thread(new Runnable() {

					@Override
					public void run() {

						Alerts.loading(true);
						AssetTreeProcessor proc = new DeleteFromProjectProcessor();
						for (AssetTreeNode node : PopupSelectedNodes.this.nodes) {
							node.processThisAndChildren(proc);
						}
						Tasks.taskDeleteEmptyDirsFromProject();
						Tasks.taskTreeRedraw();
						App.getSidePanel().redrawPreview();
						Projects.markChange();
						Alerts.loading(false);
					}
				})).start();
			}
		});

		if (itemDeleteMetaFromProject != null) itemDeleteMetaFromProject.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				//@formatter:off
				boolean really = Alerts.askYesNo(
						App.getFrame(),
						"Deletion",
						"Are you sure to delete \"McMeta\" files\n" +
						"of the selected assets from project?"
				);
				//@formatter:on

				if (!really) return;

				(new Thread(new Runnable() {

					@Override
					public void run() {

						Alerts.loading(true);
						AssetTreeProcessor proc = new DeleteFromProjectProcessor(false, true);
						for (AssetTreeNode node : PopupSelectedNodes.this.nodes) {
							node.processThisAndChildren(proc);
						}
						Tasks.taskDeleteEmptyDirsFromProject();
						Tasks.taskTreeRedraw();
						App.getSidePanel().redrawPreview();
						Projects.markChange();
						Alerts.loading(false);
					}
				})).start();
			}
		});

		itemImportReplacement.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Tasks.taskImportReplacement((AssetTreeLeaf) nodes.get(0));
			}
		});

		itemApplyResolved.addActionListener(listenerResolveRecursive);
		itemApplyResolvedOrInherit.addActionListener(listenerResolveOrInheritRecursive);

		itemEditAsset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Tasks.taskEditAsset((AssetTreeLeaf) nodes.get(0));
			}
		});

		if (itemEditMeta != null) itemEditMeta.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				Tasks.taskEditMeta((AssetTreeLeaf) nodes.get(0));
			}
		});
	}


	private List<Component> buildMenuItems(boolean recursive) {

		List<Component> items = new ArrayList<Component>();

		ActionListener listener = (recursive ? listenerSetRecursive : listenerSetSimple);

		JMenuItem item;

		item = new JMenuItem("INHERIT", KeyEvent.VK_I);
		item.setActionCommand(MagicSources.INHERIT);
		item.addActionListener(listener);
		item.setIcon(Icons.MENU_INHERIT);
		items.add(item);

		item = new JMenuItem("VANILLA", KeyEvent.VK_V);
		item.setActionCommand(MagicSources.VANILLA);
		item.addActionListener(listener);
		item.setIcon(Icons.MENU_VANILLA);
		items.add(item);

		item = new JMenuItem("PROJECT", KeyEvent.VK_P);
		item.setActionCommand(MagicSources.PROJECT);
		item.addActionListener(listener);
		item.setIcon(Icons.MENU_PROJECT);
		items.add(item);

		items.add(null);


		// count sounds in selection
		CountNodesOfTypeProcessor procCnt = new CountNodesOfTypeProcessor(EAsset.SOUND);
		for (AssetTreeNode node : nodes) {
			node.processThisAndChildren(procCnt);
		}

		if (procCnt.getCount() > 0) {
			item = new JMenuItem("SILENCE");
			item.setActionCommand(MagicSources.SILENCE);
			item.addActionListener(listener);
			item.setIcon(Icons.MENU_SILENCE);
			items.add(item);

			items.add(null);
		}


		List<String> sources = Sources.getSourceNames();

		for (String source : sources) {
			item = new JMenuItem(Sources.processForDisplay(source));
			item.setActionCommand(source);
			item.addActionListener(listener);
			item.setIcon(Icons.MENU_SET_TO_SOURCE);
			items.add(item);
		}

		if (sources.size() == 0) {
			item = new JMenuItem("No library sources...");
			item.setEnabled(false);
			items.add(item);
		}

		return items;
	}

	private ActionListener listenerSetSimple = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			String source = e.getActionCommand();

			AssetTreeProcessor proc = new SetToSourceProcessor(source);
			for (AssetTreeNode node : nodes) {
				proc.process(node);
			}

			Tasks.taskTreeRedraw();
			App.getSidePanel().redrawPreview();
			Projects.markChange();
		}
	};

	private ActionListener listenerSetRecursive = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			String source = e.getActionCommand();

			AssetTreeProcessor proc = new SetToSourceProcessor(source);
			for (AssetTreeNode node : nodes) {
				node.processThisAndChildren(proc);
			}

			Tasks.taskTreeRedraw();
			App.getSidePanel().redrawPreview();
			Projects.markChange();
		}
	};

	private ActionListener listenerResolveRecursive = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			AssetTreeProcessor proc = new ApplyInheritProcessor();
			for (AssetTreeNode node : nodes) {
				node.processThisAndChildren(proc);
			}

			Tasks.taskTreeRedraw();
			App.getSidePanel().redrawPreview();
			Projects.markChange();
		}
	};

	private ActionListener listenerResolveOrInheritRecursive = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {

			ApplyInheritProcessor proc1 = new ApplyInheritProcessor(MagicSources.INHERIT);
			for (AssetTreeNode node : nodes) {
				node.processThisAndChildren(proc1);
			}

			SetToSourceProcessor proc2 = new SetToSourceProcessor(MagicSources.INHERIT);
			proc2.setModifyLeaves(false); // do only groups
			for (AssetTreeNode node : nodes) {
				node.processThisAndChildren(proc2);
			}

			Tasks.taskTreeRedraw();
			App.getSidePanel().redrawPreview();
			Projects.markChange();
		}
	};
}
