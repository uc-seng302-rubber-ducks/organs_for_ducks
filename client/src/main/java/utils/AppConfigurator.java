package utils;

import javafx.application.Application;
import odms.commons.config.ConfigPropertiesSession;
import odms.commons.model._enum.Environments;
import odms.commons.utils.Log;
import odms.controller.AppController;

import java.util.Map;

/**
 * class to handle commandline args and environment setupArguments
 */
public class AppConfigurator {

    private ConfigPropertiesSession session;

    public AppConfigurator(ConfigPropertiesSession session) {
        this.session = session;
    }

    /**
     * takes arguments from the given Parameters (command line args) and optionally, a file.
     * The command line args will take priority if there are any conflicts.
     *
     * @param parameters launch params used by Application including command line args.
     * @param filename config file name to get initial parameters from. can be null if no file exists/is needed
     */
    public void setupArguments(Application.Parameters parameters, String filename) {
        if (filename != null) {
            session.loadFromFile(filename);
        }
        for (Map.Entry<String, String> param : parameters.getNamed().entrySet()) {
            session.setProperty(param.getKey(), param.getValue());
            Log.info("PARAM: " + param.getKey() + " -> " + param.getValue());
        }
    }

    /**
     * sets up the websocket to listen to server.websocket.url
     * if --testConfig=true is found, this will be skipped
     * @param controller AppController instance used as an entry point to the correct socket handler
     */
    public void setupWebsocket(AppController controller) {
        if (isTestConfig()) {
            return;
        }
        controller.getSocketHandler().start(session.getProperty("server.websocket.url"));

    }

    /**
     * sets up logging to the correct environment
     */
    public void setupLogging(Environments env) {
        Log.setup(env);
    }


    /**
     * checks if the testConfig parameter exists and if it is true
     *
     * @return true if testConfig=true
     */
    private boolean isTestConfig() {
        String testConfigProperty = session.getProperty("testConfig", "false"); //defaults to "false" if it's not found
        return testConfigProperty.equalsIgnoreCase("true");
    }
}
