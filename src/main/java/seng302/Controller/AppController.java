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

  private DonorController donorController = new DonorController();
  private AppController() {
    donors = JsonReader.importJsonDonors();
    clinicians = JsonReader.importClinicians();
    for(Clinician c : clinicians){
      if(c.getStaffId() == 0){
        return; //short circut out if defalut clinication exists
      }
    }
    clinicians.add(new Clinician("Default",0,"","","admin"));
    JsonWriter.saveClinicians(clinicians);
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
    if (donors.contains(donor)){
      donors.remove(donor);
      donors.add(donor);
    } else {
      donors.add(donor);
    }
    try {
      JsonWriter.saveCurrentDonorState(donors);
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
}
