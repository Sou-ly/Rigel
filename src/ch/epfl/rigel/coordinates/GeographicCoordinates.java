package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

import java.util.Locale;

/**
 * @author Souleyman Boudouh (302207)
 */
public final class GeographicCoordinates extends SphericalCoordinates {

    private static final RightOpenInterval lonInterval = RightOpenInterval.symmetric(360);
    private static final ClosedInterval latInterval = ClosedInterval.symmetric(180);

    private GeographicCoordinates(double lonDeg, double latDeg) {
        super(Angle.ofDeg(lonDeg), Angle.ofDeg(latDeg));
    }

    /**
     * Creates GeographicCoordinates only if lonDeg and latDeg are valid parameters
     *
     * @param lonDeg longitude in degree
     * @param latDeg latitude in degree
     * @throws IllegalArgumentException if one of the parameters is invalid
     */
    public static GeographicCoordinates ofDeg(double lonDeg, double latDeg) {
        return new GeographicCoordinates(Preconditions.checkInInterval(lonInterval, lonDeg),
                Preconditions.checkInInterval(latInterval, latDeg));
    }

    /**
     * @param lonDeg longitude in degree
     * @return true if contained in [-180, 180[ interval
     */
    public static boolean isValidLonDeg(double lonDeg) {
        return lonInterval.contains(lonDeg);
    }

    /**
     * @param latDeg latitude in degree
     * @return true if contained in [-90, 90] interval
     */
    public static boolean isValidLatDeg(double latDeg) {
        return latInterval.contains(latDeg);
    }

    /**
     * @return longitude in radian
     */
    @Override
    public double lon() {
        return super.lon();
    }

    /**
     * @return longitude in degree
     */
    @Override
    public double lonDeg() {
        return super.lonDeg();
    }

    /**
     * @return latitude in radian
     */
    @Override
    public double lat() {
        return super.lat();
    }

    /**
     * @return latitude in degree
     */
    @Override
    public double latDeg() {
        return super.latDeg();
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(lon=%.4f°, lat=%.4f°)", lonDeg(), latDeg());
    }
}
