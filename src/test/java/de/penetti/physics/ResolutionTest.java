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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 * @author Enrico S. Penetti &lt;enrico@penetti.de&gt;
 */
public class ResolutionTest
{
  @Test
  public void testResolution_superficial()
  {
    new Resolution(1, new Length(1, LengthUnit.FEET)).toString();
  }

  @Test
  public void testConstructor()
  {
    new Resolution(1, new Length(1, LengthUnit.FEET));
  }
  @Test
  public void testConstructorWithZeroPixel()
  {
    assertThrows(IllegalArgumentException.class, () -> {
      new Resolution(0, new Length(1, LengthUnit.FEET));
    });
  }
  @Test
  public void testConstructorWithNegativePixel()
  {
    assertThrows(IllegalArgumentException.class, () -> {
      new Resolution(-2, new Length(1, LengthUnit.FEET));
    });
  }
  @Test
  public void testConstructorWithZeroLength()
  {
    assertThrows(IllegalArgumentException.class, () -> {
      new Resolution(1, new Length(0, LengthUnit.FEET));
    });
  }
  @Test
  public void testConstructorWithNegativeLength()
  {
    assertThrows(IllegalArgumentException.class, () -> {
      new Resolution(1, new Length(-1, LengthUnit.FEET));
    });
  }

  @Test
  public void testGetPixelCount()
  {
    Resolution res = new Resolution(2, new Length(1, LengthUnit.METER));
    assertEquals(2, res.getDots());
  }

  @Test
  public void testGetLength()
  {
    Resolution res = new Resolution(2, new Length(1, LengthUnit.METER));
    assertEquals(new Length(1, LengthUnit.METER), res.getLength());
  }

  @Test
  public void testGetPixelPerMeter()
  {
    Resolution res = new Resolution(2, new Length(1, LengthUnit.METER));
    assertEquals(2., res.getDotsPerMeter(), PhysicsUtils.DELTA);
    
    res = new Resolution(2, new Length(1, LengthUnit.MILLIMETER));
    assertEquals(2000., res.getDotsPerMeter(), PhysicsUtils.DELTA);
    
    res = new Resolution(2, new Length(2.5, LengthUnit.MILLIMETER));
    assertEquals(800., res.getDotsPerMeter(), PhysicsUtils.DELTA);
    
    res = new Resolution(1, new Length(1, LengthUnit.INCH));
    assertEquals(39.37007874015748, res.getDotsPerMeter(), PhysicsUtils.DELTA);
  }
}
