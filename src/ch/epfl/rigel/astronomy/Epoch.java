package ch.epfl.rigel.astronomy;

import java.time.*;
import java.time.temporal.ChronoUnit;

/**
 * @author Francois Dumoncel (314420)
 * @author Souleyman Boudouh (302207)
 */
public enum Epoch {

    J2000(ZonedDateTime.of(LocalDate.of(2000, Month.JANUARY, 1),
            LocalTime.NOON, ZoneOffset.UTC)),
    J2010(ZonedDateTime.of(LocalDate.of(2010, Month.JANUARY, 1).minusDays(1),
            LocalTime.MIDNIGHT, ZoneOffset.UTC));


    /**
     * some constant to make more simple the calculation
     */
    public static final double millisPerDay = 1000 * 60 * 60 * 24;
    public static final double daysPerCentury = 36525;


    private final ZonedDateTime epoch;

    Epoch(ZonedDateTime epoch) {
        this.epoch = epoch;
    }

    /**
     * Return the number of days until the when date
     *
     * @param when the given moment
     * @return number of days until when
     */
    public double daysUntil(ZonedDateTime when) {
        return (epoch.until(when, ChronoUnit.MILLIS)) / millisPerDay;
    }

    /**
     * Return the number of julian centuries until when
     *
     * @param when the given moment
     * @return number of julian centuries until when
     */
    public double julianCenturiesUntil(ZonedDateTime when) {
        return daysUntil(when) / daysPerCentury;
    }


    /**
     * Return the epoch
     *
     * @return Epoch
     */
    ZonedDateTime getEpoch() {
        return epoch;
    }
}