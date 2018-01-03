package dbk.generator.types;

import dbk.abacus.Pair;
import dbk.abacus.Tuple2;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Step {

    private final static int[] POW_10 = new int[]{
            1,
            10,
            100,
            1000,
            10000,
            100000,
            1000000,
            10000000,
            100000000,
            1000000000,
    };

    private final int[] values;
    //boolean minusSign = false;
    //private final int maxDigits;


    public Step(int len) {
        values = new int[len];
        //maxDigits = len;
    }

    private Step(int[] values) {
        this.values = values;
        //maxDigits = values.length;
    }

    public Step(int[] values, boolean minusSign) {
        this.values = values;
        //maxDigits = values.length;
        //this.minusSign = minusSign;
    }

    //    public static Step of(int[] values) {
//        return new Step(values);

    //    }
    public Step(int len, Step step) {
        this(len);
        System.arraycopy(step.values, 0, values, 0, step.length());
    }

    public static Step of(int... values) {
        //int[] values1 = IntStream.range(0, values.length).map(i -> values[values.length - 1 - i]).toArray();
        return new Step(values);
    }

    public static Step of(int sum) {
        int sign = sum < 0 ? -1 : 1;
        int digitsNum = ((sum == 0) ? 0 : (int) Math.floor(Math.log10(sum))) + 1;
        int values[] = new int[digitsNum];

        for (int i = 0; i < digitsNum; i++) {
            values[i] = sign * (Math.abs(sum) % 10);
            sum = sum / 10;
        }
        return new Step(values);

    }

    public int length() {
        return values.length;
    }

    public int get(int index) {
        return values[index];
    }

    public int getWithOutSign(int index) {
        if (index == values.length - 1
                && values.length > 1
                && values[values.length - 1] == -1
                && IntStream.of(values).limit(values.length - 2).anyMatch(v -> v > 0)) {
            return 0;
        } else {
            return values[index];
        }
    }

    public void set(int i, int value) {
        values[i] = value;
    }

    private void add(int i, int carry) {
        values[i] += carry;
    }

    public static List<Integer> getValues(List<Step> steps) {
        return steps.stream().map(Step::getValue).collect(Collectors.toList());
//        List<Integer> list = new ArrayList<>(steps.size());
//        for(Step step : steps){
//            list.add(step.getValue());
//        }
//        return list;
    }

    public int getValue() {
//        if (!minusSign) {
        int step = 0;
        for (int j = 0; j < values.length; j++) {
            step += values[j] * POW_10[j];
        }
        return step;
//        } else {
//            int step = 0;
//            for (int j = 0; j < values.length; j++) {
//                int value = invert(values[j]);
//                step += value * Math.pow(10, j);
//            }
//            step = - step - 1;
//            return step;
//        }
    }


    private int invert(int value) {
        return 9 - value;
    }

    @Override
    public String toString() {

        String result = IntStream.range(0, length())
                .map(index -> values[length() - index - 1])
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());
        return String.format("[%s]", result);// + minusString;


    }

    //        return minusCount;
//    public int getMinusCount() {

//    }

    public static Step sum(List<Step> path) {
        int length = path.stream().mapToInt(Step::length).max().getAsInt();
        Step firstStep = path.get(0);
        //int[] sumSimple = new int[length + 1];
        Step sum = new Step(length + 1);

        //System.arraycopy(firstStep, 0, sumSimple, 1, firstStep.length);
        int carry = 0;
        for (Step step : path) {
            int summa = 0;
            carry = 0;
            for (int i = 0; i < length ; i++) {
                int val = step.getByIndex(i) + sum.getByIndex(i);
                carry = (val >= 10) ? 1 : (val < 0) ? -1 : 0;
                summa = (val >= 10) ? val - 10 : (val < 0) ? val + 10 : val;
                //sumSimple[i] = summa;
                sum.set(i, summa);
                //sumSimple[i + 1] += carry;
                sum.add(i + 1, carry);
            }
        }
        if (sum.get(length) >= 10 || sum.get(length) < -10) {
            System.out.println("Overflow in sum of " + path + ", with result " + sum);
        }


        return sum;
    }

    public Step addWithLoan(int[] operand, int maxDigits) {
        Step sum = sum(Arrays.asList(this, new Step(operand)));

        int length = sum.values.length - 1;
        if (sum.get(length) == -1 && (length > 1) && sum.get(length - 1) == 9) {
            int lastNinePos = newSizeWithoutNines(maxDigits, sum);
            int[] result = new int[lastNinePos + 2];
            System.arraycopy(sum.values, 0, result, 0, lastNinePos);
            result[lastNinePos] = 9;
            result[lastNinePos + 1] = -1;
            return new Step(result);

        }

        OptionalInt maxNonEmpty = IntStream.range(0, sum.values.length).filter(i -> sum.values[i] != 0).max();

        if (maxNonEmpty.isPresent() && maxNonEmpty.getAsInt() == sum.values.length - 1) {
            return sum;
        } else {
            return new Step(Arrays.copyOf(sum.values, maxNonEmpty.orElse(0) + 1));
        }


    }

    /**
     * max digits ->2
     * -199 -> -199
     * -1999-> -199
     * -19789-> -1789
     *
     * @param maxDigits
     * @param sum
     * @return
     */
    static int newSizeWithoutNines(int maxDigits, Step sum) {
        int length = sum.values.length;
        OptionalInt first = IntStream.range(2, length)
                .filter(i -> sum.get(length - i - 1) != 9)
                .findFirst();

        int lastNinePos = length - first                 .orElse(length  - maxDigits);
        if (lastNinePos < maxDigits && length > maxDigits) {
            lastNinePos = maxDigits;
        }
        return lastNinePos;
    }


    public int getByIndex(int i) {
        return i < values.length ? values[i] : 0;
    }


    public Step minus(Step step) {
        return null;
    }

    public int[] getValues() {
        return values;
    }

    public List<Tuple2<Integer, Integer>> getValuesAndIndex() {
        return IntStream.range(0, values.length)
                .mapToObj(i -> Pair.of(values[i], i)).collect(Collectors.toList());

    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        Step step = (Step) o;
//
//        //if (minusSign != step.minusSign) return false;
//
//        return Arrays.equals(values, step.values) || getValue() == step.getValue();
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Step step = (Step) o;

        return Arrays.equals(values, step.values);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(values);
//        result = 31 * result + (minusSign ? 1 : 0);
//        result = 31 * result + maxDigits;
        return result;
    }
}
