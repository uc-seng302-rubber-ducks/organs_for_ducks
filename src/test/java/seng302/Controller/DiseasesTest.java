package seng302.Controller;

import org.junit.Before;
import org.junit.Test;
import seng302.Model.Disease;
import seng302.Model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.fail;

public class DiseasesTest {

    private AppController controller;
    ArrayList<Disease> baseOrder = new ArrayList<Disease>(8);
    ArrayList<Disease> orderedDate = new ArrayList<Disease>(8);
    ArrayList<Disease> orderedName = new ArrayList<Disease>(8);
    ArrayList<Disease> orderedChronic = new ArrayList<Disease>(8);

    @Before
    public void resetUsers() {
        DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        controller = AppController.getInstance();
        controller.setUsers(new ArrayList<>());
        try {
            controller.Register("Donor", LocalDate.parse("1978-03-06", sdf), "ABC1234");
            //controller.Register("One Organ", LocalDate.parse("1997-02-05", sdf), "ASD2345");
            User user = controller.findUsers("Donor").get(0);
            Disease diseaseA1 = new Disease("A1", true, false, LocalDate.parse("2000-01-06", sdf));
            Disease diseaseA2 = new Disease("A2", false, false, LocalDate.parse("2000-01-05", sdf));
            Disease diseaseA3 = new Disease("A3", true, false, LocalDate.parse("2000-01-04", sdf));
            Disease diseaseB1 = new Disease("B1", false, false, LocalDate.parse("2000-01-01", sdf));
            Disease diseaseC1 = new Disease("C1", true, false, LocalDate.parse("2000-01-03", sdf));
            Disease diseaseD1 = new Disease("D1", false, false, LocalDate.parse("2000-01-02", sdf));
            Disease diseaseb1 = new Disease("b1", false, false, LocalDate.parse("2000-01-01", sdf));
            Disease diseasec1 = new Disease("c1", true, false, LocalDate.parse("2000-01-03", sdf));

            //ArrayList<Disease> baseOrder = new ArrayList<Disease>(8); // ????? I had the left side as a collection but then i couldnt use baseOrder.get???
            baseOrder.add(diseaseA1);
            baseOrder.add(diseaseA2);
            baseOrder.add(diseaseA3);
            baseOrder.add(diseaseB1);
            baseOrder.add(diseaseC1);
            baseOrder.add(diseaseD1);
            baseOrder.add(diseaseb1);
            baseOrder.add(diseasec1);

            //Collection<Disease> orderedDate = new ArrayList<Disease>(8);
            orderedDate.add(diseaseB1);
            orderedDate.add(diseaseb1);
            orderedDate.add(diseaseD1);
            orderedDate.add(diseaseC1);
            orderedDate.add(diseasec1);
            orderedDate.add(diseaseA3);
            orderedDate.add(diseaseA2);
            orderedDate.add(diseaseA1);

            //Collection<Disease> orderedName = new ArrayList<Disease>(8);
            orderedName.add(diseaseA1);
            orderedName.add(diseaseA2);
            orderedName.add(diseaseA3);
            orderedName.add(diseaseB1);
            orderedName.add(diseaseC1);
            orderedName.add(diseaseD1);
            orderedName.add(diseaseb1);
            orderedName.add(diseasec1);

            //Collection<Disease> orderedChronic = new ArrayList<Disease>(8);
            orderedChronic.add(diseaseA1);
            orderedChronic.add(diseaseA3);
            orderedChronic.add(diseaseC1);
            orderedChronic.add(diseasec1);
            orderedChronic.add(diseaseA2);
            orderedChronic.add(diseaseB1);
            orderedChronic.add(diseaseD1);
            orderedChronic.add(diseaseb1);

            //Populate user.currentDiseases
            for (int i = 0; i < 8; i++) {
                user.getCurrentDiseases().add(baseOrder.get(i));
            }

        } catch (Exception ex) {
            //System.out.println(ex);
            fail("Error setting up before test");
        }
    }

    /**
     * Three test to check if their respective comparators work on sample data
     */
    @Test
    public void shouldBeOrderedByDateAscending() { //Tests the comparator works correctly
        //Basic Objects
        User user = controller.getUser("ABC1234");
        Disease disease = new Disease("", false, false, LocalDate.now());

        Collections.sort(user.getCurrentDiseases(), disease.diseaseDateComparator);

        //bootleg .equals()
        boolean assertion = true;
        for (int i = 0; i < 8; i++) {
            if (!(user.getCurrentDiseases().get(i) == orderedDate.get(i))) {
                assertion = false;
            }
        }
        assert assertion;
    }

    @Test
    public void shouldBeOrderByNameAscending() {
        User user = controller.getUser("ABC1234");
        Disease disease = new Disease("", false, false, LocalDate.now());

        Collections.sort(user.getCurrentDiseases(), disease.diseaseNameComparator);

        //assert (user.getCurrentDiseases().equals(orderedName));
        //bootleg .equals()
        boolean assertion = true;
        for (int i = 0; i < 8; i++) {
            if (!(user.getCurrentDiseases().get(i) == orderedName.get(i))) {
                assertion = false;
            }
        }
        assert assertion;
    }

    @Test
    public void shouldBeOrderByChronicAscending() {
        User user = controller.getUser("ABC1234");
        Disease disease = new Disease("", false, false, LocalDate.now());

        Collections.sort(user.getCurrentDiseases(), disease.diseaseChronicComparator);

        //assert (user.getCurrentDiseases().equals(orderedChronic));
        //bootleg .equals()
        boolean assertion = true;
        for (int i = 0; i < 8; i++) {
            if (!(user.getCurrentDiseases().get(i) == orderedChronic.get(i))) {
                assertion = false;
            }
        }
        assert assertion;
    }

    @Test
    public void chronicDiseaseShouldNotBeAbleToBeDeleted() {

    }
}
