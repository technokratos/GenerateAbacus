package dbk.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Step {

    private final int[] values;
    boolean minus = false;


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

    public void add(int i, int carry) {
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
        return step;
    }

    @Override
    public String toString() {
        String result = IntStream.range(1, length())
                .map(index -> values[length() - index])
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());

        return ((minus)? "*": "") + result;
    }

    public static Step sum(List<Step> path) {
        int length = path.stream().mapToInt(Step::length).max().getAsInt();
        Step firstStep = path.get(0);
        //int[] sumSimple = new int[length + 1];
        Step sum = new Step(length + 1);

        //System.arraycopy(firstStep, 0, sumSimple, 1, firstStep.length);
        int carry = 0;
        for(Step step: path){
            int summa = 0;
            carry = 0;
            for (int i = 0; i<step.length() ; i++) {
                int val = step.get(i) + sum.get(i);
                carry = (val >= 10)? 1:(val<0)? -1:0;
                summa = (val >= 10)? val - 10: (val<0)? val + 10: val;
                //sumSimple[i] = summa;
                sum.set(i, summa);
                //sumSimple[i + 1] += carry;
                sum.add(i+1,  carry);
            }
        }
        return sum;
    }
}
