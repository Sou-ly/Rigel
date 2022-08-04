package ch.epfl.rigel.math;

import ch.epfl.rigel.Preconditions;

import java.util.Locale;

/**
 * @author Francois Dumoncel (314420)
 * @author Souleyman Boudouh (302207)
 */
public final class ClosedInterval extends Interval {

    private ClosedInterval(double a, double b) {
        super(a, b);
    }

    /**
     * Construction method for a ClosedInterval
     *
     * @param low  lower bound of the ClosedInterval
     * @param high upper bound of the ClosedInterval
     * @return a new ClosedInterval of shape [low, high]
     */
    public static ClosedInterval of(double low, double high) {
        Preconditions.checkArgument((low < high));
        return new ClosedInterval(low, high);
    }

    /**
     * Construction method for a ClosedInterval of size 'size' center 0
     *
     * @param size size of the wanted interval
     * @return a symmetric interval center on 0
     */
    public static ClosedInterval symmetric(double size) {
        Preconditions.checkArgument((size > 0));
        double halfSize = size / 2.0;
        return new ClosedInterval(- halfSize, halfSize);
    }

    /**
     * Check if the value 'v' is contained in the interval
     *
     * @param v value to check
     * @return true if 'v' is in the interval, false otherwise
     */
    @Override
    public boolean contains(double v) {
        return v >= low() && v <= high();
    }

    /**
     * clip 'v' at the interval
     *
     * @param v value to clip
     * @return the upper bound if v >= upper bound, the lower bound if v <= lower bound
     * and v if it is contained in the interval
     */
    public double clip(double v) {
        if (v >= high()) {
            return high();
        } else if (v <= low()) {
            return low();
        } else return v;
    }


    /**
     * @return and print an visual representation of a closed inteval of shape [low, up]
     */
    @Override
    public String toString() {
        return String.format(Locale.ROOT, "[ %.2f ; %.2f ]", low(), high());
    }
}
