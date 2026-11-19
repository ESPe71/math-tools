# Refaktoring – Einschätzung zu `de.penetti.*`

Stand: 2026-11-19. Grundlage: Durchsicht des Quellcodes und der Tests sowie der vorhandenen
Surefire-Reports in `target/` (die Tests wurden dabei nicht neu ausgeführt).

## Gesamteindruck

Solide Basis für ein Hobby-/Lernprojekt: klare Struktur, überwiegend immutable Klassen,
gute Fluent-Builder (`Line`, `Polyline`), viel Dokumentation, breite Testabdeckung
(`RomanTest`: 1351 Fälle). Schwächen: Randfälle, uneinheitliche Fließkomma-Toleranzen,
halbfertige Stellen und einzelne echte Bugs.

## Empfohlene Reihenfolge

1. [ ] `MathUtil.adjustAngle` gegen Endlosschleife absichern
2. [ ] `Vector.random` und `Line.normal()` korrigieren
3. [ ] `ByteUnitTest` locale-stabil machen
4. [ ] Epsilon-Behandlung in `Line` und `Vector` vereinheitlichen
5. [ ] `Polyline.centroid` korrigieren, `eleminateNonCorners`-Stub entfernen
6. [ ] `Roman`-Parsing validieren

## Bugs und Risiken

### Wahrscheinlich echte Fehler

| Klasse | Problem | Vorschlag |
|---|---|---|
| `MathUtil.adjustAngle` | Bei `NaN`/`±Infinity` endlose `while`-Schleife; bei sehr großen Werten extrem langsam. | Guard für nicht-endliche Werte, Modulo bzw. `Math.IEEEremainder` statt Schleife. |
| `Vector.random(min, max)` | `(max - min) + 1d` liefert Werte über `max` hinaus (Integer-Denkfehler). | `+ 1d` entfernen. |
| `Line.normal()` | Zufallsbasiert, nicht deterministisch, kann theoretisch endlos laufen. `SecureRandom.getInstanceStrong()` kann blockieren. | Direkt berechnen: `(-dy, dx)` normalisiert, Orientierung festlegen. |
| `Line.intersection` | Exception wird per `System.out.println` verschluckt und als `DEFICIENT` zurückgegeben. Segment-Test ohne Toleranz, Endpunktberührung kann durch Rundung verfehlt werden. | Fehler nicht verschlucken, Toleranz im Segment-Test. |
| `Line.classify` / `intersection` | Inkonsistent: `intersection` nutzt `EPSILON`, `classify` exakte `double`-Vergleiche. | Einheitliche Toleranzstrategie. |
| `Line.getSlope` | Vertikale Linie → `Infinity`, Punkt → `NaN`. Sonderfälle stecken kompliziert in `angleOfIntersection`. | Winkel über `atan2` der Richtungsvektoren. |
| `Vector.isLinearlyDependent` | Exakter `equals`-Vergleich nach `normalize()`, fragil bei Rundung. | Determinante mit Epsilon prüfen. |
| `Arc(start, end, bulge)` | `bulge == 0` → Division durch null. Vollkreis geht verloren, weil `adjustAngle(2π)` → `0`. `getBulge()` nicht implementiert. | Fall `bulge == 0` behandeln, Vollkreis gesondert modellieren, `getBulge()` implementieren oder entfernen. |
| `Polyline.centroid()` | Mittelwert der Ecken, kein echter Polygonschwerpunkt. Die korrekte `CentroidCenterOfGravity` existiert, wird aber nicht verwendet. | Auf `CentroidCenterOfGravity` umstellen (Fläche ≠ 0 beachten). |
| `Polyline.eleminateNonCorners` | `static`, gibt `null` zurück, Tippfehler im Namen. | Implementieren (als Instanzmethode) oder entfernen. |
| `Polyline.intersection(...)` | Liefert für jedes Segment ein Ergebnis, auch bei `PARALLEL`/ohne Schnitt. | Auf echte Schnitte filtern oder Semantik dokumentieren. |
| `Polyline.contains` / `area` | Ignorieren das Feld `closed` (steht in der Doku, bleibt eine Stolperfalle). | Verhalten vereinheitlichen oder Methodennamen klarer machen. |

### Kleinere Fehler und Unschönheiten

- **`NumberRange`**
  - Fehlermeldung verdreht ("min must be greater or equals max").
  - In `contains` überschattet ein generisches `T` das Klassen-`T`.
  - `equals` vergleicht typübergreifend über `Numbers.compare`, `hashCode` nutzt `Objects.hash` → equals/hashCode-Vertrag verletzt (z. B. `(1, 2)` gleich `(1.0, 2.0)`, aber anderer Hash).
- **`Numbers`**
  - Javadoc von `Infinity.NEGATIVE_INFINITY` / `POSITIVE_INFINITY` vertauscht.
  - Mischvergleich `Float`/`Double` über `new BigDecimal(double)` → `0.1f != 0.1d`; überraschend, sollte dokumentiert werden.
- **`Tuple.fromGenericString`**
  - `Class.forName` mit Benutzereingabe initialisiert die Klasse, danach reflektives `valueOf` → Sicherheitsrisiko bei unsicheren Eingaben. Whitelist (`Integer`, `Double`, …) verwenden.
  - Loggt Parsefehler auf `SEVERE`.
- **`Roman(String)`**
  - Greedy-Präfix-Parsing ohne Validierung; akzeptiert vermutlich Unsinn wie `IVIVIV` oder `VX`. Abhilfe: Round-Trip-Prüfung (`toRoman(toArab(s)).equals(s)` für mindestens eine Strategie).
  - Wertebereichsfehler als `ArithmeticException`; `IllegalArgumentException` wäre passender.
- **`Base64`**
  - `getBytes()` / `new String(...)` nutzen den Plattform-Charset → UTF-8 explizit angeben.
  - Name kollidiert mit `java.util.Base64` (überall voll qualifiziert).
- **`Line`**: Jama wird nur für ein 2×2-Gleichungssystem geladen; Cramersche Regel genügt und spart die Abhängigkeit.
- **`ByteUnit`**: Statisches `NumberFormat` ist nicht thread-safe.

## Tests

**Gut:** breite Abdeckung (`Roman`, `Numbers`, `Line`, `Vector`, `Polyline`), gezielte Randfälle (Null-Argumente, NaN, Infinity).

**Schwächen:**

- `ByteUnitTest` hat laut Report 2 Fehlschläge: erwartet `1,5 KiB`, geliefert `1.5 KiB`. Die Tests setzen eine deutsche Default-Locale voraus. Locale im Test fixieren oder Format locale-unabhängig machen.
- `LineTest.testConstructorWithNullPoints_1` ist identisch zu `testConstructorWithNullLine` (Copy-Paste).
- `Base64Test` hängt vom Arbeitsverzeichnis ab (`target/cat.txt`) → `@TempDir` verwenden.
- Fehlende Tests für: `adjustAngle(NaN/∞)`, `Arc` mit `bulge = 0` und Vollkreis, ungültige römische Strings, vertikale Linien bei `getSlope`.
- Tests, die von `Vector.random` / `Line.normal()` abhängen, sind nicht deterministisch.
- Dünne Abdeckung: `Arc` (5 Tests), `MathUtil` (2 Tests).
- Uneinheitlicher Stil (Klammerplatzierung, Sichtbarkeit) → Formatter/Checkstyle einführen.

## Build

- JUnit 5.6.0 und `maven-compiler-plugin` 3.8.1 sind veraltet.
- `maven-surefire-plugin` nicht explizit gepinnt → für reproduzierbare Builds Version festlegen.
- Java 11 ist ok; bei höherer Zielversion ließe sich einiges vereinfachen (`record`, `switch`-Ausdrücke).
