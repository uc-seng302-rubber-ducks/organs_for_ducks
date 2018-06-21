package seng302.utils;

//import com.mysql.jdbc.PreparedStatement;
import java.sql.*;

import seng302.model.Administrator;
import seng302.model.Clinician;
import seng302.model.User;

import java.io.InvalidClassException;
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

    private void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql:" + URL + TEST_DB, USER, PASSWORD);
        System.out.println(connection);
    }

    /**
     * Method to obtain all the users from the database. Opens and closes it's own connection to the database
     *
     * @return a Collection of Users
     */
    public Collection<User> getAllUsers() throws SQLException {
        String sql = "SELECT * FROM User u LEFT JOIN PreviousDisease pd ON pd.fkUserNhi = u.nhi " +
                "LEFT JOIN CurrentDisease cd ON cd.fkUserNhi = u.nhi " +
                "LEFT JOIN Medication m ON m.fkUserNhi = u.nhi";
        PreparedStatement statement = connection.prepareStatement(sql);
        return executeQuery(statement);
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
        String sql = "SELECT * FROM Clinician";
        PreparedStatement statement = connection.prepareStatement(sql);
        return executeQuery(statement);
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
        String sql = "SELECT * FROM Administrator";
        PreparedStatement statement = connection.prepareStatement(sql);
        return executeQuery(statement);
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
     * @param <T> The type of Collection to return.
     * @return A collection of type T.
     */
    private <T> Collection<T> executeQuery(PreparedStatement statement) {
        try {
            connect();
            ResultSet resultSet = statement.executeQuery();
            Collection<T> queryResult = new ArrayList<>();
            while (resultSet.next()){
//                User user = new User(); //TODO: How do you generify this?
//                user.setName(resultSet.getString(2), resultSet.getString(3), resultSet.getString(4));
                //queryResult.add(user);
            }
            connection.close();
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
        dbHandler.connect();

    }
}
