package odms.commands;

import odms.controller.AppController;
import picocli.CommandLine;

import java.util.List;

@CommandLine.Command(name = "countries", description = "Displays all currently available countries")
public class ViewCountries implements Runnable {

    @Override
    public void run() {

        AppController controller = AppController.getInstance();
        List<String> countries = controller.getAllowedCountries();

        System.out.println("There are " + countries.size() + " countries currently available:");
        for (String country : countries) {
            System.out.println(country);
        }

    }
}
