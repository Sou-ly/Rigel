package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;

import java.util.Locale;
import java.util.function.Function;

/**
 * @author Francois Dumoncel (314420)
 */

public final class StereographicProjection implements Function<HorizontalCoordinates, CartesianCoordinates> {

    /**
     * some private attributes to avoid useless calculation
     */
    private final double lambda_0;
    private final double phi_1;
    private final double cosPhi_1;
    private final double sinPhi_1;

    /**
     * @param center coordinates of the center of the projection
     */
    public StereographicProjection(HorizontalCoordinates center) {
        lambda_0 = center.lon();
        phi_1 = center.lat();
        cosPhi_1 = Math.cos(phi_1);
        sinPhi_1 = Math.sin(phi_1);
    }

    /**
     * Computation of the coordinates of the center of the projection
     *
     * @param hor the point through which the parallel passes
     * @return coordinates of the center circle of the projection passing by the hor point
     */
    public CartesianCoordinates circleCenterForParallel(HorizontalCoordinates hor) {
        double yCenterCoordinate = cosPhi_1 / (Math.sin(hor.lat()) + sinPhi_1);
        return CartesianCoordinates.of(0, yCenterCoordinate);
    }


    /**
     * returns the radius of the circle corresponding to the projection of the parallel passing through the point of horizontal coordinates
     *
     * @param parallel the parallel passing by the point of coordinates hor
     * @return the radius of the circle of the projection
     */
    public double circleRadiusForParallel(HorizontalCoordinates parallel) {
        return Math.cos(parallel.lat()) / (Math.sin(parallel.lat()) + sinPhi_1);
    }

    /**
     * Returns the projected diameter of a sphere of angular size rad
     *
     * @param rad angular size in radian
     * @return the diameter of the projected sphere
     */
    public double applyToAngle(double rad) {
        return 2 * Math.tan(rad / 4);
    }

    /**
     * Redefinition of the apply() method from the Function interface
     *
     * @param azAlt HorizontalCoordinates of the azAlt point
     * @return the Cartesian projection
     */
    @Override
    public CartesianCoordinates apply(HorizontalCoordinates azAlt) {
        double lambdaD = azAlt.az() - lambda_0;
        double cosLambdaD = Math.cos(lambdaD);
        double d = 1 / (1 + Math.sin(azAlt.lat()) * sinPhi_1 + Math.cos(azAlt.lat()) * cosPhi_1 * cosLambdaD);

        double x = d * Math.cos(azAlt.lat()) * Math.sin(lambdaD);
        double y = d * (Math.sin(azAlt.lat()) * cosPhi_1 - Math.cos(azAlt.lat()) * sinPhi_1 * cosLambdaD);

        return CartesianCoordinates.of(x, y);
    }

    /**
     * Compute the inverse of the apply() method
     *
     * @param xy point in cartesian coordinates (x,y)
     * @return the projection of xy in Horizontal Coordinates
     */
    public HorizontalCoordinates inverseApply(CartesianCoordinates xy) {
        if (xy.x() == 0 && xy.y() == 0) {
            return HorizontalCoordinates.of(Angle.normalizePositive(lambda_0), phi_1);
        } else {
            double rho = Math.sqrt(xy.x() * xy.x() + xy.y() * xy.y());
            double sinC = (2 * rho) / ((rho * rho) + 1);
            double cosC = (1 - (rho * rho)) / ((rho * rho) + 1);

            double lambda = Math.atan2(xy.x() * sinC, rho * cosPhi_1 * cosC - xy.y() * sinPhi_1 * sinC) + lambda_0;
            double phi = Math.asin((cosC * sinPhi_1) + ((xy.y() * sinC * cosPhi_1) / rho));

            double newLambda = Angle.normalizePositive(lambda);

            return HorizontalCoordinates.of(newLambda, phi);
        }
    }

    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }

    /**
     * @return "Stereographic Projection : (x=,y=)"
     */
    @Override
    public final String toString() {
        return String.format(Locale.ROOT, "Stereographic Projection : (x=%.4f, y=%.4f)",
                lambda_0, phi_1);
    }
}