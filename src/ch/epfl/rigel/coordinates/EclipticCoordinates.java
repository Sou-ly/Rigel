package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

import java.util.Locale;

/**
 * @author Francois Dumoncel (314420)
 */
public final class EclipticCoordinates extends SphericalCoordinates {

    private static final RightOpenInterval lonInterval = RightOpenInterval.of(0, Angle.TAU);
    private static final ClosedInterval latInterval = ClosedInterval.symmetric(Angle.TAU / 2);

    private EclipticCoordinates(double lon, double lat) {
        super(lon, lat);
    }

    /**
     * @param lon (lambda) in radian
     * @param lat (beta) in radian
     */
    public static EclipticCoordinates of(double lon, double lat) {
        return new EclipticCoordinates(Preconditions.checkInInterval(lonInterval, lon),
                Preconditions.checkInInterval(latInterval, lat));
    }

    /**
     * @return lon in radian
     */
    @Override
    public double lon() {
        return super.lon();
    }

    /**
     * @return lon in degree
     */
    @Override
    public double lonDeg() {
        return super.lonDeg();
    }

    /**
     * @return lat in radian
     */
    @Override
    public double lat() {
        return super.lat();
    }

    /**
     * @return lat in degree
     */
    @Override
    public double latDeg() {
        return super.latDeg();
    }


    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(λ=%.4f°, β=%.4f°)", lonDeg(), latDeg());
    }
}
