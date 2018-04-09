package seng302.Model;

import com.google.gson.annotations.Expose;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class User {

  @Expose
  private String NHI;
  @Expose
  private String name;
  @Expose
  private LocalDate dateOfBirth;
  @Expose
  private LocalDate dateOfDeath;
  @Expose
  private String gender;
  @Expose
  private double height;
  @Expose
  private double weight;
  @Expose
  private String bloodType;
  @Expose
  private String currentAddress;
  @Expose
  private String region;
  @Expose
  private LocalDateTime timeCreated;
  @Expose
  private Boolean isDeceased;

  @Expose
  private LocalDateTime lastModified;
  @Expose
  private ArrayList<String> miscAttributes;
  @Expose
  private HashMap<String, String> updateHistory;
  @Expose
  private ArrayList<String> previousMedication;
  @Expose
  private ArrayList<String> currentMedication;
  @Expose
  private HashMap<String, ArrayList<LocalDateTime>> previousMedicationTimes;
  @Expose
  private HashMap<String, ArrayList<LocalDateTime>> currentMedicationTimes;
  @Expose
  private ArrayList<Change> changes;

  //flags and extra details for if the person is a donor or a receiver
  @Expose
  private DonorDetails donorDetails;
  @Expose
  private ReceiverDetails receiverDetails;

  public User(java.time.LocalDate dateOfBirth, java.time.LocalDate dateOfDeath, String gender,
      double height, double weight,
      String bloodType,
      String currentAddress, String region, LocalDateTime timeCreated, String name,
      LocalDateTime lastModified,
      boolean isDeceased) {
    this.dateOfBirth = dateOfBirth;
    this.dateOfDeath = dateOfDeath;
    if (gender.startsWith("m") || gender.startsWith("M")) {
      this.gender = "M";
    } else if (gender.startsWith("f") || gender.startsWith("F")) {
      this.gender = "F";
    } else {
      this.gender = "U";
    }
    this.height = height;
    this.weight = weight;
    this.bloodType = groupBloodType(bloodType);
    this.currentAddress = currentAddress;
    this.region = region;
    if (timeCreated == null) {
      this.timeCreated = LocalDateTime.now();
    } else {
      this.timeCreated = timeCreated;
    }

    this.name = name;
    if (lastModified == null) {
      this.lastModified = LocalDateTime.now();
    } else {
      this.lastModified = lastModified;
    }
    this.isDeceased = isDeceased;
    updateHistory = new HashMap<>();
    this.miscAttributes = new ArrayList<>();
    this.currentMedication = new ArrayList<>();
    this.previousMedication = new ArrayList<>();
    this.currentMedicationTimes = new HashMap<String, ArrayList<LocalDateTime>>();
    this.previousMedicationTimes = new HashMap<String, ArrayList<LocalDateTime>>();

    this.donorDetails = new DonorDetails(this);
    this.receiverDetails = new ReceiverDetails(this);
    //TODO fix json reader
    try {
      changes = JsonHandler.importHistoryFromFile(name);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public User(String name, java.time.LocalDate dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
    this.name = name;
    timeCreated = LocalDateTime.now();
    lastModified = LocalDateTime.now();
    this.gender = "U";
    this.bloodType = "U";
    updateHistory = new HashMap<>();
    updateHistory.put(dateToString(getTimeCreated()), "Profile created.");
    this.miscAttributes = new ArrayList<>();
    this.currentMedication = new ArrayList<>();
    this.previousMedication = new ArrayList<>();
    this.currentMedicationTimes = new HashMap<String, ArrayList<LocalDateTime>>();
    this.previousMedicationTimes = new HashMap<String, ArrayList<LocalDateTime>>();

    this.donorDetails = new DonorDetails(this);
    this.receiverDetails = new ReceiverDetails(this);
    //TODO fix json reader
    //changes = JsonReader.importHistoryFromFile(this);
  }

  /**
   * empty constructor to allow an empty donor to be created for the gui
   */
  public User() {
    timeCreated = LocalDateTime.now();
    miscAttributes = new ArrayList<String>();
    this.currentMedication = new ArrayList<>();
    this.previousMedication = new ArrayList<>();
    this.currentMedicationTimes = new HashMap<String, ArrayList<LocalDateTime>>();
    this.previousMedicationTimes = new HashMap<String, ArrayList<LocalDateTime>>();

    this.donorDetails = new DonorDetails(this);
    this.receiverDetails = new ReceiverDetails(this);
    changes = new ArrayList<>();
  }


  public DonorDetails getDonorDetails() {
    return donorDetails;
  }

  public void setDonorDetails(DonorDetails donorDetails) {
    updateLastModified();
    this.donorDetails = donorDetails;
  }

  public ReceiverDetails getReceiverDetails() {
    return receiverDetails;
  }

  public void setReceiverDetails(ReceiverDetails receiverDetails) {
    updateLastModified();
    this.receiverDetails = receiverDetails;
  }

  //TODO details object is set at initialization. will always return true
  public boolean isDonor() {
    if (this.donorDetails == null) {
      return false;
    }
    return !this.donorDetails.isEmpty();

  }

  public boolean isReceiver() {
    if (this.receiverDetails == null) {
      return false;
    }
    return !this.receiverDetails.isEmpty();
  }

  public String getNHI() {
    return NHI;
  }

  public void setNHI(String NHI) {
    updateLastModified();
    this.NHI = NHI;
  }

  /**
   * Utility function to update the last modified timestamp when a change is made to a donor. Can be
   * changed later to allow writing to the JSON change log latter
   */
  public void updateLastModified() {
    lastModified = LocalDateTime.now();
  }

  public LocalDateTime getLastModified() {
    return lastModified;
  }

  //For UndoRedoStacks
  public void setLastModified(LocalDateTime lastModified) {
    this.lastModified = lastModified;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    updateLastModified();
    this.name = name;
  }

  public java.time.LocalDate getDateOfBirth() {
    return dateOfBirth;
  }

  public void setDateOfBirth(java.time.LocalDate dateOfBirth) {
    updateLastModified();
    this.dateOfBirth = dateOfBirth;
  }

  public java.time.LocalDate getDateOfDeath() {
    return dateOfDeath;
  }

  public void setDateOfDeath(java.time.LocalDate dateOfDeath) {
    updateLastModified();
    this.dateOfDeath = dateOfDeath;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    updateLastModified();
    this.gender = gender;
  }

  public double getHeight() {
    return height;
  }

  public void setHeight(double height) {
    updateLastModified();
    this.height = height;
  }

  public double getWeight() {
    return weight;
  }

  public void setWeight(double weight) {
    updateLastModified();
    this.weight = weight;
  }

  public String getBloodType() {
    return bloodType;
  }

  public void setBloodType(String bloodType) {
    updateLastModified();
    this.bloodType = bloodType;
  }

  public String getCurrentAddress() {
    return currentAddress;
  }

  public void setCurrentAddress(String currentAddress) {
    updateLastModified();
    this.currentAddress = currentAddress;
  }

  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    updateLastModified();
    this.region = region;
  }

  public LocalDateTime getTimeCreated() {
    return timeCreated;
  }

  //For UndoRedoStacks
  public void setTimeCreated(LocalDateTime timeCreated) {
    updateLastModified();
    this.timeCreated = timeCreated;
  }

  public String getAge() {
    if (dateOfDeath != null) {

      return Long.toString(ChronoUnit.YEARS.between(dateOfBirth, dateOfDeath));
    }
    return Long.toString(ChronoUnit.YEARS.between(dateOfBirth, java.time.LocalDate.now()));
  }

  public Boolean getDeceased() {
    return isDeceased;
  }

  public void setDeceased(Boolean deceased) {
    updateLastModified();
    isDeceased = deceased;
  }

  /**
   * Method to ensure that all blood types are valid blood types returns U if not a valid blood
   * type
   *
   * @param possibleType type to test
   * @return correct blood type
   */
  public String groupBloodType(String possibleType) {

    if (possibleType == null) {
      return "U";
    }
    if (possibleType.equalsIgnoreCase("AB+")) {
      return "AB+";
    } else if (possibleType.equalsIgnoreCase("AB-")) {
      return "AB-";
    } else if (possibleType.equalsIgnoreCase("A+")) {
      return "A+";
    } else if (possibleType.equalsIgnoreCase("A-")) {
      return "A-";
    } else if (possibleType.equalsIgnoreCase("B+")) {
      return "B+";
    } else if (possibleType.equalsIgnoreCase("A-")) {
      return "B-";
    } else if (possibleType.equalsIgnoreCase("O+")) {
      return "O+";
    } else if (possibleType.equalsIgnoreCase("O-")) {
      return "O-";
    } else {
      return "U";
    }
  }


  public ArrayList<String> getMiscAttributes() {
    return miscAttributes;
  }

  public void setMiscAttributes(ArrayList<String> miscAttributes) {
    updateLastModified();
    this.miscAttributes = miscAttributes;
  }

  // @TODO: find all instances of potential updates and add to the Hashmap

  public HashMap<String, String> getUpdateHistory() {
    return updateHistory;
  }

  public void setUpdateHistory(HashMap<String, String> updateHistory) {
    this.updateHistory = updateHistory;
  }

  private String dateToString(LocalDateTime dateTime) {
    return dateTime.toString();
  }

  public void addToUpdateHistory(LocalDateTime dateTime, String action) {
    String timeStamp = dateToString(dateTime);
    updateHistory.put(timeStamp, action);
  }

  public void removeMiscAttribute(String attribute) {
    miscAttributes.remove(attribute);
  }

  public void addAttribute(String attribute) {
    updateLastModified();
    miscAttributes.add(attribute);
  }

  public ArrayList<String> getPreviousMedication() {
    return previousMedication;
  }

  public void setPreviousMedication(ArrayList<String> previousMedication) {
    this.previousMedication = previousMedication;
  }

  public ArrayList<String> getCurrentMedication() {
    return currentMedication;
  }

  public void setCurrentMedication(ArrayList<String> currentMedication) {
    this.currentMedication = currentMedication;
  }

  public void addCurrentMedication(String medication) {
    updateLastModified();
    currentMedication.add(medication);
    addMedicationTimes(medication, currentMedicationTimes);
  }

  public void addPreviousMedication(String medication) {
    updateLastModified();
    previousMedication.add(medication);
    addMedicationTimes(medication, previousMedicationTimes);
  }

  public void addCurrentMedicationSetup(String medication) {
    updateLastModified();
    currentMedication.add(medication);
  }

  public void addPreviousMedicationSetUp(String medication) {
    updateLastModified();
    previousMedication.add(medication);
  }


  public void removeCurrentMedication(String medication) {
    updateLastModified();
    currentMedication.remove(medication);
  }

  public void removePreviousMedication(String medication) {
    updateLastModified();
    previousMedication.remove(medication);
  }

  public HashMap<String, ArrayList<LocalDateTime>> getPreviousMedicationTimes() {
    return previousMedicationTimes;
  }

  public void setPreviousMedicationTimes(
      HashMap<String, ArrayList<LocalDateTime>> previousMedicationTimes) {
    updateLastModified();
    this.previousMedicationTimes = previousMedicationTimes;
  }

  public HashMap<String, ArrayList<LocalDateTime>> getCurrentMedicationTimes() {
    return currentMedicationTimes;
  }


  public void setCurrentMedicationTimes(
      HashMap<String, ArrayList<LocalDateTime>> currentMedicationTimes) {
    updateLastModified();
    this.currentMedicationTimes = currentMedicationTimes;
  }

  /**
   * Use this one when adding a new medication from the donor interface
   *
   * @param medication medication to be added
   * @param medicationTimes hashmap to be appended to
   */
  public void addMedicationTimes(String medication,
      HashMap<String, ArrayList<LocalDateTime>> medicationTimes) {
    LocalDateTime time = LocalDateTime.now();
    updateLastModified();
    ArrayList<LocalDateTime> previouslyExists;
    try {
      previouslyExists = medicationTimes.get(medication);
      previouslyExists.add(time);
    } catch (NullPointerException e) {
      previouslyExists = new ArrayList<>();
      previouslyExists.add(time);
    }

    medicationTimes.put(medication, previouslyExists);
    updateLastModified();
  }


  /**
   * Use this one when creating the user from the json object
   *
   * @param medication medication string key
   * @param stamps list of timestamps
   */
  public void addCurrentMedicationTimes(String medication, ArrayList<LocalDateTime> stamps) {

    currentMedicationTimes.put(medication, stamps);
    updateLastModified();
  }


  /**
   * Use this one when creating the user from the json object
   *
   * @param medication medication string key
   * @param stamps list of timestamps
   */
  public void addPreviousMedicationTimes(String medication, ArrayList<LocalDateTime> stamps) {
    previousMedicationTimes.put(medication, stamps);
    updateLastModified();
  }


  public ArrayList<Change> getChanges() {
    return changes;
  }

  public void setChanges(ArrayList<Change> changes) {
    this.changes = changes;
  }

  public void addChange(Change change) {
    changes.add(change);
  }

  public String getTooltip() {
    //TODO fix this to show full info where possible
    if (this.donorDetails.getOrgans() == null) {
      return name;
    }
    if (!this.getDonorDetails().getOrgans().isEmpty()) {
      String toReturn = name + ". Donor: ";
      for (Organs o : this.donorDetails.getOrgans()) {
        toReturn += o.toString() + " ";
      }
      return toReturn;
    } else {
      return name;
    }
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User other = (User) o;
    //TODO change to use NHI when this is implemented. same with hashcode
    return this.NHI.equals(other.NHI);
    //return Objects.equals(dateOfBirth, other.dateOfBirth) && name.equalsIgnoreCase(other.name);
  }

  @Override
  public int hashCode() {

    return Objects.hash(dateOfBirth, name);
  }

  @Override
  public String toString() {
    return "name:'" + name + "\'" +
        "\ndate Of Birth: " + dateOfBirth +
        "\ndate Of Death :" + dateOfDeath +
        "\ngender: " + gender +
        "\nheight: " + height +
        "\nweight: " + weight +
        "\nblood Type: '" + bloodType + '\'' +
        "\ncurrent Address: '" + currentAddress + '\'' +
        "\nregion: '" + region + '\'' +
        "\norgans: " + donorDetails.getOrgans() +
        "\ntime Created: " + timeCreated +
        "\nlast modified: " + lastModified +
        "\nhashcode=" + hashCode();
  }
}
