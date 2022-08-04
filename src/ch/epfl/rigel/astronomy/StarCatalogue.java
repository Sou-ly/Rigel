package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author Souleyman Boudouh (302207)
 * @author Francois Dumoncel (314420)
 */
public final class StarCatalogue {

    private final List<Star> stars;
    private Map<Star, Integer> indexHashMap = new HashMap<>();
    private Map<Asterism, List<Integer>> astIntHM = new HashMap<>();

    // Bonus
    private List<Pulsar> pulsarList;

    /**
     * Construct a Star and an asterism Catalogues from given list of stars and asterism
     *
     * @param stars     given list of Stars
     * @param asterisms given list of Asterism
     * @throws IllegalArgumentException if a asterism contain a star that the list doesn't contain
     */
    public StarCatalogue(List<Star> stars, List<Asterism> asterisms, List<Pulsar> pulsars) {
        this.stars = List.copyOf(stars);

        //Bonus
        this.pulsarList = List.copyOf(pulsars);

        for (Star star : stars) {
            indexHashMap.put(star, stars.indexOf(star));
        };

        for (Asterism asterism : asterisms) {
            Preconditions.checkArgument(stars.containsAll(asterism.stars()));
            List<Integer> starId = new ArrayList<>();
            for (Star star : asterism.stars()) {
                starId.add(indexHashMap.get(star));
            }
            astIntHM.put(asterism, starId);
        }
    }

    /**
     * @return a immutable copy of the list of stars
     */
    public List<Star> stars() {
        return Collections.unmodifiableList(stars);
    }

    //Bonus
    public List<Pulsar> pulsars() {
        return Collections.unmodifiableList(pulsarList);
    }

    /**
     * @return an immutable view of the key of the hashMap
     */
    public Set<Asterism> asterisms() {
        Set<Asterism> key = astIntHM.keySet();
        return Set.copyOf(key);
    }

    /**
     * Return the list of index of stars which composed the asterism
     *
     * @param asterism asterism
     * @return a list of index of stars (index of the catalogue)
     * @throws IllegalArgumentException if the asterism isn't in the catalogue
     */
    public List<Integer> asterismIndices(Asterism asterism) {
        return Collections.unmodifiableList(astIntHM.get(asterism));
    }

    /**
     * Represents a star catalogue in construction
     */
    public static final class Builder {

        private final List<Asterism> asterisms;
        private final List<Star> stars;

        //Bonus
        private final List<Pulsar> pulsars;

        /**
         * Construct a builder which represents an instance of StarCatalogue in construction
         */
        public Builder() {
            asterisms = new ArrayList<>();
            stars = new ArrayList<>();

            //Bonus
            pulsars = new ArrayList<>();
        }

        /**
         * Add a star to the catalogue in construction
         *
         * @param star to add
         * @return the catalogue in construction  with the new star
         */
        public Builder addStar(Star star) {
            stars.add(star);
            return this;
        }

        /**
         * @return an immutable list of stars of the catalogue
         */
        public List<Star> stars() {
            return Collections.unmodifiableList(stars);
        }

        /**
         * Add an asterism to the catalogue in construction
         *
         * @param asterism to add
         * @return the catalogue in construction with the new asterism
         */
        public Builder addAsterism(Asterism asterism) {
            asterisms.add(asterism);
            return this;
        }

        /**
         * @return an immutable list of asterism of the catalogue
         */
        public List<Asterism> asterisms() {
            return Collections.unmodifiableList(asterisms);
        }

        //Bonus
        public Builder addPulsar(Pulsar pulsar) {
            pulsars.add(pulsar);
            return this;
        }

        //Bonus
        public List<Pulsar> pulsars() {
            return Collections.unmodifiableList(pulsars);
        }

        /**
         * Add the stars/asterisms obtained by loading the InputStream
         *
         * @param inputStream the stream that contains stars/asterisms
         * @param loader      the loader which add the stream to the catalogue in construction
         * @return the builder
         * @throws IOException in case of error
         */
        public Builder loadFrom(InputStream inputStream, Loader loader) throws IOException {
            loader.load(inputStream, this);
            return this;
        }

        /**
         * Construct the final instance of StarCatalogue
         *
         * @return the final catalogue
         */
        public StarCatalogue build() {
            return new StarCatalogue(stars, asterisms, pulsars);
        }
    }

    /**
     * Represents a catalog loader of stars and asterisms.
     */
    public interface Loader {
        /**
         * Loads the stars and/or asterisms of the input stream and adds them to the catalog in construction of the builder
         *
         * @param inputStream the stream of data
         * @param builder     the catalogue in construction
         * @throws IOException in case of error
         */
        void load(InputStream inputStream, Builder builder) throws IOException;
    }
}





