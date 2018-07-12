package seng302.steps;

import odms.commons.model.CacheManager;
import odms.commons.model.MedicationInteractionCache;
import odms.commons.utils.HttpRequester;
import odms.controller.AppController;
import okhttp3.OkHttpClient;

/**
 * This class is used as a data storage/state storage class so that the same instances of items can be propagated through to the GivenSteps, WhenSteps
 * and ThenSteps classes
 */
public class CucumberTestModel {

    private static AppController controller = AppController.getInstance();
    private static String userNhi;
    private static boolean isClinicianLogin;
    private static CacheManager cacheManager = CacheManager.getInstance();
    private static MedicationInteractionCache medicationInteractionCache = cacheManager.getInteractionCache();
    private static HttpRequester httpRequester = new HttpRequester(new OkHttpClient());

    public static HttpRequester getHttpRequester() {
        return httpRequester;
    }

    public static void setHttpRequester(HttpRequester httpRequester) {
        CucumberTestModel.httpRequester = httpRequester;
    }

    public static MedicationInteractionCache getMedicationInteractionCache() {
        return medicationInteractionCache;
    }

    public static void setMedicationInteractionCache(MedicationInteractionCache medicationInteractionCache) {
        CucumberTestModel.medicationInteractionCache = medicationInteractionCache;
    }

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
