package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

// ------------------------- BONUS ------------------------- \\

public class Pulsar extends CelestialObject {

    public Pulsar(String name, EquatorialCoordinates equatorialPos, double p0, double p1, double age, double dist, float magnitude) {
        super(name, equatorialPos,0,magnitude, age, dist, p0, p1);
    }

}
