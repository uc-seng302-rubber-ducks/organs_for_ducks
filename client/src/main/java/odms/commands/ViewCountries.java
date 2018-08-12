package odms.commands;

import odms.controller.AppController;
import odms.view.IoHelper;
import picocli.CommandLine;

import java.util.List;

@CommandLine.Command(name = "countries", description = "Displays all currently available countries")
public class ViewCountries implements Runnable {

    @Override
    public void run() {
        AppController controller = AppController.getInstance();
        List<String> countries = controller.getAllowedCountries();

        IoHelper.display("There are " + countries.size() + " countries currently allowed:");
        for (String country : countries) {
            IoHelper.display(country);
        }

    }
}
