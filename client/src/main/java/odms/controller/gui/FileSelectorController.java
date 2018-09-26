package odms.controller.gui;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Class information found from
 * https://docs.oracle.com/javafx/2/ui_controls/file-chooser.htm
 */

public class FileSelectorController {

    /**
     * Opens a file selector
     *
     * @param stage stage to open file selector on
     * @param extensions list of extension that can be selected from
     * @return the file path of the file
     */
    public String getFileSelector(final Stage stage, Collection<String> extensions) {
        stage.setTitle("File Selector");

        FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser, extensions);
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            return file.getAbsolutePath();
        } else {
            return null;
        }

    }

    /**
     * Customizes configuration of a file chooser
     *
     * @param allowedExtensions list of extension that can be selected from
     * @param fileChooser fileChooser to configure
     */
    private void configureFileChooser(final FileChooser fileChooser, Collection<String> allowedExtensions) {
        fileChooser.setTitle("File Selector");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        List<FileChooser.ExtensionFilter> filters = new ArrayList<>();
        String[] allowed = new String[allowedExtensions.size()];
        int i = 0;
        for (String extension : allowedExtensions) {
            allowed[i] = extension;
            filters.add(new FileChooser.ExtensionFilter(extension, extension));
            i++;
        }
        filters.add(new FileChooser.ExtensionFilter("All", allowed));
        filters.sort((o1, o2) -> {
            if (o1.getDescription().equals("All")) return -1;
            if (o2.getDescription().equals("All")) return 1;
            return 0;
        });
        fileChooser.getExtensionFilters().addAll(filters);
    }


}
