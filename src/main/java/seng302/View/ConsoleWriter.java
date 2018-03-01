package seng302.View;

import java.io.Console;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import seng302.Controller.AppController;
import seng302.Model.Donor;
import seng302.Model.JsonReader;
import seng302.Model.JsonWriter;

public class ConsoleWriter {

  private static boolean register(AppController controller, Scanner sc, Boolean fullInfo) {

    System.out.println("Please enter a name");
    String name = sc.next();

    System.out.println("Please enter a date of birth in the format \"21/12/2018\"");
    String rawDate = sc.next();
    Date dateOfBirth = readDate(rawDate);

    if (fullInfo) {
      System.out.println("Please enter a date of death");
      rawDate = sc.next();
      Date dateOfDeath = readDate(rawDate);

      System.out.println("Please enter a gender");
      //TODO enforce some kind of format (m/f)
      String gender = sc.next();

      System.out.println("Please enter a height in m (e.g. 1.85)");
      double height = Double.parseDouble(sc.next());
      System.out.println("Please enter a weight in kg (e.g. 75)");
      double weight = Double.parseDouble(sc.next());
      System.out.println("Please enter a blood type");
      String bloodType = sc.next();

      String currentAddress = "";
      String region = "";
      //details not relevant if person is deceased
      if (dateOfDeath == null) {
        System.out.println("Please enter a current address");
        currentAddress = sc.next();
        System.out.println("Please enter a region");
        region = sc.next();
      }
      boolean response = controller.Register(name, dateOfBirth, dateOfDeath, gender, height,
          weight, bloodType, currentAddress, region);
      return response;
    }
    boolean response = controller.Register(name, dateOfBirth);
    return response;
  }

  private static Date readDate(String rawDate) {
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    Date date;
    try {
      date = sdf.parse(rawDate);
    }
    catch (ParseException e) {
      System.err.println("Error parsing date");
      date = null;
    }
    return date;
  }

  public static void delete(AppController controller, Scanner sc){
      System.out.println("Please enter the name of the person you wish to delete");
      String name = sc.next();
      System.out.println("and the date of birth(dd/mm/yyy)");
      String dobStr = sc.next();
      Date dob = readDate(dobStr);
      Donor toDelete = controller.findDonor(name, dob);
      if (toDelete == null){
          System.out.println("The Donor could not be found please try again");
      } else {
          System.out.println("Is this the donor you wish to delete:/n" + toDelete.toString());
          boolean acceptable = false;
          while(!acceptable) {
              System.out.println("Please enter (y/n)");
              String reponse = sc.next();
              if(reponse.equalsIgnoreCase("y")){
                  controller.deleteDonor(toDelete);
                  acceptable = true;
                  System.out.println("Donor " + toDelete.getName()+ " has been deleted");
              }else if (reponse.equalsIgnoreCase("n")){
                  System.out.println("Donor has not been removed");
                  acceptable = true;
              } else {
                  System.out.println("Response was invalid please enter a valid response");
              }
          }

      }
  }

  public static void main( String[] args ){
    System.out.println("Welcome to the CLI");
    String input;
    Scanner sc = new Scanner(System.in);
    AppController controller = AppController.getInstance();
    try {
      controller.setDonors(JsonReader.importJsonDonors());
    } catch (IOException e) {
      e.printStackTrace();
    }
    while(true) {
      input = sc.next();
      switch(input) {
        case "quit":
          try {
            JsonWriter.saveCurrentDonorState(controller.getDonors());
          } catch (IOException e) {
            e.printStackTrace();
          }
          System.exit(0);
        case "register":
          System.out.println("Do you wish to do a full sign-up or simple? (true for full)");
          boolean full = sc.nextBoolean();
          register(controller, sc, full);
          break;
          case "delete":
             delete(controller, sc);
              break;

          default:
          System.out.println("Cannot find command: "+input+"\n Please check your spelling");
      }
    }
  }
}

