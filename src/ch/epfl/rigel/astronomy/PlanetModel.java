package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.Angle;

import java.util.Arrays;
import java.util.List;

import static java.lang.Math.cos;
import static java.lang.Math.sin;


/**
 * @author Souleyman Boudouh (302207)
 */
public enum PlanetModel implements CelestialObjectModel<Planet> {
    /**
     * Some given constants for planets
     */
    MERCURY("Mercure", 0.24085, 75.5671, 77.612, 0.205627,
            0.387098, 7.0051, 48.449, 6.74, -0.42),
    VENUS("Vénus", 0.615207, 272.30044, 131.54, 0.006812,
            0.723329, 3.3947, 76.769, 16.92, -4.40),
    EARTH("Terre", 0.999996, 99.556772, 103.2055, 0.016671,
            0.999985, 0, 0, 0, 0),
    MARS("Mars", 1.880765, 109.09646, 336.217, 0.093348,
            1.523689, 1.8497, 49.632, 9.36, -1.52),
    JUPITER("Jupiter", 11.857911, 337.917132, 14.6633, 0.048907,
            5.20278, 1.3035, 100.595, 196.74, -9.40),
    SATURN("Saturne", 29.310579, 172.398316, 89.567, 0.053853,
            9.51134, 2.4873, 113.752, 165.60, -8.88),
    URANUS("Uranus", 84.039492, 356.135400, 172.884833, 0.046321,
            19.21814, 0.773059, 73.926961, 65.80, -7.19),
    NEPTUNE("Neptune", 165.84539, 326.895127, 23.07, 0.010483,
            30.1985, 1.7673, 131.879, 62.20, -6.87);

    private final String name;
    private final double TP;
    private final double EPSILON;
    private final double S_OMEGA;
    private final double e;
    private final double a;
    private final double i;
    private final double cosI;
    private final double sinI;
    private final double B_OMEGA;
    private final double THETA_0;
    private final double V_0;
    private static final double DAY = 365.242191;
    public final static List<PlanetModel> ALL = Arrays.asList(PlanetModel.values());

    /**
     * Construct a planet with the following parameters
     *
     * @param name              the name of the planet (in french)
     * @param tropicalYear      the tropicalYear of the planet
     * @param J2010LonDeg       j2010 longitude in degree
     * @param perigeeLonDeg     longitude of the perigee in degree
     * @param eccentricity      the orbital eccentricity
     * @param semiMajorAxis     semi major axis
     * @param tilt              the inclination
     * @param orbitalNodeLonDeg orbital node longitude in degree
     * @param angularSize       the angular size of the planet
     * @param magnitude         the magnitude of the planet
     */
    PlanetModel(String name, double tropicalYear, double J2010LonDeg, double perigeeLonDeg, double eccentricity,
                double semiMajorAxis, double tilt, double orbitalNodeLonDeg, double angularSize, double magnitude) {
        this.name = name;
        TP = tropicalYear;
        EPSILON = Angle.ofDeg(J2010LonDeg);
        S_OMEGA = Angle.ofDeg(perigeeLonDeg);
        e = eccentricity;
        a = semiMajorAxis;
        i = Angle.ofDeg(tilt);
        cosI = cos(i);
        sinI = sin(i);
        B_OMEGA = Angle.ofDeg(orbitalNodeLonDeg);
        THETA_0 = Angle.ofArcsec(angularSize);
        V_0 = magnitude;
    }


    /**
     * Compute the position of a planet at a given moment
     *
     * @param daysSinceJ2010                 the number of days between the given moment and 2010
     * @param eclipticToEquatorialConversion the conversion to get equatorial position
     * @return a new planet with computed equatorial coordinates
     */
    @Override
    public Planet at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {
        if (this == EARTH) {
            return new Planet(name, EquatorialCoordinates.of(0, 0), 0, 0,0,0,0,0);
        } else {
            double M = (Angle.TAU / DAY) * (daysSinceJ2010 / TP) + EPSILON - S_OMEGA;
            double v = M + 2 * e * sin(M);

            double r = a * (1 - e * e) / (1 + e * cos(v));
            double l = v + S_OMEGA;
            double p = sin(l-B_OMEGA);
            double o = cos(l-B_OMEGA);
            double phi = Math.asin(p * sinI);
            double rp = r * cos(phi);
            double lp = Math.atan2(p * cosI, o) + B_OMEGA;

            double MEarth = ((Angle.TAU / DAY) * (daysSinceJ2010 / EARTH.TP)) + EARTH.EPSILON - EARTH.S_OMEGA;
            double vEarth = MEarth + 2 * EARTH.e * sin(MEarth);
            double R = EARTH.a * (1 - EARTH.e * EARTH.e) / (1 + EARTH.e * cos(vEarth));
            double L = vEarth + EARTH.S_OMEGA;

            double RHO = Math.sqrt((R * R + r * r) - (2 * R * r * cos(l - L) * cos(phi)));
            double lambda = 0;

            double omega = R * sin(lp - L);
            switch (this) {
                case MERCURY:
                case VENUS:
                    lambda = Angle.normalizePositive(Math.PI + L + Math.atan(rp * sin(L - lp) / (R - rp * cos(L - lp))));
                    break;

                case MARS:
                case JUPITER:
                case SATURN:
                case URANUS:
                case NEPTUNE:
                    lambda = Angle.normalizePositive(lp + Math.atan2(omega, (rp - R * cos(lp - L))));
                    break;
            }

            double beta = Math.atan(rp * Math.tan(phi) * sin(lambda - lp) / (omega));

            double F1 = (1 + cos(lambda - l)) / 2;
            double m1 = V_0 + 5 * Math.log10(r * RHO / Math.sqrt(F1));

            return new Planet(name, eclipticToEquatorialConversion.apply(EclipticCoordinates.of(lambda, beta)), (float) (THETA_0 / RHO), (float) m1,0,0,0,0);
        }
    }
}