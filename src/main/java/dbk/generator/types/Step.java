package dbk.generator.types;

import dbk.abacus.Pair;
import dbk.abacus.Tuple2;
import dbk.generator.Digs;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Step {

    private final int[] values;
    int minusCount = 0;


    public Step(int len) {
        values = new int[len];
    }

    private Step(int[] values) {
        this.values = values;
    }

    public static Step of(int[] values) {
        return new Step(values);
    }

    public Step(int len, Step step) {
        this(len);
        System.arraycopy(step.values,0, values, 0, step.length() );
    }

    public int length() {
        return values.length;
    }

    public int get(int index) {
        return values[index];
    }

    public void set(int i, int value) {
        values[i] = value;
    }

    public void sumDig(int i, int carry) {
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
        int step =0;
        for (int j = 0; j < values.length; j++) {
            step += values[j] * Math.pow(10, j);
        }
        return step + getMinusCarryValue();
    }

    @Override
    public String toString() {
        String result = IntStream.range(0, length())
                .map(index -> values[length() - index - 1])
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());
        String minusString = "";
        if (minusCount >0){
            int sum = getMinusCarryValue();
            minusString = String.valueOf(sum);
        }
        return "("+minusCount+")" + result;// + minusString;
    }

    private int getMinusCarryValue() {
        int multi = 1;
        int sum = 0;
        for (int i = 0; i < minusCount; i++) {
            multi *=10;
            sum+= multi;
        }
        return -sum;
    }

    public int getMinusCount() {
        return minusCount;
    }

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
            for (int i = 0; i < step.length(); i++) {
                int val = step.get(i) + sum.get(i);
                carry = (val >= 10) ? 1 : (val < 0) ? -1 : 0;
                summa = (val >= 10) ? val - 10 : (val < 0) ? val + 10 : val;
                //sumSimple[i] = summa;
                sum.set(i, summa);
                //sumSimple[i + 1] += carry;
                sum.sumDig(i + 1, carry);
            }
        }
        sum.setMinusCount(path.stream().mapToInt(Step::getMinusCount).sum());
        return sum ;
    }

    public Step addWithLoan(int[] operand) {
        int[] add = Digs.add(values, operand);

        Step result = new Step(add);
        if(Digs.getValue(add) < 0) {
            result.minusCount = this.minusCount - 1;
        }
        return result;

    }

    private static int getByIndex(int[] b, int i) {
        return i < b.length? b[i]: 0;
    }

    public void setMinusCount(int minusCount) {
        this.minusCount = minusCount;
    }

    public Step minus(Step step) {
        return null;
    }

    public static Step of(int sum) {
        int sign = sum < 0? -1: 1;
        int digitsNum = ((sum == 0) ? 0:  (int) Math.floor(Math.log10(sum))) + 1;
        int values[] = new int[digitsNum];

        for (int i = 0 ; i < digitsNum; i++) {
            values[i]= sign * (Math.abs(sum) % 10);
            sum = sum / 10;
        }
        return new Step(values);

    }

    public int[] getValues() {

        return values;
    }

    public List<Tuple2<Integer, Integer>> getValuesAndIndex() {
        return IntStream.range(0, values.length)
                .mapToObj(i-> Pair.of(values[i], i)).collect(Collectors.toList());

    }
}
