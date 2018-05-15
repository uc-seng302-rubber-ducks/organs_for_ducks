package seng302.Controller;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;

/**
 * Class information found from
 * https://docs.oracle.com/javafx/2/ui_controls/file-chooser.htm
 */

public final class FileSelectorController {

    private Desktop desktop = Desktop.getDesktop();


    public static String getFileSelector(final Stage stage) {
        stage.setTitle("File Selector");

        final FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);
        File file = fileChooser.showOpenDialog(stage);
        if(file != null) {
            String filename = file.getAbsolutePath().toString();
            return filename;
        } else {
            return null;
        }

    }

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
