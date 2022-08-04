package ch.epfl.rigel;

import ch.epfl.rigel.math.Interval;

/**
 * @author Francois Dumoncel (314420)
 * @author Souleyman Boudouh (302207)
 */
public final class Preconditions {

    /**
     * Private and empty constructor to make the class not instantiable
     */
    private Preconditions() {
    }

    /**
     * @param isTrue is the argument to be verified (true or false)
     */
    public static void checkArgument(boolean isTrue) {
        if (!isTrue) {
            throw new IllegalArgumentException("Argument isn't valid please try again");
        }
    }

    /**
     * Throw exception if value isn't contained in interval
     *
     * @param interval interval which is supposed to contain value
     * @param value    the value who need to be check
     * @return value if the value is contained in the interval
     */
    public static double checkInInterval(Interval interval, double value) {
        checkArgument(interval.contains(value));
        return value;
    }
}

