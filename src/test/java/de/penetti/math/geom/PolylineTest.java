/*
 * This library is released under the LGPL 2.1 licence.
 * You may therefore freely use it in both non-commerical and commercial applications
 * and you don't need to open up your source code.
 * Improvements made to this library should however be returned to the project
 * for the benefit of everyone.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package de.penetti.math.geom;


import org.junit.jupiter.api.Test;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static de.penetti.math.TestUtils.DELTA_ZERO;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Enrico S. Penetti (enrico@penetti.de)
 */
public class PolylineTest {
  private static final Polyline polyline;
  static {
    // Werte muessen fuer die Tests im Uhrzeigersinn angegeben werden (siehe testSetCCW())
    polyline = new Polyline(true,
                            new Vector(-4, 9),
                            new Vector(-4, 13),
                            new Vector(-7, 15),
                            new Vector(-9, 11),
                            new Vector(-12, 19),
                            new Vector(4, 18),
                            new Vector(14, 14),
                            new Vector(10, 14),
                            new Vector(13, 10),
                            new Vector(11, 7),
                            new Vector(9, 4),
                            new Vector(12, 0),
                            new Vector(4, -2),
                            new Vector(15, -3),
                            new Vector(12, -7),
                            new Vector(13, -16),
                            new Vector(8, -18),
                            new Vector(8, -6),
                            new Vector(1, -12),
                            new Vector(-6, -14),
                            new Vector(-14, -11),
                            new Vector(-9, -4),
                            new Vector(-12, 5));
  }

  @Test
  public void testConstructorWithList() {
    List<Vector> vectors = new ArrayList<>();
    vectors.add(new Vector());
    vectors.add(new Vector(2, 0));
    vectors.add(new Vector(2, 2));
    vectors.add(new Vector(0, 2));
    vectors.add(new Vector());
    new Polyline(vectors);
  }

  @Test
  public void testConstructorWithNullList() {
    assertThrows(NullPointerException.class, () -> {
      new Polyline(null);
    });
  }

  @Test
  public void testConstructorWith0PointsList() {
    assertThrows(IllegalArgumentException.class, () -> {
      List<Vector> vectors = new ArrayList<>();
      new Polyline(vectors);
    });
  }

  @Test
  public void testConstructorWith1PointsList() {
    assertThrows(IllegalArgumentException.class, () -> {
      List<Vector> vectors = new ArrayList<>();
      vectors.add(new Vector());
      new Polyline(vectors);
    });
  }

  @Test
  public void testConstructorWith2PointsList() {
    List<Vector> vectors = new ArrayList<>();
    vectors.add(new Vector());
    vectors.add(new Vector(2, 0));
    new Polyline(vectors);
  }

  @Test
  public void testConstructorWithNullPointInList() {
    assertThrows(NullPointerException.class, () -> {
      List<Vector> vectors = new ArrayList<>();
      vectors.add(new Vector());
      vectors.add(new Vector(2, 0));
      vectors.add(new Vector(2, 2));
      vectors.add(null);
      vectors.add(new Vector(0, 2));
      vectors.add(new Vector());
      new Polyline(vectors);
    });
  }

  @Test
  public void testConstructor() {
    new Polyline(new Vector(), new Vector(2, 0), new Vector(2, 2), new Vector(0, 2), new Vector());
  }

  @Test
  public void testConstructorWithNull_0() {
    assertThrows(NullPointerException.class, () -> {
      new Polyline(null, new Vector(2, 0), new Vector(2, 2), new Vector(0, 2), new Vector());
    });
  }

  @Test
  public void testConstructorWithNull_1() {
    assertThrows(NullPointerException.class, () -> {
      new Polyline(new Vector(), null, new Vector(2, 2), new Vector(0, 2), new Vector());
    });
  }

  @Test
  public void testConstructorWithNull_2() {
    assertThrows(NullPointerException.class, () -> {
      new Polyline(new Vector(), new Vector(2, 0), (Vector) null);
    });
  }

  @Test
  public void testConstructorWithNull_3() {
    assertThrows(NullPointerException.class, () -> {
      new Polyline(new Vector(), new Vector(2, 0), (Vector[]) null);
    });
  }

  @Test
  public void testConstructorWithNull_4() {
    assertThrows(NullPointerException.class, () -> {
      new Polyline(new Vector(), new Vector(2, 0), new Vector(2, 2), null, new Vector(0, 2), new Vector());
    });
  }

  @Test
  public void testSize() {
    List<Vector> vectors = new ArrayList<>();
    vectors.add(new Vector());
    vectors.add(new Vector(2, 0));
    vectors.add(new Vector(2, 2));
    vectors.add(new Vector(0, 2));
    vectors.add(new Vector());
    Polyline pl = new Polyline(vectors);

    assertEquals(5, pl.size());

    vectors.remove(2);
    vectors.remove(3);
    assertEquals(5, pl.size());
  }

  @Test
  public void testStream() {
    List<Vector> vectors = new ArrayList<>();
    vectors.add(new Vector());
    vectors.add(new Vector(2, 0));
    vectors.add(new Vector(2, 2));
    vectors.add(new Vector(0, 2));
    vectors.add(new Vector());
    Polyline pl = new Polyline(vectors);
    int[] i = {0};
    pl.stream().forEach(p -> {
      if (!p.equals(vectors.get(i[0]++)))
        fail("index " + (i[0] - 1) + ": " + p);
    });

    List<Vector> p2 = new ArrayList<>(vectors);
    vectors.remove(2);
    vectors.remove(3);
    i[0] = 0;
    pl.stream().forEach(p -> {
      if (!p.equals(p2.get(i[0]++)))
        fail("index " + (i[0] - 1) + ": " + p);
    });
  }

  @Test
  void testLength() {
    Polyline pl = new Polyline(new Vector(), new Vector(2, 0), new Vector(2,2), new Vector(0, 2));
    assertEquals(6d, pl.length());

    pl = new Polyline(true, new Vector(), new Vector(2, 0), new Vector(2,2), new Vector(0, 2));
    assertEquals(8d, pl.length());

    pl = new Polyline(new Vector(), new Vector(2, 0), new Vector(2,2), new Vector(0, 2), new Vector());
    assertEquals(8d, pl.length());
  }

  @Test
  public void testContains()
  {
    assertEquals(true,  polyline.contains(new Vector()));
    assertEquals(false, polyline.contains(new Vector(20, 20)));
    assertEquals(false, polyline.contains(new Vector(-7, 13)));
    assertEquals(false, polyline.contains(new Vector(6, -2)));
    assertEquals(true,  polyline.contains(new Vector(9, -17)));
    assertEquals(false, polyline.contains(new Vector(6, -9)));
    assertEquals(true,  polyline.contains(new Vector(13, -4)));
    assertEquals(false, polyline.contains(new Vector(-10, -4)));
    assertEquals(true,  polyline.contains(new Vector(-9, -3)));
    assertEquals(true,  polyline.contains(new Vector(-9, -4)));
    assertEquals(true,  polyline.contains(new Vector(9, 4)));
    assertEquals(true,  polyline.contains(new Vector(11, 7)));
    assertEquals(true,  polyline.contains(new Vector(-12, 5)));
    assertEquals(true,  polyline.contains(new Vector(7, 1)));
    assertEquals(true,  polyline.contains(new Vector(-6, -7)));
    assertEquals(false, polyline.contains(new Vector(-6, -15)));
    assertEquals(true,  polyline.contains(new Vector(-8, 14)));
    assertEquals(true,  polyline.contains(new Vector(-9, 13)));
    assertEquals(true,  polyline.contains(new Vector(-9, 15)));
    assertEquals(true,  polyline.contains(new Vector(-9, 14)));
    assertEquals(true,  polyline.contains(new Vector(9, 16)));
    assertEquals(true,  polyline.contains(new Vector(3, 8)));
    assertEquals(false, polyline.contains(new Vector(12, 13)));
    assertEquals(false, polyline.contains(new Vector(-7, 10)));
  }

  @Test
  public void testArea()
  {
    assertEquals(641.0, polyline.area(), DELTA_ZERO);
    assertEquals(.0, new Polyline(new Vector(), new Vector(1, 1)).area(), DELTA_ZERO);
    assertEquals(0, new Polyline(new Vector(), new Vector(0, 2)).area(), DELTA_ZERO);
    assertEquals(0, new Polyline(new Vector(), new Vector(1, 2)).area(), DELTA_ZERO);
    assertEquals(4, new Polyline(new Vector(), new Vector(2, 0),
                                          new Vector(2, 2), new Vector(0, 2)).area(), DELTA_ZERO);
    assertEquals(2 , new Polyline(new Vector(), new Vector(2, 0),
                                          new Vector(2, 2)).area(), DELTA_ZERO);
    assertEquals(14, new Polyline(new Vector(), new Vector(2, 0),
                                           new Vector(2, 2), new Vector(5, 2),
                                           new Vector(5, 5), new Vector(4, 5),
                                           new Vector(4, 4), new Vector(1, 4),
                                           new Vector(1, 3), new Vector(0, 3)).area(), DELTA_ZERO);
    assertEquals(14, new Polyline(new Vector(), new Vector(2, 0),
                                           new Vector(2, 2), new Vector(5, 2),
                                           new Vector(5, 5), new Vector(4, 5),
                                           new Vector(4, 4), new Vector(1, 4),
                                           new Vector(1, 3), new Vector(0, 3),
                                           new Vector()).area(), DELTA_ZERO);

    double area = new Polyline(new Vector(), new Vector(.0, 73.8),
                               new Vector(58.9,  73.8), new Vector(58.9, .0)).area();
    assertEquals(4346.82, area, DELTA_ZERO);
  }

  @Test
  void testCentroid() {
    Polyline pl = new Polyline(new Vector(), new Vector(4, 0), new Vector(4, 4), new Vector(0, 4));
    Vector centroid = pl.centroid();
    assertEquals(new Vector(2, 2), centroid);
  }
  @Test
  void testCentroidWithoutArea() {
    Polyline pl = new Polyline(new Vector(), new Vector(4, 4));
    Vector centroid = pl.centroid();
    assertEquals(new Vector(2, 2), centroid);

    pl = new Polyline(new Vector(), new Vector(4, 4), new Vector(8, 8));
    centroid = pl.centroid();
    assertEquals(new Vector(4, 4), centroid);
  }
  @Test
  void testCentroidOut() {
    System.out.println(polyline.centroid());
  }

  @Test
  public void testSetCCW()
  { // Vorbedingung: Punkte der polyline sind im Uhrzeigersinn angegeben
    assertFalse(polyline.isCCW());

    Polyline pl = polyline.cw(); // ist schon cw => pl = polyline
    assertFalse(pl.isCCW());
    assertEquals(polyline, pl);

    pl = pl.ccw();
    assertTrue(pl.isCCW());

    pl = new Polyline(new Vector(), new Vector(2, 0), new Vector(2, 2));
    assertTrue(pl.isCCW());

    pl = new Polyline(new Vector(), new Vector(-2, 0), new Vector(-2, -2));
    assertTrue(pl.isCCW());

    pl = new Polyline(new Vector(), new Vector(2, 0), new Vector(2, -2));
    assertFalse(pl.isCCW());

    pl = new Polyline(new Vector(), new Vector(-2, 0), new Vector(-2, 2));
    assertFalse(pl.isCCW());

    pl = new Polyline(new Vector(), new Vector(2, 0));
    assertTrue(pl.isCCW());

    pl = new Polyline(new Vector(), new Vector(-2, 0));
    assertTrue(pl.isCCW());

    pl = new Polyline(new Vector(), new Vector(2, 2));
    assertTrue(pl.isCCW());

    pl = new Polyline(new Vector(), new Vector(-2, -2));
    assertTrue(pl.isCCW());

    // Jetzt könnte man noch die Reihenfolge der Punkte überprüfen
  }

  @Test
  void testIntersectionLine() {
    Collection<Polyline.LineIntersection> intersections = polyline.intersection(Line.withOrigin(new Vector(-4, 15))
                                                                            .withDirection(new Vector(0, 8)));
    assertEquals(1, intersections.stream().filter(i -> i.getLineStatus() == Line.LineStatus.IDENTICAL).count());
    assertEquals(1, intersections.stream().filter(i -> i.getLineStatus() == Line.LineStatus.PARALLEL).count());
    assertEquals(1, intersections.stream().filter(i -> i.getLineStatus() == Line.LineStatus.SEGMENT_INTERSECTS).count());
    assertEquals(20, intersections.stream().filter(i -> i.getLineStatus() == Line.LineStatus.LINE_INTERSECTS).count());


    intersections = polyline.intersection(Line.withOrigin(new Vector(-4, 9))
                                              .withDirection(new Vector(0, 4)));
    assertEquals(1, intersections.stream().filter(i -> i.getLineStatus() == Line.LineStatus.IDENTICAL).count());
    assertEquals(1, intersections.stream().filter(i -> i.getLineStatus() == Line.LineStatus.PARALLEL).count());
    assertEquals(2, intersections.stream().filter(i -> i.getLineStatus() == Line.LineStatus.SEGMENT_INTERSECTS).count());
    assertEquals(19, intersections.stream().filter(i -> i.getLineStatus() == Line.LineStatus.LINE_INTERSECTS).count());

    intersections = polyline.intersection(Line.withOrigin(new Vector(17, -17))
                                              .withDestination(new Vector(-14, 14)));
    assertEquals(0, intersections.stream().filter(i -> i.getLineStatus() == Line.LineStatus.IDENTICAL).count());
    assertEquals(0, intersections.stream().filter(i -> i.getLineStatus() == Line.LineStatus.PARALLEL).count());
    assertEquals(4, intersections.stream().filter(i -> i.getLineStatus() == Line.LineStatus.SEGMENT_INTERSECTS).count());
    assertEquals(19, intersections.stream().filter(i -> i.getLineStatus() == Line.LineStatus.LINE_INTERSECTS).count());
  }
  @Test
  void testIntersectionPolyline() {
    Polyline pl = new Polyline(new Vector(3, -11),
                               new Vector(3, -7),
                               new Vector(9, -7),
                               new Vector(9, -13));
    Collection<Polyline.PolylineIntersection> intersections = polyline.intersection(pl);
    assertEquals(3, intersections.stream().filter(i -> i.getLineStatus() == Line.LineStatus.SEGMENT_INTERSECTS).count());

    intersections = polyline.intersection(new Polyline(true, pl));
    assertEquals(4, intersections.stream().filter(i -> i.getLineStatus() == Line.LineStatus.SEGMENT_INTERSECTS).count());
  }

  @Test
  void testContainsVector() {
    assertTrue(polyline.containsVector(new Vector(4, -2)));
    assertTrue(polyline.containsVector(new Vector(10, 14)));
    assertTrue(polyline.containsVector(new Vector(-4, 9)));
    assertTrue(polyline.containsVector(new Vector(-9, -4)));

    assertFalse(polyline.containsVector(new Vector()));
    assertFalse(polyline.containsVector(new Vector(4, 5)));
  }

  @Test
  void testBuilder() {
    Polyline pl1 = Polyline.startAt(new Vector())
                           .withDirection(new Vector(4, 4))
                           .withDirection(new Vector(0, -4))
                           .build(true);
    Polyline pl2 = Polyline.startAt(new Vector())
                           .moveTo(new Vector(4, 4))
                           .moveTo(new Vector(4, 0))
                           .build(true);
    assertTrue(pl1.equals(pl2));
  }
}
