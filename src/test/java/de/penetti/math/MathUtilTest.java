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
package de.penetti.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 *
 * @author Enrico S. Penetti &lt;enrico@penetti.de&gt;
 */
public class MathUtilTest
{
  @Test
  public void testAdjustRotation()
  {
    assertEquals(Math.toRadians(90), MathUtil.adjustAngle(Math.toRadians(90)));
    assertEquals(Math.toRadians(0), MathUtil.adjustAngle(Math.toRadians(0)));
    assertEquals(Math.toRadians(180), MathUtil.adjustAngle(Math.toRadians(180)));
    assertEquals(Math.toRadians(0), MathUtil.adjustAngle(Math.toRadians(360)));
    assertEquals(Math.toRadians(40), MathUtil.adjustAngle(Math.toRadians(400)), TestUtils.DELTA_9);
    assertEquals(Math.toRadians(320), MathUtil.adjustAngle(Math.toRadians(-40)), TestUtils.DELTA_9);
  }

  @Test
  public void testRound()
  {
    assertEquals(4.0, MathUtil.round(4.4), TestUtils.DELTA_9);
    assertEquals(5.0, MathUtil.round(4.5), TestUtils.DELTA_9);
    assertEquals(3.0, MathUtil.round(3.4), TestUtils.DELTA_9);
    assertEquals(4.0, MathUtil.round(3.5), TestUtils.DELTA_9);

    assertEquals(5.77, MathUtil.round(5.765, 2), TestUtils.DELTA_9);
    assertEquals(5.76, MathUtil.round(5.763, 2), TestUtils.DELTA_9);
    assertEquals(5.77, MathUtil.round(5.767, 2), TestUtils.DELTA_9);
  }
}
