package ch.epfl.rigel.coordinates;

import java.util.Locale;

/**
 * @author Francois Dumoncel (314420)
 */
public final class CartesianCoordinates {

    private final double x;
    private final double y;

    /**
     * Construct a couple of cartesian coordinates
     *
     * @param x abscissa
     * @param y orderly
     */
    private CartesianCoordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Construction method of a cartesian coordinates
     *
     * @param x abscissa
     * @param y orderly
     * @return cartesian coordinates
     */
    public static CartesianCoordinates of(double x, double y) {
        return new CartesianCoordinates(x, y);
    }

    /**
     * @return abscissa
     */
    public double x() {
        return x;
    }

    /**
     * @return orderly
     */
    public double y() {
        return y;
    }

    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final String toString() {
        return String.format(Locale.ROOT, "(x=%.4f, y=%.4f)", x(), y());
    }

}