package seng302.Model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import org.joda.time.DateTime;

import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * Class for all donors created in this application
 *
 * No methods in this class, as of 26/02/2018, are non standard.
 *
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
   private String name;
   private HashSet<Organs> organs;

    public Donor(Date dateOfBirth, Date dateOfDeath, String gender, double height, double weight, String bloodType, String currentAddress, String region, DateTime timeCreated, String name) {
        this.dateOfBirth = dateOfBirth;
        this.dateOfDeath = dateOfDeath;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.bloodType = bloodType;
        this.currentAddress = currentAddress;
        this.region = region;
        if (timeCreated == null){
            this.timeCreated = DateTime.now();
        } else {
            this.timeCreated = timeCreated;
        }

        this.name = name;
    }

    public Donor(String name, Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        this.name = name;
        timeCreated = DateTime.now();
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Date getDateOfDeath() {
        return dateOfDeath;
    }

    public void setDateOfDeath(Date dateOfDeath) {
        this.dateOfDeath = dateOfDeath;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public DateTime getTimeCreated() {
        return timeCreated;
    }

    public HashSet<Organs> getOrgans() {
      return organs;
    }

    public void setOrgans(HashSet<Organs> organs) {
    this.organs = organs;
    }

    public void AddOrgan(Organs organ) {
      if( organs == null) {
        organs = new HashSet<>();
      }
      this.organs.add(organ);
    }

    public void RemoveOrgan(Organs organ) {
      if(organs.contains(organ)) {
        organs.remove(organ);
      }
    }
  @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Donor donor = (Donor) o;
        return Objects.equals(dateOfBirth, donor.dateOfBirth) &&
                Objects.equals(name, donor.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(dateOfBirth, name);
    }

    @Override
    public String toString() {
        return "Donor{" +
                "name='" + name + '\'' +
                ", date Of Birth=" + dateOfBirth +
                ", date Of Death=" + dateOfDeath +
                ", gender=" + gender +
                ", height=" + height +
                ", weight=" + weight +
                ", bloodType='" + bloodType + '\'' +
                ", currentAddress='" + currentAddress + '\'' +
                ", region='" + region + '\'' +
                ", timeCreated=" + timeCreated +
                ", organs="+organs +
                ", hashCode="+ hashCode() +
                '}';
    }
}
