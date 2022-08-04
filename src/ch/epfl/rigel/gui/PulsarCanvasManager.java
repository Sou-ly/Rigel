package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.CelestialObject;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

import java.util.Optional;


// ------------------------- BONUS ------------------------- \\


/**
 * @author Francois Dumoncel (314420)
 */
public class PulsarCanvasManager {
    private final Canvas pulsarCanvas;

    private ObjectProperty<CartesianCoordinates> mousePosition = new SimpleObjectProperty<>(CartesianCoordinates.of(1, 1));
    private ObjectProperty<Optional<CelestialObject>> objectUnderMouse = new SimpleObjectProperty<>();

    private Binding<ObservedSky> sky;
    private Binding<Transform> planeToCanvas;
    private Binding<StereographicProjection> projection;
    private Binding<SkyCanvasPainter> painterBinding;

    private DoubleProperty rotationPeriod = new SimpleDoubleProperty();
    private DoubleProperty age = new SimpleDoubleProperty();
    private DoubleProperty dist = new SimpleDoubleProperty();

    private static final RightOpenInterval azInterval = RightOpenInterval.of(0, 360);
    private static final ClosedInterval altInterval = ClosedInterval.of(5, 90);
    private static final ClosedInterval fovInterval = ClosedInterval.of(30, 150);


    PulsarCanvasManager(StarCatalogue catalogue, DateTimeBean dateTimeBean, ObserverLocationBean observerLocationBean, ViewingParametersBean viewingParametersBean) {
        pulsarCanvas = new Canvas(800, 800);
        SkyCanvasPainter painter = new SkyCanvasPainter(pulsarCanvas);

        projection = Bindings.createObjectBinding(
                () -> new StereographicProjection(viewingParametersBean.getCenter()),
                viewingParametersBean.centerProperty()
        );

        planeToCanvas = Bindings.createObjectBinding(
                () -> {
                    double dilatationFactor = pulsarCanvas.widthProperty().get() / projection.getValue().applyToAngle(Angle.ofDeg(viewingParametersBean.getFieldOfViewDeg()));
                    return Transform.affine(dilatationFactor, 0, 0, -dilatationFactor, pulsarCanvas.getWidth() / 2, pulsarCanvas.getHeight() / 2);
                }, pulsarCanvas.widthProperty(), pulsarCanvas.heightProperty(), viewingParametersBean.fieldProperty()
        );

        sky = Bindings.createObjectBinding(
                () -> new ObservedSky(dateTimeBean.getZonedDateTime(), observerLocationBean.getCoordinates(), projection.getValue(), catalogue),
                dateTimeBean.dateProperty(), observerLocationBean.coordinatesProperty(),
                projection, viewingParametersBean.fieldProperty(), viewingParametersBean.centerProperty(), dateTimeBean.timeProperty(), planeToCanvas
        );

        painterBinding = Bindings.createObjectBinding(
                () -> {
                    drawPulsar(painter, sky.getValue(), projection.getValue(), planeToCanvas.getValue());
                    return painter;
                }, sky
        );


        painterBinding.addListener((p, o, n) -> System.out.println());

        pulsarCanvas.setOnMouseClicked(e -> pulsarCanvas.requestFocus());

        pulsarCanvas.setOnMouseMoved(e -> {
            mousePosition.set(CartesianCoordinates.of(e.getX(), e.getY()));
            try {
                objectUnderMouse.set(sky.getValue().objectClosestTo(inverseTransformation(mousePosition.get(), planeToCanvas.getValue()), planeToCanvas.getValue().inverseDeltaTransform(10, 0).magnitude()));
                if (objectUnderMouse.get().isPresent()) {
                    age.set(objectUnderMouse.get().get().getAge() / 1_000_000);
                    rotationPeriod.set(objectUnderMouse.get().get().getP0());
                    dist.set(objectUnderMouse.get().get().getDistance());
                } else {
                    age.set(0);
                    rotationPeriod.set(0);
                    dist.set(0);
                }
            } catch (NonInvertibleTransformException ex) {
                ex.printStackTrace();
            }
        });

        pulsarCanvas.setOnScroll(e -> {
            double FoV = (Math.abs(e.getDeltaX()) < Math.abs(e.getDeltaY())) ? e.getDeltaY() : e.getDeltaX();
            viewingParametersBean.setFieldOfViewDeg(fovInterval.clip(viewingParametersBean.getFieldOfViewDeg() + FoV / 6));
        });

        pulsarCanvas.setOnKeyPressed(keyEvent -> {
            KeyCode key = keyEvent.getCode();

            switch (key) {
                case S:
                    viewingParametersBean.setCenter(HorizontalCoordinates.ofDeg(viewingParametersBean.getCenter().azDeg(),
                            altInterval.clip(viewingParametersBean.getCenter().altDeg() - 5)));
                    break;
                case W:
                    viewingParametersBean.setCenter(HorizontalCoordinates.ofDeg(viewingParametersBean.getCenter().azDeg(),
                            altInterval.clip(viewingParametersBean.getCenter().altDeg() + 5)));
                    break;
                case A:
                    viewingParametersBean.setCenter(HorizontalCoordinates.ofDeg(azInterval.reduce(viewingParametersBean.getCenter().azDeg() - 10),
                            viewingParametersBean.getCenter().altDeg()));
                    break;
                case D:
                    viewingParametersBean.setCenter(HorizontalCoordinates.ofDeg(azInterval.reduce(viewingParametersBean.getCenter().azDeg() + 10),
                            viewingParametersBean.getCenter().altDeg()));
                    break;
            }
            keyEvent.consume();
        });
    }

    private void drawPulsar(SkyCanvasPainter painter, ObservedSky sky, StereographicProjection projection, Transform planeToCanvas) {
        painter.clearForPulsar();
        painter.drawPulsars(sky, projection, planeToCanvas);
        painter.drawHorizon(projection, planeToCanvas);
    }

    /**
     * Compute the inverse of the transformation projection -> Canvas. So basically, coordinates are transformed from Canvas -> projection
     *
     * @param mousePosition the position of the mouse
     * @param planeToCanvas instance of Transform to use to do the transformation
     * @return new coordinates (in the projection) as an instance of CartesianCoordinates
     * @throws NonInvertibleTransformException if the transformation is impossible (So if the point isn't transformed from projection to canvas yet)
     */
    private CartesianCoordinates inverseTransformation(CartesianCoordinates mousePosition, Transform planeToCanvas) throws NonInvertibleTransformException {
        Point2D pos = planeToCanvas.inverseTransform(mousePosition.x(), mousePosition.y());
        return CartesianCoordinates.of(pos.getX(), pos.getY());
    }

    ObjectProperty<Optional<CelestialObject>> objectUnderMouseProperty() {
        return objectUnderMouse;
    }

    public double getRotationPeriod() {
        return rotationPeriod.get();
    }

    public DoubleProperty rotationPeriodProperty() {
        return rotationPeriod;
    }

    public void setRotationPeriod(double rotationPeriod) {
        this.rotationPeriod.set(rotationPeriod);
    }

    public double getAge() {
        return age.get();
    }

    public DoubleProperty ageProperty() {
        return age;
    }

    public double getDist() {
        return dist.get();
    }

    public DoubleProperty distProperty() {
        return dist;
    }

    public void setDist(double dist) {
        this.dist.set(dist);
    }

    public Canvas pulsarCanvas() {
        return pulsarCanvas;
    }

}
