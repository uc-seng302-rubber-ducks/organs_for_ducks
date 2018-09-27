package utils;

import javafx.scene.image.Image;
import odms.commons.utils.Log;

import java.io.IOException;
import java.net.URL;

/**
 * Loads the taskbar image for a stage to use in it's icons list
 */
public class StageIconLoader {

    /**
     * Loads the task-bar image
     * @return the Image object
     */
    public Image getIconImage() {
        URL url = getClass().getResource("/logos/LoveDuck.png");
        if (url == null) {
            Log.warning("Could not load the icon for the taskbar. Check that the filepath is correct");
            return null;
        } else {
            try {
                return new Image(url.openStream());
            } catch (IOException io) {
                Log.severe("Could not load the image from the filepath", io);
                return null;
            }
        }
    }
}
