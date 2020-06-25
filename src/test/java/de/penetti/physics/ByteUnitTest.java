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

import org.junit.jupiter.api.Test;

import static de.penetti.physics.ByteUnit.Binary;
import static de.penetti.physics.ByteUnit.Decimal;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author Enrico S. Penetti (enrico@penetti.de)
 */
public class ByteUnitTest
{
  @Test
  public void testValues()
  {
    ByteUnit[] expResult = {Binary, Decimal};
    ByteUnit[] result = ByteUnit.values();
    assertArrayEquals(expResult, result);
  }

  @Test
  public void testValueOfBinary()
  {
    String name = "Binary";
    ByteUnit expResult = Binary;
    ByteUnit result = ByteUnit.valueOf(name);
    assertEquals(expResult, result);
  }
  @Test
  public void testValueOfDecimal()
  {
    String name = "Decimal";
    ByteUnit expResult = Decimal;
    ByteUnit result = ByteUnit.valueOf(name);
    assertEquals(expResult, result);
  }

  @Test
  public void testFormatDecimal()
  {
    assertEquals("1 KB", Decimal.format(1000));
    assertEquals("1,5 KB", Decimal.format(1500));
  }
  @Test
  public void testFormatBinary()
  {
    assertEquals("1 KiB", Binary.format(1024));
    assertEquals("1,5 KiB", Binary.format(1536));
  }
}
