package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.Angle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

// ------------------------- BONUS ------------------------- \\

public enum PulsarDatabaseLoader implements StarCatalogue.Loader {
    INSTANCE;

    private static final int NAME = 0;
    private static final int RA2000 = 1;
    private static final int DE2000 = 2;
    private static final int P0 = 3;
    private static final int P1 = 4;
    private static final int DIST = 6;

    @Override
    public void load(InputStream is, StarCatalogue.Builder b) throws IOException {

        try (BufferedReader r = new BufferedReader(new InputStreamReader(is, StandardCharsets.US_ASCII))) {
            String line;
            line = r.readLine(); //Skip the 1st line

            while ((line = r.readLine()) != null) {
                String[] data = line.split(";");

                String name = data[NAME];
                double radeg = Double.parseDouble(data[RA2000]);
                double decDeg = Double.parseDouble(data[DE2000]);
                double P_0 = !(data[P0].equals("")) ? Double.parseDouble(data[P0]) : 0;
                double P_1 = !(data[P1].equals("")) ? Double.parseDouble(data[P1]) : 0;
                double dist = !(data[DIST].equals("")) ? Double.parseDouble(data[DIST]) : 0;

                double age = 0.5 * (P_0 / P_1);
                float magnitude = 15 + (float) Math.random() * (27 - 15);

                b.addPulsar(new Pulsar(name, EquatorialCoordinates.of(Angle.normalizePositive(Angle.ofDeg(radeg)), Angle.ofDeg(decDeg)), P_0, P_1, dist, age, magnitude));

            }
        }
    }
}
