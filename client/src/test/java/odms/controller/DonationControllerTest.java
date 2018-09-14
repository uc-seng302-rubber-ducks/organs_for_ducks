package odms.controller;

import odms.TestUtils.AppControllerMocker;
import odms.commons.model.User;
import odms.commons.utils.Log;
import odms.controller.gui.panel.DonationTabPageController;
import odms.controller.gui.window.UserController;
import org.junit.Before;
import org.junit.BeforeClass;

import java.lang.reflect.Method;

import static org.mockito.Mockito.mock;

public class DonationControllerTest {

    private DonationTabPageController donationController;
    private Method toggleDisqualifiedExpired;

    @BeforeClass
    public void setUp() {
        Class[] cArgs = new Class[1];
        cArgs[0] = boolean.class;
        donationController = mock(DonationTabPageController.class);
        AppController appController = AppControllerMocker.getFullMock();
        UserController parent = mock(UserController.class);
        User user = new User();
        donationController.init(appController, user, parent);
        try {
            toggleDisqualifiedExpired = donationController.getClass().getDeclaredMethod("toggleDisqualifiedExpired", cArgs);
            toggleDisqualifiedExpired.setAccessible(true);



        } catch (NoSuchMethodException m) {
            Log.severe("Method for reflection was not found", m);
        }

    }


    @Before
    public void before() {
    }

//    I felt like it was not worth it to do all this work for a couple of private methods
//    @Test
//    public void test() {
////        Class[] arguments = new Class[1];
////        arguments[0] = boolean.class;
//        try {
//            toggleDisqualifiedExpired.invoke(donationController, true);
//        } catch (IllegalAccessException | InvocationTargetException e) {
//            Log.severe("Reflected method threw errorseng", e);
//        }
//
//    }

}
