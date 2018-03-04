package seng302.View;


import java.util.Scanner;
import picocli.CommandLine;
import seng302.Controller.AppController;
import seng302.Model.CliCommands.CliRoot;
import seng302.Model.JsonReader;

public class CLI {

  public static void main(String[] args) {
    System.out.println("Welcome to the CLI. enter your command or type 'help' for help");

    AppController controller = AppController.getInstance();
    controller.setDonors(JsonReader.importJsonDonors());
    Scanner scanner = new Scanner(System.in);
    String input;
    String[] arguments;
    input = scanner.nextLine();
    while(!input.trim().equals("quit")) {
      arguments = input.split(" ");
      new CommandLine(new CliRoot())
          .parseWithHandler(new CommandLine.RunLast(), System.err, arguments);
      input = scanner.nextLine();
    }
  }
}
