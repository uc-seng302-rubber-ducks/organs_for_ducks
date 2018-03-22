package seng302.Model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

import org.joda.time.DateTime;

import java.text.DateFormat;
import org.joda.time.LocalDate;
import org.joda.time.ReadableInstant;
import org.joda.time.Years;

/**
 * Class for all donors created in this application
 *
 * No methods in this class, as of 26/02/2018, are non standard.
 */
public class Donor {

  private Date dateOfBirth;
  private Date dateOfDeath;
  private String gender;
  private double height;
  private double weight;
  private String bloodType;
  private String currentAddress;
  private String region;
  private DateTime timeCreated;
  private Boolean isDeceased;
  private String name;
  private HashSet<Organs> organs;
  private DateTime lastModified;
  private ArrayList<String> miscAttributes;


  public Donor(Date dateOfBirth, Date dateOfDeath, String gender, double height, double weight,
      String bloodType,
      String currentAddress, String region, DateTime timeCreated, String name,
      DateTime lastModified,
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
      this.timeCreated = DateTime.now();
    } else {
      this.timeCreated = timeCreated;
    }

    this.name = name;
    if (lastModified == null) {
      this.lastModified = DateTime.now();
    } else {
      this.lastModified = lastModified;
    }
    this.isDeceased = isDeceased;
    miscAttributes = new ArrayList<>();
  }

  public Donor(String name, Date dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
    this.name = name;
    timeCreated = DateTime.now();
    lastModified = DateTime.now();
    this.gender = "U";
    this.bloodType = "U";
    miscAttributes = new ArrayList<>();
  }

  /**
   * empty constructor to allow an empty donor to be created for the gui
   */
  public Donor() {
    timeCreated = DateTime.now();
    organs = new HashSet<>();
    miscAttributes = new ArrayList<String>();
  }


  /**
   * Utility function to update the last modified timestamp when a change is made to a donor. Can be
   * changed later to allow writing to the JSON change log latter
   */
  public void updateLastModified() {
    lastModified = DateTime.now();
  }

  public DateTime getLastModified() {
    return lastModified;
  }

  //For UndoRedoStacks
  public void setLastModified(DateTime lastModified) {
    this.lastModified = lastModified;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    updateLastModified();
    this.name = name;
  }

  public Date getDateOfBirth() {
    return dateOfBirth;
  }

  public void setDateOfBirth(Date dateOfBirth) {
    updateLastModified();
    this.dateOfBirth = dateOfBirth;
  }

  public Date getDateOfDeath() {
    return dateOfDeath;
  }

  public void setDateOfDeath(Date dateOfDeath) {
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

  public DateTime getTimeCreated() {
    return timeCreated;
  }

  //For UndoRedoStacks
  public void setTimeCreated(DateTime timeCreated) {
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

  public Years getAge() {
    if (dateOfDeath != null) {

      return Years.yearsBetween(LocalDate.fromDateFields(dateOfBirth),
          LocalDate.fromDateFields(dateOfDeath));
    }
    return Years.yearsBetween(LocalDate.fromDateFields(dateOfBirth), LocalDate.now());
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

  public void removeMiscAttribute(String attribute) {
    miscAttributes.remove(attribute);
  }

  public void addAttribute(String attribute) {
    miscAttributes.add(attribute);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
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
        "\ngender: " + gender +
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
