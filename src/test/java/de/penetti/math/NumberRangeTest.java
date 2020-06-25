package de.penetti.math;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NumberRangeTest
{

  @Test
  public void testNumberRange_superficial()
  {
    NumberRange<Double> nr = new NumberRange<>(1.0, 100.0);
    nr.toString();
    nr.hashCode();
  }

  @Test
  @SuppressWarnings("ResultOfObjectAllocationIgnored")
  public void testConstructor()
  {
    new NumberRange<>(1.0, 100.0);
    new NumberRange<>(1, 100);
    new NumberRange<>(1, 1);
    new NumberRange<>(100.0, 100.0);
    new NumberRange<>(100.0, 100.0, NumbersInterval.CLOSED);
    new NumberRange<>(100.0, 100.0, NumbersInterval.OPEN);
    NumberRange<Double> nr1 = new NumberRange<>(100.0, 100.0, NumbersInterval.LEFT_CLOSED);
    NumberRange<Double> nr2 = new NumberRange<>(100.0, 100.0, NumbersInterval.RIGHT_CLOSED);

    new NumberRange<>(nr1);
    new NumberRange<>(nr2);

    new NumberRange<>(nr1, NumbersInterval.OPEN);
    new NumberRange<>(nr2, NumbersInterval.CLOSED);
  }

  @Test
  @SuppressWarnings("ResultOfObjectAllocationIgnored")
  public void testConstructorWithException()
  {
    assertThrows(IllegalArgumentException.class, () -> {
      new NumberRange<>(20.0, 19.0);
    });
  }

  @Test
  public void testContains()
  {
    NumberRange<Double> nr = new NumberRange<>(1.0, 100.0);
    assertTrue(nr.contains(50));
    assertTrue(nr.contains(1.0));
    assertTrue(nr.contains(100.0));
    assertFalse(nr.contains(.5));
    assertFalse(nr.contains(100.0005));

    nr = new NumberRange<>(1.0, 100.0, NumbersInterval.CLOSED);
    assertTrue(nr.contains(50));
    assertTrue(nr.contains(1.0));
    assertTrue(nr.contains(100.0));
    assertFalse(nr.contains(.5));
    assertFalse(nr.contains(100.0005));

    nr = new NumberRange<>(1.0, 100.0, NumbersInterval.OPEN);
    assertTrue(nr.contains(50));
    assertFalse(nr.contains(1.0));
    assertFalse(nr.contains(100.0));
    assertFalse(nr.contains(.5));
    assertFalse(nr.contains(100.0005));

    nr = new NumberRange<>(1.0, 100.0, NumbersInterval.LEFT_CLOSED);
    assertTrue(nr.contains(50));
    assertTrue(nr.contains(1.0));
    assertFalse(nr.contains(100.0));
    assertFalse(nr.contains(.5));
    assertFalse(nr.contains(100.0005));

    nr = new NumberRange<>(1.0, 100.0, NumbersInterval.RIGHT_CLOSED);
    assertTrue(nr.contains(50));
    assertFalse(nr.contains(1.0));
    assertTrue(nr.contains(100.0));
    assertFalse(nr.contains(.5));
    assertFalse(nr.contains(100.0005));
  }

  @Test
  public void testAdjust()
  {
    NumberRange<Double> nr = new NumberRange<>(1.0, 100.0);
    assertEquals(1.0, nr.adjust(-50.), TestUtils.DELTA_9);
    assertEquals(1.0, nr.adjust(0.3), TestUtils.DELTA_9);
    assertEquals(100., nr.adjust(100.0003), TestUtils.DELTA_9);
    assertEquals(100., nr.adjust(105.), TestUtils.DELTA_9);
    assertEquals(55., nr.adjust(55.), TestUtils.DELTA_9);
  }

  @Test
  public void testEquals()
  {
    NumberRange<Integer> iNr1 = new NumberRange<>(1, 5);
    NumberRange<Integer> iNr2 = new NumberRange<>(1, 5);
    NumberRange<Integer> iNr3 = new NumberRange<>(1, 3);
    NumberRange<Integer> iNr4 = new NumberRange<>(2, 5);
    assertEquals(true, iNr1.equals(iNr2));
    assertEquals(false, iNr1.equals(iNr3));
    assertEquals(false, iNr1.equals(iNr4));

    NumberRange<Double> dNr2 = new NumberRange<>(1., 5.);
    NumberRange<Double> dNr3 = new NumberRange<>(1., 3.);
    assertEquals(true, iNr1.equals(dNr2));
    assertEquals(false, iNr1.equals(dNr3));

    NumberRange<Integer> niNr1 = new NumberRange<>(1, 5, NumbersInterval.OPEN);
    NumberRange<Integer> niNr2 = new NumberRange<>(1, 5, NumbersInterval.CLOSED);
    assertEquals(false, niNr1.equals(niNr2));

    assertFalse(niNr1.equals(5));
  }

  @Test
  public void testGetMaximum()
  {
    NumberRange<Integer> iNr1 = new NumberRange<>(1, 5);
    NumberRange<Integer> iNr2 = new NumberRange<>(1, 5);
    NumberRange<Integer> iNr3 = new NumberRange<>(1, 3);
    NumberRange<Integer> iNr4 = new NumberRange<>(2, 5);

    assertEquals((Integer)5, iNr1.getMaximum());
    assertEquals((Integer)5, iNr2.getMaximum());
    assertEquals((Integer)3, iNr3.getMaximum());
    assertEquals((Integer)5, iNr4.getMaximum());
  }
  @Test
  public void testGetMinimum()
  {
    NumberRange<Integer> iNr1 = new NumberRange<>(1, 5);
    NumberRange<Integer> iNr2 = new NumberRange<>(1, 5);
    NumberRange<Integer> iNr3 = new NumberRange<>(1, 3);
    NumberRange<Integer> iNr4 = new NumberRange<>(2, 5);

    assertEquals((Integer)1, iNr1.getMinimum());
    assertEquals((Integer)1, iNr2.getMinimum());
    assertEquals((Integer)1, iNr3.getMinimum());
    assertEquals((Integer)2, iNr4.getMinimum());
  }
}
