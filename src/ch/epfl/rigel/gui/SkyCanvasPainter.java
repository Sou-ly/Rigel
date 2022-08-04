package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.*;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Transform;
import javafx.util.Duration;

import java.util.*;

public class SkyCanvasPainter {

    private Canvas canvas;
    private GraphicsContext ctx;
    private static final ClosedInterval STAR_MAGNITUDE_INTERVAL = ClosedInterval.of(-2, 5);
    private static final ClosedInterval PULSAR_MAGNITUDE_INTERVAL = ClosedInterval.of(15, 27);
    private static final double SUN_ANGULAR_SIZE = Angle.ofDeg(0.5);

    public SkyCanvasPainter(Canvas canvas) {
        this.canvas = canvas;
        this.ctx = canvas.getGraphicsContext2D();

    }

    private static double diameterFromMagnitude(double magnitude, StereographicProjection projection) {
        double clippedMagnitude = STAR_MAGNITUDE_INTERVAL.clip(magnitude);
        double f = (99 - 17 * clippedMagnitude) / 140;
        return f * projection.applyToAngle(SUN_ANGULAR_SIZE);
    }

    void clear() {
        ctx.setFill(Color.BLACK);
        ctx.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        ctx.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    void drawMoon(ObservedSky sky, StereographicProjection projection, Transform planeToCanvas) {
        double diameter = projection.applyToAngle(SUN_ANGULAR_SIZE);
        double diamForCanvas = planeToCanvas.deltaTransform(diameter, 0).magnitude();

        Point2D pos = planeToCanvas.transform(sky.moonPosition().x(), sky.moonPosition().y());
        double x = pos.getX(), y = pos.getY();

        ctx.setFill(Color.WHITE);
        ctx.fillOval(x - diamForCanvas / 2, y - diamForCanvas / 2, diamForCanvas, diamForCanvas);
    }

    void drawStars(ObservedSky sky, StereographicProjection projection, Transform planeToCanvas) {
        Star[] starToDraw = sky.stars();
        double[] starProjectedCoords = sky.starPosition();
        double[] starCanvasCoords = new double[starProjectedCoords.length];

        planeToCanvas.transform2DPoints(starProjectedCoords, 0, starCanvasCoords, 0, 5067);

        Bounds bounds = canvas.getBoundsInLocal();

        for (Asterism asterism : sky.usedAsterism()) {

            List<Integer> positionIndexList = sky.usedIndex(asterism);

            for (int i = 0; i < positionIndexList.size() - 1; i++) {

                int startIndex = 2 * positionIndexList.get(i);
                int endIndex = 2 * positionIndexList.get(i + 1);

                Point2D startingPoint = new Point2D(starCanvasCoords[startIndex], starCanvasCoords[startIndex + 1]);
                Point2D endingPoint = new Point2D(starCanvasCoords[endIndex], starCanvasCoords[endIndex + 1]);

                if (bounds.contains(startingPoint) || bounds.contains(endingPoint)) {
                    ctx.setStroke(Color.BLUE);
                    ctx.setLineWidth(1);
                    ctx.beginPath();
                    ctx.moveTo(startingPoint.getX(), startingPoint.getY());
                    ctx.lineTo(endingPoint.getX(), endingPoint.getY());
                    ctx.stroke();
                    ctx.closePath();
                }

            }
        }

        //Draw stars
        for (int i = 0; i < starToDraw.length; i++) {
            double diameter = diameterFromMagnitude(starToDraw[i].magnitude(), projection);
            double diameterForCanvas = planeToCanvas.deltaTransform(diameter, 0).magnitude();
            double r = diameterForCanvas / 2;

            ctx.setFill(BlackBodyColor.colorForTemperature(starToDraw[i].colorTemperature()));
            ctx.fillOval(starCanvasCoords[2 * i] - r, starCanvasCoords[(2 * i) + 1] - r, diameterForCanvas, diameterForCanvas);
        }

    }

    void drawSun(ObservedSky sky, StereographicProjection projection, Transform planeToCanvas) {
        double diameter = projection.applyToAngle(SUN_ANGULAR_SIZE);
        double diam1 = planeToCanvas.deltaTransform(diameter, 0).magnitude();
        double diam2 = diam1 + 2, diam3 = diam1 * 2.2;

        Point2D pos = planeToCanvas.transform(sky.sunPosition().x(), sky.sunPosition().y());
        double x = pos.getX(), y = pos.getY();

        ctx.setFill(Color.YELLOW.deriveColor(1.0, 1.0, 1.0, 0.25));
        ctx.fillOval(x - diam3 / 2, y - diam3 / 2, diam3, diam3);

        ctx.setFill(Color.YELLOW);
        ctx.fillOval(x - diam2 / 2, y - diam2 / 2, diam2, diam2);

        ctx.setFill(Color.WHITE);
        ctx.fillOval(x - diam1 / 2, y - diam1 / 2, diam1, diam1);

    }

    void drawHorizon(StereographicProjection projection, Transform planeToCanvas) {
        double radiusForCircle = projection.circleRadiusForParallel(HorizontalCoordinates.ofDeg(0, 0));
        double radius = Math.abs(planeToCanvas.deltaTransform(radiusForCircle, 0).magnitude());

        CartesianCoordinates centerForCircle = projection.circleCenterForParallel(HorizontalCoordinates.ofDeg(0, 0));
        Point2D center = planeToCanvas.transform(centerForCircle.x(), centerForCircle.y());
        double X = center.getX(), Y = center.getY();

        ctx.setStroke(Color.RED);
        ctx.setLineWidth(2);
        ctx.strokeOval(X - radius, Y - radius, radius * 2, radius * 2);

        for (int i = 0; i < 8; i++) {
            HorizontalCoordinates point = HorizontalCoordinates.ofDeg(i * 45, -0.5);
            CartesianCoordinates point1 = projection.apply(point);

            Point2D planeOctant = planeToCanvas.transform(point1.x(), point1.y());
            double W = planeOctant.getX(), Z = planeOctant.getY();

            ctx.setLineWidth(1);
            ctx.setTextBaseline(VPos.TOP);
            ctx.setTextAlign(TextAlignment.CENTER);
            ctx.setFill(Color.RED);
            ctx.setLineWidth(2);
            ctx.fillText(point.azOctantName("N", "E", "S", "O"), W, Z);
        }
    }

    void drawPlanets(ObservedSky sky, StereographicProjection projection, Transform planeToCanvas) {
        Planet[] planetToDraw = sky.planets();
        double[] planetProjectedCoords = sky.planetPosition();
        double[] planetCanvasCoords = new double[planetProjectedCoords.length];

        planeToCanvas.transform2DPoints(planetProjectedCoords, 0, planetCanvasCoords, 0, 7);

        for (int i = 0; i < planetToDraw.length; i++) {

            double diameter = diameterFromMagnitude(planetToDraw[i].magnitude(), projection);
            double diamForCanvas = planeToCanvas.deltaTransform(diameter, 0).magnitude();
            double r = diamForCanvas / 2;

            ctx.setFill(Color.LIGHTGRAY);
            ctx.fillOval(planetCanvasCoords[2 * i] - r, planetCanvasCoords[(2 * i) + 1] - r, diamForCanvas, diamForCanvas);

        }
    }


    // ------------------------- BONUS ------------------------- \\

    private static double diameterFromMagnitudeForPulsar(float magnitude, StereographicProjection projection) {
        double clippedMagnitude = PULSAR_MAGNITUDE_INTERVAL.clip(magnitude);
        double f = (99 - 17 * clippedMagnitude) / 800;
        return f * projection.applyToAngle(SUN_ANGULAR_SIZE);
    }

    void clearForPulsar() {
        ctx.setFill(Color.web("#46115c"));
        ctx.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        ctx.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    void drawPulsars(ObservedSky sky, StereographicProjection projection, Transform planeToCanvas) {
        Pulsar[] pulsarsToDraw = sky.pulsars();
        double[] pulsarProjectedCoords = sky.pulsarPosition();
        double[] pulsarTransformedCoords = new double[pulsarProjectedCoords.length];

        planeToCanvas.transform2DPoints(pulsarProjectedCoords, 0, pulsarTransformedCoords, 0, 2528);
        HashMap<Point2D, Double> pulsarPoint = new HashMap<>();

        for (int i = 0; i < pulsarsToDraw.length; i++) {
            double diameter = diameterFromMagnitudeForPulsar((float) pulsarsToDraw[i].magnitude(), projection);
            double diameterForCanvas = planeToCanvas.deltaTransform(diameter, 0).magnitude();
            double r = diameterForCanvas / 2;

            pulsarPoint.put(new Point2D(pulsarTransformedCoords[2 * i] - r, pulsarTransformedCoords[(2 * i) + 1] - r), diameterForCanvas);

            ctx.setFill(Color.WHITE);
            ctx.fillOval(pulsarTransformedCoords[2 * i] - r, pulsarTransformedCoords[(2 * i) + 1] - r, diameterForCanvas, diameterForCanvas);
        }
    }

}
