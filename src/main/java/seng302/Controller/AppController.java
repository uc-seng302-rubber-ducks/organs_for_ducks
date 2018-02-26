package seng302.Controller;

import java.util.ArrayList;
import java.util.Date;
import seng302.Model.Donor;

public class AppController {

  private ArrayList<Donor> donors = new ArrayList<>();
  private static AppController controller;

  private  AppController() {
    //constructor goes here
  }

  /**
   * Returns the instance of the controller
   * @return AppController
   */
  public static AppController getInstance() {
    if (controller == null) {
      controller = new AppController();
    }
    return controller;
  }

  /**
   * appends a single Donor to the list of donors stored in the controller
   * @param name
   * @param dateOfBirth
   * @return boolean flag if the operation succeeded
   */
  public boolean Register(String name, Date dateOfBirth, Date dateOfDeath, String gender, double height, double weight,
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

      donors.add(newDonor);
      return true;
    }
    catch (Exception e){
      //TODO debug writer?
      System.err.println(e.getMessage());
      return false;
    }
  }

  public boolean Register(String name, Date dateOfBirth) {
    try {
      Donor newDonor = new Donor(name, dateOfBirth);
      donors.add(newDonor);
      return true;
    }
    catch (Exception e){
      //TODO debug writer?
      System.err.println(e.getMessage());
      return false;
    }
  }

  public ArrayList<Donor> getDonors() {
    return donors;
  }
}
