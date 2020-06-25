package de.penetti.math;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Utility-class to working with {@link Number}s.
 *
 * @author Enrico S. Penetti &lt;enrico@penetti.de&gt;
 */
public final class Numbers {
  private Numbers() {
  }

  /**
   * Gets the maximum of values from t.
   *
   * @param <T>
   * @param t
   * @return
   */
  @SafeVarargs
  public static <T extends Number & Comparable<? extends Number>> T max(final T... t) {
    if (t.length > 0) {
      T max = t[0];
      for (int i = 1; i < t.length; ++i) {
        if (compare(t[i], max) > 0) {
          max = t[i];
        }
      }
      return max;
    } else {
      throw new IllegalArgumentException();
    }
  }

  /**
   * Gets the minimum of values from t.
   *
   * @param <T>
   * @param t
   * @return
   */
  @SafeVarargs
  public static <T extends Number & Comparable<? extends Number>> T min(final T... t) {
    if (t.length > 0) {
      T min = t[0];
      for (int i = 1; i < t.length; ++i) {
        if (compare(t[i], min) < 0) {
          min = t[i];
        }
      }
      return min;
    } else {
      throw new IllegalArgumentException();
    }
  }

  /**
   * Compares the both values a and b. It returns
   * <pre>
   *      0 =&gt; a== b;
   *      1 =&gt; a &gt; b;
   *     -1 =&gt; a &lt; b
   * </pre>
   * The comparising is independend from the type of the both numbers. That means, you can mix different types of numbers.
   * E.g. you can compare BigInteger with Float, or Double and Float and so on.<br>
   * In accordance to {@link Double} and {@link Float}, NaN is considered by this method to be equal to itself and
   * greater than all other values (including POSITIVE_INFINITY).<br>
   * {@link Double#NEGATIVE_INFINITY} is equals {@link Float#NEGATIVE_INFINITY} is equals {@link Infinity#NEGATIVE_INFINITY} resp.
   * {@link Double#POSITIVE_INFINITY} is equals {@link Float#POSITIVE_INFINITY} is equals {@link Infinity#POSITIVE_INFINITY}.
   *
   * @param <T>
   * @param a
   * @param b
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <T extends Number & Comparable<? extends Number>> int compare(final T a, final T b) {
    if (a.getClass().equals(b.getClass())) {
      return ((Comparable) a).compareTo(b);
    }
    if (isNaN(a) || isNaN(b)) {
      return compareNaN(a, b);
    }
    if (isInfinity(a) || isInfinity(b)) {
      return compareInfinity(a, b);
    }
    BigDecimal bdA = asBigDecimal(a);
    BigDecimal bdB = asBigDecimal(b);
    if (bdA != null &&
      bdB != null) {
      return bdA.compareTo(bdB);
    }
    throw new IllegalArgumentException(String.format("The given numbers can not be compared: %s(%s) : %s(%s)",
      a.getClass().getName(), a,
      b.getClass().getName(), b));
  }

  /**
   * Compares two values, with at least one value is Not a Number (NaN).<br>
   * In accordance to {@link Double} and {@link Float}, NaN is considered by this method to be equal to itself and
   * greater than all other values (including POSITIVE_INFINITY).
   *
   * @param <T>
   * @param a
   * @param b
   * @return
   */
  static <T extends Number & Comparable<? extends Number>> int compareNaN(final T a, final T b) {
    boolean nanA = isNaN(a);
    boolean nanB = isNaN(b);
    if (nanA && nanB) {
      return 0;
    }
    if (nanA && !nanB) {
      return 1;
    }
    if (!nanA && nanB) {
      return -1;
    }
    throw new IllegalArgumentException("There a no 'not a number'."); // should never happens
  }

  /**
   * Compares two values, with at least one value is {@link Infinity#NEGATIVE_INFINITY} or {@link Infinity#POSITIVE_INFINITY}.<br>
   * {@link Double#NEGATIVE_INFINITY} is equals {@link Float#NEGATIVE_INFINITY} is equals {@link Infinity#NEGATIVE_INFINITY} resp.
   * {@link Double#POSITIVE_INFINITY} is equals {@link Float#POSITIVE_INFINITY} is equals {@link Infinity#POSITIVE_INFINITY}.<br>
   * if a == {@link Infinity#NEGATIVE_INFINITY} and b == {@link Infinity#NEGATIVE_INFINITY} it returns 0 resp.
   * if a == {@link Infinity#POSITIVE_INFINITY} and b == {@link Infinity#POSITIVE_INFINITY} it returns 0.<br>
   * {@link Infinity#NEGATIVE_INFINITY} is always lower than other values;
   * {@link Infinity#POSITIVE_INFINITY} is always greater than other values;
   *
   * @param <T>
   * @param a
   * @param b
   * @return
   */
  static <T extends Number & Comparable<? extends Number>> int compareInfinity(final T a, final T b) {
    Infinity infA = getInfinity(a);
    Infinity infB = getInfinity(b);
    if ((infA == Infinity.NEGATIVE_INFINITY && infB == Infinity.NEGATIVE_INFINITY) ||
        (infA == Infinity.POSITIVE_INFINITY && infB == Infinity.POSITIVE_INFINITY)) {
      return 0;
    }
    if (infA == Infinity.NEGATIVE_INFINITY && infB == Infinity.POSITIVE_INFINITY) {
      return -1;
    }
    if (infA == Infinity.POSITIVE_INFINITY && infB == Infinity.NEGATIVE_INFINITY) {
      return 1;
    }
    if (infA == Infinity.NEGATIVE_INFINITY ||
        infB == Infinity.POSITIVE_INFINITY) {
      return -1;
    }
    if (infA == Infinity.POSITIVE_INFINITY ||
        infB == Infinity.NEGATIVE_INFINITY) {
      return 1;
    }
    throw new IllegalArgumentException("There a no infinite number."); // should never happens
  }

  /**
   * Returns {@code true} if the specified number is a Not-a-Number (NaN) value, {@code false} otherwise.
   *
   * @param no the value to be tested.
   * @return {@code true} if the value of the argument is NaN; {@code false} otherwise.
   */
  public static boolean isNaN(Number no) {
    if (no instanceof Double) {
      return Double.isNaN((Double) no);
    }
    if (no instanceof Float) {
      return Float.isNaN((Float) no);
    }
    return false;
  }

  /**
   * Returns {@code true} if the specified number is infinitely large in magnitude, {@code false} otherwise.
   *
   * @param no the value to be tested.
   * @return {@code true} if the value of the argument is positive infinity or negative infinity; {@code false} otherwise.
   */
  public static boolean isInfinity(Number no) {
    return getInfinity(no) != Infinity.NOT_INFINITY;
  }

  /**
   * Gets the {@link Infinity} for the given {@link Number}.<br>
   *
   * @param no
   * @return
   * @see Infinity
   */
  public static Infinity getInfinity(Number no) {
    if (no instanceof Double) {
      return no.doubleValue() == Double.NEGATIVE_INFINITY ? Infinity.NEGATIVE_INFINITY
        : no.doubleValue() == Double.POSITIVE_INFINITY ? Infinity.POSITIVE_INFINITY
        : Infinity.NOT_INFINITY;
    }
    if (no instanceof Float) {
      return no.doubleValue() == Float.NEGATIVE_INFINITY ? Infinity.NEGATIVE_INFINITY
        : no.doubleValue() == Float.POSITIVE_INFINITY ? Infinity.POSITIVE_INFINITY
        : Infinity.NOT_INFINITY;
    }
    return Infinity.NOT_INFINITY;
  }

  /**
   * Creates a {@link BigDecimal} from the given {@link Number}.
   *
   * @param no
   * @return
   */
  private static BigDecimal asBigDecimal(Number no) {
    BigDecimal bd;
    String clazz = no.getClass().getName();
    switch (clazz) {
      case "java.lang.Byte":
      case "java.lang.Short":
      case "java.lang.Integer":
      case "java.lang.Long":
        bd = new BigDecimal(no.longValue());
        break;
      case "java.lang.Float":
      case "java.lang.Double":
        bd = new BigDecimal(no.doubleValue());
        break;
      case "java.math.BigInteger":
        bd = new BigDecimal((BigInteger) no);
        break;
      case "java.math.BigDecimal":
        bd = (BigDecimal) no;
        break;
      default:
        bd = null;
    }
    return bd;
  }

  /**
   * Constants to see the Infinity of Double or Float values.
   */
  public enum Infinity {
    /**
     * A constant holding the positive infinity.
     */
    NEGATIVE_INFINITY,
    /**
     * A constant holding the negative infinity
     */
    POSITIVE_INFINITY,
    /**
     * A constant to indicate, there is noting infinity
     */
    NOT_INFINITY;
  }
}
