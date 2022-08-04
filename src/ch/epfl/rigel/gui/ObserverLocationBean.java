package ch.epfl.rigel.gui;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class ObserverLocationBean {

    private DoubleProperty lonDeg = new SimpleDoubleProperty();
    private DoubleProperty latDeg = new SimpleDoubleProperty();
    private Binding<GeographicCoordinates> coordinates = Bindings.createObjectBinding(
            () -> GeographicCoordinates.ofDeg(lonDeg.get(), latDeg.get()), lonDeg, latDeg
    );

    public ObserverLocationBean() {
    }

    public double getLonDeg() {
        return lonDeg.get();
    }

    public DoubleProperty lonDegProperty() {
        return lonDeg;
    }

    public void setLonDeg(double lonDeg) {
        this.lonDeg.set(lonDeg);
    }

    public double getLatDeg() {
        return latDeg.get();
    }

    public DoubleProperty latDegProperty() {
        return latDeg;
    }

    public void setLatDeg(double latDeg) {
        this.latDeg.set(latDeg);
    }

    public GeographicCoordinates getCoordinates() {
        return coordinates.getValue();
    }

    public Binding<GeographicCoordinates> coordinatesProperty() {
        return coordinates;
    }

    public void setCoordinates(GeographicCoordinates coords) {
        setLonDeg(coords.lonDeg());
        setLatDeg(coords.latDeg());
    }

}
