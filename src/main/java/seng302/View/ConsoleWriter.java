package seng302.View;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import seng302.Controller.AppController;
import seng302.Model.Donor;
import seng302.Model.Organs;
import seng302.Model.JsonReader;
import seng302.Model.JsonWriter;

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
    System.out.println("e.g. intestine,bone marrow,liver");
    sc.nextLine();
    String input = sc.nextLine();
    String[] organList = input.split(",");
    for(String item: organList) {
      switch(item.toLowerCase()) {
        case "intestine":
          donor.addOrgan(Organs.INTESTINE);
          break;
        case "cornea":
          donor.addOrgan(Organs.CORNEA);
          break;
        case "liver":
          donor.addOrgan(Organs.LIVER);
          break;
        case "kidney":
          donor.addOrgan(Organs.KIDNEY);
          break;
        case "skin":
          donor.addOrgan(Organs.SKIN);
          break;
        case "connective tissue":
          donor.addOrgan(Organs.CONNECTIVE_TISSUE);
          break;
        case "pancreas":
          donor.addOrgan(Organs.PANCREAS);
          break;
        case "heart":
          donor.addOrgan(Organs.HEART);
          break;
        case "lung":
          donor.addOrgan(Organs.LUNG);
          break;
        case "middle ear":
          donor.addOrgan(Organs.MIDDLE_EAR);
          break;
        case "bone marrow":
          donor.addOrgan(Organs.BONE_MARROW);
          break;
        default:
          System.out.println("Organ "+item.toLowerCase() +" not recognised");
      }
    }

    return true;

  }

  private static boolean removeOrgans(Scanner sc, Donor donor) {
    System.out.println("These organs are currently listed for donation:");
    System.out.println(donor.getOrgans());
    System.out.println("Which organs are no longer available?");
    System.out.println("(e.g. Kidney,Bone marrow,heart)");
    sc.nextLine();
    String input = sc.nextLine();
    String[] organList = input.split(",");
    for (String item : organList) {
      switch (item.toLowerCase()) {
        case "intestine":
          donor.removeOrgan(Organs.INTESTINE);
          break;
        case "cornea":
          donor.removeOrgan(Organs.CORNEA);
          break;
        case "liver":
          donor.removeOrgan(Organs.LIVER);
          break;
        case "kidney":
          donor.removeOrgan(Organs.KIDNEY);
          break;
        case "skin":
          donor.removeOrgan(Organs.SKIN);
          break;
        case "connective tissue":
          donor.removeOrgan(Organs.CONNECTIVE_TISSUE);
          break;
        case "pancreas":
          donor.removeOrgan(Organs.PANCREAS);
          break;
        case "heart":
          donor.removeOrgan(Organs.HEART);
          break;
        case "lung":
          donor.removeOrgan(Organs.LUNG);
          break;
        case "middle ear":
          donor.removeOrgan(Organs.MIDDLE_EAR);
          break;
        case "bone marrow":
          donor.removeOrgan(Organs.BONE_MARROW);
          break;
        default:
          System.out.println("Organ " + item.toLowerCase() + " not recognised");
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

      controller.setDonors(JsonReader.importJsonDonors());

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
            ArrayList<Donor> allDonors = controller.getDonors();
            for (Donor d : allDonors) {
              System.out.println(d);
            }
          } else {
            code = Integer.parseInt(input);
            System.out.println(controller.getDonor(code));
          }
          break;

        case "update":
          System.out.println("Which donor do you want to update? (enter their unique code)");
          input = sc.next();
          if(input.equals("")){
            break;
          }
          code = Integer.parseInt(input);
          Donor donor = controller.getDonor(code);
          System.out.println("Do you want to add or remove organs to be donated?");
          System.out.println("(Add/Remove)");
          input = sc.next();
          if(input.equals("add")) {
            addOrgans(sc, donor);
          }
          else if (input.equals("remove")) {
            removeOrgans(sc, donor);
          }
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

