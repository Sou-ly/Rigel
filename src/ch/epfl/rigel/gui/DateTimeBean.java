package ch.epfl.rigel.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.*;

public final class DateTimeBean {

    private ObjectProperty<LocalDate> date = new SimpleObjectProperty<>(null);
    private ObjectProperty<LocalTime> time = new SimpleObjectProperty<>(null);
    private ObjectProperty<ZoneId> zone = new SimpleObjectProperty<>(null);

    public DateTimeBean() {
    }

    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    public LocalDate getDate(){
        return date.get();
    }

    public void setDate(LocalDate newDate) {
        date.set(newDate);
    }

    public ObjectProperty<LocalTime> timeProperty() {
        return time;
    }

    public LocalTime getTime() {
        return time.get();
    }

    public void setTime(LocalTime newTime) {
        time.set(newTime);
    }

    public ObjectProperty<ZoneId> zoneProperty() {
        return zone;
    }

    public ZoneId getZone() {
        return zone.get();
    }

    public void setZone(ZoneId newZone) {
        zone.set(newZone);
    }

    public ZonedDateTime getZonedDateTime() {
        return ZonedDateTime.of(getDate(), getTime(), getZone());
    }

    public void setZonedDateTime(ZonedDateTime zdt){
        date.set(zdt.toLocalDate());
        time.set(zdt.toLocalTime());
        zone.set(zdt.getZone());
    }
}

