package seng302.utils;

//import com.mysql.jdbc.PreparedStatement;

import seng302.model.Administrator;
import seng302.model.Clinician;
import seng302.model.User;

import java.io.InvalidClassException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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
    private static final String CREATE_USER_STMT = "INSERT INTO User (nhi, firstName, middleName, lastName, preferredName, dob, dod) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String CREATE_USER_CONTACT_STMT = "INSERT INTO ContactDetails (fkUserNhi, homePhone, email, cellPhone) VALUES (?, ?, ?, ?)";
    private static final String CREATE_STAFF_CONTACT_STMT = "INSERT INTO ContactDetails (fkStaffId, homePhone, email, cellPhone) VALUES (?, ?, ?, ?)";
    private static final String CREATE_ADDRESS_STMT = "INSERT INTO Address (fkContactId, streetNumber, streetName, neighbourhood, city, region, country) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String CREATE_HEALTH_DETAILS = "INSERT INTO HealthDetails (fkUserNhi, gender, birthGender, smoker, alcoholConsumption, height, weight) VALUES (?, ?, ?, ?, ?, ?, ?)"; // TODO: Include blood type 24/6 - Eiran
    private static final String CREATE_EMERGENCY_STMT = "INSERT INTO EmergencyContactDetails (fkContactId, contactName, contactRelationship) VALUES (?, ?, ?)";

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

        while (resultSet.next()){
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
            executeUpdate(users);
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

        while (resultSet.next()) {
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
            executeUpdate(clinicians);
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

        while (resultSet.next()) {
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
            executeUpdate(administrators);
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
    private ResultSet executeQuery(PreparedStatement statement) {
        try {
            return statement.executeQuery();

        } catch (SQLException sqlEx) {
            Log.warning("Error in connection to database", sqlEx);
            System.out.println("Error connecting to database");
        }
        return null;
    }

    /**
     * Executes an update for each of items in the collection. The Collection must be of a type User, Clinician or Administrator
     *
     * @param collection collection of objects to update the database with
     * @param <T>        User, Clinician or Administrator
     * @throws InvalidClassException if the collection does not hold Users, Clinicians or Administrators
     */
    private <T> void executeUpdate(Collection<T> collection) throws InvalidClassException {
        try {
            connect();
            Iterator iter = collection.iterator();
            while (iter.hasNext()) {
                if (iter.next() instanceof Administrator) {

                } else if (iter.next() instanceof Clinician) {

                } else if (iter.next() instanceof User) {

                } else {
                    throw new InvalidClassException("Collection not a collection of Users, Clinicians or Administrators");
                }
            }
            connection.close();
        } catch (SQLException sqlEx) {
            Log.warning("Error in connection to database", sqlEx);
            System.out.println("Error connecting to database");
        }
    }

    /**
     * Executes an update for each of items in the collection. The Collection must be of a type User, Clinician or Administrator
     *
     * @param collection collection of objects to update the database with
     * @param <T>        User, Clinician or Administrator
     * @throws InvalidClassException if the collection does not hold Users, Clinicians or Administrators
     */
    private <T> void executeCreation(Collection<T> collection) throws InvalidClassException {
        try {
            connect();
            Iterator iter = collection.iterator();
            while (iter.hasNext()) {
                Object object = iter.next();
                if (object instanceof Administrator) {

                } else if (object instanceof Clinician) {

                } else if (object instanceof User) {
                    User user = (User) object;
                    createUser(user);
                    createContact(user.getNhi(), user);
                    createHealthDetails(user.getNhi(), user);
                } else {
                    throw new InvalidClassException("Collection not a collection of Users, Clinicians or Administrators");
                }
            }
            connection.close();
        } catch (SQLException sqlEx) {
            Log.warning("Error in connection to database", sqlEx);
            System.out.println("Error connecting to database");
        }
    }

    /**
     * Creates an user entry in the tables.
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

        stmt.executeUpdate();
    }

    /**
     * Creates a contact object with for the given user using the CREATE_USER_CONTACT_STMT.
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
    }

    /**
     * Creates a health details entry in the database with the associated user
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

    //TODO: Remove this main once the DB handler is fully developed
    public static void main(String[] args) throws SQLException{
        DBHandler dbHandler = new DBHandler();
    }
}
