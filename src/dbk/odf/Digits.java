package dbk.odf;

import dbk.abacus.Tuple2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by dbk on 05-Sep-16.
 */
public class Digits {
    public static int[] add(int[] a, int[] b) {
        //IntStream.range(0, value.length).mapToObj(i-> sum[i] + value[i]).mapToInt(i-> i.intValue()).toArray();
        int summa =0;
        int carry = 0;
        int[] result = new int[b.length + 1];
        for (int i = b.length -1 ; i >=0 ; i--) {
            int val = a[i] + b[i] + carry;
            carry = (val >= 10)? 1:0;
            summa = (val >= 10)? val - 10: val;
            result[i +1] = summa;
        }
        result[0] = carry;
        return result;

    }
    public static int[] sum(List<int[]> path) {
        int length = path.stream().mapToInt(step-> step.length).max().getAsInt();
        int[] firstStep = path.get(0);
        int[] sum = new int[length + 1];
        //System.arraycopy(firstStep, 0, sum, 1, firstStep.length);
        for(int[] step: path){
            int summa = 0;
            int carry = 0;
            for (int i = step.length -1 ; i >=0 ; i--) {
                int val = step[i] + sum[i+1] + carry;
                carry = (val >= 10)? 1:(val<0)? -1:0;
                summa = (val >= 10)? val - 10: (val<0)? val + 10: val;
                sum[i +1] = summa;
            }
            sum[0] = +carry;

        }
        return sum;
    }
    public static Tuple2<int[], Integer> sumCarry(List<int[]> path) {
        int[] sum = sum(path);
        return  new Tuple2<>(Arrays.copyOfRange(sum, 1, sum.length), sum[0]);
    }



    public static int getValue(int[] digits) {
        int step =0;
        for (int j = digits.length - 1; j >=0; j--) {
            step += digits[j] * Math.pow(10, digits.length  - j - 1);
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
}
