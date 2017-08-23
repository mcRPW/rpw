package net.mightypork.rpw.gui.windows.dialogs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Config.FilePath;
import net.mightypork.rpw.gui.Gui;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.helpers.FileChooser;
import net.mightypork.rpw.gui.widgets.ManagerLayout;
import net.mightypork.rpw.gui.widgets.VBox;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.project.Projects;
import net.mightypork.rpw.tasks.Tasks;
import net.mightypork.rpw.utils.files.DesktopApi;
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.logging.Log;


public class DialogProjectProperties extends RpwDialog {

    private FileChooser fc;

    private JButton buttonOK;
    private JLabel imageView;

    private final File iconFile;
    private JButton btnIconEdit;
    private JButton btnIconImport;
    private JButton btnIconDefault;
    private JButton btnIconRefresh;
    private JTextField nameField;
    private JTextField titleField;
    private JTextField descriptionField;


    private void redrawIcon() {
        imageView.setIcon(getProjectIcon());
    }


    public DialogProjectProperties() {
        super(App.getFrame(), "Project Properties");

        iconFile = new File(Projects.getActive().getProjectDirectory(), "pack.png");

        createDialog();
    }


    @Override
    protected JComponent buildGui() {
        VBox vbox;
        vbox = new VBox();

        vbox.windowPadding();

        vbox.heading("Project details");

        vbox.titsep("Properties");
        vbox.gap();

        // form

        nameField = Gui.textField("", "Project folder name", "Name of the project folder");
        nameField.setEditable(false);
        nameField.setBackground(new Color(0xeeeeee));
        titleField = Gui.textField("", "Project title", "Title of project");
        descriptionField = Gui.textField("", "Project description", "Description of project");

        vbox.springForm(new String[]{"Name:", "Title:", "Description:"}, new JComponent[]{nameField, titleField, descriptionField});

        vbox.gap();
        vbox.add(Gui.commentLine("Use \"My Projects\" dialog to rename project."));
        vbox.gapl();

        vbox.titsep("Icon");
        vbox.gap();

        imageView = new JLabel(getProjectIcon());
        imageView.setPreferredSize(new Dimension(160, 128));
        imageView.setHorizontalAlignment(SwingConstants.CENTER);
        imageView.setAlignmentX(0.5f);

        btnIconEdit = Gui.sidebarButton("Edit", "Open in image editor", Icons.MENU_EDIT);
        btnIconImport = Gui.sidebarButton("Import", "Import replacement icon", Icons.MENU_IMPORT_BOX);
        btnIconDefault = Gui.sidebarButton("Default", "Reset to RPW default icon", Icons.MENU_DELETE);
        btnIconRefresh = Gui.sidebarButton("Refresh", "Reload preview", Icons.MENU_RELOAD);

        final ManagerLayout ml = new ManagerLayout(4);
        ml.setMainComponent(imageView);
        ml.setTopButtons(btnIconEdit, btnIconImport, btnIconDefault);
        ml.setBottomButtons(btnIconRefresh);
        ml.build();
        vbox.add(ml);

        vbox.gapl();

        buttonOK = new JButton("OK", Icons.MENU_YES);
        vbox.buttonRow(Gui.RIGHT, buttonOK);

        return vbox;
    }


    @Override
    protected void onShown() {
        nameField.setText(Projects.getActive().getName());
        titleField.setText(Projects.getActive().getTitle());
        descriptionField.setText(Projects.getActive().getDescription());
    }


    @Override
    protected void initGui() {
        fc = new FileChooser(this, FilePath.IMPORT_FILE, "Import Project Icon (128x128 PNG)", FileChooser.PNG, true, false, false);
    }


    private Icon getProjectIcon() {
        return Icons.getIconFromFile(iconFile, new Dimension(128, 128));
    }


    @Override
    public void onClose() {
        Tasks.taskOnProjectPropertiesChanged();
    }


    @Override
    protected void addActions() {
        setEnterButton(buttonOK);

        btnIconEdit.addActionListener(imgEditListener);
        btnIconImport.addActionListener(imgImportListener);
        btnIconDefault.addActionListener(imgDefaultListener);
        btnIconRefresh.addActionListener(imgRedrawListener);
        buttonOK.addActionListener(okListener);
    }

    private final ActionListener okListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            Projects.getActive().setTitle(titleField.getText());
            Projects.getActive().setDescription(descriptionField.getText());
            closeDialog();
        }
    };

    private final ActionListener imgEditListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!DesktopApi.editImage(iconFile)) {
                //@formatter:off
                Alerts.error(
                        App.getFrame(),
                        "Could not edit file, your\n" +
                                "platform is not supported.\n" +
                                "\n" +
                                "Check log file for details."
                );
                //@formatter:on
            }
        }
    };

    private final ActionListener imgImportListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            fc.showOpenDialog();
            if (fc.approved()) {
                final File f = fc.getSelectedFile();

                if (f == null) return;

                if (f.exists()) {
                    try {
                        FileUtils.copyFile(f, iconFile);
                        redrawIcon();
                    } catch (final IOException e1) {
                        //@formatter:off
                        Alerts.error(
                                self(),
                                "Error Copying File",
                                "An error occured while copying\n" +
                                        "selected icon file to project\n" +
                                        "directory."
                        );
                        //@formatter:on
                    }
                }
            }
        }

    };

    private final ActionListener imgDefaultListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            Projects.getActive().installDefaultIcon(true);
            redrawIcon();
        }
    };

    private final ActionListener imgRedrawListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            redrawIcon();
        }
    };

}
