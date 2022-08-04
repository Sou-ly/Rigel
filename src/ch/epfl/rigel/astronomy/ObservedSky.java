package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.coordinates.*;
import javafx.scene.shape.Circle;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class ObservedSky {

    private final StarCatalogue catalogue;
    private final Sun sun;
    private final Moon moon;
    private Map<CelestialObject, CartesianCoordinates> all = new HashMap<>();
    private CartesianCoordinates sunProjectedPosition;
    private CartesianCoordinates moonProjectedPosition;
    private Pulsar[] pulsars;
    private double[] pulsarProjectedPosition;
    private Planet[] planetList;
    private double[] starsPosition;
    private Star[] stars;
    private double[] planetProjectedPosition;

    public ObservedSky(ZonedDateTime when, GeographicCoordinates where, StereographicProjection projection, StarCatalogue catalogue) {
        this.catalogue = catalogue;
        pulsars = new Pulsar[catalogue.pulsars().size()];
        pulsarProjectedPosition = new double[pulsars.length * 2];
        stars = new Star[catalogue.stars().size()];
        starsPosition = new double[stars.length * 2];
        planetList = new Planet[PlanetModel.ALL.size() - 1];
        planetProjectedPosition = new double[planetList.length * 2];

        EclipticToEquatorialConversion ecltoEq = new EclipticToEquatorialConversion(when);
        double daysSinceJ2010 = Epoch.J2010.daysUntil(when);

        EclipticToEquatorialConversion eclToEqu = new EclipticToEquatorialConversion(when);
        EquatorialToHorizontalConversion equToHor = new EquatorialToHorizontalConversion(when, where);

        Function<EclipticCoordinates, HorizontalCoordinates> eclToHor = eclToEqu.andThen(equToHor);

        sun = SunModel.SUN.at(daysSinceJ2010, ecltoEq);
        sunProjectedPosition = projection.apply(eclToHor.apply(sun.eclipticPos()));
        all.put(sun, sunProjectedPosition);

        moon = MoonModel.MOON.at(daysSinceJ2010, ecltoEq);
        moonProjectedPosition = projection.apply(equToHor.apply(moon.equatorialPos()));
        all.put(moon, moonProjectedPosition);

        List<Planet> planetTransi = new ArrayList<>();
        for (PlanetModel planet : PlanetModel.values()) {
            if (planet != PlanetModel.EARTH) {
                planetTransi.add(planet.at(daysSinceJ2010, ecltoEq));
            }
        }
        for (int i = 0; i < planetTransi.size(); i++) {
            planetList[i] = planetTransi.get(i);
        }

        for (int j = 0; j < planetList.length; j++) {
            CartesianCoordinates projectedPosition = projection.apply(equToHor.apply(planetList[j].equatorialPos()));
            planetProjectedPosition[2 * j] = projectedPosition.x();
            planetProjectedPosition[(2 * j) + 1] = projectedPosition.y();
            all.put(planetList[j], projectedPosition);
        }

        for (Star star : catalogue.stars()) {
            stars[catalogue.stars().indexOf(star)] = star;
        }
        for (int i = 0; i < stars.length; i++) {
            CartesianCoordinates projectedPosition = projection.apply(equToHor.apply(stars[i].equatorialPos()));
            starsPosition[2 * i] = projectedPosition.x();
            starsPosition[(2 * i) + 1] = projectedPosition.y();
            all.put(stars[i], projectedPosition);
        }

        for (Pulsar p : catalogue.pulsars()) {
            pulsars[catalogue.pulsars().indexOf(p)] = p ;
        }

        for (int i = 0; i < pulsars.length; i++) {
            CartesianCoordinates projectedPosition = projection.apply(equToHor.apply(pulsars[i].equatorialPos()));
            pulsarProjectedPosition[2 * i] = projectedPosition.x();
            pulsarProjectedPosition[(2 * i)+1] = projectedPosition.y();
            all.put(pulsars[i], projectedPosition);
        }
    }

    public Sun sun() {
        return sun;
    }

    public Moon moon() {
        return moon;
    }

    public Planet[] planets() {
        return planetList;
    }

    public Star[] stars() {
        return stars;
    }

    public Pulsar[] pulsars() {
        return pulsars;
    }

    public CartesianCoordinates sunPosition() {
        return sunProjectedPosition;
    }

    public CartesianCoordinates moonPosition() {
        return moonProjectedPosition;
    }

    public double[] planetPosition() {
        return planetProjectedPosition;
    }

    public double[] starPosition() {
        return starsPosition;
    }

    public double[] pulsarPosition() {
        return pulsarProjectedPosition;
    }

    public Set<Asterism> usedAsterism() {
        return catalogue.asterisms();
    }

    public List<Integer> usedIndex(Asterism asterism) {
        return catalogue.asterismIndices(asterism);
    }

    public Set<CelestialObject> allCO() {
        return all.keySet();
    }

    public Optional<CelestialObject> objectClosestTo(CartesianCoordinates position, double radius) {
        Preconditions.checkArgument(radius >= 0);
        Optional<CelestialObject> closest = Optional.empty();
        Circle circle = new Circle(position.x(), position.y(), radius);
        for (Map.Entry<CelestialObject, CartesianCoordinates> entry : all.entrySet()) {
            double x = entry.getValue().x();
            double y = entry.getValue().y();
            if(circle.contains(x, y)){
                closest = Optional.of(entry.getKey());
                circle.setRadius(sqrt(pow(x - position.x(), 2) + pow(y - position.y(), 2)));
            }
        }
        return closest;
    }
}
