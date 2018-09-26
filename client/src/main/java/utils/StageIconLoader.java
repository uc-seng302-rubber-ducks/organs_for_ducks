package utils;

import javafx.scene.image.Image;
import javafx.stage.Stage;
import odms.commons.utils.Log;

import java.io.IOException;
import java.net.URL;

public class StageIconLoader {

    public void setStageIcon(Stage stage) {
        URL url = getClass().getResource("/logos/LoveDuck.png");
//        URL url = getClass().getResource("/logos/HeartDuck.png");
        if (url == null) {
            Log.warning("Could not load the icon for the taskbar. Check that the filepath is correct");
        } else {
            try {
                javafx.scene.image.Image image = new Image(url.openStream());
                stage.getIcons().add(image);
            } catch (IOException io) {
                Log.severe("Could not load the image from the filepath", io);
            }
        }
    }
}
