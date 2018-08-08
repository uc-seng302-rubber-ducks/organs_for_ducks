package odms.controller;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import odms.bridge.*;
import odms.commons.exception.ApiException;
import odms.commons.exception.ProfileAlreadyExistsException;
import odms.commons.exception.ProfileNotFoundException;
import odms.commons.model.Administrator;
import odms.commons.model.Change;
import odms.commons.model.Clinician;
import odms.commons.model.User;
import odms.commons.model._enum.Directory;
import odms.commons.model._enum.Regions;
import odms.commons.model.datamodel.TransplantDetails;
import odms.commons.model.dto.UserOverview;
import odms.commons.utils.DataHandler;
import odms.commons.utils.JsonHandler;
import odms.commons.utils.Log;
import odms.controller.gui.StatusBarController;
import odms.controller.gui.window.AdministratorViewController;
import odms.controller.gui.window.ClinicianController;
import odms.controller.gui.window.UserController;
import odms.socket.OdmsSocketHandler;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;


/**
 * Class for the functionality of the main app
 */
public class AppController {


    private static final String USERS_FILE = Directory.JSON.directory() + "/users.json";
    private static final String CLINICIAN_FILE = Directory.JSON.directory() + "/clinicians.json";
    private static final String ADMIN_FILE = Directory.JSON.directory() + "/administrators.json";
    private static AppController controller;
    private Collection<Administrator> admins = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private ArrayList<TransplantDetails> transplantList = new ArrayList<>();
    private List<Clinician> clinicians = new CopyOnWriteArrayList<>();
    private Set<UserOverview> overviews = new CopyOnWriteArraySet<>();
    private ArrayList<String[]> historyOfCommands = new ArrayList<>();
    private List<String> allCountries;
    private List<String> allowedCountries; //store the countries chosen by admin
    private List<String> allNZRegion;
    private int historyPointer = 0;
    private DataHandler dataHandler = new JsonHandler();
    private OkHttpClient client = new OkHttpClient();
    private UserBridge userBridge = new UserBridge(client);
    private ClinicianBridge clinicianBridge = new ClinicianBridge(client);
    private AdministratorBridge administratorBridge = new AdministratorBridge(client);
    private LoginBridge loginBridge = new LoginBridge(client);
    private TransplantBridge transplantBridge = new TransplantBridge(client);
    private UserController userController = null;
    private ClinicianController clinicianController = null;
    private CountriesBridge countriesBridge = new CountriesBridge(client);
    private AdministratorViewController administratorViewController = null;
    private StatusBarController statusBarController = new StatusBarController();
    private Stack<User> redoStack = new Stack<>();
    private String token;
    private SQLBridge sqlBridge = new SQLBridge(client);
    private OdmsSocketHandler socketHandler = new OdmsSocketHandler(client);

    /**
     * Creates new instance of AppController
     */
    private AppController() {

        String[] empty = {""};
        historyOfCommands.add(empty);//putting an empty string into the string array to be displayed if history pointer is 0


        this.allCountries = generateAllCountries();
        generateAllNZRegion();
    }

    /**
     * Returns the instance of the controller
     *
     * @return AppController
     */
    public static AppController getInstance() {
        if (controller == null) {
            controller = new AppController();
        }
        return controller;
    }

    /**
     * Never use this unless testing. Please.
     * After the tests, make sure you reset it using setInstance(null)
     *
     * @param appController controller instance to return
     */
    public static void setInstance(AppController appController) {
        controller = appController;
    }

    public TransplantBridge getTransplantBridge() {
        return transplantBridge;
    }
    /**
     * If New Zealand is selected at the country combo box, the region combo box will appear.
     * If country other than New Zealand is selected at the country combo box, the region combo box will
     * be replaced with a text field.
     * region text field is cleared by default when it appears.
     * region combo box selects the first item by default when it appears.
     * @param countrySelector Combo Box
     * @param regionSelector Combo Box
     * @param regionInput Text Field
     */
    public void countrySelectorEventHandler(ComboBox countrySelector, ComboBox regionSelector, TextField regionInput){
        if(! countrySelector.getSelectionModel().getSelectedItem().equals("New Zealand")) {
            regionSelector.setVisible(false);
            regionInput.setVisible(true);
            //TODO: if the following line is removed, update javadoc of this method and all its callers. -14 july
            regionInput.clear(); //TODO: redo stack for region is cleared when region input is cleared. try undo redo when selecting nz as country and selecting other countries + selecting/entering region. -14 july

        } else {
            regionSelector.setVisible(true);
            regionSelector.setValue("");
            regionInput.setVisible(false);
        }
    }


    /**
     * create a list of all country names.
     */
    private List<String> generateAllCountries() {
        List allCountry = new ArrayList<>();
        String[] locales = Locale.getISOCountries();

        for (String countryCode : locales) {
            Locale obj = new Locale("", countryCode);
            allCountry.add(obj.getDisplayCountry());
        }
        allCountry.sort(String.CASE_INSENSITIVE_ORDER);
        return allCountry;
    }

    /**
     *
     * @return unmodifiable collection of all country names
     */
    public List<String> getAllCountries() {
        return allCountries;
    }

    public List<String> getAllowedCountries() {
        Set s = null;
        try {
            s = getCountriesBridge().getAllowedCountries();
        } catch (IOException e) {
            Log.severe("Database threw IOE", e);
            allowedCountries = new ArrayList<>();
        }
        if (s != null) {
            allowedCountries = new ArrayList(s);
        }
        if (allowedCountries.isEmpty()) {
            allowedCountries.add("New Zealand");
        }
        allowedCountries.sort(String.CASE_INSENSITIVE_ORDER);
        return allowedCountries;
    }

    public void setAllowedCountries(List<String> allowedCountries) {
        this.allowedCountries = allowedCountries;
        countriesBridge.putAllowedCountries(new HashSet<>(allowedCountries), token);
    }

    /**
     * create a list of all New Zealand Region names.
     */
    private void generateAllNZRegion(){
        allNZRegion = new ArrayList<>();
        Arrays.asList(Regions.values()).forEach(region -> allNZRegion.add(region.toString()));
        allNZRegion.sort(String.CASE_INSENSITIVE_ORDER);
    }

    /**
     *
     * @return unmodifiable collection of all New Zealand region names
     */
    public List<String> getAllNZRegion() {
        return Collections.unmodifiableList(allNZRegion);
    }


    /**
     * Sets the point in history
     */
    public void setHistoryPointer() {
        this.historyPointer = historyOfCommands.size();
    }

    /**
     * Adds an executed command to the command history
     *
     * @param command Command to be added
     */
    public void addToHistoryOfCommands(String[] command) {
        historyOfCommands.add(command);
    }

    /**
     * Updates the history pointer to ensure that the end of the array isnt overrun and the number
     * stays positive so the history pointer doenst overrun.
     *
     * @param amount -1 for older commands 1 for newer commands
     */
    public void historyPointerUpdate(int amount) {
        if (historyPointer + amount <= 0) {
            historyPointer = 0;
        } else if (historyPointer + amount > historyOfCommands.size()) {
            historyPointer = historyOfCommands.size();
        }
    }

    /**
     * When called queries the history pointer and acquires the command located at the appropriate
     * point
     *
     * @return A string array of the command history.
     */
    public String[] getCommand() {
        return historyOfCommands.get(historyPointer);
    }

    /**
     * Takes a users name and dob, finds the user in the session list and returns them.
     *
     * @param name Name of the user
     * @param dob  date of birth of the user
     * @return The user that matches the name and dob, otherwise null if no user was found.
     */
    //TODO: Make this redundant
    public User findUser(String name, LocalDate dob) {
        User toReturn = new User();
        for (User user : users) {
            if (user.getFullName().equals(name) && user.getDateOfBirth().equals(dob)) {
                toReturn = user;
            }
        }
        return toReturn;
    }

    /**
     * finds all users who's name field contains the search string
     *
     * @param name The name of the user
     * @return an array list of users.
     */
    public List<User> findUsers(String name) {
        ArrayList<User> toReturn = new ArrayList<>();
        for (User user : users) {
            if (user.getFullName().toLowerCase().contains(name.toLowerCase()) && !user.isDeleted()) {
                toReturn.add(user);
            }
        }
        return toReturn;
    }

    /**
     * Finds a single user by nhi
     *
     * @param nhi The unique identifier of a user (national health index)
     * @return The user with the matching nhi, or null if no user matches.
     */
    public User findUser(String nhi) {
        User user = null;
        try {
            user = getUserBridge().getUser(nhi);
        } catch (IOException e) {
            Log.warning("Failed to get user " + nhi);
        }
        return user;
    }

    /**
     * takes a passed user and removes them from the maintained list of users
     *
     * @param user user to remove
     */
    public void deleteUser(User user) {
        user.setDeleted(true);

        getUserBridge().deleteUser(user);
    }

    public List<User> getUsers() {
        return users;
    }

    public void addUserOverview(UserOverview overview) {
        this.overviews.add(overview);
        if (clinicianController != null) {
            clinicianController.refreshTables();
        }
        if (administratorViewController != null) {
            administratorViewController.refreshTables();
        }
    }

    /**
     * @param users An array list of users.
     */
    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    /**
     * Method to update the user of any changes passed in by the gui. Removes the old entry of the
     * user from the list and then adds the updated entry If the user is not already in the list it is
     * added
     * <p>
     *
     * @param user user to be updated/added
     */
    public void update(User user) {
        ArrayList<Change> changelogWrite = new ArrayList<>();
        if (users.contains(user)) {
            users.remove(user);
            users.add(user);
        } else {
            users.add(user);
            changelogWrite.add(new Change(LocalDateTime.now(), "Added user " + user.getFullName()));
        }
    }

    /**
     * Saves the current list of users to the json
     *
     * @param user User to be saved
     */
    public void saveUser(User user) {
        try {
            User originalUser;
            if (!user.getUndoStack().isEmpty()) {
                originalUser = user.getUndoStack().firstElement().getState();
            } else {
                originalUser = user;
            }

            if (userBridge.getExists(originalUser.getNhi())) {
                userBridge.putUser(user, originalUser.getNhi());
                Thread.sleep(100);
                userBridge.putProfilePicture(user.getNhi(), user.getProfilePhotoFilePath());

            } else {
                userBridge.postUser(user);
            }
        } catch (IOException e) {
            Log.warning("Could not save user " + user.getNhi(), e);
        } catch (InterruptedException e) {
            Log.warning("Thread sleep time was interrupted", e);
        }
    }

    /**
     * adds a user to the users list
     *
     * @param user user to be added
     * @return if the user was added
     */
    public boolean addUser(User user) {
        return users.add(user);
    }

    /**
     * adds a clinician to the clinicians
     *
     * @param clinician clinician to be added
     */
    public void addClinician(Clinician clinician) {
        clinicians.add(clinician);
        if (administratorViewController != null) {
            administratorViewController.refreshTables();
        }
    }

    /**
     * adds a administrator ti the administrators
     *
     * @param administrator administrator to be added
     */
    public void addAdmin(Administrator administrator) {
        admins.add(administrator);
        if (administratorViewController != null) {
            administratorViewController.refreshTables();
        }
    }

    public List<Clinician> getClinicians() {
        return clinicians;
    }

    public void setClinicians(List<Clinician> clinicians) {
        this.clinicians = clinicians;
    }

    public Collection<Administrator> getAdmins() {
        return admins;
    }

    /**
     * Retrieves the specified clinician from the database
     *
     * @param id The staff id (unique identifier) of the clinician
     * @return The clinician that matches the given staff id, or null if no clinician matches.
     */
    public Clinician getClinician(String id) {
        try {
            return getClinicianBridge().getClinician(id, getToken());
        } catch (ApiException ex) {
            Log.warning("Error while trying to retrieve clinician " + id + " status "+ex.getResponseCode(), ex);
        }
        return null;
    }

    /**
     * Refreshes the list of clinicians with the updated clone
     *
     * @param clinician The current clinician.
     */
    public void updateClinicians(Clinician clinician) {
        if (clinicians.contains(clinician)) {
            clinicians.remove(clinician);
            clinicians.add(clinician);
        } else {
            clinicians.add(clinician);
        }
    }

    /**
     * Gets the original clinician before any changes were made and searches the database for the entry.
     * The clinician is saved to the database by a put request if the entry is found, otherwise by a post request.
     *
     * @param clinician Clinician to be saved
     */
    public void saveClinician(Clinician clinician) throws IOException {
            Clinician originalClinician;
            if (!clinician.getUndoStack().isEmpty()) {
                originalClinician = clinician.getUndoStack().firstElement().getState();
            } else {
                originalClinician = clinician;
            }

            if (clinicianBridge.getExists(originalClinician.getStaffId())) {
                clinicianBridge.putProfilePicture(originalClinician.getStaffId(), getToken(), clinician.getProfilePhotoFilePath());
                clinicianBridge.putClinician(clinician, originalClinician.getStaffId(), token);
            } else {
                clinicianBridge.postClinician(clinician, token);
            }
    }

    /**
     * Takes a passed clinician and removes them from the maintained list of clinicians
     *
     * @param clinician The clinician to be deleted
     */
    public void deleteClinician(Clinician clinician) {
        clinician.setDeleted(true);

        getClinicianBridge().deleteClinician(clinician, getToken());
    }

    /**
     * Removes the given admin from the list of administrators unless the given admin is the default admin.
     *
     * @param admin The given admin
     */
    public void deleteAdmin(Administrator admin) {
        admin.setDeleted(true);

        getAdministratorBridge().deleteAdmin(admin, token);
        admins.remove(admin);
    }

    public UserController getUserController() {
        return userController;
    }

    /**
     * @param userController The controller class for the user overview.
     */
    public void setUserController(UserController userController) {
        this.userController = userController;
    }

    public ClinicianController getClinicianController() {
        return clinicianController;
    }

    public void setClinicianController(ClinicianController clinicianController) {
        this.clinicianController = clinicianController;
    }

    public AdministratorViewController getAdministratorViewController() {
        return administratorViewController;
    }

    public void setAdministratorViewController(AdministratorViewController administratorViewController) {
        this.administratorViewController = administratorViewController;
    }

    public Administrator getAdministrator(String username) {
        for (Administrator a : admins) {
            if (a.getUserName().equals(username) && !a.isDeleted()) {
                return a;
            }
        }
        return null;
    }

    /**
     * Refreshes the list of administrators with the updated admin
     *
     * @param administrator the current administrator
     */
    public void updateAdmin(Administrator administrator) {
        if (admins.contains(administrator)) {
            admins.remove(administrator);
            admins.add(administrator);
        } else {
            admins.add(administrator);
        }
    }

    /**
     * Saves the given administrator to the database
     *
     * @param administrator Administrator to be saved
     */
    public void saveAdmin(Administrator administrator) {
        Administrator originalAdmin;
        if (!administrator.getUndoStack().isEmpty()) {
            originalAdmin = administrator.getUndoStack().firstElement().getState();
        } else {
            originalAdmin = administrator;
        }

        if (administratorBridge.getExists(originalAdmin.getUserName())) {
            administratorBridge.putAdmin(administrator, originalAdmin.getUserName(), getToken());
        } else {
            administratorBridge.postAdmin(administrator, token);
        }
    }


    /**
     * Method to remove the specified user object from the deleted user set and add it into the pool
     * of users
     *
     * @param user user object to undo deletion of
     * @throws ProfileNotFoundException      if the user is not in the deletedUserSet
     * @throws ProfileAlreadyExistsException if a user with the same NHI is in the users list
     */
    public void undoDeletion(User user) throws ProfileNotFoundException, ProfileAlreadyExistsException {
        if (user.isDeleted()) {
            if (findUser(user.getNhi()) == null) {
                user.setDeleted(false);
                redoStack.push(user);
            } else {
                throw new ProfileAlreadyExistsException();
            }
        } else {
            throw new ProfileNotFoundException();
        }
    }

    /**
     * Restores the specified clinician object by adding it to the list of active clinicians and removing it from the
     * set of deleted clinicians.
     *
     * @param clinician The specified clinician object to be restored
     * @throws ProfileNotFoundException      if the clinician is not within the set of deleted clinicians
     * @throws ProfileAlreadyExistsException if a clinician with the same Staff ID is in the clinician list
     */
    public void undoClinicianDeletion(Clinician clinician) throws ProfileNotFoundException, ProfileAlreadyExistsException {
        if (clinician.isDeleted()) {
            if (getClinician(clinician.getStaffId()) == null) {
                clinician.setDeleted(false);
            } else {
                throw new ProfileAlreadyExistsException();
            }
        } else {
            throw new ProfileNotFoundException();
        }
    }

    /**
     * Restores the specified administrator object by adding it to the list of active admins and removing it from the
     * set of deleted admins.
     *
     * @param admin the specified administrator to be restored
     * @throws ProfileNotFoundException      if the admin is not within the set of deleted administrators
     * @throws ProfileAlreadyExistsException if an administrator with the same username is in the administrator list
     */
    public void undoAdminDeletion(Administrator admin) throws ProfileNotFoundException, ProfileAlreadyExistsException {
        if (admin.isDeleted()) {
            if (getAdministrator(admin.getUserName()) == null) {
                admin.setDeleted(false);
            } else {
                throw new ProfileAlreadyExistsException();
            }
        } else {
            throw new ProfileNotFoundException();
        }
    }

    public List<TransplantDetails> getTransplantList() {
        return transplantList;
    }

    public void addTransplant(TransplantDetails transplantDetails) {
        transplantList.add(transplantDetails);
    }

    public Set<UserOverview> getUserOverviews() {
        return overviews;
    }

    public void setUserOverviews(Set<UserOverview> users) {
        overviews = users;
    }

    public AdministratorBridge getAdministratorBridge() {
        return administratorBridge;
    }

    public UserBridge getUserBridge() {
        return userBridge;
    }

    public ClinicianBridge getClinicianBridge() {
        return clinicianBridge;
    }

    public LoginBridge getLoginBridge() {
        return loginBridge;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setAdministratorBridge(AdministratorBridge adminBridge) {
        administratorBridge = adminBridge;
    }

    public void setClinicianBridge(ClinicianBridge cliBridge) {
        clinicianBridge = cliBridge;
    }

    public void setLoginBridge(LoginBridge loginBridge) {
        this.loginBridge = loginBridge;
    }

    public void setUserBridge(UserBridge userBridge) {
        this.userBridge = userBridge;
    }

    public void setTransplantBridge(TransplantBridge transplantBridge) {
        this.transplantBridge = transplantBridge;
    }

    public void setCountriesBridge(CountriesBridge countriesBridge) {
        this.countriesBridge = countriesBridge;
    }

    public CountriesBridge getCountriesBridge() {
        return countriesBridge;
    }

    public SQLBridge getSqlBridge() {
        return sqlBridge;
    }

    public OdmsSocketHandler getSocketHandler() {
        return socketHandler;
    }
}
