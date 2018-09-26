package utils;

import javafx.scene.image.Image;
import javafx.stage.Stage;
import odms.commons.config.ConfigPropertiesSession;
import odms.commons.utils.Log;

import java.io.IOException;
import java.net.URL;

/**
 * Loads the taskbar image for a stage to use in it's icons list
 */
public class StageIconLoader {

    /**
     * Gets the logo image and
     * @return the Image object
     */
    public Image getIconImage() {
        if (!ConfigPropertiesSession.getInstance().getProperty("testConfig", "false").equalsIgnoreCase("true")) {
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
        } else {
            return null;
        }
    }

    /**
     * Takes a stage and adds the logo to it's task-bar icons
     * @param stage to add the icon to
     * @return to return with the task bar icon added
     */
    public Stage addStageIcon(Stage stage) {
        if (!ConfigPropertiesSession.getInstance().getProperty("testConfig", "false").equalsIgnoreCase("true")) {
            Image image = getIconImage();
            if (image == null) {
                return stage;
            } else {
                stage.getIcons().add(image);
                return stage;
            }
        } else {
            return stage;
        }
    }
}
