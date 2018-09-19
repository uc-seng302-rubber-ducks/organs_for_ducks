package odms.commons.model;

import odms.commons.model._abstract.IgnoreForUndo;
import odms.commons.model.datamodel.ContactDetails;
import odms.commons.model.datamodel.Medication;
import odms.commons.model.dto.UserOverview;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * builder class for user utilising the
 *
 * @see IgnoreForUndo annotation
 * any changes by this builder will not be stored as undoable actions, (ideally) saving on memory
 */
@IgnoreForUndo
public class UserBuilder {

    private User secret;

    public UserBuilder() {
        secret = new User();
    }

    public UserBuilder(UserOverview basedOn) {
        secret = UserOverview.toUser(basedOn);
    }

    public UserBuilder setUniqueId(int uniqueId) {
        secret.setUniqueId(uniqueId);
        return this;
    }

    public UserBuilder setDateOfBirth(LocalDate dateOfBirth) {
        secret.setDateOfBirth(dateOfBirth);
        return this;
    }

    public UserBuilder setDateOfDeath(LocalDate dateOfDeath) {
        secret.setDateOfDeath(dateOfDeath);
        return this;
    }

    public UserBuilder setDeathCity(String deathCity) {
        secret.setDeathCity(deathCity);
        return this;
    }

    public UserBuilder setDeathRegion(String deathRegion) {
        secret.setDeathRegion(deathRegion);
        return this;
    }

    public UserBuilder setDeathCountry(String deathCountry) {
        secret.setDeathCountry(deathCountry);
        return this;
    }

    public UserBuilder setPreferredFirstName(String preferredFirstName) {
        secret.setPreferredFirstName(preferredFirstName);
        return this;
    }

    public UserBuilder setMiddleName(String middleName) {
        secret.setMiddleName(middleName);
        return this;
    }

    public UserBuilder setLastName(String lastName) {
        secret.setLastName(lastName);
        return this;
    }

    public UserBuilder setBirthCountry(String birthCountry) {
        secret.setBirthCountry(birthCountry);
        return this;
    }

    public UserBuilder setProfilePhotoFilePath(String profilePhotoFilePath) {
        secret.setProfilePhotoFilePath(profilePhotoFilePath);
        return this;
    }

    public UserBuilder setContact(EmergencyContact contact) {
        secret.setContact(contact);
        return this;
    }

    public UserBuilder setContactDetails(ContactDetails contactDetails) {
        secret.setContactDetails(contactDetails);
        return this;
    }

    public UserBuilder setHealthDetails(HealthDetails healthDetails) {
        secret.setHealthDetails(healthDetails);
        return this;
    }

    public UserBuilder setLastModified(LocalDateTime lastModified) {
        secret.setLastModified(lastModified);
        return this;
    }

    public UserBuilder setUpdateHistory(Map<String, String> updateHistory) {
        secret.setUpdateHistory(updateHistory);
        return this;
    }

    public UserBuilder setPreviousMedication(List<Medication> previousMedication) {
        secret.setPreviousMedication(previousMedication);
        return this;
    }

    public UserBuilder setCurrentMedication(List<Medication> currentMedication) {
        secret.setCurrentMedication(currentMedication);
        return this;
    }

    public UserBuilder setMedicalProcedures(List<MedicalProcedure> medicalProcedures) {
        secret.setMedicalProcedures(medicalProcedures);
        return this;
    }

    public UserBuilder setDonorDetails(DonorDetails donorDetails) {
        secret.setDonorDetails(donorDetails);
        return this;
    }

    public UserBuilder setReceiverDetails(ReceiverDetails receiverDetails) {
        secret.setReceiverDetails(receiverDetails);
        return this;
    }

    public UserBuilder setPastDiseases(List<Disease> pastDiseases) {
        secret.setPastDiseases(pastDiseases);
        return this;
    }

    public UserBuilder setCurrentDiseases(List<Disease> currentDiseases) {
        secret.setCurrentDiseases(currentDiseases);
        return this;
    }

    public UserBuilder setNhi(String nhi) {
        secret.setNhi(nhi);
        return this;
    }

    public UserBuilder setFirstName(String name) {
        secret.setFirstName(name);
        return this;
    }

    public User build() {
        return secret;
    }
}
