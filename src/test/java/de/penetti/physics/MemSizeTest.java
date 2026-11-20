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
      assertEquals(size, MemSize.parseLocalized(size.format(MemSizeUnit.KB, 10)), locale.toString());
      assertEquals(size, MemSize.parseLocalized(size.format(MemSizeUnit.Byte, 10)), locale.toString());
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
  public void formatWithUnit() {
    assertEquals("1 KiB", MemSize.ofBytes(1024).format(MemSizeUnit.KiB, 10));
    assertEquals("1.5 KB", MemSize.ofBytes(1500).format(MemSizeUnit.KB, 10));
    assertEquals("1,000 B", MemSize.ofBytes(1000).format(MemSizeUnit.Byte, 10));
    assertEquals("0 KB", MemSize.ofBytes(0).format(MemSizeUnit.KB, 10));
    assertEquals("0.0000000001 YB", MemSize.ofBytes(100_000_000_000_000L).format(MemSizeUnit.YB, 10));
  }

  @Test
  public void formatUsesDefaultLocale() {
    MemSize size = MemSize.ofBytes(1500);
    Locale.setDefault(Locale.GERMANY);
    assertEquals("1,5 KB", size.format(MemSizeUnit.KB, 10));
    assertEquals("1.500 B", size.format(MemSizeUnit.Byte, 10));
    Locale.setDefault(Locale.US);
    assertEquals("1.5 KB", size.format(MemSizeUnit.KB, 10));
    assertEquals("1,500 B", size.format(MemSizeUnit.Byte, 10));
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
    assertEquals("976.563 KiB", MemSize.ofBytes(1_000_000).humanReadable(MemSizeUnitType.Binary));
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
    assertEquals("1,465 KiB", size.humanReadable(MemSizeUnitType.Binary));
    assertEquals("1.000 YB", MemSize.of(1000L, MemSizeUnit.YB).humanReadable());
    Locale.setDefault(Locale.US);
    assertEquals("1.5 KB", size.humanReadable());
    assertEquals("1.465 KiB", size.humanReadable(MemSizeUnitType.Binary));
  }

  @Test
  public void humanReadableRoundsHalfUp() {
    assertEquals("1.005 KB", MemSize.ofBytes(1005).humanReadable());
    assertEquals("1.004 KB", MemSize.ofBytes(1004).humanReadable());
    assertEquals("1.01 KB", MemSize.ofBytes(1005).humanReadable(2));
    assertEquals("1 KB", MemSize.ofBytes(1004).humanReadable(2));
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
    assertEquals(size.humanReadable(MemSize.DEFAULT_FRACTION_DIGITS), size.humanReadable());
    assertEquals(size.humanReadable(MemSizeUnitType.Binary, MemSize.DEFAULT_FRACTION_DIGITS),
        size.humanReadable(MemSizeUnitType.Binary));
    assertEquals("1.465 MB", size.humanReadable());
  }

  @Test
  public void humanReadableRoundsUpToNextUnit() {
    // Standard (3 Stellen): 999,999 KB bleibt in KB, erst 999,9995 MB rundet auf 1 GB
    assertEquals("999.999 KB", MemSize.ofBytes(999_999).humanReadable());
    assertEquals("999.999 MB", MemSize.ofBytes(999_999_499).humanReadable());
    assertEquals("1 GB", MemSize.ofBytes(999_999_500).humanReadable());
    assertEquals("1 GB", MemSize.ofBytes(999_999_999).humanReadable());
    // Mit 2 Stellen rundet bereits 999,995 KB auf 1 MB
    assertEquals("1 MB", MemSize.ofBytes(999_999).humanReadable(2));
    assertEquals("1 MB", MemSize.ofBytes(999_995).humanReadable(2));
    assertEquals("999.99 KB", MemSize.ofBytes(999_994).humanReadable(2));
    assertEquals("1 MB", MemSize.ofBytes(999_500).humanReadable(0));
    assertEquals("999 KB", MemSize.ofBytes(999_499).humanReadable(0));
    assertEquals("999.5 KB", MemSize.ofBytes(999_499).humanReadable(1));
  }

  @Test
  public void humanReadableBinaryRoundsUpToNextUnit() {
    // Standard (3 Stellen): 1023,99902 KiB bleibt in KiB
    assertEquals("1,023.999 KiB", MemSize.ofBytes(1_048_575).humanReadable(MemSizeUnitType.Binary));
    assertEquals("1,023.989 KiB", MemSize.ofBytes(1_048_565).humanReadable(MemSizeUnitType.Binary));
    // Mit 2 Stellen rundet 1023,99902 KiB auf 1024 und damit auf 1 MiB
    assertEquals("1 MiB", MemSize.ofBytes(1_048_575).humanReadable(MemSizeUnitType.Binary, 2));
  }

  @Test
  public void humanReadableLargestUnitCannotRoundUp() {
    MemSize size = MemSize.of(new BigDecimal("999.999"), MemSizeUnit.YB);
    assertEquals("1,000 YB", size.humanReadable(2));
  }

  @Test
  public void bestUnitIsByteBelowSmallestUnit() {
    assertEquals(MemSizeUnit.Byte, MemSize.ofBytes(0).bestUnit(MemSizeUnitType.Decimal, 2));
    assertEquals(MemSizeUnit.Byte, MemSize.ofBytes(999).bestUnit(MemSizeUnitType.Decimal, 2));
    assertEquals(MemSizeUnit.Byte, MemSize.ofBytes(1023).bestUnit(MemSizeUnitType.Binary, 2));
    assertEquals(MemSizeUnit.Byte, MemSize.ofBytes(5000).bestUnit(MemSizeUnitType.Byte, 2));
  }

  @Test
  public void bestUnitPicksLargestUnitAboveOne() {
    assertEquals(MemSizeUnit.KB, MemSize.ofBytes(1000).bestUnit(MemSizeUnitType.Decimal, 2));
    assertEquals(MemSizeUnit.KB, MemSize.ofBytes(1_500).bestUnit(MemSizeUnitType.Decimal, 2));
    assertEquals(MemSizeUnit.MB, MemSize.ofBytes(1_000_000).bestUnit(MemSizeUnitType.Decimal, 2));
    assertEquals(MemSizeUnit.KiB, MemSize.ofBytes(1024).bestUnit(MemSizeUnitType.Binary, 2));
    assertEquals(MemSizeUnit.MiB, MemSize.ofBytes(1_048_576).bestUnit(MemSizeUnitType.Binary, 2));
  }

  @Test
  public void bestUnitRoundsUpToNextUnit() {
    assertEquals(MemSizeUnit.MB, MemSize.ofBytes(999_999).bestUnit(MemSizeUnitType.Decimal, 2));
    assertEquals(MemSizeUnit.KB, MemSize.ofBytes(999_999).bestUnit(MemSizeUnitType.Decimal, 3));
    assertEquals(MemSizeUnit.KB, MemSize.ofBytes(999_994).bestUnit(MemSizeUnitType.Decimal, 2));
    assertEquals(MemSizeUnit.MiB, MemSize.ofBytes(1_048_575).bestUnit(MemSizeUnitType.Binary, 2));
  }

  @Test
  public void bestUnitStopsAtLargestUnit() {
    MemSize size = MemSize.of(new BigDecimal("999.999"), MemSizeUnit.YB);
    assertEquals(MemSizeUnit.YB, size.bestUnit(MemSizeUnitType.Decimal, 2));
    MemSize binary = MemSize.of(new BigDecimal("1023.999"), MemSizeUnit.YiB);
    assertEquals(MemSizeUnit.YiB, binary.bestUnit(MemSizeUnitType.Binary, 2));
  }

  @Test
  public void bestUnitRejectsNegativeFractionDigits() {
    assertThrows(IllegalArgumentException.class,
        () -> MemSize.ofBytes(0).bestUnit(MemSizeUnitType.Decimal, -1));
    assertThrows(IllegalArgumentException.class,
        () -> MemSize.ofBytes(1500).bestUnit(MemSizeUnitType.Decimal, -1));
  }

  @Test
  public void humanReadableBytesHaveNoFractionDigits() {
    assertEquals("999 B", MemSize.ofBytes(999).humanReadable(5));
    assertEquals("0 B", MemSize.ofBytes(0).humanReadable(5));
  }

  @Test
  public void formatWithFractionDigits() {
    assertEquals("2 KB", MemSize.ofBytes(1500).format(MemSizeUnit.KB, 0));
    assertEquals("1.5 KB", MemSize.ofBytes(1500).format(MemSizeUnit.KB, 2));
    assertEquals("1.01 KB", MemSize.ofBytes(1005).format(MemSizeUnit.KB, 2));
    assertEquals("1 KiB", MemSize.ofBytes(1500).format(MemSizeUnit.KiB, 0));
    assertEquals("1.46484375 KiB", MemSize.ofBytes(1500).format(MemSizeUnit.KiB, 8));
  }

  @Test
  public void formatWithMoreThanTenFractionDigits() {
    MemSize size = MemSize.ofBytes(1_000_000_000_000L); // 1e-12 YB
    assertEquals("0 YB", size.format(MemSizeUnit.YB, 10));
    assertEquals("0.000000000001 YB", size.format(MemSizeUnit.YB, 15));
  }

  @Test
  public void toUnitWithScale() {
    assertEquals(new BigDecimal("1.46"), MemSize.ofBytes(1500).toUnit(MemSizeUnit.KiB, 2));
    assertEquals(new BigDecimal("1"), MemSize.ofBytes(1500).toUnit(MemSizeUnit.KiB, 0));
    assertEquals(0, new BigDecimal("1.46484375").compareTo(MemSize.ofBytes(1500).toUnit(MemSizeUnit.KiB)));
  }

  @Test
  public void toUnitIsExact() {
    assertEquals("1.46484375", MemSize.ofBytes(1500).toUnit(MemSizeUnit.KiB).toPlainString());
    BigDecimal oneByteInYiB = MemSize.ofBytes(1).toUnit(MemSizeUnit.YiB);
    assertEquals(80, oneByteInYiB.scale());
    assertEquals(0, BigDecimal.ONE.compareTo(oneByteInYiB.multiply(new BigDecimal(MemSizeUnit.YiB.getFactor()))));
    assertEquals(0, new BigDecimal("1E-24").compareTo(MemSize.ofBytes(1).toUnit(MemSizeUnit.YB)));
  }

  @Test
  public void negativeFractionDigitsThrow() {
    MemSize size = MemSize.ofBytes(1500);
    assertThrows(IllegalArgumentException.class, () -> size.humanReadable(-1));
    assertThrows(IllegalArgumentException.class,
        () -> size.humanReadable(MemSizeUnitType.Binary, -1));
    assertThrows(IllegalArgumentException.class, () -> size.format(MemSizeUnit.KB, -1));
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
