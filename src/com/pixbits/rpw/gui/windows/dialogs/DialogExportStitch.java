package com.pixbits.rpw.gui.windows.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.google.gson.reflect.TypeToken;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Config;
import net.mightypork.rpw.Const;
import net.mightypork.rpw.Config.FilePath;
import net.mightypork.rpw.gui.Gui;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.helpers.FileChooser;
import net.mightypork.rpw.gui.helpers.TextInputValidator;
import net.mightypork.rpw.gui.widgets.FileInput;
import net.mightypork.rpw.gui.widgets.SimpleStringList;
import net.mightypork.rpw.gui.widgets.VBox;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.tasks.Tasks;
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.files.OsUtils;
import net.mightypork.rpw.utils.files.SimpleConfig;
import net.mightypork.rpw.utils.logging.Log;

public class DialogExportStitch extends RpwDialog {
  private final String[] groupNames = {"Blocks", "Items"};
  private JCheckBox[] selection;

  private FileInput filepicker;

  private JButton buttonOK;
  private JButton buttonCancel;

  public DialogExportStitch()
  {
    super(App.getFrame(), "Export Stitch");
        
    createDialog();
  }
  

  @Override
  protected JComponent buildGui()
  {
    selection = new JCheckBox[groupNames.length];
    for (int i = 0; i < selection.length; ++i)
      selection[i] = new JCheckBox(groupNames[i]);
    
    final VBox vbox = new VBox();
    vbox.windowPadding();
    
    vbox.heading("Export Stitched PNGs");
    
    vbox.titsep("Resources to export");
    vbox.gap_small();
    
    for (JCheckBox cb : selection)
      vbox.add(cb);
    
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
  
  private final ActionListener exportListener = new ActionListener() {
    
    @Override
    public void actionPerformed(ActionEvent evt)
    {
      if (!filepicker.hasFile()) {
        Alerts.error(self(), "Missing folder", "The selected folder does not exist.");
        return;
      }
      
      final File file = filepicker.getFile();
      
      
    }
  };

}
