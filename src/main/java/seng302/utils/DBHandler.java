package seng302.utils;

//import com.mysql.jdbc.PreparedStatement;

import seng302.model.Administrator;
import seng302.model.Clinician;
import seng302.model.MedicalProcedure;
import seng302.model.User;
import seng302.model.datamodel.ProcedureKey;

import java.io.InvalidClassException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DBHandler {
    /**
     * Active connection to the database
     */
    private Connection connection;

    /**
     * String constants for connecting to the database
     */
    private static final String URL = "//mysql2.csse.canterbury.ac.nz:3306";
    private static final String USER = "seng302-team100";
    private static final String PASSWORD = "VicingSheds6258";
    private static final String TEST_DB = "/seng302-2018-team100-test";
    private static final String PROD_DB = "/seng302-2018-team100-prod";

    /**
     * SQL commands for executing creates
     */
    //<editor-fold desc="INSERT Queries">
    private static final String CREATE_USER_STMT = "INSERT INTO User (nhi, firstName, middleName, lastName, preferedName, dob, dod, timeCreated, lastModified) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String CREATE_USER_CONTACT_STMT = "INSERT INTO ContactDetails (fkUserNhi, homePhone, email, cellPhone) VALUES (?, ?, ?, ?)";
    private static final String CREATE_STAFF_CONTACT_STMT = "INSERT INTO ContactDetails (fkStaffId, homePhone, email, cellPhone) VALUES (?, ?, ?, ?)";
    private static final String CREATE_CLINICIAN_STMT = "INSERT INTO Clinician (staffId, firstName, middleName, lastName) VALUES (?, ?, ?, ?)";
    private static final String CREATE_ADMIN_STMT = "INSERT INTO Administrator (username, firstName, middleName, lastName) VALUES (?, ?, ?, ?)";
    private static final String CREATE_ADDRESS_STMT = "INSERT INTO Address (fkContactId, streetNumber, streetName, neighbourhood, city, region, country) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String CREATE_HEALTH_DETAILS = "INSERT INTO HealthDetails (fkUserNhi, gender, birthGender, smoker, alcoholConsumption, height, weight) VALUES (?, ?, ?, ?, ?, ?, ?)"; // TODO: Include blood type 24/6 - Eiran
    private static final String CREATE_EMERGENCY_STMT = "INSERT INTO EmergencyContactDetails (fkContactId, contactName, contactRelationship) VALUES (?, ?, ?)";
    private static final String GET_LATEST_CONTACT_ENTRY = "SELECT MAX(contactId) AS contactId FROM ContactDetails WHERE fkUserNhi=?";
    private static final String CREATE_NEW_MEDICATION = "INSERT INTO Medication (fkUserNhi, medicationName) VALUES (?, ?)";
    private static final String CREATE_NEW_PROCEDURE = "INSERT INTO Procedure (fkUserNhi, procedureName, procedureDescription, procedureDate) VALUES (?, ?, ?)";
    //</editor-fold>

    /**
     * SQL Queries for updates
     */
    private static final String UPDATE_USER_STMT = "";
    private static final String UPDATE_USER_CONTACT_STMT = "";
    private static final String UPDATE_USER_HEALTH_STMT = "";
    private static final String UPDATE_MEDICATION_STMT = "";

    /**
     * SQL Queries for deletes
     */
    private static final String DELETE_USER_STMT = "DELETE FROM User WHERE nhi = ?";
    private static final String DELETE_PROCEDURE_STMT = "DELETE FROM MedicalProcedure WHERE procedureDate = ? AND procedureName = ? AND fkUserNhi = ?";
    private static final String DELETE_CLINICIAN_STMT = "DELETE FROM Clinician WHERE staffId = ?";
    private static final String DELETE_ADMIN_STMT = "DELETE FROM Administrators WHERE username = ?";
    private static final String DELETE_USER_DISEASE_STMT = "DELETE FROM CurrentDisease WHERE diseaseName = ? AND diagnosisDate = ? AND fkUserNhi = ?";
    private static final String DELETE_PAST_DISEASE_STMT = "DELETE FROM PreviousDisease WHERE diseaseName = ? AND diagnosisDate = ? AND fkUserNhi = ?";
    private static final String DELETE_MEDICATION_STMT = "DELETE FROM Medication WHERE medicationName = ? AND fkUserNhi = ?";

    /**
     * Establishes a connection to the database
     * @throws SQLException if there is an error in connecting to the database
     */
    private void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql:" + URL + TEST_DB, USER, PASSWORD);
    }

    /**
     * Helper function to convert date string from database
     * to LocalDateTime object.
     * @param date date string from database
     * @return LocalDateTime object
     */
    private LocalDateTime dateToLocalDateTime(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        return LocalDateTime.parse(date, formatter);
    }

    /**
     * Method to obtain all the users from the database. Opens and closes it's own connection to the database
     *
     * @return a Collection of Users
     */
    public Collection<User> getAllUsers() throws SQLException {
        Collection<User> users = new ArrayList<>();
        String sql = "SELECT * FROM User";
        connect();

        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = executeQuery(statement);

        while (resultSet != null && resultSet.next()) {
            System.out.println(resultSet.getString(6));
            User user = new User();
            user.setNhi(resultSet.getString(1)); //todo: issue with cloning user object for memento
            user.setName(resultSet.getString(2), resultSet.getString(3), resultSet.getString(4));
            user.setPreferredFirstName(resultSet.getString(5));
            user.setTimeCreated(dateToLocalDateTime(resultSet.getString(6)));
            user.setLastModified(dateToLocalDateTime(resultSet.getString(7)));
            users.add(user);
        }

        connection.close();
        return users;
    }

    /**
     * Method to save all the users to the database. Opens and closes it's own connection to the database
     *
     * @param users A non null collection of users to save to the database
     */
    public void saveUsers(Collection<User> users) {
        try {
            updateDatabase(users);
        } catch (InvalidClassException invalidEx) {
            //Should never happen, but if it does, system failure
            invalidEx.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Loads the clinicians from the database. Opens and closes its own connection to the database
     *
     * @return the Collection of clinicians
     */
    public Collection<Clinician> loadClinicians() throws SQLException {
        Collection<Clinician> clinicians = new ArrayList<>();
        String sql = "SELECT * FROM Clinician cl " +
                "LEFT JOIN PasswordDetails pd " +
                "ON cl.staffId = pd.fkStaffId";
        connect();

        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = executeQuery(statement);

        while (resultSet != null && resultSet.next()) {
            Clinician clinician = new Clinician();
            clinician.setStaffId(resultSet.getString(1));
            clinician.setFirstName(resultSet.getString(2));
            clinician.setMiddleName(resultSet.getString(3));
            clinician.setLastName(resultSet.getString(4));
            clinician.setDateCreated(dateToLocalDateTime(resultSet.getString(5)));
            clinician.setDateLastModified(dateToLocalDateTime(resultSet.getString(6)));
            //clinician.setPassword(resultSet.getString(10)); //TODO since the database stores the hash and salt, do we store those directly without having to use setPassword?
            clinicians.add(clinician);
        }

        connection.close();
        return clinicians;
    }

    /**
     * Updates the clinicians stored in active memory.
     *
     * @param clinicians Collection of clinicians to update.
     */
    public void saveClinicians(Collection<Clinician> clinicians) {
        try {
            updateDatabase(clinicians);
        } catch (InvalidClassException invalidEx) {
            //Should never happen, but if it does, system failure
            invalidEx.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Loads the administrators from the database. Opens and closes its own connection to the database
     *
     * @return the Collection of administrators
     */
    public Collection<Administrator> loadAdmins() throws SQLException {
        Collection<Administrator> administrators = new ArrayList<>();
        String sql = "SELECT * FROM Administrator ad " +
                "LEFT JOIN PasswordDetails pd " +
                "ON ad.userName = pd.fkAdminUserName";
        connect();

        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = executeQuery(statement);

        while (resultSet != null && resultSet.next()) {
            Administrator administrator = new Administrator();
            administrator.setUserName(resultSet.getString(1));
            administrator.setFirstName(resultSet.getString(2));
            administrator.setMiddleName(resultSet.getString(3));
            administrator.setLastName(resultSet.getString(4));
            administrator.setDateCreated(dateToLocalDateTime(resultSet.getString(5)));
            administrator.setDateLastModified(dateToLocalDateTime(resultSet.getString(6)));
            //administrator.setPassword(resultSet.getString(10)); //TODO since the database stores the hash and salt, do we store those directly without having to use setPassword?
            administrators.add(administrator);
        }

        connection.close();
        return administrators;
    }

    /**
     * Updates the administrators stored in active memory.
     *
     * @param administrators Collection of admins to update.
     */
    public void saveAdministrators(Collection<Administrator> administrators) {
        try {
            updateDatabase(administrators);
        } catch (InvalidClassException invalidEx) {
            //Should never happen, but if it does, system failure
            invalidEx.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Executes a PreparedStatement provided for the database
     *
     * @param statement a PreparedStatement
     * @return A result set
     */
    private ResultSet executeQuery(PreparedStatement statement) throws SQLException {
        return statement.executeQuery();
    }

    /**
     * Executes an update for each of items in the collection. The Collection must be of a type User, Clinician or Administrator
     *
     * @param collection collection of objects to update the database with
     * @param <T>        User, Clinician or Administrator
     * @throws InvalidClassException if the collection does not hold Users, Clinicians or Administrators
     */
    private <T> void updateDatabase(Collection<T> collection) throws InvalidClassException {
        String identifier;
        String identifierName;
        String table;
        boolean toDelete; // Flag that marks whether the current role is to be deleted.
        try {
            connect();
            for (T object : collection) {

                if (object instanceof Administrator) {
                    Administrator admin = (Administrator) object;
                    if (admin.getChanges().size() <= 0) {
                        continue;
                    }
                    table = "Administrator";
                    identifierName = "userName";
                    identifier = admin.getUserName();
                    toDelete = admin.isDeleted();
                } else if (object instanceof Clinician) {
                    Clinician clinician = (Clinician) object;
                    if (clinician.getChanges().size() <= 0) {
                        continue;
                    }
                    table = "Clinician";
                    identifierName = "staffId";
                    identifier = clinician.getStaffId();
                    toDelete = clinician.isDeleted();
                } else if (object instanceof User) {
                    User user = (User) object;
                    if (user.getChanges().size() <= 0) {
                        continue;
                    }
                    table = "User";
                    identifierName = "nhi";
                    identifier = user.getNhi();
                    toDelete = user.isDeleted();
                } else {
                    throw new InvalidClassException("Object is not of type Users, Clinicians or Administrators");
                }

                PreparedStatement stmt = connection.prepareStatement(String.format("SELECT %s FROM %s WHERE %s = ?", identifierName, table, identifierName));
                stmt.setString(4, identifier);
                ResultSet queryResults = executeQuery(stmt);
                if (!queryResults.next()) {
                    executeCreation(object);
                } else if (toDelete) {
                    deleteRole(object);
                } else {
                    executeUpdate(object);
                }
            }
            connection.close();
        } catch (SQLException sqlEx) {
            Log.warning("Error in connection to database", sqlEx);
            System.out.println("Error connecting to database");
        }
    }

    /**
     * Executes an update for an entry associated with the provided object.
     * Pre-condition: The object must be of type User, Clinician or Admin
     * Post-condition: The entry in the database reflects the entry
     *
     * @param object The object associated with the entry in the database
     * @param <T>    Admin, Clinician or User
     */
    private <T> void executeUpdate(T object) {

    }

    /**
     * Executes an update for each of items in the collection.
     * Precondition: The object must be of a type User, Clinician or Administrator
     * Post-conditions: An entry that represents the object is created and stored in the database.
     *
     * @param object collection of objects to update the database with
     * @param <T>        User, Clinician or Administrator
     * @throws InvalidClassException if the object is not User, Clinicians or Administrators
     */
    private <T> void executeCreation(T object) throws InvalidClassException {
        try {
            connect();
            connection.prepareStatement("START TRANSACTION").execute();
            try {
                if (object instanceof Administrator) {
                    Administrator admin = (Administrator) object;
                    createAdmin(admin);
                    createPassword(admin);
                } else if (object instanceof Clinician) {
                    Clinician clinician = (Clinician) object;
                    createClinician(clinician);
                    // TODO: Create the clinician contact stuff once the abstractions are completed 25/6 - Eiran
                } else if (object instanceof User) {
                    User user = (User) object;
                    createUser(user);
                    createEmergencyContact(user.getNhi(), user);
                    createContact(user.getNhi(), user);
                    createHealthDetails(user.getNhi(), user);
                } else {
                    throw new InvalidClassException("Provided role is not of type Users, Clinicians or Administrators");
                }
            } catch (SQLException sqlEx) {
                connection.prepareStatement("ROLLBACK").execute();
                System.out.println("An error occured"); //TODO: Make this a popup
            }
            connection.prepareStatement("COMMIT");
            connection.close();
        } catch (SQLException sqlEx) {
            Log.warning("Error in connection to database", sqlEx);
            System.out.println("Error connecting to database");
        }
    }

    //<editor-fold desc="Create helper functions">
    /**
     * Saves the hashed password to the PasswordDetails table in the database
     * Precondition: The role provided is an Administrator or Clinician
     * The connection is active using connect()
     * Postcondition: The hashed password and salt is stored in the database.
     *
     * @param role Object to store the password of
     * @param <T>  Administrator or Clinician
     * @throws InvalidClassException if the role is not an instance of Administrator or Clinician
     * @throws SQLException          If there is an error in storing it into the database or the connection is invalid
     */
    private <T> void createPassword(T role) throws InvalidClassException, SQLException {
        if (role instanceof Administrator) {
            Administrator admin = (Administrator) role;
            PreparedStatement statement = connection.prepareStatement("INSERT INTO PasswordDetails (fkAdminUserName, hash, salt) VALUES (?, ?, ?)");
            statement.setString(1, admin.getUserName());
            //TODO: Figure out how to send the password hash and salt 25/6 - Eiran
        } else if (role instanceof Clinician) {

        } else {
            throw new InvalidClassException("Not a Administrator or Clinician");
        }
    }

    /**
     * Creates an admin entry in the database tables using CREATE_ADMIN_STMT
     * Preconditions: Must have an active connection to the database
     * Post-conditions: The given admin is created in the database
     *
     * @param admin administrator object to create
     * @throws SQLException If there isn't an active connection to the database or there is an error creating the administrator
     */
    private void createAdmin(Administrator admin) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(CREATE_ADMIN_STMT);

        statement.setString(1, admin.getUserName());
        statement.setString(2, admin.getFirstName());
        statement.setString(3, admin.getMiddleName());
        statement.setString(4, admin.getLastName());

        statement.executeUpdate();
    }

    /**
     * Creates a clinician entry in the tables
     * Preconditions: Must have an active connection to the database
     * Postconditions: The given clinician is created in the database
     *
     * @param clinician The clinician object to create
     * @throws SQLException If there isn't an active connection to the database or there is an error in creating the clinician
     */
    private void createClinician(Clinician clinician) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(CREATE_CLINICIAN_STMT);
        statement.setString(1, clinician.getStaffId());
        statement.setString(2, clinician.getFirstName());
        statement.setString(3, clinician.getMiddleName());
        statement.setString(4, clinician.getLastName());

        statement.executeUpdate();
    }

    /**
     * Creates an user entry in the tables.
     * Must have an active connection to the database (created through connect())
     *
     * @param user user object to place into the entry
     * @throws SQLException if there is an issue with the execution of the of the statement.
     */
    private void createUser(User user) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(CREATE_USER_STMT);
        stmt.setString(1, user.getNhi());
        stmt.setString(2, user.getFirstName());
        stmt.setString(3, user.getMiddleName());
        stmt.setString(4, user.getLastName());
        stmt.setString(5, user.getPreferredFirstName());
        stmt.setDate(6, Date.valueOf(user.getDateOfBirth()));
        stmt.setDate(7, Date.valueOf(user.getDateOfDeath()));
        stmt.setTimestamp(8, Timestamp.valueOf(user.getTimeCreated()));
        stmt.setTimestamp(9, Timestamp.valueOf(user.getLastModified()));

        stmt.executeUpdate();
    }

    /**
     * Creates a contact object with for the given user using the CREATE_USER_CONTACT_STMT.
     * Must have an active connection to the database (created through connect())
     *
     * @param userNhi nhi of the user to associate the contact object with.
     * @param user    user to create the associated contact for
     * @throws SQLException if there is a problem with creating the contact
     */
    private void createContact(String userNhi, User user) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(CREATE_USER_CONTACT_STMT);
        stmt.setString(1, userNhi);
        stmt.setString(2, user.getHomePhone());
        stmt.setString(3, user.getEmail());
        stmt.setString(4, user.getCellPhone());
        stmt.executeUpdate();

        String contactId = getContactId(userNhi);

        PreparedStatement createAddrStatement = connection.prepareStatement(CREATE_ADDRESS_STMT);
        createAddrStatement.setString(1, contactId);
    }

    /**
     * Gets the most recently created contact object for a given userNhi.
     * Preconditions: A contact object for the user exists.
     * There is an active connection to the database created via connect()
     * Postconditions: The contactId is returned
     *
     * @param userNhi NHI of the user to get the latest contact info for
     * @return The contact id of the latest contact entry for the specified user
     * @throws SQLException if a latest entry doesn't exist or the connection is null.
     */
    private String getContactId(String userNhi) throws SQLException {
        PreparedStatement getContactId = connection.prepareStatement(GET_LATEST_CONTACT_ENTRY);
        getContactId.setString(1, userNhi);
        ResultSet results = executeQuery(getContactId);
        if (results == null) {
            throw new SQLException("Required Contact Entry was not found");
        }
        return results.getString("contactId");
    }

    /**
     * Creates a health details entry in the database with the associated user
     * Must have an active connection to the database (created through connect())
     *
     * @param userNhi NHI of the user to associate the health details with.
     * @param user    user to create the associated contact for
     * @throws SQLException if there is a problem when creating the health details
     */
    private void createHealthDetails(String userNhi, User user) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(CREATE_HEALTH_DETAILS);
        stmt.setString(1, userNhi);
        stmt.setString(2, user.getGenderIdentity());
        stmt.setString(3, user.getBirthGender());
        stmt.setBoolean(4, user.isSmoker());
        stmt.setString(5, user.getAlcoholConsumption());
        stmt.setDouble(6, user.getHeight());
        stmt.setDouble(7, user.getWeight());

        stmt.executeUpdate();
    }

    /**
     * Creates and links the emergency contact details to the user.
     * Must have an active connection to the database (created through connect())
     * Must be called before createContact, otherwise it may link to the wrong contact object.
     *
     * @param userNhi The NHI of the user to link the contact details for
     * @param user    The user for which the emergency contact is to be created.
     * @throws SQLException If there is an error in creating the emergency contact.
     */
    private void createEmergencyContact(String userNhi, User user) throws SQLException {
        //TODO: Make the create contact method take in a contact object and make call to create emergency contact here. 25/6 - Eiran
        String contactId = getContactId(userNhi);

        PreparedStatement stmt = connection.prepareStatement(CREATE_EMERGENCY_STMT);
        stmt.setString(1, contactId);
        stmt.setString(2, user.getContact().getName());
        stmt.setString(3, user.getContact().getRelationship());

        stmt.executeUpdate();
    }
    //</editor-fold>

    /**
     * Deletes a role entry from the database that is associated with the given object
     * Preconditions: The object provided is of type User, Clinician, Admin
     * Postcondition: The entry associated with the object will be deleted.
     *
     * @param object The object associated with the entry to be deleted
     * @param <T>    User, Clinician or Administrator
     * @throws InvalidClassException if the provided object is not of type User, Clinician or Admin
     */
    private <T> void deleteRole(T object) throws InvalidClassException {
        String identifier;
        String sql;
        try {
            connect();
            connection.prepareStatement("START TRANSACTION").execute();
            try {
                if (object instanceof Administrator) {
                    Administrator admin = (Administrator) object;
                    identifier = admin.getUserName();
                    sql = DELETE_ADMIN_STMT;
                } else if (object instanceof Clinician) {
                    Clinician clinician = (Clinician) object;
                    identifier = clinician.getStaffId();
                    sql = DELETE_CLINICIAN_STMT;
                } else if (object instanceof User) {
                    User user = (User) object;
                    identifier = user.getNhi();
                    sql = DELETE_USER_STMT;
                } else {
                    throw new InvalidClassException("Provided role is not of type Users, Clinicians or Administrators");
                }
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, identifier);

                stmt.executeUpdate();
            } catch (SQLException sqlEx) {
                Log.severe("A fatal error in deletion, cancelling operation", sqlEx);
                connection.prepareStatement("ROLLBACK").execute();
                System.out.println("An error occured"); //TODO: Make this a popup
            }

            connection.prepareStatement("COMMIT");
            connection.close();
        } catch (SQLException sqlEx) {
            Log.warning("Error in connection to database", sqlEx);
            System.out.println("Error connecting to database");
        }
    }

    /**
     * Deletes finer user details such as medications, procedures and diseases
     * Preconditions: There is an active connection to the database
     * Post-conditions: The changes made locally are reflected on the database
     *
     * @param user user to delete the details of
     * @throws SQLException If there is an error deleting the details.
     */
    private void deleteUsersDetails(User user) throws SQLException {
        deleteUserProcedures(user);
        deleteUserMedications(user);
        deleteUserCurrentDiseases(user);
        deleteUserPreviousDiseases(user);
    }

    /**
     * Deletes previous diseases that the user has had on the database to reflect the data on the application.
     * Pre-conditions: There is an active connection to the database
     * Post-conditions: The data on the database reflects the data locally.
     *
     * @param user The user for which the previous disease belonged to.
     */
    private void deleteUserPreviousDiseases(User user) throws SQLException {
        PreparedStatement deleteDisease = connection.prepareStatement(DELETE_PAST_DISEASE_STMT);
    }

    /**
     * @param user
     */
    private void deleteUserCurrentDiseases(User user) {
    }

    /**
     *
     * @param user
     */
    private void deleteUserMedications(User user) {

    }

    /**
     * Delete procedures that are present in the database but not in the local user storage
     * Pre-conditions: There is an active connection to the database
     * Post-condition: The procedures that have been deleted locally are deleted in the database
     *
     * @param user User to check and delete procedures for
     * @throws SQLException if the connection to the database is invalid
     */
    private void deleteUserProcedures(User user) throws SQLException {
        PreparedStatement getProcedureStmt = connection.prepareStatement("SELECT procedureName, procedureDate FROM MedicalProcedure WHERE fkUserNhi = ?");
        getProcedureStmt.setString(1, user.getNhi());
        ResultSet dbProcedures = executeQuery(getProcedureStmt);

        // Convert the list into a mapping of procedure keys and procedures 26/6 - Eiran
        Map<ProcedureKey, MedicalProcedure> procedureMap = new HashMap<>();
        for (MedicalProcedure p : user.getMedicalProcedures()) {
            procedureMap.put(p.getKey(), p);
        }

        // Loops through each entry in the results and deletes it if it is not present in the local list. 26/6 - Eiran
        while (dbProcedures.next()) {
            ProcedureKey key = new ProcedureKey(dbProcedures.getString(1), dbProcedures.getDate(2).toLocalDate());
            if (!procedureMap.containsKey(key)) {
                PreparedStatement deleteProcedure = connection.prepareStatement(DELETE_PROCEDURE_STMT);
                deleteProcedure.setString(1, key.getName());
                deleteProcedure.setDate(2, Date.valueOf(key.getDate()));
                deleteProcedure.setString(3, user.getNhi());

                deleteProcedure.executeUpdate();
            }
        }
    }


}
