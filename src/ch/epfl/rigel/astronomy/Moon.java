package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.ClosedInterval;

import java.util.Locale;

/**
 * @author Souleyman Boudouh (302207)
 */
public final class Moon extends CelestialObject {

    private static final double AGE = 4.53e9;
    private static final double DIST = 1.2e-11;
    private static final double P0 = 2_360_448  ;
    private static final double P1 = 0;

    private final float phase;
    private static final ClosedInterval phaseInterval = ClosedInterval.of(0, 1);

    /**
     * Construct a moon with the following parameters
     *
     * @param equatorialPos equatorial coordinates of the moon
     * @param angularSize   angular size
     * @param magnitude     magnitude
     * @param phase         phase
     */
    public Moon(EquatorialCoordinates equatorialPos, float angularSize, float magnitude, float phase) {
        super("Lune", equatorialPos, angularSize, magnitude, AGE,DIST,P0,P1);
        this.phase = (float) Preconditions.checkInInterval(phaseInterval, phase);
    }

    /**
     * @return a visual representation of the moon's phase
     */
    @Override
    public String info() {
        return String.format(Locale.ROOT, "%s (%.1f%s)", name(), phase * 100, "%");
    }


}
