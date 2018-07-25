package odms.commons.utils;


import odms.commons.model._enum.Environments;

import java.util.Properties;

public class ConfigFileLoader {

    Properties prop;

    public ConfigFileLoader() {
        prop = new Properties();
    }

    public void loadConfig(Environments env) {
        if (env.equals(Environments.CLIENT)) {

        } else if (env.equals(Environments.SERVER)) {

        }
    }
}
