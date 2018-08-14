package odms.commons.config;

import odms.commons.utils.Log;

import java.io.IOException;
import java.util.Properties;

public class ConfigPropertiesLoader {

    /**
     * attempts to load the config file into a properties object. returns empty Properies if unable to do so
     *
     * @param filename name of the file in the resources folder
     * @return Properties object that may or may not contain info from the config file
     */
    public Properties loadConfig(String filename) {
        Properties prop = new Properties();
        try {
            prop.load(this.getClass().getClassLoader().getResourceAsStream(filename));
        } catch (IOException | NullPointerException ex) {
            Log.severe("failed to load config file: " + filename, ex);
        }
        return prop;
    }
}
