package de.penetti.math;

import java.util.function.Supplier;

import static de.penetti.math.Numbers.compare;

/**
 * @author Enrico S. Penetti &lt;enrico@penetti.de&gt;
 */
public enum NumbersInterval {
  CLOSED {
    @Override
    public <T extends Number & Comparable<? extends Number>> boolean contains(T left, T value, T right) {
      check(left, right, () -> ERROR_MSG);
      return compare(value, left) >= 0 && compare(value, right) <= 0;
    }
  },
  OPEN {
    @Override
    public <T extends Number & Comparable<? extends Number>> boolean contains(T left, T value, T right) {
      check(left, right, () -> ERROR_MSG);
      return compare(value, left) > 0 && compare(value, right) < 0;
    }
  },
  LEFT_CLOSED {
    @Override
    public <T extends Number & Comparable<? extends Number>> boolean contains(T left, T value, T right) {
      check(left, right, () -> ERROR_MSG);
      return compare(value, left) >= 0 && compare(value, right) < 0;
    }
  },
  RIGHT_CLOSED {
    @Override
    public <T extends Number & Comparable<? extends Number>> boolean contains(T left, T value, T right) {
      check(left, right, () -> ERROR_MSG);
      return compare(value, left) > 0 && compare(value, right) <= 0;
    }
  };

  private static final String ERROR_MSG = "left can not greater than right.";

  public static <T extends Number & Comparable<? extends Number>> void check(T left, T right) {
    check(left, right, () -> ERROR_MSG);
  }

  public static <T extends Number & Comparable<? extends Number>> void check(T left, T right, Supplier<String> errorMessage) {
    if (compare(left, right) > 0) {
      throw new IllegalArgumentException(errorMessage.get());
    }
  }

  public abstract <T extends Number & Comparable<? extends Number>> boolean contains(T left, T value, T right);
}
