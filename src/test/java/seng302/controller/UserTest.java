package seng302.controller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import seng302.model._enum.Organs;
import seng302.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class UserTest {

    User testUser;

    @Before
    public void createTestUser() {
        testUser = new User("Frank", LocalDate.of(1938, 2, 3), "ABC1234");
        testUser.setNhi("ABC1234");
    }

    @Test
    public void testEquality() {
        //equal as NHI is equal
        User otherUser = new User("Geoff", LocalDate.of(1938, 2, 3), "ABC1234");
        Assert.assertEquals(testUser, otherUser);
    }

    @Test
    public void testInequality() {
        User otherUser = new User("Geoff", LocalDate.of(1938, 2, 3), "BCD1234");
        Assert.assertNotEquals(testUser, otherUser);
    }

    @Test
    public void UserWithEmptyDonorDetailsHasIsDonorFalse() {
        Assert.assertFalse(testUser.isDonor());
    }

    @Test
    public void UserWithRecentlyEmptyDonorDetailsHasIsDonorFalse() {
        //create a user, give organs to donate, then remove
        //assert isDonor == false
        HashSet<Organs> organs = new HashSet<>();
        organs.add(Organs.CONNECTIVE_TISSUE);
        testUser.getDonorDetails().setOrgans(organs);
        testUser.getDonorDetails().removeOrgan(Organs.CONNECTIVE_TISSUE);

        Assert.assertFalse(testUser.isDonor());
    }

    @Test
    public void UserWithOrgansToDonateHasIsDonorTrue() {
        HashSet<Organs> organs = new HashSet<>();
        organs.add(Organs.CONNECTIVE_TISSUE);
        testUser.getDonorDetails().setOrgans(organs);

        Assert.assertTrue(testUser.isDonor());
    }

    @Test
    public void UserWithEmptyReceiverDetailsHasIsReceiverFalse() {
        Assert.assertFalse(testUser.isReceiver());
    }

    @Test
    public void UserWithRecentlyEmptyReceiverDetailsHasIsReceiverFalse() {
        HashSet<Organs> organs = new HashSet<>();
        organs.add(Organs.CONNECTIVE_TISSUE);
        testUser.getDonorDetails().setOrgans(organs);

        Assert.assertTrue(testUser.isDonor());
    }

    @Test
    public void UpdateLastModifiedSetsLastModifiedTimeToNow() throws Exception {
        //10 ms buffers to ensure times are not the same
        LocalDateTime firstModified = testUser.getLastModified();
        Thread.sleep(10);
        LocalDateTime first = LocalDateTime.now();
        Thread.sleep(10);
        testUser.setName("Jeff", "", "");
        Thread.sleep(10);
        LocalDateTime second = LocalDateTime.now();

        //last modified before change is before first timestamp
        Assert.assertTrue(firstModified.isBefore(first));
        //last modified after change is between the two timestamps
        Assert.assertTrue(
                first.isBefore(testUser.getLastModified()) && testUser.getLastModified().isBefore(second));
    }

    @Test
    public void AgeOfUserShouldBeCorrect() {
        LocalDate dob = testUser.getDateOfBirth();
        long expectedAge = ChronoUnit.YEARS.between(dob, LocalDate.now());

        Assert.assertEquals(Long.toString(expectedAge), testUser.getStringAge());

    }

    @Test
    public void AgeOfDeceasedUserShouldBeCorrect() {
        testUser.setDateOfDeath(LocalDate.of(1939, 2, 3));
        Assert.assertEquals(testUser.getStringAge(), "1");
    }

    @Test
    public void BloodTypeCannotBeInvalid() {
        testUser.setBloodType("Q");
        Assert.assertEquals("U", testUser.getBloodType());
    }

    @Test
    public void AddingCurrentMedicationsAppendsToCurrentMedicationTimes() {
        testUser.addCurrentMedication("test medication");
        Map<String, List<LocalDateTime>> currentMedicationTimes = testUser
                .getCurrentMedicationTimes();
        Assert.assertTrue(currentMedicationTimes.containsKey("test medication"));
        Assert.assertEquals(1, currentMedicationTimes.get("test medication").size());
    }

    @Test
    public void AddingPreviousMedicationsAppendsToPreviousMedicationTimes() {
        testUser.addPreviousMedication("test medication");
        Map<String, List<LocalDateTime>> previousMedicationTimes = testUser
                .getPreviousMedicationTimes();
        Assert.assertTrue(previousMedicationTimes.containsKey("test medication"));
        Assert.assertEquals(1, previousMedicationTimes.get("test medication").size());
    }

    @Test
    public void AddingDuplicateMedicationsDoesNotDuplicateStoredMedication() {
        //both current and previous medications use same method for this, only testing one
        testUser.addCurrentMedication("panadol");
        testUser.addCurrentMedication("panadol");
        Map<String, List<LocalDateTime>> currentMedicationTimes = testUser
                .getCurrentMedicationTimes();

        //one entry with key "panadol"
        Assert.assertTrue(currentMedicationTimes.containsKey("panadol"));
        Assert.assertEquals(1, currentMedicationTimes.size());

        //panadol entry has two times
        Assert.assertEquals(2, currentMedicationTimes.get("panadol").size());
    }

    @Test
    public void NonDonorToolTipShouldReturnName() {
        Assert.assertFalse(testUser.isDonor());
        String tooltip = testUser.getTooltip();
        Assert.assertEquals(testUser.getFullName(), tooltip);
    }

    @Test
    public void DonorToolTipShouldReturnNameAndOrgans() throws Exception {
        testUser.getDonorDetails().addOrgan(Organs.HEART);
        String tooltip = testUser.getTooltip();
        String name = testUser.getFullName();
        Assert.assertEquals(name + ". Donor: Heart ", tooltip);
    }

}
