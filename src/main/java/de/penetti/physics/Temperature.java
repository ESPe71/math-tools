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

import java.util.Objects;

/**
 *
 * @author Enrico S. Penetti &lt;enrico@penetti.de&gt;
 */
public final class Temperature
{
  private final double value;
  private final TemperatureUnit unit;

  public Temperature(double value, TemperatureUnit unit)
  {
    this.value = value;
    this.unit = unit;
  }

  public double getValue()
  {
    return value;
  }

  public TemperatureUnit getUnit()
  {
    return unit;
  }

  public double getBaseTemperature()
  {
    return unit.getValue(getValue(), TemperatureUnit.KELVIN);
  }

  /**
   * Two {@link Temperature}s are equals, if the base temperature [K] are equals or differ less or equals to delta.
   *
   * @param temperature
   * @param delta
   * @return
   * @see #equals(java.lang.Object)
   */
  public boolean equals(Temperature temperature, Temperature delta)
  {
    double b1 = getBaseTemperature();
    double b2 = temperature.getBaseTemperature();
    double d = delta.getBaseTemperature();
    if (Double.compare(b1, b2) == 0)
    {
      return true;
    }
    else if (Math.abs(b1 - b2) <= d)
    {
      return true;
    }
    return false;
  }

  /**
   * Two {@link Temperature}s are equals, if the value and the {@link TemperatureUnit} are equals. If you wish to test the equality from the
   * absolute temperature, you must use {@link #equals(Temperature, Temperature) }.
   *
   * @param obj
   * @return
   * @see #equals(de.penetti.core.physics.Temperature, de.penetti.core.physics.Temperature)
   */
  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof Temperature)
    {
      double otherTemp = ((Temperature) obj).getValue();
      double thisTemp = getValue();
      if (Double.compare(otherTemp, thisTemp) == 0)
      {
        return ((Temperature) obj).getUnit() == getUnit();
      }
      else
      {
        return false;
      }
    }
    return false;
  }

  @Override
  public int hashCode()
  {
    int hash = 7;
    hash = 59 * hash + (int) (Double.doubleToLongBits(this.value) ^ (Double.doubleToLongBits(this.value) >>> 32));
    hash = 59 * hash + Objects.hashCode(this.unit);
    return hash;
  }
}
