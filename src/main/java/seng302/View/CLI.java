package seng302.View;


import org.jline.keymap.KeyMap;
import org.jline.reader.*;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp.Capability;
import picocli.CommandLine;
import seng302.Controller.AppController;
import seng302.Controller.CliCommands.Blockable;
import seng302.Controller.CliCommands.CliRoot;
import seng302.Directory;
import seng302.Model.JsonHandler;
import seng302.Model.User;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class to run the command line version of the application
 */
public class CLI {

  private static Blockable blockage = null;

  /**
   *
   * @return A line reader.
   */
  private static LineReader getLineReader() {
    try {
      TerminalBuilder builder = TerminalBuilder.builder();
      Terminal terminal = builder
          .system(true)
          .dumb(true)
          .build();
      History history = new DefaultHistory();
      LineReader lineReader = LineReaderBuilder
          .builder().terminal(terminal)
          .history(history)
          .build();
      KeyMap<Binding> keyMap = lineReader.getKeyMaps().get(LineReader.MAIN);
      keyMap.bind(new Reference(LineReader.UP_LINE_OR_HISTORY),
          KeyMap.key(terminal, Capability.key_up));
      keyMap.bind(new Reference(LineReader.DOWN_LINE_OR_HISTORY),
          KeyMap.key(terminal, Capability.key_down));
      return lineReader;
    } catch (IOException ex) {
      ex.printStackTrace();
      System.exit(1);
      return null;
    }
  }

  public static void main(String[] args) {
    System.out.println("Welcome to the CLI. enter your command or type 'help' for help");

    AppController controller = AppController.getInstance();
    try {
      controller.setUsers(
          (ArrayList<User>) JsonHandler.loadUsers(Directory.JSON.directory() + "/users.json"));
    } catch (FileNotFoundException e) {
      System.out.println("No users file exists. Creating blank session");
      controller.setUsers(new ArrayList<>());
    }

    String input;
    String[] arguments;
    LineReader lineReader = getLineReader();

    input = lineReader.readLine(">> ");
    while (!input.trim().equals("quit")) {
      parseInput(input, controller);
      input = lineReader.readLine(">> ");
    }
    System.out.println("CLI exited.");
    if (args != null && args[0].equals("gui")) {
      System.out.println("return to GUI");
    }
  }

  /**
   * Parses the user input and executes the command
   * @param input Whole String command to execute
   * @param controller Instance of the AppController
   */
  public static void parseInput(String input, AppController controller) {
    if (blockage == null) {
      String[] arguments = input.split(" ");
      controller.addToHistoryOfCommands(arguments);
      new CommandLine(new CliRoot())
              .parseWithHandler(new CommandLine.RunLast(), System.err, arguments);
    } else {
      blockage.confirm(input);
    }
  }

  public static void clearBlockage() {
    blockage = null;
  }

  public static void setBlockage(Blockable blockable) {
    blockage = blockable;
  }
}
