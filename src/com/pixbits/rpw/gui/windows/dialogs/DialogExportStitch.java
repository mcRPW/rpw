package com.pixbits.rpw.gui.windows.dialogs;



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Set;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;

import net.mightypork.rpw.App;

import net.mightypork.rpw.Config.FilePath;
import net.mightypork.rpw.gui.Gui;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.helpers.FileChooser;
import net.mightypork.rpw.gui.widgets.FileInput;
import net.mightypork.rpw.gui.widgets.VBox;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.project.Projects;

import net.mightypork.rpw.project.Project;

import com.pixbits.tasks.*;

public class DialogExportStitch extends RpwDialog {
  private JCheckBox[] selection;

  private FileInput filepicker;

  private JButton buttonOK;
  private JButton buttonCancel;
  
  private JCheckBox exportMissing;
  private JCheckBox exportExisting;

  public DialogExportStitch()
  {
    super(App.getFrame(), "Export Stitch");
        
    createDialog();
  }
  

  @Override
  protected JComponent buildGui()
  {
    selection = new JCheckBox[AssetCategory.values().length+1];
    for (int i = 0; i < AssetCategory.values().length; ++i)
      selection[i] = new JCheckBox(AssetCategory.values()[i].name);
    
    selection[selection.length-1] = new JCheckBox("Select All");
    selection[selection.length-1].addActionListener(checkboxListener);
    
    for (JCheckBox cb : selection) cb.setSelected(true);
    
    final VBox vbox = new VBox();
    vbox.windowPadding();
    
    vbox.heading("Export Stitched PNGs");
    
    vbox.titsep("Resources to export");
    vbox.gap_small();
    
    for (JCheckBox cb : selection)
      vbox.add(cb);
    
    vbox.gap();
    
    vbox.add(exportMissing = new JCheckBox("Export missing"));
    vbox.add(exportExisting = new JCheckBox("Export existing"));
    exportMissing.setSelected(true);
    
    vbox.gapl();
    
    vbox.titsep("Folder to export to");
    vbox.gap();
    
    //@formatter:off
    filepicker = new FileInput(
        this,
        "Select folder to export to...",
        FilePath.EXPORT,
        "Export stitched pack",
        FileChooser.FOLDERS,
        true        
    );
    //@formatter:on
    
    vbox.add(filepicker);
    
    vbox.gapl();

    vbox.titsep("Export");
    vbox.gap();
    
    buttonOK = new JButton("Export", Icons.MENU_EXPORT);
    buttonCancel = new JButton("Cancel", Icons.MENU_CANCEL);
    vbox.buttonRow(Gui.RIGHT, buttonOK, buttonCancel);
    
    return vbox;
  }
  
  
  @Override
  protected void addActions()
  {
    setEnterButton(buttonOK);
    
    buttonCancel.addActionListener(closeListener);
    
    buttonOK.addActionListener(exportListener);
  }
  
  @Override
  protected void onShown()
  {

  }
  
  private final ActionListener checkboxListener = new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent evt)
    {
      JCheckBox src = (JCheckBox)evt.getSource();
      
      if (src == selection[selection.length-1])
      {
        for (int i = 0; i < selection.length-1; ++i)
          selection[i].setSelected(src.isSelected());
      }
    }
  };
  
  private final ActionListener exportListener = new ActionListener() {
    
    @Override
    public void actionPerformed(ActionEvent evt)
    {
      if (!filepicker.hasFile()) {
        Alerts.error(self(), "Missing folder", "The selected folder does not exist.");
        return;
      }
      
      Set<AssetCategory> categories = new HashSet<AssetCategory>();
      
      for (int i = 0; i < AssetCategory.values().length; ++i)
        if (selection[i].isSelected())
          categories.add(AssetCategory.values()[i]);
      
      if (categories.isEmpty())
      {
        Alerts.error(self(), "Category Required", "At least one category is required");
        return;
      }
      
      final File file = filepicker.getFile();
      final Project project = Projects.getActive();
      
      Tasks.exportPackToStitchedPng(file, project, categories, exportMissing.isSelected(), exportExisting.isSelected());
    }
  };

}
