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

//    /**
//     * Opens a file selector
//     *
//     * @param stage stage to open file selector on
//     * @return the file path of the file
//     */
//    public static String getFileSelector(final Stage stage, Collection<String> extensions) {
//        stage.setTitle("File Selector");
//
//        final FileChooser fileChooser = new FileChooser();
//        configureFileChooser(fileChooser, extensions);
//        File file = fileChooser.showOpenDialog(stage);
//        if (file != null) {
//            return file.getAbsolutePath();
//        } else {
//            return null;
//        }
//
//    }
//
//    /**
//     * Customizes configuration of a file chooser
//     *
//     * @param fileChooser fileChooser to configure
//     */
//    private static void configureFileChooser(final FileChooser fileChooser, Collection<String> allowedExtensions) {
//        fileChooser.setTitle("File Selector");
//        fileChooser.setInitialDirectory(
//                new File(System.getProperty("user.home"))
//        );
//        List<FileChooser.ExtensionFilter> filters = new ArrayList<>();
//        for (String extension : allowedExtensions) {
//            filters.add(new FileChooser.ExtensionFilter(extension, extension));
//        }
//        fileChooser.getExtensionFilters().addAll(filters);
//    }


}
