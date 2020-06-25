package de.penetti.math.geom;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Objects;

import static de.penetti.math.MathUtil.TWO_PI;
import static java.time.ZoneOffset.UTC;

/**
 * This class is immutable.
 *
 * @author Enrico
 */
public final class Vector implements Serializable {
  private static final long serialVersionUID = -1935965610871146970L;
  private final double x;
  private final double y;

  Vector() {
    this(0, 0);
  }

  Vector(double x, double y) {
    this.x = x;
    this.y = y;
  }

  Vector(Vector vector) {
    Objects.requireNonNull(vector);
    this.x = vector.x;
    this.y = vector.y;
  }

  public static Vector random() {
    return create(magic(), magic());
  }

  public static Vector random(double min, double max) {
    return random(min, max, min, max);
  }

  public static Vector random(double xMin, double xMax, double yMin, double yMax) {
    return create((magic() * ((xMax - xMin) + 1d)) + xMin,
                  (magic() * ((yMax - yMin) + 1d)) + yMin);
  }

  public static Vector create(double x, double y) {
    return new Vector(x, y);
  }

  private static double magic() {
    return SecureRandomHolder.random.nextDouble();
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  /**
   * Rotates this vector with the given angle [rad] around the coordinate origin.
   * <pre>
   *   n.x = x * cos(angle) - y * sin(angle)
   *   n.y = x * sin(angle) + y * cos(angle)
   * </pre>
   *
   * @param angle in radians
   * @return the new rotated vector
   */
  public Vector rotate(double angle) {
    double ca = Math.cos(angle);
    double sa = Math.sin(angle);
    Vector vec = create(x * ca - y * sa,
                        x * sa + y * ca);
    return vec;
  }

  /**
   * Rotates this vector with the given angle [rad] around the given origin.
   * <pre>
   *   sub this vector by origin
   *   rotate the vector with the given angle [rad]
   *   add the vector by origin
   * </pre>
   *
   * @param origin vector around which to rotate
   * @param angle  angle in radians
   * @return the new rotated vector
   */
  public Vector rotate(Vector origin, double angle) {
    Vector vec = sub(origin);
    vec = vec.rotate(angle);
    vec = vec.add(origin);
    return vec;
  }

  /**
   * Returns the angle in radians between this vector and the vector v.
   * This means, the angle between the line (0,0) -> this and the line (0,0) -> v.
   *
   * @return the angle in radians
   * @parameter v
   */
  public double angle(Vector v) {
    double angle;
    double vDot = dot(v) / (length() * v.length());
    if (vDot < -1.0) {
      vDot = -1.0;
    }
    else if (vDot > 1.0) {
      vDot = 1.0;
    }
    angle = Math.acos(vDot);

    double zCross = x * v.y - y * v.x;
    if (zCross < 0) {
      angle = TWO_PI - angle;
    }

    return angle;
  }

  /**
   * Computes the sum from this vector with the vector v.
   * <pre>
   *   n = this + v
   * </pre>
   *
   * @param v the vector to add
   * @return the new added vector
   */
  public Vector add(Vector v) {
    return create(x + v.x, y + v.y);
  }

  /**
   * Computes the difference from this vector and the vector v.
   * <pre>
   *   n = this - v
   * </pre>
   *
   * @param v the second tuple
   * @return the new vector
   */
  public Vector sub(Vector v) {
    return create(x - v.x, y - v.y);
  }

  /**
   * Multiplies this vector with f.
   * <pre>
   *   n = this * f
   * </pre>
   *
   * @return the multiplied vector
   */
  public Vector mul(double f) {
    return create(f * x, f * y);
  }

  /**
   * Computes the length of this vector from the origin.
   *
   * @return the length of this vector
   */
  public double length() {
    return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
  }

  /**
   * Computes the length from this vector to vector v.
   *
   * @param v
   * @return the length from this vector to vector v
   */
  public double length(Vector v) {
    return v.sub(this).length();
  }

  /**
   * Computes the unit vector of this vector.
   * <pre>
   *   n = p / |p|
   * </pre>
   * If the length of this vector is zero, the returned point has an x and y value of NaN.
   *
   * @return the normalized (unit) vector
   */
  public Vector normalize() {
    double n = length();
    return create(x / n, y / n);
  }

  /**
   * Computes the dot product of this vector with vector v.
   *
   * @return the dot product
   */
  public double dot(Vector v) {
    return (x * v.x + y * v.y);
  }

  /**
   * Checks whether this direction vector is linearly dependent on the direction vector dirV.
   *
   * @param dirV direction vector
   * @return linearly dependency
   */
  public boolean isLinearlyDependent(Vector dirV) {
    boolean linearlyDependent = false;

    if (length() == 0.0 ||
        dirV.length() == 0.0) {
      return false;
    }

    Vector v1 = normalize();
    Vector v2 = dirV.normalize();

    linearlyDependent = v1.equals(v2);

    if (!linearlyDependent) { // the direction may be reversed
      v1 = create(-v1.x, -v1.y);
      linearlyDependent = v1.equals(v2);
    }
    return linearlyDependent;
  }

  /**
   * Computes the determinant from this vector with vector v.
   *
   * @param v
   * @return the determinant
   */
  public double determinante(Vector v) {
    double det = (x * v.y - y * v.x);
    return det;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Vector vector = (Vector) o;
    return Double.compare(vector.x, x) == 0 &&
           Double.compare(vector.y, y) == 0;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y);
  }

  @Override
  public String toString() {
    return String.format("Vector(%.3f:%.3f)", x, y);
  }

  private static class SecureRandomHolder {
    private static SecureRandom random;

    static {
      try {
        random = SecureRandom.getInstanceStrong();
      }
      catch (NoSuchAlgorithmException e) {
        random = new SecureRandom(("" + LocalDateTime.now().toEpochSecond(UTC)).getBytes());
      }
    }
  }
}
