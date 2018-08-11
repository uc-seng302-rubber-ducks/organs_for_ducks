package odms.commons.utils;

import odms.commons.model._enum.Regions;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

public class CityDistanceCalculator {

    private static final double EARTH_RADIUS = 6371; //km

    public double distanceBetweenRegions(Regions regionA, Regions regionB) {
        List<Double> regionACoord = extractCoordFromRegion(regionA);
        List<Double> regionBCoord = extractCoordFromRegion(regionB);

        return calculateDistance(regionACoord, regionBCoord);

    }

    private List<Double> extractCoordFromRegion(Regions region) {
        List<Double> result = new ArrayList<>();
        result.add(region.getECoord());
        result.add(region.getSCoord());
        return result;
    }

    public double calculateDistance(List<Double> coordA, List<Double> coordB) {
        List<Double> vectorA = calculateNVector(coordA.get(0), coordA.get(1));
        List<Double> vectorB = calculateNVector(coordB.get(0), coordB.get(1));

        double arcos = acos(vectorDotProduct(vectorA, vectorB));
        double arcsin = asin(vectorMagnitude(vectorCrossProduct(vectorA, vectorB)));

        double angularDifference = arcsin / arcos;

        return angularDifference * EARTH_RADIUS;
    }

    public List<Double> calculateNVector(double latitude, double longitude) {
        List<Double> nVector = new ArrayList<>();
        nVector.add(cos(latitude)*cos(longitude));
        nVector.add(cos(latitude)*sin(longitude));
        nVector.add(sin(latitude));

        return nVector;
    }

    public double vectorDotProduct(List<Double> vectorA, List<Double> vectorB) {
        double product = 0;
        for (int i = 0; i < 3; i++) {
            product += vectorA.get(i) * vectorB.get(i);
        }
        return product;
    }

    public List<Double> vectorCrossProduct(List<Double> vectorA, List<Double> vectorB) {
        List<Double> product = new ArrayList<>();

        product.add(vectorA.get(1) * vectorB.get(2) - vectorA.get(2) * vectorB.get(1));
        product.add(vectorA.get(0) * vectorB.get(2) - vectorA.get(2) * vectorB.get(0));
        product.add(vectorA.get(0) * vectorB.get(1) - vectorA.get(1) * vectorB.get(0));

        return product;
    }

    public double vectorMagnitude(List<Double> vector) {
        double power1 = pow(vector.get(0), 2);
        double power2 = pow(vector.get(1), 2);
        double power3 = pow(vector.get(2), 2);

        return sqrt(power1 + power2 + power3);
    }
}
