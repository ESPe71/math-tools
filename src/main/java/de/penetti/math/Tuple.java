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

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @param <T>
 * @author Enrico
 */
public class Tuple<T extends Number> {
  private static final Character LEFT_BRACKET = '(';
  private static final Character RIGHT_BRACKET = ')';

  private final T[] tuple;

  @SafeVarargs
  public Tuple(T... tuple) {
    this.tuple = tuple;
  }

  @SuppressWarnings("unchecked")
  public static <N extends Number> Tuple<N> fromGenericString(String tupleAsString) throws NumberFormatException {
    try {
      String value = tupleAsString;
      int idx = value.indexOf(LEFT_BRACKET);
      String type = value.substring(0, idx);
      value = value.substring(idx);
      Class<? extends Number> typeClass = (Class<? extends Number>) Class.forName(type);
      Tuple<?> tuple = Tuple.fromString(value, typeClass, n -> new Tuple<>(n));
      return (Tuple<N>) tuple;
    } catch (NumberFormatException e) {
      logFromGenericString(tupleAsString, e.getLocalizedMessage());
      throw e;
    } catch (ClassNotFoundException e) {
      logFromGenericString(tupleAsString, e.getLocalizedMessage());
      throw new NumberFormatException(e.getLocalizedMessage());
    }
  }

  private static void logFromGenericString(String tupleAsString, String errorMsg) {
    Logger.getLogger(Tuple.class.getName())
      .log(Level.SEVERE,
        String.format("Tuple.fromGenericString(%s): %s", tupleAsString, errorMsg));
  }

  @SuppressWarnings("unchecked")
  public static <N extends Number> Tuple<N> fromString(String tupleAsString,
                                                       Class<N> typeClass,
                                                       Function<N[], Tuple<N>> creator) throws NumberFormatException {
    try {
      StringTokenizer st = new StringTokenizer(trim(tupleAsString), " ,", false);
      int count = st.countTokens();
      N[] numbers = (N[]) Array.newInstance(typeClass, count);
      Method method = typeClass.getDeclaredMethod("valueOf", String.class);
      for (int i = 0; i < count; i++) {
        numbers[i] = (N) method.invoke(null, st.nextToken());
      }
      return creator.apply(numbers);
    } catch (IllegalAccessException | IllegalArgumentException | NegativeArraySizeException |
      NoSuchMethodException | SecurityException | InvocationTargetException e) {
      throw new NumberFormatException(e.getLocalizedMessage());
    }
  }

  public static Tuple<java.lang.Double> fromDoubleString(String tupleAsString) {
    return Tuple.fromString(tupleAsString, java.lang.Double.class, (t) -> new Tuple<>(t));
  }

  public static Tuple<java.lang.Integer> fromIntegerString(String tupleAsString) {
    return Tuple.fromString(tupleAsString, java.lang.Integer.class, (t) -> new Tuple<>(t));
  }

  private static String trim(String tupleAsString) {
    String tuple = tupleAsString.trim();
    if (tuple.startsWith(LEFT_BRACKET.toString())) {
      tuple = tuple.substring(1);
    }
    if (tuple.endsWith(RIGHT_BRACKET.toString())) {
      tuple = tuple.substring(0, tuple.length() - 1);
    }
    return tuple;
  }

  public T get(int idx) {
    return tuple[idx];
  }

  public int size() {
    return tuple.length;
  }

  @SuppressWarnings("unchecked")
  public <T extends Class<? extends Number>> T getType() {
    return (T) tuple.getClass().getComponentType();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Tuple<?> tuple1 = (Tuple<?>) o;
    return Arrays.equals(tuple, tuple1.tuple);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(tuple);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(LEFT_BRACKET);
    if (tuple.length > 0) {
      sb.append(tuple[0].toString());
      for (int i = 1; i < tuple.length; i++) {
        sb.append(", ").append(tuple[i].toString());
      }
    }
    sb.append(RIGHT_BRACKET);
    return sb.toString();
  }

  public String toGenericString() {
    return new StringBuilder().append(getType().getName()).append(toString()).toString();
  }
}
