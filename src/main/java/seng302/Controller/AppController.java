package seng302.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import seng302.Model.Clinician;
import seng302.Model.Donor;
import seng302.Model.JsonReader;
import seng302.Model.JsonWriter;

public class AppController {

  private ArrayList<Donor> donors = new ArrayList<>();
  private ArrayList<Clinician> clinicians = new ArrayList<>();
  private static AppController controller;
  private ArrayList<String[]> historyOfCommands = new ArrayList<>();
  private int historyPointer = 0;

  private DonorController donorController = new DonorController();
  private AppController() {
//    donors = JsonReader.importJsonDonors();
    clinicians = JsonReader.importClinicians();
    for(Clinician c : clinicians){
      if(c.getStaffId() == 0){
        return; //short circut out if defalut clinication exists
      }
    }
    clinicians.add(new Clinician("Default",0,"","","admin"));
    JsonWriter.saveClinicians(clinicians);
    String[] empty = {""};
    historyOfCommands.add(empty);//putting an empty string into the string array to be displayed if history pointer is 0
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
   * appends a single Donor to the list of donors stored in the Controller
   *
   * @return hashCode of the new donor or -1 on error
   */
  public int Register(String name, Date dateOfBirth, Date dateOfDeath, String gender, double height,
      double weight,
      String bloodType, String currentAddress, String region) {
    try {
      Donor newDonor = new Donor(name, dateOfBirth);
      newDonor.setDateOfDeath(dateOfDeath);
      newDonor.setGender(gender);
      newDonor.setHeight(height);
      newDonor.setWeight(weight);
      newDonor.setBloodType(bloodType);
      newDonor.setCurrentAddress(currentAddress);
      newDonor.setRegion(region);

      if (donors.contains(newDonor)) {
        return -1;
      }
      donors.add(newDonor);
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
   * @return hashCode of the new donor or -1 on error
   */
  public int Register(String name, Date dateOfBirth) {
    try {
      Donor newDonor = new Donor(name, dateOfBirth);
      if (donors.contains(newDonor)) {
        return -1;
      }
      donors.add(newDonor);
      return newDonor.hashCode();
    } catch (Exception e) {
      //TODO debug writer?
      System.err.println(e.getMessage());
      return -1;
    }
  }

  /**
   * Takes a donors name and dob, finds the donor in the session list and returns them.
   *
   * @param name Name of the donor
   * @param dob date of birth of the donor
   */
  public Donor findDonor(String name, Date dob) {
    Donor check = null;
    Donor testDonor = new Donor(name,
        dob); //creates temporary Donor to check against the donor list
    ArrayList<Donor> sessionList = getDonors();
    int place = sessionList.indexOf(testDonor);
    if (place != -1) {
      return sessionList.get(place);
    } else {
      return check;
    }
  }

  /**
   * finds all donors who's name field contains the search string
   */
  public ArrayList<Donor> findDonors(String name) {
    ArrayList<Donor> toReturn = new ArrayList<>();
    for (Donor donor : donors) {
      if (donor.getName().toLowerCase().contains(name.toLowerCase())) {
        toReturn.add(donor);
      }
    }
    return toReturn;
  }

  /**
   * Finds donor by name only. This method will need to be migrated to unique username in later builds
   * returns null if donor is not found
   */
  public Donor findDonor(String name) {
    Donor toReturn = null;
    for (Donor d : donors){
      if(d.getName().equalsIgnoreCase(name)){
        return d;
      }
    }
    return toReturn;
  }



  /**
   * takes a passed donor and removes them from the maintained list of donors
   *
   * @param donor donor to remove
   */
  public void deleteDonor(Donor donor) {
    ArrayList<Donor> sessionList = getDonors();
    sessionList.remove(donor);
    setDonors(sessionList);
    try {
      JsonWriter.saveCurrentDonorState(sessionList);
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public ArrayList<Donor> getDonors() {
    return donors;
  }

  /**
   * finds a single donor by their hashCode (unique id)
   *
   * @return Donor corresponding with the hashCode given or null if dne
   */
  public Donor getDonor(int hashCode) {
    for (Donor donor : donors) {
      if (donor.hashCode() == hashCode) {
        return donor;
      }
    }
    return null;
  }

  /**
   * Method to update the donor of any changes passed in by the gui.
   * Removes the old entry of the donor form the list and then adds the updated entry
   * If the donor is not already in the list it is added
   *
   * TODO: each donor may need to be assigned a unique id for this part
   *
   * @param donor donor to be updated/added
   */
  public void update(Donor donor){
      ArrayList<String > changelogWrite = new ArrayList<>();
      if (donors.contains(donor)){
        donors.remove(donor);
        donors.add(donor);
    } else {
      donors.add(donor);
      changelogWrite.add("Added Donor " + donor.getName());
    }
    try {
      JsonWriter.saveCurrentDonorState(donors);
      JsonWriter.changeLog(changelogWrite, donor.getName().toLowerCase().replace(" ", "_"));

    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public void setDonors(ArrayList<Donor> donors) {
    this.donors = donors;
  }

  public Clinician getClinician(int id){
   for (Clinician c : clinicians){
     if (c.getStaffId() == id) {
       return c;
     }
   }
      return new Clinician();
   }

   public void updateClinicians(Clinician clinician){
    if(clinicians.contains(clinician)){
      clinicians.remove(clinician);
      clinicians.add(clinician);

    } else {
      clinicians.add(clinician);
    }

    JsonWriter.saveClinicians(clinicians);
   }

  public DonorController getDonorController() {
    return donorController;
  }

  public void setDonorController(DonorController donorController) {
    this.donorController = donorController;
  }


  public ArrayList<String> differanceInDonors(Donor oldDonor, Donor newDonor){
   ArrayList<String> diffs = new ArrayList<>();
   try {
     if (!oldDonor.getName().equalsIgnoreCase(newDonor.getName())) {
       diffs.add("Changed Name from " + oldDonor.getName() + " to " + newDonor.getName());
     }
     if (oldDonor.getDateOfBirth() != newDonor.getDateOfBirth()) {
       diffs.add("Changed DOB from  " + oldDonor.getDateOfBirth().toString() + " to " + newDonor
           .getDateOfBirth());
     }
     if (oldDonor.getDateOfDeath() != newDonor.getDateOfDeath()) {
       diffs.add(
           "Changed DOD from " + oldDonor.getDateOfDeath() + " to " + newDonor.getDateOfDeath());
     }
     if (!(oldDonor.getGender().equalsIgnoreCase(newDonor.getGender()))) {
       diffs.add("Changed Gender from " + oldDonor.getGender() + " to " + newDonor.getGender());
     }
     if (oldDonor.getHeight() != newDonor.getHeight()) {
       diffs.add("Changed Height from " + oldDonor.getHeight() + " to " + newDonor.getHeight());
     }
     if (oldDonor.getWeight() != newDonor.getWeight()) {
       diffs.add("Changed Weight from " + oldDonor.getWeight() + " to " + newDonor.getWeight());
     }
     if (!oldDonor.getBloodType().equalsIgnoreCase(newDonor.getBloodType())) {
       diffs.add(
           "Changed Blood Type from " + oldDonor.getBloodType() + " to " + newDonor.getBloodType());
     }
     if (!oldDonor.getCurrentAddress().equalsIgnoreCase(newDonor.getCurrentAddress())) {
       diffs.add("Changed Address from " + oldDonor.getCurrentAddress() + " to " + newDonor
           .getCurrentAddress());
     }
     if (!oldDonor.getRegion().equalsIgnoreCase(newDonor.getRegion())) {
       diffs.add("Changes Region from " + oldDonor.getRegion() + " to " + newDonor.getRegion());
     }
     if (oldDonor.getDeceased() != newDonor.getDeceased()) {
       diffs.add(
           "Changed From Deceased = " + oldDonor.getDeceased() + " to " + newDonor.getDeceased());
     }
     if (oldDonor.getOrgans() != newDonor.getOrgans()) {
       diffs.add("Changed From Organs Donating = " + oldDonor.getOrgans() + " to " + newDonor
           .getOrgans());
     }
     for (String atty : oldDonor.getMiscAttributes()) {
       if (!newDonor.getMiscAttributes().contains(atty)) {
         diffs.add("Removed misc Atttribute " + atty);
       }
     }
     for (String atty : newDonor.getMiscAttributes()) {
       if (!oldDonor.getMiscAttributes().contains(atty)) {
         diffs.add("Added misc Attribute " + atty);
       }
     }

     for (String med : oldDonor.getPreviousMedication()) {
       if (!newDonor.getPreviousMedication().contains(med)) {
         diffs.add("Started taking " + med + " again");
       }
     }
     for (String med : newDonor.getPreviousMedication()) {
       if (!oldDonor.getPreviousMedication().contains(med)) {
         diffs.add(med + " was removed from the  donors records");
       }
     }
     for (String med : oldDonor.getCurrentMedication()) {
       if (!newDonor.getCurrentMedication().contains(med)) {
         diffs.add("Stopped taking " + med);
       }
     }
     for (String med : newDonor.getPreviousMedication()) {
       if (!oldDonor.getPreviousMedication().contains(med)) {
         diffs.add("Started taking " + med);
       }
     }
   }
   catch (NullPointerException ex) {
     //no 'change', just added
     //TODO add "added __ to __" messages
   }
      if(diffs.size() > 0){
          JsonWriter.changeLog(diffs,newDonor.getName().toLowerCase().replace(" ", "_"));
          for(String diff : diffs)
          newDonor.addChange(diff);
          return diffs;
      }
      return diffs;
  }
}
