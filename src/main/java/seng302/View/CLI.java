package seng302.View;


import java.util.Scanner;
import picocli.CommandLine;
import seng302.Controller.AppController;
import seng302.Controller.CliCommands.CliRoot;
import seng302.Model.JsonReader;
import seng302.Model.JsonWriter;

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
      JsonWriter.changeLog(arguments);
      new CommandLine(new CliRoot())
          .parseWithHandler(new CommandLine.RunLast(), System.err, arguments);
      input = scanner.nextLine();
    }
  }
}
