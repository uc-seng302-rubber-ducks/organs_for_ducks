package seng302.controller.gui;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * Class information found from
 * https://docs.oracle.com/javafx/2/ui_controls/file-chooser.htm
 */

public final class FileSelectorController {

    /**
     * Opens a file selector and configure it to select
     * JSON files only.
     *
     * @param stage stage to open file selector on
     * @return the file path of the file
     */
    public static String getJsonFileSelector(final Stage stage) {
        stage.setTitle("File Selector");

        final FileChooser fileChooser = new FileChooser();
        configureJsonFileChooser(fileChooser);
        return getFile(fileChooser, stage);
    }

    /**
     * Opens a file selector and configure it to select
     * image files only.
     *
     * @param stage stage to open file selector on
     * @return the file path of the file
     */
    public static String getImageFileSelector(final Stage stage) {
        stage.setTitle("File Selector");

        final FileChooser fileChooser = new FileChooser();
        configureImageFileChooser(fileChooser);
        return getFile(fileChooser, stage);
    }

    /**
     * Opens a file selector
     * @param fileChooser initialised file chooser
     * @param stage stage to open file selector on
     * @return the file path of the file
     */
    private static String getFile(FileChooser fileChooser, Stage stage){
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            return file.getAbsolutePath();
        } else {
            return null;
        }
    }

    /**
     * Customizes configuration of a JSON file chooser
     *
     * @param fileChooser fileChooser to configure
     */
    private static void configureJsonFileChooser(
            final FileChooser fileChooser) {
        fileChooser.setTitle("File Selector");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON", "*.json")
        );
    }

    /**
     * Customizes configuration of a image file chooser
     *
     * @param fileChooser fileChooser to configure
     */
    private static void configureImageFileChooser(
            final FileChooser fileChooser) {
        fileChooser.setTitle("File Selector");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
    }


}
