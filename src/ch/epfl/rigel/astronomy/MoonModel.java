package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.Angle;

/**
 * @author Francois Dumoncel (314420)
 */
public enum MoonModel implements CelestialObjectModel<Moon> {
    MOON();

    private static final double L_0 = Angle.ofDeg(91.929336);
    private static final double P_0 = Angle.ofDeg(130.143076);
    private static final double N_0 = Angle.ofDeg(291.682547);
    private static final double I = Angle.ofDeg(5.145396);

    /**
     * Compute the position of the moon at a given moment
     *
     * @param daysSinceJ2010                 the number of days between the given moment and 2010
     * @param eclipticToEquatorialConversion the position of the moon in equatorial coordinates
     * @return a new moon with equatorial coordinates obtained after conversion of his ecliptic coordinates computed
     */
    @Override
    public Moon at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {

        double l = (Angle.ofDeg(13.1763966)) * daysSinceJ2010 + L_0;
        double M_m = l - (Angle.ofDeg(0.1114041) * daysSinceJ2010) - P_0;

        Sun sun = SunModel.SUN.at(daysSinceJ2010, eclipticToEquatorialConversion);
        double meanAnomalyOfSun = sun.meanAnomaly();
        double lambdaRond = sun.eclipticPos().lon();

        double E_v = Angle.ofDeg(1.2739) * Math.sin(2 * (l - lambdaRond) - M_m);
        double A_e = Angle.ofDeg(0.1858) * Math.sin(meanAnomalyOfSun);
        double A_3 = Angle.ofDeg(0.37) * Math.sin(meanAnomalyOfSun);

        double M_mPrime = M_m + E_v - A_e - A_3;

        double E_c = Angle.ofDeg(6.2886) * Math.sin(M_mPrime);
        double A_4 = Angle.ofDeg(0.214) * Math.sin(2 * M_mPrime);

        double lPrime = l + E_v + E_c - A_e + A_4;

        double V = Angle.ofDeg(0.6583) * Math.sin(2 * (lPrime - lambdaRond));

        double lSecond = lPrime + V;


        double N = N_0 - Angle.ofDeg(0.0529539) * daysSinceJ2010;
        double NPrime = N - Angle.ofDeg(0.16) * Math.sin(meanAnomalyOfSun);


        //ecliptic coordinates of the moon
        double lambda_m = Math.atan2((Math.sin(lSecond - NPrime) * Math.cos(I)), Math.cos(lSecond - NPrime)) + NPrime;
        double beta_m = Math.asin(Math.sin(lSecond - NPrime) * Math.sin(I));

        //phase of the moon (between 0 and 1)
        double F = (1 - Math.cos(lSecond - lambdaRond)) / 2;

        //angular size of the moon
        double ecc = 0.0549;
        double rho = (1 - (ecc * ecc)) / (1 + (ecc * Math.cos(M_mPrime + E_c)));
        double theta = Angle.ofDeg(0.5181) / rho;

        return new Moon(eclipticToEquatorialConversion.apply(EclipticCoordinates.of(Angle.normalizePositive(lambda_m), beta_m)),
                (float) theta, 0, (float) F);
    }
}