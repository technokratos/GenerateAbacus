package dbk.abacus;

/**
 * Created by denis on 15.09.17.
 */
public class Pair extends Tuple2<Integer, Integer> {
    private static double modifier = 0.5;
    private final double initWeight;

    public Pair(Integer up, Integer down, double weight) {
        super(up, down);
        this.weight = weight;
        initWeight = weight;
    }

    public Integer getUp() {
        return this.getA();
    }

    public Integer getDown() {
        return this.getB();
    }

    private double weight;

    private Count count = new Count();

    public void inc() {
        count.inc();
        weight = weight * modifier;
    }

    public Integer getCount() {
        return count.value;
    }

    public double getWeight() {
        return weight;
    }

    public void resetWeight() {
        weight = initWeight;
    }

    public static void setModifier(double modifier) {
        Pair.modifier = modifier;
    }
}
