package seng302.Controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import seng302.App;

import java.util.concurrent.TimeoutException;

public class UpdateDonorControllerTest extends ApplicationTest {
    @Before
    public void setUpCreateScene() throws TimeoutException {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(App.class);
        AppController.getInstance().getUsers().clear();
    }

    @After
    public void tearDown() {
        AppController.getInstance().getUsers().clear();
    }

    @Test
    public void testUpdateName() {

    }

    @Test
    public void testCancel() {

    }

    @Test
    public void testUpdateNothing() {

    }

    @Test
    public void testUpdateDoB() {

    }

    @Test
    public void testUpdateDoD() {

    }

    @Test
    public void testUpdateAddress() {

    }

    @Test
    public void testUpdateHomePhone() {

    }

    @Test
    public void testUpdateCellPhone() {

    }

    @Test
    public void testUpdateEmail() {

    }

    @Test
    public void testUpdateRegion() {

    }

    @Test
    public void testUpdateBloodType() {

    }

    @Test
    public void testUpdateSmokerStatus() {

    }

    @Test
    public void testUpdateAlcoholConsumption() {

    }

    @Test
    public void testUpdateHeight() {

    }

    @Test
    public void testUpdateWeight() {

    }

    @Test
    public void updateBMIAfterUpdate() {

    }

}
