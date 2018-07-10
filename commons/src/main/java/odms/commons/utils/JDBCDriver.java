package odms.commons.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * Driver class for the database
 *
 * Allows connections and DB admin to be constructed away from the sql part of the database
 */
public class JDBCDriver {


    /**
     * String constants for connecting to the database
     */
    private static final String URL = "//mysql2.csse.canterbury.ac.nz:3306";
    private static final String USER = "seng302-team100";
    private static final String PASSWORD = "VicingSheds6258";
    private static final String TEST_DB = "/seng302-2018-team100-test";
    private static final String PROD_DB = "/seng302-2018-team100-prod";



    /**
     * Establishes a connection to the database and returns it
     *
     * @return A connection to the current database
     * @throws SQLException if there is an error in connecting to the database
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql:" + URL + TEST_DB + "?zeroDateTimeBehavior=convertToNull", USER, PASSWORD );
    }

    /**
     * Establishes a connection to the database and returns it
     *
     * @return A connection to the current database
     * @throws SQLException if there is an error in connecting to the database
     */
    public Connection getTestConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql:" + URL + TEST_DB, USER, PASSWORD);
    }

}
