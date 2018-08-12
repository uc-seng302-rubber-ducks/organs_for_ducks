package odms.commons.utils;

import odms.commons.model._enum.Regions;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

public class CityDistanceCalculator {

    private static final double EARTH_RADIUS = 6371; //km

    public List<Double> extractCoordFromRegion(Regions region) {
        List<Double> result = new ArrayList<>();
        result.add(region.getSCoord());
        result.add(region.getECoord());
        return result;
    }

    public double haversineCalculation(List<Double> coordA, List<Double> coordB) {
        double hav1 = sin(toRadians(coordB.get(0) - coordA.get(0)) / 2) * sin(toRadians(coordB.get(0) - coordA.get(0)) / 2);
        double hav2 = sin(toRadians(coordB.get(1) - coordA.get(1)) / 2) * sin(toRadians(coordB.get(1) - coordA.get(1)) / 2);

        double arcsin = asin(sqrt(hav1 + cos(toRadians(coordA.get(0))) * cos(toRadians(coordB.get(0))) * hav2));

        return 2 * EARTH_RADIUS * arcsin;
    }

    public double distanceBetweenRegions(Regions regionA, Regions regionB) {
        List<Double> regionACoord = extractCoordFromRegion(regionA);
        List<Double> regionBCoord = extractCoordFromRegion(regionB);

        return haversineCalculation(regionACoord, regionBCoord);
    }


}
