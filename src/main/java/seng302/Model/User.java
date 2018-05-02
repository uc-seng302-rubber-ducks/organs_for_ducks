package seng302.Model;

import com.google.gson.annotations.Expose;
import org.joda.time.DateTime;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;


public class User {

  @Expose
  private String nhi;
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
  private String firstName;
  @Expose
  private String preferredFirstName;
  @Expose
  private String middleName;
  @Expose
  private String lastName;
  @Expose
  private String birthGender;
  @Expose
  private String genderIdentity;
  @Expose
  private String alcoholConsumption;
  @Expose
  private boolean smoker;
  @Expose
  private String homePhone;
  @Expose
  private String cellPhone;
  @Expose
  private String email;
  @Expose
  private EmergencyContact contact;

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
  @Expose
  private  ArrayList<MedicalProcedure> medicalProcedures;

  //flags and extra details for if the person is a donor or a receiver
  @Expose
  private DonorDetails donorDetails = new DonorDetails(this);
  @Expose
  private ReceiverDetails receiverDetails = new ReceiverDetails(this);

  @Expose
  private ArrayList<Disease> pastDiseases;

  @Expose
  private ArrayList<Disease> currentDiseases;

    // updated constructor that works with the creation page
    public User(String nhi, LocalDate dateOfBirth, LocalDate dateOfDeath, String birthGender, String genderIdentity,
    double height, double weight, String bloodType, String alcoholConsumption,boolean smoker,
    String currentAddress, String region, String homePhone, String cellPhone, String email,
            EmergencyContact contact, String name, String firstName, String preferredFirstName, String middleName,
            String lastName){

      this.nhi = nhi;
      this.dateOfBirth = dateOfBirth;
      this.dateOfDeath = dateOfDeath;

      this.birthGender = birthGender;
      this.genderIdentity = genderIdentity;
      this.height = height;
      this.weight = weight;
      this.bloodType = bloodType;
      this.alcoholConsumption = alcoholConsumption;
      this.smoker = smoker;

      this.currentAddress = currentAddress;
      this.region = region;
      this.homePhone = homePhone;
      this.cellPhone = cellPhone;
      this.email = email;
      this.contact = contact;

      this.name = name;
      this.firstName = firstName;
      this.preferredFirstName = preferredFirstName;
      this.middleName = middleName;
      this.lastName = lastName;

      this.timeCreated = LocalDateTime.now();
      updateHistory = new HashMap<>();
      this.miscAttributes = new ArrayList<>();
      this.currentMedication = new ArrayList<>();
      this.previousMedication = new ArrayList<>();
      this.currentMedicationTimes = new HashMap<>();
      this.previousMedicationTimes = new HashMap<>();
        this.donorDetails = new DonorDetails(this);
        this.receiverDetails = new ReceiverDetails(this);

      this.currentDiseases = new ArrayList<>();
      this.pastDiseases = new ArrayList<>();
      this.medicalProcedures  = new ArrayList<>();

      try {
        changes = JsonHandler.importHistoryFromFile(name);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }

    }


  public User(String name, java.time.LocalDate dateOfBirth, String nhi) {
    this.dateOfBirth = dateOfBirth;
    this.name = name;
    this.donorDetails = new DonorDetails(this);
    this.firstName = name;
    this.receiverDetails = new ReceiverDetails(this);
    this.nhi = nhi;
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
    try {
      changes = JsonHandler.importHistoryFromFile(name);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    this.currentDiseases = new ArrayList<>();
    this.pastDiseases = new ArrayList<>();

    this.donorDetails = new DonorDetails(this);
    this.receiverDetails = new ReceiverDetails(this);
    this.medicalProcedures =  new ArrayList<>();
    try {
      changes = JsonHandler.importHistoryFromFile(name);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }


  /**
   * empty constructor to allow an empty donor to be created for the gui
   */
  public User() {
    timeCreated = LocalDateTime.now();
    miscAttributes = new ArrayList<String>();
    this.currentMedication = new ArrayList<>();
    this.previousMedication = new ArrayList<>();

    this.currentDiseases = new ArrayList<>();
    this.pastDiseases = new ArrayList<>();

    this.currentMedicationTimes = new HashMap<String, ArrayList<LocalDateTime>>();
    this.previousMedicationTimes = new HashMap<String, ArrayList<LocalDateTime>>();
    this.medicalProcedures = new ArrayList<>();
    this.donorDetails = new DonorDetails(this);
    this.receiverDetails = new ReceiverDetails(this);
    changes = new ArrayList<>();
  }

    public EmergencyContact getContact() {
        return contact;
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

  public String getNhi() {
    return nhi;
  }

  public void setNhi(String nhi) {
    updateLastModified();
    this.nhi = nhi;
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


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String name) {
        updateLastModified();
        this.firstName = name;
    }

    public String getPrefFirstName() {
        return preferredFirstName;
    }

    public void setPreferredFirstName(String preferredFirstName) {
        updateLastModified();
        this.preferredFirstName = preferredFirstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String name) {
        updateLastModified();
        this.middleName = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String name) {
        updateLastModified();
        this.lastName = name;
    }

    public String getFullName() {
        String fullName;

        if (middleName != null && lastName != null) {
            fullName = firstName + " " + middleName  + " " + lastName;

        } else if (middleName != null) {
            fullName = firstName + " " + middleName;

        } else if (lastName != null) {
            fullName = firstName + " " + lastName;

        } else {
            fullName = firstName;
        }

        return fullName;
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
    String validType = groupBloodType(bloodType);
    updateLastModified();
    this.bloodType = validType;
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

  public String getStringAge() {
    if (dateOfDeath != null) {

      return Long.toString(ChronoUnit.YEARS.between(dateOfBirth, dateOfDeath));
    }
    return Long.toString(ChronoUnit.YEARS.between(dateOfBirth, java.time.LocalDate.now()));
  }

  public int getAge() {
    if (dateOfDeath != null) {

      return Math.toIntExact(ChronoUnit.YEARS.between(dateOfBirth, dateOfDeath));
    }
    return Math.toIntExact(ChronoUnit.YEARS.between(dateOfBirth, java.time.LocalDate.now()));
  }

  public Boolean getDeceased() {
    return isDeceased;
  }

  public void setDeceased(Boolean deceased) {
    updateLastModified();
    isDeceased = deceased;
  }

  public ArrayList<Disease> getCurrentDiseases() {
    return currentDiseases;
  }

  public void addCurrentDisease(Disease currentDisease) {
    currentDiseases.add(currentDisease);
  }

  public ArrayList<Disease> getPastDiseases() {
    return pastDiseases;
  }

  public void addPastDisease(Disease pastDisease) {
    this.pastDiseases.add(pastDisease);
  }

  public String getBirthGender() {
    return birthGender;
  }

  public void setBirthGender(String birthGender) {
    this.birthGender = birthGender;
  }

  public String getGenderIdentity() {
    return genderIdentity;
  }

  public void setGenderIdentity(String genderIdentity) {
    this.genderIdentity = genderIdentity;
  }

  public String getAlcoholConsumption() {
    return alcoholConsumption;
  }

  public void setAlcoholConsumption(String alcoholConsumption) {
    this.alcoholConsumption = alcoholConsumption;
  }

  public boolean isSmoker() {
    return smoker;
  }

  public void setSmoker(boolean smoker) {
    this.smoker = smoker;
  }

  public String getHomePhone() {
    return homePhone;
  }

  public void setHomePhone(String homePhone) {
    this.homePhone = homePhone;
  }

  public String getCellPhone() {
    return cellPhone;
  }

  public void setCellPhone(String cellPhone) {
    this.cellPhone = cellPhone;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setContact(EmergencyContact contact) {
    this.contact = contact;
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
    } else if (possibleType.equalsIgnoreCase("B-")) {
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

  public ArrayList<MedicalProcedure> getMedicalProcedures() {
    return medicalProcedures;
  }

  public void setMedicalProcedures(ArrayList<MedicalProcedure> medicalProcedures) {
    updateLastModified();
    this.medicalProcedures = medicalProcedures;
  }

  public void addMedicalProcedure(MedicalProcedure medicalProcedure){
    updateLastModified();
    medicalProcedures.add(medicalProcedure);
  }

  public void removeMedicalProcedure(MedicalProcedure medicalProcedure){
    updateLastModified();
    medicalProcedures.remove(medicalProcedure);
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
    return this.nhi.equals(other.getNhi());
    //return Objects.equals(dateOfBirth, other.dateOfBirth) && name.equalsIgnoreCase(other.name);
  }

  @Override
  public int hashCode() {

    return Objects.hash(nhi);
  }

  @Override
  public String toString() {
    return "name:'" + name + "\'" +
        "\nnhi: " + nhi +
        "\ndate Of Birth: " + dateOfBirth +
        "\ndate Of Death :" + dateOfDeath +
        "\nbirth gender: " + birthGender +
        "\npreferred gender: " + genderIdentity +
        "\nheight: " + height +
        "\nweight: " + weight +
        "\nblood Type: '" + bloodType + '\'' +
        "\ncurrent Address: '" + currentAddress + '\'' +
        "\nregion: '" + region + '\'' +
        "\norgans: " + (isDonor() ?  donorDetails.getOrgans() : (name + " is not a donor")) +
        "\ntime Created: " + timeCreated +
        "\nlast modified: " + lastModified;
  }
}
