package odms.commons.model._enum;

/**
 *
 */
public enum Regions {
    NORTHLAND("Northland"),
    AUCKLAND("Auckland"),
    WAIKATO("Waikato"),
    BAYOFPLENTY("Bay of Plenty"),
    GISBORNE("Gisborne"),
    HAWKESBAY("Hawkes Bay"),
    TARANAKI("Taranaki"),
    MANAWATUWANGANUI("Manawatu-Wanganui"),
    WELLINGTON("Wellington"),
    TASMAN("Tasman"),
    NELSON("Nelson"),
    MARLBOROUGH("Marlborough"),
    WESTCOAST("West Coast"),
    CANTERBURY("Canterbury"),
    OTAGO("Otago"),
    SOUTHLAND("Southland"),
    CHATHAMISLANDS("Chatham Islands");


    private final String region;

    Regions(String s) {
        region = s;
    }

    public boolean equalsRegion(String otherRegion) {
        return region.equals(otherRegion);
    }

    @Override
    public String toString() {
        return this.region;
    }


}
