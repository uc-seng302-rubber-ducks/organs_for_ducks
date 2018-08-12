package odms.commons.model;

import odms.commons.model._enum.Regions;
import odms.commons.utils.CityDistanceCalculator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CityDistanceCalculatorTest {

    private CityDistanceCalculator distanceCalculator = new CityDistanceCalculator();
    private Regions testRegionA;
    private Regions testRegionB;
    private List<Double> testCoordA = new ArrayList<>();
    private List<Double> testCoordB = new ArrayList<>();

    @Before
    public void beforeTest() {
        testRegionA = Regions.AUCKLAND;
        testRegionB = Regions.WELLINGTON;
        testCoordA.add(0.0);
        testCoordA.add(0.0);
        testCoordB.add(180.0);
        testCoordB.add(0.0);
    }

    @Test
    public void testCalculateDistance() {
        double testDistance = distanceCalculator.haversineCalculation(testCoordA, testCoordB);
        Assert.assertEquals(20015, testDistance, 2);
    }

    @Test
    public void testExtractCoordinatesFromRegion() {
        List<Double> testCoord = distanceCalculator.extractCoordFromRegion(testRegionA);
        Assert.assertEquals(-36.8485, testCoord.get(0), 0.0001);
        Assert.assertEquals(174.7633, testCoord.get(1), 0.0001);
    }

    @Test
    public void testDistanceBetweenRegions() {
        double testDistance = distanceCalculator.distanceBetweenRegions(testRegionA,testRegionB);
        Assert.assertEquals(492, testDistance, 2);
    }
}
