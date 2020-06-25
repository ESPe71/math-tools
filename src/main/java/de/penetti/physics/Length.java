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

import java.text.DecimalFormat;
import java.util.Objects;

/**
 *
 * @author Enrico S. Penetti &lt;enrico@penetti.de&gt;
 */
public final class Length
{
  private final double length;
  private final LengthUnit unit;

  public Length(double length, LengthUnit unit)
  {
    this.length = length;
    this.unit = unit;
  }

  public double getLength()
  {
    return length;
  }

  public double getLength(LengthUnit asUnit)
  {
    return getUnit().getValue(getLength(), asUnit);
  }

  public LengthUnit getUnit()
  {
    return unit;
  }

  /**
   * Two {@link Length}s are equals, if the base length [m] are equals or differ less or equals to delta.
   *
   * @param len
   * @param delta
   * @return
   * @see #equals(java.lang.Object)
   */
  public boolean equals(Length len, Length delta)
  {
    double bl1 = getLength(LengthUnit.METER);
    double bl2 = len.getLength(LengthUnit.METER);
    double d = delta.getLength(LengthUnit.METER);
    if (Double.compare(bl1, bl2) == 0)
    {
      return true;
    }
    else if (Math.abs(bl1 - bl2) <= d)
    {
      return true;
    }
    return false;
  }

  /**
   * Two {@link Length}s are equals, if the lenght and the {@link LengthUnit} are equals. If you wish to test the equality from the absolute
   * length, you must use {@link #equals(de.penetti.physics.Length, de.penetti.physics.Length) }.
   *
   * @param obj
   * @return
   * @see #equals(de.penetti.physics.Length, de.penetti.physics.Length)
   */
  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof Length)
    {
      double otherLenght = ((Length) obj).getLength();
      double thisLenght = getLength();
      if (Double.compare(otherLenght, thisLenght) == 0)
      {
        return ((Length) obj).getUnit().equals(getUnit());
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
    hash = 59 * hash + (int) (Double.doubleToLongBits(this.length) ^ (Double.doubleToLongBits(this.length) >>> 32));
    hash = 59 * hash + Objects.hashCode(this.unit);
    return hash;
  }

  @Override
  public String toString()
  {
    return DecimalFormat.getInstance().format(getLength()) + getUnit().getUnitSign();
  }
}
