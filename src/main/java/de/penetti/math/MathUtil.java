package de.penetti.math;

/**
 * @author Enrico S. Penetti &lt;enrico@penetti.de&gt;
 */
public final class MathUtil {
  public static final double HALF_PI = Math.PI * .5;
  public static final double PI = Math.PI;
  public static final double TWO_PI = 2 * Math.PI;

  private MathUtil() {
  }

  public static double adjustAngle(double angleInRadians) {
    double radians = angleInRadians;
    while (Double.compare(radians, TWO_PI) > 0) {
      radians -= TWO_PI;
    }
    while (Double.compare(radians, .0) < 0) {
      radians += TWO_PI;
    }
    if (Double.compare(radians, TWO_PI) == 0) {
      radians = .0;
    }
    return radians;
  }

  /**
   * Rounds the value of a.
   * <pre>
   * Math.floor(a + 0.5d)
   * </pre>
   *
   * @param a
   * @return
   */
  @SuppressWarnings("checkstyle:magicnumber")
  public static double round(final double a) {
    return Math.floor(a + 0.5d);
  }

  /**
   * Rounds the value of a with special count of decimal places.
   *
   * @param a
   * @param decimalPlaces
   * @return
   */
  @SuppressWarnings("checkstyle:magicnumber")
  public static double round(final double a, final int decimalPlaces) {
    double pow = Math.pow(10, decimalPlaces);
    return round(a * pow) / pow;
  }
}
