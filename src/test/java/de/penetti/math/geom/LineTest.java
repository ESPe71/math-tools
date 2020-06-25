package de.penetti.math.geom;


import org.junit.jupiter.api.Test;

import java.awt.geom.Line2D;

import static de.penetti.math.MathUtil.HALF_PI;
import static de.penetti.math.TestUtils.DELTA_6;
import static de.penetti.math.TestUtils.DELTA_ZERO;
import static de.penetti.math.geom.Line.LineStatus.*;
import static de.penetti.math.geom.Line.PointStatus.*;
import static org.junit.jupiter.api.Assertions.*;

public class LineTest
{
  @Test
  public void testConstructorWithNullLine()
  {
    assertThrows(NullPointerException.class, () -> {
      new Line(null);
    });
  }
  @Test
  public void testConstructorWithNullPoints_1()
  {
    assertThrows(NullPointerException.class, () -> {
      new Line(null);
    });
  }
  @Test
  public void testConstructorWithNullPoints_2()
  {
    assertThrows(NullPointerException.class, () -> {
      new Line(null, new Vector());
    });
  }
  @Test
  public void testConstructorWithNullPoints_3()
  {
    assertThrows(NullPointerException.class, () -> {
      new Line(new Vector(), null);
    });
  }
  
  @Test
  public void testGetOrigin()
  {
    Line line = new Line(new Vector(3.14, 5.3), new Vector());
    assertEquals(new Vector(3.14, 5.3), line.getOrigin());
  }

  @Test
  public void testGetDestination()
  {
    Line line = new Line(new Vector(), new Vector(3.14, 5.3));
    assertEquals(new Vector(3.14, 5.3), line.getDestination());
  }

  @Test
  public void testGetDirection()
  {
    Line line = new Line(new Vector(), new Vector(3.14, 5.3));
    assertEquals(new Vector(3.14, 5.3), line.getDirection());
  }

  @Test
  public void testLengthLine()
  {
    Line line = new Line(new Vector(), new Vector());
    assertEquals(.0, line.length());

    line = new Line(new Vector(), new Vector(2, 2));
    assertEquals(Math.sqrt(8), line.length());

    line = new Line(new Vector(2, 2), new Vector(6, 6));
    assertEquals(Math.sqrt(32), line.length());
  }

  @Test
  public void getSlope() {
    assertEquals(.0, new Line(new Vector(), new Vector(5, 0)).getSlope(), DELTA_ZERO);
    assertEquals(Double.POSITIVE_INFINITY, new Line(new Vector(), new Vector(0, 5)).getSlope(), DELTA_ZERO);
    assertEquals(Double.NEGATIVE_INFINITY, new Line(new Vector(), new Vector(0, -5)).getSlope(), DELTA_ZERO);

    assertEquals(2.5, new Line(new Vector(1, 2), new Vector(3, 7)).getSlope(), DELTA_ZERO);
    assertEquals(.4, new Line(new Vector(2, 1), new Vector(7, 3)).getSlope(), DELTA_ZERO);

    assertEquals(1.0, new Line(new Vector(), new Vector(5, 5)).getSlope(), DELTA_ZERO);
    assertEquals(1.0, new Line(new Vector(), new Vector(10, 10)).getSlope(), DELTA_ZERO);
    assertEquals(1.0, new Line(new Vector(5, 5), new Vector(10, 10)).getSlope(), DELTA_ZERO);

    assertEquals(1.0, new Line(new Vector(), new Vector(-5, -5)).getSlope(), DELTA_ZERO);
    assertEquals(1.0, new Line(new Vector(), new Vector(-10, -10)).getSlope(), DELTA_ZERO);
    assertEquals(1.0, new Line(new Vector(-5, -5), new Vector(-10, -10)).getSlope(), DELTA_ZERO);

    assertEquals(-1.0, new Line(new Vector(), new Vector(5, -5)).getSlope(), DELTA_ZERO);
    assertEquals(-1.0, new Line(new Vector(), new Vector(10, -10)).getSlope(), DELTA_ZERO);
    assertEquals(-1.0, new Line(new Vector(5, -5), new Vector(10, -10)).getSlope(), DELTA_ZERO);
  }

  @Test
  public void testAngle() {
    assertEquals(.0, new Line(new Vector(), new Vector(5, 0)).getAngle(), DELTA_ZERO);
    assertEquals(HALF_PI, new Line(new Vector(5, -5), new Vector(5, 0)).getAngle(), DELTA_ZERO);
    assertEquals(HALF_PI, new Line(new Vector(5, 5), new Vector(5, 0)).getAngle(), DELTA_ZERO);

    assertEquals(HALF_PI / 2, new Line(new Vector(), new Vector(5, 5)).getAngle(), DELTA_ZERO);
    assertEquals(HALF_PI / 2, new Line(new Vector(2, 2), new Vector()).getAngle(), DELTA_ZERO);

    assertEquals(HALF_PI, new Line(new Vector(), new Vector(0, 5)).getAngle(), DELTA_ZERO);
  }

  @Test
  public void testAngleWithLine() {
    assertEquals(HALF_PI, new Line(new Vector(), new Vector(5, 5)).getAngle(new Line(new Vector(), new Vector(-5, 5))), DELTA_ZERO);

    assertEquals(HALF_PI / 2, new Line(new Vector(), new Vector(0, 5)).getAngle(new Line(new Vector(), new Vector(5, 5))), DELTA_ZERO);
    assertEquals(HALF_PI / 2, new Line(new Vector(), new Vector(5, 5)).getAngle(new Line(new Vector(), new Vector(0, 5))), DELTA_ZERO);

    assertEquals(.0, new Line(new Vector(), new Vector(0, 5)).getAngle(new Line(new Vector(), new Vector(0, 15))), DELTA_ZERO);
    assertEquals(.0, new Line(new Vector(), new Vector(0, 5)).getAngle(new Line(new Vector(1, 0), new Vector(1, 15))), DELTA_ZERO);

    assertEquals(.0, new Line(new Vector(1, 5), new Vector(2, 7)).getAngle(new Line(new Vector(2, 6), new Vector(3, 8))), DELTA_ZERO);
  }

  @Test
  public void testAngleOfIntersection()
  {
    assertEquals(HALF_PI, Line.angleOfIntersection(2, -0.5), DELTA_ZERO);
    assertEquals(HALF_PI * .5, Line.angleOfIntersection(3, .5), DELTA_ZERO);
  }

  @Test
  public void testOrthogonal()
  {
    Vector pnt = new Line(new Vector(), new Vector(5, 5)).orthogonal(new Vector(2, 4));
    assertEquals(-1d, pnt.getX(), DELTA_ZERO);
    assertEquals(1., pnt.getY(), DELTA_ZERO);

    pnt = new Line(new Vector(4, 6), new Vector(9, 16)).orthogonal(new Vector(4, 16));
    assertEquals(-4., pnt.getX(), DELTA_ZERO);
    assertEquals(2., pnt.getY(), DELTA_ZERO);

    pnt = new Line(new Vector(3, -16), new Vector(7, -14)).orthogonal(new Vector(3, -11));
    assertEquals(-2., pnt.getX(), DELTA_ZERO);
    assertEquals(4., pnt.getY(), DELTA_ZERO);

    pnt = new Line(new Vector(3, -16), new Vector(7, -14)).orthogonal(new Vector(7, -19));
    assertEquals(2., pnt.getX(), DELTA_ZERO);
    assertEquals(-4., pnt.getY(), DELTA_ZERO);
  }

  @Test
  public void testNormal()
  {
    Vector pnt = new Line(1, -12, 4, -2).normal();
    assertEquals(-.957826, pnt.getX(), DELTA_6);
    assertEquals(0.287348, pnt.getY(), DELTA_6);

    pnt = new Line(0, 0, 4, 4).normal();
    assertEquals(-0.707107, pnt.getX(), DELTA_6);
    assertEquals(0.707107, pnt.getY(), DELTA_6);

    pnt = new Line(2, 0, 6, 4).normal();
    assertEquals(-0.707107, pnt.getX(), DELTA_6);
    assertEquals(0.707107, pnt.getY(), DELTA_6);

    pnt = new Line(0, 2, 4, 6).normal();
    assertEquals(-0.707107, pnt.getX(), DELTA_6);
    assertEquals(0.707107, pnt.getY(), DELTA_6);

    pnt = new Line(2, 10, 7, 8).normal();
    assertEquals(0.371391, pnt.getX(), DELTA_6);
    assertEquals(0.928477, pnt.getY(), DELTA_6);
  }

  @Test
  public void testClassify()
  {
    Line l = new Line(-12, -7, 7, 12);
    assertEquals(RIGHT,   l.classify(new Vector()));
    assertEquals(RIGHT,   l.classify(new Vector(2, 6)));
    assertEquals(RIGHT,   l.classify(new Vector(-14, -13)));
    assertEquals(LEFT,    l.classify(new Vector(-4, 4)));
    assertEquals(LEFT,    l.classify(new Vector(4, 10)));
    assertEquals(LEFT,    l.classify(new Vector(-16, -8)));
    assertEquals(BETWEEN, l.classify(new Vector(0, 5)));
    assertEquals(BETWEEN, l.classify(new Vector(-9, -4)));
    assertEquals(ORIGIN,  l.classify(new Vector(-12, -7)));
    assertEquals(DESTINATION, l.classify(new Vector(7, 12)));
    assertEquals(BEHIND,  l.classify(new Vector(-14, -9)));
    assertEquals(BEHIND,  l.classify(new Vector(-17, -12)));
    assertEquals(BEYOND,  l.classify(new Vector(10, 15)));
    assertEquals(BEYOND,  l.classify(new Vector(14, 19)));
  }

  @Test
  public void testSubdivide()
  {
    Line l = new Line(0, 0, 4, 4);

    Vector p = l.subdivide(.5);
    assertEquals(2, p.getX(), DELTA_ZERO);
    assertEquals(2, p.getY(), DELTA_ZERO);

    p = l.subdivide(2);
    assertEquals(8, p.getX(), DELTA_ZERO);
    assertEquals(8, p.getY(), DELTA_ZERO);

    p = l.subdivide(-.5);
    assertEquals(-2, p.getX(), DELTA_ZERO);
    assertEquals(-2, p.getY(), DELTA_ZERO);

    p = l.subdivide(-2);
    assertEquals(-8, p.getX(), DELTA_ZERO);
    assertEquals(-8, p.getY(), DELTA_ZERO);

    p = l.subdivide(0);
    assertEquals(0, p.getX(), DELTA_ZERO);
    assertEquals(0, p.getY(), DELTA_ZERO);

    p = l.subdivide(1.0);
    assertEquals(4, p.getX(), DELTA_ZERO);
    assertEquals(4, p.getY(), DELTA_ZERO);
  }

  @Test
  public void testLinesIntersection()
  {
    Line l1 = new Line(4, 10, 6, 14);
    Line l2 = new Line(2, 11, 7, 13);
    Line.Intersection s = l1.intersection(l2);
    assertEquals(SEGMENT_INTERSECTS, s.getLineStatus());
    assertEquals(5.125, s.getIntersection().get().getX(), DELTA_ZERO);
    assertEquals(12.25, s.getIntersection().get().getY(), DELTA_ZERO);

    l1 = new Line(6, 10, 8, 14);
    l2 = new Line(2, 11, 7, 13);
    s = l1.intersection(l2);
    assertEquals(LINE_INTERSECTS, s.getLineStatus());
    assertEquals(7.625, s.getIntersection().get().getX(), DELTA_ZERO);
    assertEquals(13.25, s.getIntersection().get().getY(), DELTA_ZERO);

    l1 = new Line(6, 10, 8, 14);
    l2 = new Line(4, 10, 6, 14);
    s = l1.intersection(l2);
    assertEquals(PARALLEL, s.getLineStatus());

    l1 = new Line(6, 10, 8, 14);
    l2 = new Line(4, 6, 10, 18);
    s = l1.intersection(l2);
    assertEquals(IDENTICAL, s.getLineStatus());

    l1 = new Line(-2, -18, 320514.2267375339, 334886.60298546473);
    l2 = new Line(8, -18, 8, -6);
    s = l1.intersection(l2);
    assertEquals(SEGMENT_INTERSECTS, s.getLineStatus());

    l1 = new Line(-46758.32608970703, 1065327.9000232674, -46757.32703884512, 1065327.9435821392);
    l2 = new Line(-905507.625, 1181660.0, -53350.90234375, 1218814.25);
    s = l1.intersection(l2);
    assertEquals(PARALLEL, s.getLineStatus());
  }

  @Test
  public void testEquals()
  {
    Line line = new Line(new Vector(), new Vector(3.14, 5.3));
    assertTrue(line.equals(new Line(new Vector(), new Vector(3.14, 5.3))));
    assertFalse(line.equals(new Line(new Vector(3.14, 5.3), new Vector())));
    assertFalse(line.equals(this));
    assertFalse(line.equals(null));
  }

  @Test
  public void testHashCode()
  {
    Line line = new Line(new Vector(), new Vector(3.14, 5.3));
    assertEquals(new Line(line).hashCode(), line.hashCode());
  }
  
}
