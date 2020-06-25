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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author Enrico S. Penetti &lt;enrico@penetti.de&gt;
 */
public class TemperatureUnitTest
{
  @Test
  public void testTemperatureUnit_superficial()
  {
    TemperatureUnit.CELSIUS.toString();
    TemperatureUnit.valueOf("CELSIUS");
  }

  @Test
  public void testGetValue()
  {
    assertEquals(0., TemperatureUnit.getValue(-273.15, TemperatureUnit.CELSIUS, TemperatureUnit.KELVIN));
    assertEquals(273.15, TemperatureUnit.getValue(0, TemperatureUnit.CELSIUS, TemperatureUnit.KELVIN));
    assertEquals(278.15, TemperatureUnit.getValue(5, TemperatureUnit.CELSIUS, TemperatureUnit.KELVIN));
    assertEquals(126.85, TemperatureUnit.getValue(400, TemperatureUnit.KELVIN, TemperatureUnit.CELSIUS), PhysicsUtils.DELTA);
    assertEquals(-459.67, TemperatureUnit.getValue(0., TemperatureUnit.KELVIN, TemperatureUnit.FAHRENHEIT));
    assertEquals(0., TemperatureUnit.getValue(-459.67, TemperatureUnit.FAHRENHEIT, TemperatureUnit.KELVIN));
    assertEquals(278.15, TemperatureUnit.getValue(41., TemperatureUnit.FAHRENHEIT, TemperatureUnit.KELVIN), PhysicsUtils.DELTA);
    assertEquals(5., TemperatureUnit.getValue(41., TemperatureUnit.FAHRENHEIT, TemperatureUnit.CELSIUS), PhysicsUtils.DELTA);
    assertEquals(98.6, TemperatureUnit.getValue(37., TemperatureUnit.CELSIUS, TemperatureUnit.FAHRENHEIT), PhysicsUtils.DELTA);
    
    assertEquals(98.6, TemperatureUnit.getValue(98.6, TemperatureUnit.KELVIN, TemperatureUnit.KELVIN), PhysicsUtils.DELTA);
    assertEquals(98.6, TemperatureUnit.getValue(98.6, TemperatureUnit.CELSIUS, TemperatureUnit.CELSIUS), PhysicsUtils.DELTA);
    assertEquals(98.6, TemperatureUnit.getValue(98.6, TemperatureUnit.FAHRENHEIT, TemperatureUnit.FAHRENHEIT), PhysicsUtils.DELTA);
    assertEquals(98.6, TemperatureUnit.getValue(98.6, TemperatureUnit.RANKINE, TemperatureUnit.RANKINE), PhysicsUtils.DELTA);
    assertEquals(98.6, TemperatureUnit.getValue(98.6, TemperatureUnit.REAUMUR, TemperatureUnit.REAUMUR), PhysicsUtils.DELTA);
    assertEquals(98.6, TemperatureUnit.getValue(98.6, TemperatureUnit.ROMER, TemperatureUnit.ROMER), PhysicsUtils.DELTA);
    assertEquals(98.6, TemperatureUnit.getValue(98.6, TemperatureUnit.DELISLE, TemperatureUnit.DELISLE), PhysicsUtils.DELTA);
    assertEquals(98.6, TemperatureUnit.getValue(98.6, TemperatureUnit.NEWTON, TemperatureUnit.NEWTON), PhysicsUtils.DELTA);
  }

  @Test
  public void testGetUnitSign()
  {
    String[] signs = new String[]{"°C", "K", "°F", "°Ra", "°Ré", "°Rø", "°De", "°N"};
    TemperatureUnit[] values = TemperatureUnit.values();
    for(int i=0; i<values.length; i++)
      assertEquals(signs[i], values[i].getUnitSign());
  }

  @Test
  public void testGetUnits()
  {
    TemperatureUnit[] result = TemperatureUnit.values();
    assertEquals(8, result.length);
  }

  @Test
  public void testGetBySign()
  {
    String[] signs = new String[]{"K", "°C", "°F", "°Ra", "°Ré", "°Rø", "°De", "°N",
                                  "C", "F", "Ra", "Ré", "Rø", "De", "N",
                                  "°Re", "Re", "°Ro", "Ro"};
    TemperatureUnit[] expected = new TemperatureUnit[]{TemperatureUnit.KELVIN, TemperatureUnit.CELSIUS, TemperatureUnit.FAHRENHEIT, 
                                                       TemperatureUnit.RANKINE, TemperatureUnit.REAUMUR, TemperatureUnit.ROMER,
                                                       TemperatureUnit.DELISLE, TemperatureUnit.NEWTON,
                                                       TemperatureUnit.CELSIUS, TemperatureUnit.FAHRENHEIT, TemperatureUnit.RANKINE,
                                                       TemperatureUnit.REAUMUR, TemperatureUnit.ROMER, TemperatureUnit.DELISLE,
                                                       TemperatureUnit.NEWTON, TemperatureUnit.REAUMUR, TemperatureUnit.REAUMUR,
                                                       TemperatureUnit.ROMER, TemperatureUnit.ROMER};
    
    for(int i=0; i<signs.length; i++)
      assertEquals(expected[i], TemperatureUnit.getBySign(signs[i]).get());
    
    Optional<TemperatureUnit> tu = TemperatureUnit.getBySign("ThereAreNotExistant");
    assertEquals(Optional.empty(), tu);
  }

  @Test
  public void testGetValue_double_TemperatureUnit()
  {
    assertEquals(0., TemperatureUnit.CELSIUS.getValue(-273.15, TemperatureUnit.KELVIN));
    assertEquals(273.15, TemperatureUnit.CELSIUS.getValue(0, TemperatureUnit.KELVIN));
    assertEquals(278.15, TemperatureUnit.CELSIUS.getValue(5, TemperatureUnit.KELVIN));
    assertEquals(126.85, TemperatureUnit.KELVIN.getValue(400, TemperatureUnit.CELSIUS), PhysicsUtils.DELTA);
    assertEquals(-459.67, TemperatureUnit.KELVIN.getValue(0., TemperatureUnit.FAHRENHEIT));
    assertEquals(0., TemperatureUnit.FAHRENHEIT.getValue(-459.67, TemperatureUnit.KELVIN));
    assertEquals(278.15, TemperatureUnit.FAHRENHEIT.getValue(41., TemperatureUnit.KELVIN), PhysicsUtils.DELTA);
    assertEquals(5., TemperatureUnit.FAHRENHEIT.getValue(41., TemperatureUnit.CELSIUS), PhysicsUtils.DELTA);
    assertEquals(98.6, TemperatureUnit.CELSIUS.getValue(37., TemperatureUnit.FAHRENHEIT), PhysicsUtils.DELTA);
    
    assertEquals(98.6, TemperatureUnit.KELVIN.getValue(98.6, TemperatureUnit.KELVIN), PhysicsUtils.DELTA);
    assertEquals(98.6, TemperatureUnit.CELSIUS.getValue(98.6, TemperatureUnit.CELSIUS), PhysicsUtils.DELTA);
    assertEquals(98.6, TemperatureUnit.FAHRENHEIT.getValue(98.6, TemperatureUnit.FAHRENHEIT), PhysicsUtils.DELTA);
    assertEquals(98.6, TemperatureUnit.RANKINE.getValue(98.6, TemperatureUnit.RANKINE), PhysicsUtils.DELTA);
    assertEquals(98.6, TemperatureUnit.REAUMUR.getValue(98.6, TemperatureUnit.REAUMUR), PhysicsUtils.DELTA);
    assertEquals(98.6, TemperatureUnit.ROMER.getValue(98.6, TemperatureUnit.ROMER), PhysicsUtils.DELTA);
    assertEquals(98.6, TemperatureUnit.DELISLE.getValue(98.6, TemperatureUnit.DELISLE), PhysicsUtils.DELTA);
    assertEquals(98.6, TemperatureUnit.NEWTON.getValue(98.6, TemperatureUnit.NEWTON), PhysicsUtils.DELTA);
  }

    @Test
  public void testGetName()
  {
    String[] namesDe = new String[]{"Grad Celsius", "Kelvin", "Grad Fahrenheit", "Grad Rankine", "Grad Réaumur",
                                    "Grad Rømer", "Grad Delisle", "Grad Newton"};
    String[] namesEn = new String[]{"Degree Celsius", "Kelvin", "Degree Fahrenheit", "Degree Rankine", "Degree Réaumur",
                                    "Degree Rømer", "Degree Delisle", "Degree Newton"};
    TemperatureUnit[] units = TemperatureUnit.values();
    for(int i=0; i<units.length; i++)
    {
      String nameDe = namesDe[i];
      String nameEn = namesEn[i];
      String name = units[i].getName();
      assertEquals(true, name.equals(nameDe) || name.equals(nameEn));
    }
  }
}
