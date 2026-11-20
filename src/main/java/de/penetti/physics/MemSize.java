package de.penetti.physics;

import lombok.Getter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Unveränderliche Darstellung einer Speichergröße in Bytes.
 * Unterstützt Werte bis zu 10^40 (weit über Yottabyte hinaus) dank BigInteger.
 */
@Getter
public final class MemSize implements Comparable<MemSize> {

  /** Höchste Anzahl Nachkommastellen in {@link #humanReadable()}. */
  public static final int DEFAULT_HUMAN_READABLE_DIGITS = 2;

  /** Höchste Anzahl Nachkommastellen in {@link #toUnit(MemSizeUnit)}. */
  public static final int DEFAULT_TO_UNIT_DIGITS = 10;

  private final BigInteger bytes;

  private MemSize(BigInteger bytes) {
    this.bytes = bytes;
  }

  /**
   * Factory-Methode für eine Speichergröße in Bytes.
   *
   * @param bytes Die Anzahl der Bytes. Darf nicht null sein und muss >= 0 sein.
   * @throws NullPointerException     Wenn bytes null ist.
   * @throws IllegalArgumentException Wenn bytes negativ ist.
   */
  public static MemSize ofBytes(BigInteger bytes) {
    Objects.requireNonNull(bytes, "Bytes darf nicht null sein");
    if (bytes.signum() < 0) {
      throw new IllegalArgumentException("Speichergröße darf nicht negativ sein");
    }
    return new MemSize(bytes);
  }

  /**
   * Factory-Methode für eine Speichergröße in Bytes (long).
   *
   * @param bytes Die Anzahl der Bytes. Muss >= 0 sein.
   * @throws IllegalArgumentException Wenn bytes negativ ist.
   */
  public static MemSize ofBytes(long bytes) {
    return ofBytes(BigInteger.valueOf(bytes));
  }

  /**
   * Factory-Methode für eine Speichergröße basierend auf einem Wert und einer Einheit.
   * Bruchteile von Bytes werden abgeschnitten.
   *
   * @param value Die numerische Größe (z.B. 1.5). Darf nicht null und nicht negativ sein.
   * @param unit  Die Einheit (z.B. KiB).
   * @return Die resultierende MemSize in Bytes.
   * @throws NullPointerException     Wenn value oder unit null ist.
   * @throws IllegalArgumentException Wenn value negativ ist.
   */
  public static MemSize of(BigDecimal value, MemSizeUnit unit) {
    Objects.requireNonNull(value, "Value darf nicht null sein");
    Objects.requireNonNull(unit, "Unit darf nicht null sein");
    if (value.signum() < 0) {
      throw new IllegalArgumentException("Wert darf nicht negativ sein");
    }
    return new MemSize(value.multiply(new BigDecimal(unit.getFactor())).toBigInteger());
  }

  /**
   * Factory-Methode für eine Speichergröße basierend auf einem double-Wert und einer Einheit.
   * Der Wert wird über seine Dezimaldarstellung umgerechnet, um Fließkomma-Artefakte zu vermeiden
   * (0.1 KB sind genau 100 Bytes).
   *
   * @throws IllegalArgumentException Wenn value negativ, NaN oder unendlich ist.
   */
  public static MemSize of(double value, MemSizeUnit unit) {
    if (Double.isNaN(value) || Double.isInfinite(value) || value < 0) {
      throw new IllegalArgumentException("Wert muss eine endliche Zahl >= 0 sein: " + value);
    }
    return of(new BigDecimal(Double.toString(value)), unit);
  }

  /**
   * Factory-Methode für eine Speichergröße basierend auf einem long-Wert und einer Einheit.
   *
   * @param value Die numerische Größe (z.B. 1024). Darf nicht negativ sein.
   * @param unit  Die Einheit (z.B. KiB).
   * @return Die resultierende MemSize in Bytes.
   * @throws IllegalArgumentException Wenn value negativ ist.
   */
  public static MemSize of(long value, MemSizeUnit unit) {
    return of(BigDecimal.valueOf(value), unit);
  }

  /**
   * Parst einen String (z.B. "1024 MB", "1.5 GiB") in eine MemSize, unabhängig von der
   * Default-Locale: Punkt ist das Dezimal-, Komma das Tausendertrennzeichen ("1,234.5 KB").
   * Geeignet für Konfigurationen, Argumente und Protokolle. Für Benutzereingaben in der
   * Schreibweise der aktuellen Locale siehe {@link #parseLocalized(String)}.
   * Nutzt das Pattern aus MemSizeUnit.
   *
   * @param sizeString Der String zur Speicherung.
   * @return Die MemSize-Instanz.
   * @throws IllegalArgumentException Wenn das Format ungültig ist.
   */
  public static MemSize parse(String sizeString) {
    return parse(sizeString, DecimalFormatSymbols.getInstance(Locale.ROOT));
  }

  /**
   * Wie {@link #parse(String)}, aber Dezimal- und Tausendertrennzeichen richten sich nach der
   * aktuell gesetzten Default-Locale (englisch "1.5 GiB", deutsch "1,5 GiB"). Das Gegenstück zu
   * {@link #format(MemSizeUnit, int)} und {@link #humanReadable()}, die dieselbe Locale nutzen.
   * Das Ergebnis hängt von der Locale des Rechners ab, deshalb nicht für Konfigurationen
   * verwenden.
   *
   * @param sizeString Der String zur Speicherung.
   * @return Die MemSize-Instanz.
   * @throws IllegalArgumentException Wenn das Format in der aktuellen Locale ungültig ist.
   */
  public static MemSize parseLocalized(String sizeString) {
    return parse(sizeString, DecimalFormatSymbols.getInstance());
  }

  private static MemSize parse(String sizeString, DecimalFormatSymbols symbols) {
    if (sizeString == null || sizeString.isBlank()) {
      return MemSize.ofBytes(0);
    }

    Matcher matcher = MemSizeUnit.SIZE_PATTERN.matcher(sizeString);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Ungültiges Format für Speichergröße: " + sizeString +
          ". Erwartet: [Zahl] [Einheit], z.B. '100 MB' oder '1 GiB'");
    }

    String numberStr = matcher.group(1);
    String unitStr = matcher.group(2); // Kann null sein

    // Wenn keine Einheit angegeben ist, wird standardmäßig Byte angenommen
    MemSizeUnit unit = MemSizeUnit.of(unitStr);

    // Wir verwenden BigDecimal für das Parsen der Zahl, um Nachkommastellen zu unterstützen,
    // runden dann aber auf Bytes (BigInteger) auf.
    BigDecimal number = parseNumber(numberStr, symbols);

    if (number.signum() < 0) {
      throw new IllegalArgumentException("Speichergröße darf nicht negativ sein");
    }

    // Berechnung: Zahl * Faktor der Einheit
    // Wir verwenden BigDecimal für die Multiplikation, um Präzision zu wahren,
    // bevor wir auf BigInteger runden.
    BigInteger totalBytes = number.multiply(new BigDecimal(unit.getFactor())).toBigInteger();

    return new MemSize(totalBytes);
  }

  /**
   * Liest eine Zahl mit den Trennzeichen aus {@code symbols}. Das Tausendertrennzeichen
   * ist nur in Dreiergruppen gültig ("1.000" im Deutschen, "1,000" im Englischen), damit eine
   * Eingabe in der falschen Schreibweise (z.B. "1,5" bei englischer Locale) mit einer
   * Fehlermeldung abgelehnt wird, statt still einen falschen Wert zu ergeben.
   */
  private static BigDecimal parseNumber(String text, DecimalFormatSymbols symbols) {
    String decimal = String.valueOf(symbols.getDecimalSeparator());
    String grouping = String.valueOf(symbols.getGroupingSeparator());

    String sign = "";
    String body = text;
    if (body.startsWith("+") || body.startsWith("-")) {
      sign = body.substring(0, 1);
      body = body.substring(1);
    }

    int decimalAt = body.indexOf(decimal);
    String integerPart = decimalAt < 0 ? body : body.substring(0, decimalAt);
    String fractionPart = decimalAt < 0 ? "" : body.substring(decimalAt + 1);

    boolean groupedOk = integerPart.matches(
        "\\d{1,3}(?:" + Pattern.quote(grouping) + "\\d{3})+");
    if (groupedOk) {
      integerPart = integerPart.replace(grouping, "");
    }
    if (!integerPart.matches("\\d*") || !fractionPart.matches("\\d*")
        || (integerPart.isEmpty() && fractionPart.isEmpty())) {
      throw new IllegalArgumentException("Ungültige Zahl in Speichergröße: " + text
          + " (erwartet: Dezimaltrennzeichen '" + decimal + "', Tausendertrennzeichen '"
          + grouping + "')");
    }

    return new BigDecimal(sign + (integerPart.isEmpty() ? "0" : integerPart)
        + (fractionPart.isEmpty() ? "" : "." + fractionPart));
  }

  /**
   * Gibt die Größe in der angegebenen Einheit zurück (als BigDecimal).
   * Beispiel: 1024 Bytes in KiB -> 1.0
   *
   * @param targetUnit Die Ziel einheit.
   * @return Die Größe als BigDecimal.
   */
  public BigDecimal toUnit(MemSizeUnit targetUnit) {
    return toUnit(targetUnit, DEFAULT_TO_UNIT_DIGITS);
  }

  /**
   * Wie {@link #toUnit(MemSizeUnit)}, aber auf die angegebene Anzahl Nachkommastellen gerundet
   * (HALF_UP).
   *
   * @param targetUnit Die Zieleinheit.
   * @param scale      Die Anzahl der Nachkommastellen, mindestens 0.
   * @return Die Größe als BigDecimal.
   * @throws IllegalArgumentException Wenn scale negativ ist.
   */
  public BigDecimal toUnit(MemSizeUnit targetUnit, int scale) {
    requireNonNegativeDigits(scale);
    // Division: Bytes / Faktor
    // Wir verwenden BigDecimal für die Division, um Nachkommastellen zu erhalten
    BigDecimal factor = new BigDecimal(targetUnit.getFactor());
    return new BigDecimal(this.bytes).divide(factor, scale, RoundingMode.HALF_UP);
  }

  private static void requireNonNegativeDigits(int digits) {
    if (digits < 0) {
      throw new IllegalArgumentException("Anzahl der Nachkommastellen darf nicht negativ sein: " + digits);
    }
  }

  private static String formatNumber(BigDecimal value, int maxFractionDigits) {
    NumberFormat numberFormat = NumberFormat.getNumberInstance();
    numberFormat.setMaximumFractionDigits(maxFractionDigits);
    numberFormat.setRoundingMode(RoundingMode.HALF_UP);
    return numberFormat.format(value);
  }

  /**
   * Vergleicht diese MemSize mit einer anderen Instanz.
   *
   * @param other Die andere MemSize-Instanz.
   * @return einen negativen Wert, wenn diese Instanz kleiner ist,
   *         0, wenn sie gleich ist, und einen positiven Wert, wenn sie größer ist.
   * @throws NullPointerException Wenn other null ist (gemäß Vertrag von {@link Comparable}).
   */
  @Override
  public int compareTo(MemSize other) {
    Objects.requireNonNull(other, "other darf nicht null sein");
    return this.bytes.compareTo(other.bytes);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MemSize memSize = (MemSize) o;
    return bytes.equals(memSize.bytes);
  }

  @Override
  public int hashCode() {
    return bytes.hashCode();
  }

  @Override
  public String toString() {
    return humanReadable();
  }

  /**
   * Gibt die Speichergröße als String in der angegebenen, festen Einheit zurück (im Gegensatz zu
   * {@link #humanReadable()}, das die Einheit selbst wählt). Die Zahl wird auf höchstens
   * {@code maxFractionDigits} Nachkommastellen gerundet (HALF_UP), unnötige Nullen entfallen.
   * Das Zahlenformat folgt der aktuell gesetzten {@link Locale#getDefault() Default-Locale}.
   *
   * @param targetUnit        Die Ziel-Einheit (z.B. MemSizeUnit.GiB).
   * @param maxFractionDigits Die höchste Anzahl Nachkommastellen, mindestens 0.
   * @return Ein formatierter String (z.B. "1,5 GiB" oder "1.024 B").
   * @throws IllegalArgumentException Wenn maxFractionDigits negativ ist.
   */
  public String format(MemSizeUnit targetUnit, int maxFractionDigits) {
    return formatNumber(toUnit(targetUnit, maxFractionDigits), maxFractionDigits)
        + " " + targetUnit.getSymbol();
  }

  /**
   * Gibt die Speichergröße in einer menschenfreundlichen, optimalen Einheit zurück.
   *
   * @param type Der zu verwendende Einheitentyp (Binary für KiB/MiB... oder Decimal für KB/MB...).
   *             Ignoriert MemSizeUnitType.Byte, da Byte die Basis ist und immer verwendet wird,
   *             wenn der Wert kleiner als die kleinste Einheit des Typs ist.
   * @return Der formatierte String. Das Zahlenformat folgt der aktuell gesetzten
   *         {@link Locale#getDefault() Default-Locale} (z.B. "1,5 KiB" für Deutsch, "1.5 KiB" für Englisch).
   */
  public String humanReadable(MemSizeUnitType type) {
    return humanReadable(type, DEFAULT_HUMAN_READABLE_DIGITS);
  }

  /**
   * Wie {@link #humanReadable(MemSizeUnitType)}, aber mit höchstens {@code maxFractionDigits}
   * Nachkommastellen (gerundet HALF_UP, unnötige Nullen entfallen). Bei Werten unter der
   * kleinsten Einheit (ganze Bytes) gibt es keine Nachkommastellen.
   *
   * @param type              Der zu verwendende Einheitentyp.
   * @param maxFractionDigits Die höchste Anzahl Nachkommastellen, mindestens 0.
   * @throws IllegalArgumentException Wenn maxFractionDigits negativ ist.
   */
  public String humanReadable(MemSizeUnitType type, int maxFractionDigits) {
    MemSizeUnit unit = bestUnit(type, maxFractionDigits);
    if (unit == MemSizeUnit.Byte) {
      // Ganze Bytes, ohne Nachkommastellen und ohne Tausendertrennzeichen
      return this.bytes + " B";
    }
    return format(unit, maxFractionDigits);
  }

  /**
   * Wählt die Einheit für die lesbare Ausgabe: die größte Einheit des Typs, in der der Wert
   * mindestens 1 ist. Liegt der Wert unter der kleinsten Einheit des Typs (oder ist der Typ
   * {@link MemSizeUnitType#Byte}), ist es {@link MemSizeUnit#Byte}. Rundet der Wert in der
   * gewählten Einheit auf die Basis des Typs (z.B. 999,999 KB bei 2 Stellen auf "1000 KB"),
   * wird die nächstgrößere Einheit genommen ("1 MB"), sofern es eine gibt.
   *
   * @param type              Der Einheitentyp (Binary oder Decimal).
   * @param maxFractionDigits Die Anzahl Nachkommastellen, auf die später gerundet wird.
   * @throws IllegalArgumentException Wenn maxFractionDigits negativ ist.
   */
  MemSizeUnit bestUnit(MemSizeUnitType type, int maxFractionDigits) {
    requireNonNegativeDigits(maxFractionDigits);

    List<MemSizeUnit> units = MemSizeUnit.of(type);
    // Typ Byte liefert nur [Byte]; Bytes sind die Basis und werden nicht umgerechnet.
    // Das gilt auch für Werte unter der kleinsten Einheit (und damit für 0 Bytes).
    if (units.isEmpty() || type == MemSizeUnitType.Byte
        || this.bytes.compareTo(units.getFirst().getFactor()) < 0) {
      return MemSizeUnit.Byte;
    }

    // Einheiten von klein nach groß: die größte, in der der Wert >= 1 ist.
    // Sobald der Wert < 1 ist, sind alle größeren Einheiten noch kleiner.
    int bestIndex = 0;
    for (int i = 0; i < units.size() && toUnit(units.get(i)).compareTo(BigDecimal.ONE) >= 0; i++) {
      bestIndex = i;
    }

    // Rundet der Wert auf die nächste Einheit auf, stattdessen diese nehmen.
    // In der größten Einheit gibt es keine nächste.
    BigDecimal rounded = toUnit(units.get(bestIndex), maxFractionDigits);
    if (bestIndex < units.size() - 1 && rounded.compareTo(BigDecimal.valueOf(type.getBase())) >= 0) {
      bestIndex++;
    }
    return units.get(bestIndex);
  }

  /**
   * Wie {@link #humanReadable()}, aber mit höchstens {@code maxFractionDigits} Nachkommastellen.
   *
   * @throws IllegalArgumentException Wenn maxFractionDigits negativ ist.
   */
  public String humanReadable(int maxFractionDigits) {
    return humanReadable(MemSizeUnitType.Decimal, maxFractionDigits);
  }

  /**
   * Gibt die Speichergröße in einer menschenfreundlichen, optimalen Einheit zurück (Standard: Dezimal/SI).
   *
   * @return Der formatierte String.
   */
  public String humanReadable() {
    return humanReadable(MemSizeUnitType.Decimal);
  }

  /**
   * Addiert zwei MemSize Objekte.
   */
  public MemSize add(MemSize other) {
    return new MemSize(this.bytes.add(other.getBytes()));
  }

  /**
   * Subtrahiert eine andere MemSize.
   */
  public MemSize subtract(MemSize other) {
    BigInteger result = this.bytes.subtract(other.getBytes());
    if (result.signum() < 0) {
      throw new IllegalArgumentException("Ergebnis der Subtraktion darf nicht negativ sein");
    }
    return new MemSize(result);
  }
}