import ch.epfl.rigel.astronomy.*;
import ch.epfl.rigel.coordinates.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.time.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ObservedSkyTest {


    @Test
    void testObservedSkyTheory() throws IOException {

        ObservedSky sky;
        StereographicProjection stereo;
        GeographicCoordinates geoCoords;
        ZonedDateTime time;
        EquatorialToHorizontalConversion convEquToHor;

        StarCatalogue catalogue;
        String HYG_CATALOGUE_NAME = "/hygdata_v3.csv";
        try (InputStream hygStream = getClass().getResourceAsStream(HYG_CATALOGUE_NAME)) {
            String AST_CATALOGUE_NAME = "/asterisms.txt";
            InputStream asterismStream = getClass().getResourceAsStream(AST_CATALOGUE_NAME);
            String PUL_CAT_NAME = "/pulscat.csv";
            InputStream pulsarStream = getClass().getResourceAsStream(PUL_CAT_NAME);
            catalogue = new StarCatalogue.Builder()
                    .loadFrom(hygStream, HygDatabaseLoader.INSTANCE)
                    .loadFrom(asterismStream, AsterismLoader.INSTANCE)
                    .loadFrom(pulsarStream, PulsarDatabaseLoader.INSTANCE).build();

        }

        time = ZonedDateTime.of(LocalDate.of(2020, Month.APRIL, 4), LocalTime.of(0, 0), ZoneOffset.UTC);

        geoCoords = GeographicCoordinates.ofDeg(30, 45);
        stereo = new StereographicProjection(HorizontalCoordinates.ofDeg(20, 22));

        convEquToHor = new EquatorialToHorizontalConversion(time, geoCoords);

        sky = new ObservedSky(time, geoCoords, stereo, catalogue);

        assertEquals(Optional.of("Tau Phe").get(), sky.objectClosestTo(stereo.apply(new EquatorialToHorizontalConversion(time,geoCoords)
                        .apply(EquatorialCoordinates.of(0.004696959812148989,-0.8618930353430763))),0.1).get().name());

        assertEquals(Optional.of("Kap Cas").get(), sky.objectClosestTo(stereo.apply(HorizontalCoordinates.ofDeg(16,23)), 0.07).get().name());
        assertEquals(Optional.of("Lune").get(), sky.objectClosestTo(stereo.apply(stereo.inverseApply((CartesianCoordinates.of(-0.9802, 0.3940)))), 0.1).get().name());
        assertEquals(Optional.empty(), sky.objectClosestTo(stereo.apply(HorizontalCoordinates.ofDeg(180,23)), 0.01));

//
//        for (Star star : sky.stars()) {
//            assertEquals(stereo.apply(convEquToHor.apply(star.equatorialPos())).x(), sky.starPosition().get(star).x());
//            assertEquals(stereo.apply(convEquToHor.apply(star.equatorialPos())).y(), sky.starPosition().get(star).y());
//        }
//        assertEquals(catalogue.stars().size(), sky.stars().size());
//
//        for (Planet planet : sky.planets()) {
//            assertEquals(stereo.apply(convEquToHor.apply(planet.equatorialPos())).x(), sky.planetPosition().get(planet).x());
//            assertEquals(stereo.apply(convEquToHor.apply(planet.equatorialPos())).y(), sky.planetPosition().get(planet).y());
//        }
//
//        assertEquals(7, sky.planets().size());
//
//        assertEquals(catalogue.asterisms(), sky.usedAsterism());
//        assertEquals(catalogue.asterisms().size(), sky.usedAsterism().size());
//
//        for (Asterism ast : catalogue.asterisms()) {
//            assertEquals(catalogue.asterismIndices(ast), sky.usedIndex(ast));
//            assertEquals(catalogue.asterismIndices(ast).size(), sky.usedIndex(ast).size());
//        }
    }
}



