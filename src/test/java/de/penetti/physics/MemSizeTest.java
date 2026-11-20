package de.penetti.physics;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MemSizeTest {

  private Locale defaultLocale;

  @BeforeEach
  public void setUpLocale() {
    defaultLocale = Locale.getDefault();
    Locale.setDefault(Locale.ENGLISH);
  }

  @AfterEach
  public void tearDownLocale() {
    Locale.setDefault(defaultLocale);
  }

  // --- Konstruktoren und Factories ---

  @Test
  public void constructorBigInteger() {
    assertEquals(BigInteger.valueOf(42), new MemSize(BigInteger.valueOf(42)).getBytes());
  }

  @Test
  public void constructorBigIntegerInvalid() {
    assertThrows(NullPointerException.class, () -> new MemSize((BigInteger) null));
    assertThrows(IllegalArgumentException.class, () -> new MemSize(BigInteger.valueOf(-1)));
  }

  @Test
  public void constructorLongBytes() {
    assertEquals(BigInteger.valueOf(7), new MemSize(7L).getBytes());
    assertThrows(IllegalArgumentException.class, () -> new MemSize(-1L));
  }

  @Test
  public void constructorLongWithUnit() {
    assertEquals(BigInteger.valueOf(2048), new MemSize(2L, MemSizeUnit.KiB).getBytes());
    assertEquals(BigInteger.valueOf(3_000_000), new MemSize(3L, MemSizeUnit.MB).getBytes());
    assertThrows(IllegalArgumentException.class, () -> new MemSize(-1L, MemSizeUnit.KB));
  }

  @Test
  public void constructorDoubleWithUnit() {
    assertEquals(BigInteger.valueOf(1536), new MemSize(1.5, MemSizeUnit.KiB).getBytes());
    assertEquals(BigInteger.valueOf(100), new MemSize(0.1, MemSizeUnit.KB).getBytes());
    assertThrows(IllegalArgumentException.class, () -> new MemSize(-0.5, MemSizeUnit.KB));
  }

  @Test
  public void constructorBigDecimalWithUnit() {
    assertEquals(BigInteger.valueOf(1_500_000_000L),
        new MemSize(new BigDecimal("1.5"), MemSizeUnit.GB).getBytes());
    assertThrows(NullPointerException.class, () -> new MemSize((BigDecimal) null, MemSizeUnit.KB));
    assertThrows(IllegalArgumentException.class,
        () -> new MemSize(new BigDecimal("-1"), MemSizeUnit.KB));
  }

  @Test
  public void constructorString() {
    assertEquals(BigInteger.valueOf(1024), new MemSize("1 KiB").getBytes());
  }

  @Test
  public void factories() {
    assertEquals(new MemSize(10L), MemSize.ofBytes(10));
    assertEquals(new MemSize(10L), MemSize.ofBytes(BigInteger.TEN));
    assertEquals(new MemSize(1024L), MemSize.of(1L, MemSizeUnit.KiB));
    assertEquals(new MemSize(1500L), MemSize.of(1.5, MemSizeUnit.KB));
    assertEquals(new MemSize(1500L), MemSize.of(new BigDecimal("1.5"), MemSizeUnit.KB));
    assertThrows(IllegalArgumentException.class, () -> MemSize.ofBytes(-1));
    assertThrows(IllegalArgumentException.class, () -> MemSize.of(-1L, MemSizeUnit.KB));
    assertThrows(IllegalArgumentException.class, () -> MemSize.of(-1.0, MemSizeUnit.KB));
    assertThrows(IllegalArgumentException.class,
        () -> MemSize.of(new BigDecimal("-1"), MemSizeUnit.KB));
  }

  @Test
  public void veryLargeValues() {
    MemSize yottabyte = MemSize.of(1L, MemSizeUnit.YB);
    assertEquals(BigInteger.TEN.pow(24), yottabyte.getBytes());
    assertEquals(BigInteger.TEN.pow(27), MemSize.of(1000L, MemSizeUnit.YB).getBytes());
  }

  // --- parse ---

  @Test
  public void parseWithUnits() {
    assertEquals(BigInteger.valueOf(1_000_000), MemSize.parse("1 MB").getBytes());
    assertEquals(BigInteger.valueOf(1_048_576), MemSize.parse("1 MiB").getBytes());
    assertEquals(BigInteger.valueOf(1536), MemSize.parse("1.5 KiB").getBytes());
    assertEquals(BigInteger.valueOf(512), MemSize.parse("512B").getBytes());
  }

  @Test
  public void parseIsCaseInsensitiveAndTolerantToWhitespace() {
    assertEquals(MemSize.of(1L, MemSizeUnit.GiB), MemSize.parse("  1 gib  "));
    assertEquals(MemSize.of(2L, MemSizeUnit.KB), MemSize.parse("2kb"));
  }

  @Test
  public void parseUsesDefaultLocaleGerman() {
    Locale.setDefault(Locale.GERMANY);
    assertEquals(BigInteger.valueOf(1500), MemSize.parse("1,5 KB").getBytes());
    assertEquals(BigInteger.valueOf(1536), MemSize.parse("1,5 KiB").getBytes());
    assertEquals(BigInteger.valueOf(500), MemSize.parse(",5 KB").getBytes());
    assertEquals(BigInteger.valueOf(1_000_000), MemSize.parse("1.000 KB").getBytes());
    assertEquals(BigInteger.valueOf(1_234_500), MemSize.parse("1.234,5 KB").getBytes());
    assertEquals(BigInteger.valueOf(2048), MemSize.parse("2048").getBytes());
  }

  @Test
  public void parseUsesDefaultLocaleEnglish() {
    assertEquals(BigInteger.valueOf(1500), MemSize.parse("1.5 KB").getBytes());
    assertEquals(BigInteger.valueOf(1_000_000), MemSize.parse("1,000 KB").getBytes());
    assertEquals(BigInteger.valueOf(1_234_500), MemSize.parse("1,234.5 KB").getBytes());
  }

  @Test
  public void parseRejectsWrongLocaleNotation() {
    // Englisch: Komma ist Tausendertrennzeichen und nur in Dreiergruppen gültig
    assertThrows(IllegalArgumentException.class, () -> MemSize.parse("1,5 KB"));
    assertThrows(IllegalArgumentException.class, () -> MemSize.parse("1,5,5 KB"));
    assertThrows(IllegalArgumentException.class, () -> MemSize.parse("1.5.5 KB"));
    assertThrows(IllegalArgumentException.class, () -> MemSize.parse("1,0000 KB"));
    // Deutsch: Punkt ist Tausendertrennzeichen und nur in Dreiergruppen gültig
    Locale.setDefault(Locale.GERMANY);
    assertThrows(IllegalArgumentException.class, () -> MemSize.parse("1.5 KB"));
    assertThrows(IllegalArgumentException.class, () -> MemSize.parse("1,000.5 KB"));
  }

  @Test
  public void parseRoundTripsToString() {
    MemSize size = MemSize.of(1234.5, MemSizeUnit.KB);
    for (Locale locale : new Locale[] {Locale.GERMANY, Locale.US, Locale.FRANCE}) {
      Locale.setDefault(locale);
      assertEquals(size, MemSize.parse(size.toString(MemSizeUnit.KB)), locale.toString());
      assertEquals(size, MemSize.parse(size.toString(MemSizeUnit.Byte)), locale.toString());
    }
  }

  @Test
  public void parseWithoutUnitIsBytes() {
    assertEquals(BigInteger.valueOf(123), MemSize.parse("123").getBytes());
  }

  @Test
  public void parseNullAndBlankIsZero() {
    assertEquals(BigInteger.ZERO, MemSize.parse(null).getBytes());
    assertEquals(BigInteger.ZERO, MemSize.parse("").getBytes());
    assertEquals(BigInteger.ZERO, MemSize.parse("   ").getBytes());
  }

  @Test
  public void parseTruncatesFractionalBytes() {
    assertEquals(BigInteger.ONE, MemSize.parse("1.9").getBytes());
  }

  @Test
  public void parseInvalid() {
    assertThrows(IllegalArgumentException.class, () -> MemSize.parse("abc"));
    assertThrows(IllegalArgumentException.class, () -> MemSize.parse("10 XB"));
    assertThrows(IllegalArgumentException.class, () -> MemSize.parse("1 KB extra"));
    assertThrows(IllegalArgumentException.class, () -> MemSize.parse("-5 MB"));
  }

  // --- Umrechnung und Ausgabe ---

  @Test
  public void toUnit() {
    assertEquals(0, new BigDecimal("1").compareTo(MemSize.ofBytes(1024).toUnit(MemSizeUnit.KiB)));
    assertEquals(0, new BigDecimal("1.5").compareTo(MemSize.ofBytes(1500).toUnit(MemSizeUnit.KB)));
    assertEquals(0, new BigDecimal("0.5").compareTo(MemSize.ofBytes(512).toUnit(MemSizeUnit.KiB)));
  }

  @Test
  public void toStringWithUnit() {
    assertEquals("1 KiB", MemSize.ofBytes(1024).toString(MemSizeUnit.KiB));
    assertEquals("1.5 KB", MemSize.ofBytes(1500).toString(MemSizeUnit.KB));
    assertEquals("1,000 B", MemSize.ofBytes(1000).toString(MemSizeUnit.Byte));
    assertEquals("0 KB", MemSize.ofBytes(0).toString(MemSizeUnit.KB));
    assertEquals("0.0000000001 YB", MemSize.ofBytes(100_000_000_000_000L).toString(MemSizeUnit.YB));
  }

  @Test
  public void toStringWithUnitUsesDefaultLocale() {
    MemSize size = MemSize.ofBytes(1500);
    Locale.setDefault(Locale.GERMANY);
    assertEquals("1,5 KB", size.toString(MemSizeUnit.KB));
    assertEquals("1.500 B", size.toString(MemSizeUnit.Byte));
    Locale.setDefault(Locale.US);
    assertEquals("1.5 KB", size.toString(MemSizeUnit.KB));
    assertEquals("1,500 B", size.toString(MemSizeUnit.Byte));
  }

  @Test
  public void humanReadableDecimal() {
    assertEquals("0 B", MemSize.ofBytes(0).humanReadable());
    assertEquals("999 B", MemSize.ofBytes(999).humanReadable());
    assertEquals("1 KB", MemSize.ofBytes(1000).humanReadable());
    assertEquals("1.5 KB", MemSize.ofBytes(1500).humanReadable());
    assertEquals("1 MB", MemSize.ofBytes(1_000_000).humanReadable());
    assertEquals("2.5 GB", MemSize.of(2.5, MemSizeUnit.GB).humanReadable());
    assertEquals("1 YB", MemSize.of(1L, MemSizeUnit.YB).humanReadable());
  }

  @Test
  public void humanReadableBinary() {
    assertEquals("0 B", MemSize.ofBytes(0).humanReadable(MemSizeUnitType.Binary));
    assertEquals("1023 B", MemSize.ofBytes(1023).humanReadable(MemSizeUnitType.Binary));
    assertEquals("1 KiB", MemSize.ofBytes(1024).humanReadable(MemSizeUnitType.Binary));
    assertEquals("976.56 KiB", MemSize.ofBytes(1_000_000).humanReadable(MemSizeUnitType.Binary));
    assertEquals("1 GiB", MemSize.of(1L, MemSizeUnit.GiB).humanReadable(MemSizeUnitType.Binary));
  }

  @Test
  public void humanReadableByteType() {
    assertEquals("5000 B", MemSize.ofBytes(5000).humanReadable(MemSizeUnitType.Byte));
  }

  @Test
  public void humanReadableUsesDefaultLocale() {
    MemSize size = MemSize.ofBytes(1500);
    Locale.setDefault(Locale.GERMANY);
    assertEquals("1,5 KB", size.humanReadable());
    assertEquals("1,5 KB", size.toString());
    assertEquals("1,46 KiB", size.humanReadable(MemSizeUnitType.Binary));
    assertEquals("1.000 YB", MemSize.of(1000L, MemSizeUnit.YB).humanReadable());
    Locale.setDefault(Locale.US);
    assertEquals("1.5 KB", size.humanReadable());
    assertEquals("1.46 KiB", size.humanReadable(MemSizeUnitType.Binary));
  }

  @Test
  public void humanReadableRoundsHalfUp() {
    assertEquals("1.01 KB", MemSize.ofBytes(1005).humanReadable());
    assertEquals("1 KB", MemSize.ofBytes(1004).humanReadable());
  }

  @Test
  public void toStringIsHumanReadable() {
    assertEquals("1.5 KB", MemSize.ofBytes(1500).toString());
  }

  // --- Arithmetik ---

  @Test
  public void add() {
    assertEquals(MemSize.ofBytes(1500), MemSize.ofBytes(1000).add(MemSize.ofBytes(500)));
  }

  @Test
  public void subtract() {
    assertEquals(MemSize.ofBytes(500), MemSize.ofBytes(1000).subtract(MemSize.ofBytes(500)));
    assertEquals(MemSize.ofBytes(0), MemSize.ofBytes(5).subtract(MemSize.ofBytes(5)));
  }

  @Test
  public void subtractNegativeResultThrows() {
    assertThrows(IllegalArgumentException.class,
        () -> MemSize.ofBytes(1).subtract(MemSize.ofBytes(2)));
  }

  // --- Vergleich ---

  @Test
  public void compareTo() {
    MemSize small = MemSize.ofBytes(1);
    MemSize large = MemSize.ofBytes(2);
    assertTrue(small.compareTo(large) < 0);
    assertTrue(large.compareTo(small) > 0);
    assertEquals(0, small.compareTo(MemSize.ofBytes(1)));
    assertThrows(NullPointerException.class, () -> small.compareTo(null));
  }

  @Test
  public void isEqualTo() {
    assertTrue(MemSize.parse("1 KiB").isEqualTo(MemSize.ofBytes(1024)));
    assertFalse(MemSize.ofBytes(1).isEqualTo(MemSize.ofBytes(2)));
    assertFalse(MemSize.ofBytes(1).isEqualTo(null));
  }

  @Test
  public void equalsAndHashCode() {
    MemSize a = MemSize.ofBytes(1024);
    MemSize b = MemSize.of(1L, MemSizeUnit.KiB);
    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
    assertFalse(a.equals(MemSize.ofBytes(1)));
    assertFalse(a.equals(null));
    assertFalse(a.equals("1024"));
  }
}
