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
import java.text.NumberFormat;

/**
 *
 * @author Enrico S. Penetti &lt;enrico@penetti.de&gt;
 */
public final class Resolution
{
  private final int dots;
  private final Length length;

  public Resolution(int dots, Length length)
  {
    if (dots < 1)
    {
      throw new IllegalArgumentException("Pixel count can not be less than 1.");
    }
    if (length.getLength() <= .0)
    {
      throw new IllegalArgumentException("Length can not be less than or equal to zero.");
    }
    this.dots = dots;
    this.length = length;
  }

  public int getDots()
  {
    return dots;
  }

  public Length getLength()
  {
    return length;
  }

  public double getDotsPerMeter()
  {
    return getDots() / getLength().getLength(LengthUnit.METER);
  }

  public String getText()
  {
    NumberFormat numberFmt = DecimalFormat.getNumberInstance();
    Length len = getLength();
    StringBuilder sb = new StringBuilder();
    sb.append(getDots())
      .append("px/")
      .append(numberFmt.format(len.getLength()))
      .append(len.getUnit().getUnitSign());
    return sb.toString();
  }

  @Override
  public String toString()
  {
    return getText();
  }
}
