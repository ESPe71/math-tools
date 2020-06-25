/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.penetti.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Enrico
 */
public class NumbersIntervalTest
{
  @Test
  public void testNumbersInterval_superficial()
  {
    NumbersInterval.values();
    NumbersInterval.valueOf("CLOSED");
  }
  
  @Test
  public void testCheck()
  {
    NumbersInterval.check(2, 3);
  }
  @Test
  public void testCheckWithException()
  {
    assertThrows(IllegalArgumentException.class, () -> {
      NumbersInterval.check(6, 3);
    });
  }

  @Test
  public void testNumbersInterval_Closed_isIn()
  {
    assertTrue(NumbersInterval.CLOSED.contains(1, 1, 10));
    assertTrue(NumbersInterval.CLOSED.contains(1, 10, 10));
    assertTrue(NumbersInterval.CLOSED.contains(1, 5, 10));
    assertFalse(NumbersInterval.CLOSED.contains(1, 0, 10));
    assertFalse(NumbersInterval.CLOSED.contains(1, 15, 10));
  }
  @Test
  public void testNumbersInterval_Open_isIn()
  {
    assertFalse(NumbersInterval.OPEN.contains(1, 1, 10));
    assertFalse(NumbersInterval.OPEN.contains(1, 10, 10));
    assertTrue(NumbersInterval.OPEN.contains(1, 5, 10));
    assertFalse(NumbersInterval.OPEN.contains(1, 0, 10));
    assertFalse(NumbersInterval.OPEN.contains(1, 15, 10));
  }
  @Test
  public void testNumbersInterval_LeftClosed_isIn()
  {
    assertTrue(NumbersInterval.LEFT_CLOSED.contains(1, 1, 10));
    assertFalse(NumbersInterval.LEFT_CLOSED.contains(1, 10, 10));
    assertTrue(NumbersInterval.LEFT_CLOSED.contains(1, 5, 10));
    assertFalse(NumbersInterval.LEFT_CLOSED.contains(1, 0, 10));
    assertFalse(NumbersInterval.LEFT_CLOSED.contains(1, 15, 10));
  }
  @Test
  public void testNumbersInterval_RightClosed_isIn()
  {
    assertFalse(NumbersInterval.RIGHT_CLOSED.contains(1, 1, 10));
    assertTrue(NumbersInterval.RIGHT_CLOSED.contains(1, 10, 10));
    assertTrue(NumbersInterval.RIGHT_CLOSED.contains(1, 5, 10));
    assertFalse(NumbersInterval.RIGHT_CLOSED.contains(1, 0, 10));
    assertFalse(NumbersInterval.RIGHT_CLOSED.contains(1, 15, 10));
  }
 
    @Test
  public void testIsIn()
  {
    assertTrue(NumbersInterval.CLOSED.contains(1, 2, 4));
    assertTrue(NumbersInterval.OPEN.contains(1, 2, 4));
    
    assertTrue(NumbersInterval.CLOSED.contains(2, 2, 4));
    assertTrue(NumbersInterval.CLOSED.contains(2, 4, 4));
    
    assertFalse(NumbersInterval.OPEN.contains(2, 2, 4));
    assertFalse(NumbersInterval.OPEN.contains(2, 4, 4));
    
    assertTrue(NumbersInterval.LEFT_CLOSED.contains(2, 2, 4));
    assertFalse(NumbersInterval.LEFT_CLOSED.contains(2, 4, 4));
    
    assertFalse(NumbersInterval.RIGHT_CLOSED.contains(2, 2, 4));
    assertTrue(NumbersInterval.RIGHT_CLOSED.contains(2, 4, 4));
  }
  @Test
  public void testIsInOpenWithException()
  {
    assertThrows(IllegalArgumentException.class, () -> {
      NumbersInterval.OPEN.contains(4, 2, 2);
    });
  }
  @Test
  public void testIsInClosedWithException()
  {
    assertThrows(IllegalArgumentException.class, () -> {
      NumbersInterval.CLOSED.contains(4, 2, 2);
    });
  }
  @Test
  public void testIsInLeftClosedWithException()
  {
    assertThrows(IllegalArgumentException.class, () -> {
      NumbersInterval.LEFT_CLOSED.contains(4, 2, 2);
    });
  }
  @Test
  public void testIsInRightClosedWithException()
  {
    assertThrows(IllegalArgumentException.class, () -> {
      NumbersInterval.RIGHT_CLOSED.contains(4, 2, 2);
    });
  }
}
