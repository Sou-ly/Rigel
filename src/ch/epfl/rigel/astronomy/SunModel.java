package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.Angle;

/**
 * @author Francois Dumoncel (314420)
 */
public enum SunModel implements CelestialObjectModel<Sun> {
    SUN();

    private static final double DAY = 365.242191;
    private static final double SUN_ECCENTRICITY = 0.016705;
    private static final double EPSILON_G = Angle.ofDeg(279.557208);
    private static final double OMEGA_G = Angle.ofDeg(283.112438);
    private static final double THETA_0 = Angle.ofDeg(0.533128);

    /**
     * Compute the ecliptic coordinates of the sun at a given moment
     *
     * @param daysSinceJ2010                 the number of days between 2010 and the given moment
     * @param eclipticToEquatorialConversion conversion given
     * @return a Sun with the computed ecliptic coordinates  of angular size theta
     */
    @Override
    public Sun at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {

        //Anomalies
        double meanAnomaly = ((Angle.TAU * daysSinceJ2010) / DAY + EPSILON_G - OMEGA_G);
        double trueAnomaly = (meanAnomaly + 2 * SUN_ECCENTRICITY * Math.sin(meanAnomaly));

        //Ecliptic coordinates
        double lambda = Angle.normalizePositive(trueAnomaly + OMEGA_G);
        double phi = 0;

        //AngularSize
        double theta = (THETA_0 * ((1 + SUN_ECCENTRICITY * Math.cos(trueAnomaly)) / (1 - (SUN_ECCENTRICITY * SUN_ECCENTRICITY))));

        return new Sun(EclipticCoordinates.of(lambda, phi), eclipticToEquatorialConversion.apply(EclipticCoordinates.of(lambda, phi)), (float) theta, (float) meanAnomaly);
    }
}