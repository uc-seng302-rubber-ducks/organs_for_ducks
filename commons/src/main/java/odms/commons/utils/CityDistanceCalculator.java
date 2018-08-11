package odms.commons.utils;

import odms.commons.model._enum.Regions;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

public class CityDistanceCalculator {

    private static final double EARTH_RADIUS = 6371; //km

    public double calculateDistance(Regions regionA, Regions regionB) {
        List<Double> vectorA = calculateNVector(regionA.getECoord(), regionA.getSCoord());
        List<Double> vectorB = calculateNVector(regionB.getECoord(), regionB.getSCoord());

        double arcos = acos(vectorDotProduct(vectorA, vectorB));
        double arcsin = asin(vectorMagnitude(vectorCrossProduct(vectorA, vectorB)));

        double angularDifference = arcsin / arcos;

        return angularDifference * EARTH_RADIUS;
    }

    private List<Double> calculateNVector(double latitude, double longitude) {
        List<Double> nVector = new ArrayList<>();
        nVector.add(cos(latitude)*cos(longitude));
        nVector.add(cos(latitude)*sin(longitude));
        nVector.add(sin(latitude));

        return nVector;
    }

    private double vectorDotProduct(List<Double> vectorA, List<Double> vectorB) {
        double product = 0;
        for (int i = 0; i < 3; i++) {
            product += vectorA.get(i) * vectorB.get(i);
        }
        return product;
    }

    private List<Double> vectorCrossProduct(List<Double> vectorA, List<Double> vectorB) {
        List<Double> product = new ArrayList<>();

        product.add(vectorA.get(1) * vectorB.get(2) - vectorA.get(2) * vectorB.get(1));
        product.add(vectorA.get(0) * vectorB.get(2) - vectorA.get(2) * vectorB.get(0));
        product.add(vectorA.get(0) * vectorB.get(1) - vectorA.get(1) * vectorB.get(0));

        return product;
    }

    private double vectorMagnitude(List<Double> vector) {
        return sqrt(pow(vector.get(0), vector.get(0)) + pow(vector.get(1), vector.get(1)) + pow(vector.get(2), vector.get(2)));
    }
}
