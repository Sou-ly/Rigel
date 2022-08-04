package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

/**
 * @author Souleyman Boudouh (302207)
 */
public final class Planet extends CelestialObject {

    /**
     * Construct a planet with the following parameters
     *
     * @param name          the name of the planet
     * @param equatorialPos the position of the planet
     * @param angularSize   the angular size of the planet
     * @param magnitude     the magnitude size of the planet
     */
    public Planet(String name, EquatorialCoordinates equatorialPos, float angularSize, float magnitude, double age, double dist, double P0, double P1) {
        super(name, equatorialPos, angularSize, magnitude, age, dist, P0, P1);
    }
}
