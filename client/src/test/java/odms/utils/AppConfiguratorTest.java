package odms.utils;

import odms.commons.config.ConfigPropertiesSession;
import odms.controller.AppController;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import utils.AppConfigurator;

import static org.mockito.Mockito.mock;

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
        Assert.fail("not yet implemented");
    }

    @Test
    public void webSocketShouldNotStartWhenTestConfigFlagPresent() {
        Assert.fail("not yet implemented");
    }

    @Test
    public void webSocketShouldStartWhenNoTestFlagPresent() {
        Assert.fail("not yet implemented");
    }


}
