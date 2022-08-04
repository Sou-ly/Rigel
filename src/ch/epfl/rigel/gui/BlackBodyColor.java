package ch.epfl.rigel.gui;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.ClosedInterval;
import javafx.scene.paint.Color;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class BlackBodyColor {


    private static final HashMap<Integer, String> colorTemperature = new HashMap<>();
    private static final BlackBodyColor BLACK_BODY_COLOR = new BlackBodyColor();

    private BlackBodyColor() {
        InputStream colors = BlackBodyColor.class.getResourceAsStream("/bbr_color.txt");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(colors, StandardCharsets.US_ASCII))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!(line.startsWith("#") || line.contains("2deg"))) {
                    String[] data = line.split("\\s+");
                    colorTemperature.put(Integer.parseInt(data[1]), data[data.length - 1]);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Color colorForTemperature(double temperature) throws UncheckedIOException, IllegalArgumentException {
        Preconditions.checkInInterval(ClosedInterval.of(1000, 40000), temperature);
        double i = (int) Math.round(temperature/100)*100;
        return Color.web(colorTemperature.get((int) i));
    }
}

