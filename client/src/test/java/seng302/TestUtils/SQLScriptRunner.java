package seng302.TestUtils;

import odms.commons.utils.JDBCDriver;
import odms.commons.utils.Log;

import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * method to run sql scripts
 */
public class SQLScriptRunner {

    private static String RESET_DATABASE_SCRIPT_FILEPATH = "src/main/resources/sqlScripts/createDataBase.sql";
    private static String RESAMPLE_DATABASE_SCRIPT_FILEPATH = "src/main/resources/sqlScripts/sampleDatabaseData.sql";

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
    public static void RunSqlScript(String filePath) throws SQLException, IOException{
        JDBCDriver jdbcDriver = null;
        try {
            jdbcDriver = new JDBCDriver();
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }

        String absolutePath = new File("./").getAbsolutePath();
        absolutePath = absolutePath.substring(0, absolutePath.length()-1); //remove the full stop
        String scriptFilePath = absolutePath + filePath;

        String SQLString = "";
        Connection connection;
        try (BufferedReader reader = new BufferedReader(new FileReader(scriptFilePath))) {

            String line;
            connection = jdbcDriver.getConnection();

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

        connection.close();
    }

    /**
     * provides the RunSqlScript method with the file Path of
     * the SQL script and executes the method.
     * Log any success or failures when executing RunSqlScript method.
     */
    public static void run(){
        try {
            RunSqlScript(RESET_DATABASE_SCRIPT_FILEPATH);
            RunSqlScript(RESAMPLE_DATABASE_SCRIPT_FILEPATH);

            Log.info("Database reset and resample is successful");

        } catch (SQLException e){
            Log.severe("Error when running SQL Script", e);
            System.err.println("Error when running SQL Script: " + e);

        } catch (IOException e){
            Log.severe("Error when reading SQL Script file", e);
            System.err.println("Error when reading SQL Script file: " + e);
        }
    }

}
