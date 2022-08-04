package ch.epfl.rigel.gui;

import javafx.animation.AnimationTimer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.ZonedDateTime;

public final class TimeAnimator extends AnimationTimer {

    private ObjectProperty<Boolean> running = new SimpleObjectProperty<>(null);
    private final ObjectProperty<TimeAccelerator> accelerator = new SimpleObjectProperty<>();
    private long timeFromStart;
    private boolean starting;
    private DateTimeBean dateTimeBean;
    private ZonedDateTime zonedDateTime;

    public TimeAnimator(DateTimeBean dateTimeBean) {
        this.dateTimeBean = dateTimeBean;
    }

    /**
     * Called 60 times per second by start(), handle updates and accelerates the time
     *
     * @param now the time of the system at each call of start()
     */
    @Override
    public void handle(long now) {
        if (starting) {
            starting = false;
            timeFromStart = now;
        } else {
            long deltaTime = now - timeFromStart;
            timeFromStart = now;
            dateTimeBean.setZonedDateTime(getAccelerator().adjust(dateTimeBean.getZonedDateTime(), deltaTime));
        }
    }

    /**
     * Start the acceleration of the time
     */
    @Override
    public void start() {
        starting = true;
        running.set(true);
        super.start();
    }

    /**
     * Stop the acceleration of the time
     */
    @Override
    public void stop() {
        running.set(false);
        super.stop();
    }
    public ReadOnlyBooleanProperty getRunning() {
        return ReadOnlyBooleanProperty.readOnlyBooleanProperty(running);
    }

    public TimeAccelerator getAccelerator() {
        return accelerator.get();
    }

    public ObjectProperty<TimeAccelerator> acceleratorProperty() {
        return accelerator;
    }

    public void setAccelerator(TimeAccelerator accelerator) {
        this.accelerator.set(accelerator);
    }

}