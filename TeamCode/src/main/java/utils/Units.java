package utils;

public class Units {
    private static final double INCHES_TO_METERS = 0.0254;

    public static double inchesToMeters(double inches) {
        return inches * INCHES_TO_METERS;
    }

    public static double metersToInches(double meters) {
        return meters / INCHES_TO_METERS;
    }
}
