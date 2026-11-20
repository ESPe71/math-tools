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

  // --- Factories ---

  @Test
  public void ofBytesBigInteger() {
    assertEquals(BigInteger.valueOf(42), MemSize.ofBytes(BigInteger.valueOf(42)).getBytes());
    assertThrows(NullPointerException.class, () -> MemSize.ofBytes((BigInteger) null));
    assertThrows(IllegalArgumentException.class, () -> MemSize.ofBytes(BigInteger.valueOf(-1)));
  }

  @Test
  public void ofBytesLong() {
    assertEquals(BigInteger.valueOf(7), MemSize.ofBytes(7L).getBytes());
    assertEquals(BigInteger.ZERO, MemSize.ofBytes(0L).getBytes());
    assertThrows(IllegalArgumentException.class, () -> MemSize.ofBytes(-1L));
  }

  @Test
  public void ofLongWithUnit() {
    assertEquals(BigInteger.valueOf(2048), MemSize.of(2L, MemSizeUnit.KiB).getBytes());
    assertEquals(BigInteger.valueOf(3_000_000), MemSize.of(3L, MemSizeUnit.MB).getBytes());
    assertEquals(BigInteger.valueOf(5), MemSize.of(5L, MemSizeUnit.Byte).getBytes());
    assertThrows(IllegalArgumentException.class, () -> MemSize.of(-1L, MemSizeUnit.KB));
  }

  @Test
  public void ofDoubleWithUnit() {
    assertEquals(BigInteger.valueOf(1536), MemSize.of(1.5, MemSizeUnit.KiB).getBytes());
    assertEquals(BigInteger.valueOf(100), MemSize.of(0.1, MemSizeUnit.KB).getBytes());
    assertThrows(IllegalArgumentException.class, () -> MemSize.of(-0.5, MemSizeUnit.KB));
    assertThrows(IllegalArgumentException.class, () -> MemSize.of(Double.NaN, MemSizeUnit.KB));
    assertThrows(IllegalArgumentException.class,
        () -> MemSize.of(Double.POSITIVE_INFINITY, MemSizeUnit.KB));
  }

  @Test
  public void ofBigDecimalWithUnit() {
    assertEquals(BigInteger.valueOf(1_500_000_000L),
        MemSize.of(new BigDecimal("1.5"), MemSizeUnit.GB).getBytes());
    assertThrows(NullPointerException.class, () -> MemSize.of((BigDecimal) null, MemSizeUnit.KB));
    assertThrows(NullPointerException.class, () -> MemSize.of(BigDecimal.ONE, null));
    assertThrows(IllegalArgumentException.class,
        () -> MemSize.of(new BigDecimal("-1"), MemSizeUnit.KB));
  }

  @Test
  public void ofRejectsNegativeValueEvenIfItTruncatesToZero() {
    assertThrows(IllegalArgumentException.class,
        () -> MemSize.of(new BigDecimal("-0.0001"), MemSizeUnit.Byte));
  }

  @Test
  public void factoriesAreEquivalent() {
    assertEquals(MemSize.ofBytes(10L), MemSize.ofBytes(BigInteger.TEN));
    assertEquals(MemSize.ofBytes(1024L), MemSize.of(1L, MemSizeUnit.KiB));
    assertEquals(MemSize.ofBytes(1500L), MemSize.of(1.5, MemSizeUnit.KB));
    assertEquals(MemSize.ofBytes(1500L), MemSize.of(new BigDecimal("1.5"), MemSizeUnit.KB));
    assertEquals(MemSize.ofBytes(1024L), MemSize.parse("1 KiB"));
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
  public void parseIsIndependentOfDefaultLocale() {
    for (Locale locale : new Locale[] {Locale.US, Locale.GERMANY, Locale.FRANCE}) {
      Locale.setDefault(locale);
      assertEquals(BigInteger.valueOf(1500), MemSize.parse("1.5 KB").getBytes(), locale.toString());
      assertEquals(BigInteger.valueOf(1_000_000), MemSize.parse("1,000 KB").getBytes(),
          locale.toString());
      assertEquals(BigInteger.valueOf(1_234_500), MemSize.parse("1,234.5 KB").getBytes(),
          locale.toString());
      assertEquals(MemSize.parse("1.5 KB"), MemSize.parse("1.5 KB"), locale.toString());
      assertThrows(IllegalArgumentException.class, () -> MemSize.parse("1,5 KB"));
      assertThrows(IllegalArgumentException.class, () -> MemSize.parse("1,5,5 KB"));
      assertThrows(IllegalArgumentException.class, () -> MemSize.parse("1.234,5 KB"));
    }
  }

  @Test
  public void parseLocalizedGerman() {
    Locale.setDefault(Locale.GERMANY);
    assertEquals(BigInteger.valueOf(1500), MemSize.parseLocalized("1,5 KB").getBytes());
    assertEquals(BigInteger.valueOf(1536), MemSize.parseLocalized("1,5 KiB").getBytes());
    assertEquals(BigInteger.valueOf(500), MemSize.parseLocalized(",5 KB").getBytes());
    assertEquals(BigInteger.valueOf(1_000_000), MemSize.parseLocalized("1.000 KB").getBytes());
    assertEquals(BigInteger.valueOf(1_234_500), MemSize.parseLocalized("1.234,5 KB").getBytes());
    assertEquals(BigInteger.valueOf(2048), MemSize.parseLocalized("2048").getBytes());
  }

  @Test
  public void parseLocalizedEnglish() {
    assertEquals(BigInteger.valueOf(1500), MemSize.parseLocalized("1.5 KB").getBytes());
    assertEquals(BigInteger.valueOf(1_000_000), MemSize.parseLocalized("1,000 KB").getBytes());
    assertEquals(BigInteger.valueOf(1_234_500), MemSize.parseLocalized("1,234.5 KB").getBytes());
  }

  @Test
  public void parseLocalizedRejectsWrongNotation() {
    // Englisch: Komma ist Tausendertrennzeichen und nur in Dreiergruppen gültig
    assertThrows(IllegalArgumentException.class, () -> MemSize.parseLocalized("1,5 KB"));
    assertThrows(IllegalArgumentException.class, () -> MemSize.parseLocalized("1,5,5 KB"));
    assertThrows(IllegalArgumentException.class, () -> MemSize.parseLocalized("1.5.5 KB"));
    assertThrows(IllegalArgumentException.class, () -> MemSize.parseLocalized("1,0000 KB"));
    // Deutsch: Punkt ist Tausendertrennzeichen und nur in Dreiergruppen gültig
    Locale.setDefault(Locale.GERMANY);
    assertThrows(IllegalArgumentException.class, () -> MemSize.parseLocalized("1.5 KB"));
    assertThrows(IllegalArgumentException.class, () -> MemSize.parseLocalized("1,000.5 KB"));
  }

  @Test
  public void parseLocalizedRoundTripsToString() {
    MemSize size = MemSize.of(1234.5, MemSizeUnit.KB);
    for (Locale locale : new Locale[] {Locale.GERMANY, Locale.US, Locale.FRANCE}) {
      Locale.setDefault(locale);
      assertEquals(size, MemSize.parseLocalized(size.toString(MemSizeUnit.KB)), locale.toString());
      assertEquals(size, MemSize.parseLocalized(size.toString(MemSizeUnit.Byte)), locale.toString());
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

  // --- Variable Nachkommastellen ---

  @Test
  public void humanReadableWithFractionDigits() {
    assertEquals("2 KB", MemSize.ofBytes(1500).humanReadable(0));
    assertEquals("1.5 KB", MemSize.ofBytes(1500).humanReadable(1));
    assertEquals("1 KB", MemSize.ofBytes(1005).humanReadable(0));
    assertEquals("1 KB", MemSize.ofBytes(1005).humanReadable(1));
    assertEquals("1.005 KB", MemSize.ofBytes(1005).humanReadable(3));
    assertEquals("1.5 MB", MemSize.ofBytes(1_465_000).humanReadable(1));
    assertEquals("1.47 MB", MemSize.ofBytes(1_465_000).humanReadable(2));
    assertEquals("1.465 MB", MemSize.ofBytes(1_465_000).humanReadable(3));
    assertEquals("1.465 MB", MemSize.ofBytes(1_465_000).humanReadable(8));
  }

  @Test
  public void humanReadableBinaryWithFractionDigits() {
    MemSize size = MemSize.ofBytes(1500); // 1,46484375 KiB
    assertEquals("1 KiB", size.humanReadable(MemSizeUnitType.Binary, 0));
    assertEquals("1.465 KiB", size.humanReadable(MemSizeUnitType.Binary, 3));
    assertEquals("1.46484 KiB", size.humanReadable(MemSizeUnitType.Binary, 5));
  }

  @Test
  public void humanReadableFractionDigitsUseDefaultLocale() {
    Locale.setDefault(Locale.GERMANY);
    assertEquals("1,465 MB", MemSize.ofBytes(1_465_000).humanReadable(3));
    Locale.setDefault(Locale.US);
    assertEquals("1.465 MB", MemSize.ofBytes(1_465_000).humanReadable(3));
  }

  @Test
  public void humanReadableDefaultsStayTheSame() {
    MemSize size = MemSize.ofBytes(1_465_000);
    assertEquals(size.humanReadable(MemSize.DEFAULT_HUMAN_READABLE_DIGITS), size.humanReadable());
    assertEquals(size.humanReadable(MemSizeUnitType.Binary, MemSize.DEFAULT_HUMAN_READABLE_DIGITS),
        size.humanReadable(MemSizeUnitType.Binary));
    assertEquals("1.47 MB", size.humanReadable());
  }

  @Test
  public void humanReadableRoundsUpToNextUnit() {
    assertEquals("1 MB", MemSize.ofBytes(999_999).humanReadable());
    assertEquals("1 MB", MemSize.ofBytes(999_995).humanReadable());
    assertEquals("999.99 KB", MemSize.ofBytes(999_994).humanReadable());
    assertEquals("1 GB", MemSize.ofBytes(999_999_999).humanReadable());
    assertEquals("1 MB", MemSize.ofBytes(999_500).humanReadable(0));
    assertEquals("999 KB", MemSize.ofBytes(999_499).humanReadable(0));
    assertEquals("999.5 KB", MemSize.ofBytes(999_499).humanReadable(1));
  }

  @Test
  public void humanReadableBinaryRoundsUpToNextUnit() {
    assertEquals("1 MiB", MemSize.ofBytes(1_048_575).humanReadable(MemSizeUnitType.Binary));
    assertEquals("1,023.99 KiB", MemSize.ofBytes(1_048_565).humanReadable(MemSizeUnitType.Binary));
  }

  @Test
  public void humanReadableLargestUnitCannotRoundUp() {
    MemSize size = MemSize.of(new BigDecimal("999.999"), MemSizeUnit.YB);
    assertEquals("1,000 YB", size.humanReadable(2));
  }

  @Test
  public void humanReadableBytesHaveNoFractionDigits() {
    assertEquals("999 B", MemSize.ofBytes(999).humanReadable(5));
    assertEquals("0 B", MemSize.ofBytes(0).humanReadable(5));
  }

  @Test
  public void toStringWithFractionDigits() {
    assertEquals("2 KB", MemSize.ofBytes(1500).toString(MemSizeUnit.KB, 0));
    assertEquals("1.5 KB", MemSize.ofBytes(1500).toString(MemSizeUnit.KB, 2));
    assertEquals("1.01 KB", MemSize.ofBytes(1005).toString(MemSizeUnit.KB, 2));
    assertEquals("1 KiB", MemSize.ofBytes(1500).toString(MemSizeUnit.KiB, 0));
    assertEquals("1.46484375 KiB", MemSize.ofBytes(1500).toString(MemSizeUnit.KiB, 8));
  }

  @Test
  public void toStringWithMoreThanTenFractionDigits() {
    MemSize size = MemSize.ofBytes(1_000_000_000_000L); // 1e-12 YB
    assertEquals("0 YB", size.toString(MemSizeUnit.YB));
    assertEquals("0.000000000001 YB", size.toString(MemSizeUnit.YB, 15));
  }

  @Test
  public void toUnitWithScale() {
    assertEquals(new BigDecimal("1.46"), MemSize.ofBytes(1500).toUnit(MemSizeUnit.KiB, 2));
    assertEquals(new BigDecimal("1"), MemSize.ofBytes(1500).toUnit(MemSizeUnit.KiB, 0));
    assertEquals(new BigDecimal("1.4648437500"), MemSize.ofBytes(1500).toUnit(MemSizeUnit.KiB));
  }

  @Test
  public void negativeFractionDigitsThrow() {
    MemSize size = MemSize.ofBytes(1500);
    assertThrows(IllegalArgumentException.class, () -> size.humanReadable(-1));
    assertThrows(IllegalArgumentException.class,
        () -> size.humanReadable(MemSizeUnitType.Binary, -1));
    assertThrows(IllegalArgumentException.class, () -> size.toString(MemSizeUnit.KB, -1));
    assertThrows(IllegalArgumentException.class, () -> size.toUnit(MemSizeUnit.KB, -1));
    assertThrows(IllegalArgumentException.class, () -> MemSize.ofBytes(0).humanReadable(-1));
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
