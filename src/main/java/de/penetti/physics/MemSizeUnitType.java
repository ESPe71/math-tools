package de.penetti.physics;

public enum MemSizeUnitType {
  Byte(1),
  Decimal(1000),  // SI
  Binary(1024);   // IEC

  private final long base;

  MemSizeUnitType(long base) {
    this.base = base;
  }

  public long getBase() {
    return base;
  }
}