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
    private AppController controller;

    public AppConfigurator(ConfigPropertiesSession session, AppController controller) {
        this.session = session;
        this.controller = controller;
    }

    /**
     * takes application and command line args and adds them to the ConfigPropertiesSession.
     * If --testConfig=true is found, will run the app with
     *
     * @param parameters launch params used by Application including command line args.
     *                   Application.getParameters()
     */
    public void setupArguments(Application.Parameters parameters) {
        for (Map.Entry<String, String> param : parameters.getNamed().entrySet()) {
            session.setProperty(param.getKey(), param.getValue());
            Log.info("PARAM: " + param.getKey() + " -> " + param.getValue());
        }
    }

    /**
     * sets up the websocket to listen to server.websocket.url
     * if --testConfig=true is found, this will be skipped
     */
    public void setupWebsocket() {
        String testConfigProperty = session.getProperty("testConfig", "false"); //defaults to "false" if it's not found
        if (testConfigProperty.equalsIgnoreCase("true")) {
            ;
            return;
        }
        controller.getSocketHandler().start(session.getProperty("server.websocket.url", "ws://localhost:4941/websocket"));

    }

    /**
     * sets up logging to the correct environment
     */
    public void setupLogging(Environments env) {
        Log.setup(env);
    }
}
