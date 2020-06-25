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
public class TemperatureTest
{
  @Test
  public void testLength_superficial()
  {
    new Temperature(5, TemperatureUnit.CELSIUS).hashCode();
  }

  @Test
  public void testGetValue()
  {
    Temperature temp = new Temperature(37.5, TemperatureUnit.CELSIUS);
    assertEquals(37.5, temp.getValue(), PhysicsUtils.DELTA);
  }

  @Test
  public void testGetUnit()
  {
    Temperature temp = new Temperature(37.5, TemperatureUnit.CELSIUS);
    assertEquals(TemperatureUnit.CELSIUS, temp.getUnit());
  }

  @Test
  public void testGetBaseTemperature()
  {
    Temperature temp = new Temperature(37.5, TemperatureUnit.CELSIUS);
    assertEquals(310.65, temp.getBaseTemperature(), PhysicsUtils.DELTA);
    
    temp = new Temperature(37.5, TemperatureUnit.KELVIN);
    assertEquals(37.5, temp.getBaseTemperature(), PhysicsUtils.DELTA);
    
    temp = new Temperature(37.5, TemperatureUnit.FAHRENHEIT);
    assertEquals(276.205555556, temp.getBaseTemperature(), PhysicsUtils.DELTA);
    
    temp = new Temperature(37.5, TemperatureUnit.ROMER);
    assertEquals(330.292857142, temp.getBaseTemperature(), PhysicsUtils.DELTA);
  }

  @Test
  public void testEqualsByTemperatureAndDelta()
  {
    Temperature romerTemp = new Temperature(-83.40375, TemperatureUnit.ROMER); // 100K
    Temperature fahrenTemp = new Temperature(-279.67, TemperatureUnit.FAHRENHEIT); // 100K
    Temperature deltaTemp = new Temperature(.00001, TemperatureUnit.KELVIN);
    assertTrue(romerTemp.equals(fahrenTemp, deltaTemp));
    
    assertTrue(romerTemp.equals(new Temperature(-83.40375, TemperatureUnit.ROMER), deltaTemp));
    assertFalse(romerTemp.equals(new Temperature(50, TemperatureUnit.ROMER), deltaTemp));
  }

  @Test
  public void testEquals()
  {
    Temperature romerTemp = new Temperature(-83.40375, TemperatureUnit.ROMER); // 100K
    Temperature romerTemp2 = new Temperature(-83.40375, TemperatureUnit.ROMER); // 100K
    Temperature fahrenTemp = new Temperature(-279.67, TemperatureUnit.FAHRENHEIT); // 100K
    assertEquals(false, romerTemp.equals(fahrenTemp));
    assertEquals(true, romerTemp.equals(romerTemp2));
    
    assertFalse(romerTemp.equals(5));
    assertTrue(romerTemp.equals(new Temperature(-83.40375, TemperatureUnit.ROMER)));
    assertFalse(romerTemp.equals(new Temperature(-83.40375, TemperatureUnit.REAUMUR)));
  }
}
