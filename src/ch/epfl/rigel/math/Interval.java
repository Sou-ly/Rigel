package ch.epfl.rigel.math;

import static java.lang.Math.*;

/**
 * @author Francois Dumoncel (314420)
 * @author Souleyman Boudouh (302207)
 */
public abstract class Interval {

    private final double min;
    private final double max;

    /**
     * Construct an interval with the lower bound = min(a,b) and the upper bound = max(a,b)
     */
    protected Interval(double a, double b) {
        min = a;
        max = b;
    }

    /**
     * @return the lower bound of the interval
     */
    public double low() {
        return min;
    }

    /**
     * @return the upper bound of the interval
     */
    public double high() {
        return max;
    }

    /**
     * @return the size of the interval
     */
    public double size() {
        return max - min;
    }

    public abstract boolean contains(double v);

    //Never compare interval's boundaries
    @Override
    public final boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }
}
