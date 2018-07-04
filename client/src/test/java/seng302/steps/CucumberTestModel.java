package seng302.steps;

import odms.controller.AppController;

/**
 * This class is used as a data storage/state storage class so that the same instances of items can be propagated through to the GivenSteps, WhenSteps
 * and ThenSteps classes
 */
public class CucumberTestModel {

    private static AppController controller = AppController.getInstance();
    private static String userNhi;
    private static boolean isClinicianLogin;

    public static AppController getController() {
        return controller;
    }

    public static void setUserNhi(String nhi) {
        userNhi = nhi;
    }

    public static String getUserNhi() {
        return userNhi;
    }

    public static boolean isClinicianLogin() {
        return isClinicianLogin;
    }

    public static void setIsClinicianLogin(boolean isClinician) {
        isClinicianLogin = isClinician;
    }
}
