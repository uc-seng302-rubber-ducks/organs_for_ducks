package odms.utils;

import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.AvailableOrganDetail;
import odms.commons.model.datamodel.TransplantDetails;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrganRankerTests {

    private List<TransplantDetails> transplantsWaiting;
    private OrganRanker organRanker;

    @Before
    public void setUp(){
        transplantsWaiting = new ArrayList<>();
        organRanker = new OrganRanker();

    }

    @Test
    public void testTransplantsReturnTwoMatches(){
        LocalDateTime time = LocalDateTime.of(2018, 9, 8, 14, 43);
        AvailableOrganDetail availableOrgan = new AvailableOrganDetail(Organs.LIVER, "abc1234", time, "Otago", "A+", 30);


        TransplantDetails transplant1 = new TransplantDetails("abc1222", "", Organs.LIVER,
                LocalDate.of(2018, 7, 7), "Otago", 30, "A+");
        transplantsWaiting.add(transplant1);
        TransplantDetails transplant2 = new TransplantDetails("abc1272", "", Organs.LIVER,
                LocalDate.of(2018, 7, 7), "Otago", 30, "A+");
        transplantsWaiting.add(transplant2);

        Map<AvailableOrganDetail, List<TransplantDetails>> results = organRanker.matchOrgansToReceivers(availableOrgan,transplantsWaiting);
        Assert.assertEquals(transplant1, results.get(availableOrgan).get(0));
        Assert.assertEquals(transplant2, results.get(availableOrgan).get(1));
    }

    @Test
    public void testTransplantsReturnNoMatchesWhenRegionDifferent(){
        LocalDateTime time = LocalDateTime.of(2018, 9, 8, 14, 43);
        AvailableOrganDetail availableOrganDetail = new AvailableOrganDetail(Organs.LIVER, "abc1234", time, "as", "A+", 30);



        transplantsWaiting.add(new TransplantDetails("abc1222", "", Organs.LIVER,
                LocalDate.of(2018, 7,7), "ae", 30  ,"A+"));

        Map<AvailableOrganDetail, List<TransplantDetails>> results = organRanker.matchOrgansToReceivers(availableOrganDetail,transplantsWaiting);
        Assert.assertTrue(results.get(results.keySet().iterator().next()).isEmpty());

    }


    @Test
    public void testTransplantsHaveTwoReturnedFor2Valid(){
        LocalDateTime time = LocalDateTime.of(2018, 9, 8, 14, 43);
        AvailableOrganDetail e = new AvailableOrganDetail(Organs.KIDNEY, "abc1234", time, "Canterbury", "A+", 30);



        transplantsWaiting.add(new TransplantDetails("abc1222", "", Organs.KIDNEY,
                LocalDate.of(2018, 7,7), "Canterbury", 30  ,"A+"));
        TransplantDetails shouldBe = new TransplantDetails("abc1322", "", Organs.KIDNEY,
                LocalDate.of(2018, 6,7), "Canterbury", 30  ,"A+");
        transplantsWaiting.add(shouldBe);

        Map<AvailableOrganDetail, List<TransplantDetails>> results = organRanker.matchOrgansToReceivers(e,transplantsWaiting);
        Assert.assertEquals(2, results.get(results.keySet().iterator().next()).size());
    }


    @Test
    public void testTransplantsReturnNoMatchesWhenOrganDifferent(){
        LocalDateTime time = LocalDateTime.of(2018, 9, 8, 14, 43);
        AvailableOrganDetail organAvailable = new AvailableOrganDetail(Organs.LIVER, "abc1234", time, "Canterbury", "A+", 30);


        transplantsWaiting.add(new TransplantDetails("abc1222", "", Organs.KIDNEY,
                LocalDate.of(2018, 7,7), "Canterbury", 30  ,"A+"));

        Map<AvailableOrganDetail, List<TransplantDetails>> results = organRanker.matchOrgansToReceivers(organAvailable,transplantsWaiting);
        Assert.assertTrue(results.get(results.keySet().iterator().next()).isEmpty());
    }

    @Test
    public void testOrgansReturnNoneWhenOneIs12(){
        LocalDateTime time = LocalDateTime.of(2018, 9, 8, 14, 43);
        AvailableOrganDetail organAvailable = new AvailableOrganDetail(Organs.KIDNEY, "abc1234", time, "Canterbury", "A+", 12);

        transplantsWaiting.add(new TransplantDetails("abc1222", "", Organs.KIDNEY,
                LocalDate.of(2018, 7,7), "Canterbury", 30  ,"A+"));

        Map<AvailableOrganDetail, List<TransplantDetails>> results = organRanker.matchOrgansToReceivers(organAvailable,transplantsWaiting);
        Assert.assertTrue(results.get(results.keySet().iterator().next()).isEmpty());
    }

    @Test
    public void testOrgansReturnOneWhenBothAre12(){
        LocalDateTime time = LocalDateTime.of(2018, 9, 8, 14, 43);
        AvailableOrganDetail organAvailable = new AvailableOrganDetail(Organs.KIDNEY, "abc1234", time, "Canterbury", "A+", 12);

        transplantsWaiting.add(new TransplantDetails("abc1222", "", Organs.KIDNEY,
                LocalDate.of(2018, 7,7), "Canterbury", 12  ,"A+"));

        Map<AvailableOrganDetail, List<TransplantDetails>> results = organRanker.matchOrgansToReceivers(organAvailable,transplantsWaiting);
        Assert.assertFalse(results.get(results.keySet().iterator().next()).isEmpty());
    }

    @Test
    public void testOrgansReturnNoneWhenAgeIs16yearsHigher(){
        LocalDateTime time = LocalDateTime.of(2018, 9, 8, 14, 43);
        AvailableOrganDetail organAvailable = new AvailableOrganDetail(Organs.KIDNEY, "abc1234", time, "Canterbury", "A+", 30);

        transplantsWaiting.add(new TransplantDetails("abc1222", "", Organs.KIDNEY,
                LocalDate.of(2018, 7,7), "Canterbury", 46  ,"A+"));

        Map<AvailableOrganDetail, List<TransplantDetails>> results = organRanker.matchOrgansToReceivers(organAvailable,transplantsWaiting);
        Assert.assertTrue(results.get(organAvailable).isEmpty() );
    }

    @Test
    public void testOrgansReturnNoneWhenAgeIs16yearsLower(){
        LocalDateTime time = LocalDateTime.of(2018, 9, 8, 14, 43);
        AvailableOrganDetail organAvailable = new AvailableOrganDetail(Organs.KIDNEY, "abc1234", time, "Canterbury", "A+", 46);

        transplantsWaiting.add(new TransplantDetails("abc1222", "", Organs.KIDNEY,
                LocalDate.of(2018, 7,7), "Canterbury", 30  ,"A+"));

        Map<AvailableOrganDetail, List<TransplantDetails>>results = organRanker.matchOrgansToReceivers(organAvailable,transplantsWaiting);
        Assert.assertTrue(results.get(results.keySet().iterator().next()).isEmpty());
    }


    @Test
    public void testOrgansReturnNoneWhenBloodTypesAreDifferent(){
        LocalDateTime time = LocalDateTime.of(2018, 9, 8, 14, 43);
        AvailableOrganDetail organAvailable = new AvailableOrganDetail(Organs.KIDNEY, "abc1234", time, "Canterbury", "A-", 30);;

        transplantsWaiting.add(new TransplantDetails("abc1222", "", Organs.KIDNEY,
                LocalDate.of(2018, 7,7), "Canterbury", 30  ,"A+"));

        Map<AvailableOrganDetail, List<TransplantDetails>> results = organRanker.matchOrgansToReceivers(organAvailable,transplantsWaiting);
        Assert.assertTrue(results.get(results.keySet().iterator().next()).isEmpty());
    }

    @Test
    public void testCorrectOneIsReturnedWhenBestMatchExistsAndBetterSecondMatchHasWrongDistance(){
        LocalDateTime time = LocalDateTime.of(2018, 9, 8, 14, 43);
        AvailableOrganDetail organAvailable = new AvailableOrganDetail(Organs.KIDNEY, "abc1234", time, "Canterbury", "A+", 30);



        TransplantDetails shouldBe = new TransplantDetails("abc1322", "", Organs.KIDNEY,
                LocalDate.of(2018, 7,7), "Canterbury", 30  ,"A+");
        transplantsWaiting.add(shouldBe);
        transplantsWaiting.add(new TransplantDetails("abc1222", "", Organs.KIDNEY,
                LocalDate.of(2018, 6,7), "Otago", 30  ,"A+"));

        Map<AvailableOrganDetail, List<TransplantDetails>> results = organRanker.matchOrgansToReceivers(organAvailable,transplantsWaiting);
        Assert.assertEquals(shouldBe, results.get(results.keySet().iterator().next()).get(0));
    }

    @Test
    public void testNothingIsReturnedWhenDistanceIsTooFar(){
        LocalDateTime time = LocalDateTime.of(2018, 9, 8, 14, 43);
        AvailableOrganDetail organAvailable = new AvailableOrganDetail(Organs.TEST_ORGAN, "abc1234", time, "Southland", "A+", 30);


        transplantsWaiting.add(new TransplantDetails("abc1222", "", Organs.TEST_ORGAN,
                LocalDate.of(2018, 7,7), "Northland", 30  ,"A+"));


        Map<AvailableOrganDetail, List<TransplantDetails>> results = organRanker.matchOrgansToReceivers(organAvailable,transplantsWaiting);
        Assert.assertTrue(results.get(results.keySet().iterator().next()).isEmpty());
    }

    @Test
    public void testMatchIsReturnedWhenDistanceIsOk(){
        LocalDateTime time = LocalDateTime.of(2018, 9, 8, 14, 43);
        AvailableOrganDetail organAvailable = new AvailableOrganDetail(Organs.LIVER, "abc1234", time, "Canterbury", "A+", 30);


        transplantsWaiting.add(new TransplantDetails("abc1222", "", Organs.LIVER,
                LocalDate.of(2018, 7,7), "West Coast", 30  ,"A+"));


        Map<AvailableOrganDetail, List<TransplantDetails>> results = organRanker.matchOrgansToReceivers(organAvailable,transplantsWaiting);
        Assert.assertFalse(results.get(results.keySet().iterator().next()).isEmpty());
    }

    @Test
    public void testNothingIsReturnedIfDonorIsAlsoReceiver(){
        LocalDateTime time = LocalDateTime.of(2018, 9, 8, 14, 43);
        AvailableOrganDetail organAvailable = new AvailableOrganDetail(Organs.KIDNEY, "abc1234", time, "Canterbury", "A-", 30);;

        transplantsWaiting.add(new TransplantDetails("abc1234", "", Organs.KIDNEY,
                LocalDate.of(2018, 7,7), "Canterbury", 30  ,"A-"));

        Map<AvailableOrganDetail, List<TransplantDetails>> results = organRanker.matchOrgansToReceivers(organAvailable,transplantsWaiting);
        Assert.assertTrue(results.get(results.keySet().iterator().next()).isEmpty());
    }


}


