package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;

import java.util.Objects;

/**
 * @author Souleyman Boudouh (302207)
 */
public final class Sun extends CelestialObject {

    private final float meanAnomaly;
    private final EclipticCoordinates eclipticPos;

    /**
     * Construct a sun with the following parameters
     *
     * @param eclipticPos   ecliptic coordinates
     * @param equatorialPos equatorial coordinates
     * @param angularSize   angular size
     * @param meanAnomaly   mean Anomaly
     */
    public Sun(EclipticCoordinates eclipticPos, EquatorialCoordinates equatorialPos, float angularSize, float meanAnomaly) {
        super("Soleil", equatorialPos, angularSize, -26.7f,4.6e9,0.0087,2.333e6, 0);
        this.meanAnomaly = meanAnomaly;
        this.eclipticPos = Objects.requireNonNull(eclipticPos);
    }

    /**
     * @return meanAnomaly of this sun
     */
    public double meanAnomaly() {
        return meanAnomaly;
    }

    /**
     * @return ecliptic coordinates of this sun
     */
    public EclipticCoordinates eclipticPos() {
        return eclipticPos;
    }
}
