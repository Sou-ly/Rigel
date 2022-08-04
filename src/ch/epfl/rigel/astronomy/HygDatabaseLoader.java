package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.Angle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @author Souleyman Boudouh (302207)
 */
public enum HygDatabaseLoader implements StarCatalogue.Loader {
    INSTANCE;

    private static final int HIP = 1;
    private static final int PROPER = 6;
    private static final int MAG = 13;
    private static final int CI = 16;
    private static final int RARAD = 23;
    private static final int DECRAD = 24;
    private static final int BAYER = 27;
    private static final int CON = 29;

    /**
     * This method adds to the catalog builder all the stars obtained from the HYG catalog using the content of the columns as explained in the instruction.
     *
     * @param inputStream the stream of data
     * @param builder     the catalogue in construction
     * @throws IOException in case of errors
     */
    @Override
    public void load(InputStream inputStream, StarCatalogue.Builder builder) throws IOException {

        try (BufferedReader s = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.US_ASCII))) {
            String line;
            line = s.readLine(); // first line doesnt contain any info

            while ((line = s.readLine()) != null) {
                String[] data = line.split(",");

                int hipID = !(data[HIP].equals("")) ? Integer.parseInt(data[HIP]) : 0;
                String name = !(data[PROPER].equals("")) ? data[PROPER] : (!(data[BAYER].equals("")) ? data[BAYER] : "?") + " " + data[CON];
                double rarad = Double.parseDouble(data[RARAD]);
                double decrad = Double.parseDouble(data[DECRAD]);
                double mag = !(data[MAG].equals("")) ? Double.parseDouble(data[MAG]) : 0;
                double colorIndex = !(data[CI].equals("")) ? Double.parseDouble(data[CI]) : 0;

                builder.addStar(new Star(hipID, name, EquatorialCoordinates.of(Angle.normalizePositive(rarad), decrad), (float) mag, (float) colorIndex));
            }

        }
    }
}