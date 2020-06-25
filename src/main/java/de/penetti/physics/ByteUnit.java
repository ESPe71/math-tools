/*
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
 */
package de.penetti.physics;

import java.text.NumberFormat;
import java.util.ResourceBundle;

/**
 *
 * @author Enrico S. Penetti (enrico@penetti.de)
 */
public enum ByteUnit
{
  Binary
  {
    private static final int BINARY_MULTIPLE = 1024;
    private final String[] binaryUnits = {
                                           resource.getString("ByteUnit.Binary.B"),
                                           resource.getString("ByteUnit.Binary.KiB"),
                                           resource.getString("ByteUnit.Binary.MiB"),
                                           resource.getString("ByteUnit.Binary.GiB"),
                                           resource.getString("ByteUnit.Binary.TiB"),
                                           resource.getString("ByteUnit.Binary.PiB"),
                                           resource.getString("ByteUnit.Binary.EiB"),
                                           resource.getString("ByteUnit.Binary.ZiB"),
                                           resource.getString("ByteUnit.Binary.YiB")
                                         };
    @Override
    public String format(long size)
    {
      return format(size, BINARY_MULTIPLE, binaryUnits);
    }
  },
  Decimal
  {
    private static final int DECIMAL_MULTIPLE = 1000;
    private final String[] decimalUnits = {
                                            resource.getString("ByteUnit.Decimal.B"),
                                            resource.getString("ByteUnit.Decimal.KB"),
                                            resource.getString("ByteUnit.Decimal.MB"),
                                            resource.getString("ByteUnit.Decimal.GB"),
                                            resource.getString("ByteUnit.Decimal.TB"),
                                            resource.getString("ByteUnit.Decimal.PB"),
                                            resource.getString("ByteUnit.Decimal.EB"),
                                            resource.getString("ByteUnit.Decimal.ZB"),
                                            resource.getString("ByteUnit.Decimal.YB")
                                          };
    @Override
    public String format(long size)
    {
      return format(size, DECIMAL_MULTIPLE, decimalUnits);
    }
  };

  private static final NumberFormat NUMBERFORMAT = NumberFormat.getNumberInstance();

  @SuppressWarnings("checkstyle:visibilitymodifier")
  protected final ResourceBundle resource = ResourceBundle.getBundle("de.penetti.physics.byteunit");

  public abstract String format(long size);

  protected static String format(long size, int multiple, String[] units)
  {
    double dSize = size;
    double dMultiple = multiple;
    int i = 0;
    for (; i < units.length && dSize >= multiple; i++)
    {
      dSize /= dMultiple;
    }
    return String.format("%s %s", NUMBERFORMAT.format(dSize), units[i]);
  }
}
