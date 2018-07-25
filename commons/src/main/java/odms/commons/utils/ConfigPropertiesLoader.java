package odms.commons.utils;

import java.io.IOException;
import java.util.Properties;

public class ConfigPropertiesLoader {

    private Properties prop;

    public ConfigPropertiesLoader() {
        prop = new Properties();
    }

    /**
     * attempts to load the config file into a properties object. returns empty Properies if unable to do so
     *
     * @param filename name of the file in the resources folder
     * @return Properties object that may or may not contain info from the config file
     */
    public Properties loadConfig(String filename) {
        try {
            prop.load(this.getClass().getClassLoader().getResourceAsStream(filename));
        } catch (IOException ex) {
            Log.severe("failed to load config file: " + filename, ex);
        }
        return prop;
    }
}
