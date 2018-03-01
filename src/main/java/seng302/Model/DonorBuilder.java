package seng302.Model;

import org.joda.time.DateTime;

import java.util.Date;

public class DonorBuilder {
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

    public DonorBuilder setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public DonorBuilder setDateOfDeath(Date dateOfDeath) {
        this.dateOfDeath = dateOfDeath;
        return this;
    }

    public DonorBuilder setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public DonorBuilder setHeight(double height) {
        this.height = height;
        return this;
    }

    public DonorBuilder setWeight(double weight) {
        this.weight = weight;
        return this;
    }

    public DonorBuilder setBloodType(String bloodType) {
        this.bloodType = bloodType;
        return this;
    }

    public DonorBuilder setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
        return this;
    }

    public DonorBuilder setRegion(String region) {
        this.region = region;
        return this;
    }

    public DonorBuilder setTimeCreated(DateTime timeCreated) {
        this.timeCreated = timeCreated;
        return this;
    }

    public DonorBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public Donor createDonor() {
        return new Donor(dateOfBirth, dateOfDeath, gender, height, weight, bloodType, currentAddress, region, timeCreated, name);
    }
}