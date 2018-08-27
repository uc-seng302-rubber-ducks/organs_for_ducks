package odms.utils;

import javafx.application.Application;
import odms.commons.config.ConfigPropertiesSession;
import odms.controller.AppController;
import odms.socket.OdmsSocketHandler;
import org.junit.Before;
import org.junit.Test;
import utils.AppConfigurator;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class AppConfiguratorTest {

    private AppConfigurator configurator;
    private ConfigPropertiesSession session;
    private AppController controller;

    @Before
    public void setUp() {
        session = mock(ConfigPropertiesSession.class);
        controller = mock(AppController.class);
        configurator = new AppConfigurator(session, controller);
    }

    @Test
    public void setupArgumentsShouldAddToSession() {
        Application.Parameters testParams = mock(Application.Parameters.class);
        Map<String, String> namedParams = new HashMap<>();
        namedParams.put("test", "value");
        namedParams.put("hello", "there");
        when(testParams.getNamed()).thenReturn(namedParams);

        configurator.setupArguments(testParams);

        verify(session, times(1)).setProperty("test", "value");
        verify(session, times(1)).setProperty("hello", "there");
    }

    @Test
    public void webSocketShouldNotStartWhenTestConfigFlagPresent() {
        when(session.getProperty(eq("testConfig"), anyString())).thenReturn("true");

        configurator.setupWebsocket();

        verify(controller, never()).getSocketHandler();
    }

    @Test
    public void webSocketShouldStartWhenNoTestFlagPresent() {
        when(controller.getSocketHandler()).thenReturn(mock(OdmsSocketHandler.class));
        when(session.getProperty(eq("testConfig"), eq("false"))).thenReturn("false");

        configurator.setupWebsocket();

        verify(controller, times(1)).getSocketHandler();
    }

}
