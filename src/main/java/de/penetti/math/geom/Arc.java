package de.penetti.math.geom;

import de.penetti.math.Tuple;

import static de.penetti.math.MathUtil.adjustAngle;

/**
 * @author Enrico
 */
public class Arc {
  private final Vector center;
  private final double xRadius;
  private final double yRadius;
  private final double startAngle; // [rad]
  private final double endAngle;   // [rad]

  public Arc(Vector center, double xRadius, double yRadius, double startAngle, double endAngle) {
    this.center = center;
    this.xRadius = xRadius;
    this.yRadius = yRadius;
    this.startAngle = adjustAngle(startAngle);
    this.endAngle = adjustAngle(endAngle);
  }

  public Arc(Vector center, double radius, double startAngle, double endAngle) {
    this(center, radius, radius, startAngle, endAngle);
  }

  /**
   * Es wird ein Kreisbogen anhand von Start- und Endpunkt und einer Ausbuchtung erzeugt.
   * In AutoCAD können z.B. LWPolyline und Vertex eine Ausbuchtung (Bulge; Gruppencode 42) haben.
   * <p>
   * Bulge ist das Mass für die Geometrie eines Bogensegmentes (Krümmung). Das Bulge ist der Tangens eines Viertels des eingeschlossenen
   * Winkels des Bogensegmentes. Negativ, falls der Bogen im Uhrzeigersinn verläuft. Ein Bulge von 0 bedeutet eine gerade Linie und ein
   * Bulge von 1 ist ein Halbkreis.</p>
   * <p>
   * Die Ermittlung des Kreisbogens erfolgt nach dem Algorithmus von Duff Kurland - Autodesk, Inc. (cvtbulge).
   * </p>
   *
   * @param start Startpunkt des Kreisbogens
   * @param end   Endpunkt des Kreisbogens
   * @param bulge Ausbuchtung
   * @see <a href="http://www.faqs.org/faqs/CAD/autolisp-faq/part2/section-6.html">cvtbulge</a>
   */
  public Arc(Vector start, Vector end, double bulge) {
    double cotbce = (1.0 / bulge - bulge) / 2.0;
    this.center = new Vector((start.getX() + end.getX() - ((end.getY() - start.getY()) * cotbce)) / 2.0,
                             (start.getY() + end.getY() + ((end.getX() - start.getX()) * cotbce)) / 2.0);
    Tuple<Double> angles = calculateAngles(center, start, end, bulge < .0); // angles.0 => start; angles.1 => end
    this.xRadius = center.length(start);
    this.yRadius = this.xRadius;
    this.startAngle = angles.get(0);
    this.endAngle = angles.get(1);
  }

  private Tuple<Double> calculateAngles(Vector mid, Vector start, Vector end, boolean swapAngles) {
    Tuple<Double> angles = new Tuple<>(adjustAngle(Math.atan2(start.getY() - mid.getY(), start.getX() - mid.getX())),
                                       adjustAngle(Math.atan2(end.getY() - mid.getY(), end.getX() - mid.getX())));
    if (swapAngles) {
      angles = new Tuple<>(angles.get(1), angles.get(0));
    }
    return angles;
  }

  /**
   * Gibt den Punkt des Zentrums des Kreises zurück, den der Kreisbogen aufspannt.
   *
   * @return Zentrum
   */
  public Vector getCenter() {
    return center;
  }

  /**
   * Gibt den xRadius zurück.
   *
   * @return radius
   */
  public double getXRadius() {
    return xRadius;
  }

  public double getYRadius() {
    return yRadius;
  }

  /**
   * Gibt den Startwinkel zurück.
   *
   * @return Startwinkel in Radians
   */
  public double getStartAngle() {
    return startAngle;
  }

  /**
   * Gibt den Endwinkel zurück.
   *
   * @return Endwinkel in Radians
   */
  public double getEndAngle() {
    return endAngle;
  }

  /**
   * Gibt die Ausdehnung von Start- und Endwinkel zurück. Die Ausdehnung ist die Subtraktion des Startwinkels vom Endwinkel. Sie ist immer
   * positiv.
   *
   * @return Ausdehnung der Winkel in Radians
   */
  public double getExtend() {
    double extend = endAngle - startAngle;
    extend = adjustAngle(extend);
    return extend;
  }

  /**
   * TODO: Ermittelt die "Bulge" eines Kreisbogens?
   * <pre>
   * //Kurve
   * xControl = 2 * (xM - 0.25 * x1 - 0.25 * x2); //x1, x2 sind X-Koordinaten der Anfangs- und Endpunkte der Kurve,
   * //xM ist X-Koordinate des "Mittelpunktes" der Kurve
   * yControl = 2 * (yM - 0.25 * y1 - 0.25 * y2);
   * QuadCurve2D curve = new QuadCurve2D.Double(x1, y1, xControl, yControl, x2, y2);
   * </pre>
   *
   * @return
   */
  public double getBulge() {
    throw new UnsupportedOperationException("double " + Arc.class.getName() + ".getBulge() is not yet implemented.");
  }
}
