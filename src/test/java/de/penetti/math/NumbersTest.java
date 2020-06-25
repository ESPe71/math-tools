package de.penetti.math;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;


public class NumbersTest
{
  @Test
  public void testMax()
  {
    int iMax = Numbers.max(1, 2, 3, 3, 4, 5);
    assertEquals(5, iMax);

    double dMax = Numbers.max(1., 2.3, 3.0, 3.0, 5.45);
    assertEquals(5.45, dMax, TestUtils.DELTA_9);
  }

  @Test
  public void testMaxWithException()
  {
    assertThrows(IllegalArgumentException.class, () -> {
      Numbers.max();
    });
  }

  @Test
  public void testMin()
  {
    int iMin = Numbers.min(1, 2, 3, 3, 4, 5);
    assertEquals(1, iMin);

    iMin = Numbers.min(5, 4, 3, 3, 2, 1);
    assertEquals(1, iMin);

    double dMin = Numbers.min(0.3, 1., 2.3, 3.0, 3.0, 5.45);
    assertEquals(.3, dMin, TestUtils.DELTA_9);

    dMin = Numbers.min(5.45, 1., 3.0, 2.3, 3.0, 1.0, 0.3);
    assertEquals(.3, dMin, TestUtils.DELTA_9);
  }

  @Test
  public void testMinWithException()
  {
    assertThrows(IllegalArgumentException.class, () -> {
      Numbers.min();
    });
  }

  @Test
  @SuppressWarnings("UnnecessaryBoxing")
  public void testCompare()
  {
    assertTrue(Numbers.compare(1, 2)<0);
    assertTrue(Numbers.compare(1, 1)==0);
    assertTrue(Numbers.compare(2, 1)>0);

    assertTrue(Numbers.compare(1.5, 2.5)<0);
    assertTrue(Numbers.compare(1.5, 1.5)==0);
    assertTrue(Numbers.compare(2.5, 1.5)>0);

    assertTrue(Numbers.compare(1.9999999, 2)<0);
    assertTrue(Numbers.compare(1.9999999, 1.9999999)==0);
    assertTrue(Numbers.compare(2, 1.9999999)>0);

    assertTrue(Numbers.compare(Integer.valueOf(1), Integer.valueOf(2)) == -1);
    assertTrue(Numbers.compare(Integer.valueOf(1), Double.valueOf(2.0)) == -1);
    assertTrue(Numbers.compare(Integer.valueOf(1), Double.valueOf(Double.MAX_VALUE)) == -1);
    assertTrue(Numbers.compare(Integer.valueOf(1), Double.valueOf(Double.MIN_VALUE)) == 1);
    assertTrue(Numbers.compare(Integer.valueOf(1), Double.valueOf(1.000001)) == -1);
    assertTrue(Numbers.compare(Integer.valueOf(1), Double.valueOf(1.000)) == 0);
    assertTrue(Numbers.compare(Integer.valueOf(1), Double.valueOf(0.25*4)) == 0);
  }
  @Test
  public void testCompareWithNaN()
  {
    Double dNaN = Double.NaN;
    Float fNaN = Float.NaN;
    assertTrue(Numbers.compare(dNaN, dNaN) == 0);
    assertTrue(Numbers.compare(dNaN, fNaN) == 0);

    assertTrue(Numbers.compare(dNaN, 5) == 1);
    assertTrue(Numbers.compare(-20, fNaN) == -1);
  }
  @Test
  public void testCompareWithInfinity()
  {
    Double dNInf = Double.NEGATIVE_INFINITY;
    Double dInf = Double.POSITIVE_INFINITY;
    Float fNInf = Float.NEGATIVE_INFINITY;
    Float fInf = Float.POSITIVE_INFINITY;

    assertTrue(Numbers.compare(dNInf, dNInf) == 0);
    assertTrue(Numbers.compare(fNInf, fNInf) == 0);
    assertTrue(Numbers.compare(dNInf, fNInf) == 0);
    assertTrue(Numbers.compare(dInf, dInf) == 0);
    assertTrue(Numbers.compare(fInf, fInf) == 0);
    assertTrue(Numbers.compare(dInf, fInf) == 0);

    assertTrue(Numbers.compare(dInf, fNInf) == 1);
    assertTrue(Numbers.compare(dNInf, fInf) == -1);

    assertTrue(Numbers.compare(dNInf, -3049478) == -1);
    assertTrue(Numbers.compare(fInf, 3049478) == 1);

    assertTrue(Numbers.compare(-3049478.54, dNInf) == 1);
    assertTrue(Numbers.compare(3049478.29834, fInf) == -1);
  }
  @Test
  public void testCompareWithException()
  {
    assertThrows(IllegalArgumentException.class, () -> {
      BewilderedNumber no = new BewilderedNumber(0);
      Numbers.compare(1, no);
    });
  }

  @Test
  public void testCompareBigInteger()
  {
    // MAX_INTEGER = 2147483647
    BigInteger bi1 = new BigInteger("214748364721474836472147483647");
    BigInteger bi2 = new BigInteger("214748364721474836472147483647");
    assertTrue(Numbers.compare(bi1, bi2) == 0);

    bi1 = new BigInteger("214748364721474836472147483646");
    bi2 = new BigInteger("214748364721474836472147483647");
    assertTrue(Numbers.compare(bi1, bi2) < 0);

    bi1 = new BigInteger("214748364721474836472147483647");
    bi2 = new BigInteger("214748364721474836472147483646");
    assertTrue(Numbers.compare(bi1, bi2) > 0);

    bi1 = new BigInteger("214748364721474836472147483647");
    BigDecimal bd2 = new BigDecimal("214748364721474836472147483647");
    assertTrue(Numbers.compare(bi1, bd2) == 0);
  }
  @Test
  public void testCompareMixedNumbers()
  {
    assertTrue(Numbers.compare(new BigDecimal(5), new BigInteger("5")) == 0);
    assertTrue(Numbers.compare(5, new BigInteger("5")) == 0);
    assertTrue(Numbers.compare(5., new BigInteger("5")) == 0);
    assertTrue(Numbers.compare(5.5, new BigDecimal(5.5)) == 0);
  }
  @Test
  public void testIsNaN()
  {
    Double dNaN = Double.NaN;
    Double dNInf = Double.NEGATIVE_INFINITY;
    Double dInf = Double.POSITIVE_INFINITY;
    Float fNaN = Float.NaN;
    Float fNInf = Float.NEGATIVE_INFINITY;
    Float fInf = Float.POSITIVE_INFINITY;

    assertTrue(Numbers.isNaN(dNaN));
    assertFalse(Numbers.isNaN(dNInf));
    assertFalse(Numbers.isNaN(dInf));

    assertTrue(Numbers.isNaN(fNaN));
    assertFalse(Numbers.isNaN(fNInf));
    assertFalse(Numbers.isNaN(fInf));

    assertFalse(Numbers.isNaN(Double.MAX_VALUE * 2)); // Infinity
    assertTrue(Numbers.isNaN(Math.sqrt(-1))); // NaN
    assertTrue(Numbers.isNaN(Float.NEGATIVE_INFINITY + Float.POSITIVE_INFINITY)); // NaN

    assertFalse(Numbers.isNaN(5.4));
    assertFalse(Numbers.isNaN(Math.E));
    assertFalse(Numbers.isNaN(Math.PI));

    assertFalse(Numbers.isNaN(Integer.MAX_VALUE + 5)); // overflow, but not a NaN

    assertFalse(Numbers.isNaN(new BigDecimal("214748364721474836472147483647.214748364721474836472147483647")));
  }
  @Test
  public void testCompareNaN()
  {
    Double dNaN = Double.NaN;
    Float fNaN = Float.NaN;
    assertTrue(Numbers.compareNaN(dNaN, dNaN) == 0);
    assertTrue(Numbers.compareNaN(dNaN, fNaN) == 0);

    assertTrue(Numbers.compareNaN(dNaN, 5) == 1);
    assertTrue(Numbers.compareNaN(-20, fNaN) == -1);
  }
  @Test
  public void testCompareNaNException()
  {
    assertThrows(IllegalArgumentException.class, () -> {
      Numbers.compareNaN(3.14, 6);
    });
  }
  @Test
  public void testCompareInfinity()
  {
    Double dNInf = Double.NEGATIVE_INFINITY;
    Double dInf = Double.POSITIVE_INFINITY;
    Float fNInf = Float.NEGATIVE_INFINITY;
    Float fInf = Float.POSITIVE_INFINITY;

    assertTrue(Numbers.compareInfinity(dNInf, dNInf) == 0);
    assertTrue(Numbers.compareInfinity(fNInf, fNInf) == 0);
    assertTrue(Numbers.compareInfinity(dNInf, fNInf) == 0);
    assertTrue(Numbers.compareInfinity(dInf, dInf) == 0);
    assertTrue(Numbers.compareInfinity(fInf, fInf) == 0);
    assertTrue(Numbers.compareInfinity(dInf, fInf) == 0);

    assertTrue(Numbers.compareInfinity(dInf, fNInf) == 1);
    assertTrue(Numbers.compareInfinity(dNInf, fInf) == -1);

    assertTrue(Numbers.compareInfinity(dNInf, -3049478) == -1);
    assertTrue(Numbers.compareInfinity(fInf, 3049478) == 1);

    assertTrue(Numbers.compareInfinity(-3049478.54, dNInf) == 1);
    assertTrue(Numbers.compareInfinity(3049478.29834, fInf) == -1);
  }
  @Test
  public void testCompareInfinityException()
  {
    assertThrows(IllegalArgumentException.class, () -> {
      Numbers.compareInfinity(Math.E, Math.PI);
    });
  }
  @Test
  public void testIsInfinity()
  {
    assertTrue(Numbers.isInfinity(Double.NEGATIVE_INFINITY));
    assertTrue(Numbers.isInfinity(Double.POSITIVE_INFINITY));
    assertTrue(Numbers.isInfinity(Float.NEGATIVE_INFINITY));
    assertTrue(Numbers.isInfinity(Float.POSITIVE_INFINITY));
    assertFalse(Numbers.isInfinity(Double.NaN));
    assertFalse(Numbers.isInfinity(Float.NaN));
    assertFalse(Numbers.isInfinity(Math.PI));
    assertFalse(Numbers.isInfinity(new BigDecimal("214748364721474836472147483647.214748364721474836472147483647")));
  }
  @Test
  public void testGetInfinity()
  {
    assertEquals(Numbers.Infinity.NEGATIVE_INFINITY, Numbers.getInfinity(Double.NEGATIVE_INFINITY));
    assertEquals(Numbers.Infinity.NEGATIVE_INFINITY, Numbers.getInfinity(Float.NEGATIVE_INFINITY));
    assertEquals(Numbers.Infinity.POSITIVE_INFINITY, Numbers.getInfinity(Double.POSITIVE_INFINITY));
    assertEquals(Numbers.Infinity.POSITIVE_INFINITY, Numbers.getInfinity(Float.POSITIVE_INFINITY));
    assertEquals(Numbers.Infinity.NOT_INFINITY, Numbers.getInfinity(new BigDecimal("214748364721474836472147483647."
                                                                                 + "214748364721474836472147483647")));
    assertEquals(Numbers.Infinity.NOT_INFINITY, Numbers.getInfinity(Math.E));
  }

  /**
   * This class would not be supported by Numbers.<br>
   * So it would be got an Exception, if we compare this.
   */
  private class BewilderedNumber extends Number implements Comparable<BewilderedNumber>
  {
    private final int no;
    public BewilderedNumber(int no)
    {
      this.no = no;
    }

    @Override
    public int intValue()
    {
      return no;
    }

    @Override
    public long longValue()
    {
      return no;
    }

    @Override
    public float floatValue()
    {
      return no;
    }

    @Override
    public double doubleValue()
    {
      return no;
    }

    @Override
    public int compareTo(BewilderedNumber o)
    {
      if(no == o.no)
        return 0;
      if(no > o.no)
        return 1;
      return -1;
    }
  }
}
