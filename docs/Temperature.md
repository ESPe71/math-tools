# Einschätzung: `Temperature`

Datei: `src/main/java/de/penetti/physics/Temperature.java`
Zugehörig: `TemperatureUnit` (siehe `TemperaturUnit.md`), `TemperatureTest`
Stand: 20.11.2026, nach der Korrektur des Delta-Fehlers (Commit `2e463ab`) und der Umstellung auf
`TemperatureUnit.convertDifference` (Commit `31aa4cb`).

## Gesamteindruck

Eine kleine, saubere Wertklasse: `final`, unveränderliche Felder, `equals` und `hashCode` sind
zueinander konsistent. Die Aufteilung ist gut (Wert plus Einheit, die Umrechnung liegt in
`TemperatureUnit`). Nach der Korrektur ist `equals(Temperature, Temperature)` richtig. Offen sind vor
allem fehlende Prüfungen im Konstruktor, fehlendes Javadoc und einige Methoden, die man von einem
solchen Typ erwartet.

Im Hauptcode wird `Temperature` bisher nirgends verwendet (nur `TemperatureUnit` verweist per
`@see` darauf). Die Klasse ist also eine reine Bibliotheksklasse, und Änderungen an ihrer API
treffen nur Tests und externe Nutzer.

## Was gut ist

- **Unveränderlich und `final`.** Keine Setter, keine Vererbung, die `equals` brechen könnte.
- **Zwei klar getrennte Gleichheitsbegriffe.** `equals(Object)` vergleicht Wert und Einheit
  (streng, im Javadoc erklärt); `equals(Temperature, Temperature)` vergleicht die absolute
  Temperatur mit Toleranz.
- **Das Delta ist jetzt eine Differenz.** Ein Delta von 1 °C ist 1 K, nicht 274,15 K. Die
  Berechnung liegt in `TemperatureUnit.convertDifference` und ist dort getestet; die Klasse
  selbst hat keine eigene Skalenlogik mehr.
- **`equals`/`hashCode` sind konsistent**, auch bei `NaN` (gleich sich selbst) und `-0.0` (ungleich
  `0.0`, mit unterschiedlichem Hash). Das entspricht dem Verhalten von `Double.equals`.

## Verbesserungsvorschläge

### 1. Keine Prüfung im Konstruktor (wichtigster Punkt)
Der Konstruktor akzeptiert alles. Ich habe es ausprobiert:
- Eine `null`-Einheit wird angenommen, der Fehler (`NullPointerException`) kommt erst viel später
  in `getBaseTemperature()`, weit weg von der Ursache.
- `-5 K` ist möglich, also eine Temperatur unter dem absoluten Nullpunkt.
- `NaN` und unendliche Werte sind möglich.

Mindestens `Objects.requireNonNull(unit, ...)` im Konstruktor. Ob Werte unter dem absoluten
Nullpunkt abgelehnt werden sollen, ist eine fachliche Entscheidung: Für eine Bibliothek, die auch
Differenzen oder Zwischenwerte darstellt, kann das zu streng sein. Dann gehört es höchstens in eine
eigene Prüfmethode oder in die Dokumentation.

### 2. `null`-Argumente bei `equals(Temperature, Temperature)`
`equals(null, delta)` und ein `null`-Delta werfen eine `NullPointerException` ohne Hinweis. Das ist
bei Java-Methoden üblich, sollte aber im Javadoc stehen oder mit `Objects.requireNonNull` und einer
Meldung versehen werden.

### 3. Javadoc ist unvollständig
`javadoc -Xdoclint:all` meldet zehn Warnungen: Die Klasse hat keine Beschreibung; Konstruktor,
`getValue()`, `getUnit()` und `getBaseTemperature()` haben keinen Kommentar; in den beiden
`equals`-Methoden sind `@param` und `@return` leer. Besonders nützlich wären:
- was `getBaseTemperature()` liefert (absolute Temperatur in Kelvin) und dass es ein Wert ist, kein
  `Temperature`-Objekt;
- dass `equals(Temperature, Temperature)` die Toleranz als **Differenz** auffasst (steht im
  Beschreibungstext, aber `@param delta` ist leer);
- ob die Toleranz inklusive ist (ja: `<=`).

### 4. Der Methodenname `equals(Temperature, Temperature)` ist missverständlich
Ein Überladen von `equals` mit zwei Parametern fällt in einer Methodenliste kaum auf und erinnert an
den Vertrag von `Object.equals`, den die Methode nicht erfüllt (keine Transitivität, wie jede
Toleranzprüfung). Ein Name wie `isEqualTo(other, tolerance)` oder `isCloseTo(...)` sagt klarer, was
passiert. Das wäre eine API-Änderung und nur sinnvoll, wenn es noch keine externen Aufrufer gibt.
Das Projekt hat schon einmal `isEqualTo` bei `MemSize` entfernt, weil `equals` genügte; hier genügt
`equals` aber **nicht**, weil die Toleranz dazukommt.

### 5. Es fehlen übliche Methoden
- **`toString()`.** Aktuell kommt `de.penetti.physics.Temperature@5c64dd34` heraus (geprüft). Sinnvoll
  wäre z. B. `37.5 °C`, mit `getUnit().getUnitSign()`.
- **`to(TemperatureUnit)`.** Liefert eine neue `Temperature` in der anderen Einheit. Heute muss man
  `unit.getValue(...)` selbst aufrufen und das Ergebnis neu verpacken.
- **`Comparable<Temperature>`** über die Kelvin-Temperatur. Wichtig: Dann stimmt `compareTo == 0`
  nicht mit `equals` überein (`0 °C` und `273,15 K` sind `compareTo`-gleich, aber nicht `equals`-
  gleich). Das gehört ins Javadoc, wie bei `BigDecimal`.
- Der Name `getBaseTemperature()` ist unklar; `toKelvin()` wäre selbsterklärend. Umbenennen nur,
  wenn die API ohnehin geändert wird.

### 6. Kleinigkeiten im Code
- `equals(Object)` ist unnötig verschachtelt. Mit einem `instanceof`-Pattern und einem einzigen
  `return` (`Double.compare(...) == 0 && unit == other.unit`) wird es ein Dreizeiler.
- In `equals(Temperature, Temperature)` deckt `Math.abs(b1 - b2) <= d` den Fall gleicher Werte
  schon ab; der erste `Double.compare`-Zweig ist nur für `NaN` und unendliche Werte nötig
  (dort ist die Differenz `NaN`). Das ist richtig, aber ohne Kommentar nicht erkennbar.
- `hashCode` ist von Hand geschrieben. `Objects.hash(value, unit)` wäre kürzer. Der Hash der Einheit
  ist bei Enums der Identitäts-Hash und damit zwischen JVM-Läufen nicht stabil; für `equals`/
  `hashCode` unkritisch, aber nicht für Dinge wie persistierte Hash-Werte.
- Die private Hilfsmethode `getDifferenceInKelvin()` steht mitten zwischen den öffentlichen
  Methoden.

### 7. Tests
- `testLength_superficial` heißt noch nach der Längenklasse (kopiert aus `LengthTest`), obwohl sie
  `Temperature` prüft. Umbenennen.
- Nicht abgedeckt: `NaN`, `-0.0`, unendliche Werte, `null`-Argumente, ein negatives Delta in der
  Eingabe (z. B. `-1 K`, das über `Math.abs` positiv wird), und dass die Toleranz inklusive ist
  (Differenz genau gleich Delta).
- Mehrere Tests vergleichen Gleitkommawerte mit `PhysicsUtils.DELTA`; das ist in Ordnung.

## Empfohlene Reihenfolge

1. Punkt 1 (`null` im Konstruktor ablehnen) und Punkt 3 (Javadoc): klein, risikoarm.
2. Punkt 7 (Test umbenennen, Randfälle ergänzen), dann Punkt 6 (Aufräumen mit Tests als Netz).
3. Punkt 5 (`toString`, `to(...)`, `Comparable`): neue API, nur bei konkretem Bedarf.
4. Punkt 4 (Umbenennung): nur wenn keine externen Nutzer betroffen sind.

## Was ich lassen würde

Die Aufteilung in `Temperature` und `TemperatureUnit`, die Unveränderlichkeit, die strenge
`equals(Object)` (Wert und Einheit) und die Toleranzprüfung als eigene Methode.
