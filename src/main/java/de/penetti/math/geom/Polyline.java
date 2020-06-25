package de.penetti.math.geom;

import java.io.Serializable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Stream;

/**
 * This class is immutable
 *
 * @author Enrico
 */
public class Polyline implements Serializable {
  private static final long serialVersionUID = 5085818176600234063L;

  // TODO: statt nur Points evtl. Points und bulges und closed?
  // TODO: evtl. auch QuadCurve und CubicCurve
  private final List<Vector> vectors = new ArrayList<>();
  private final boolean closed;

  Polyline(List<Vector> vectors) {
    this(false, vectors);
  }

  Polyline(boolean closed, List<Vector> vectors) {
    Objects.requireNonNull(vectors);
    vectors.forEach(p -> Objects.requireNonNull(p));
    if (vectors.size() < 2) {
      throw new IllegalArgumentException("A polyline requires a minimum of two points.");
    }
    vectors.forEach(p -> this.vectors.add(new Vector(p)));
    this.closed = closed;
  }

  Polyline(Vector p1, Vector p2, Vector... px) {
    this(false, p1, p2, px);
  }

  Polyline(boolean closed, Vector p1, Vector p2, Vector... px) {
    Objects.requireNonNull(p1);
    Objects.requireNonNull(p2);
    Objects.requireNonNull(px);
    Arrays.stream(px).forEach(p -> Objects.requireNonNull(p));
    vectors.add(new Vector(p1));
    vectors.add(new Vector(p2));
    Arrays.stream(px).forEach(p -> vectors.add(new Vector(p)));
    this.closed = closed;
  }

  Polyline(boolean closed, Polyline polyline) {
    vectors.addAll(new ArrayList<>(polyline.vectors));
    this.closed = closed;
  }

  public static Builder.MoveToOrDirection startAt(Vector v) {
    return new Builder.PolylineBuilder(v);
  }

  /**
   * Entfernt überflüssige Ecken der Polyline.
   * Eine Ecke ist dann überflüssig, wenn der Winkel der zugehörigen Kanten kleiner als {@code tolerance} ist.
   *
   * @param tolerance [rad]
   * @return
   * @author wuesten
   */
  public static Polyline eleminateNonCorners(double tolerance) {
    return null; // TODO
  }

  public int size() {
    return vectors.size();
  }

  public Stream<Vector> stream() {
    return vectors.stream();
  }

  /**
   * Prüft, ob der gegebene Punkt innerhalb der geschlossenen Polyline, also einem Polygon, ist.
   * Der Punkt ist ebenfalls innerhalb, wenn er auf einer Strecke oder einem Punkt des Polygons ist.
   * Es wird angenommen, dass die Polyline geschlossen ist, also die Polyline ein Polygon ist.
   * Die Eigenschaft {@code closed} wird nicht berücksicht.
   * Algorithmus ist Punkt-in-Polygon-Test nach Jordan nach https://de.wikipedia.org/wiki/Punkt-in-Polygon-Test_nach_Jordan.
   *
   * @param v
   * @return
   */
  public boolean contains(Vector v) {
    int t = -1;
    int n = size();
    for (int i = 0; i < n; i++) {
      Vector a = vectors.get(i);
      Vector b = vectors.get((i + 1) % n);
      t = t * KreuzProdTest(v, a, b);
      if (t == 0) {
        break; // v liegt direkt auf einer Kante bzw. einem Eckpunkt des Polygons
      }
    }
    return t >= 0;
  }

  private int KreuzProdTest(Vector v, Vector a, Vector b) {
    if (a.getY() > b.getY()) {
      Vector tmp = a;
      a = b;
      b = tmp;
    }
    if (v.getY() <= a.getY() ||
        v.getY() > b.getY()) {
      return 1;
    }
    double delta = (a.getX() - v.getX()) * (b.getY() - v.getY())
                 - (a.getY() - v.getY()) * (b.getX() - v.getX());
    if (delta > .0) {
      return 1;
    }
    else if (delta < .0) {
      return -1;
    }
    return 0;
  }

  public boolean containsVector(Vector v) {
    return vectors.parallelStream().anyMatch(p -> p.equals(v));
  }

  /**
   * Ermittelt die Gesamtlänge der Polyline.
   *
   * @return
   */
  public double length() {
    return runThroughSum(closed ? size() : size() - 1, (a, b) -> a.length(b));
  }

  /**
   * Ermittelt die Fläche der geschlossenen Polyline, also des Polygons.
   * Es wird die Gaußsche Trapezformel nach https://de.wikipedia.org/wiki/Gau%C3%9Fsche_Trapezformel verwendet.
   *
   * @return
   */
  public double area() {
    return Math.abs(runThroughSum(size(), (a, b) -> (a.getY() + b.getY()) * (a.getX() - b.getX())) / 2);
  }

  /**
   * Tests, if this polyline is counter clock wise.
   * If this polyline consists only of two points, this method returns always {@code true}.
   *
   * @return true if this polyline counter clock wise, false otherwise (clock wise)
   */
  public boolean isCCW() {
    return runThroughSum(size(), (a, b) -> (b.getX() - a.getX()) * (b.getY() + a.getY())) <= .0;
  }

  private double runThroughSum(int n, BiFunction<Vector, Vector, Double> function) {
    double result = .0;
    for (int i = 0; i < n; i++) {
      Vector a = vectors.get(i);
      Vector b = vectors.get((i + 1) % size());
      result += function.apply(a, b);
    }
    return result;
  }

  /**
   * Sortiert die Punkte der Polyline mathematisch positiv, d.h. gegen den Uhrzeigersinn.
   */
  public Polyline ccw() {
    if (isCCW()) {
      return this;
    }
    else {
      return reverse();
    }
  }

  /**
   * Sortiert die Punkte der Polyline mathematisch negativ, d.h. mit dem Uhrzeigersinn.
   */
  public Polyline cw() {
    if (isCCW()) {
      return reverse();
    }
    else {
      return this;
    }
  }

  private Polyline reverse() {
    List<Vector> reverse = new ArrayList<>(vectors);
    Collections.reverse(reverse);
    return new Polyline(closed, reverse);
  }

  /**
   * Berechnet den Schwerpunkt der geschlossenen Polyline.
   *
   * @return
   */
  public Vector centroid() {
    return CentroidHolder.ALGORITHM.centroid(this);
  }

  /**
   * Ermittelt die Schnittpunkte der Polyline mit einer Line
   *
   * @param line
   * @return
   */
  public Collection<LineIntersection> intersection(Line line) {
    return intersection(line, null);
  }

  /**
   * Ermittelt die Schnittpunkte der Polyline mit der gegebenen Polyline
   *
   * @param polyline
   * @return
   */
  public Collection<PolylineIntersection> intersection(Polyline polyline) {
    Collection<PolylineIntersection> intersections = new ArrayList<>();
    for (int i = 0; i < (closed ? size() : size() - 1); i++) {
      Vector a = vectors.get(i);
      Vector b = vectors.get((i + 1) % size());
      Line pLine = new Line(a, b);
      Collection<PolylineIntersection> intersection = polyline.intersection(pLine, this);
      intersections.addAll(intersection);
    }
    return intersections;
  }

  private <T extends Line.AbstractIntersection> Collection<T> intersection(Line line, Polyline polyline) {
    Collection<T> intersections = new ArrayList<>();
    for (int i = 0; i < (closed ? size() : size() - 1); i++) {
      Vector a = vectors.get(i);
      Vector b = vectors.get((i + 1) % size());
      Line pLine = new Line(a, b);
      T intersection = polyline == null
                     ? (T) new LineIntersection(pLine.intersection(line), this)
                     : (T) new PolylineIntersection(line.intersection(pLine), polyline, this);
      intersections.add(intersection);
    }
    return intersections;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Polyline polyline = (Polyline) o;
    return closed == polyline.closed &&
           vectors.equals(polyline.vectors);
  }

  @Override
  public int hashCode() {
    return Objects.hash(vectors, closed);
  }

  @Override
  public String toString() {
    return String.format("Polyline@%s(%d Vectors; %s)", Integer.toHexString(hashCode()), size(), closed ? "closed" : "open");
  }

  private interface CentroidAlgorithm {
    Vector centroid(Polyline polyline);
  }

  public static final class LineIntersection extends Line.AbstractIntersection {
    private final Polyline polyline;

    private LineIntersection(Line.Intersection lIntersetion, Polyline polyline) {
      super(lIntersetion.getLine1(), lIntersetion.getLine2(),
            lIntersetion.getIntersection().orElse(null), lIntersetion.getLineStatus());
      this.polyline = polyline;
    }

    public Polyline getPolyline() {
      return polyline;
    }

    @Override
    public String toString() {
      return "Intersection{" +
        polyline +
        "; " + getLine1() +
        "; " + getLine2() +
        "; " + getIntersection() +
        "; " + getLineStatus() +
        '}';
    }
  }

  public static final class PolylineIntersection extends Line.AbstractIntersection {
    private final Polyline polyline1;
    private final Polyline polyline2;

    private PolylineIntersection(Line.Intersection lIntersetion, Polyline polyline1, Polyline polyline2) {
      super(lIntersetion.getLine1(), lIntersetion.getLine2(),
            lIntersetion.getIntersection().orElse(null), lIntersetion.getLineStatus());
      this.polyline1 = polyline1;
      this.polyline2 = polyline2;
    }

    public Polyline getPolyline1() {
      return polyline1;
    }

    public Polyline getPolyline2() {
      return polyline2;
    }

    @Override
    public String toString() {
      return "Intersection{" +
        "polyline1=" + polyline1 +
        ", polyline2=" + polyline2 +
        ", line1=" + getLine1() +
        ", line2=" + getLine2() +
        ", intersection=" + getIntersection() +
        ", lineStatus=" + getLineStatus() +
        '}';
    }
  }

  private static final class CentroidHolder {
    private static final CentroidAlgorithm ALGORITHM = new CentroidSimple();
  }

  /**
   * Es wird ein simpler Algorithmus verwendet, der den Durchschnitt aller Punkte verwendet.
   */
  private static class CentroidSimple implements CentroidAlgorithm {
    @Override
    public Vector centroid(Polyline polyline) {
      double centroidX = 0;
      double centroidY = 0;
      for (Vector v : polyline.vectors) {
        centroidX += v.getX();
        centroidY += v.getY();
      }
      int n = polyline.size();
      return new Vector(centroidX / n, centroidY / n);
    }
  }

  /**
   * Algorithmus nach https://de.wikipedia.org/wiki/Geometrischer_Schwerpunkt#Polygon
   * Die Fläche des Polygons muss hierbei ungleich 0 sein.
   */
  private static class CentroidCenterOfGravity implements CentroidAlgorithm {
    public Vector centroid(Polyline polyline) {
      double area = polyline.area();
      double centroidX = 0;
      double centroidY = 0;
      int n = polyline.size();
      for (int i = 0; i < n; i++) {
        Vector a = polyline.vectors.get(i);
        Vector b = polyline.vectors.get((i + 1) % n);
        centroidX += (a.getX() + b.getX()) * (a.getX() * b.getY() - b.getX() * a.getY());
        centroidY += (a.getY() + b.getY()) * (a.getX() * b.getY() - b.getX() * a.getY());
      }
      Vector centroid = new Vector(centroidX, centroidY);
      centroid = centroid.mul(1. / (6. * area));
      return centroid;
    }
  }

  public static final class Builder {
    public interface MoveTo {
      MoveToOrDirectionOrBuild moveTo(Vector v);
    }

    public interface Direction {
      MoveToOrDirectionOrBuild withDirection(Vector v);
    }

    public interface MoveToOrDirection extends MoveTo, Direction {
    }

    public interface Build {
      Polyline build();

      Polyline build(boolean close);
    }

    public interface MoveToOrDirectionOrBuild extends MoveToOrDirection, Build {
    }

    private final static class PolylineBuilder implements MoveToOrDirectionOrBuild {
      List<Vector> vectors = new ArrayList<>();

      public PolylineBuilder(Vector v) {
        vectors.add(v);
      }

      @Override
      public MoveToOrDirectionOrBuild moveTo(Vector v) {
        vectors.add(v);
        return this;
      }

      @Override
      public MoveToOrDirectionOrBuild withDirection(Vector v) {
        Vector l = vectors.get(vectors.size() - 1);
        vectors.add(l.add(v));
        return this;
      }

      @Override
      public Polyline build() {
        return new Polyline(vectors);
      }

      @Override
      public Polyline build(boolean close) {
        return new Polyline(close, vectors);
      }
    }
  }
}
