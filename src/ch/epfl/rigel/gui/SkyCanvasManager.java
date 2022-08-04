package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.CelestialObject;
import ch.epfl.rigel.astronomy.Moon;
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

public class SkyCanvasManager {

    private ObjectProperty<CartesianCoordinates> mousePosition = new SimpleObjectProperty<>(CartesianCoordinates.of(1, 1));
    private ObjectProperty<Optional<CelestialObject>> objectUnderMouse = new SimpleObjectProperty<>();
    private Canvas canvas;

    private final Binding<ObservedSky> sky;
    private final Binding<Transform> planeToCanvas;
    private final Binding<StereographicProjection> projection;
    private Binding<SkyCanvasPainter> painterBinding;
    private Binding<HorizontalCoordinates> mouseHorizontalPosition;

    private static final RightOpenInterval azInterval = RightOpenInterval.of(0, 360);
    private static final ClosedInterval altInterval = ClosedInterval.of(5, 90);
    private static final ClosedInterval fovInterval = ClosedInterval.of(30, 150);

    private DoubleProperty mouseAzDeg = new SimpleDoubleProperty();
    private DoubleProperty mouseAltDeg = new SimpleDoubleProperty();


    public SkyCanvasManager(StarCatalogue catalogue, DateTimeBean dateTimeBean, ObserverLocationBean observerLocationBean, ViewingParametersBean viewingParametersBean) {
        canvas = new Canvas(800, 600);
        SkyCanvasPainter painter = new SkyCanvasPainter(canvas);

        projection = Bindings.createObjectBinding(
                () -> new StereographicProjection(viewingParametersBean.getCenter()),
                viewingParametersBean.centerProperty()
        );

        planeToCanvas = Bindings.createObjectBinding(
                () -> {
                    double dilatationFactor = canvas.widthProperty().get() / projection.getValue().applyToAngle(Angle.ofDeg(viewingParametersBean.getFieldOfViewDeg()));
                    return Transform.affine(dilatationFactor, 0, 0, -dilatationFactor, canvas.getWidth() / 2, canvas.getHeight() / 2);
                }, canvas.widthProperty(), canvas.heightProperty(), viewingParametersBean.fieldProperty()
        );

        sky = Bindings.createObjectBinding(
                () -> new ObservedSky(dateTimeBean.getZonedDateTime(), observerLocationBean.getCoordinates(), projection.getValue(), catalogue),
                dateTimeBean.dateProperty(), observerLocationBean.coordinatesProperty(),
                projection, viewingParametersBean.fieldProperty(), viewingParametersBean.centerProperty(), dateTimeBean.timeProperty(), planeToCanvas
        );


        sky.addListener((p, o, n) -> drawSky(painter, sky.getValue(), projection.getValue(), planeToCanvas.getValue()));


        canvas.setOnMouseMoved(e -> {
            setMousePosition(CartesianCoordinates.of(e.getX(), e.getY()));
            setMouseAltDeg(mouseHorizontalPosition.getValue().altDeg());
            setMouseAzDeg(mouseHorizontalPosition.getValue().azDeg());
            try {
                setObjectUnderMouse(sky.getValue().objectClosestTo(inverseTransformation(getMousePosition(), planeToCanvas.getValue()), planeToCanvas.getValue().inverseDeltaTransform(10, 0).magnitude()));
            } catch (NonInvertibleTransformException ex) {
                ex.printStackTrace();
            }
        });

        mouseHorizontalPosition = Bindings.createObjectBinding(
                () -> projection.getValue().inverseApply(inverseTransformation(mousePosition.get(), planeToCanvas.getValue())),
                mousePositionProperty(), projection, planeToCanvas
        );

        canvas.setOnMouseClicked(e -> canvas.requestFocus());

        canvas.setOnScroll(e -> {
            double FoV = (Math.abs(e.getDeltaX()) < Math.abs(e.getDeltaY())) ? e.getDeltaY() : e.getDeltaX();
            viewingParametersBean.setFieldOfViewDeg(fovInterval.clip(viewingParametersBean.getFieldOfViewDeg() + FoV / 6));
            drawSky(painter, sky.getValue(), projection.getValue(), planeToCanvas.getValue());
        });

        canvas.setOnKeyPressed(keyEvent -> {
            KeyCode key = keyEvent.getCode();

            switch (key) {
                case DOWN:
                    modifyAltCords(viewingParametersBean, -5);
                    break;
                case UP:
                    modifyAltCords(viewingParametersBean, 5);
                    break;
                case LEFT:
                    modifyAzCords(viewingParametersBean, -10);
                    break;
                case RIGHT:
                    modifyAzCords(viewingParametersBean, 10);
                    break;
            }
            drawSky(painter, sky.getValue(), projection.getValue(), planeToCanvas.getValue());
            keyEvent.consume();
        });
    }

    /**
     * Draw the entire sky to avoid some redundant lines of codes
     *
     * @param painter       a SkyCanvasPainter instance
     * @param sky           the observedSky at the given moment
     * @param projection    the stereographic projection to use
     * @param planeToCanvas useful to transform coordinates, a Transform instance
     */
    private void drawSky(SkyCanvasPainter painter, ObservedSky sky, StereographicProjection projection, Transform planeToCanvas) {
        painter.clear();
        painter.drawStars(sky, projection, planeToCanvas);
        painter.drawPlanets(sky, projection, planeToCanvas);
        painter.drawSun(sky, projection, planeToCanvas);
        painter.drawMoon(sky, projection, planeToCanvas);
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

    private void modifyAltCords(ViewingParametersBean viewingParametersBean, int delta) {
        viewingParametersBean.setCenter(HorizontalCoordinates.ofDeg(viewingParametersBean.getCenter().azDeg(),
                altInterval.clip(viewingParametersBean.getCenter().altDeg() + delta)));
    }

    private void modifyAzCords(ViewingParametersBean viewingParametersBean, int delta) {
        viewingParametersBean.setCenter(HorizontalCoordinates.ofDeg(azInterval.reduce(viewingParametersBean.getCenter().azDeg() + delta),
                viewingParametersBean.getCenter().altDeg()));
    }

    // ------------------ Getter & Setter ------------------- \\

    private CartesianCoordinates getMousePosition() {
        return mousePosition.get();
    }

    private ObjectProperty<CartesianCoordinates> mousePositionProperty() {
        return mousePosition;
    }

    private void setMousePosition(CartesianCoordinates mousePosition) {
        this.mousePosition.set(mousePosition);
    }

    ObjectProperty<Optional<CelestialObject>> objectUnderMouseProperty() {
        return objectUnderMouse;
    }

    private void setObjectUnderMouse(Optional<CelestialObject> objectUnderMouse) {
        this.objectUnderMouse.set(objectUnderMouse); }

    DoubleProperty mouseAzDegProperty() {
        return mouseAzDeg;
    }

    private void setMouseAzDeg(double mouseAzDeg) {
        this.mouseAzDeg.set(mouseAzDeg);
    }

    DoubleProperty mouseAltDegProperty() {
        return mouseAltDeg;
    }

    private void setMouseAltDeg(double mouseAltDeg) {
        this.mouseAltDeg.set(mouseAltDeg);
    }

    public ObservedSky getSky() {
        return sky.getValue();
    }

    Canvas canvas() {
        return canvas;
    }
}
