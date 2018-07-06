package seng302.commands;


import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import seng302.utils.DBHandler;
import seng302.utils.JDBCDriver;
import seng302.utils.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;

@Command(name = "sql", description = " Command used to enter sql select statements")
public class Sql implements Runnable {

    @Option(names = {"-h", "help"}, usageHelp = true)
    boolean helpRequested = false;

    @Parameters(description = "String containing a select statement. e.g. SELECT * from User")
    private String[] statementArray;

    @Override
    public void run() {
        DBHandler dbHandler = new DBHandler();
        StringBuilder sb = new StringBuilder();
        String statement;
        for (String s : statementArray) {
            sb.append(s);
            sb.append(" ");
        }
        statement = sb.toString();

        //command should only be select statement
        if (!statement.toUpperCase().startsWith("SELECT") || statement.contains("sleep(")) {
            System.out.println("This database is read only in SQL mode and only select statements may be run");
            Log.warning("User attempted to run non-select command: " + statement);
            return;
        }

        ResultSet resultSet = null;
        Connection conn = null;
        try {
            JDBCDriver jdbcDriver = new JDBCDriver();
            conn = jdbcDriver.getConnection();
            conn.prepareStatement(statement);
        } catch (SQLException e) {
            System.out.println("Failed to establish connection to the database. please try again or contact your " +
                    "administrator if the problem persists");
            return;
        }
        try {
            resultSet = dbHandler.executeStatement(statement, conn);
        } catch (SQLException e) {
            System.out.println("An Invalid SQL statement was entered. Please ensure your command is in correct MySql syntax");
            Log.warning("invalid SQL statement caught", e);
            return;
        }
        if (resultSet == null) {
            //a successful call should at least return an empty ResultSet
            System.out.println("A database error has occurred, please try again or contact your administrator if the " +
                    "problem persists");
            return;
        }

        try {
            if (!resultSet.next()){
                System.out.println("No results were returned");
            } else{
                do {
                    System.out.println("!--------------------------------------------------------------------------!" );
                    ResultSetMetaData rsmd = resultSet.getMetaData();
                   int columns = rsmd.getColumnCount();
                   for(int i = 1; i <= columns; i++){
                       String columnName = resultSet.getString(i);
                       System.out.println( rsmd.getColumnName(i)+ " " + columnName );
                   }
                } while (resultSet.next());
            }
        } catch (SQLException e) {
            System.out.println("SQL had an error retrieving the next result");
            Log.warning("SQL had an error retrieving the next result", e);
        }
        try {
            conn.close();
        } catch (SQLException e) {
            Log.warning("Connection could not be closed", e);
        }
    }
}
