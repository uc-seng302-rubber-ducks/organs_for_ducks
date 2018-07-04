package seng302.model;

import com.google.gson.annotations.Expose;
import javafx.collections.FXCollections;
import seng302.controller.gui.popup.ReceiverOrganDetailsHolder;
import seng302.model._abstract.Listenable;
import seng302.model._abstract.Undoable;
import seng302.model._enum.EventTypes;
import seng302.model._enum.Organs;
import seng302.model.datamodel.ContactDetails;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Class for handling calls to user
 */
public class User extends Undoable<User> implements Listenable {

    //<editor-fold desc="properties">
    @Expose
    private String nhi;
    @Expose
    private String name;
    @Expose
    private LocalDate dateOfBirth;
    @Expose
    private LocalDate dateOfDeath;
    @Expose
    private LocalDateTime timeCreated;
    @Expose
    private boolean isDeceased = false;
    @Expose
    private String firstName;
    @Expose
    private String preferredFirstName;
    @Expose
    private String middleName;
    @Expose
    private String lastName;
    @Expose
    private String birthCountry;

    @Expose
    private EmergencyContact contact;

    @Expose
    private ContactDetails contactDetails;

    @Expose
    private HealthDetails healthDetails;


    @Expose
    private LocalDateTime lastModified;
    @Expose
    private List<String> miscAttributes;
    @Expose
    private Map<String, String> updateHistory;
    @Expose
    private List<String> previousMedication;
    @Expose
    private List<String> currentMedication;
    @Expose
    private Map<String, List<LocalDateTime>> previousMedicationTimes;
    @Expose
    private Map<String, List<LocalDateTime>> currentMedicationTimes;

    @Expose
    private List<MedicalProcedure> medicalProcedures;

    //flags and extra details for if the person is a donor or a receiver
    @Expose
    private DonorDetails donorDetails;
    @Expose
    private ReceiverDetails receiverDetails;

    @Expose
    private Collection<Organs> commonOrgans;

    @Expose
    private List<Disease> pastDiseases;

    @Expose
    private List<Disease> currentDiseases;

    private transient List<Change> changes;
    private transient PropertyChangeSupport pcs;
    //</editor-fold>


    /**
     * Constructor for a User
     *
     * @param firstName   users first name
     * @param dateOfBirth users date of birth
     * @param nhi         users national health index
     */
    public User(String firstName, java.time.LocalDate dateOfBirth, String nhi) {
        this.dateOfBirth = dateOfBirth;
        this.name = firstName;
        this.donorDetails = new DonorDetails(this);
        this.firstName = firstName;
        this.receiverDetails = new ReceiverDetails(this);
        this.nhi = nhi;
        this.middleName = "";
        this.lastName = "";
        this.dateOfDeath = null;
        this.preferredFirstName = firstName;
        timeCreated = LocalDateTime.now();
        lastModified = LocalDateTime.now();
        updateHistory = new HashMap<>();
        this.contact = new EmergencyContact("", "", "");
        updateHistory.put(dateToString(getTimeCreated()), "Profile created.");
        this.miscAttributes = new ArrayList<>();
        this.currentMedication = new ArrayList<>();
        this.previousMedication = new ArrayList<>();
        this.currentMedicationTimes = new HashMap<>();
        this.previousMedicationTimes = new HashMap<>();
        this.currentDiseases = new ArrayList<>();
        this.pastDiseases = new ArrayList<>();
        this.commonOrgans = new HashSet<>();
        this.contactDetails = new ContactDetails();
        this.donorDetails = new DonorDetails(this);
        this.receiverDetails = new ReceiverDetails(this);
        this.commonOrgans = new HashSet<>();
        this.medicalProcedures = new ArrayList<>();
        this.changes = FXCollections.observableArrayList();
        this.pcs = new PropertyChangeSupport(this);
        this.healthDetails = new HealthDetails();
        contactDetails.setAttachedUser(this);
        contact.setAttachedUser(this);
    }


    /**
     * empty constructor to allow an empty user to be created for the gui
     */
    public User() {
        timeCreated = LocalDateTime.now();
        miscAttributes = new ArrayList<>();
        this.currentMedication = new ArrayList<>();
        this.previousMedication = new ArrayList<>();

        this.currentDiseases = new ArrayList<>();
        this.pastDiseases = new ArrayList<>();
        this.contactDetails = new ContactDetails();
        this.currentMedicationTimes = new HashMap<>();
        this.previousMedicationTimes = new HashMap<>();
        this.updateHistory = new HashMap<>();
        this.medicalProcedures = new ArrayList<>();
        this.donorDetails = new DonorDetails(this);
        this.receiverDetails = new ReceiverDetails(this);
        this.commonOrgans = new HashSet<>();
        changes = FXCollections.observableArrayList();
        this.pcs = new PropertyChangeSupport(this);
        this.healthDetails = new HealthDetails();
        contactDetails.setAttachedUser(this);

    }


    public ContactDetails getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(ContactDetails contactDetails) {
        this.contactDetails = contactDetails;
    }

    public EmergencyContact getContact() {
        return contact;
    }

    public void setContact(EmergencyContact contact) {
        Memento<User> mem = new Memento<>();
        mem.setOldObject(this.clone());
        updateLastModified();
        this.contact = contact;
        mem.setNewObject(this.clone());
        getUndoStack().push(mem);
    }

    public HealthDetails getHealthDetails() {
        return healthDetails;
    }

    public void setHealthDetails(HealthDetails healthDetails) {
        Memento<User> mem = new Memento<>();
        mem.setOldObject(this.clone());
        updateLastModified();
        this.healthDetails = healthDetails;
        mem.setNewObject(this.clone());
        getUndoStack().push(mem);
    }

    public DonorDetails getDonorDetails() {
        return donorDetails;
    }

    public void setDonorDetails(DonorDetails donorDetails) {
        Memento<User> mem = new Memento<>();
        mem.setOldObject(this.clone());
        updateLastModified();
        this.donorDetails = donorDetails;
        mem.setNewObject(this.clone());
        getUndoStack().push(mem);
    }

    public ReceiverDetails getReceiverDetails() {
        return receiverDetails;
    }

    public void setReceiverDetails(ReceiverDetails receiverDetails) {
        Memento<User> mem = new Memento<>();
        mem.setOldObject(this.clone());
        updateLastModified();
        this.receiverDetails = receiverDetails;
        mem.setNewObject(this.clone());
        getUndoStack().push(mem);
    }

    //TODO details object is set at initialization. will always return true 17/05

    public Collection<Organs> getCommonOrgans() {
        return commonOrgans;
    }

    /**
     * Checks to see if user is a donor
     *
     * @return true if donor
     */
    public boolean isDonor() {
        return this.donorDetails != null && !this.donorDetails.isEmpty();

    }

    /**
     * Checks to see if user is a receiver
     *
     * @return true if receiver
     */
    public boolean isReceiver() {
        return this.receiverDetails != null && !this.receiverDetails.isEmpty();
    }

    public String getNhi() {
        return nhi;
    }

    public void setNhi(String nhi) {
        Memento<User> mem = new Memento<>();
        mem.setOldObject(this.clone());
        updateLastModified();
        this.nhi = nhi;
        addChange(new Change("Updated NHI to " + nhi));
        mem.setNewObject(this.clone());
        getUndoStack().push(mem);
    }

    /**
     * Utility function to update the last modified timestamp when a change is made to a user. Can be
     * changed later to allow writing to the JSON change log later
     */
    public void updateLastModified() {
        lastModified = LocalDateTime.now();
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public void setName(String fName, String mName, String lName) {
        Memento<User> mem = new Memento<>();
        mem.setOldObject(this.clone());
        this.firstName = fName;
        this.middleName = mName;
        this.lastName = lName;
        updateLastModified();
        addChange(new Change("set full name to " + fName + " " + mName + " " + lName));
        mem.setNewObject(this.clone());
        if (!mem.getNewObject().getFullName().equals(mem.getOldObject().getFullName())) {
            getUndoStack().push(mem);
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String name) {
        Memento<User> mem = new Memento<>();
        mem.setOldObject(this.clone());
        updateLastModified();
        // Changes the default case where the preferred name is the same as the first name
        if (preferredFirstName == null || preferredFirstName.equals(firstName)) {
            preferredFirstName = name;
        }
        this.firstName = name;
        addChange(new Change("Changed first name to " + name));
        mem.setNewObject(this.clone());
        getUndoStack().push(mem);

    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String name) {
        Memento<User> mem = new Memento<>();
        mem.setOldObject(this.clone());
        updateLastModified();
        this.middleName = name;
        addChange(new Change("Changed middle name to " + middleName));
        mem.setNewObject(this.clone());
        getUndoStack().push(mem);
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String name) {
        Memento<User> mem = new Memento<>();
        mem.setOldObject(this.clone());
        updateLastModified();
        this.lastName = name;
        addChange(new Change("Changed last name to " + lastName));
        mem.setNewObject(this.clone());
        getUndoStack().push(mem);
    }

    public String getFullName() {
        String fullName;

        if (middleName != null && lastName != null) {
            fullName = firstName + " " + middleName + " " + lastName;

        } else if (middleName != null) {
            fullName = firstName + " " + middleName;

        } else if (lastName != null) {
            fullName = firstName + " " + lastName;

        } else {
            fullName = firstName;
        }

        return fullName.trim();
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        Memento<User> mem = new Memento<>();
        mem.setOldObject(this.clone());
        updateLastModified();
        this.dateOfBirth = dateOfBirth;
        addChange(new Change("Changed date of birth to " + dateOfBirth.toString()));
        mem.setNewObject(this.clone());
        getUndoStack().push(mem);
    }

    public LocalDate getDateOfDeath() {
        return dateOfDeath;
    }

    public void setDateOfDeath(LocalDate dateOfDeath) {
        Memento<User> mem = new Memento<>();
        mem.setOldObject(this.clone());
        updateLastModified();
        this.dateOfDeath = dateOfDeath;
        this.isDeceased = dateOfDeath != null;
        addChange(new Change(isDeceased ? ("Changed date of death to " + dateOfDeath.toString())
                : "Removed date of death"));
        mem.setNewObject(this.clone());
        getUndoStack().push(mem);
    }

    public double getHeight() {
        return healthDetails.getHeight();
    }

    public void setHeight(double height) {
        Memento<User> mem = new Memento<>();
        mem.setOldObject(this.clone());
        updateLastModified();
        if (healthDetails.getHeight() != height) {
            healthDetails.setHeight(height);
            addChange(new Change("Changed height to " + height));
            mem.setNewObject(this.clone());
            getUndoStack().push(mem);
        }
    }

    public double getWeight() {
        return healthDetails.getWeight();
    }

    public void setWeight(double weight) {
        Memento<User> mem = new Memento<>();
        mem.setOldObject(this.clone());
        updateLastModified();
        if (weight != healthDetails.getWeight()) {
            healthDetails.setWeight(weight);
            addChange(new Change("Changed weight to " + weight));
            mem.setNewObject(this.clone());
            getUndoStack().push(mem);
        }
    }

    public String getHeightText() {
        return healthDetails.getHeightText();
    }

    public void setHeightText(String height) {
        Memento<User> mem = new Memento<>();
        mem.setOldObject(this.clone());
        updateLastModified();
        if (!(healthDetails.getHeightText().equals(height))) {
            healthDetails.setHeightText(height);
            addChange(new Change("set height to " + height));
            mem.setNewObject(this.clone());
            getUndoStack().push(mem);
        }
    }

    public String getWeightText() {
        return healthDetails.getWeightText();
    }

    public void setWeightText(String weight) {
        Memento<User> mem = new Memento<>();
        mem.setOldObject(this.clone());
        updateLastModified();
        if (!(healthDetails.getWeightText().equals(weight))) {
            healthDetails.setWeightText(weight);
            addChange(new Change("set weight to " + weight));
            mem.setNewObject(this.clone());
            getUndoStack().push(mem);
        }
    }

    public String getBloodType() {
        return healthDetails.getBloodType();
    }

    public void setBloodType(String bloodType) {
        Memento<User> mem = new Memento<>();
        mem.setOldObject(this.clone());
        String validType = groupBloodType(bloodType);
        updateLastModified();
        if (!healthDetails.getBloodType().equals(validType)) {
            healthDetails.setBloodType(validType);
            addChange(new Change("Changed blood type to " + bloodType));
            mem.setNewObject(this.clone());
            getUndoStack().push(mem);
        }
    }

    public String getBirthGender() {
        return healthDetails.getBirthGender();
    }

    public void setBirthGender(String birthGender) {
        Memento<User> mem = new Memento<>();
        mem.setOldObject(this.clone());
        updateLastModified();
        // Changes the default case where the gender identity is the same as the birth gender
        if (healthDetails.getGenderIdentity() == null) {
            healthDetails.setGenderIdentity(healthDetails.getBirthGender());
        }
        healthDetails.setBirthGender(birthGender);
        addChange(new Change("Changed birth gender to " + birthGender));
        mem.setNewObject(this.clone());
        getUndoStack().push(mem);
    }

    public String getGenderIdentity() {
        return healthDetails.getGenderIdentity();
    }

    public void setGenderIdentity(String genderIdentity) {

        Memento<User> mem = new Memento<>();
        mem.setOldObject(this.clone());
        updateLastModified();
        healthDetails.setGenderIdentity(genderIdentity);
        addChange(new Change("Changed birth Identity to " + genderIdentity));
        mem.setNewObject(this.clone());
        getUndoStack().push(mem);
    }

    public String getAlcoholConsumption() {
        return healthDetails.getAlcoholConsumption();
    }

    public void setAlcoholConsumption(String alcoholConsumption) {
        Memento<User> mem = new Memento<>();
        mem.setOldObject(this.clone());
        updateLastModified();
        healthDetails.setAlcoholConsumption(alcoholConsumption);
        addChange(new Change("Changed alcohol consumption to " + alcoholConsumption));
        mem.setNewObject(this.clone());
        getUndoStack().push(mem);
    }

    public String getAddress() {
        return contactDetails.getAddress().toString();
    }

    public boolean isSmoker() {
        return healthDetails.isSmoker();
    }

    public void setSmoker(boolean smoker) {
        Memento<User> mem = new Memento<>();
        mem.setOldObject(this.clone());
        updateLastModified();
        healthDetails.setSmoker(smoker);
        addChange(new Change("Changed smoker status to " + smoker));
        mem.setNewObject(this.clone());
        getUndoStack().push(mem);
    }


    public String getStreetNumber() {
        return contactDetails.getAddress().getStreetNumber();
    }

    public void setStreetNumber(String streetNumber) {
        contactDetails.getAddress().setStreetNumber(streetNumber);
    }

    public String getStreetName() {
        return contactDetails.getAddress().getStreetName();
    }

    public void setStreetName(String streetName) {
        contactDetails.getAddress().setStreetName(streetName);
    }

    public String getNeighborhood() {
        return contactDetails.getAddress().getNeighborhood();
    }

    public void setNeighborhood(String neighborhood) {
        contactDetails.getAddress().setNeighborhood(neighborhood);
    }

    public String getCity() {
        return contactDetails.getAddress().getCity();
    }

    public void setCity(String city) {
        contactDetails.getAddress().setCity(city);
    }

    public String getZipCode() {
        return contactDetails.getAddress().getZipCode();
    }

    public void setZipCode(String zipCode) {
        contactDetails.getAddress().setZipCode(zipCode);
    }

    public String getCountry() {
        return contactDetails.getAddress().getCountry();
    }

    public void setCountry(String country) {
        contactDetails.getAddress().setCountry(country);
    }

    public String getBirthCountry() {
        return birthCountry;
    }

    public void setBirthCountry(String birthCountry) {
        this.birthCountry = birthCountry;
    }

    /**
     * Method to ensure that all blood types are valid blood types returns U if not a valid blood
     * type
     *
     * @param possibleType type to test
     * @return correct blood type
     */
    private String groupBloodType(String possibleType) {

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


    public String getRegion() {
        return contactDetails.getAddress().getRegion();
    }

    public void setRegion(String region) {
        Memento<User> mem = new Memento<>();
        mem.setOldObject(this.clone());
        updateLastModified();
        contactDetails.getAddress().setRegion(region);
        if (contactDetails.getAddress() != null && !contactDetails.getAddress().equals("")) {
            addChange(new Change("Changed region to " + region));
        }
        mem.setNewObject(this.clone());
        getUndoStack().push(mem);
    }

    public LocalDateTime getTimeCreated() {
        return timeCreated;
    }

    public String getStringAge() {
        if (dateOfDeath != null) {

            return Long.toString(ChronoUnit.YEARS.between(dateOfBirth, dateOfDeath));
        }
        return Long.toString(ChronoUnit.YEARS.between(dateOfBirth, LocalDate.now()));
    }

    public int getAge() {
        if (dateOfDeath != null) {

            return Math.toIntExact(ChronoUnit.YEARS.between(dateOfBirth, dateOfDeath));
        }
        return Math.toIntExact(ChronoUnit.YEARS.between(dateOfBirth, java.time.LocalDate.now()));
    }

    //TODO: refactor code to calculate off date od death and remove this variable 17/05
    public Boolean getDeceased() {
        return isDeceased;
    }

    public List<Disease> getCurrentDiseases() {
        return currentDiseases;
    }

    public void addCurrentDisease(Disease currentDisease) {
        currentDiseases.add(currentDisease);
        addChange(new Change("Added current disease " + currentDisease.toString()));
    }

    public List<Disease> getPastDiseases() {
        return pastDiseases;
    }

    public void addPastDisease(Disease pastDisease) {
        addChange(new Change("Added past disease " + pastDisease.toString()));
        this.pastDiseases.add(pastDisease);
    }

    public String getPreferredFirstName() {
        return preferredFirstName;
    }

    public void setPreferredFirstName(String preferredFirstName) {
        Memento<User> mem = new Memento<>();
        mem.setOldObject(this.clone());
        updateLastModified();
        this.preferredFirstName = preferredFirstName;
        addChange(new Change("Changed preferred first name to " + preferredFirstName));
        mem.setNewObject(this.clone());
        getUndoStack().push(mem);
    }

    public String getHomePhone() {
        return contactDetails.getHomePhoneNumber();
    }

    public void setHomePhone(String homePhone) {
        Memento<User> mem = new Memento<>();
        mem.setOldObject(this.clone());
        updateLastModified();
        contactDetails.setHomePhoneNumber(homePhone);
        addChange(new Change("Changed Home phone to " + homePhone));
        mem.setNewObject(this.clone());
        getUndoStack().push(mem);
    }

    public String getCellPhone() {
        return contactDetails.getCellPhoneNumber();
    }

    public void setCellPhone(String cellPhone) {
        Memento<User> mem = new Memento<>();
        mem.setOldObject(this.clone());
        updateLastModified();
        contactDetails.setCellPhoneNumber(cellPhone);
        addChange(new Change("Changed cell Phone to " + cellPhone));
        mem.setNewObject(this.clone());
        getUndoStack().push(mem);
    }

    public String getEmail() {
        return contactDetails.getEmail();
    }

    public void setEmail(String email) {
        Memento<User> mem = new Memento<>();
        mem.setOldObject(this.clone());
        updateLastModified();
        contactDetails.setEmail(email);
        addChange(new Change("Changed email to " + email));
        mem.setNewObject(this.clone());
        getUndoStack().push(mem);
    }

    // @TODO: find all instances of potential updates and add to the Hashmap

    public Map<String, String> getUpdateHistory() {
        return updateHistory;
    }

    public void setUpdateHistory(Map<String, String> updateHistory) {
        this.updateHistory = updateHistory;
    }

    private String dateToString(LocalDateTime dateTime) {
        return dateTime.toString();
    }

    public void addToUpdateHistory(LocalDateTime dateTime, String action) {
        String timeStamp = dateToString(dateTime);
        updateHistory.put(timeStamp, action);
    }


    public List<String> getPreviousMedication() {
        return previousMedication;
    }

    public void setPreviousMedication(List<String> previousMedication) {
        this.previousMedication = previousMedication;
    }

    public List<String> getCurrentMedication() {
        return currentMedication;
    }

    public void setCurrentMedication(List<String> currentMedication) {
        this.currentMedication = currentMedication;
    }

    public void addCurrentMedication(String medication) {
        Memento<User> memento = new Memento<>();
        memento.setOldObject(this.clone());
        updateLastModified();
        currentMedication.add(medication);
        addMedicationTimes(medication, currentMedicationTimes);
        addChange(new Change("Added current medication" + medication));
        memento.setNewObject(this.clone());
        getUndoStack().push(memento);
    }

    public void addPreviousMedication(String medication) {
        updateLastModified();
        previousMedication.add(medication);
        addMedicationTimes(medication, previousMedicationTimes);
        addChange(new Change("Added previous medication" + medication));
    }

    public void addCurrentMedicationSetup(String medication) {
        updateLastModified();
        currentMedication.add(medication);
        addChange(new Change("Added current medication" + medication));
    }

    public void addPreviousMedicationSetUp(String medication) {
        updateLastModified();
        previousMedication.add(medication);
        addChange(new Change("Added previous medication" + medication));
    }


    public void removeCurrentMedication(String medication) {
        updateLastModified();
        currentMedication.remove(medication);
        addChange(new Change("Removed current medication" + medication));
    }

    public void removePreviousMedication(String medication) {
        updateLastModified();
        previousMedication.remove(medication);
        addChange(new Change("Removed previous medication" + medication));
    }

    public Map<String, List<LocalDateTime>> getPreviousMedicationTimes() {
        return previousMedicationTimes;
    }

    public void setPreviousMedicationTimes(
            Map<String, List<LocalDateTime>> previousMedicationTimes) {
        updateLastModified();
        this.previousMedicationTimes = previousMedicationTimes;
    }

    public Map<String, List<LocalDateTime>> getCurrentMedicationTimes() {
        return currentMedicationTimes;
    }


    public void setCurrentMedicationTimes(
            Map<String, List<LocalDateTime>> currentMedicationTimes) {
        updateLastModified();
        this.currentMedicationTimes = currentMedicationTimes;
    }

    /**
     * Use this one when adding a new medication from the donor interface
     *
     * @param medication      medication to be added
     * @param medicationTimes hashmap to be appended to
     */
    private void addMedicationTimes(String medication,
                                    Map<String, List<LocalDateTime>> medicationTimes) {
        LocalDateTime time = LocalDateTime.now();
        updateLastModified();
        List<LocalDateTime> previouslyExists;
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
     * @param stamps     list of timestamps
     */
    public void addCurrentMedicationTimes(String medication, List<LocalDateTime> stamps) {

        currentMedicationTimes.put(medication, stamps);
        updateLastModified();
    }


    /**
     * Use this one when creating the user from the json object
     *
     * @param medication medication string key
     * @param stamps     list of timestamps
     */
    public void addPreviousMedicationTimes(String medication, List<LocalDateTime> stamps) {
        previousMedicationTimes.put(medication, stamps);
        updateLastModified();
    }


    public List<Change> getChanges() {
        return changes;
    }

    public void setChanges(List<Change> changes) {
        this.changes = changes;
    }

    public void addChange(Change change) {
        changes.add(change);
        this.fire(
                new PropertyChangeEvent(this, EventTypes.USER_UPDATE.name(), new Object(), new Object()));
    }

    public List<MedicalProcedure> getMedicalProcedures() {
        return medicalProcedures;
    }

    public void setMedicalProcedures(List<MedicalProcedure> medicalProcedures) {
        updateLastModified();
        this.medicalProcedures = medicalProcedures;
    }

    public void addMedicalProcedure(MedicalProcedure medicalProcedure) {
        updateLastModified();
        medicalProcedures.add(medicalProcedure);
        addChange(new Change("Added Medical Procedure" + medicalProcedure));
    }

    public void removeMedicalProcedure(MedicalProcedure medicalProcedure) {
        updateLastModified();
        medicalProcedures.remove(medicalProcedure);
        addChange(new Change("Removed Medical Procedure" + medicalProcedure));
    }

    public String getTooltip() {
        //TODO fix this to show full info where possible 05/17
        if (this.donorDetails.getOrgans() == null) {
            return name;
        }
        if (!this.getDonorDetails().getOrgans().isEmpty()) {
            StringBuilder toReturn = new StringBuilder(name + ". Donor: ");
            for (Organs o : this.donorDetails.getOrgans()) {
                toReturn.append(o.toString()).append(" ");
            }
            return toReturn.toString();
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
    }

    @Override
    public int hashCode() {

        return Objects.hash(nhi);
    }

    @Override
    public String toString() {
        return "User{" +
                "nhi='" + nhi + '\'' +
                ", name='" + name + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", dateOfDeath=" + dateOfDeath +
                ", timeCreated=" + timeCreated +
                ", isDeceased=" + isDeceased +
                ", firstName='" + firstName + '\'' +
                ", preferredFirstName='" + preferredFirstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", contact=" + contact +
                ", healthDetails=" + healthDetails +
                ", lastModified=" + lastModified +
                ", miscAttributes=" + miscAttributes +
                ", updateHistory=" + updateHistory +
                ", previousMedication=" + previousMedication +
                ", currentMedication=" + currentMedication +
                ", previousMedicationTimes=" + previousMedicationTimes +
                ", currentMedicationTimes=" + currentMedicationTimes +
                ", medicalProcedures=" + medicalProcedures +
                ", donorDetails=" + donorDetails +
                ", receiverDetails=" + receiverDetails +
                ", commonOrgans=" + commonOrgans +
                ", pastDiseases=" + pastDiseases +
                ", currentDiseases=" + currentDiseases +
                ", changes=" + changes +
                ", pcs=" + pcs +
                '}';
    }

    @Override
    public void undo() {
        if (getUndoStack().isEmpty()) {
            return;
        }
        Memento<User> memento = getUndoStack().pop();
        this.changeInto(memento.getOldObject());
        getRedoStack().push(memento);
        addChange(new Change("undo"));
    }

    @Override
    public void redo() {
        if (getRedoStack().isEmpty()) {
            return;
        }
        Memento<User> memento = getRedoStack().pop();
        this.changeInto(memento.getNewObject());
        getUndoStack().push(memento);
        addChange(new Change("redo"));
    }

    @Override
    public User clone() {
        User newUser = new User();
        newUser.nhi = this.nhi;
        newUser.dateOfBirth = this.dateOfBirth;
        newUser.dateOfDeath = this.dateOfDeath;

        newUser.contactDetails = this.contactDetails;
        if (this.contact != null) {
            newUser.contact = new EmergencyContact(this.contact.getName(), this.contact.getCellPhoneNumber(),
                    this.contact.getHomePhoneNumber(), this.contact.getAddress(),
                    this.contact.getEmail(), this.contact.getRelationship());
        } else {
            newUser.contact = null;
        }

        newUser.healthDetails = this.healthDetails;

        newUser.name = this.name;
        newUser.firstName = this.firstName;
        newUser.preferredFirstName = this.preferredFirstName;
        newUser.middleName = this.middleName;
        newUser.lastName = this.lastName;
        newUser.timeCreated = this.timeCreated;
        newUser.updateHistory = new HashMap<>(this.updateHistory);
        newUser.miscAttributes = new ArrayList<>(this.miscAttributes);
        newUser.currentMedication = new ArrayList<>(this.currentMedication);
        newUser.previousMedication = new ArrayList<>(this.previousMedication);
        newUser.currentMedicationTimes = new HashMap<>(this.currentMedicationTimes);
        newUser.previousMedicationTimes = new HashMap<>(this.previousMedicationTimes);
        newUser.donorDetails = new DonorDetails(newUser);
        for (Organs o : this.donorDetails.getOrgans()) {
            newUser.donorDetails.getOrgans().add(o);
        }
        newUser.receiverDetails = new ReceiverDetails(newUser);
        //Map<Organs, ArrayList<ReceiverOrganDetailsHolder>> organs = new EnumMap<Organs, ArrayList<ReceiverOrganDetailsHolder>>(this.receiverDetails.getOrgans());
        for (Organs o : this.receiverDetails.getOrgans().keySet()) {
            ArrayList<ReceiverOrganDetailsHolder> detailHolders = new ArrayList<>(this.receiverDetails.getOrgans().get(o));
            for (int i = 0; i < this.receiverDetails.getOrgans().get(o).size(); i++) {
                ReceiverOrganDetailsHolder newHolder = new ReceiverOrganDetailsHolder(null, null, null);// = newUser.receiverDetails.getOrgans().get(o).get(i);
                ReceiverOrganDetailsHolder oldHolder = this.receiverDetails.getOrgans().get(o).get(i);
                newHolder.setStartDate(oldHolder.getStartDate());
                newHolder.setStopDate(oldHolder.getStopDate());
                newHolder.setOrganDeregisterReason(oldHolder.getOrganDeregisterReason());
                detailHolders.add(newHolder);
            }
            newUser.receiverDetails.getOrgans().put(o, detailHolders);
        }

        newUser.currentDiseases = new ArrayList<>(this.currentDiseases);
        newUser.pastDiseases = new ArrayList<>(this.pastDiseases);
        newUser.medicalProcedures = new ArrayList<>();
        for (MedicalProcedure m : this.medicalProcedures) {
            MedicalProcedure newMed = new MedicalProcedure();
            newMed.setSummary(m.getSummary());
            newMed.setDescription(m.getDescription());
            newMed.setProcedureDate(m.getProcedureDate());
            newMed.setOrgansAffected(new ArrayList<>(m.getOrgansAffected()));
            newUser.medicalProcedures.add(newMed);
        }

        newUser.changes = new ArrayList<>(this.changes);
        newUser.setUndoStack((Stack<Memento<User>>) this.getUndoStack().clone());
        newUser.setRedoStack((Stack<Memento<User>>) this.getRedoStack().clone());
        return newUser;
    }

    /**
     * Changes this instance of a user to become another user
     *
     * @param other other User object to convert this instance into.
     */
    private void changeInto(User other) {
        this.nhi = other.nhi;
        this.dateOfBirth = other.dateOfBirth;
        this.dateOfDeath = other.dateOfDeath;
        this.healthDetails = other.healthDetails;


        this.contactDetails = other.contactDetails;
        this.contact = other.contact;
        this.contact.setAttachedUser(this);

        this.name = other.name;
        this.firstName = other.firstName;
        this.preferredFirstName = other.preferredFirstName;
        this.middleName = other.middleName;
        this.lastName = other.lastName;

        this.timeCreated = other.timeCreated;
        updateHistory = other.updateHistory;
        this.miscAttributes = other.miscAttributes;
        this.currentMedication = other.currentMedication;
        this.previousMedication = other.previousMedication;
        this.currentMedicationTimes = other.currentMedicationTimes;
        this.previousMedicationTimes = other.previousMedicationTimes;
        this.donorDetails = other.donorDetails;
        this.donorDetails.setAttachedUser(this);
        this.receiverDetails = other.receiverDetails;
        this.receiverDetails.setAttachedUser(this);

        this.currentDiseases = other.currentDiseases;
        this.pastDiseases = other.pastDiseases;
        this.medicalProcedures = other.medicalProcedures;

        this.changes = other.changes;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    @Override
    public void fire(PropertyChangeEvent event) {
        this.pcs.firePropertyChange(event);
    }

}
