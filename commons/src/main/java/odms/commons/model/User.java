package odms.commons.model;

import com.google.gson.annotations.Expose;
import javafx.collections.FXCollections;
import odms.commons.model._abstract.Listenable;
import odms.commons.model._abstract.Undoable;
import odms.commons.model._enum.EventTypes;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.ContactDetails;
import odms.commons.model.datamodel.ReceiverOrganDetailsHolder;

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

    public static User clone(User user) {
        User newUser = new User();
        newUser.nhi = user.nhi;
        newUser.dateOfBirth = user.dateOfBirth;
        newUser.dateOfDeath = user.dateOfDeath;

        newUser.contactDetails = user.contactDetails;
        if (user.contact != null) {
            newUser.contact = new EmergencyContact(user.contact.getName(), user.contact.getCellPhoneNumber(),
                    user.contact.getHomePhoneNumber(), user.contact.getAddress(),
                    user.contact.getEmail(), user.contact.getRelationship());
        } else {
            newUser.contact = null;
        }

        newUser.healthDetails = user.healthDetails;

        newUser.name = user.name;
        newUser.firstName = user.firstName;
        newUser.preferredFirstName = user.preferredFirstName;
        newUser.middleName = user.middleName;
        newUser.lastName = user.lastName;

        newUser.timeCreated = user.timeCreated;
        if (user.updateHistory != null) {
            newUser.updateHistory = new HashMap<>(user.updateHistory);
        } else {
            newUser.updateHistory = new HashMap<>();
        }
        newUser.miscAttributes = new ArrayList<>(user.miscAttributes);
        newUser.currentMedication = new ArrayList<>(user.currentMedication);
        newUser.previousMedication = new ArrayList<>(user.previousMedication);
        newUser.currentMedicationTimes = new HashMap<>(user.currentMedicationTimes);
        newUser.previousMedicationTimes = new HashMap<>(user.previousMedicationTimes);
        newUser.donorDetails = new DonorDetails(newUser);
        for (Organs o : user.donorDetails.getOrgans()) {
            newUser.donorDetails.getOrgans().add(o);
        }
        newUser.receiverDetails = new ReceiverDetails(newUser);
        //Map<Organs, ArrayList<ReceiverOrganDetailsHolder>> organs = new EnumMap<Organs, ArrayList<ReceiverOrganDetailsHolder>>(this.receiverDetails.getOrgans());
        //newUser.receiverDetails.setOrgans(organs);
        for (Organs o : user.receiverDetails.getOrgans().keySet()) {
            ArrayList<ReceiverOrganDetailsHolder> detailHolders = new ArrayList<>(user.receiverDetails.getOrgans().get(o));
            for (int i = 0; i < user.receiverDetails.getOrgans().get(o).size(); i++) {
                ReceiverOrganDetailsHolder newHolder = new ReceiverOrganDetailsHolder(null, null, null);// = newUser.receiverDetails.getOrgans().get(o).get(i);
                ReceiverOrganDetailsHolder oldHolder = user.receiverDetails.getOrgans().get(o).get(i);
                newHolder.setStartDate(oldHolder.getStartDate());
                newHolder.setStopDate(oldHolder.getStopDate());
                newHolder.setOrganDeregisterReason(oldHolder.getOrganDeregisterReason());
                detailHolders.add(newHolder);
            }
            newUser.receiverDetails.getOrgans().put(o, detailHolders);
        }

        newUser.currentDiseases = new ArrayList<>(user.currentDiseases);
        newUser.pastDiseases = new ArrayList<>(user.pastDiseases);
        newUser.medicalProcedures = new ArrayList<>();
        for (MedicalProcedure m : user.medicalProcedures) {
            MedicalProcedure newMed = new MedicalProcedure();
            newMed.setSummary(m.getSummary());
            newMed.setDescription(m.getDescription());
            newMed.setProcedureDate(m.getProcedureDate());
            newMed.setOrgansAffected(new ArrayList<>(m.getOrgansAffected()));
            newUser.medicalProcedures.add(newMed);
        }

        newUser.changes = new ArrayList<>(user.changes);
        newUser.setUndoStack((Stack<Memento<User>>) user.getUndoStack().clone());
        newUser.setRedoStack((Stack<Memento<User>>) user.getRedoStack().clone());
        return newUser;
    }

    public ContactDetails getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(ContactDetails contactDetails) {
        this.saveStateForUndo();
        updateLastModified();
        this.contactDetails = contactDetails;
    }

    public EmergencyContact getContact() {
        return contact;
    }

    public void setContact(EmergencyContact contact) {
        this.saveStateForUndo();
        updateLastModified();
        this.contact = contact;
    }

    public HealthDetails getHealthDetails() {
        return healthDetails;
    }

    public void setHealthDetails(HealthDetails healthDetails) {
        this.saveStateForUndo();
        updateLastModified();
        this.healthDetails = healthDetails;
    }

    public DonorDetails getDonorDetails() {
        return donorDetails;
    }

    public void setDonorDetails(DonorDetails donorDetails) {
        this.saveStateForUndo();
        updateLastModified();
        this.donorDetails = donorDetails;
    }

    public ReceiverDetails getReceiverDetails() {
        return receiverDetails;
    }

    public void setReceiverDetails(ReceiverDetails receiverDetails) {
        this.saveStateForUndo();
        updateLastModified();
        this.receiverDetails = receiverDetails;
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
        this.saveStateForUndo();
        updateLastModified();
        this.nhi = nhi;
        addChange(new Change("Updated NHI to " + nhi));
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
        this.saveStateForUndo();
        this.firstName = fName;
        this.middleName = mName;
        this.lastName = lName;
        updateLastModified();
        addChange(new Change("set full name to " + fName + " " + mName + " " + lName));
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String name) {
        this.saveStateForUndo();
        updateLastModified();
        // Changes the default case where the preferred name is the same as the first name
        if (preferredFirstName == null || preferredFirstName.equals(firstName)) {
            preferredFirstName = name;
        }
        this.firstName = name;
        addChange(new Change("Changed first name to " + name));

    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String name) {
        this.saveStateForUndo();
        updateLastModified();
        this.middleName = name;
        addChange(new Change("Changed middle name to " + middleName));
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String name) {
        this.saveStateForUndo();
        updateLastModified();
        this.lastName = name;
        addChange(new Change("Changed last name to " + lastName));
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
        this.saveStateForUndo();
        updateLastModified();
        this.dateOfBirth = dateOfBirth;
        addChange(new Change("Changed date of birth to " + dateOfBirth.toString()));
    }

    public LocalDate getDateOfDeath() {
        return dateOfDeath;
    }

    public void setDateOfDeath(LocalDate dateOfDeath) {
        this.saveStateForUndo();
        updateLastModified();
        this.dateOfDeath = dateOfDeath;
        this.isDeceased = dateOfDeath != null;
        addChange(new Change(isDeceased ? ("Changed date of death to " + dateOfDeath.toString())
                : "Removed date of death"));
    }

    public double getHeight() {
        return healthDetails.getHeight();
    }

    public void setHeight(double height) {
        if (healthDetails.getHeight() != height) {
            this.saveStateForUndo();
            updateLastModified();
            healthDetails.setHeight(height);
            addChange(new Change("Changed height to " + height));
        }
    }

    public double getWeight() {
        return healthDetails.getWeight();
    }

    public void setWeight(double weight) {
        if (weight != healthDetails.getWeight()) {
            this.saveStateForUndo();
            updateLastModified();
            healthDetails.setWeight(weight);
            addChange(new Change("Changed weight to " + weight));
        }
    }

    public String getHeightText() {
        return healthDetails.getHeightText();
    }

    public void setHeightText(String height) {
        this.saveStateForUndo();
        updateLastModified();
        if (!(healthDetails.getHeightText().equals(height))) {
            healthDetails.setHeightText(height);
            addChange(new Change("set height to " + height));
        }
    }

    public String getWeightText() {
        return healthDetails.getWeightText();
    }

    public void setWeightText(String weight) {
        this.saveStateForUndo();
        updateLastModified();
        if (!(healthDetails.getWeightText().equals(weight))) {
            healthDetails.setWeightText(weight);
            addChange(new Change("set weight to " + weight));
        }
    }

    public String getBloodType() {
        return healthDetails.getBloodType();
    }

    public void setBloodType(String bloodType) {
        String validType = groupBloodType(bloodType);
        if (!healthDetails.getBloodType().equals(validType)) {
            this.saveStateForUndo();
            updateLastModified();
            healthDetails.setBloodType(validType);
            addChange(new Change("Changed blood type to " + bloodType));
        }
    }

    public String getBirthGender() {
        return healthDetails.getBirthGender();
    }

    public void setBirthGender(String birthGender) {
        this.saveStateForUndo();
        updateLastModified();
        // Changes the default case where the gender identity is the same as the birth gender
        if (healthDetails.getGenderIdentity() == null) {
            healthDetails.setGenderIdentity(healthDetails.getBirthGender());
        }
        healthDetails.setBirthGender(birthGender);
        addChange(new Change("Changed birth gender to " + birthGender));
    }

    public String getGenderIdentity() {
        return healthDetails.getGenderIdentity();
    }

    public void setGenderIdentity(String genderIdentity) {
        this.saveStateForUndo();
        updateLastModified();
        healthDetails.setGenderIdentity(genderIdentity);
        addChange(new Change("Changed birth Identity to " + genderIdentity));
    }

    public String getAlcoholConsumption() {
        return healthDetails.getAlcoholConsumption();
    }

    public void setAlcoholConsumption(String alcoholConsumption) {
        this.saveStateForUndo();
        updateLastModified();
        healthDetails.setAlcoholConsumption(alcoholConsumption);
        addChange(new Change("Changed alcohol consumption to " + alcoholConsumption));
    }

    public String getAddress() {
        return contactDetails.getAddress().toString();
    }

    public boolean isSmoker() {
        return healthDetails.isSmoker();
    }

    public void setSmoker(boolean smoker) {
        this.saveStateForUndo();
        updateLastModified();
        healthDetails.setSmoker(smoker);
        addChange(new Change("Changed smoker status to " + smoker));
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
        this.saveStateForUndo();
        updateLastModified();
        contactDetails.getAddress().setRegion(region);
        if (contactDetails.getAddress() != null && !contactDetails.getAddress().equals("")) {
            addChange(new Change("Changed region to " + region));
        }
    }

    public LocalDateTime getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(LocalDateTime timeCreated) {
        this.timeCreated = timeCreated;
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
        this.saveStateForUndo();
        updateLastModified();
        this.preferredFirstName = preferredFirstName;
        addChange(new Change("Changed preferred first name to " + preferredFirstName));
    }

    public String getHomePhone() {
        return contactDetails.getHomePhoneNumber();
    }

    public void setHomePhone(String homePhone) {
        this.saveStateForUndo();
        updateLastModified();
        contactDetails.setHomePhoneNumber(homePhone);
        addChange(new Change("Changed Home phone to " + homePhone));
    }

    public String getCellPhone() {
        return contactDetails.getCellPhoneNumber();
    }

    public void setCellPhone(String cellPhone) {
        this.saveStateForUndo();
        updateLastModified();
        contactDetails.setCellPhoneNumber(cellPhone);
        addChange(new Change("Changed cell Phone to " + cellPhone));
    }

    public String getEmail() {
        return contactDetails.getEmail();
    }

    // @TODO: find all instances of potential updates and add to the Hashmap

    public void setEmail(String email) {
        this.saveStateForUndo();
        updateLastModified();
        contactDetails.setEmail(email);
        addChange(new Change("Changed email to " + email));
    }

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
        this.saveStateForUndo();
        updateLastModified();
        currentMedication.add(medication);
        addMedicationTimes(medication, currentMedicationTimes);
        addChange(new Change("Added current medication" + medication));
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

    @Override
    public void setDeleted(boolean deleted) {
        super.setDeleted(deleted);
        addChange(new Change("Deleted user"));
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

    public void saveStateForUndo() {
        Memento<User> memento = new Memento<>(User.clone(this));
        getUndoStack().push(memento);
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
        getRedoStack().push(new Memento<>(clone(this)));
        Memento<User> memento = getUndoStack().pop();
        this.changeInto(memento.getState());
        addChange(new Change("undo"));
    }

    @Override
    public void redo() {
        if (getRedoStack().isEmpty()) {
            return;
        }
        getUndoStack().push(new Memento<>(clone(this)));
        Memento<User> memento = getRedoStack().pop();
        this.changeInto(memento.getState());
        addChange(new Change("redo"));
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
