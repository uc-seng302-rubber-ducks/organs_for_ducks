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


  private static boolean addOrgans(Scanner sc, Donor donor){
    System.out.println("Please enter which organs you want to donate");
    System.out.println("list the entries separated by commas");
    System.out.println("e.g. intestine,bone marrow,liver");
    sc.nextLine();
    String input = sc.nextLine();
    String[] organList = input.split(",");
    for(String o : organList){
      donor.addOrgan(Organs.valueOf(o.toUpperCase()));
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
      donor.removeOrgan(Organs.valueOf(item.toUpperCase()));
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

