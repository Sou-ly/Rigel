package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * @author Francois Dumoncel (314420)
 * @author Souleyman Boudouh (302207)
 */
public final class SiderealTime {

    private static final double constantForLittleT = 1.002737909;
    private static final Polynomial polynomialForS0 = Polynomial.of(0.000025862, 2400.051336, 6.697374558);

    private SiderealTime() {}

    /**
     * Compute the sidereal time at Greenwich on a given moment
     *
     * @param when the given moment
     * @return sidereal time at time when, on greenwich's meridian
     */
    public static double greenwich(ZonedDateTime when) {

        ZonedDateTime newWhen = when.withZoneSameInstant(ZoneOffset.UTC);
        double T = Epoch.J2000.julianCenturiesUntil(newWhen.truncatedTo(ChronoUnit.DAYS));
        double t = newWhen.truncatedTo(ChronoUnit.DAYS).until(newWhen, ChronoUnit.MILLIS) / 3.6e6;

        double S_0 = polynomialForS0.at(T);
        double S_g = S_0 + (constantForLittleT * t);

        return Angle.normalizePositive(Angle.ofHr(S_g));
    }

    /**
     * Compute the local sidereal time at where et when
     *
     * @param when  the given moment
     * @param where the given place
     * @return sidereal time at time when and at place where, in [0, 360[
     */
    public static double local(ZonedDateTime when, GeographicCoordinates where) {
        return Angle.normalizePositive(greenwich(when) + where.lon());
    }
}