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

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Enrico S. Penetti &lt;enrico@penetti.de&gt;
 */
public class LengthTest
{
  @Test
  public void testLength_superficial()
  {
    new Length(5, LengthUnit.FEET).hashCode();
    new Length(5, LengthUnit.FEET).toString();
  }

  @Test
  public void testGetLength()
  {
    Length l = new Length(2.5, LengthUnit.KILOMETER);
    assertEquals(2.5, l.getLength(), PhysicsUtils.DELTA);
  }

  @Test
  public void testGetUnit()
  {
    Length l = new Length(2.5, LengthUnit.KILOMETER);
    assertEquals(LengthUnit.KILOMETER, l.getUnit());
  }

  @Test
  public void testGetLengthByLengthUnit()
  {
    Length l = new Length(2.5, LengthUnit.KILOMETER);
    assertEquals(2500., l.getLength(LengthUnit.METER), PhysicsUtils.DELTA);
    
    l = new Length(2.5, LengthUnit.INCH);
    assertEquals(.0635, l.getLength(LengthUnit.METER), PhysicsUtils.DELTA);
  }
  
  @Test
  public void testEquals()
  {
    Length l1 = new Length(2.54, LengthUnit.CENTIMETER);
    Length l2 = new Length(1, LengthUnit.INCH);
    Length l3 = new Length(2.54, LengthUnit.CENTIMETER);
    Length l4 = new Length(l2.getLength(LengthUnit.CENTIMETER), LengthUnit.CENTIMETER);

    assertEquals(false, l1.equals(l2));
    assertEquals(true, l1.equals(l3));
    assertEquals(true, l1.equals(l4));
    
    double bl1 = l1.getLength(LengthUnit.METER);
    double bl2 = l2.getLength(LengthUnit.METER);
    double bl3 = l3.getLength(LengthUnit.METER);
    double bl4 = l4.getLength(LengthUnit.METER);
    assertEquals(.0254, bl1, PhysicsUtils.DELTA);
    assertEquals(.0254, bl2, PhysicsUtils.DELTA);
    assertEquals(.0254, bl3, PhysicsUtils.DELTA);
    assertEquals(.0254, bl4, PhysicsUtils.DELTA);
    
    assertFalse(l1.equals(5));
  }
  
  @Test
  public void testEqualsByLengthAndDelta()
  {
    Length l1 = new Length(2.54, LengthUnit.CENTIMETER);
    Length l2 = new Length(1, LengthUnit.INCH);
    Length delta = new Length(1, LengthUnit.MICROMETER);
    assertTrue(l1.equals(l2, delta));
    assertTrue(l1.equals(new Length(2.54, LengthUnit.CENTIMETER), delta));
    assertFalse(l2.equals(new Length(1, LengthUnit.CENTIMETER), delta));
  }
}
