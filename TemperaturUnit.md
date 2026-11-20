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

### 2. `Function<Double, Double>` statt `DoubleUnaryOperator` (klein, risikoarm)
`Function<Double, Double>` boxt bei jeder Umrechnung. `DoubleUnaryOperator` ist der passende
Typ. Das Schlüsselwort `transient` an den Feldern ist bei Enums wirkungslos
(Serialisierung läuft über den Namen) und kann entfallen.

### 3. `getBySign` ist umständlich
- Vier Konstanten (`DEGREE_SIGN`, `SMALL_E`, `SMALL_ACUTE_E`, ...) für drei `replace`-Aufrufe.
- Bei jedem Aufruf werden alle Einheiten neu normalisiert.
- Besser: eine `Map`, einmal im `static`-Block aufgebaut. Dabei klären, ob Kleinschreibung
  (`k`, `c`) akzeptiert werden soll. Aktuell ist die Suche case-sensitiv; das steht nirgends
  dokumentiert.

### 4. Keine Differenzumrechnung
Die Formeln rechnen absolute Temperaturen um. Für Temperaturdifferenzen (Toleranzen, Deltas)
gilt nur der Skalenfaktor, nicht der Offset. Das hat in `Temperature.equals(Temperature,
Temperature)` zu einem Fehler geführt (Delta von 1 °C wurde zu 274,15 K). Er ist behoben, indem
`Temperature` das Delta über Wert minus Nullpunkt umrechnet.
Sauberer wäre ein Skalenfaktor pro Einheit im Enum (Celsius 1, Fahrenheit 5/9, Delisle negativ
2/3). Die Differenz wäre dann `Math.abs(value * scale)`, und die Fachlogik läge dort, wo die
Einheit definiert ist.

### 5. Umrechnung immer über Kelvin
Das ist als Zwischenschritt richtig und vermeidet n×n Formeln. Jede Umrechnung enthält dadurch
aber zwei Rundungsschritte. Die Tests brauchen deshalb Toleranzen. Praktisch unkritisch, aber
gut zu wissen.

### 6. Sprache wird einmal festgelegt
`RESOURCE` ist ein `static final`-Feld; die Locale gilt, die beim ersten Laden der Klasse
eingestellt ist. `getName()` hat keinen `Locale`-Parameter. Die Bundles für fr und es sind
vorhanden, aber nicht durch Tests abgedeckt. Wenn mehrere Sprachen wirklich unterstützt werden
sollen: `getName(Locale)` ergänzen und je Sprache einen Test schreiben.

### 7. Dokumentation
Es fehlt Javadoc für die Klasse und die öffentlichen Methoden. Wichtig wären vor allem:
- Die Formeln rechnen absolute Temperaturen, keine Differenzen.
- Reihenfolge der Konstruktorparameter (`fromKelvin`, dann `toKelvin`) ist leicht zu verwechseln.
- Verhalten von `getBySign` (Groß-/Kleinschreibung, Weglassen von `°`).

## Empfohlene Reihenfolge

1. Punkt 1 und 2 (klein, risikoarm). *Punkt 1 ist erledigt.*
2. Punkt 3 (Map statt Schleife) zusammen mit der Entscheidung zur Groß-/Kleinschreibung.
3. Punkt 4, sobald `Temperature` weitere Differenz-Operationen bekommt.
4. Punkt 6 nur, wenn Mehrsprachigkeit im Betrieb tatsächlich gebraucht wird.
5. Punkt 7 laufend.

## Was ich lassen würde

Das Enum selbst, die Lambdas als Datenfelder, die `Optional`-Rückgabe von `getBySign` und die
Umrechnung über Kelvin als gemeinsame Basis.
