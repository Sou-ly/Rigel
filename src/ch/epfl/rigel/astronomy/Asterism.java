package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;

import java.util.List;

/**
 * @author Francois Dumoncel (314420)
 */
public final class Asterism {

    private final List<Star> stars;

    /**
     * Construct an asterism with a list of star
     *
     * @param stars which composes asterism
     */
    public Asterism(List<Star> stars) {
        Preconditions.checkArgument(!stars.isEmpty());
        this.stars = List.copyOf(stars);
    }

    /**
     * @return a immutable list of the stars contained in the asterism
     */
    public List<Star> stars() {
        return stars;
    }
}