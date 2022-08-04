package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.astronomy.Epoch;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;

import java.time.ZonedDateTime;
import java.util.function.Function;

/**
 * @author Souleyman Boudouh (302207)
 */
public final class EclipticToEquatorialConversion implements Function<EclipticCoordinates, EquatorialCoordinates> {


    /**
     * private and final attributes to avoid redundant calculation
     */
    private static final Polynomial epsilonPoly = Polynomial.of(Angle.ofArcsec(0.00181), Angle.ofArcsec(-0.0006), Angle.ofArcsec(-46.815), Angle.ofDMS(23, 26, 21.45));
    private final double cosEpsilon;
    private final double sinEpsilon;

    public EclipticToEquatorialConversion(ZonedDateTime when) {
        double epsilon = epsilonPoly.at(Epoch.J2000.julianCenturiesUntil(when));
        cosEpsilon = Math.cos(epsilon);
        sinEpsilon = Math.sin(epsilon);
    }


    /**
     * Converts ecliptic coordinates into equatorial coordinates
     *
     * @param ecl the ecliptic coordinates
     * @return the converted equatorial coordinates
     */
    @Override
    public EquatorialCoordinates apply(EclipticCoordinates ecl) {
        double lambda = ecl.lon();
        double beta = ecl.lat();

        double alpha = Math.atan2(Math.sin(lambda) * cosEpsilon - Math.tan(beta) * sinEpsilon, Math.cos(lambda));
        double delta = Math.asin(Math.sin(beta) * cosEpsilon + Math.cos(beta) * sinEpsilon * Math.sin(lambda));

        return EquatorialCoordinates.of(Angle.normalizePositive(alpha), delta);
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }
}
