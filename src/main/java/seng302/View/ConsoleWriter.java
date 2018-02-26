package seng302.View;

import java.io.Console;
import java.util.Scanner;
import seng302.Controller.AppController;

public class ConsoleWriter {

  public static void main( String[] args ){
    System.out.println("Welcome to the CLI");
    String input = null;
    Scanner sc = new Scanner(System.in);
    AppController controller = AppController.getInstance();
    while(true) {
      input = sc.next();
      switch(input) {
        case "quit":
          System.exit(0);
        case "register":
          boolean response = controller.Register();
          System.out.println(response);
          break;

        default:
          System.out.println("Cannot find command "+input+"\n Please check your spelling");
      }
    }
  }
}

