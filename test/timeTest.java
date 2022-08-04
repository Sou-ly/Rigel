//import ch.epfl.rigel.astronomy.AsterismLoader;
//import ch.epfl.rigel.astronomy.HygDatabaseLoader;
//import ch.epfl.rigel.astronomy.ObservedSky;
//import ch.epfl.rigel.astronomy.StarCatalogue;
//import ch.epfl.rigel.coordinates.*;
//import org.junit.jupiter.api.Test;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.time.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class timeTest {
//
//    @Test
//    void timeTestoklm() throws IOException {
//        String HYG_CATALOGUE_NAME = "/hygdata_v3.csv";
//        String AST_CATALOGUE_NAME = "/asterisms.txt";
//        StarCatalogue catalogue;
//        ObservedSky sky;
//        StereographicProjection stereo;
//        GeographicCoordinates geoCoords;
//        ZonedDateTime time;
//
//        try (
//                InputStream hygStream = timeTest.class
//                        .getResourceAsStream(HYG_CATALOGUE_NAME)) {
//            InputStream asterismStream = timeTest.class
//                    .getResourceAsStream(AST_CATALOGUE_NAME);
//
//            catalogue = new StarCatalogue.Builder().loadFrom(hygStream, HygDatabaseLoader.INSTANCE).loadFrom(asterismStream, AsterismLoader.INSTANCE).build();
//
//        }
//
//        time = ZonedDateTime.of(LocalDate.of(2020, Month.APRIL, 4), LocalTime.of(0, 0), ZoneOffset.UTC);
//
//        geoCoords = GeographicCoordinates.ofDeg(30, 45);
//        stereo = new StereographicProjection(HorizontalCoordinates.ofDeg(20, 22));
//
//
//        sky = new ObservedSky(time, geoCoords, stereo, catalogue);
//
//        long start = System.currentTimeMillis();
//
//        sky.objectClosestTo(stereo.apply(new EquatorialToHorizontalConversion(time,geoCoords).apply(EquatorialCoordinates.of(0.004696959812148989,-0.8618930353430763))),0.1).get().name();
//
//        long runtime = System.currentTimeMillis() - start;
//        System.out.println("Run time : " + (System.currentTimeMillis() - start));
//
//        assertTrue(runtime < 1000);
//
//
//
//
//    }
//}
