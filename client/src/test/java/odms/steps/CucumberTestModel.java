package odms.steps;

import odms.bridge.*;
import odms.commons.model.CacheManager;
import odms.commons.model.MedicationInteractionCache;
import odms.commons.model.User;
import odms.commons.utils.HttpRequester;
import odms.controller.AppController;
import okhttp3.OkHttpClient;

import static org.mockito.Mockito.mock;

/**
 * This class is used as a data storage/state storage class so that the same instances of items can be propagated through to the GivenSteps, WhenSteps
 * and ThenSteps classes
 */
public class CucumberTestModel {

    private static AppController controller = AppController.getInstance();
    private static UserBridge userBridge = mock(UserBridge.class);
    private static ClinicianBridge clinicianBridge = mock(ClinicianBridge.class);
    private static AdministratorBridge administratorBridge = mock(AdministratorBridge.class);
    private static LoginBridge loginBridge = mock(LoginBridge.class);
    private static CountriesBridge countriesBridge = mock(CountriesBridge.class);
    private static TransplantBridge transplantBridge = mock(TransplantBridge.class);
    private static OrgansBridge organsBridge = mock(OrgansBridge.class);
    private static User user;
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
        controller.setUserBridge(userBridge);
        controller.setClinicianBridge(clinicianBridge);
        controller.setAdministratorBridge(administratorBridge);
        controller.setLoginBridge(loginBridge);
        controller.setTransplantBridge(transplantBridge);
        controller.setCountriesBridge(countriesBridge);
        controller.setOrgansBridge(organsBridge);
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

    public static UserBridge getUserBridge() {
        return userBridge;
    }

    public static void setUserBridge(UserBridge userBridge) {
        CucumberTestModel.userBridge = userBridge;
    }

    public static ClinicianBridge getClinicianBridge() {
        return clinicianBridge;
    }

    public static void setClinicianBridge(ClinicianBridge clinicianBridge) {
        CucumberTestModel.clinicianBridge = clinicianBridge;
    }

    public static AdministratorBridge getAdministratorBridge() {
        return administratorBridge;
    }

    public static void setAdministratorBridge(AdministratorBridge administratorBridge) {
        CucumberTestModel.administratorBridge = administratorBridge;
    }

    public static LoginBridge getLoginBridge() {
        return loginBridge;
    }

    public static void setLoginBridge(LoginBridge loginBridge) {
        CucumberTestModel.loginBridge = loginBridge;
    }

    public static TransplantBridge getTransplantBridge() {
        return transplantBridge;
    }

    public static void setTransplantBridge(TransplantBridge transplantBridge) {
        CucumberTestModel.transplantBridge = transplantBridge;
    }

    public static void setUser(User user) {
        CucumberTestModel.user = user;
    }

    public static User getUser() {
        return user;
    }
}
