package odms.commons.config;

import java.util.Properties;

/**
 * singleton that holds the config properties through a single session of the app. these can be empty or read in from a filepath
 * e.g. clientConfig.properties
 */
public class ConfigPropertiesSession {
    private static ConfigPropertiesSession session;
    private Properties properties;


    private ConfigPropertiesSession(String filepath) {
        if (filepath != null) {
            this.properties = new ConfigPropertiesLoader().loadConfig(filepath);
        } else {
            this.properties = new Properties();
        }
    }

    /**
     * forcibly sets the session to have new properties loaded from the given filepath. existing data will be overwritten/wiped.
     *
     * @param filepath relative location of the file (from the resources folder in the current package)
     */
    public static ConfigPropertiesSession init(String filepath) {
        session = new ConfigPropertiesSession(filepath);
        return session;
    }

    /**
     * @return current instance of the session. if there is not one, an empty session will be given.
     */
    public static ConfigPropertiesSession getInstance() {
        if (session == null) {
            session = new ConfigPropertiesSession(null);
        }
        return session;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }
}