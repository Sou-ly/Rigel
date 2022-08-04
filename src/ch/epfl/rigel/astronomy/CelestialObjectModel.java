package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;

/**
 * @param <O> depend of the celestial object (Planet, Sun, Moon ...)
 * @author Souleyman Boudouh (302207)
 */
@FunctionalInterface
public interface CelestialObjectModel<O> {
    /**
     * Redefined method in class Model
     */
    O at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion);

}