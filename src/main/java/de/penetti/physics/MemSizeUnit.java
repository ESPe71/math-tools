package de.penetti.physics;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public enum MemSizeUnit {
  // Basis
  Byte("B", MemSizeUnitType.Byte, BigInteger.ONE),

  // Binary (Base 2)
  KiB("KiB", MemSizeUnitType.Binary, BigInteger.ONE.shiftLeft(10)),    // 2^10
  MiB("MiB", MemSizeUnitType.Binary, BigInteger.ONE.shiftLeft(20)),    // 2^20
  GiB("GiB", MemSizeUnitType.Binary, BigInteger.ONE.shiftLeft(30)),    // 2^30
  TiB("TiB", MemSizeUnitType.Binary, BigInteger.ONE.shiftLeft(40)),    // 2^40
  PiB("PiB", MemSizeUnitType.Binary, BigInteger.ONE.shiftLeft(50)),    // 2^50
  EiB("EiB", MemSizeUnitType.Binary, BigInteger.ONE.shiftLeft(60)),    // 2^60
  ZiB("ZiB", MemSizeUnitType.Binary, BigInteger.ONE.shiftLeft(70)),    // 2^70
  YiB("YiB", MemSizeUnitType.Binary, BigInteger.ONE.shiftLeft(80)),    // 2^80

  // Decimal (Base 10)
  KB("KB", MemSizeUnitType.Decimal, BigInteger.TEN.pow(3)),           // 10^3
  MB("MB", MemSizeUnitType.Decimal, BigInteger.TEN.pow(6)),           // 10^6
  GB("GB", MemSizeUnitType.Decimal, BigInteger.TEN.pow(9)),           // 10^9
  TB("TB", MemSizeUnitType.Decimal, BigInteger.TEN.pow(12)),          // 10^12
  PB("PB", MemSizeUnitType.Decimal, BigInteger.TEN.pow(15)),          // 10^15
  EB("EB", MemSizeUnitType.Decimal, BigInteger.TEN.pow(18)),          // 10^18
  ZB("ZB", MemSizeUnitType.Decimal, BigInteger.TEN.pow(21)),          // 10^21
  YB("YB", MemSizeUnitType.Decimal, BigInteger.TEN.pow(24));          // 10^24

  // --- Statische Listen und Cache ---
  private static final List<MemSizeUnit> BASE     = List.of(Byte);
  private static final List<MemSizeUnit> BINARIES = List.of(KiB, MiB, GiB, TiB, PiB, EiB, ZiB, YiB);
  private static final List<MemSizeUnit> DECIMALS = List.of(KB, MB, GB, TB, PB, EB, ZB, YB);

  // Cache für schnelleren Lookup (O(1) statt O(n))
  private static final Map<String, MemSizeUnit> SYMBOL_MAP;
  static {
    Map<String, MemSizeUnit> map = new java.util.HashMap<>();
    for (MemSizeUnit unit : values()) {
      map.put(unit.symbol.toUpperCase(), unit);
    }
    SYMBOL_MAP = Map.copyOf(map);
  }

  static final Pattern SIZE_PATTERN = Pattern.compile(
      "^\\s*([+-]?[\\d.,'’\\u00a0\\u202f]*\\d)\\s*(B|KB|MB|GB|TB|PB|EB|ZB|YB|KiB|MiB|GiB|TiB|PiB|EiB|ZiB|YiB)?\\s*$",
      Pattern.CASE_INSENSITIVE
  );

  private final String symbol;
  private final MemSizeUnitType type;
  private final BigInteger factor;

  MemSizeUnit(String symbol, MemSizeUnitType type, BigInteger factor) {
    this.symbol = symbol;
    this.type = type;
    this.factor = factor;
  }

  public String getSymbol() {
    return symbol;
  }

  public MemSizeUnitType getType() {
    return type;
  }

  public BigInteger getFactor() {
    return factor;
  }

  /**
   * Ruft die MemSizeUnit basierend auf dem Symbol zurück.
   * @param unit Das Symbol (z.B. "GB", "gib", "MiB"). Null oder leer wird als Byte behandelt.
   * @return Die entsprechende Enum-Konstante.
   * @throws IllegalArgumentException Wenn das Symbol nicht erkannt wird.
   */
  static MemSizeUnit of(String unit) {
    if (unit == null || unit.isBlank() || "BYTE".equalsIgnoreCase(unit.trim())) {
      return Byte;
    }

    // Nutze den Map-Cache für Performance
    MemSizeUnit found = SYMBOL_MAP.get(unit.trim().toUpperCase());
    if (found == null) {
      throw new IllegalArgumentException("Unbekannte Speichergrößeneinheit: " + unit);
    }
    return found;
  }

  /**
   * Gibt alle Einheiten eines bestimmten Typs zurück.
   */
  static List<MemSizeUnit> of(MemSizeUnitType type) {
    return switch (type) {
      case Byte -> BASE;
      case Binary -> BINARIES;
      case Decimal -> DECIMALS;
    };
  }
}