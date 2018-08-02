package odms.commands;


import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.utils.SQLBridge;
import odms.view.IoHelper;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.util.List;

@Command(name = "sql", description = " Command used to enter sql select statements")
public class Sql implements Runnable {

    @Option(names = {"-h", "help"}, usageHelp = true)
    boolean helpRequested = false;

    @Parameters(description = "String containing a select statement. e.g. SELECT * from User")
    private String[] statementArray;

    @Override
    public void run() {
        AppController controller = AppController.getInstance();
        StringBuilder sb = new StringBuilder();
        SQLBridge sqlBridge = controller.getSqlBridge();
        String statement;
        for (String s : statementArray) {
            sb.append(s);
            sb.append(" ");
        }
        statement = sb.toString();

        //command should only be select statement
        if (!statement.toUpperCase().startsWith("SELECT") || statement.contains("sleep(")) {
            IoHelper.display("This database is read only in SQL mode and only select statements may be run");
            Log.warning("User attempted to run non-select command: " + statement);
            return;
        }

        try {
            List<String> result = sqlBridge.excuteSqlStatement(statement,controller.getToken());
            if(result.isEmpty()){
                IoHelper.display("The result set was empty an invalid query may have been entered or this result was no rows");
            } else {
                IoHelper.display("----------------------------------------------------------------------------------------");
                for(String s : result){
                    IoHelper.display(s);
                    IoHelper.display("----------------------------------------------------------------------------------------");
                }
            }


        } catch (IOException e) {
            Log.severe("Stuff went badly wrong", e);
            IoHelper.display("A fatal error occurred. Please try again");
        }

    }
}
