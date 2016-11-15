package dbk.abacus;

import java.util.Arrays;
import java.util.List;

/**
 * Created by dbk on 20.08.2016.
 */
public class Tuple2<A, B> implements Comparable<Tuple2<A,B>> {

    private final A a;
    private final B b;

    public static final <A, B> Tuple2<A, B> create(A var0, B var1) {
        return new Tuple2(var0, var1);
    }

    public Tuple2(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public final A getA() {
        return this.a;
    }

    public final B getB() {
        return this.b;
    }

    public List<? extends Object> asList() {
        return Arrays.asList(this.getA(), this.getB());
    }

    public boolean equals(Object var1) {
        if (var1 instanceof Tuple2) {
            Tuple2 var2 = (Tuple2) var1;
            return this.asList().equals(var2.asList());
        } else {
            return false;
        }
    }

    public int hashCode() {
        return a.hashCode() * 1000000 + b.hashCode();
    }

    public String toString() {
        return this.getClass().getSimpleName() + " " + this.asList();
    }


    @Override
    public int compareTo(Tuple2<A, B> o) {
        return  (a.hashCode() * 1000000 + b.hashCode())  - ( o.a.hashCode() * 1000000 + o.b.hashCode());
    }
}