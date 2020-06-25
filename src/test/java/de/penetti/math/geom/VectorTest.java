package de.penetti.math.geom;

import de.penetti.math.Numbers;
import org.junit.jupiter.api.Test;

import static de.penetti.math.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

public class VectorTest
{
  @Test
  public void testConstructorWithNullValue()
  {
    assertThrows(NullPointerException.class, () -> {
      new Vector(null);
    });
  }
  
  @Test
  public void testGetX()
  {
    Vector instance = new Vector(3.14, 5);
    double expResult = 3.14;
    double result = instance.getX();
    assertEquals(expResult, result, DELTA_ZERO);
  }

  @Test
  public void testGetY()
  {
    Vector instance = new Vector(5.4, 3.14);
    double expResult = 3.14;
    double result = instance.getY();
    assertEquals(expResult, result, DELTA_ZERO);
  }

  @Test
  public void testEquals()
  {
    assertTrue(new Vector(3.14, 4.31).equals(new Vector(3.14, 4.31)));
    assertTrue(new Vector(.0, .0).equals(new Vector()));
    
    Vector p1 = new Vector(3, 5);
    Vector p2 = new Vector(p1);
    assertTrue(p1.equals(p2));
    assertTrue(p2.equals(p1));
    
    assertFalse(new Vector().equals(p1));
    assertFalse(new Vector().equals(this));
    assertFalse(new Vector().equals(null));
  }

  @Test
  public void testHashCode()
  {
    assertEquals(new Vector(3.14, 4.31).hashCode(), new Vector(3.14, 4.31).hashCode());
  }

  @Test
  public void testRotate()
  {
    Vector vector = new Vector(3, 3).rotate(Math.toRadians(45));
    assertEquals(0, vector.getX(), DELTA_12);
    assertEquals(Math.sqrt(18), vector.getY(), DELTA_12);

    vector = new Vector(3, 3).rotate(Math.toRadians(-45));
    assertEquals(Math.sqrt(18), vector.getX(), DELTA_12);
    assertEquals(0, vector.getY(), DELTA_12);

    vector = new Vector(3, 3).rotate(Math.toRadians(90));
    assertEquals(-3, vector.getX(), DELTA_12);
    assertEquals(3, vector.getY(), DELTA_12);

    vector = new Vector(3, 3).rotate(Math.toRadians(180));
    assertEquals(-3, vector.getX(), DELTA_12);
    assertEquals(-3, vector.getY(), DELTA_12);

    vector = new Vector(3, 3).rotate(Math.toRadians(270));
    assertEquals(3, vector.getX(), DELTA_12);
    assertEquals(-3, vector.getY(), DELTA_12);

    vector = new Vector(3, 3).rotate(Math.toRadians(-270));
    assertEquals(-3, vector.getX(), DELTA_12);
    assertEquals(3, vector.getY(), DELTA_12);
  }
  @Test
  public void testRotateByOrigin()
  {
    Vector vector =  new Vector(3, 3).rotate(new Vector(5, 5), Math.toRadians(45));
    assertEquals(5, vector.getX(), DELTA_12);
    assertEquals(5+(-Math.sqrt(8)), vector.getY(), DELTA_12);

    vector =  new Vector(3, 3).rotate(new Vector(5, 5), Math.toRadians(-45));
    assertEquals(5+(-Math.sqrt(8)), vector.getX(), DELTA_12);
    assertEquals(5, vector.getY(), DELTA_12);

    vector =  new Vector(3, 3).rotate(new Vector(5, 5), Math.toRadians(90));
    assertEquals(7, vector.getX(), DELTA_12);
    assertEquals(3, vector.getY(), DELTA_12);

    vector =  new Vector(3, 3).rotate(new Vector(5, 5), Math.toRadians(180));
    assertEquals(7, vector.getX(), DELTA_12);
    assertEquals(7, vector.getY(), DELTA_12);

    vector =  new Vector(3, 3).rotate(new Vector(5, 5), Math.toRadians(270));
    assertEquals(3, vector.getX(), DELTA_12);
    assertEquals(7, vector.getY(), DELTA_12);

    vector =  new Vector(3, 3).rotate(new Vector(5, 5), Math.toRadians(-270));
    assertEquals(7, vector.getX(), DELTA_12);
    assertEquals(3, vector.getY(), DELTA_12);

    vector =  new Vector(3, 3).rotate(new Vector(5, 5), Math.toRadians(0));
    assertEquals(3, vector.getX(), DELTA_12);
    assertEquals(3, vector.getY(), DELTA_12);

    vector =  new Vector(3, 3).rotate(new Vector(5, 5), Math.toRadians(360));
    assertEquals(3, vector.getX(), DELTA_12);
    assertEquals(3, vector.getY(), DELTA_12);

    vector =  new Vector(3, 3).rotate(new Vector(5, 5), Math.toRadians(720));
    assertEquals(3, vector.getX(), DELTA_12);
    assertEquals(3, vector.getY(), DELTA_12);

    vector =  new Vector(3, 3).rotate(new Vector(5, 5), Math.toRadians(-360));
    assertEquals(3, vector.getX(), DELTA_12);
    assertEquals(3, vector.getY(), DELTA_12);
  }
  @Test
  public void testAngle()
  {
    assertEquals(0, new Vector(3, 3).angle(new Vector(6, 6)), DELTA_ZERO);
    assertEquals(Math.toRadians(90), new Vector(3, 3).angle(new Vector(-3, 3)), DELTA_ZERO);
    assertEquals(Math.toRadians(45), new Vector(3, 3).angle(new Vector(0, 3)), DELTA_ZERO);
    assertEquals(Math.toRadians(45), new Vector(3.567, 3.567).angle(new Vector(0, 3)), DELTA_ZERO);
    assertEquals(Math.toRadians(135), new Vector(3, 3).angle(new Vector(-3, 0)), DELTA_ZERO);
    assertEquals(Math.toRadians(180), new Vector(3, 3).angle(new Vector(-3, -3)), DELTA_ZERO);
    assertEquals(Math.toRadians(225), new Vector(3.5, 3.5).angle(new Vector(0, -3)), DELTA_ZERO);
    assertEquals(Math.toRadians(270), new Vector(3, 3).angle(new Vector(3, -3)), DELTA_ZERO);
    assertEquals(Math.toRadians(315), new Vector(3, 3).angle(new Vector(5, 0)), DELTA_ZERO);
  }

  @Test
  public void testAdd()
  {
    Vector pnt = new Vector().add(new Vector());
    assertEquals(.0, pnt.getX(), DELTA_ZERO);
    assertEquals(.0, pnt.getY(), DELTA_ZERO);

    pnt = new Vector().add(new Vector(5.43, 9.54));
    assertEquals(5.43, pnt.getX(), DELTA_ZERO);
    assertEquals(9.54, pnt.getY(), DELTA_ZERO);

    pnt = new Vector(8.54, 90.543).add(new Vector(5.43, 9.54));
    assertEquals(13.97, pnt.getX(), DELTA_12);
    assertEquals(100.083, pnt.getY(), DELTA_ZERO);

    pnt = new Vector(4.35, -7.43).add(new Vector(-5.43, 9.54));
    assertEquals(-1.08, pnt.getX(), DELTA_ZERO);
    assertEquals(2.11, pnt.getY(), DELTA_12);
  }

  @Test
  public void testSub()
  {
    Vector pnt = new Vector().sub(new Vector());
    assertEquals(.0, pnt.getX(), DELTA_ZERO);
    assertEquals(.0, pnt.getY(), DELTA_ZERO);

    pnt = new Vector().sub(new Vector(5.43, 9.54));
    assertEquals(-5.43, pnt.getX(), DELTA_ZERO);
    assertEquals(-9.54, pnt.getY(), DELTA_ZERO);

    pnt = new Vector(8.54, 90.543).sub(new Vector(5.43, 9.54));
    assertEquals(3.11, pnt.getX(), DELTA_3);
    assertEquals(81.003, pnt.getY(), DELTA_3);

    pnt = new Vector(4.35, -7.43).sub(new Vector(-5.43, 9.54));
    assertEquals(9.78, pnt.getX(), DELTA_ZERO);
    assertEquals(-16.97, pnt.getY(), DELTA_ZERO);
  }

  @Test
  public void testMul()
  {
    Vector pnt = new Vector(2.5, 6.74).mul(3.98);
    assertEquals(9.95, pnt.getX(), DELTA_12);
    assertEquals(26.8252, pnt.getY(), DELTA_12);

    pnt = new Vector(-4.73, 9.53).mul(8.32);
    assertEquals(-39.3536, pnt.getX(), DELTA_12);
    assertEquals(79.2896, pnt.getY(), DELTA_12);

    pnt = new Vector(7.39, 5.936).mul(-93.7452);
    assertEquals(-692.777028, pnt.getX(), DELTA_12);
    assertEquals(-556.4715072, pnt.getY(), DELTA_12);
  }

  @Test
  public void testLength()
  {
    assertEquals(0, new Vector().length(), DELTA_ZERO);
    assertEquals(Math.sqrt(8), new Vector(2, 2).length(), DELTA_ZERO);
    assertEquals(Math.sqrt(22.00127225), new Vector(2.654, 3.8675).length(), DELTA_ZERO);
    assertEquals(3, new Vector(0, 3).length(), DELTA_ZERO);
    assertEquals(Math.sqrt(32), new Vector(-4, 4).length(), DELTA_ZERO);
    assertEquals(Math.sqrt(32), new Vector(4, -4).length(), DELTA_ZERO);
    assertEquals(6, new Vector(6, 0).length(), DELTA_ZERO);
    assertEquals(6, new Vector(0, 6).length(), DELTA_ZERO);
  }
  @Test
  public void testNormalize()
  {
    Vector vector = new Vector(3, 0).normalize();
    assertEquals(1, vector.getX(), DELTA_ZERO);
    assertEquals(0, vector.getY(), DELTA_ZERO);

    vector = new Vector(3, 3).normalize();
    assertEquals(Math.sqrt(2)/2, vector.getX(), DELTA_12);
    assertEquals(Math.sqrt(2)/2, vector.getY(), DELTA_12);

    vector = new Vector(30, 30).normalize();
    assertEquals(Math.sqrt(2)/2, vector.getX(), DELTA_12);
    assertEquals(Math.sqrt(2)/2, vector.getY(), DELTA_12);

    vector = new Vector(-3, 3).normalize();
    assertEquals(-Math.sqrt(2)/2, vector.getX(), DELTA_12);
    assertEquals(Math.sqrt(2)/2, vector.getY(), DELTA_12);

    vector = new Vector(44.56, -44.56).normalize();
    assertEquals(Math.sqrt(2)/2, vector.getX(), DELTA_12);
    assertEquals(-Math.sqrt(2)/2, vector.getY(), DELTA_12);

    vector = new Vector(-23, 23).normalize();
    assertEquals(-Math.sqrt(2)/2, vector.getX(), DELTA_12);
    assertEquals(Math.sqrt(2)/2, vector.getY(), DELTA_12);

    vector = new Vector(-76, -76).normalize();
    assertEquals(-Math.sqrt(2)/2, vector.getX(), DELTA_12);
    assertEquals(-Math.sqrt(2)/2, vector.getY(), DELTA_12);

    vector = new Vector(0, 0).normalize();
    assertEquals(Double.NaN, vector.getX(), DELTA_12);
    assertEquals(Double.NaN, vector.getY(), DELTA_12);

    assertTrue(Numbers.isNaN(vector.getX()));
    assertTrue(Numbers.isNaN(vector.getY()));

    vector = new Vector(0.000000001, 0).normalize();
    assertEquals(1, vector.getX(), DELTA_12);
    assertEquals(0, vector.getY(), DELTA_12);

    vector = new Vector(0.000000001, 0.000000001).normalize();
    assertEquals(Math.sqrt(2)/2, vector.getX(), DELTA_12);
    assertEquals(Math.sqrt(2)/2, vector.getY(), DELTA_12);
  }

  @Test
  public void testLengthPoint()
  {
    assertEquals(0, new Vector().length(new Vector()), DELTA_ZERO);
    assertEquals(Math.sqrt(109), new Vector(1, -12).length(new Vector(4, -2)), DELTA_ZERO);
  }

  @Test
  public void testDot()
  {
    assertEquals(66, new Vector(3, 5).dot(new Vector(7, 9)), DELTA_ZERO);
    assertEquals(24, new Vector(-3, 5).dot(new Vector(7, 9)), DELTA_ZERO);
    assertEquals(-66, new Vector(3, -5).dot(new Vector(-7, 9)), DELTA_ZERO);
    assertEquals(66, new Vector(-3, -5).dot(new Vector(-7, -9)), DELTA_ZERO);
    assertEquals(78.51492564, new Vector(3.6423, 5.7693).dot(new Vector(7.2956, 9.0032)), DELTA_ZERO);
  }

  @Test
  public void testIsLinearlyDependent()
  {
    assertEquals(true,  new Vector(2, 2).isLinearlyDependent(new Vector(4, 4)));
    assertEquals(false, new Vector().isLinearlyDependent(new Vector(4, 4)));
    assertEquals(false, new Vector(2, 2).isLinearlyDependent(new Vector()));
    assertEquals(false, new Vector().isLinearlyDependent(new Vector()));
    assertEquals(false, new Vector(7, 19).isLinearlyDependent(new Vector(2, 14)));
    assertEquals(true,  new Vector(7, 19).isLinearlyDependent(new Vector(-7, -19)));
  }

  @Test
  public void testDeterminante()
  {
    Vector v1 = new Vector(2, 1);
    Vector v2 = new Vector(1, 5);
    assertEquals(9, v1.determinante(v2), DELTA_ZERO);
    assertEquals(-9, v2.determinante(v1), DELTA_ZERO);

    v1 = new Vector(1, 2);
    v2 = new Vector(0.5, 1);
    assertEquals(0,v1.determinante(v2), DELTA_ZERO);
  }

}
