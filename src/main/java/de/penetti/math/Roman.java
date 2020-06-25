package de.penetti.math;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <h3>Wie sind die Regeln beim Schreiben Römischer Zahlen?</h3>
 * <h4>Einfache Umrechnung</h4>
 * <p>
 * Die Zahlen werden von links nach rechts gelesen und addiert, wobei der Wert der Zahlzeichen von links nach rechts abnimmt.
 * Wenn arabische in Römische Zahlen umgerechnet werden, gilt es zu beachten, dass maximal vier gleiche Symbole hintereinander
 * stehen können. Dementsprechend wird die 5 nicht durch fünf Striche dargestellt, sondern durch V.
 * <br><small>Quelle: https://www.roemische-zahlen.net/</small>
 *
 *
 * <h4>Subtraktionsregel</h4>
 * <p>
 * Neben der einfachen Umrechnung der Römischen Zahlen existiert auch eine verkürzte, heute verwendete Schreibweise,
 * die sogenannte Subtraktionsregel.
 * Der Grundgedanke der Subtraktionsregel ist, dass vier gleiche Zahlzeichen vermieden werden, indem eine kleinere Zahl vor
 * eine größere geschrieben wird. Ein einfaches Beispiel dafür ist die 4. Sie wird durch IV dargestellt, was wörtlich mit fünf weniger
 * eins übersetzt werden könnte. Für die subtrahierende Abbildung im Römischen Zahlensystem gelten folgende Regeln:
 * <ol>
 * <li>I steht nur vor V und X</li>
 * <li>X steht nur vor L und C</li>
 * <li>C steht nur vor D und M</li>
 * <small>Quelle: https://www.roemische-zahlen.net/</small>
 * </ol>
 * </p>
 * <ul>
 * <li>Römische Zahlen dürfen nur die Ziffern I, V, X, L, C, D und M enthalten. Ein Zeichen in einem Zahlwort des Römischen Zahlensystems
 * hat immer den gleichen Wert unabhängig von seiner Stellung im Zahlwort.</li>
 * <li>In einem römischen Zahlwort sind die einzelnen Zeichen von links nach rechts mit absteigender Wertigkeit notiert.</li>
 * <li>Der Wert des Zahlwortes bestimmt sich aus der Summe der Wertigkeiten der einzelnen Ziffern.</li>
 * <li>Symbole einer kleineren Zahl, die vor dem einer größeren stehen, werden von diesem subtrahiert.</li>
 * <li>Die Zeichen I, X, C und M werden höchstens dreimal hintereinander geschrieben.</li>
 * <li>Die Zeichen V, L und D werden bei einer Zahl höchstens einmal verwendet. (Auf Uhren wird meistens die 4 durch IIII dargestellt.)</li>
 * <li>Die Grundzahlen I, X, C dürfen nur von der nächsthöheren Zwischen- oder Grundzahl subtrahiert werden:
 * I können nur von V und X abgezogen werden; X können nur von L und C abgezogen werden; C können nur von D und M abgezogen werden.</li>
 * <small>Quelle: https://www.periodni.com/de/romische_zahlen_umwandeln.html</small>
 * </ul>
 *
 * <p>
 * Es gibt zwei Abweichungen von dieser Normalform, die schon seit der Antike vereinzelt zu belegen sind und
 * auch in jüngerer Zeit auftreten:
 * <ol>
 * <li>Das Zeichen in subtraktiver Stellung wird verdoppelt und dann der Wert zweimal abgezogen,
 * z. B. IIX statt VIII für 8, XXC statt LXXX für 80</li>
 * <li>I oder X werden in subtraktiver Stellung nicht nur vor den beiden jeweils nächstgrößeren Zeichen, sondern vor noch
 * höheren Zeichen verwendet, z. B. IL statt XLIX für 49, IC statt XCIX für 99 oder XM statt CMXC für 990</li>
 * </ol>
 * Beide Abweichungen treten mitunter kombiniert auf, also IIL statt XLVIII für 48, IIC statt XCVIII für 98.<br>
 * <small>https://de.wikipedia.org/wiki/R%C3%B6mische_Zahlschrift</small>
 * </p>
 * <p></p>
 * <p>
 * Aufgrund der Beschränkungen auf 3 bzw. 4 gleiche Zeichen ist der Zahlenbereich sehr eingeschränkt.<br>
 * Es gibt kein Zeichen für die 0. Falls der Wert 0 ist, wird ein leerer String erzeugt. Der maximale Wert beträgt 3999.
 * </p>
 *
 *
 * <h4>Maximaler Wert</h4>
 * Der maximale Wert ergibt sich aus den o.g. Bedingungen.<br>
 * <b>Einfache Schreibweise</b><br>
 * MMMDCCCCLXXXXVIIII => 3999<br>
 *
 * <b>Subtraktive Schreibweise</b><br>
 * MMMCMXCIX => 3999<br>
 * MMMIM => 3999
 */
public final class Roman {
  public enum Sign {
    M(1000, "M"),
    IM(999, "IM"),
    IIM(998, "IIM"),
    XM(990, "XM"),
    XXM(980, "XXM"),
    CM(900, "CM"),
    CCM(800, "CCM"),
    D(500, "D"),
    ID(499, "ID"),
    IID(498, "IID"),
    XD(490, "XD"),
    XXD(480, "XXD"),
    CD(400, "CD"),
    CCD(300, "CCD"),
    C(100, "C"),
    IC(99, "IC"),
    IIC(98, "IIC"),
    XC(90, "XC"),
    XXC(80, "XXC"),
    L(50, "L"),
    IL(49, "IL"),
    IIL(48, "IIL"),
    XL(40, "XL"),
    XXL(30, "XXL"),
    X(10, "X"),
    IX(9, "IX"),
    IIX(8, "IIX"),
    V(5, "V"),
    IV(4, "IV"),
    IIV(3, "IIV"),
    I(1, "I");

    public static Sign getSign(String sign) {
      for (Sign s : values()) {
        if (s.roman.equals(sign)) {
          return s;
        }
      }
      return null;
    }

    private final int arab;
    private final String roman;

    Sign(int arab, String roman) {
      this.arab = arab;
      this.roman = roman;
    }

    int getValue() {
      return arab;
    }

    String getSign() {
      return roman;
    }
  }

  public enum Strategy {
    SIMPLE(List.of(Sign.M, Sign.D, Sign.C, Sign.L, Sign.X, Sign.V, Sign.I)),
    SUBTRACTION(List.of(Sign.M, Sign.CM,
                        Sign.D, Sign.CD,
                        Sign.C, Sign.XC,
                        Sign.L, Sign.XL,
                        Sign.X, Sign.IX,
                        Sign.V, Sign.IV,
                        Sign.I)),
    MODERN_1(List.of(Sign.M, Sign.CM, Sign.CCM,
                     Sign.D, Sign.CD, Sign.CCD,
                     Sign.C, Sign.XC, Sign.XXC,
                     Sign.L, Sign.XL, Sign.XXL,
                     Sign.X, Sign.IX, Sign.IIX,
                     Sign.V, Sign.IV, Sign.IIV,
                     Sign.I)),
    MODERN_2(List.of(Sign.M, Sign.IM, Sign.XM, Sign.CM,
                     Sign.D, Sign.ID, Sign.XD, Sign.CD,
                     Sign.C, Sign.IC, Sign.XC,
                     Sign.L, Sign.IL, Sign.XL,
                     Sign.X, Sign.IX,
                     Sign.V, Sign.IV,
                     Sign.I)),
    MODERN_12(List.of(Sign.M, Sign.IM, Sign.IIM, Sign.XM, Sign.XXM, Sign.CM, Sign.CCM,
                      Sign.D, Sign.ID, Sign.IID, Sign.XD, Sign.XXD, Sign.CD, Sign.CCD,
                      Sign.C, Sign.IC, Sign.IIC, Sign.XC, Sign.XXC,
                      Sign.L, Sign.IL, Sign.IIL, Sign.XL, Sign.XXL,
                      Sign.X, Sign.IX, Sign.IIX,
                      Sign.V, Sign.IV, Sign.IIV,
                      Sign.I));
    private final List<Sign> signs;

    Strategy(List<Sign> signs) {
      this.signs = signs;
    }
  }

  private static final int MIN_VALUE = 0;
  private static final int MAX_VALUE = 3999;
  private static final int MAX_LENGTH_BASE = 3;


  private final int value;

  public Roman(int arabValue) {
    checkValueInRange(arabValue);
    this.value = arabValue;
  }

  public Roman(String romanValue) {
    int arab = toArab(romanValue);
    checkValueInRange(arab);
    this.value = arab;
  }

  public String getValue() {
    return getValue(Strategy.SUBTRACTION);
  }

  public String getValue(Strategy strategy) {
    return toRoman(getArabValue(), strategy);
  }

  public int getArabValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Roman roman = (Roman) o;
    return value == roman.value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    List<String> possibilities = Arrays.stream(Strategy.values())
                                       .map(s -> new Roman(value).getValue(s))
                                       .distinct()
                                       .collect(Collectors.toList());
    return String.format("Roman{%d => %s}", value, possibilities);
  }

  private int toArab(String roman) {
    int arab = 0;
    int startIndex = 0;
    while (startIndex < roman.length()) {
      int endIndex = startIndex + MAX_LENGTH_BASE;
      if (endIndex > roman.length()) {
        endIndex = roman.length();
      }
      String s = roman.substring(startIndex, endIndex);
      int[] recursiveDepth = {0};
      arab += toArab(s, recursiveDepth, roman, startIndex);
      startIndex += s.length() - recursiveDepth[0];
    }
    return arab;
  }

  private int toArab(String romanSign, int[] recursiveDepth, String roman, int index) {
    Roman.Sign s = Roman.Sign.getSign(romanSign);
    if (s == null) {
      if (romanSign.length() <= 1) {
        throw new NumberFormatException(String.format("Invalid roman at index %d: %s.", index, roman));
      }
      recursiveDepth[0]++;
      return toArab(romanSign.substring(0, romanSign.length() - 1), recursiveDepth, roman, index);
    }
    return s.getValue();
  }

  private String toRoman(int arabValue, Strategy strategy) {
    checkValueInRange(arabValue);
    StringBuilder romanValue = new StringBuilder();
    int[] aValue = {arabValue};
    strategy.signs.forEach(v -> aValue[0] = decrValue(aValue[0], v, romanValue));
    return romanValue.toString();
  }

  private static void checkValueInRange(int arabValue) {
    if (arabValue < MIN_VALUE || arabValue > MAX_VALUE) {
      throw new ArithmeticException(String.format("The arabic number %d is not in the range of roman numbers "
                                                + "which is from %d to %d.", arabValue, MIN_VALUE, MAX_VALUE));
    }
  }

  private static int decrValue(int arabValue, Sign sign, StringBuilder romanAsRef) {
    while (arabValue >= sign.arab) {
      romanAsRef.append(sign.roman);
      arabValue -= sign.arab;
    }
    return arabValue;
  }

  public static void main(String[] args) {
    for (int i = 0; i <= MAX_VALUE; i++) {
      Roman r = new Roman(i);
      System.out.println(r.toString());
    }
  }
}