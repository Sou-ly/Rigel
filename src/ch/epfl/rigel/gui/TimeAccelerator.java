package ch.epfl.rigel.gui;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

@FunctionalInterface
public interface TimeAccelerator {


    ZonedDateTime adjust(ZonedDateTime T_0, long timeSinceStart);

    static TimeAccelerator continuous(int alpha) {
        return (T_0, timeSinceStart) -> T_0.plus(alpha*timeSinceStart, ChronoUnit.NANOS);
    }

    static TimeAccelerator discrete(int frequency, Duration steps){
        return (T_0, timeSinceStart) -> T_0.plus( Math.floorDiv(frequency * timeSinceStart, 1000000000) * steps.toNanos(), ChronoUnit.NANOS);
    }
}
