package odms.commons.utils;

import odms.commons.model._enum.Regions;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

public class CityDistanceCalculator {

    private static final double EARTH_RADIUS = 6371000; //m

    /**
     * Gets the lattitude and longitude of a region
     *
     * @param region region to get the latitude and longitude from
     * @return A list containing two items, a latitude and longitude
     */
    public List<Double> extractCoordFromRegion(Regions region) {
        List<Double> result = new ArrayList<>();
        result.add(region.getSCoord());
        result.add(region.getECoord());
        return result;
    }

    /**
     * Performs a haversine calculation to find how far away one location is from another
     * @param coordA latitude and longitude of location A
     * @param coordB latitude and longitude of location B
     * @return distance in a straight line in metres
     */
    public double haversineCalculation(List<Double> coordA, List<Double> coordB) {
        double hav1 = sin(toRadians(coordB.get(0) - coordA.get(0)) / 2) * sin(toRadians(coordB.get(0) - coordA.get(0)) / 2);
        double hav2 = sin(toRadians(coordB.get(1) - coordA.get(1)) / 2) * sin(toRadians(coordB.get(1) - coordA.get(1)) / 2);

        double arcsin = asin(sqrt(hav1 + cos(toRadians(coordA.get(0))) * cos(toRadians(coordB.get(0))) * hav2));

        return 2 * EARTH_RADIUS * arcsin;
    }

    /**
     * Calculates the distance between regions
     * @param regionA Region A
     * @param regionB Region B
     * @return the distance between two regions in metres
     */
    public double distanceBetweenRegions(Regions regionA, Regions regionB) {
        List<Double> regionACoord = extractCoordFromRegion(regionA);
        List<Double> regionBCoord = extractCoordFromRegion(regionB);

        return haversineCalculation(regionACoord, regionBCoord);
    }


    public double distanceBetweenRegions(String regionA, String regionB) {

        Regions r1 = Regions.valueOf(regionA.toUpperCase().replaceAll(" ", ""));
        Regions r2 = Regions.valueOf(regionB.toUpperCase().replaceAll(" ", ""));

        return distanceBetweenRegions(r1, r2);
    }


}
