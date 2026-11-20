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
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.DoubleUnaryOperator;

/**
 *
 * @author Enrico S. Penetti &lt;enrico@penetti.de&gt;
 */
@SuppressWarnings("checkstyle:magicnumber")
public enum TemperatureUnit
{
  CELSIUS   ("°C",  d -> d - 273.15,                       d -> d + 273.15,                       1.),
  KELVIN    ("K",   d -> d,                                d -> d,                                1.),
  FAHRENHEIT("°F",  d -> d * 1.8 - 459.67,                 d -> (d + 459.67) * (5.0 / 9.0),       5. / 9.),
  RANKINE   ("°Ra", d -> d * 1.8,                          d -> d * (5.0 / 9.0),                  5. / 9.),
  REAUMUR   ("°Ré", d -> (d - 273.15) * .8,                d -> d * 1.25 + 273.15,                1.25),
  ROMER     ("°Rø", d -> (d - 273.15) * (21. / 40.) + 7.5, d -> (d - 7.5) * (40. / 21.) + 273.15, 40. / 21.),
  DELISLE   ("°De", d -> (373.15 - d) * 1.5,               d -> 373.15 - d * (2. / 3.),           -2. / 3.),
  NEWTON    ("°N",  d -> (d - 273.15) * .33,               d -> d * (100. / 33.) + 273.15,        100. / 33.);

  private static final ResourceBundle RESOURCE = ResourceBundle.getBundle("de.penetti.physics.temperatureunit");

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

  public String getUnitSign()
  {
    return unitSign;
  }
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

  public String getName()
  {
    return RESOURCE.getString("TemperatureUnit.Name." + name());
  }

  @Override
  public String toString()
  {
    return getName();
  }
}
