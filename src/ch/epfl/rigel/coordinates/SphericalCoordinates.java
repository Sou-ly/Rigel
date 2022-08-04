package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;

/**
 * @author Francois Dumoncel (314420)
 * @author Souleyman Boudouh (302207)
 */
abstract class SphericalCoordinates {

    private final double lon;
    private final double lat;

    /**
     * @param lon longitude
     * @param lat latitude
     */
    SphericalCoordinates(double lon, double lat) {
        this.lon = lon;
        this.lat = lat;
    }

    /**
     * @return the longitude
     */
    public double lon() {
        return lon;
    }

    /**
     * @return the latitude
     */
    public double lat() {
        return lat;
    }

    /**
     * @return the longitude in degrees
     */
    double lonDeg() {
        return Angle.toDeg(lon);
    }

    /**
     * @return the latitude in degrees
     */
    double latDeg() {
        return Angle.toDeg(lat);
    }

    @Override
    public final boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }

}