package seng302.Controller;

import java.time.LocalDate;
import java.util.HashSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import seng302.Model.Organs;
import seng302.Model.User;

public class UserTest {

  User testUser;

  @Before
  public void createTestUser() {
    testUser = new User("Frank", LocalDate.of(1938, 2, 3));
    testUser.setNHI("ABC1234");
  }

  @Test
  public void testEquality() {
    //equal as NHI is equal
    User otherUser = new User("Geoff", LocalDate.of(1938, 2, 3));
    otherUser.setNHI("ABC1234");
    Assert.assertEquals(testUser, otherUser);
  }

  @Test
  public void testInequality() {
    User otherUser = new User("Geoff", LocalDate.of(1938, 2, 3));
    otherUser.setNHI("BCD123");
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

  }

  @Test
  public void UserWithRecentlyEmptyReceiverDetailsHasIsReceiverFalse() {
    //create a user, give organs to receive, remove them
    //assert isReceiver == false
  }

  @Test
  public void UpdateLastModifiedSetsLastModifiedTimeToNow() {
    //get time now
    //perform action
    //get time now
    //assert last updated is between two timestamps
  }

  @Test
  public void AgeOfUserShouldBeCorrect() {

  }

  @Test
  public void AgeOfDeceasedUserShouldBeCorrect() {

  }

  @Test
  public void BloodTypeCannotBeInvalid() {
    //try give it blood type Q
    //should be set to U for unknown
  }

  @Test
  public void AddingCurrentMedicationsAppendsToCurrentMedicationTimes() {

  }

  @Test
  public void AddingPreviousMedicationsAppendsToPreviousMedicationTimes() {

  }

  @Test
  public void AddingDuplicateMedicationsDoesNotDuplicateStoredMedication() {
    //add a medication twice
    //assert that it has two time entries logged against it
  }

  @Test
  public void NonDonorToolTipShouldReturnName() {

  }

  @Test
  public void DonorToolTipShouldReturnNameAndOrgans() {

  }
}
