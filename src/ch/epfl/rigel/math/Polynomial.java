package ch.epfl.rigel.math;

import ch.epfl.rigel.Preconditions;

/**
 * @author Francois Dumoncel (314420)
 * @author Souleyman Boudouh (302207)
 */
public final class Polynomial {


    private double[] coeffArray;
    private final int degree;

    private Polynomial(double[] coefficients) {
        coeffArray = coefficients;
        degree = coeffArray.length - 1;
    }

    /**
     * Construction method of polynomial
     *
     * @param coefficientN the coefficient of the greater exponent
     * @param coefficients other coefficient
     * @return a polynomial with the coefficient passed
     * @throws IllegalArgumentException if coefficientN == 0
     */
    public static Polynomial of(double coefficientN, double... coefficients) {
        Preconditions.checkArgument(coefficientN != 0);
        double[] copyArray = new double[coefficients.length + 1];
        copyArray[0] = coefficientN;
        System.arraycopy(coefficients, 0, copyArray, 1, coefficients.length);
        return new Polynomial(copyArray);
    }

    /**
     * Compute the value of the polynomial a the given point
     *
     * @param x the given point
     * @return the value of the polynomial
     */
    public double at(double x) {
        double value = coeffArray[0];

        for (int i = 0; i < degree; i++) {
            value = value * x + coeffArray[i + 1];
        }
        return value;
    }

    /**
     * Display the polynomial without useless characters
     *
     * @return a visual representation of the polynomial
     */
    @Override
    public String toString() {
        StringBuilder stringToPrint = new StringBuilder();

        for (int i = 0; i <= degree; i++) {
            int exponent = degree - i;

            if (coeffArray[i] != 0) {

                if (Math.abs(coeffArray[i]) == 1) {
                    stringToPrint.append(((coeffArray[i] < 0) ? "-" : ""));
                } else {
                    stringToPrint.append((coeffArray[i] < 0 || i == 0) ? "" : "+").append(coeffArray[i]);
                }

                if (exponent == 1) {
                    stringToPrint.append("x");
                } else if (exponent > 1) {
                    stringToPrint.append("x^").append(exponent);
                }
            }
        }
        return stringToPrint.toString();
    }

    @Override
    public boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException();
    }

}