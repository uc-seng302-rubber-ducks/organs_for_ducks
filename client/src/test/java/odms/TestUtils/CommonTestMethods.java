package odms.TestUtils;

import odms.commons.config.ConfigPropertiesSession;

public class CommonTestMethods {
    public static void runMethods() {
        runHeadless();
        runTestMode();
    }

    private static void runHeadless() {
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("prism.order", "sw");
        System.setProperty("prism.text", "t2k");
        System.setProperty("java.awt.headless", "true");
        System.setProperty("headless.geometry", "1920x1440-32");
    }

    private static void runTestMode() {
        ConfigPropertiesSession.setInstance(null);
        ConfigPropertiesSession.getInstance().setProperty("testConfig", "true");
    }
}
