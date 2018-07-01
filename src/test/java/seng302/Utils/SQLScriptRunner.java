package seng302.Utils;

import seng302.utils.JDBCDriver;
import seng302.utils.Log;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * method to run sql scripts
 */
public class SQLScriptRunner {

    /**
     * Opens a file based on filePath given, reads the file and execute the
     * SQL strings contained in the file.
     *
     * @param filePath location of the SQL script
     * @throws SQLException if any SQL error occurs
     * @throws IOException if any errors with reading the file occurs
     */
    public static void RunSqlScript(String filePath) throws SQLException, IOException{
        JDBCDriver jdbcDriver = new JDBCDriver();

        String absolutePath = new File("./").getAbsolutePath();
        absolutePath = absolutePath.substring(0, absolutePath.length()-1); //remove the full stop
        String scriptFilePath = absolutePath + filePath;

        String SQLString = "";
        BufferedReader reader = null;

        reader = new BufferedReader(new FileReader(scriptFilePath));

        String line = null;
        Connection connection = jdbcDriver.getTestConnection();

        // read script line by line
        while ((line = reader.readLine()) != null) {
            // execute query
            if(line.endsWith(";")){
                SQLString += line;
                PreparedStatement statement = connection.prepareStatement(SQLString);
                statement.execute();
                SQLString = "";

            } else if(!line.equals("")) {
                SQLString += line;
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
            RunSqlScript("src/main/resources/sqlScripts/createDataBase.sql");
            RunSqlScript("src/main/resources/sqlScripts/sampleDatabaseData.sql");

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
