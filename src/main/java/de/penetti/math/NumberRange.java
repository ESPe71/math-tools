package de.penetti.math;

import java.util.Objects;

import static de.penetti.math.NumbersInterval.CLOSED;
import static de.penetti.math.NumbersInterval.check;

/**
 * Class to represent a range of {@link Number}.<br>
 *
 * @param <T>
 * @author Enrico S. Penetti &lt;enrico@penetti.de&gt;
 */
public final class NumberRange<T extends Number & Comparable<? extends Number>> {
  private final T min;
  private final T max;
  private final NumbersInterval interval;

  public NumberRange(T min, T max, NumbersInterval interval) {
    check(min, max, () -> "min must be greater or equals max.");
    this.min = min;
    this.max = max;
    this.interval = interval;
  }

  /**
   * Constructor The {@link NumbersInterval} is {@link NumbersInterval#CLOSED}
   *
   * @param min
   * @param max
   * @throws IllegalArgumentException if min&gt;max
   */
  public NumberRange(T min, T max) throws IllegalArgumentException {
    this(min, max, CLOSED);
  }

  public NumberRange(NumberRange<T> noRange) {
    this(noRange.getMinimum(), noRange.getMaximum(), noRange.getInterval());
  }

  public NumberRange(NumberRange<T> noRange, NumbersInterval interval) {
    this(noRange.getMinimum(), noRange.getMaximum(), interval);
  }

  /**
   * Gets the minimum of this {@link NumberRange}
   *
   * @return
   */
  public T getMinimum() {
    return min;
  }

  /**
   * Gets the maximum of this {@link NumberRange}.
   *
   * @return
   */
  public T getMaximum() {
    return max;
  }

  public NumbersInterval getInterval() {
    return interval;
  }

  /**
   * Tests if this {@link NumberRange} contains {@code value} in accordance with the {@link NumbersInterval}.<br>
   *
   * @param value
   * @return
   */
  public <T extends Number & Comparable<? extends Number>> boolean contains(T value) {
    return interval.contains(min, value, max);
  }

  /**
   * Adjust {@code value} with this {@link NumberRange}.<br>
   * If {@code value} less than {@link #getMinimum()} it returns {@link #getMinimum()}, If {@code value}
   * greater than {@link #getMaximum()} it returns {@link #getMaximum()},
   *
   * @param value
   * @return
   */
  public T adjust(T value) {
    T d = Numbers.max(min, Numbers.min(value, max));
    return d;
  }

  @Override
  public String toString() {
    return getClass().getName() + "[min=" + min + "; max=" + max + "; interval=" + interval + "]";
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof NumberRange) {
      NumberRange<?> nr = (NumberRange<?>) obj;
      if (nr.interval == interval &&
          Numbers.compare(nr.min, min) == 0 &&
          Numbers.compare(nr.max, max) == 0) {
        return true;
      }
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(min, max, interval);
  }
}
