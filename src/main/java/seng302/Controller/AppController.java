package seng302.Controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import seng302.Model.*;

public class AppController {

  private ArrayList<User> users = new ArrayList<>();
  private ArrayList<Clinician> clinicians = new ArrayList<>();
  private static AppController controller;
  private ArrayList<String[]> historyOfCommands = new ArrayList<>();
  private int historyPointer = 0;

  private DonorController donorController = new DonorController();
  private AppController() {
    try {
      users = JsonHandler.loadUsers();
      System.out.println(users.size() + " donors were successfully loaded");
      clinicians = JsonHandler.loadClinicians();
      System.out.println(clinicians.size() + " clinicians were successfully loaded");
    } catch (FileNotFoundException e) {
      System.out.println("File was not found");
    }
    String[] empty = {""};
    historyOfCommands.add(empty);//putting an empty string into the string array to be displayed if history pointer is 0
    boolean defaultSeen = false;
    for(Clinician c : clinicians){
      if(c.getStaffId().equals("0")){
        defaultSeen = true;
        System.out.println("Default seen");
        break;//short circuit out if default clinician exists
      }
    } //all code you wish to execute must be above this point!!!!!!!!
    if (!defaultSeen) {
      clinicians.add(new Clinician("Default", "0", "", "", "admin"));
      try {
        JsonHandler.saveClinicians(clinicians);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Returns the instance of the Controller
   *
   * @return AppController
   */
  public static AppController getInstance() {
    if (controller == null) {
      controller = new AppController();
    }
    return controller;
  }


  /**
   * appends a single Donor to the list of users stored in the Controller
   *
   * @return hashCode of the new donor or -1 on error
   */
  public int Register(String name, LocalDate dateOfBirth, LocalDate dateOfDeath, String gender, double height,
                      double weight,
      String bloodType, String currentAddress, String region, String NHI) {
    try {
      User newDonor = new User(name, dateOfBirth, NHI);
      newDonor.setDateOfDeath(dateOfDeath);
      newDonor.setGender(gender);
      newDonor.setHeight(height);
      newDonor.setWeight(weight);
      newDonor.setBloodType(bloodType);
      newDonor.setCurrentAddress(currentAddress);
      newDonor.setRegion(region);

      if (users.contains(newDonor)) {
        return -1;
      }
      users.add(newDonor);
      return newDonor.hashCode();
    } catch (Exception e) {

      //TODO debug writer?
      System.err.println(e.getMessage());
      return -1;
    }
  }


  public void setHistoryPointer() {
    this.historyPointer = historyOfCommands.size();
  }

  /*public String[] getCommandHistoryFromCorrectIndex() { ;
  }*/

  /**
   * Adds an executed command to the command history
   *
   * @param command Command to be added
   */
  public void addToHistoryOfCommands(String[] command){
    historyOfCommands.add(command);
  }


  /**
   * Updates the history pointer to ensure that the end of the array isnt overrun and the number stays positive so the history pointer doenst overrun.
   * @param amount -1 for older commands 1 for newer commands
   */
  public void historyPointerUpdate(int amount){
    if (historyPointer + amount <= 0){
      historyPointer = 0;
    } else if (historyPointer + amount > historyOfCommands.size()){
      historyPointer = historyOfCommands.size();
    }
  }

  /**
   * When called queries the history pointer and acquires the command located at the appropriate point
   * @return
   */
  public String[] getCommand(){
    return historyOfCommands.get(historyPointer);
  }

  /**
   *
   * @param name name of new user
   * @param dateOfBirth dob of new user
   * @param NHI NHI of new user
   * @return true if the user was created, false if there was an error or user already exists
   */
  public boolean Register(String name, LocalDate dateOfBirth, String NHI) {
    try {
      User newUser = new User(name, dateOfBirth, NHI);
      if (users.contains(newUser)) {
        return false;
      }
      users.add(newUser);
      return true;
    } catch (Exception e) {
      //TODO debug writer?
      System.err.println(e.getMessage());
      return false;
    }
  }

  /**
   * Takes a users name and dob, finds the donor in the session list and returns them.
   *
   * @param name Name of the donor
   * @param dob date of birth of the donor
   */
  public User findUser(String name, LocalDate dob) {
//    User check = null;
//    User testUser = new User(name,
//            dob); //creates temporary user to check against the user list
//    ArrayList<User> sessionList = getUsers();
//    int place = sessionList.indexOf(testUser);
//    if (place != -1) {
//      return sessionList.get(place);
//    } else {
//      return check;
//    }
    User toReturn = new User();
    for (User user : users) {
      if (user.getName().equals(name) && user.getDateOfBirth().equals(dob)) {
        toReturn = user;
      }
    }
    return toReturn;
  }

  /**
   * finds all users who's name field contains the search string
   */
  public ArrayList<User> findUsers(String name) {
    ArrayList<User> toReturn = new ArrayList<>();
    for (User user : users) {
      if (user.getName().toLowerCase().contains(name.toLowerCase())) {
        toReturn.add(user);
      }
    }
    return toReturn;
  }

  /**
   * Find user by NHI
   * @param NHI of user e.g. ABC1234
   * @return User corresponding to this NHI or null if none found
   */
  public User findUser(String NHI) {
    for (User u : users){
      if (u.getNHI().equals(NHI)) {
        return u;
      }
    }
    return null;
  }



  /**
   * takes a passed donor and removes them from the maintained list of users
   *
   * @param user user to remove
   */
  public void deleteDonor(User user) {
    ArrayList<User> sessionList = getUsers();
    sessionList.remove(user);
    setUsers(sessionList);
    //TODO fix json writer
//    try {
//      //JsonWriter.saveCurrentDonorState(sessionList);
//    } catch (IOException e) {
//      e.printStackTrace();
//    }

  }

  public ArrayList<User> getUsers() {
    return users;
  }

  /**
   * finds a user by their NHI
   *
   * @return Donor corresponding with the NHI given or null if dne
   */
  public User getUser(String NHI) {
    for (User user : users) {
      if (user.getNHI().equals(NHI)) {
        return user;
      }
    }
    return null;
  }

  /**
   * Method to update the user of any changes passed in by the gui.
   * Removes the old entry of the user form the list and then adds the updated entry
   * If the user is not already in the list it is added
   *
   * TODO: each user may need to be assigned a unique id for this part
   *
   * @param user user to be updated/added
   */
  public void update(User user){
      ArrayList<String > changelogWrite = new ArrayList<>();
      if (users.contains(user)){
        users.remove(user);
        users.add(user);
    } else {
      users.add(user);
      changelogWrite.add("Added Donor " + user.getName());
    }
    try {
      JsonHandler.saveUsers(users);
      //JsonHandler.saveChangelog(changelogWrite, donor.getName().toLowerCase().replace(" ", "_"));

    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public void setUsers(ArrayList<User> users) {
    this.users = users;
  }

  public Clinician getClinician(String id){
   for (Clinician c : clinicians){
     if (c.getStaffId().equals(id)) {
       return c;
     }
   }
      return new Clinician();
   }

   public void updateClinicians(Clinician clinician){
    if(clinicians.contains(clinician)){
    } else {
      clinicians.add(clinician);
    }

     try {
       JsonHandler.saveClinicians(clinicians);
     } catch (IOException e) {
       e.printStackTrace();
     }
   }

  public DonorController getDonorController() {
    return donorController;
  }

  public void setDonorController(DonorController donorController) {
    this.donorController = donorController;
  }


  public ArrayList<Change> differanceInDonors(User oldUser, User newUser){
   ArrayList<String> diffs = new ArrayList<>();
   try {
     if (!oldUser.getName().equalsIgnoreCase(newUser.getName())) {
       diffs.add("Changed Name from " + oldUser.getName() + " to " + newUser.getName());
     }
     if (oldUser.getDateOfBirth() != newUser.getDateOfBirth()) {
       diffs.add("Changed DOB from  " + oldUser.getDateOfBirth().toString() + " to " + newUser
           .getDateOfBirth());
     }
     if (oldUser.getDateOfDeath() != newUser.getDateOfDeath()) {
       diffs.add(
           "Changed DOD from " + oldUser.getDateOfDeath() + " to " + newUser.getDateOfDeath());
     }
     if (!(oldUser.getGender().equalsIgnoreCase(newUser.getGender()))) {
       diffs.add("Changed Gender from " + oldUser.getGender() + " to " + newUser.getGender());
     }
     if (oldUser.getHeight() != newUser.getHeight()) {
       diffs.add("Changed Height from " + oldUser.getHeight() + " to " + newUser.getHeight());
     }
     if (oldUser.getWeight() != newUser.getWeight()) {
       diffs.add("Changed Weight from " + oldUser.getWeight() + " to " + newUser.getWeight());
     }
     if (!oldUser.getBloodType().equalsIgnoreCase(newUser.getBloodType())) {
       diffs.add(
           "Changed Blood Type from " + oldUser.getBloodType() + " to " + newUser.getBloodType());
     }
     if (!oldUser.getCurrentAddress().equalsIgnoreCase(newUser.getCurrentAddress())) {
       diffs.add("Changed Address from " + oldUser.getCurrentAddress() + " to " + newUser
           .getCurrentAddress());
     }
     if (!oldUser.getRegion().equalsIgnoreCase(newUser.getRegion())) {
       diffs.add("Changes Region from " + oldUser.getRegion() + " to " + newUser.getRegion());
     }
     if (oldUser.getDeceased() != newUser.getDeceased()) {
       diffs.add(
           "Changed From Deceased = " + oldUser.getDeceased() + " to " + newUser.getDeceased());
     }
     if (oldUser.getDonorDetails().getOrgans() != newUser.getDonorDetails().getOrgans()) {
       diffs.add("Changed From Organs Donating = " + oldUser.getDonorDetails().getOrgans() + " to " + newUser
           .getDonorDetails().getOrgans());
     }
     for (String atty : oldUser.getMiscAttributes()) {
       if (!newUser.getMiscAttributes().contains(atty)) {
         diffs.add("Removed misc Atttribute " + atty);
       }
     }
     for (String atty : newUser.getMiscAttributes()) {
       if (!oldUser.getMiscAttributes().contains(atty)) {
         diffs.add("Added misc Attribute " + atty);
       }
     }

     for (String med : oldUser.getPreviousMedication()) {
       if (!newUser.getPreviousMedication().contains(med)) {
         diffs.add("Started taking " + med + " again");
       }
     }
     for (String med : newUser.getPreviousMedication()) {
       if (!oldUser.getPreviousMedication().contains(med)) {
         diffs.add(med + " was removed from the  users records");
       }
     }
     for (String med : oldUser.getCurrentMedication()) {
       if (!newUser.getCurrentMedication().contains(med)) {
         diffs.add("Stopped taking " + med);
       }
     }
     for (String med : newUser.getPreviousMedication()) {
       if (!oldUser.getPreviousMedication().contains(med)) {
         diffs.add("Started taking " + med);
       }
     }
   }
   catch (NullPointerException ex) {
     //no 'change', just added
     //TODO add "added __ to __" messages
   }
      ArrayList<Change> changes = new ArrayList<>();
      if (diffs.size() > 0) {
          for (String diff : diffs) {
              Change c = new Change(LocalDateTime.now(), diff);
              newUser.addChange(c);
              changes.add(c);
          }
      try {
        JsonHandler.saveChangelog(changes, newUser.getName().toLowerCase().replace(" ", "_"));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return changes;
  }
}
