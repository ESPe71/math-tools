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
public class LengthUnitTest
{
  @Test
  public void testLengthUnit_superficial()
  {
    LengthUnit.METER.toString();
    LengthUnit.valueOf("METER");
  }

  @Test
  public void testGetMetric()
  {
    LengthUnit[] result = LengthUnit.getByType(LengthUnit.Type.METRIC);
    assertEquals(21, result.length);
  }

  @Test
  public void testGetImperial()
  {
    LengthUnit[] result = LengthUnit.getByType(LengthUnit.Type.IMPERIAL);
    assertEquals(7, result.length);
  }

  @Test
  public void testGetAstro()
  {
    LengthUnit[] result = LengthUnit.getByType(LengthUnit.Type.ASTRO);
    assertEquals(3, result.length);
  }

  @Test
  public void testGetFactor()
  {
    LengthUnit[] result = LengthUnit.getByType(LengthUnit.Type.METRIC);
    double[] expFactor = {1e24,1e21,1e18,1e15,1e12,1e9,1e6,1e3,1e2,1e1,
                          1,1e-1,1e-2,1e-3,1e-6,1e-9,1e-12,1e-15,1e-18,1e-21,1e-24};
    for(int i=0; i<result.length; i++)
      assertEquals(expFactor[i], result[i].getFactor(), PhysicsUtils.DELTA);
    
    result = LengthUnit.getByType(LengthUnit.Type.IMPERIAL);
    expFactor = new double[]{1609.3440, 5.0292, 1.8288, .9144, .3048, .0254, .000352777777};
    for(int i=0; i<result.length; i++)
      assertEquals(expFactor[i], result[i].getFactor(), PhysicsUtils.DELTA);
  }

  @Test
  public void testGetUnitSign()
  {
    LengthUnit[] result = LengthUnit.getByType(LengthUnit.Type.METRIC);
    String[] expSigns = {"Ym","Zm","Em","Pm","Tm","Gm","Mm","km","hm","dam",
                         "m","dm","cm","mm","µm","nm","pm","fm","am","zm","ym"};
    for(int i=0; i<result.length; i++)
      assertEquals(expSigns[i], result[i].getUnitSign());

    result = LengthUnit.getByType(LengthUnit.Type.IMPERIAL);
    expSigns = new String[]{"mi","rd","fm","yd","ft","in","pt"};
    for(int i=0; i<result.length; i++)
      assertEquals(expSigns[i], result[i].getUnitSign());
  }

  @Test
  public void testGetValue_double_LengthUnit()
  {
    assertEquals(1., LengthUnit.METER.getValue(1000, LengthUnit.KILOMETER));
    
    assertEquals(2.54, LengthUnit.INCH.getValue(1, LengthUnit.CENTIMETER));
    assertEquals(.352777777777777, LengthUnit.POINT.getValue(1, LengthUnit.MILLIMETER), PhysicsUtils.DELTA);
    assertEquals(1/.352777777777777, LengthUnit.MILLIMETER.getValue(1, LengthUnit.POINT), PhysicsUtils.DELTA);
  }
    @Test
  public void testGetValue_3args()
  {
    assertEquals(1., LengthUnit.getValue(1000, LengthUnit.METER, LengthUnit.KILOMETER));
    
    assertEquals(2.54, LengthUnit.getValue(1, LengthUnit.INCH, LengthUnit.CENTIMETER));
    assertEquals(2.54*5.4, LengthUnit.getValue(5.4, LengthUnit.INCH, LengthUnit.CENTIMETER), PhysicsUtils.DELTA);
  }
  
  @Test
  public void testGetBySign()
  {
    Optional<LengthUnit> oUnit = LengthUnit.getByTypeAndSign(LengthUnit.Type.METRIC, "m");
    assertEquals(true, oUnit.isPresent());
    assertEquals(LengthUnit.METER, oUnit.get());
    
    oUnit = LengthUnit.getByTypeAndSign(LengthUnit.Type.METRIC, "Zm");
    assertEquals(true, oUnit.isPresent());
    assertEquals(LengthUnit.ZETTAMETER, oUnit.get());

    oUnit = LengthUnit.getByTypeAndSign(LengthUnit.Type.METRIC, "km");
    assertEquals(true, oUnit.isPresent());
    assertEquals(LengthUnit.KILOMETER, oUnit.get());
    
    oUnit = LengthUnit.getByTypeAndSign(LengthUnit.Type.METRIC, "µm");
    assertEquals(true, oUnit.isPresent());
    assertEquals(LengthUnit.MICROMETER, oUnit.get());

    oUnit = LengthUnit.getByTypeAndSign(LengthUnit.Type.METRIC, "fm");
    assertEquals(true, oUnit.isPresent());
    assertEquals(LengthUnit.FEMTOMETER, oUnit.get());

    oUnit = LengthUnit.getByTypeAndSign(LengthUnit.Type.IMPERIAL, "fm");
    assertEquals(true, oUnit.isPresent());
    assertEquals(LengthUnit.FATHOM, oUnit.get());

    oUnit = LengthUnit.getByTypeAndSign(LengthUnit.Type.IMPERIAL, "mi");
    assertEquals(true, oUnit.isPresent());
    assertEquals(LengthUnit.MILE, oUnit.get());
    
    oUnit = LengthUnit.getByTypeAndSign(LengthUnit.Type.IMPERIAL, "yd");
    assertEquals(true, oUnit.isPresent());
    assertEquals(LengthUnit.YARD, oUnit.get());
    
    oUnit = LengthUnit.getByTypeAndSign(LengthUnit.Type.IMPERIAL, "in");
    assertEquals(true, oUnit.isPresent());
    assertEquals(LengthUnit.INCH, oUnit.get());
    
    oUnit = LengthUnit.getByTypeAndSign(LengthUnit.Type.METRIC, "unsinn");
    assertEquals(false, oUnit.isPresent());
  }
  
  @Test
  public void testGetName()
  {
    String name = LengthUnit.YARD.getName();
    boolean b = name.equals("Yard") || name.equals("Schritt");
    assertEquals(true, b);
    
    name = LengthUnit.METER.getName();
    b = name.equals("Metre") || name.equals("Meter");
    assertEquals(true, b);

    name = LengthUnit.INCH.getName();
    b = name.equals("Inch") || name.equals("Zoll");
    assertEquals(true, b);
    
    name = LengthUnit.KILOMETER.getName();
    b = name.equals("Kilometre") || name.equals("Kilometer");
    assertEquals(true, b);

    name = LengthUnit.FATHOM.getName();
    b = name.equals("Fathom") || name.equals("Faden");
    assertEquals(true, b);
  }
  
  @Test
  public void testExtremeValues()
  {
    assertEquals(1e48, LengthUnit.YOTTAMETER.getValue(1, LengthUnit.YOCTOMETER));
    assertEquals(1e48, new Length(1e48, LengthUnit.YOCTOMETER).getLength());
    assertEquals(LengthUnit.YOCTOMETER.getValue(1e48, LengthUnit.METER), new Length(1e24, LengthUnit.YOCTOMETER).getLength());
    
    // Some commas are not considered because no meaningful units are used.
    assertEquals(           2348763_362573940928387371098437.85438756432, 
                 new Length(2348763.362573940928387371098437_85438756432, LengthUnit.YOTTAMETER).getLength(LengthUnit.METER));
    assertEquals(          0.000000000000000000000000_8646583926384958_10984635586952647,
                 new Length(                         .8646583926384958_10984635586952647, LengthUnit.YOCTOMETER).getLength(LengthUnit.METER));
    assertEquals(          0.8646583926384958_10984635586952647e-24,
                 new Length(.8646583926384958_10984635586952647, LengthUnit.YOCTOMETER).getLength(LengthUnit.METER));
  }
}
