package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

import java.util.Locale;

/**
 * @author Francois Dumoncel (314420)
 */

public final class HorizontalCoordinates extends SphericalCoordinates {

    private static final RightOpenInterval azInterval = RightOpenInterval.of(0, Angle.TAU);
    private static final ClosedInterval altInterval = ClosedInterval.symmetric(Angle.TAU / 2);

    private HorizontalCoordinates(double lat, double lon) {
        super(lat, lon);
    }

    /**
     * @param az  represents the azimuth in radian
     * @param alt represents the altitude in radian
     * @return HorizontalCoordinates using az and alt
     * @throws IllegalArgumentException if az isn't in [0,360[ or if alt isn't in [-90,90]
     */
    public static HorizontalCoordinates of(double az, double alt) {
        return new HorizontalCoordinates(Preconditions.checkInInterval(azInterval, az),
                Preconditions.checkInInterval(altInterval, alt));
    }

    /**
     * Do the same as above but using directly az and alt in degree
     */

    public static HorizontalCoordinates ofDeg(double azDeg, double altDeg) {
        return new HorizontalCoordinates(Preconditions.checkInInterval(azInterval, Angle.ofDeg(azDeg)),
                Preconditions.checkInInterval(altInterval, Angle.ofDeg(altDeg)));
    }

    /**
     * Return the good octant
     *
     * @param n "North"
     * @param e "East"
     * @param s "South"
     * @param o "West"
     * @return the good octant for an arbitrary azimuth (e.g : for 180° it return "S" for South)
     */
    public String azOctantName(String n, String e, String s, String o) {
        String octantToPrint = n;

        int octantValue = (int) Math.round((lonDeg() * 8) / 360);

        switch (octantValue) {
            case 1:
                octantToPrint = n + e;
                break;
            case 2:
                octantToPrint = e;
                break;
            case 3:
                octantToPrint = s + e;
                break;
            case 4:
                octantToPrint = s;
                break;
            case 5:
                octantToPrint = s + o;
                break;
            case 6:
                octantToPrint = o;
                break;
            case 7:
                octantToPrint = n + o;
                break;
        }

        return octantToPrint;
    }

    /**
     * @return altitude in radian
     */
    public double alt() {
        return super.lat();
    }

    /**
     * @return altitude in degree
     */
    public double altDeg() {
        return super.latDeg();
    }


    /**
     * @return azimuth in radian
     */
    public double az() {
        return super.lon();
    }

    /**
     * @return azimuth in degree
     */
    public double azDeg() {
        return super.lonDeg();
    }


    /**
     * Return the angular distance between a point B (this) to a point A (that)
     *
     * @param that is the coordinates of the point A
     * @return the angular distance between this (B) and that (A)
     */
    public double angularDistanceTo(HorizontalCoordinates that) {
        return Math.acos(
                Math.sin(alt()) * Math.sin(that.alt()) +
                        Math.cos(alt()) * Math.cos(that.alt()) * Math.cos(az() - that.az())
        );
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(az=%.4f°, alt=%.4f°)", azDeg(), altDeg());
    }

}