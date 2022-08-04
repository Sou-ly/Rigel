package ch.epfl.rigel.gui;

import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.math.Angle;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.function.DoubleBinaryOperator;

public class ViewingParametersBean {

    private DoubleProperty fieldOfViewDeg = new SimpleDoubleProperty();
    private ObjectProperty<HorizontalCoordinates> center = new SimpleObjectProperty<>();

    ViewingParametersBean() {
    }
    DoubleProperty fieldProperty() {
        return fieldOfViewDeg;
    }

    double getFieldOfViewDeg() {
        return fieldOfViewDeg.get();
    }

    void setFieldOfViewDeg(double fieldOfViewDeg) {
        this.fieldOfViewDeg.set(fieldOfViewDeg);
    }

    HorizontalCoordinates getCenter() {
        return center.get();
    }

    ObjectProperty<HorizontalCoordinates> centerProperty() {
        return center;
    }

    void setCenter(HorizontalCoordinates center) {
        this.center.set(center);
    }
}
