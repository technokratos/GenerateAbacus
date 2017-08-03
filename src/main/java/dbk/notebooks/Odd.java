package dbk.notebooks;

/**
 * Created by denis on 03.08.17.
 */
public enum Odd {
    ODD, EVEN;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
