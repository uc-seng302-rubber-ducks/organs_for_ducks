package seng302.utils;

//import com.mysql.jdbc.PreparedStatement;
import java.sql.*;

import seng302.model.Administrator;
import seng302.model.Clinician;
import seng302.model.User;

import java.io.InvalidClassException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
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

    private void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql:" + URL + TEST_DB, USER, PASSWORD);
        System.out.println(connection);
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

    //TODO: Remove this main once the DB handler is fully developed
    public static void main(String[] args) throws SQLException{
        DBHandler dbHandler = new DBHandler();
    }
}
