package seng302.Controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import seng302.App;

import java.util.concurrent.TimeoutException;

public class DonorViewControllerTest extends ApplicationTest {
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
