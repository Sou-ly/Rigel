package ch.epfl.rigel.astronomy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Souleyman Boudouh (302207)
 */
public enum AsterismLoader implements StarCatalogue.Loader {
    INSTANCE;

    private Map<Integer, Star> starsID = new HashMap<>();

    /**
     * This method adds to the catalog builder all the asterism obtained from the HYG catalog using the content of the columns as explained in the instruction.
     *
     * @param inputStream the stream of data
     * @param builder     the catalogue in construction
     * @throws IOException in case of errors
     */
    @Override
    public void load(InputStream inputStream, StarCatalogue.Builder builder) throws IOException {

        try (BufferedReader s = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.US_ASCII))) {
            for (Star star : builder.stars()) {
                starsID.put(star.hipparcosId(), star);
            }

            String line;

            while ((line = s.readLine()) != null) {
                String[] data = line.split(",");
                List<Star> starList = Arrays.stream(data).map(id -> starsID.get(Integer.parseInt(id))).collect(Collectors.toList());
                builder.addAsterism(new Asterism(starList));
            }
        }
    }
}
