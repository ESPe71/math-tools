package de.penetti.math.geom;

import Jama.Matrix;
import de.penetti.math.NumberRange;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import static de.penetti.math.MathUtil.HALF_PI;
import static de.penetti.math.MathUtil.PI;

/**
 * This class is immutable.
 *
 * @author Enrico
 */
public final class Line implements Serializable {
  private static final long serialVersionUID = -999701483216745289L;
  private final static double EPSILON = 1e-10;
  private final Vector origin;
  private final Vector destination;

  Line(double originX, double originY, double destionationX, double destinationY) {
    this.origin = new Vector(originX, originY);
    this.destination = new Vector(destionationX, destinationY);
  }

  Line(Vector origin, Vector destination) {
    Objects.requireNonNull(origin);
    Objects.requireNonNull(destination);
    this.origin = new Vector(origin);
    this.destination = new Vector(destination);
  }

  Line(Line line) {
    Objects.requireNonNull(line);
    this.origin = new Vector(line.getOrigin());
    this.destination = new Vector(line.getDestination());
  }

  public static Builder.DestinationOrDirection withOrigin(Vector origin) {
    return new Builder.LineBuilder(origin);
  }

  public static Line fromLine(Line line) {
    return new Line(line);
  }

  /**
   * Berechnet den Schnittwinkel zweier Geraden mit Hilfe der Anstiege m.
   * <br>alpha = atan( (m1 - m2) / (1 + m1 m2) )
   * Falls die Geraden Parallel sind, wird 0.0 zurückgegeben.
   *
   * @param m1 Steigung der Geraden g1
   * @param m2 Steigung der Geraden g2
   * @return Schnittwinkel in rad
   */
  static double angleOfIntersection(double m1, double m2) {
    double dRet = Math.atan((m1 - m2) / (1 + m1 * m2));
    if (Double.compare(m1, m2) == 0) {
      dRet = 0.0;
    }
    else if (Double.isNaN(dRet)) {
      if ((Double.isInfinite(m1) && Double.compare(m2, .0) == 0) ||
        (Double.isInfinite(m2) && Double.compare(m1, .0) == 0) ||
        (Double.compare(m1, -1 / m2) == 0) ||
        (Double.compare(m2, -1 / m1) == 0)) {
        dRet = HALF_PI;
      }
      else if (Double.isInfinite(m1)) {
        dRet = HALF_PI - Math.atan(m2);
      }
      else if (Double.isInfinite(m2)) {
        dRet = HALF_PI - Math.atan(m1);
      }
    }
    if (dRet < 0) {
      dRet += PI;
    }
    return dRet;
  }

  public Vector getOrigin() {
    return origin;
  }

  public Vector getDestination() {
    return destination;
  }

  public Vector getDirection() {
    return destination.sub(origin);
  }

  public double length() {
    return origin.length(destination);
  }

  /**
   * Computes the slope of this line.
   * Ermittelt den Anstieg der Geraden.
   *
   * @return the slope of the line
   */
  public double getSlope() {
    return (destination.getY() - origin.getY()) / (destination.getX() - origin.getX());
  }

  /**
   * Computes the angle [rad] against the x-axis.
   *
   * @return
   */
  public double getAngle() {
    return angleOfIntersection(getSlope(), .0);
  }

  /**
   * Computes the angle [rad] against the given line.
   *
   * @param line
   * @return
   */
  public double getAngle(Line line) {
    return angleOfIntersection(getSlope(), line.getSlope());
  }

  /**
   * Calculates the vector that lies orthogonally on the line and is bounded by v.
   */
  public Vector orthogonal(Vector v) {
    Vector a = getDirection();
    double l = v.sub(origin).dot(a) / a.dot(a);
    Vector pf = origin.add(a.mul(l));
    Vector d = v.sub(pf);
    return d;
  }

  /**
   * Calculates the normal vector of the line.
   * The normal vector points from (origin, destination) upwards. I.e. if the rise is negative,
   * x is always positive, if the rise is positive, x is always negative.
   *
   * @see #orthogonal(Vector)
   */
  public Vector normal() {
    Vector normal = null;
    while (normal == null) {
      Vector rand = Vector.random(1, 100_000d);
      normal = orthogonal(rand).normalize();
      if (Double.isNaN(normal.getX()) ||
        Double.isNaN(normal.getY()))
        normal = null;
    }
    if (origin.getX() < destination.getX()) {
      if (origin.getY() < destination.getY()) {
        if (normal.getX() > 0) {
          normal = normal.mul(-1.00);
        }
      }
    }
    else {
      if (origin.getY() > destination.getY()) {
        if (normal.getX() < 0) {
          normal = normal.mul(-1.00);
        }
      }
    }
    return normal;
  }

  public boolean contains(Vector v) {
    PointStatus ps = classify(v);
    return ps == PointStatus.ORIGIN ||
           ps == PointStatus.DESTINATION ||
           ps == PointStatus.BETWEEN;
  }

  public boolean containsVector(Vector v) {
    return origin.equals(v) ||
           destination.equals(v);
  }

  /**
   * Classifies the point v, relative to the directed line segment (origin, destination).
   *
   * @return LEFT, RIGHT, BEHIND, BEYOND, ORIGIN, DESTINATION oder BETWEEN
   */
  public PointStatus classify(Vector v) {
    Vector a = getDirection();
    Vector b = v.sub(origin);
    double sa = a.determinante(b);
    if (sa > 0.0) {
      return PointStatus.LEFT;
    }
    if (sa < 0.0) {
      return PointStatus.RIGHT;
    }
    if ((a.getX() * b.getX() < 0.0) ||
        (a.getY() * b.getY() < 0.0)) {
      return PointStatus.BEHIND;
    }
    if (a.length() < b.length()) {
      return PointStatus.BEYOND;
    }
    if (origin.equals(v)) {
      return PointStatus.ORIGIN;
    }
    if (destination.equals(v)) {
      return PointStatus.DESTINATION;
    }
    return PointStatus.BETWEEN;
  }

  /**
   * Returns the point t on the straight line (p1, p2).
   * The parameter form of the straight line equation is used: <br>
   * P(t) = a + t (b - a)
   * <p>You can split the line at half:
   * <pre>
   *   Line line = ...
   *   Vector v = line.subdivide(.5);
   *   Line leftLine = Line.withOrigin(line.getOrigin).withDestionation(v);
   *   Line rightLine = Line.withOrigin(v).withDestination(line.getDestination);
   * </pre>
   * </p>
   *
   * @param t
   * @return P(t)
   */
  public Vector subdivide(double t) {
    Vector ba = getDirection(); // (b - a)
    Vector tba = ba.mul(t); // t ( b - a)
    return origin.add(tba);
  }

  /**
   * Determines whether or how this straight line intersects with the straight line.
   * Returns the {@link Intersection} whether the two line segments intersect, whether
   * the straight lines intersect, or whether the two straight lines are identical or parallel.
   * The intersection, if any, is also in {@link Intersection}.
   *
   * @param line
   * @return
   */
  public Intersection intersection(Line line) {
    LineStatus lineStatus = LineStatus.DEFICIENT;
    Vector intersection = null;
    try {
      Vector dir1 = getDirection().normalize();
      Vector dir2 = line.getDirection().normalize();
      double det = dir1.determinante(dir2);
      if (Math.abs(det) < EPSILON) { // if(getDirection().isLinearlyDependent(line.getDirection()))
        // Geraden sind identisch oder parallel, wenn die Richtungsvektoren linear abhängig sind
        // bzw wenn die Determinante der Richtungsvektoren gegen 0 geht
        // ε für Ungenauigkeit bei double-Berechnungen
        if (distance(line.getOrigin()) < EPSILON) {
          lineStatus = LineStatus.IDENTICAL; // Geraden sind identisch
        }
        else {
          lineStatus = LineStatus.PARALLEL;  // Geraden verlaufen parallel zueinander
        }
      }
      else { // Geraden schneiden sich
        // Aufstellen der Matrix aus den Richtungsvektoren
        Matrix a = new Matrix(new double[][]{{dir1.getX(), -dir2.getX()},
                                             {dir1.getY(), -dir2.getY()}});
        // Aufstellen des Lösungsvektors aus den Ortsvektoren
        Matrix b = new Matrix(new double[][]{{-origin.getX() + line.origin.getX()},
                                             {-origin.getY() + line.origin.getY()}});
        // und Lösen des GLS
        Matrix x = a.solve(b);

        // λ ermitteln
        double λ = x.get(1, 0);

        // Prüfen, ob für λ und µ der gleiche Schnittpunkt berechnet wird durch
        // Einsetzen von λ in die jeweilige vektorielle Geradengleichung
        double ax = line.origin.getX() + λ * dir2.getX();
        double ay = line.origin.getY() + λ * dir2.getY();

        // Geraden haben einen Schnittpunkt miteinander
        // Einsetzen von λ in die Geradengleichung, um die Koordinaten des Schnittpunktes zu bestimmen
        intersection = new Vector(ax, ay);
        lineStatus = LineStatus.LINE_INTERSECTS;

        // Test, ob Schnittpunkt direkt auf der Strecke
        Vector endPnt1 = new Vector(origin.getX() + getDirection().getX(), origin.getY() + getDirection().getY());
        Vector endPnt2 = new Vector(line.origin.getX() + line.getDirection().getX(), line.origin.getY() + line.getDirection().getY());
        NumberRange<Double> r1x = new NumberRange<>(Math.min(origin.getX(), endPnt1.getX()), Math.max(origin.getX(), endPnt1.getX()));
        NumberRange<Double> r1y = new NumberRange<>(Math.min(origin.getY(), endPnt1.getY()), Math.max(origin.getY(), endPnt1.getY()));
        NumberRange<Double> r2x = new NumberRange<>(Math.min(line.origin.getX(), endPnt2.getX()), Math.max(line.origin.getX(), endPnt2.getX()));
        NumberRange<Double> r2y = new NumberRange<>(Math.min(line.origin.getY(), endPnt2.getY()), Math.max(line.origin.getY(), endPnt2.getY()));
        if (r1x.contains(intersection.getX()) &&
            r1y.contains(intersection.getY()) &&
            r2x.contains(intersection.getX()) &&
            r2y.contains(intersection.getY())) {
          lineStatus = LineStatus.SEGMENT_INTERSECTS;
        }
      }
    }
    catch (Exception e) {
      System.out.println(e.getClass().getSimpleName() + ": " + e.getLocalizedMessage());
    }
    return new Intersection(this, line, intersection, lineStatus);
  }

  /**
   * Gets the distance from vector v to this line.
   *
   * @param v
   * @return
   */
  public double distance(Vector v) {
    return orthogonal(v).length();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Line line = (Line) o;
    return origin.equals(line.origin) &&
           destination.equals(line.destination);
  }

  @Override
  public int hashCode() {
    return Objects.hash(origin, destination);
  }

  @Override
  public String toString() {
    return String.format("Line(%s:%s)", origin, destination);
  }

  public enum PointStatus {
    LEFT,               // Der Punkt liegt links von der Geraden(p1, p2)
    RIGHT,              // Der Punkt liegt rechts von der Geraden(p1, p2)
    BEHIND,
    BEYOND,
    ORIGIN,            // Der Punkt liegt auf p1 der Geraden(p1, p2)
    DESTINATION,       // Der Punkt liegt auf p2 der Geraden(p1, p2)
    BETWEEN            // Der Punkt liegt auf der Geraden(p1, p2) und zwischen ORIGIN und DESTINATION
  }

  public enum LineStatus {
    DEFICIENT,          // Fehler
    IDENTICAL,          // Geraden sind identisch
    PARALLEL,           // Geraden sind parallel
    LINE_INTERSECTS,    // Geraden besitzen einen Schnittpunkt; Dieser Status schließt SEGMENT_INTERSECTS aus
    SEGMENT_INTERSECTS, // Strecken besitzen einen Schnittpunkt; Dieser Status schließt LINE_INTERSECTS mit ein
  }

  public static final class Intersection extends AbstractIntersection {
    private Intersection(Line line1, Line line2, Vector intersection, LineStatus lineStatus) {
      super(line1, line2, intersection, lineStatus);
    }
  }

  static class AbstractIntersection {
    private final Line line1;
    private final Line line2;
    private final Optional<Vector> intersection;
    private final LineStatus lineStatus;

    AbstractIntersection(Line line1, Line line2, Vector intersection, LineStatus lineStatus) {
      this.line1 = line1;
      this.line2 = line2;
      this.intersection = Optional.ofNullable(intersection);
      this.lineStatus = lineStatus;
    }

    public Line getLine1() {
      return line1;
    }

    public Line getLine2() {
      return line2;
    }

    public Optional<Vector> getIntersection() {
      return intersection;
    }

    public LineStatus getLineStatus() {
      return lineStatus;
    }

    @Override
    public String toString() {
      return "Intersection{" +
        "line1=" + line1 +
        ", line2=" + line2 +
        ", intersection=" + intersection +
        ", lineStatus=" + lineStatus +
        '}';
    }
  }

  public final static class Builder {
    private Builder() {
    }

    public interface Destination {
      Line withDestination(Vector destination);
    }

    public interface Direction {
      Line withDirection(Vector direction);
    }

    public interface DestinationOrDirection extends Destination, Direction {
    }

    private final static class LineBuilder implements DestinationOrDirection {
      private final Vector origin;

      private LineBuilder(Vector origin) {
        this.origin = origin;
      }

      @Override
      public Line withDestination(Vector destination) {
        return new Line(origin, destination);
      }

      @Override
      public Line withDirection(Vector direction) {
        return new Line(origin, origin.add(direction));
      }
    }
  }
}
