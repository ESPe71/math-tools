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

import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 *
 * @author Enrico S. Penetti &lt;enrico@penetti.de&gt;
 */
@SuppressWarnings("checkstyle:multiplestringliterals") // "fm"
public enum LengthUnit
{
  YOTTAMETER(Type.METRIC, "Ym",  1e24),
  ZETTAMETER(Type.METRIC, "Zm",  1e21),
  EXAMETER  (Type.METRIC, "Em",  1e18),
  PETAMETER (Type.METRIC, "Pm",  1e15),
  TERAMETER (Type.METRIC, "Tm",  1e12),
  GIGAMETER (Type.METRIC, "Gm",  1e9),
  MEGAMETER (Type.METRIC, "Mm",  1e6),
  KILOMETER (Type.METRIC, "km",  1e3),
  HECTOMETER(Type.METRIC, "hm",  1e2),
  DECAMETER (Type.METRIC, "dam", 1e1),
  METER     (Type.METRIC, "m",   1),       // Base unit
  DECIMETER (Type.METRIC, "dm",  1e-1),
  CENTIMETER(Type.METRIC, "cm",  1e-2),
  MILLIMETER(Type.METRIC, "mm",  1e-3),
  MICROMETER(Type.METRIC, "µm",  1e-6),
  NANOMETER (Type.METRIC, "nm",  1e-9),
  PICOMETER (Type.METRIC, "pm",  1e-12),
  FEMTOMETER(Type.METRIC, "fm",  1e-15),
  ATTOMETER (Type.METRIC, "am",  1e-18),
  ZEPTOMETER(Type.METRIC, "zm",  1e-21),
  YOCTOMETER(Type.METRIC, "ym",  1e-24),

  MILE      (Type.IMPERIAL, "mi",  1609.3440),             // 1Statute Mile=5280ft
  ROD       (Type.IMPERIAL, "rd",     5.0292),             // 198inch
  FATHOM    (Type.IMPERIAL, "fm",     1.8288),             // 72inch
  YARD      (Type.IMPERIAL, "yd",      .9144),             // 36inch
  FEET      (Type.IMPERIAL, "ft",      .3048),             // 12inch
  INCH      (Type.IMPERIAL, "in",      .0254),             // 1inch=2.54cm=.0254m
  POINT     (Type.IMPERIAL, "pt",  INCH.getFactor() / 72), // 1/72 inch oder 0,352777 mm

  AE        (Type.ASTRO, "AE",         149_597_870_700. ), // Astronomische Einheit
  LJ        (Type.ASTRO, "Lj",   9_460_730_472_580_800.),  // Lichtjahr
  PC        (Type.ASTRO, "pc",  30_856_775_777_948_584.);  // Parsec

  private static final ResourceBundle RESOURCE = ResourceBundle.getBundle("de.penetti.physics.lengthunit");

  private static final LengthUnit[] METRIC   = {YOTTAMETER, ZETTAMETER, EXAMETER, PETAMETER, TERAMETER,
                                                GIGAMETER, MEGAMETER, KILOMETER, HECTOMETER, DECAMETER,
                                                METER, DECIMETER, CENTIMETER, MILLIMETER, MICROMETER, NANOMETER,
                                                PICOMETER, FEMTOMETER, ATTOMETER, ZEPTOMETER, YOCTOMETER};
  private static final LengthUnit[] IMPERIAL = {MILE, ROD, FATHOM, YARD, FEET, INCH, POINT};
  private static final LengthUnit[] ASTRO    = {AE, LJ, PC};

  private final Type type;
  private final String unitSign;
  private final double factor;

  LengthUnit(Type type, String unitSign, double factorAgainstBase)
  {
    this.type = type;
    this.unitSign = unitSign;
    this.factor = factorAgainstBase;
  }

  public static LengthUnit[] getByType(Type type) {
    switch (type) {
      case METRIC: return Arrays.copyOf(METRIC, METRIC.length);
      case IMPERIAL: return Arrays.copyOf(IMPERIAL, IMPERIAL.length);
      case ASTRO: return Arrays.copyOf(ASTRO, ASTRO.length);
    }
    return new LengthUnit[0];
  }

  public static Optional<LengthUnit> getByTypeAndSign(Type type, String unitSign)
  {
    for (LengthUnit u : getByType(type))
    {
      if (u.getUnitSign().equals(unitSign))
      {
        return Optional.of(u);
      }
    }
    return Optional.empty();
  }

  public double getFactor()
  {
    return factor;
  }

  public Type getType() {
    return type;
  }

  public String getUnitSign()
  {
    return unitSign;
  }

  public double getValue(double value, LengthUnit toUnit)
  {
    return getValue(value, this, toUnit);
  }

  public static double getValue(double value, LengthUnit fromUnit, LengthUnit toUnit)
  {
    double factor = fromUnit.getFactor() / toUnit.getFactor();
    double retValue = value * factor;
    return retValue;
  }

  public String getName()
  {
    return RESOURCE.getString("LengthUnit.Name." + type + "." + getUnitSign());
  }

  @Override
  public String toString()
  {
    return getName();
  }

  public enum Type {
    METRIC,
    IMPERIAL,
    ASTRO
  }
}
