package seng302.View;


import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Scanner;
import org.jline.keymap.KeyMap;
import org.jline.reader.Binding;
import org.jline.reader.History;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.Reference;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp.Capability;
import picocli.CommandLine;
import seng302.Controller.AppController;
import seng302.Controller.CliCommands.CliRoot;
import seng302.Model.JsonReader;
import seng302.Model.JsonWriter;

public class CLI {

  private static LineReader getLineReader() {
    try {
      TerminalBuilder builder = TerminalBuilder.builder();
      Terminal terminal = builder.system(true).build();
      History history = new DefaultHistory();

      LineReader lineReader = LineReaderBuilder
          .builder().terminal(terminal).history(history).build();
      KeyMap<Binding> keyMap = lineReader.getKeyMaps().get(LineReader.MAIN);
      keyMap.bind(new Reference(LineReader.UP_LINE_OR_HISTORY), KeyMap.key(terminal, Capability.key_up));
      keyMap.bind(new Reference(LineReader.DOWN_LINE_OR_HISTORY), KeyMap.key(terminal, Capability.key_down));
      return lineReader;
    }
    catch (IOException ex) {
      ex.printStackTrace();
      System.exit(1);
      return null;
    }
  }

  public static void main(String[] args) {
    System.out.println("Welcome to the CLI. enter your command or type 'help' for help");

    AppController controller = AppController.getInstance();
    controller.setDonors(JsonReader.importJsonDonors());

    String input;
    String[] arguments;
    LineReader lineReader = getLineReader();

    input = lineReader.readLine();
    while(!input.trim().equals("quit")) {
      arguments = input.split(" ");
      JsonWriter.changeLog(arguments);
      controller.addToHistoryOfCommands(arguments);
      new CommandLine(new CliRoot())
          .parseWithHandler(new CommandLine.RunLast(), System.err, arguments);
      //System.out.println(lineReader.getHistory().last());
      input = lineReader.readLine();

    }
  }
}
