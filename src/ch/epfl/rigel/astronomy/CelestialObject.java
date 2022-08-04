package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;

import java.util.Objects;

/**
 * @author Souleyman Boudouh (302207)
 */
public abstract class CelestialObject {

    private final String name;
    private final EquatorialCoordinates equatorialPos;
    private final float angularSize;
    private final float magnitude;
    private final double age;
    private final double dist;
    private final double P0;
    private final double P1;

    /**
     * Construct a celestial object with the following parameters
     *
     * @param name          the name of the object
     * @param equatorialPos the position of the object
     * @param angularSize   the angular size of the object
     * @param magnitude     the magnitude of the object
     */
    CelestialObject(String name, EquatorialCoordinates equatorialPos, float angularSize, float magnitude, double age, double dist, double P0, double P1) {
        Preconditions.checkArgument(!(angularSize < 0));
        this.name = Objects.requireNonNull(name);
        this.equatorialPos = Objects.requireNonNull(equatorialPos);
        this.angularSize = angularSize;
        this.magnitude = magnitude;
        this.age = age;
        this.dist = dist;
        this.P0 = P0;
        this.P1 = P1;

    }

    public double getAge() {
        return age;
    }

    public double getP0() {
        return P0;
    }

    public double getP1() {
        return P1;
    }

    public double getDistance() {
        return dist;
    }

    /**
     * @return the name of this celestial object
     */
    public String name() {
        return name;
    }

    /**
     * @return the angular size of this celestial object
     */
    public double angularSize() {
        return angularSize;
    }

    /**
     * @return the magnitude of this celestial object
     */
    public double magnitude() {
        return magnitude;
    }

    /**
     * @return the equatorial coordinates of this celestial object
     */
    public EquatorialCoordinates equatorialPos() {
        return equatorialPos;
    }


    /**
     * @return the name of this celestial object
     */
    public String info() {
        return name();
    }

    /**
     * @return a visual representation of name
     */
    @Override
    public final String toString() {
        return info();
    }
}