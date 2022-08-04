package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.ClosedInterval;

/**
 * @author Francois Dumoncel (314420)
 */
public final class Star extends CelestialObject {

    private final int hipparcosId;
    private static final ClosedInterval ciInterval = ClosedInterval.of(-0.5f, 5.5f);
    private final float colorIndex;

    /**
     * Construct a star with the following parameters
     *
     * @param hipparcosId   the ID of the stars (>= 0)
     * @param name          the name of the star
     * @param equatorialPos the position of the star
     * @param magnitude     the magnitude of the star
     * @param colorIndex    the colorIndex of the star
     * @throws IllegalArgumentException if the colorIndex isn't in [-0.5 , 5.5] of if the hipparcosId is negative
     */
    public Star(int hipparcosId, String name, EquatorialCoordinates equatorialPos, float magnitude, float colorIndex) {
        super(name, equatorialPos, 0, magnitude, 0, 0,0,0);
        Preconditions.checkArgument(!(hipparcosId < 0));
        this.hipparcosId = hipparcosId;
        this.colorIndex = (float) Preconditions.checkInInterval(ciInterval, colorIndex);
    }

    /**
     * @return hipparcosId
     */
    public int hipparcosId() {
        return hipparcosId;
    }

    /**
     * Compute the color temperature of this star from his colorIndex
     *
     * @return the colorTemperature in Kelvin degrees
     */
    public int colorTemperature() {
        return (int) Math.floor(4600 * ((1 / (0.92 * colorIndex + 1.7)) + (1 / (0.92 * colorIndex + 0.62))));
    }

}