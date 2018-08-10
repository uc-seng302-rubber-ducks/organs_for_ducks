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

    List<TransplantDetails> transplantsWaiting;
    List<AvailableOrganDetail> availableOrganDetails;
    OrganRanker organRanker;

    @Before
    public void setUp(){
        availableOrganDetails = new ArrayList<>();
        transplantsWaiting = new ArrayList<>();
        organRanker = new OrganRanker();

    }

    @Test
    public void testTransplantsReturnTwoMatches(){
        LocalDateTime time = LocalDateTime.of(2018, 9, 8, 14, 43);
        availableOrganDetails.add(new AvailableOrganDetail(Organs.LIVER, "abc1234", time, "Otago", "A+" , 30));
        availableOrganDetails.add(new AvailableOrganDetail(Organs.KIDNEY, "abc1234", time, "Otago", "A+" , 30));




        transplantsWaiting.add(new TransplantDetails("abc1222", "", Organs.LIVER,
                LocalDate.of(2018, 7,7), "Otago", 30  ,"A+"));
        transplantsWaiting.add(new TransplantDetails("abc1222", "", Organs.KIDNEY,
                LocalDate.of(2018, 7,7), "Otago", 30  ,"A+"));

        Map<AvailableOrganDetail, TransplantDetails> results = organRanker.matchOrgansToReceivers(availableOrganDetails,transplantsWaiting);
        Assert.assertTrue(results.keySet().size() == 2);
    }

    @Test
    public void testTransplantsReturnNoMatchesWhenRegionDifferent(){
        LocalDateTime time = LocalDateTime.of(2018, 9, 8, 14, 43);
        availableOrganDetails.add(new AvailableOrganDetail(Organs.LIVER, "abc1234", time, "Otago", "A+" , 30));
        availableOrganDetails.add(new AvailableOrganDetail(Organs.KIDNEY, "abc1234", time, "Otago", "A+" , 30));


        transplantsWaiting.add(new TransplantDetails("abc1222", "", Organs.LIVER,
                LocalDate.of(2018, 7,7), "Canterbury", 30  ,"A+"));
        transplantsWaiting.add(new TransplantDetails("abc1222", "", Organs.KIDNEY,
                LocalDate.of(2018, 7,7), "Canterbury", 30  ,"A+"));

        Map<AvailableOrganDetail, TransplantDetails> results = organRanker.matchOrgansToReceivers(availableOrganDetails,transplantsWaiting);
        Assert.assertTrue(results.keySet().size() == 0);
    }


    @Test
    public void testTransplantThatIsEarlierIsReturnedWhenSecond(){
        LocalDateTime time = LocalDateTime.of(2018, 9, 8, 14, 43);
        AvailableOrganDetail e = new AvailableOrganDetail(Organs.KIDNEY, "abc1234", time, "Canterbury", "A+", 30);
        availableOrganDetails.add(e);


        transplantsWaiting.add(new TransplantDetails("abc1222", "", Organs.KIDNEY,
                LocalDate.of(2018, 7,7), "Canterbury", 30  ,"A+"));
        TransplantDetails shouldBe = new TransplantDetails("abc1322", "", Organs.KIDNEY,
                LocalDate.of(2018, 6,7), "Canterbury", 30  ,"A+");
        transplantsWaiting.add(shouldBe);

        Map<AvailableOrganDetail, TransplantDetails> results = organRanker.matchOrgansToReceivers(availableOrganDetails,transplantsWaiting);
        Assert.assertEquals(shouldBe, results.get(e));
    }

    @Test
    public void testTransplantThatIsEarlierIsReturnedWhenFirst(){
        LocalDateTime time = LocalDateTime.of(2018, 9, 8, 14, 43);
        AvailableOrganDetail e = new AvailableOrganDetail(Organs.KIDNEY, "abc1234", time, "Canterbury", "A+", 30);
        availableOrganDetails.add(e);


        TransplantDetails shouldBe = new TransplantDetails("abc1322", "", Organs.KIDNEY,
                LocalDate.of(2018, 6,7), "Canterbury", 30  ,"A+");
        transplantsWaiting.add(shouldBe);
        transplantsWaiting.add(new TransplantDetails("abc1222", "", Organs.KIDNEY,
                LocalDate.of(2018, 7,7), "Canterbury", 30  ,"A+"));

        Map<AvailableOrganDetail, TransplantDetails> results = organRanker.matchOrgansToReceivers(availableOrganDetails,transplantsWaiting);
        Assert.assertEquals(shouldBe, results.get(e));
    }


    @Test
    public void testTransplantsReturnNoMatchesWhenOrganDifferent(){
        LocalDateTime time = LocalDateTime.of(2018, 9, 8, 14, 43);
        availableOrganDetails.add(new AvailableOrganDetail(Organs.LIVER, "abc1234", time, "Canterbury", "A+" , 30));

        transplantsWaiting.add(new TransplantDetails("abc1222", "", Organs.KIDNEY,
                LocalDate.of(2018, 7,7), "Canterbury", 30  ,"A+"));

        Map<AvailableOrganDetail, TransplantDetails> results = organRanker.matchOrgansToReceivers(availableOrganDetails,transplantsWaiting);
        Assert.assertTrue(results.keySet().size() == 0);
    }

    @Test
    public void testOrgansReturnNoneWhenOneIs12(){
        LocalDateTime time = LocalDateTime.of(2018, 9, 8, 14, 43);
        availableOrganDetails.add(new AvailableOrganDetail(Organs.KIDNEY, "abc1234", time, "Canterbury", "A+" , 12));

        transplantsWaiting.add(new TransplantDetails("abc1222", "", Organs.KIDNEY,
                LocalDate.of(2018, 7,7), "Canterbury", 30  ,"A+"));

        Map<AvailableOrganDetail, TransplantDetails> results = organRanker.matchOrgansToReceivers(availableOrganDetails,transplantsWaiting);
        Assert.assertTrue(results.keySet().size() == 0);
    }

    @Test
    public void testOrgansReturnOneWhenBothIs12(){
        LocalDateTime time = LocalDateTime.of(2018, 9, 8, 14, 43);
        availableOrganDetails.add(new AvailableOrganDetail(Organs.KIDNEY, "abc1234", time, "Canterbury", "A+" , 12));

        transplantsWaiting.add(new TransplantDetails("abc1222", "", Organs.KIDNEY,
                LocalDate.of(2018, 7,7), "Canterbury", 12  ,"A+"));

        Map<AvailableOrganDetail, TransplantDetails> results = organRanker.matchOrgansToReceivers(availableOrganDetails,transplantsWaiting);
        Assert.assertTrue(results.keySet().size() == 1);
    }

    @Test
    public void testOrgansReturnNoneWhenAgeIs16yearsHigher(){
        LocalDateTime time = LocalDateTime.of(2018, 9, 8, 14, 43);
        availableOrganDetails.add(new AvailableOrganDetail(Organs.KIDNEY, "abc1234", time, "Canterbury", "A+" , 30));

        transplantsWaiting.add(new TransplantDetails("abc1222", "", Organs.KIDNEY,
                LocalDate.of(2018, 7,7), "Canterbury", 46  ,"A+"));

        Map<AvailableOrganDetail, TransplantDetails> results = organRanker.matchOrgansToReceivers(availableOrganDetails,transplantsWaiting);
        Assert.assertTrue(results.keySet().size() == 0);
    }

    @Test
    public void testOrgansReturnNoneWhenAgeIs16yearsLower(){
        LocalDateTime time = LocalDateTime.of(2018, 9, 8, 14, 43);
        availableOrganDetails.add(new AvailableOrganDetail(Organs.KIDNEY, "abc1234", time, "Canterbury", "A+" , 46));

        transplantsWaiting.add(new TransplantDetails("abc1222", "", Organs.KIDNEY,
                LocalDate.of(2018, 7,7), "Canterbury", 30  ,"A+"));

        Map<AvailableOrganDetail, TransplantDetails> results = organRanker.matchOrgansToReceivers(availableOrganDetails,transplantsWaiting);
        Assert.assertTrue(results.keySet().size() == 0);
    }


    @Test
    public void testOrgansReturnNoneWhenBloodTypesAreDifferent(){
        LocalDateTime time = LocalDateTime.of(2018, 9, 8, 14, 43);
        availableOrganDetails.add(new AvailableOrganDetail(Organs.KIDNEY, "abc1234", time, "Canterbury", "A-" , 30));

        transplantsWaiting.add(new TransplantDetails("abc1222", "", Organs.KIDNEY,
                LocalDate.of(2018, 7,7), "Canterbury", 30  ,"A+"));

        Map<AvailableOrganDetail, TransplantDetails> results = organRanker.matchOrgansToReceivers(availableOrganDetails,transplantsWaiting);
        Assert.assertTrue(results.keySet().size() == 0);
    }

    @Test
    public void testCorrectOneIsReturnedWhenBestMatchExistsAndBetterSecondMatchHasWrongDistance(){
        LocalDateTime time = LocalDateTime.of(2018, 9, 8, 14, 43);
        AvailableOrganDetail e = new AvailableOrganDetail(Organs.KIDNEY, "abc1234", time, "Canterbury", "A+", 30);
        availableOrganDetails.add(e);


        TransplantDetails shouldBe = new TransplantDetails("abc1322", "", Organs.KIDNEY,
                LocalDate.of(2018, 7,7), "Canterbury", 30  ,"A+");
        transplantsWaiting.add(shouldBe);
        transplantsWaiting.add(new TransplantDetails("abc1222", "", Organs.KIDNEY,
                LocalDate.of(2018, 6,7), "Otago", 30  ,"A+"));

        Map<AvailableOrganDetail, TransplantDetails> results = organRanker.matchOrgansToReceivers(availableOrganDetails,transplantsWaiting);
        Assert.assertEquals(shouldBe, results.get(e));
    }


}


