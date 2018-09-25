package odms.commands;

import odms.commons.model.dto.UserOverview;
import odms.controller.AppController;
import odms.view.IoHelper;
import picocli.CommandLine;
import picocli.CommandLine.Option;

import java.util.Set;

@CommandLine.Command(name = "all", description = "View all currently registered users based on set parameters. " +
        "Returns an overview of each user, to view more use view <nhi>")
public class ViewAll implements Runnable {

   @Option(names={"-c", "-count"}, description = "How many results to return. Default = 10")
    private int count =10;

    @Option(names={"-i", "-index", "-startIndex"}, description = "Where in the set to start returning results from. default = 0")
    private int startIndex =0;

    @Option(names={"-n", "-name"},description = "Enters a first last or middle name to search by")
    private String name= "";

    @Option(names={"-r", "-region"}, description = "Allows the returned results to be filtered by region")
    private String region = "";

    @Option(names={"-g", "-gender"}, description = "Allows the returned results to be filtered by birth gender")
    private String gender = "";

    @Option(names={"-h", "-help", ""}, usageHelp = true)
    private Boolean helpRequested;

    @Override
    public void run() {
        AppController controller = AppController.getInstance();
        controller.getUserBridge().getUsers(startIndex,count,name,region,gender,controller.getToken(), null);
        Set<UserOverview> overviewSet = controller.getUserOverviews();
        for (UserOverview overview : overviewSet) {
            IoHelper.display(overview.toString());
        }
    }
}
