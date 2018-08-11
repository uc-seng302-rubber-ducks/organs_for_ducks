package odms.commons.model;

import odms.commons.model._enum.Regions;
import odms.commons.utils.CityDistanceCalculator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.sqrt;

public class CityDistanceCalculatorTest {

    private CityDistanceCalculator distanceCalculator = new CityDistanceCalculator();
    private Regions testRegionA;
    private Regions testRegionB;
    private List<Double> testCoordA = new ArrayList<>();
    private List<Double> testCoordB = new ArrayList<>();
    private List<Double> testVector = new ArrayList<>();

    @Before
    public void beforeTest() {
        testRegionA = Regions.AUCKLAND;
        testRegionB = Regions.WELLINGTON;
        testCoordA.add(0.0);
        testCoordA.add(0.0);
        testCoordB.add(3.0);
        testCoordB.add(4.0);
        testVector.add(1.0);
        testVector.add(2.0);
        testVector.add(3.0);
    }

    @Test
    public void testVectorMagnitude() {
        double vectorTest = distanceCalculator.vectorMagnitude(testVector);
        Assert.assertEquals(sqrt(14), vectorTest, 0.1);
    }

}
