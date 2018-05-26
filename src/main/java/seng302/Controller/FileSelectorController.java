package seng302.Controller;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * Class information found from
 * https://docs.oracle.com/javafx/2/ui_controls/file-chooser.htm
 */

public final class FileSelectorController {

    /**
     * Opens a file selector
     *
     * @param stage stage to open file selector on
     * @return the file path of the file
     */
    public static String getFileSelector(final Stage stage) {
        stage.setTitle("File Selector");

        final FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);
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
     * @param fileChooser fileChooser to configure
     */
    private static void configureFileChooser(
            final FileChooser fileChooser) {
        fileChooser.setTitle("File Selector");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON", "*.json")
        );
    }


}
