package odms.commons.utils;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;


/**
 * Driver class for the database
 *
 * Allows connections and DB admin to be constructed away from the sql part of the database
 */
public class JDBCDriver {


    /**
     * String constants for connecting to the database
     */
    private String url;
    private String user;
    private String password;
    private String database;


    private ComboPooledDataSource comboPooledDataSource;


    public JDBCDriver () throws PropertyVetoException {
        Properties prop = new ConfigPropertiesLoader().loadConfig("serverConfig.properties");
        url = prop.getProperty("db.url", "");
        user = prop.getProperty("db.user", "");
        password = prop.getProperty("db.password", "");
        database = prop.getProperty("db.database", "");

        createPool();
    }

    private void createPool() throws PropertyVetoException {
        comboPooledDataSource = new ComboPooledDataSource();

        try {
            comboPooledDataSource.setDriverClass("com.mysql.jdbc.Driver");
        } catch (PropertyVetoException e) {
            Log.warning("Could not create DB pool", e);
            throw  e;
        }
        comboPooledDataSource.setJdbcUrl("jdbc:mysql:" + url + database + "?zeroDateTimeBehavior=convertToNull");
        comboPooledDataSource.setUser(user);
        comboPooledDataSource.setPassword(password);
    }


    /**
     * Establishes a connection to the database and returns it
     *
     * @return A connection to the current database
     * @throws SQLException if there is an error in connecting to the database
     */
    public Connection getConnection() throws SQLException {
        return comboPooledDataSource.getConnection();
    }

}
