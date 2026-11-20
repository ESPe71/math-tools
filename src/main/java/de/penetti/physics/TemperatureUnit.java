/*
 * Copyright (C) 2016 Enrico S. Penetti &lt;enrico@penetti.de&gt;.
 *
 *
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
 *
 */
package de.penetti.physics;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.DoubleUnaryOperator;

/**
 * Temperature scales and the conversion between them: Celsius, Kelvin, Fahrenheit, Rankine, Réaumur, Rømer, Delisle and Newton.
 * <p>
 * Two kinds of conversion exist and must not be mixed up:
 * <ul>
 * <li>{@link #getValue(double, TemperatureUnit, TemperatureUnit)} converts an <em>absolute</em> temperature and respects the zero
 * point of each scale (0 °C is 273.15 K).</li>
 * <li>{@link #convertDifference(double, TemperatureUnit)} converts a temperature <em>difference</em> (a tolerance, a delta) and
 * applies only the scale (a difference of 1 °C is 1 K).</li>
 * </ul>
 * Each constant carries a unit sign, the conversion functions from and to Kelvin (the common base of all conversions) and its scale
 * in Kelvin per degree. Names are localized, see {@link #getName(Locale)}; signs can be looked up with {@link #getBySign(String)}.
 *
 * @author Enrico S. Penetti &lt;enrico@penetti.de&gt;
 * @see Temperature
 */
@SuppressWarnings("checkstyle:magicnumber")
public enum TemperatureUnit
{
  // sign, from Kelvin to this unit, from this unit to Kelvin, scale in Kelvin per degree (negative if the scale runs backwards)
  /** Degree Celsius: 0 °C is the freezing point and 100 °C the boiling point of water. */
  CELSIUS   ("°C",  d -> d - 273.15,                       d -> d + 273.15,                       1.),
  /** Kelvin, the SI unit: 0 K is absolute zero. All conversions run via Kelvin. */
  KELVIN    ("K",   d -> d,                                d -> d,                                1.),
  /** Degree Fahrenheit: water freezes at 32 °F and boils at 212 °F. */
  FAHRENHEIT("°F",  d -> d * 1.8 - 459.67,                 d -> (d + 459.67) * (5.0 / 9.0),       5. / 9.),
  /** Degree Rankine: degrees of Fahrenheit size, counted from absolute zero (0 °Ra). */
  RANKINE   ("°Ra", d -> d * 1.8,                          d -> d * (5.0 / 9.0),                  5. / 9.),
  /** Degree Réaumur: water freezes at 0 °Ré and boils at 80 °Ré. */
  REAUMUR   ("°Ré", d -> (d - 273.15) * .8,                d -> d * 1.25 + 273.15,                1.25),
  /** Degree Rømer: water freezes at 7.5 °Rø and boils at 60 °Rø. */
  ROMER     ("°Rø", d -> (d - 273.15) * (21. / 40.) + 7.5, d -> (d - 7.5) * (40. / 21.) + 273.15, 40. / 21.),
  /** Degree Delisle: a backwards scale, water boils at 0 °De and freezes at 150 °De. */
  DELISLE   ("°De", d -> (373.15 - d) * 1.5,               d -> 373.15 - d * (2. / 3.),           -2. / 3.),
  /** Degree Newton: water freezes at 0 °N and boils at 33 °N. */
  NEWTON    ("°N",  d -> (d - 273.15) * .33,               d -> d * (100. / 33.) + 273.15,        100. / 33.);

  private static final String RESOURCE_BASE_NAME = "de.penetti.physics.temperatureunit";
  private static final String DEGREE_SIGN    = "°";
  private static final String EMPTY_STRING   = "";
  private static final String SMALL_E        = "e";
  private static final String SMALL_ACUTE_E  = "é";
  private static final String SMALL_O        = "o";
  private static final String SMALL_STROKE_O = "ø";

  private final String unitSign;
  private final DoubleUnaryOperator fromKelvin;
  private final DoubleUnaryOperator toKelvin;
  private final double kelvinPerDegree;
  /**
   * Defines a temperature unit.
   *
   * @param unitSign the unit sign, e.g. "°C"
   * @param fromKelvin converts an absolute temperature from Kelvin to this unit (note the order: from Kelvin first, then to Kelvin)
   * @param toKelvin converts an absolute temperature from this unit to Kelvin
   * @param kelvinPerDegree the size of one degree of this unit in Kelvin, negative if the scale runs backwards (Delisle); only used
   *        for differences, see {@link #convertDifference(double, TemperatureUnit)}
   */
  TemperatureUnit(String unitSign, DoubleUnaryOperator fromKelvin, DoubleUnaryOperator toKelvin, double kelvinPerDegree)
  {
    this.unitSign = unitSign;
    this.fromKelvin = fromKelvin;
    this.toKelvin = toKelvin;
    this.kelvinPerDegree = kelvinPerDegree;
  }

  private static final Map<String, TemperatureUnit> BY_SIMPLIFIED_SIGN = new HashMap<>();
  static
  {
    for (TemperatureUnit u : values())
    {
      BY_SIMPLIFIED_SIGN.put(simplifyUnitSign(u.getUnitSign()), u);
    }
  }

  /**
   * Finds the unit for a unit sign. The degree sign is optional and the special letters are interchangeable with their plain
   * counterparts, so "°Ré", "Ré", "°Re" and "Re" all find {@link #REAUMUR}. The search is case-sensitive ("c" finds nothing).
   *
   * @param unitSign the unit sign, e.g. "°C" or "K"
   * @return the unit, or empty if no unit has this sign
   */
  public static Optional<TemperatureUnit> getBySign(String unitSign)
  {
    return Optional.ofNullable(BY_SIMPLIFIED_SIGN.get(simplifyUnitSign(unitSign)));
  }
  private static String simplifyUnitSign(String unitSign)
  {
    return unitSign.replace(DEGREE_SIGN, EMPTY_STRING)
                   .replace(SMALL_ACUTE_E, SMALL_E)
                   .replace(SMALL_STROKE_O, SMALL_O);
  }

  /**
   * Returns the sign of this unit as it is written after a value.
   *
   * @return the unit sign including the degree sign where the unit has one, e.g. "°C", "°Ré" or "K"
   */
  public String getUnitSign()
  {
    return unitSign;
  }

  /**
   * Converts an absolute temperature from this unit to another.
   *
   * @param value the temperature in this unit
   * @param toUnit the unit of the result
   * @return the temperature in {@code toUnit}
   * @see #getValue(double, TemperatureUnit, TemperatureUnit)
   * @see #convertDifference(double, TemperatureUnit) for temperature differences
   */
  public double getValue(double value, TemperatureUnit toUnit)
  {
    return getValue(value, this, toUnit);
  }
  /**
   * Converts a temperature from one unit to another. Different units are converted via Kelvin, so the result contains the
   * floating point rounding of two conversion steps and may differ from the exact value in the last digits. Compare converted values
   * with a tolerance. If both units are the same, the value is returned unchanged.
   *
   * @param value the temperature in {@code fromUnit}
   * @param fromUnit the unit of {@code value}
   * @param toUnit the unit of the result
   * @return the temperature in {@code toUnit}
   */
  public static double getValue(double value, TemperatureUnit fromUnit, TemperatureUnit toUnit)
  {
    if (fromUnit == toUnit)
    {
      return value;
    }
    double retValue = fromUnit.toKelvin.applyAsDouble(value);
    retValue = toUnit.fromKelvin.applyAsDouble(retValue);
    return retValue;
  }
  /**
   * Converts a temperature <em>difference</em> to another unit. Unlike {@link #getValue(double, TemperatureUnit)} only the scale of the
   * units is applied, not the zero point: a difference of 1 °C is 1 K and 1.8 °F. The result is negative if the units run in opposite
   * directions, e.g. a difference of 1 K is -1.5 °De.
   *
   * @param difference the difference in this unit
   * @param toUnit the unit of the result
   * @return the difference in {@code toUnit}
   */
  public double convertDifference(double difference, TemperatureUnit toUnit)
  {
    return difference * kelvinPerDegree / toUnit.kelvinPerDegree;
  }

  /**
   * Returns the name of this unit in the current {@link Locale#getDefault() default locale}, e.g. "Degree Celsius" or "Grad Celsius".
   * The locale is evaluated at each call, so a later {@link Locale#setDefault(Locale)} takes effect.
   *
   * @return the localized name
   * @see #getName(Locale)
   */
  public String getName()
  {
    return getName(Locale.getDefault());
  }

  /**
   * Returns the name of this unit in the given locale. Names are available in English, German, French and Spanish. For any
   * other language the usual {@link ResourceBundle} fallback applies: first the default locale, then English (base bundle).
   *
   * @param locale the locale of the name
   * @return the localized name
   */
  public String getName(Locale locale)
  {
    return ResourceBundle.getBundle(RESOURCE_BASE_NAME, locale).getString("TemperatureUnit.Name." + name());
  }

  /**
   * @return {@link #getName()}, i.e. the name in the current default locale
   */
  @Override
  public String toString()
  {
    return getName();
  }
}
