package seng302.Model;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.joda.time.DateTime;

/**
 * Class for all donors created in this application
 *
 * No methods in this class, as of 26/02/2018, are non standard.
 */
@Deprecated
public class Donor {

    private Date dateOfBirth;
    private Date dateOfDeath;
    private String birthGender;
    private String genderIdentity;
    private double height;
    private double weight;
    private String bloodType;
    private String currentAddress;
    private String region;
    private DateTime timeCreated;
    private Boolean isDeceased;
    private String name; // TODO: Take this out and use the separated names
    private String firstName;
    private String preferredFirstName;
    private String middleName;
    private String lastName;
    private HashSet<Organs> organs;
    private DateTime lastModified;
    private ArrayList<String> miscAttributes;
    private HashMap<String, String> updateHistory;
    private ArrayList<String> previousMedication;
    private ArrayList<String> currentMedication;
    private HashMap<String, ArrayList<DateTime>> previousMedicationTimes;
    private HashMap<String, ArrayList<DateTime>> currentMedicationTimes;
    private ArrayList<Change> changes;




  public Donor(java.time.LocalDate dateOfBirth, java.time.LocalDate dateOfDeath, String gender, double height, double weight,
               String bloodType,
               String currentAddress, String region, LocalDateTime timeCreated, String name,
               LocalDateTime lastModified,
               boolean isDeceased) {
    this.dateOfBirth = dateOfBirth;
    this.dateOfDeath = dateOfDeath;
    if (gender.startsWith("m") || gender.startsWith("M")) {
      this.birthGender = "M";
    } else if (gender.startsWith("f") || gender.startsWith("F")) {
      this.birthGender = "F";
    } else {
      this.birthGender = "U";
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
      try {
          changes = JsonHandler.importHistoryFromFile(name.toLowerCase().replace(" ", "_"));
      } catch (FileNotFoundException e) {
          e.printStackTrace();
      }
  }

    public Donor(String name, java.time.LocalDate dateOfBirth) {
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
        try {
            changes = JsonHandler.importHistoryFromFile(name.toLowerCase().replace(" ", "_"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /** empty constructor to allow an empty donor to be created for the gui
     *
     */
    public Donor(){
      timeCreated = LocalDateTime.now();
      organs = new HashSet<>();
      miscAttributes = new ArrayList<String>();
        this.currentMedication = new ArrayList<>();
        this.previousMedication = new ArrayList<>();
        this.currentMedicationTimes = new HashMap<String, ArrayList<LocalDateTime>>();
        this.previousMedicationTimes = new HashMap<String, ArrayList<LocalDateTime>>();
        changes = new ArrayList<>();
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

    public void setPrefFirstName(String name) {
        updateLastModified();
        this.preferredFirstName = name;
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

  public Date getDateOfBirth() {
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
    return birthGender;
  }

  public void setGender(String gender) {
    updateLastModified();
    this.birthGender = gender;
  }

    public String getGenderId() {
        return genderIdentity;
    }

    public void setGenderId(String gender) {
        updateLastModified();
        this.genderIdentity = gender;
    }

  public double getHeight() {
    return height;
  }

  public void setHeight(double height) {
    updateLastModified();
    this.height = height;
  }

  public double getWeight() {
    updateLastModified();
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

  public HashSet<Organs> getOrgans() {
    return organs;
  }

  public void setOrgans(HashSet<Organs> organs) {
    updateLastModified();
    this.organs = organs;
  }

  public void addOrgan(Organs organ) {
    updateLastModified();
    if (organs == null) {
      organs = new HashSet<>();
    }
    this.organs.add(organ);
  }

  public void removeOrgan(Organs organ) {
    if (organs.contains(organ)) {
      organs.remove(organ);
    }
  }

  public String getAge() {
    if (dateOfDeath != null) {

      return Long.toString(ChronoUnit.YEARS.between(dateOfBirth,dateOfDeath));
    }
    return Long.toString(ChronoUnit.YEARS.between(dateOfBirth, java.time.LocalDate.now()));
  }

  //Trial method
  public void initOrgans() {
    organs = new HashSet<>();
  }

  public Boolean getDeceased() {
    return isDeceased;
  }

  public void setDeceased(Boolean deceased) {
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
    this.miscAttributes = miscAttributes;
  }

    // @TODO: find all instances of potential updates and add to the Hashmap

    public HashMap<String, String> getUpdateHistory() { return updateHistory; }

    public void setUpdateHistory(HashMap<String, String> updateHistory) {this.updateHistory = updateHistory; }

    private String dateToString(LocalDateTime dateTime) {
        return dateTime.toString();
    }

    public void addToUpdateHistory(LocalDateTime dateTime, String action) {
        String timeStamp = dateToString(dateTime);
        updateHistory.put(timeStamp, action);
    }

    public void removeMiscAttribute(String attribute){
        miscAttributes.remove(attribute);
    }

  public void addAttribute(String attribute) {
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
        currentMedication.add(medication);
        addCurrentMedicationTimes(medication);
    }

    public void addPreviousMedication(String medication) {
        previousMedication.add(medication);
        addPreviousMedicationTimes(medication);
    }

    public void addCurrentMedicationSetup(String medication) {
        currentMedication.add(medication);
    }

    public void addPreviousMedicationSetUp(String medication) {
        previousMedication.add(medication);
    }


    public void removeCurrentMedication(String medication) {
        currentMedication.remove(medication);
    }

    public void removePreviousMedication(String medication) {
        previousMedication.remove(medication);
    }

    public HashMap<String, ArrayList<LocalDateTime>> getPreviousMedicationTimes() {
        return previousMedicationTimes;
    }

    public void setPreviousMedicationTimes(HashMap<String, ArrayList<LocalDateTime>> previousMedicationTimes) {
        this.previousMedicationTimes = previousMedicationTimes;
    }

    public HashMap<String, ArrayList<LocalDateTime>> getCurrentMedicationTimes() {
        return currentMedicationTimes;
    }


    public void setCurrentMedicationTimes(HashMap<String, ArrayList<LocalDateTime>> currentMedicationTimes) {
        this.currentMedicationTimes = currentMedicationTimes;
    }

    /**
     * Use this one when adding a new medication from the donor interface
     * @param medication
     */
    public void addCurrentMedicationTimes(String medication) {
        LocalDateTime time  = LocalDateTime.now();
        updateLastModified();
        ArrayList<LocalDateTime> previouslyExists;
        try {
            previouslyExists = currentMedicationTimes.get(medication);
            previouslyExists.add(time);
        } catch (NullPointerException e){
            previouslyExists = new ArrayList<>();
            previouslyExists.add(time);
        }

        currentMedicationTimes.put(medication, previouslyExists);
    }

    /**
     * Use this one when adding a new medication from the donor interface
     * @param medication medication string key
     *
     */
    public void addPreviousMedicationTimes(String medication) {
        LocalDateTime time  = LocalDateTime.now();
        updateLastModified();
        ArrayList<LocalDateTime> previouslyExists;
        try {
            previouslyExists = previousMedicationTimes.get(medication);
            previouslyExists.add(time);
        } catch (NullPointerException e) {
            previouslyExists = new ArrayList<>();
            previouslyExists.add(time);
        }
        previousMedicationTimes.put(medication, previouslyExists);
    }

    /**
     * Use this one when creating the user from the json object
     * @param medication medication string key
     * @param stamps list of timestamps
     */
    public void addCurrentMedicationTimes(String medication, ArrayList<LocalDateTime> stamps) {

        currentMedicationTimes.put(medication, stamps);
    }


    /**
     * Use this one when creating the user from the json object
     * @param medication medication string key
     * @param stamps list of timestamps
     */
    public void addPreviousMedicationTimes(String medication, ArrayList<LocalDateTime> stamps) {
        previousMedicationTimes.put(medication, stamps);
    }


    public ArrayList<Change> getChanges() {
        return changes;
    }

    public void setChanges(ArrayList<Change> changes) {
        this.changes = changes;
    }

    public void addChange(Change change){
        changes.add(change);
    }

    public String getTooltip(){
        if(organs == null){
            return name;
        }
        if (!organs.isEmpty()){
            String toReturn = name + ". Donor: ";
            for ( Organs o : organs){
                toReturn += o.toString() + " ";
            }
            return toReturn;
        } else {
            return name;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Donor donor = (Donor) o;
        return Objects.equals(dateOfBirth, donor.dateOfBirth) && name.equalsIgnoreCase(donor.name);
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
        "\ngender: " + birthGender +
        "\nheight: " + height +
        "\nweight: " + weight +
        "\nblood Type: '" + bloodType + '\'' +
        "\ncurrent Address: '" + currentAddress + '\'' +
        "\nregion: '" + region + '\'' +
        "\norgans: " + organs +
        "\ntime Created: " + timeCreated +
        "\nlast modified: " + lastModified +
        "\nhashcode=" + hashCode();
  }

}
