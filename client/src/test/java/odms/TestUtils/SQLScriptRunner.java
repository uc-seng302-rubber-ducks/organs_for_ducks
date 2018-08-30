package odms.TestUtils;

import odms.commons.database.JDBCDriver;
import odms.commons.utils.Log;

import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * method to run sql scripts
 */
public class SQLScriptRunner {

    private static String RESET_DATABASE_SCRIPT_FILEPATH = "client/src/main/resources/sqlScripts/createDataBase.sql";
    private static String RESAMPLE_DATABASE_SCRIPT_FILEPATH = "client/src/main/resources/sqlScripts/sampleDatabaseData.sql";
    private static String INSERT_COUNTRIES_STMT= "INSERT INTO Countries(countryName) VALUES (?)";

    /**
     * Opens a file based on filePath given, reads the file and execute the
     * SQL strings contained in the file.
     * Close the database connection when done.
     * Note: every SQL statement must end with semi-colon (;)
     *
     * @param filePath location of the SQL script
     * @throws SQLException if any SQL error occurs
     * @throws IOException if any errors with reading the file occurs
     */
    public static void runSqlScript(String filePath, Connection connection) throws SQLException, IOException{


        String absolutePath = new File("./").getAbsolutePath();
        absolutePath = absolutePath.substring(0, absolutePath.length()-1); //remove the full stop
        String scriptFilePath = absolutePath + filePath;

        String SQLString = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(scriptFilePath))) {

            String line;

            // read script line by line
            while ((line = reader.readLine()) != null) {
                // execute query
                if (line.endsWith(";")) { //every SQL statement must end with semi-colon (;)
                    SQLString += line;
                    try(PreparedStatement statement = connection.prepareStatement(SQLString)) {
                        statement.execute();
                    }
                    SQLString = "";

                } else if (!line.equals("")) { //ignores new line spaces
                    SQLString += line;
                }
            }
        }

    }

    public static void populateCountriesTable(Connection connection) throws SQLException{
        List<String> allCountries = new ArrayList<>();
        String[] locales = Locale.getISOCountries();

        for (String countryCode : locales) {
            Locale obj = new Locale("", countryCode);
            allCountries.add(obj.getDisplayCountry());
        }
        allCountries.sort(String.CASE_INSENSITIVE_ORDER);

        for (String country : allCountries) {
            try(PreparedStatement statement = connection.prepareStatement(INSERT_COUNTRIES_STMT)) {
                statement.setString(1, country);
                statement.execute();
            }
        }
    }

    /**
     * for reseting and resampling test database.
     * provides the runSqlScript method with the file Path of
     * the SQL script and executes the method.
     * Log any success or failures when executing runSqlScript method.
     */
    public static void resetResampleTestDB() {
        JDBCDriver jdbcDriver = null;
        Connection connection = null;
        try {
            jdbcDriver = new JDBCDriver();
        } catch (PropertyVetoException ex) {
            ex.printStackTrace();
        }

        try {
            connection = jdbcDriver.getConnection();
            runSqlScript(RESET_DATABASE_SCRIPT_FILEPATH, connection);
            runSqlScript(RESAMPLE_DATABASE_SCRIPT_FILEPATH, connection);
            populateCountriesTable(connection);
            connection.close();
            Log.info("database reset and resample is successful");

        } catch (SQLException e) {
            Log.severe("Error when running SQL Script", e);
            System.err.println("Error when running SQL Script: " + e);

        } catch (IOException e) {
            Log.severe("Error when reading SQL Script file", e);
            System.err.println("Error when reading SQL Script file: " + e);
        }
    }

    /**
     * for reseting production database.
     * provides the runSqlScript method with the file Path of
     * the SQL script and executes the method.
     * Log any success or failures when executing runSqlScript method.
     */
    public static void resetProductionDB() {
        JDBCDriver jdbcDriver = null;
        Connection connection = null;
        try {
            jdbcDriver = new JDBCDriver();
        } catch (PropertyVetoException ex) {
            ex.printStackTrace();
        }

        try {
            //connection = jdbcDriver.getConnection(); //TODO: need connection for production database. -19 july
            runSqlScript(RESET_DATABASE_SCRIPT_FILEPATH, connection);
            populateCountriesTable(connection);
            connection.close();
            Log.info("database reset is successful");

        } catch (SQLException e) {
            Log.severe("Error when running SQL Script", e);
            System.err.println("Error when running SQL Script: " + e);

        } catch (IOException e) {
            Log.severe("Error when reading SQL Script file", e);
            System.err.println("Error when reading SQL Script file: " + e);
        }
    }


}
