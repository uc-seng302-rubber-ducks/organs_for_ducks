package seng302.commands;


import picocli.CommandLine.*;
import seng302.utils.DBHandler;
import seng302.utils.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

@Command(name="sql", description = " Command used to enter sql select statements")
public class Sql implements Runnable {

    @Option(names = {"-h", "help"}, usageHelp = true, description = "TODO: Put help here")
    boolean helpRequested = false;

    @Parameters()
    private String[] statementArray;

    @Override
    public void run() {
        DBHandler dbHandler = new DBHandler();
        StringBuilder sb = new StringBuilder();
        String statement;
        for( String s : statementArray){
            sb.append(s );
            sb.append(" ");
        }
        statement = sb.toString();
        statement = statement.toUpperCase();
        if(statement.startsWith("SELECT")) {
            ResultSet resultSet = null;
            Connection conn = null;
            try {
                System.out.println(statement);
                conn = dbHandler.getConnection();
                conn.prepareStatement(statement);
            } catch (SQLException e) {
                System.out.println("An Invalid SQL statement was entered. Please ensure your command is in MYSql");
                return;
            }
            try {
                resultSet = dbHandler.executeStatement(statement, conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (resultSet == null) {
                System.out.println("A database error has occurred, please try again or contact your administrator if the " +
                        "problem persists");
                return;
            }
            try {
                while (resultSet.next()) {
                    System.out.println("w");
                    System.out.println(resultSet);
                }
            } catch (SQLException e) {
                Log.warning("SQL had an error retrieving the next result", e);
            }
        } else {
            System.out.println("This database is read only in SQL mode and only select statements may be run");
        }
    }
}
