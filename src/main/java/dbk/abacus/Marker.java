package dbk.abacus;

import dbk.odf.Digs;

import java.util.*;
import java.util.List;

/**
 * Created by dbk on 20-Sep-16.
 */
public abstract class Marker {


    private SortedMap<Tuple2<Integer, Integer>, Count> countMap = new TreeMap<>();

    public boolean mark(List<Integer> steps, boolean addSum) {
        int sum = steps.get(0);
        boolean invalid = false;
        for (int i = 1; i < steps.size() + ((addSum) ? -1 : 0); i++) {

            boolean stepInvalid = mark(sum, steps.get(i));
            invalid = invalid || stepInvalid;
            sum += steps.get(i);
        }
        return invalid;
    }

    private boolean mark(Integer step, Integer pair) {

        int[] firstValue = Digs.getValue(step);
        int[] secondValue = Digs.getValue(pair);
        int maxLen = firstValue.length > secondValue.length ? firstValue.length : secondValue.length;

        int invalidDig = 0;
        int invalidPair = 0;
        for (int i = 0; i < maxLen; i++) {
            int firstDig = (i < firstValue.length) ? firstValue[i] : 0;
            int secondDig = (i < secondValue.length) ? secondValue[i] : 0;
            addToMap(firstDig, secondDig, countMap);
            if (firstDig < 0) {
                invalidDig = firstDig;
                invalidPair = invalidPair;
            }
        }
        if (invalidDig != 0) {
            System.out.println(" invalid " + invalidDig + " " + invalidPair  + " step  " + step + " pair " + pair);
        }
        return invalidDig !=0;

    }

    public static void addToMap(Integer firstValue, Integer pair, SortedMap<Tuple2<Integer, Integer>, Count> map) {
        Tuple2<Integer, Integer> tuple2 = new Tuple2<>(firstValue, pair);
        Count count = map.get(tuple2);
        if (count == null) {
            count = new Count();
            map.put(tuple2, count);
        }
        count.inc();
    }

    public SortedMap<Tuple2<Integer, Integer>, Count> getCountMap() {
        return countMap;
    }

}