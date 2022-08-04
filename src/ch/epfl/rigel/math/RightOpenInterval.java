package ch.epfl.rigel.math;

import ch.epfl.rigel.Preconditions;

import java.util.Locale;

/**
 * @author Francois Dumoncel (314420)
 * @author Souleyman Boudouh (302207)
 */
public class RightOpenInterval extends Interval {

    private RightOpenInterval(double a, double b) {
        super(a, b);
    }

    /**
     * Construction method for a RightOpenInteval
     *
     * @param low  lower bound of the RightOpenInterval
     * @param high upper bound of the RightOpenInterval
     * @return a RightOpenInterval of shape [low high[
     */
    public static RightOpenInterval of(double low, double high) {
        Preconditions.checkArgument(low < high);
        return new RightOpenInterval(low, high);
    }

    /**
     * Construction method for a RightOpenInterval of size 'size' center on 0
     *
     * @param size size of the wanted interval
     * @return a symmetric interval center on 0
     */
    public static RightOpenInterval symmetric(double size) {
        Preconditions.checkArgument(size > 0);
        double halfSize = size / 2.0;
        return new RightOpenInterval(- halfSize, halfSize);
    }

    /**
     * Check if the value 'v' is contained in the interval
     *
     * @param v value to check
     * @return true if 'v' is in the interval, false otherwise
     */
    @Override
    public boolean contains(double v) {
        return v >= low() && v < high();
    }

    /**
     * Reduces the 'v' value to the interval
     *
     * @param v value to reduce
     * @return the reduced value of v
     */
    public double reduce(double v) {
        return v - size() * Math.floor((v - low()) / size());
    }

    /**
     * @return a visual representation of a closed interval of shape [low, up[
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "[ %.2f ; %.2f [", low(), high());
    }
}
