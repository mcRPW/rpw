package net.mightypork.rpw.gui.windows.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;

import net.mightypork.rpw.App;
import net.mightypork.rpw.Config.FilePath;
import net.mightypork.rpw.Paths;
import net.mightypork.rpw.gui.Gui;
import net.mightypork.rpw.gui.Icons;
import net.mightypork.rpw.gui.helpers.FileChooser;
import net.mightypork.rpw.gui.helpers.TextInputValidator;
import net.mightypork.rpw.gui.widgets.FileInput;
import net.mightypork.rpw.gui.widgets.FileInput.FilePickListener;
import net.mightypork.rpw.gui.widgets.VBox;
import net.mightypork.rpw.gui.windows.RpwDialog;
import net.mightypork.rpw.gui.windows.messages.Alerts;
import net.mightypork.rpw.library.Sources;
import net.mightypork.rpw.tasks.Tasks;
import net.mightypork.rpw.tree.assets.EAsset;
import net.mightypork.rpw.utils.files.FileUtils;
import net.mightypork.rpw.utils.files.OsUtils;
import net.mightypork.rpw.utils.files.ZipUtils;
import net.mightypork.rpw.utils.validation.StringFilter;


public class DialogImportPack extends RpwDialog {

    private final List<String> libPackNames;

    private JTextField field;

    private JButton buttonOk;
    private JButton buttonCancel;

    private FileInput filepicker;


    public DialogImportPack() {
        super(App.getFrame(), "Import");

        libPackNames = Sources.getResourcepackNames();

        createDialog();
    }


    @Override
    protected JComponent buildGui() {
        final VBox vb = new VBox();
        vb.windowPadding();

        vb.heading("Import resource pack");

        vb.titsep("File to import");
        vb.gap();

        //@formatter:off
        filepicker = new FileInput(
                this,
                "Select file to import...",
                FilePath.IMPORT_PACK,
                "Import resource pack",
                FileChooser.ZIP,
                true
        );
        //@formatter:on

        vb.add(filepicker);

        vb.gapl();

        field = Gui.textField("", "Pack name", "Name used in RPW");
        field.addKeyListener(TextInputValidator.filenames());

        vb.springForm(new String[]{"Name:"}, new JComponent[]{field});

        vb.gapl();

        buttonOk = new JButton("Import", Icons.MENU_YES);
        buttonCancel = new JButton("Cancel", Icons.MENU_CANCEL);
        vb.buttonRow(Gui.RIGHT, buttonOk, buttonCancel);

        return vb;
    }


    @Override
    protected void initGui() {
        filepicker.setListener(new FilePickListener() {

            @Override
            public void onFileSelected(File file) {
                try {
                    final String[] parts = FileUtils.getFilenameParts(file);
                    if (field.getText().trim().length() == 0) {
                        field.setText(parts[0]);
                    }

                } catch (final Throwable t) {
                }
            }
        });
    }


    @Override
    protected void addActions() {
        setEnterButton(buttonOk);
        buttonOk.addActionListener(submitListener);
        buttonCancel.addActionListener(closeListener);
    }


    @Override
    public void onClose() {
        Tasks.taskReloadSources(null);
    }

    private final ActionListener submitListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!filepicker.hasFile()) {
                Alerts.error(self(), "Missing file", "The selected file does not exist.");
                return;
            }

            final File file = filepicker.getFile();

            final String name = field.getText().trim();
            if (name.length() == 0) {
                Alerts.error(self(), "Invalid name", "The pack needs a name!");
                return;
            }

            if (libPackNames.contains(name)) {
                Alerts.error(self(), "Invalid name", "Pack named \"" + name + "\" is already in the library!");
                return;
            }

            // do the import

            final File out = OsUtils.getAppDir(Paths.DIR_RESOURCEPACKS + "/" + name, true);

            final StringFilter filter = new StringFilter() {

                @Override
                public boolean accept(String path) {
                    boolean ok = false;

                    final String ext = FileUtils.getExtension(path);
                    final EAsset type = EAsset.forExtension(ext);

                    ok |= path.startsWith("assets");
                    ok &= type.isAsset();

                    return ok;
                }
            };

            try {
                if (!ZipUtils.entryExists(file, "pack.mcmeta")) {
                    Alerts.error(self(), "Invalid format", "Selected ZIP file isn't\na valid resource pack!");
                    return;
                }

                ZipUtils.extractZip(file, out, filter);
                closeDialog();
                Alerts.info(App.getFrame(), "Resource pack \"" + name + "\" was imported.");

            } catch (final Exception exc) {
                Alerts.error(DialogImportPack.this, "Error while extracting the pack.");
                FileUtils.delete(out, true); // cleanup
            }

        }
    };
}
