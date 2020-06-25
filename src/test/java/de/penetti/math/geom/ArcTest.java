/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.penetti.math.geom;

import de.penetti.math.TestUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 * @author Enrico
 */
public class ArcTest
{
  @Test
  public void testContstructor()
  {
    Arc arc = new Arc(new Vector(), 30, 40, Math.toRadians(45), Math.toRadians(90));
    assertEquals(0., arc.getCenter().getX(), TestUtils.DELTA_9);
    assertEquals(0., arc.getCenter().getY(), TestUtils.DELTA_9);
    assertEquals(30., arc.getXRadius(), TestUtils.DELTA_9);
    assertEquals(40., arc.getYRadius(), TestUtils.DELTA_9);
    assertEquals(Math.toRadians(45), arc.getStartAngle(), TestUtils.DELTA_9);
    assertEquals(Math.toRadians(90), arc.getEndAngle(), TestUtils.DELTA_9);
    assertEquals(Math.toRadians(45), arc.getExtend(), TestUtils.DELTA_9);
  }
  @Test
  public void testGetArc()
  {
    Vector start = new Vector(.002027, 1.110527);
    Vector end   = new Vector(.001482, 1.111072);
    double  bulge = -.468377;
    Arc arc = new Arc(start, end, bulge);
    assertEquals(0.0019815817777, arc.getCenter().getX(), TestUtils.DELTA_9);
    assertEquals(1.1110265817777, arc.getCenter().getY(), TestUtils.DELTA_9);
    assertEquals(3.0509293974277, arc.getStartAngle(), TestUtils.DELTA_9);
    assertEquals(4.8030522365470, arc.getEndAngle(), TestUtils.DELTA_9);
    assertEquals(0.0005016420712, arc.getXRadius(), TestUtils.DELTA_9);
    assertEquals(0.0005016420712, arc.getYRadius(), TestUtils.DELTA_9);
    assertEquals(1.7521228391192, arc.getExtend(), TestUtils.DELTA_9);
  }
  @Test
  public void testGetArc2()
  {
    Vector start = new Vector(.001482, 1.111072);
    Vector end   = new Vector(.002027, 1.110527);
    double  bulge = .468377;
    Arc arc = new Arc(start, end, bulge);
    assertEquals(0.00198157, arc.getCenter().getX(), .0000001);
    assertEquals(1.11102660, arc.getCenter().getY(), .0000001);
    assertEquals(3.05092940, arc.getStartAngle(), .0000001);
    assertEquals(4.80305224, arc.getEndAngle(), .0000001);
    assertEquals(0.00050162435, arc.getXRadius(), .0000001);
    assertEquals(0.00050162435, arc.getYRadius(), .0000001);
    assertEquals(1.7521228391192, arc.getExtend(), TestUtils.DELTA_9);
  }
  @Test
  public void testArcParam_getExtend()
  {
    Arc arc = new Arc(new Vector(), 1, Math.toRadians(0), Math.toRadians(90));
    assertEquals(Math.toRadians(90), arc.getExtend());
    
    arc = new Arc(new Vector(), 1, Math.toRadians(720), Math.toRadians(90));
    assertEquals(Math.toRadians(90), arc.getExtend());
  }
  
  @Test
  public void testGetBulge()
  {
    assertThrows(UnsupportedOperationException.class, () -> {
      Arc arc = new Arc(new Vector(), 0, 0, 0);
      arc.getBulge();
    });
  }
}
