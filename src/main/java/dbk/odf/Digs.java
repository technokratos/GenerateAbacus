package dbk.odf;

import dbk.abacus.Tuple2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by dbk on 13-Sep-16.
 */
public class Digs {

    public static boolean checkSign(int[] value) {
        return IntStream.of(value).allMatch( i -> i >=0) || IntStream.of(value).allMatch(i-> i <=0);
    }
    public static boolean isNegative(int[] value) {
        IntStream stream = IntStream.of(value);
        return stream.anyMatch(i -> i < 0);
    }
    public static boolean isNegZero(int[] value) {
        return IntStream.of(value).allMatch(i -> i <=0);
    }

    public static List<Integer> getValue(List<int[]> steps) {
        List<Integer> list = new ArrayList<>(steps.size());
        for(int[] step : steps){
            list.add(getValue(step));
        }
        return list;
    }

    public static int[] getValue(int value) {
        List<Integer> list = new ArrayList<>();

        do {
            int digit = value % 10;
            list.add(digit);
            value = value/10;

        } while (Math.abs(value) > 0);
        return  list.stream().mapToInt(i-> i).toArray();
    }


    public static int[] add(int[] a, int[] b) {
        //IntStream.range(0, value.length).mapToObj(i-> sum[i] + value[i]).mapToInt(i-> i.intValue()).toArray();
        int summa =0;
        int carry = 0;
        int length = ((a.length > b.length) ? a.length : b.length);
        int[] result = new int[length + 1];
        for (int i = 0; i< length ;i++) {
            int val = getByIndex(a,i)  + getByIndex(b, i) + carry;
            carry = (val >= 10)? 1:0;
            summa = (val >= 10)? val - 10: val;
            result[i] = summa;
        }
        result[b.length] = carry;
        return result;

    }

    private static int getByIndex(int[] b, int i) {
        return i < b.length? b[i]: 0;
    }

    public static int[] sum(List<int[]> path) {

        int length = path.stream().mapToInt(step-> step.length).max().getAsInt();
        int[] firstStep = path.get(0);
        int[] sum = new int[length + 1];
        //System.arraycopy(firstStep, 0, sum, 1, firstStep.length);
        int carry = 0;
        for(int[] step: path){
            int summa = 0;
            carry = 0;
            for (int i = 0; i<step.length ; i++) {
                int val = step[i] + sum[i];
                carry = (val >= 10)? 1:(val<0)? -1:0;
                summa = (val >= 10)? val - 10: (val<0)? val + 10: val;
                sum[i] = summa;
                sum[i + 1] += carry;
            }
        }
        return sum;
    }

    public static int[] sumWithCut(List<int[]> path) {
        int[] sum = sum(path);
        int lastValuePosition = sum.length - 1;
        int lastLengthPosition = path.get(path.size() - 1).length - 1;
        for (int i = sum.length - 1 ; i >= lastLengthPosition; i--) {
            if (sum[i] != 0) {
                lastValuePosition = i;
            }
        }

        if (lastValuePosition < sum.length - 1) {
            int cutSum[] = new int[lastValuePosition + 1];
            System.arraycopy(sum, 0, cutSum, 0, cutSum.length);
            return cutSum;
        } else {
            return sum;
        }
    }

    public static Tuple2<int[], Integer> sumCarry(List<int[]> path) {
        int[] sum = sum(path);
        return  new Tuple2<>(Arrays.copyOfRange(sum, 0, sum.length - 1), sum[sum.length - 1]);
    }



    public static int getValue(int[] digits) {
        int step =0;
        for (int j = 0; j < digits.length; j++) {
            step += digits[j] * Math.pow(10, j);
        }
        return step;
    }



    public static int E(int i) {
        int r = 1;
        for (int j = 0; j < i; j++) {
            r*=10;
        }
        return r;
    }


    public static boolean possibleNegativeCarry(int[] sum, int i) {
        if (i>= sum.length) {
            return false;
        }
        if (sum[i] > 0) {
            return true;
        } else if (i < sum.length -1 ) {
            return possibleNegativeCarry(sum, i + 1);
        } else {
            return false;
        }
    }


    public static boolean possiblePositiveCarry(int[] sum, int i) {
        if (i>= sum.length) {
            return false;
        }
        if (sum[i] < 9) {
            return true;
        } else if (i < sum.length - 1 ) {
            return possiblePositiveCarry(sum, i + 1);
        } else {
            return false;
        }
    }
}
