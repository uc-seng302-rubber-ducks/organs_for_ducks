package odms.TestUtils;

import odms.bridge.ClinicianBridge;
import odms.bridge.LoginBridge;
import odms.bridge.TransplantBridge;
import odms.bridge.UserBridge;
import odms.controller.AppController;
import odms.socket.OdmsSocketHandler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AppControllerMocker {

    /**
     * helper method that will mock appController and all the nested bridges, socket handler, etc so that the appController.get___ methods are pre-filled
     * Any of these mocks that are to be used in the test should be overwritten
     * @return full mock AppController
     */
    public static AppController getFullMock() {
        AppController application = mock(AppController.class);
        UserBridge userBridge = mock(UserBridge.class);
        ClinicianBridge clinicianBridge = mock(ClinicianBridge.class);
        LoginBridge loginBridge = mock(LoginBridge.class);
        TransplantBridge transplantBridge = mock(TransplantBridge.class);
        OdmsSocketHandler socketHandler = mock(OdmsSocketHandler.class);


        when(application.getSocketHandler()).thenReturn(socketHandler);
        when(application.getUserBridge()).thenReturn(userBridge);
        when(application.getClinicianBridge()).thenReturn(clinicianBridge);
        when(application.getLoginBridge()).thenReturn(loginBridge);
        when(application.getTransplantBridge()).thenReturn(transplantBridge);
        when(application.getSocketHandler()).thenReturn(socketHandler);
        when(application.getToken()).thenReturn("tokenGoesHere");

        return application;
    }
}