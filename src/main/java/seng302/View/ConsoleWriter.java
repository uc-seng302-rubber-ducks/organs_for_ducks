package seng302.View;

import java.io.Console;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import seng302.Controller.AppController;
import seng302.Model.Donor;
import seng302.Model.Organs;

public class ConsoleWriter {

  private static int register(AppController controller, Scanner sc, Boolean fullInfo) {

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
      int response = controller.Register(name, dateOfBirth, dateOfDeath, gender, height,
          weight, bloodType, currentAddress, region);
      return response;
    }
    int response = controller.Register(name, dateOfBirth);
    return response;
  }

  private static boolean addOrgans(Scanner sc, Donor donor) {
    System.out.println("Please enter which organs you want to donate");
    System.out.println("list the entries separated by commas");
    System.out.println("e.g. intestine, bone marrow");
    sc.nextLine();
    String input = sc.nextLine();
    String[] organList = input.split(",");
    for(String item: organList) {
      switch(item.toLowerCase()) {
        case "intestine":
          donor.AddOrgan(Organs.INTESTINE);
          break;
        case "cornea":
          donor.AddOrgan(Organs.CORNEA);
          break;
        case "liver":
          donor.AddOrgan(Organs.LIVER);
          break;
        case "kidney":
          donor.AddOrgan(Organs.KIDNEY);
          break;
        case "skin":
          donor.AddOrgan(Organs.SKIN);
          break;
        case "connective tissue":
          donor.AddOrgan(Organs.CONNECTIVE_TISSUE);
          break;
        case "pancreas":
          donor.AddOrgan(Organs.PANCREAS);
          break;
        case "heart":
          donor.AddOrgan(Organs.HEART);
          break;
        case "lung":
          donor.AddOrgan(Organs.LUNG);
          break;
        case "middle ear":
          donor.AddOrgan(Organs.MIDDLE_EAR);
          break;
        case "bone marrow":
          donor.AddOrgan(Organs.BONE_MARROW);
          break;
        default:
          System.out.println("Organ "+item.toLowerCase() +" not recognised");
      }
    }

    return true;

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

  public static void main( String[] args ){
    System.out.println("Welcome to the CLI");
    String input;
    Scanner sc = new Scanner(System.in);
    AppController controller = AppController.getInstance();
    while(true) {
      input = sc.next();
      switch(input) {
        case "quit":
          System.exit(0);
        case "register":
          System.out.println("Do you wish to do a full sign-up or simple? (true for full)");
          boolean full = sc.nextBoolean();
          int code = register(controller, sc, full);
          if (code == -1) {
            System.out.println("Donor already exists");
            break;
          }
          System.out.println("Donor registered with unique id: "+code);
          addOrgans(sc, controller.getDonor(code));
          break;

        case "view":
          System.out.println("Please enter the unique id of the donor you wish to view");
          System.out.println("all for all donors");
          input = sc.next();
          if(input.equals("all")) {
            System.out.println(controller.getDonors());
          } else {
            code = Integer.parseInt(input);
            System.out.println(controller.getDonor(code));
          }
          break;
        default:
          System.out.println("Cannot find command: "+input+"\n Please check your spelling");
      }
    }
  }
}

