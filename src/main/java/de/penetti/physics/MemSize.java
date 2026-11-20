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

      private final BigInteger bytes;

  /**
   * Erzeugt eine neue MemSize aus der gegebenen Anzahl an Bytes.
   *
   * @param bytes Die Anzahl der Bytes. Darf nicht null sein und muss >= 0 sein.
   * @throws IllegalArgumentException Wenn bytes null ist oder negativ.
   */
  public MemSize(BigInteger bytes) {
    Objects.requireNonNull(bytes, "Bytes darf nicht null sein");
    if (bytes.signum() < 0) {
      throw new IllegalArgumentException("Speichergröße darf nicht negativ sein");
    }
    this.bytes = bytes;
  }

  /**
   * Erzeugt eine neue MemSize aus einem long-Wert und einer Einheit.
   *
   * @param value Die Größe in der angegebenen Einheit.
   * @param unit  Die Einheit.
   * @throws IllegalArgumentException Wenn value negativ ist.
   */
  public MemSize(long value, MemSizeUnit unit) {
    if (value < 0) {
      throw new IllegalArgumentException("Wert darf nicht negativ sein");
    }
    this.bytes = BigInteger.valueOf(value).multiply(unit.getFactor());
  }

  /**
   * Erzeugt eine neue MemSize aus einem double-Wert und einer Einheit.
   *
   * @param value Die Größe in der angegebenen Einheit.
   * @param unit  Die Einheit.
   * @throws IllegalArgumentException Wenn value negativ ist.
   */
  public MemSize(double value, MemSizeUnit unit) {
    if (value < 0) {
      throw new IllegalArgumentException("Wert darf nicht negativ sein");
    }
    // Verwende String-Konstruktor von BigDecimal zur Vermeidung von Floating-Point-Artefakten
    BigDecimal bdValue = new BigDecimal(Double.toString(value));
    this.bytes = bdValue.multiply(new BigDecimal(unit.getFactor())).toBigInteger();
  }

    /**
   * Erzeugt eine neue MemSize aus einem BigDecimal-Wert und einer Einheit.
   *
   * @param value Die Größe in der angegebenen Einheit.
   * @param unit  Die Einheit.
   * @throws IllegalArgumentException Wenn value negativ ist.
   */
  public MemSize(BigDecimal value, MemSizeUnit unit) {
    Objects.requireNonNull(value, "Value darf nicht null sein");
    if (value.signum() < 0) {
      throw new IllegalArgumentException("Wert darf nicht negativ sein");
    }
    this.bytes = value.multiply(new BigDecimal(unit.getFactor())).toBigInteger();
  }

  /**
   * Erzeugt eine neue MemSize aus der gegebenen Anzahl an Bytes (long).
   *
   * @param bytes Die Anzahl der Bytes. Muss >= 0 sein.
   * @throws IllegalArgumentException Wenn bytes negativ ist.
   */
  public MemSize(long bytes) {
    if (bytes < 0) {
      throw new IllegalArgumentException("Speichergröße darf nicht negativ sein");
    }
    this.bytes = BigInteger.valueOf(bytes);
  }

  public MemSize(String memSize) {
    MemSize ms =  MemSize.parse(memSize);
    this.bytes = ms.getBytes();
  }

  /**
   * Factory-Methode für eine Speichergröße in Bytes.
   */
  public static MemSize ofBytes(BigInteger bytes) {
    return new MemSize(bytes);
  }

  /**
   * Factory-Methode für eine Speichergröße in Bytes (long).
   */
  public static MemSize ofBytes(long bytes) {
    if (bytes < 0) {
      throw new IllegalArgumentException("Bytes darf nicht negativ sein");
    }
    return new MemSize(BigInteger.valueOf(bytes));
  }

  /**
   * Factory-Methode für eine Speichergröße basierend auf einem Wert und einer Einheit.
   *
   * @param value Die numerische Größe (z.B. 1024).
   * @param unit  Die Einheit (z.B. KiB).
   * @return Die resultierende MemSize in Bytes.
   */
  public static MemSize of(BigDecimal value, MemSizeUnit unit) {
    // Hinweis: Für maximale Präzision bei sehr großen Dezimalzahlen sollte man
    // prüfen, ob BigDecimal benötigt wird. Hier verwenden wir BigInteger Multiplikation
    // für ganzzahlige Inputs, oder wandeln BigDecimal in BigInteger um (mit Rundung).

    // Vereinfachung: Wir nehmen an, value ist eine Ganzzahl oder wir runden auf.
    // Für eine präzisere Implementierung mit Nachkommastellen müsste man
    // value.multiply(unit.getFactor()) berechnen.

    // Da MemSize intern BigInteger ist, müssen wir den BigDecimal-Wert in Bytes umrechnen.
    BigInteger byteValue = value.multiply(new BigDecimal(unit.getFactor())).toBigInteger();

    if (byteValue.signum() < 0) {
      throw new IllegalArgumentException("Ergebnis darf nicht negativ sein");
    }

    return new MemSize(byteValue);
  }

  /**
   * Factory-Methode für eine Speichergröße basierend auf einem double-Wert und einer Einheit.
   */
  public static MemSize of(double value, MemSizeUnit unit) {
    if (value < 0) {
      throw new IllegalArgumentException("Wert darf nicht negativ sein");
    }
    // Verwende String-Konstruktor von BigDecimal zur Vermeidung von Floating-Point-Artefakten
    BigDecimal bdValue = new BigDecimal(Double.toString(value));
    return of(bdValue, unit);
  }

  /**
   * Factory-Methode für eine Speichergröße basierend auf einem long-Wert und einer Einheit.
   * Achtung: Kann bei sehr großen Werten zu Überläufen in der Zwischenergebnisberechnung führen,
   * wenn man nicht vorsichtig ist, aber da unit.getFactor() BigInteger ist, ist das sicher.
   *
   * @param value Die numerische Größe (z.B. 1024).
   * @param unit  Die Einheit (z.B. KiB).
   * @return Die resultierende MemSize in Bytes.
   */
  public static MemSize of(long value, MemSizeUnit unit) {
    if (value < 0) {
      throw new IllegalArgumentException("Wert darf nicht negativ sein");
    }
    BigInteger byteValue = BigInteger.valueOf(value).multiply(unit.getFactor());
    return new MemSize(byteValue);
  }

  /**
   * Parst einen String (z.B. "1024 MB", "1.5 GiB") in eine MemSize. Dezimal- und
   * Tausendertrennzeichen richten sich nach der aktuell gesetzten Default-Locale
   * (englisch "1.5 GiB", deutsch "1,5 GiB").
   * Nutzt das Pattern aus MemSizeUnit.
   *
   * @param sizeString Der String zur Speicherung.
   * @return Die MemSize-Instanz.
   * @throws IllegalArgumentException Wenn das Format ungültig ist.
   */
  public static MemSize parse(String sizeString) {
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
    BigDecimal number = parseNumber(numberStr);

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
   * Liest eine Zahl gemäß der aktuell gesetzten Default-Locale: Dezimal- und
   * Tausendertrennzeichen stammen aus {@link DecimalFormatSymbols}. Das Tausendertrennzeichen
   * ist nur in Dreiergruppen gültig ("1.000" im Deutschen, "1,000" im Englischen), damit eine
   * Eingabe in der falschen Schreibweise (z.B. "1,5" bei englischer Locale) mit einer
   * Fehlermeldung abgelehnt wird, statt still einen falschen Wert zu ergeben.
   */
  private static BigDecimal parseNumber(String text) {
    DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
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
    // Division: Bytes / Faktor
    // Wir verwenden BigDecimal für die Division, um Nachkommastellen zu erhalten
    BigDecimal factor = new BigDecimal(targetUnit.getFactor());
    return new BigDecimal(this.bytes).divide(factor, 10, RoundingMode.HALF_UP);
  }

  /**
   * Vergleicht diese MemSize mit einer anderen Instanz auf Gleichheit.
   *
   * @param other Die andere MemSize-Instanz.
   * @return true, wenn beide Instanzen die gleiche Anzahl an Bytes haben.
   */
  public boolean isEqualTo(MemSize other) {
    if (other == null) {
      return false;
    }
    return this.bytes.equals(other.bytes);
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
   * Gibt die Speichergröße als lesbaren String in der angegebenen Einheit zurück.
   *
   * @param targetUnit Die Ziel-Einheit (z.B. MemSizeUnit.GiB).
   * @return Ein formatierter String mit bis zu 10 Nachkommastellen (z.B. "1,5 GiB" oder "1.024 B").
   *         Das Zahlenformat folgt der aktuell gesetzten {@link Locale#getDefault() Default-Locale}.
   */
  public String toString(MemSizeUnit targetUnit) {
    BigDecimal targetValue = toUnit(targetUnit);
    NumberFormat numberFormat = NumberFormat.getNumberInstance();
    numberFormat.setMaximumFractionDigits(10);
    numberFormat.setRoundingMode(RoundingMode.HALF_UP);
    return numberFormat.format(targetValue) + " " + targetUnit.getSymbol();
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
    // Fall 0: Wenn 0 Bytes, direkt zurückgeben
    if (this.bytes.equals(BigInteger.ZERO)) {
      return "0 B";
    }

    // Liste der Einheiten für den gewählten Typ holen
    List<MemSizeUnit> units = MemSizeUnit.of(type);

    // Prüfen, ob die Liste leer ist oder ob Byte-Typ gewählt wurde.
    // Da MemSizeUnit.of(MemSizeUnitType.Byte) nur [Byte] zurückgibt und
    // wir Bytes als Basis behandeln, gehen wir in diesem Fall auf den Fallback.
    if (units.isEmpty() || type == MemSizeUnitType.Byte) {
      return this.bytes + " B";
    }

    // Die kleinste Einheit in der Liste ist die erste (KiB oder KB)
    MemSizeUnit smallestUnitInList = units.getFirst();
    BigInteger smallestFactor = smallestUnitInList.getFactor();

    // Wenn der Wert kleiner ist als der Faktor der kleinsten Einheit in der Liste,
    // geben wir einfach Bytes aus.
    if (this.bytes.compareTo(smallestFactor) < 0) {
      return this.bytes + " B";
    }

    MemSizeUnit bestUnit = null;
    BigDecimal bestValue = null;

    // Wir durchsuchen die Einheiten von klein nach groß.
    // Wir suchen die größte Einheit, bei der der Wert >= 1 ist.
    for (MemSizeUnit unit : units) {
      BigDecimal value = toUnit(unit);

      if (value.compareTo(BigDecimal.ONE) >= 0) {
        bestUnit = unit;
        bestValue = value;
      } else {
        // Sobald der Wert < 1 ist, sind alle größeren Einheiten noch kleiner.
        break;
      }
    }

    // Fallback (sollte aufgrund der vorherigen Byte-Prüfung nicht nötig sein)
    if (bestValue == null) {
      return this.bytes + " B";
    }

    // Formatierung: Maximal 2 Dezimalstellen, unnötige Nullen entfallen, locale-abhängig
    NumberFormat numberFormat = NumberFormat.getNumberInstance();
    numberFormat.setMaximumFractionDigits(2);
    numberFormat.setRoundingMode(RoundingMode.HALF_UP);
    return numberFormat.format(bestValue) + " " + bestUnit.getSymbol();
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