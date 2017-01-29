package dbk.abacus;

/**
 * Created by dbk on 20-Sep-16.
 */
public class Count {
    int value = 0;
    public int inc() {
        return ++value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
