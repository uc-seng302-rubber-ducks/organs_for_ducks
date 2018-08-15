package odms.commons.model;

import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.AvailableOrganDetail;
import odms.commons.model.datamodel.TransplantDetails;
import odms.commons.utils.OrganSorter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrganSorterTest {

    List<TransplantDetails> transplantList;
    AvailableOrganDetail organ;

    @Before
    public void setUp() {
        transplantList = new ArrayList<>();
        LocalDateTime time = LocalDateTime.of(2018, 9, 8, 14, 43);
        organ = new AvailableOrganDetail(Organs.KIDNEY, "abc1234", time, "Otago", "A+", 30);
    }

    @Test
    public void testSecondEntryIsReturnedFirst(){

        TransplantDetails transplant1 = new TransplantDetails("abc1222", "", Organs.LIVER,
                LocalDate.of(2018, 7, 7), "Otago", 30, "A+");
        transplantList.add(transplant1);
        TransplantDetails transplant2 = new TransplantDetails("abc1222", "", Organs.KIDNEY,
                LocalDate.of(2018, 7, 6), "Otago", 30, "A+");
        transplantList.add(transplant2);

        transplantList = OrganSorter.sortOrgansIntoRankedOrder(organ, transplantList);

        Assert.assertEquals(transplant2, transplantList.get(0));
    }

    @Test
    public void testSecondEntryIsReturnedFirstWhenDatesSameAndBetterDistanceSecond(){

        TransplantDetails transplant1 = new TransplantDetails("abc1222", "", Organs.LIVER,
                LocalDate.of(2018, 7, 7), "Canterbury", 30, "A+");
        transplantList.add(transplant1);
        TransplantDetails transplant2 = new TransplantDetails("abc1222", "", Organs.KIDNEY,
                LocalDate.of(2018, 7, 7), "Otago", 30, "A+");
        transplantList.add(transplant2);

        transplantList = OrganSorter.sortOrgansIntoRankedOrder(organ, transplantList);

        Assert.assertEquals(transplant2, transplantList.get(0));
    }


    @Test
    public void testFirstEntryIsReturnedFirst(){

        TransplantDetails transplant1 = new TransplantDetails("abc1222", "", Organs.LIVER,
                LocalDate.of(2018, 7, 6), "Otago", 30, "A+");
        transplantList.add(transplant1);
        TransplantDetails transplant2 = new TransplantDetails("abc1222", "", Organs.KIDNEY,
                LocalDate.of(2018, 7, 7), "Otago", 30, "A+");
        transplantList.add(transplant2);

        transplantList = OrganSorter.sortOrgansIntoRankedOrder(organ, transplantList);

        Assert.assertEquals(transplant1, transplantList.get(0));
    }


    @Test
    public void testFirstEntryIsReturnedFirstAndSecondSecondWhenEqual(){

        TransplantDetails transplant1 = new TransplantDetails("abc1222", "", Organs.LIVER,
                LocalDate.of(2018, 7, 7), "Otago", 30, "A+");
        transplantList.add(transplant1);
        TransplantDetails transplant2 = new TransplantDetails("abc1222", "", Organs.KIDNEY,
                LocalDate.of(2018, 7, 7), "Otago", 30, "A+");
        transplantList.add(transplant2);

        transplantList = OrganSorter.sortOrgansIntoRankedOrder(organ, transplantList);

        Assert.assertEquals(transplant1, transplantList.get(0));
        Assert.assertEquals(transplant2, transplantList.get(1));
    }


    @Test
    public void testThreeEntriesAreInCorrectOrder(){

        TransplantDetails transplant1 = new TransplantDetails("abc1222", "", Organs.LIVER,
                LocalDate.of(2018, 7, 7), "Otago", 30, "A+");
        transplantList.add(transplant1);
        TransplantDetails transplant2 = new TransplantDetails("abc1222", "", Organs.KIDNEY,
                LocalDate.of(2018, 7, 6), "Otago", 30, "A+");
        transplantList.add(transplant2);
        TransplantDetails transplant3 = new TransplantDetails("abc1222", "", Organs.KIDNEY,
                LocalDate.of(2018, 7, 5), "Otago", 30, "A+");
        transplantList.add(transplant3);

        transplantList = OrganSorter.sortOrgansIntoRankedOrder(organ, transplantList);

        Assert.assertEquals(transplant3, transplantList.get(0));
        Assert.assertEquals(transplant2, transplantList.get(1));
        Assert.assertEquals(transplant1, transplantList.get(2));
    }

}
