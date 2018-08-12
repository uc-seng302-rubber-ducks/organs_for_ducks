package odms.commons.model._enum;

/**
 * See design decisions for reasons of coordinate choices
 */
public enum Regions {
    NORTHLAND("Northland", -35.7251, 174.3237),
    AUCKLAND("Auckland", -36.8485, 174.7633),
    WAIKATO("Waikato", -37.7870, 175.2793),
    BAYOFPLENTY("Bay of Plenty", -37.6878, 176.1651),
    GISBORNE("Gisborne", -38.6623, 178.0176),
    HAWKESBAY("Hawkes Bay", -39.4928, 176.9120),
    TARANAKI("Taranaki", -39.0556, 174.0752),
    MANAWATUWANGANUI("Manawatu-Wanganui", 40.3523, 175.6082),
    WELLINGTON("Wellington", -41.2865, 174.7762),
    TASMAN("Tasman", -41.2706, 173.2840),
    NELSON("Nelson", -41.2706, 173.2840),
    MARLBOROUGH("Marlborough", -41.5134, 173.9612),
    WESTCOAST("West Coast", -42.4504, 171.2108),
    CANTERBURY("Canterbury", -43.5321, 172.6362),
    OTAGO("Otago", -45.8788, 170.5028),
    SOUTHLAND("Southland", -46.4132, 168.3538),
    CHATHAMISLANDS("Chatham Islands", -44.0237, 175.9305);


    private final String region;
    private final double sCoord;
    private final double eCoord;

    Regions(String name, double sCoord, double eCoord) {
        region = name;
        this.sCoord = sCoord;
        this.eCoord = eCoord;
    }

    public boolean equalsRegion(String otherRegion) {
        return region.equals(otherRegion);
    }

    public double getSCoord() {
        return sCoord;
    }

    public double getECoord() {
        return eCoord;
    }

    @Override
    public String toString() {
        return this.region;
    }


}
