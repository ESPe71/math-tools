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
package de.penetti.math;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static de.penetti.math.TestUtils.DELTA_ZERO;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Enrico
 */
public class TupleTest {
  @Test
  public void testEquals() {
    Tuple<BigInteger> tuple = new Tuple<>(BigInteger.ONE, BigInteger.TEN);
    assertTrue(tuple.equals(tuple));
    assertFalse(tuple.equals(null));
    assertFalse(tuple.equals(this));

    assertTrue(tuple.equals(new Tuple<>(BigInteger.ONE, BigInteger.TEN)));
  }

  @Test
  public void testFromStringWithException() {
    assertThrows(NumberFormatException.class, () -> {
      Tuple.fromString("irgendeinblödsinn", Float.class, n -> new Tuple<>(n));
    });
  }

  @Test
  public void testFromStringInteger() {
    String tupelAsSting = "34, 65, 23, -7";
    Tuple<Integer> expResult = new Tuple<>(34, 65, 23, -7);
    Tuple<Integer> result = Tuple.fromIntegerString(tupelAsSting);
    assertEquals(expResult, result);

    tupelAsSting = "(34, 65, 23, -7)";
    result = Tuple.fromIntegerString(tupelAsSting);
    assertEquals(expResult, result);
  }

  @Test
  public void testFromStringDouble() {
    Tuple<Double> expResult = new Tuple<>(-34.5, 65D, 23D, -7D);

    String tupelAsSting = "-34.5, 65.0, 23, -7";
    Tuple<Double> result = Tuple.fromDoubleString(tupelAsSting);
    assertEquals(expResult, result);

    tupelAsSting = "(-34.5, 65.0, 23, -7)";
    result = Tuple.fromDoubleString(tupelAsSting);
    assertEquals(expResult, result);
  }

  @Test
  public void testFromStringFloat() {
    Tuple<Float> expResult = new Tuple<>(-34.5f, 65f, 23f, -7.0f);

    String tupelAsSting = "-34.5, 65.0, 23, -7";
    Tuple<Float> result = Tuple.fromString(tupelAsSting, Float.class, n -> new Tuple<>(n));
    assertEquals(expResult, result);

    tupelAsSting = "(-34.5, 65.0, 23, -7)";
    result = Tuple.fromString(tupelAsSting, Float.class, n -> new Tuple<>(n));
    assertEquals(expResult, result);
  }

  @Test
  public void testFromStringEmpty() {
    Tuple<Double> result = Tuple.fromDoubleString("()");
    assertEquals(new Tuple<>(), result);

    Tuple<Integer> result2 = Tuple.fromIntegerString("");
    assertEquals(new Tuple<>(), result2);
  }

  @Test
  public void testSize() {
    Tuple<Double> instance = new Tuple<>(-34.5, 65D, 23D, -7D);
    assertEquals(4, instance.size());
  }

  @Test
  public void testGet() {
    Tuple<Double> instance = new Tuple<>(-34.5, 65D, 23D, -7D);
    assertEquals(-34.5, instance.get(0), DELTA_ZERO);
    assertEquals(65, instance.get(1), DELTA_ZERO);
    assertEquals(23, instance.get(2), DELTA_ZERO);
    assertEquals(-7, instance.get(3), DELTA_ZERO);
  }

  @Test
  public void testToString() {
    Tuple<Double> instance = new Tuple<>(-34.5, 65D, 23D, -7D);
    assertEquals("(-34.5, 65.0, 23.0, -7.0)", instance.toString());

    Tuple<Float> instance2 = new Tuple<>();
    assertEquals("()", instance2.toString());
  }

  @Test
  public void testGetType() {
    Tuple<Double> dT = new Tuple<>(.0);
    assertEquals(Double.class, dT.getType());

    Tuple<BigInteger> biT = new Tuple<>(BigInteger.ONE, BigInteger.TEN);
    assertEquals(BigInteger.class, biT.getType());

    Tuple<Integer> iT = new Tuple<>(1, 2);
    assertEquals(Integer.class, iT.getType());
  }

  @Test
  public void testToGenericString() {
    Tuple<BigInteger> biT = new Tuple<>(BigInteger.ONE, BigInteger.TEN);
    assertEquals("java.math.BigInteger(1, 10)", biT.toGenericString());

    Tuple<Double> dT = new Tuple<>(3.14, 1.41);
    assertEquals("java.lang.Double(3.14, 1.41)", dT.toGenericString());
  }

  @Test
  public void testFromGenericString() {
    Tuple<?> t = Tuple.fromGenericString("java.lang.Double(3.14, 1.41)");
    assertEquals(new Tuple<>(3.14, 1.41), t);

    t = Tuple.fromGenericString("java.lang.Integer(3, 1)");
    assertEquals(new Tuple<>(3, 1), t);

    t = Tuple.fromGenericString("java.lang.Float(3.14, 1.41)");
    assertEquals(new Tuple<>(3.14f, 1.41f), t);
  }

  @Test
  public void testFromGenericString_2() {
    assertThrows(NumberFormatException.class, () -> {
      Tuple.fromGenericString("java.lang.Double(3.undwas14, 1.41)");
    });
  }

  @Test
  public void testFromGenericString_3() {
    assertThrows(NumberFormatException.class, () -> {
      Tuple.fromGenericString("oracle.java.lang.Double(3.14, 1.41)");
    });
  }
}
