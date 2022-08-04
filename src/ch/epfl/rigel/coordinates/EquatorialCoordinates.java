package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

import java.util.Locale;

/**
 * @author Souleyman Boudouh (302207)
 */
public final class EquatorialCoordinates extends SphericalCoordinates {

    private static final RightOpenInterval raInterval = RightOpenInterval.of(0, Angle.TAU);
    private static final ClosedInterval decInterval = ClosedInterval.symmetric(Angle.TAU / 2);

    private EquatorialCoordinates(double ra, double dec) {
        super(ra, dec);
    }

    /**
     * Creates EquatorialCoordinates only if ra and dec are valid parameters
     *
     * @param ra  right ascension in radian
     * @param dec declination in radian
     * @return EquatorialCoordinates
     * @throws IllegalArgumentException if ra or dec is invalid
     */
    public static EquatorialCoordinates of(double ra, double dec) {
        return new EquatorialCoordinates(Preconditions.checkInInterval(raInterval, ra),
                Preconditions.checkInInterval(decInterval, dec));
    }

    /**
     * @return ra in radian
     */
    public double ra() {
        return lon();
    }

    /**
     * @return ra in degree
     */
    public double raDeg() {
        return lonDeg();
    }

    /**
     * @return ra in hours
     */
    public double raHr() {
        return Angle.toHr(lon());
    }

    /**
     * @return dec in radian
     */
    public double dec() {
        return lat();
    }

    /**
     * @return dec in degree
     */
    public double decDeg() {
        return latDeg();
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(ra=%.4fh, dec=%.4f°)", raHr(), decDeg());
    }
}
