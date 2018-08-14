package odms.commons.config;

import java.util.Properties;

/**
 * singleton that holds the config properties through a single session of the app. these can be empty or read in from a filepath
 * e.g. clientConfig.properties
 */
public class ConfigPropertiesSession {
    private static ConfigPropertiesSession session;
    private Properties properties;


    private ConfigPropertiesSession() {
        this.properties = new Properties();
    }

    /**
     * @return current instance of the session. if there is not one, an empty session will be given.
     */
    public static ConfigPropertiesSession getInstance() {
        if (session == null) {
            session = new ConfigPropertiesSession();
        }
        return session;
    }

    public static void setInstance(ConfigPropertiesSession session1) {
        session = session1;
    }

    public void loadFromFile(String filepath) {
        this.properties = new ConfigPropertiesLoader().loadConfig(filepath);
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

    public Properties getProperties() {
        return properties;
    }
}