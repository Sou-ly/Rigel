package ch.epfl.rigel.math;

import ch.epfl.rigel.Preconditions;

/**
 * @author Francois Dumoncel (314420)
 * @author Souleyman Boudouh (302207)
 */
public final class Angle {

    /**
     * Some public constant to make more simple the lecture of the class
     */
    public static final double TAU = 2 * Math.PI;
    public static final double DEG_PER_HOUR = 15.0;
    public static final double MIN_PER_DEG = 60.0;
    public static final double SEC_PER_DEG = 3600.0;
    public static final double DEG_PER_RAD = 180.0 / Math.PI;

    /**
     * private constructor to make the class not instantiable
     */
    private Angle() {}

    /**
     * @param rad the value to normalize
     * @return the angle in [0,360[ in degrees
     */
    public static double normalizePositive(double rad) {
        return RightOpenInterval.of(0, TAU).reduce(rad);
    }

    /**
     * @return sec in radians
     */
    public static double ofArcsec(double sec) {
        return ofDeg(sec / SEC_PER_DEG);
    }

    /**
     * @return dms in radians
     */
    public static double ofDMS(int deg, int min, double sec) {
        Preconditions.checkArgument(deg>=0);
        Preconditions.checkArgument(RightOpenInterval.of(0, 60).contains(min) && RightOpenInterval.of(0, 60).contains(sec));
        return ofDeg(deg + (min / MIN_PER_DEG) + (sec / SEC_PER_DEG));
    }

    /**
     * @return deg in radian
     */
    public static double ofDeg(double deg) {
        return deg / DEG_PER_RAD;
    }

    /**
     * @return radian in degree
     */
    public static double toDeg(double rad) {
        return rad * DEG_PER_RAD;
    }

    /**
     * @return hour in radian
     */
    public static double ofHr(double hr) {
        return ofDeg(hr * DEG_PER_HOUR);
    }

    /**
     * @return radian in degree
     */
    public static double toHr(double rad) {
        return toDeg(rad) / DEG_PER_HOUR;
    }

}
