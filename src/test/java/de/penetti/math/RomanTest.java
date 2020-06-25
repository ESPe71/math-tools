package de.penetti.math;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class RomanTest {

  public static Stream<Arguments> createTestData() {
    //                         value  substraction  simple     modern_1   modern_2    modern_12
    return Stream.of(arguments(1971, "MCMLXXI", "MDCCCCLXXI", "MCMLXXI", "MCMLXXI", "MCMLXXI"),
                     arguments(0, "", "", "", "", ""),
                     arguments(1, "I", "I", "I", "I", "I"),
                     arguments(2, "II", "II", "II", "II", "II"),
                     arguments(3, "III", "III", "IIV", "III", "IIV"),
                     arguments(4, "IV", "IIII", "IV", "IV", "IV"),
                     arguments(5, "V", "V", "V", "V", "V"),
                     arguments(6, "VI", "VI", "VI", "VI", "VI"),
                     arguments(7, "VII", "VII", "VII", "VII", "VII"),
                     arguments(8, "VIII", "VIII", "IIX", "VIII", "IIX"),
                     arguments(9, "IX", "VIIII", "IX", "IX", "IX"),
                     arguments(10, "X", "X", "X", "X", "X"),
                     arguments(11, "XI", "XI", "XI", "XI", "XI"),
                     arguments(12, "XII", "XII", "XII", "XII", "XII"),
                     arguments(13, "XIII", "XIII", "XIIV", "XIII", "XIIV"),
                     arguments(14, "XIV", "XIIII", "XIV", "XIV", "XIV"),
                     arguments(15, "XV", "XV", "XV", "XV", "XV"),
                     arguments(16, "XVI", "XVI", "XVI", "XVI", "XVI"),
                     arguments(17, "XVII", "XVII", "XVII", "XVII", "XVII"),
                     arguments(18, "XVIII", "XVIII", "XIIX", "XVIII", "XIIX"),
                     arguments(19, "XIX", "XVIIII", "XIX", "XIX", "XIX"),
                     arguments(20, "XX", "XX", "XX", "XX", "XX"),
                     arguments(30, "XXX", "XXX", "XXL", "XXX", "XXL"),
                     arguments(40, "XL", "XXXX", "XL", "XL", "XL"),
                     arguments(48, "XLVIII", "XXXXVIII", "XLIIX", "XLVIII", "IIL"),
                     arguments(49, "XLIX", "XXXXVIIII", "XLIX", "IL", "IL"),
                     arguments(50, "L", "L", "L", "L", "L"),
                     arguments(60, "LX", "LX", "LX", "LX", "LX"),
                     arguments(70, "LXX", "LXX", "LXX", "LXX", "LXX"),
                     arguments(80, "LXXX", "LXXX", "XXC", "LXXX", "XXC"),
                     arguments(90, "XC", "LXXXX", "XC", "XC", "XC"),
                     arguments(98, "XCVIII", "LXXXXVIII", "XCIIX", "XCVIII", "IIC"),
                     arguments(99, "XCIX", "LXXXXVIIII", "XCIX", "IC", "IC"),
                     arguments(100, "C", "C", "C", "C", "C"),
                     arguments(130, "CXXX", "CXXX", "CXXL", "CXXX", "CXXL"),
                     arguments(140, "CXL", "CXXXX", "CXL", "CXL", "CXL"),
                     arguments(148, "CXLVIII", "CXXXXVIII", "CXLIIX", "CXLVIII", "CIIL"),
                     arguments(149, "CXLIX", "CXXXXVIIII", "CXLIX", "CIL", "CIL"),
                     arguments(150, "CL", "CL", "CL", "CL", "CL"),
                     arguments(158, "CLVIII", "CLVIII", "CLIIX", "CLVIII", "CLIIX"),
                     arguments(159, "CLIX", "CLVIIII", "CLIX", "CLIX", "CLIX"),
                     arguments(160, "CLX", "CLX", "CLX", "CLX", "CLX"),
                     arguments(169, "CLXIX", "CLXVIIII", "CLXIX", "CLXIX", "CLXIX"),
                     arguments(170, "CLXX", "CLXX", "CLXX", "CLXX", "CLXX"),
                     arguments(180, "CLXXX", "CLXXX", "CXXC", "CLXXX", "CXXC"),
                     arguments(190, "CXC", "CLXXXX", "CXC", "CXC", "CXC"),
                     arguments(198, "CXCVIII", "CLXXXXVIII", "CXCIIX", "CXCVIII", "CIIC"),
                     arguments(199, "CXCIX", "CLXXXXVIIII", "CXCIX", "CIC", "CIC"),
                     arguments(200, "CC", "CC", "CC", "CC", "CC"),
                     arguments(300, "CCC", "CCC", "CCD", "CCC", "CCD"),
                     arguments(350, "CCCL", "CCCL", "CCDL", "CCCL", "CCDL"),
                     arguments(390, "CCCXC", "CCCLXXXX", "CCDXC", "CCCXC", "CCDXC"),
                     arguments(400, "CD", "CCCC", "CD", "CD", "CD"),
                     arguments(480, "CDLXXX", "CCCCLXXX", "CDXXC", "CDLXXX", "XXD"),
                     arguments(490, "CDXC", "CCCCLXXXX", "CDXC", "XD", "XD"),
                     arguments(498, "CDXCVIII", "CCCCLXXXXVIII", "CDXCIIX", "XDVIII", "IID"),
                     arguments(499, "CDXCIX", "CCCCLXXXXVIIII", "CDXCIX", "ID", "ID"),
                     arguments(500, "D", "D", "D", "D", "D"),
                     arguments(600, "DC", "DC", "DC", "DC", "DC"),
                     arguments(700, "DCC", "DCC", "DCC", "DCC", "DCC"),
                     arguments(800, "DCCC", "DCCC", "CCM", "DCCC", "CCM"),
                     arguments(900, "CM", "DCCCC", "CM", "CM", "CM"),
                     arguments(980, "CMLXXX", "DCCCCLXXX", "CMXXC", "CMLXXX", "XXM"),
                     arguments(990, "CMXC", "DCCCCLXXXX", "CMXC", "XM", "XM"),
                     arguments(998, "CMXCVIII", "DCCCCLXXXXVIII", "CMXCIIX", "XMVIII", "IIM"),
                     arguments(999, "CMXCIX", "DCCCCLXXXXVIIII", "CMXCIX", "IM", "IM"),
                     arguments(1000, "M", "M", "M", "M", "M"),
                     arguments(1666, "MDCLXVI", "MDCLXVI", "MDCLXVI", "MDCLXVI", "MDCLXVI"),
                     arguments(1998, "MCMXCVIII", "MDCCCCLXXXXVIII", "MCMXCIIX", "MXMVIII", "MIIM"),
                     arguments(1999, "MCMXCIX", "MDCCCCLXXXXVIIII", "MCMXCIX", "MIM", "MIM"),
                     arguments(2018, "MMXVIII", "MMXVIII", "MMXIIX", "MMXVIII", "MMXIIX"),
                     arguments(2019, "MMXIX", "MMXVIIII", "MMXIX", "MMXIX", "MMXIX"),
                     arguments(3999, "MMMCMXCIX", "MMMDCCCCLXXXXVIIII", "MMMCMXCIX", "MMMIM", "MMMIM"));
  }

  @Test
  void testForDuplicatedSigns() {
    long count = Roman.Sign.values().length;
    long signs = Arrays.stream(Roman.Sign.values()).map(s -> s.getSign()).distinct().count();
    long values = Arrays.stream(Roman.Sign.values()).map(s -> s.getValue()).distinct().count();
    assertEquals(count, signs);
    assertEquals(count, values);
  }

  @ParameterizedTest
  @MethodSource("createTestData")
  public void testToRoman(int arabic, String roman, String simple) {
    Roman r = new Roman(arabic);
    assertEquals(roman, r.getValue(), "for " + arabic);
  }

  @ParameterizedTest
  @MethodSource("createTestData")
  public void testToSimpleRoman(int arabic, String roman, String simple) {
    Roman r = new Roman(arabic);
    assertEquals(simple, r.getValue(Roman.Strategy.SIMPLE), "for " + arabic);
  }

  @ParameterizedTest
  @MethodSource("createTestData")
  public void testToModern1Roman(int arabic, String roman, String simple, String modern) {
    Roman r = new Roman(arabic);
    assertEquals(modern, r.getValue(Roman.Strategy.MODERN_1), "for " + arabic);
  }

  @ParameterizedTest
  @MethodSource("createTestData")
  public void testToModern2Roman(int arabic, String roman, String simple, String modern1, String modern2) {
    Roman r = new Roman(arabic);
    assertEquals(modern2, r.getValue(Roman.Strategy.MODERN_2), "for " + arabic);
  }

  @ParameterizedTest
  @MethodSource("createTestData")
  public void testToModern12Roman(int arabic, String roman, String simple,
                                  String modern1, String modern2, String modern12) {
    Roman r = new Roman(arabic);
    assertEquals(modern12, r.getValue(Roman.Strategy.MODERN_12), "for " + arabic);
  }

  @ParameterizedTest
  @MethodSource("createTestData")
  public void testToArabFromNORMAL(int arabic, String roman, String simple) {
    Roman r = new Roman(roman);
    assertEquals(arabic, r.getArabValue(), "for " + arabic);
  }

  @ParameterizedTest
  @MethodSource("createTestData")
  public void testToArabFromSimple(int arabic, String roman, String simple) {
    Roman r = new Roman(simple);
    assertEquals(arabic, r.getArabValue(), "for " + arabic);
  }

  @ParameterizedTest
  @MethodSource("createTestData")
  public void testToArabFromModern1(int arabic, String roman, String simple, String modern) {
    Roman r = new Roman(modern);
    assertEquals(arabic, r.getArabValue(), "for " + arabic);
  }

  @ParameterizedTest
  @MethodSource("createTestData")
  public void testToArabFromModern2(int arabic, String roman, String simple, String modern1, String modern2) {
    Roman r = new Roman(modern2);
    assertEquals(arabic, r.getArabValue(), "for " + arabic);
  }

  @ParameterizedTest
  @MethodSource("createTestData")
  public void testToArabFromModern12(int arabic, String roman, String simple,
                                     String modern1, String modern2, String modern12) {
    Roman r = new Roman(modern12);
    assertEquals(arabic, r.getArabValue(), "for " + arabic);
  }

  @ParameterizedTest
  @MethodSource("createTestData")
  public void testFromSubToSim(int arabic, String roman, String simple) {
    Roman r = new Roman(roman);
    assertEquals(simple, r.getValue(Roman.Strategy.SIMPLE), "for " + arabic);
  }

  @ParameterizedTest
  @MethodSource("createTestData")
  public void testFromSimToSub(int arabic, String roman, String simple) {
    Roman r = new Roman(simple);
    assertEquals(roman, r.getValue(), "for " + arabic);
  }

  @ParameterizedTest
  @MethodSource("createTestData")
  public void testFromSubToMod1(int arabic, String roman, String simple, String modern) {
    Roman r = new Roman(roman);
    assertEquals(modern, r.getValue(Roman.Strategy.MODERN_1), "for " + arabic);
  }

  @ParameterizedTest
  @MethodSource("createTestData")
  public void testFromMod1ToSub(int arabic, String roman, String simple, String modern) {
    Roman r = new Roman(modern);
    assertEquals(roman, r.getValue(), "for " + arabic);
  }

  @ParameterizedTest
  @MethodSource("createTestData")
  public void testFromSimToMod2(int arabic, String roman, String simple, String modern1, String modern2) {
    Roman r = new Roman(simple);
    assertEquals(modern2, r.getValue(Roman.Strategy.MODERN_2), "for " + arabic);
  }

  @ParameterizedTest
  @MethodSource("createTestData")
  public void testFromMod2ToSim(int arabic, String roman, String simple, String modern1, String modern2) {
    Roman r = new Roman(modern2);
    assertEquals(simple, r.getValue(Roman.Strategy.SIMPLE), "for " + arabic);
  }

  @ParameterizedTest
  @MethodSource("createTestData")
  public void testFromSubToMod12(int arabic, String roman, String simple, String modern1, String modern2, String modern12) {
    Roman r = new Roman(roman);
    assertEquals(modern12, r.getValue(Roman.Strategy.MODERN_12), "for " + arabic);
  }

  @ParameterizedTest
  @MethodSource("createTestData")
  public void testFromMod12ToSub(int arabic, String roman, String simple, String modern1, String modern2, String modern12) {
    Roman r = new Roman(modern12);
    assertEquals(roman, r.getValue(), "for " + arabic);
  }


  @ParameterizedTest
  @ValueSource(ints = {0, 1, 2, 10, 45, 100, 365, 185, 586, 1000, 1666, 2019, 3000, 3500, 3999})
  void testValueRange(int value) {
    Roman r = new Roman(value);
    System.out.printf("%5d => %s%n", value, r.toString());
  }

  @ParameterizedTest
  @ValueSource(ints = {-1, -2, -10, 4000, 5500, 4999})
  void testValueNotInRange(int value) {
    assertThrows(ArithmeticException.class, () -> new Roman(value));
  }

  @ParameterizedTest
  @ValueSource(strings = {"MMMM"})
  void testRomanNotInRange(String roman) {
    assertThrows(ArithmeticException.class, () -> new Roman(roman));
  }

  @ParameterizedTest
  @ValueSource(strings = {"MMMDCCCCLXXXXVIIII", "MMMCMXCIX", "MMMIM",
    "MMIIMDIDII", "MMIIMIMII"}) // In dieser Zeile sind die Zahlen NICHT korrekt, können aber dennoch gelesen werden.
  void testMaxSimple(String roman) {
    Roman r = new Roman(roman);
    System.out.printf("%20s => %s%n", roman, r.toString());
    assertEquals(3999, r.getArabValue());
  }

  @ParameterizedTest
  @ValueSource(strings = {"MDCCCCLXXI", "MCMLXXI",
    "XXMXMI", "MCMXLXXLI"}) // In dieser Zeile sind die Zahlen NICHT korrekt, können aber dennoch gelesen werden.
  void testYearOfBirth(String year) {
    Roman r = new Roman(year);
    System.out.printf("%20s => %s%n", year, r.toString());
    assertEquals(1971, r.getArabValue());
  }

  @ParameterizedTest
  @ValueSource(strings = {"Falsche Zahl", "XX I", "MK", "1971"})
  void testForInvalidSigns(String roman) {
    Throwable t = assertThrows(NumberFormatException.class, () -> new Roman(roman));
    System.err.println(t.getMessage());
    assertTrue(t.getMessage().startsWith("Invalid roman at index"));
  }

  @Test
  void testMax() {
    Roman roman = new Roman("MMMDCCCCLXXXXVIIII"); // einfache Schreibweise: maximaler Wert
    System.out.println(roman);
  }
}