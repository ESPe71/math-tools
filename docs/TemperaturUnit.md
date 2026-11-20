# Einschätzung: `TemperatureUnit`

Datei: `src/main/java/de/penetti/physics/TemperatureUnit.java`
Zugehörig: `temperatureunit*.properties` (Basis/en, de, fr, es), `Temperature`

## Gesamteindruck

Das Enum ist kompakt und gut lesbar. Die acht Konstanten mit je zwei Lambdas in einer Zeile
wirken wie eine Tabelle und lassen sich leicht prüfen. Alle Umrechnungsformeln stimmen
(Celsius, Kelvin, Fahrenheit, Rankine, Réaumur, Rømer, Delisle, Newton). Die Einheitenzeichen
sind korrekt, die Resource-Bundles sind vollständig (kein fehlender Schlüssel).

## Was gut ist

- Enum als Träger der Umrechnung: keine Fallunterscheidung, neue Einheit = eine Zeile.
- Lambdas als Datenfeld statt `switch`.
- `getBySign` liefert `Optional` statt `null`.
- Bezeichnungen sind über Bundles lokalisiert (en, de, fr, es).

## Verbesserungsvorschläge

### 1. Resource-Schlüssel hing am Einheitenzeichen (erledigt)
Früher baute `getName()` den Schlüssel aus `getUnitSign()` ohne `°`. Im Bundle standen dadurch
Schlüssel wie `Ré` und `Rø`, und wer ein Zeichen änderte, brach still die Übersetzung.
Jetzt verwendet `getName()` `name()` als Schlüssel (`TemperatureUnit.Name.REAUMUR`). Die
Schlüssel in allen vier Bundles (en, de, fr, es) sind ASCII und an die Konstanten gebunden.
Ein fehlender Schlüssel fällt weiterhin erst zur Laufzeit als `MissingResourceException` auf;
der vorhandene Test in `TemperatureUnitTest` ruft `getName()` für alle Einheiten auf, aber nur
in der beim Start eingestellten Sprache (siehe Punkt 6).

### 2. `Function<Double, Double>` statt `DoubleUnaryOperator` (erledigt)
`Function<Double, Double>` boxt bei jeder Umrechnung. `DoubleUnaryOperator` ist der passende
Typ. Das Schlüsselwort `transient` an den Feldern ist bei Enums wirkungslos
(Serialisierung läuft über den Namen) und kann entfallen.

### 3. `getBySign` ist umständlich (erledigt)
- Vier Konstanten (`DEGREE_SIGN`, `SMALL_E`, `SMALL_ACUTE_E`, ...) für drei `replace`-Aufrufe.
- Bei jedem Aufruf werden alle Einheiten neu normalisiert.
- Besser: eine `Map`, einmal im `static`-Block aufgebaut. Dabei klären, ob Kleinschreibung
  (`k`, `c`) akzeptiert werden soll. Aktuell ist die Suche case-sensitiv; das steht nirgends
  dokumentiert.

### 4. Keine Differenzumrechnung (erledigt)
Die Formeln rechnen absolute Temperaturen um. Für Temperaturdifferenzen (Toleranzen, Deltas)
gilt nur der Skalenfaktor, nicht der Offset. Das hat in `Temperature.equals(Temperature,
Temperature)` zu einem Fehler geführt (Delta von 1 °C wurde zu 274,15 K). Er wurde zuerst in
`Temperature` behoben (Wert minus Nullpunkt) und ist jetzt sauber im Enum gelöst:

- Jede Einheit hat einen vierten Konstruktorparameter, die Skala in Kelvin pro Grad.
- Neue Methode `convertDifference(double, TemperatureUnit)` rechnet nur mit dem Skalenfaktor.
  1 °C sind danach 1 K und 1,8 °F; 1 K sind -1,5 °De (die Delisle-Skala läuft rückwärts).
- `Temperature` verwendet `Math.abs(unit.convertDifference(value, KELVIN))`.

**Herleitung der Faktoren.** Sie stammen nicht aus einer externen Quelle, sondern aus den
vorhandenen `toKelvin`-Lambdas: Eine Differenz ändert sich um die Steigung der Umrechnung, also
um den Faktor vor dem `d`; der konstante Offset spielt keine Rolle.

| Einheit    | `toKelvin`-Lambda                | Steigung (K pro Grad) |
|------------|----------------------------------|-----------------------|
| Celsius    | `d + 273.15`                     | 1                     |
| Kelvin     | `d`                              | 1                     |
| Fahrenheit | `(d + 459.67) * (5/9)`           | 5/9                   |
| Rankine    | `d * (5/9)`                      | 5/9                   |
| Réaumur    | `d * 1.25 + 273.15`              | 1,25                  |
| Rømer      | `(d - 7.5) * (40/21) + 273.15`   | 40/21                 |
| Delisle    | `373.15 - d * (2/3)`             | -2/3                  |
| Newton     | `d * (100/33) + 273.15`          | 100/33                |

Die Werte passen zu den bekannten Skalendefinitionen: das Intervall 0-100 °C entspricht
0-80 °Ré, 0-33 °N, 7,5-60 °Rø und 150-0 °De.

**Absicherung und Grenze.** `testConvertDifference_consistentWithGetValue` prüft für alle
64 Einheitenpaare, dass `convertDifference` der Steigung von `getValue` entspricht. Ein falsch
eingetragener Faktor fällt damit auf. Der Test zeigt aber nur, dass die Faktoren zu den
bestehenden Formeln passen, nicht, dass die Formeln physikalisch stimmen; das habe ich einmal
von Hand nachgerechnet, es steht aber nicht in den Tests. Bei Newton rechnet `fromKelvin` mit
`.33`, der Faktor ist 100/33, also genau der Kehrwert.

### 5. Umrechnung immer über Kelvin (erledigt)
Das ist als Zwischenschritt richtig und vermeidet n×n Formeln. Jede Umrechnung enthält dadurch
aber zwei Rundungsschritte. Die Tests brauchen deshalb Toleranzen. Praktisch unkritisch, aber
gut zu wissen.

**Umgesetzt.** Der Umweg über Kelvin bleibt, er ist richtig. Zwei Dinge wurden geändert:

- Bei gleicher Quell- und Zieleinheit gibt `getValue` den Wert unverändert zurück. Eine
  Stichprobe (8 Einheiten x 5 Werte) hatte gezeigt, dass 22 von 40 Werten nach der Hin- und
  Rückrechnung leicht abwichen, z. B. `0,1 °C -> 0,10000000000002274 °C`. Ein Test sichert das ab.
- Das Javadoc von `getValue` nennt den Umweg über Kelvin, die möglichen Abweichungen in den
  letzten Stellen und den Hinweis, mit Toleranz zu vergleichen.

Bei verschiedenen Einheiten bleiben die zwei Rundungsschritte als bewusst akzeptierte Grenze; sie lassen sich nur mit
direkten Formeln pro Paar vermeiden (n x n), was den Aufwand nicht rechtfertigt.

### 6. Sprache wird einmal festgelegt (erledigt)
`RESOURCE` ist ein `static final`-Feld; die Locale gilt, die beim ersten Laden der Klasse
eingestellt ist. `getName()` hat keinen `Locale`-Parameter. Die Bundles für fr und es sind
vorhanden, aber nicht durch Tests abgedeckt. Wenn mehrere Sprachen wirklich unterstützt werden
sollen: `getName(Locale)` ergänzen und je Sprache einen Test schreiben.

**Umgesetzt.**

- Das statische `RESOURCE`-Feld ist entfernt. `getName()` liest die Default-Locale bei jedem Aufruf
  (`getName(Locale.getDefault())`), wie `MemSize`; `toString()` folgt dem. Ein späteres
  `Locale.setDefault(...)` wirkt also.
- Neu: `getName(Locale)` für einen Namen in einer bestimmten Sprache (en, de, fr, es).
- Tests: Namen aller 8 Einheiten in en, de, fr und es sowie das Folgen der Default-Locale. Die
  Default-Locale wird in `@BeforeEach`/`@AfterEach` gesichert und zurückgesetzt.

**Fallback-Regel.** Es gilt das Java-Standardverhalten von `ResourceBundle`: Unbekannte Sprachen
(z. B. Italienisch) fallen zuerst auf die Default-Locale zurück, danach auf die Basisdatei
(Englisch). Damit `getName(Locale.ENGLISH)` auch bei deutscher Default-Locale Englisch liefert,
gibt es eine eigene `temperatureunit_en.properties` mit demselben Inhalt wie die Basisdatei. Ohne
sie würde Java für `en` die Default-Locale (z. B. `de`) vor der Basisdatei probieren und
"Grad Celsius" liefern. Das fiel zunächst nicht auf, weil die Tests die Default-Locale auf
Englisch setzten; erst ein Gegencheck hat es gezeigt. Zwei Tests sichern es ab
(`testGetName_explicitLocaleWinsOverDefaultLocale`,
`testGetName_unsupportedLanguageFallsBackToDefaultLocale`).

Gepflegt werden müssen nun zwei gleiche Dateien (`temperatureunit.properties` und
`temperatureunit_en.properties`). Ein erster Ansatz mit `ResourceBundle.Control` ohne
Default-Locale-Fallback wurde wieder verworfen: Er schaltete das übliche Java-Verhalten für alle
Sprachen ab, nur um diesen einen Fall zu lösen, und funktioniert nicht in benannten Modulen.

### 7. Dokumentation (erledigt)
Es fehlt Javadoc für die Klasse und die öffentlichen Methoden. Wichtig wären vor allem:
- Die Formeln rechnen absolute Temperaturen, keine Differenzen.
- Reihenfolge der Konstruktorparameter (`fromKelvin`, dann `toKelvin`) ist leicht zu verwechseln.
- Verhalten von `getBySign` (Groß-/Kleinschreibung, Weglassen von `°`).

**Umgesetzt.** `javadoc -Xdoclint:all` meldet für die öffentliche API von `TemperatureUnit` keine
Warnungen mehr.

- Klassen-Javadoc: Überblick, und die Unterscheidung zwischen absoluter Umrechnung
  (`getValue`, mit Nullpunkt) und Differenzumrechnung (`convertDifference`, nur Skala).
- Konstruktor: Reihenfolge der Parameter (`fromKelvin` vor `toKelvin`) ausdrücklich genannt, dazu
  die Bedeutung von `kelvinPerDegree`. Über der Konstantentabelle steht eine Spaltenüberschrift
  als Kommentar.
- Jede Konstante hat einen Satz mit den Fixpunkten der Skala (z. B. Delisle: Wasser siedet bei
  0 °De und gefriert bei 150 °De).
- `getUnitSign()` und die Instanzmethode `getValue(double, TemperatureUnit)` sind beschrieben, mit
  Verweisen auf die statische Variante und auf `convertDifference`.
- Schon vorher dokumentiert (Punkte 3 bis 6): `getBySign`, statisches `getValue`,
  `convertDifference`, `getName()` und `getName(Locale)`, `toString()`.

## Empfohlene Reihenfolge

1. Punkt 1 und 2 (klein, risikoarm). *Punkt 1 und 2 sind erledigt.*
2. Punkt 3 (Map statt Schleife) zusammen mit der Entscheidung zur Groß-/Kleinschreibung. *Punkt 3 ist erledigt (Map; Groß-/Kleinschreibung bleibt unterschieden und ist dokumentiert).*
3. Punkt 4, sobald `Temperature` weitere Differenz-Operationen bekommt. *Punkt 4 ist erledigt.*
4. Punkt 6 nur, wenn Mehrsprachigkeit im Betrieb tatsächlich gebraucht wird. *Punkt 6 ist erledigt.*
5. Punkt 7 laufend. *Punkt 7 ist erledigt.*

## Was ich lassen würde

Das Enum selbst, die Lambdas als Datenfelder, die `Optional`-Rückgabe von `getBySign` und die
Umrechnung über Kelvin als gemeinsame Basis.

## Einschätzung nach den Änderungen (Stand 20.11.2026)

Deutlich besser als zu Beginn. Die Formeln stimmten schon vorher; jetzt ist die Klasse auch sauber
gebaut, dokumentiert und getestet (alle sieben Punkte oben sind erledigt).

### Was jetzt gut ist

- **Zwei klar getrennte Umrechnungen.** `getValue` rechnet absolute Temperaturen,
  `convertDifference` rechnet Differenzen; das Javadoc sagt, wann man welche braucht. Der
  Delta-Fehler aus `Temperature` kann so nicht mehr entstehen.
- **Gleiche Einheit ohne Rundungsverlust**, und die Konsistenzprüfung der Skalen über alle 64
  Einheitenpaare fängt falsch eingetragene Faktoren ab.
- **Lokalisierung folgt dem Projekt.** `getName()` verwendet die Default-Locale beim Aufruf,
  `getName(Locale)` ist getestet (en, de, fr, es), die Schlüssel hängen an `name()`.
- **Vollständiges Javadoc.** Jede Konstante nennt die Fixpunkte ihrer Skala;
  `javadoc -Xdoclint:all` meldet für die öffentliche API keine Warnungen.

### Was noch stört (offen)

1. **Der Skalenfaktor steht doppelt in der Klasse.** Er steckt in den beiden Lambdas und noch
   einmal als vierter Konstruktorparameter (`kelvinPerDegree`); dass beides zusammenpasst, sichert
   nur ein Test. Alle acht Skalen sind linear (`K = Faktor * d + Offset`). Zwei Zahlen pro Einheit
   würden Lambdas, `kelvinPerDegree` und den Konsistenztest ersetzen. Preis: Die Ergebnisse
   weichen in den letzten Stellen von den jetzigen Formeln ab, und mehrere Tests vergleichen exakt
   (z. B. `-459.67` bei Fahrenheit). Nicht ohne Bedarf umbauen.
2. **`getBySign(null)` wirft eine `NullPointerException`**, was nirgends dokumentiert ist. Führende
   und abschließende Leerzeichen werden nicht entfernt (`" °C"` findet nichts). Das ist der einzige
   Punkt mit echtem Absturz und deshalb der erste Kandidat.
3. **Zwei identische Properties-Dateien.** `temperatureunit.properties` und
   `temperatureunit_en.properties` müssen gleich bleiben, ein Test erzwingt es nicht. Ein Test, der
   beide Dateien vergleicht, wäre billig.
4. **Kleinigkeiten im Aufbau.** Die `static`-Map steht mitten in der Klasse zwischen Konstruktor und
   Methoden; die vier `SMALL_...`-Konstanten sind viel Aufwand für drei `replace`-Aufrufe; zwischen
   manchen Methoden fehlt eine Leerzeile.

### Nicht geprüft

`Temperature` hat noch die Punkte aus der ersten Einschätzung offen: keine Validierung von `NaN`,
`null`-Einheit oder Werten unter dem absoluten Nullpunkt, kein `toString()`, kein `Comparable`.

### Empfehlung

Zuerst Punkt 2 (Absturz bei `null`), danach Punkt 3 (kleiner, lohnender Test). Punkt 1 und 4 nur bei
konkretem Anlass.
