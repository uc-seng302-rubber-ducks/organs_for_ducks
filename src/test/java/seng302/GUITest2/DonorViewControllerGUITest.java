package seng302.GUITest2;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import seng302.App;
import seng302.Utils.CommonTestMethods;
import seng302.controller.AppController;

import java.util.concurrent.TimeoutException;

public class DonorViewControllerGUITest extends ApplicationTest {

    @BeforeClass
    public static void initialization() {
        CommonTestMethods.runHeadless();
    }

    @Before
    public void setUpCreateScene() throws TimeoutException {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);
        AppController.getInstance().getUsers().clear();
    }

    @After
    public void tearDown() throws TimeoutException {
        AppController.getInstance().getUsers().clear();
        FxToolkit.cleanupStages();
    }

    @Test
    public void testBMIShown() {

    }

    @Test
    public void testCalculatedAge() {

    }

    @Test
    public void testRegisterOrgans() {

    }

    @Test
    public void testAddMiscAttributes() {

    }

    @Test
    public void testShownHistory() {

    }

    @Test
    public void testAgeOfDeath() {

    }

}
