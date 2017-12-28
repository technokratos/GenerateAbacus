package dbk.generator.probability;

public enum Carry {
    NONE(0), PLUS(1), MINUS(-1);
    final int value;

    Carry(int value) {
        this.value = value;
    }
}
