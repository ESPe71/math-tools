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

import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;

/**
 *
 * @author Enrico S. Penetti &lt;enrico@penetti.de&gt;
 */
@SuppressWarnings("checkstyle:magicnumber")
public enum TemperatureUnit
{
  CELSIUS   ("°C",  d -> d - 273.15,                       d -> d + 273.15),
  KELVIN    ("K",   d -> d,                                d -> d),
  FAHRENHEIT("°F",  d -> d * 1.8 - 459.67,                 d -> (d + 459.67) * (5.0 / 9.0)),
  RANKINE   ("°Ra", d -> d * 1.8,                          d -> d * (5.0 / 9.0)),
  REAUMUR   ("°Ré", d -> (d - 273.15) * .8,                d -> d * 1.25 + 273.15),
  ROMER     ("°Rø", d -> (d - 273.15) * (21. / 40.) + 7.5, d -> (d - 7.5) * (40. / 21.) + 273.15),
  DELISLE   ("°De", d -> (373.15 - d) * 1.5,               d -> 373.15 - d * (2. / 3.)),
  NEWTON    ("°N",  d -> (d - 273.15) * .33,               d -> d * (100. / 33.) + 273.15);

  private static final ResourceBundle RESOURCE = ResourceBundle.getBundle("de.penetti.physics.temperatureunit");

  private static final String DEGREE_SIGN    = "°";
  private static final String EMPTY_STRING   = "";
  private static final String SMALL_E        = "e";
  private static final String SMALL_ACUTE_E  = "é";
  private static final String SMALL_O        = "o";
  private static final String SMALL_STROKE_O = "ø";

  private final String unitSign;
  private final transient Function<Double, Double> fromKelvin;
  private final transient Function<Double, Double> toKelvin;
  TemperatureUnit(String unitSign, Function<Double, Double> fromKelvin, Function<Double, Double> toKelvin)
  {
    this.unitSign = unitSign;
    this.fromKelvin = fromKelvin;
    this.toKelvin = toKelvin;
  }

  public static Optional<TemperatureUnit> getBySign(String unitSign)
  {
    String us = simplifyUnitSign(unitSign);
    for (TemperatureUnit u : values())
    {
      if (simplifyUnitSign(u.getUnitSign()).equals(us))
      {
        return Optional.of(u);
      }
    }
    return Optional.empty();
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
  public static double getValue(double value, TemperatureUnit fromUnit, TemperatureUnit toUnit)
  {
    double retValue = fromUnit.toKelvin.apply(value);
    retValue = toUnit.fromKelvin.apply(retValue);
    return retValue;
  }
  public String getName()
  {
    return RESOURCE.getString("TemperatureUnit.Name." + getUnitSign().replace(DEGREE_SIGN, EMPTY_STRING));
  }

  @Override
  public String toString()
  {
    return getName();
  }
}
