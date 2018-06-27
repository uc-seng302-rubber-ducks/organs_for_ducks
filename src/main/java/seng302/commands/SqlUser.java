package seng302.commands;

import picocli.CommandLine;
import seng302.utils.DBHandler;
import seng302.utils.Log;

import java.sql.ResultSet;
import java.sql.SQLException;


@CommandLine.Command(name="user", description = "Allows an SQL statement for user details to be entered")
public class SqlUser implements Runnable {

    @CommandLine.Option(names = {"-h", "help"}, usageHelp = true, description = "TODO: Put help here")

    @CommandLine.Parameters()
    private String statement;

    @Override
    public void run() {
        DBHandler dbHandler = new DBHandler();
        statement = statement.toUpperCase();
        if(statement.startsWith("SELECT")){
            ResultSet resultSet = null;
            try {
                resultSet = dbHandler.executeStatement(statement);
            } catch (SQLException e) {
                System.out.println("A database error has occurred, please try again or contact your administrator if the " +
                        "problem persists");
            }
            if (resultSet == null){
                System.out.println("An error occurred please try again");
                return;
            }
            try {
                while (resultSet.next()){
                    System.out.println(resultSet);
                }
            } catch (SQLException e) {
                Log.warning("SQL had an error retrieving the next result",e);
            }
        } else {
            System.out.println("This database is read only in SQL mode and only select statements may be run");
        }
    }
}
