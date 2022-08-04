package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.astronomy.SiderealTime;
import ch.epfl.rigel.math.Angle;

import java.time.ZonedDateTime;
import java.util.function.Function;

/**
 * @author Souleyman Boudouh (302207)
 * @author Francois Dumoncel (314420)
 */
public final class EquatorialToHorizontalConversion implements Function<EquatorialCoordinates, HorizontalCoordinates> {

    private final double cosPhi;
    private final double sinPhi;
    private final double siderealTime;

    public EquatorialToHorizontalConversion(ZonedDateTime when, GeographicCoordinates where) {
        double phi = where.lat();
        siderealTime = SiderealTime.local(when, where);
        cosPhi = Math.cos(phi);
        sinPhi = Math.sin(phi);
    }

    /**
     * Converts equatorial coordinates to horizontal coordinates
     *
     * @param equ the equatorial coordinates
     * @return the transformation from equatorial to horizontal
     */
    @Override
    public HorizontalCoordinates apply(EquatorialCoordinates equ) {
        double h1 = siderealTime - equ.ra();
        double cosH = Math.cos(h1);
        double sinH = Math.sin(h1);
        double delta = equ.dec();

        double h = (Math.sin(delta) * sinPhi + Math.cos(delta) * cosPhi * cosH);
        double A = Math.atan2(-Math.cos(delta) * cosPhi * sinH, Math.sin(delta) - sinPhi * h);

        return HorizontalCoordinates.of(Angle.normalizePositive(A), Math.asin(h));
    }

    @Override
    public  boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException();
    }
}