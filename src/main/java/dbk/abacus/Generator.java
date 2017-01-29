package dbk.abacus;

import dbk.rand.Rand;

import java.util.Arrays;
import java.util.Random;

import static java.util.Arrays.stream;

/**
 * Created by dbk on 15-Aug-16.
 */
public class Generator {
    final int[] series;
    int index = 0;
    final int limit;

    final Random r = getRandom();

    public static Random getRandom() {
        return Rand.getR();
    }

    public Generator(int size, int limit) {
        this.limit = limit;
        this.series = new int[size];
    }


    public int next(){
        return nextStep(limit);
    }
    public int nextStep(int maxLim) {
        return nextStep(maxLim, true);
    }
    public int nextStep(int maxLim, boolean nullSum) {

        int currSum = sum();
        //???????? ????????? ????? ?? ?????? ?????????? ?????.
        int max = (currSum <0)? maxLim: maxLim - currSum;

        //?? ?????? ???? ????????????? ???????????
        int min = (nullSum) ? currSum : (currSum - 1);
        int randPlus = randPlus(max);
        int randMinus = randMinus(min);
        //????? ? ???????????? 0.5 ?????????????? ??? ?????????????? ???????? ???? ??? ?? ????
        int nextValue = (r.nextFloat()> 0.5 && randPlus != 0 || randMinus == 0)? randPlus : randMinus;
        series[index] = nextValue;
        index++;
        return nextValue;
    }


    public void generate(){
        firstStep(limit);
        for (int i = 1; i < series.length; i++) {
            next();
        }
    }



    public int randMinus(int min) {
        if (min == 0) return 0;
        return -(r.nextInt(Math.abs(min)) + 1);
    }
    public int randPlus(int max) {
        if (max == 0) return 0;
        try {
            return r.nextInt(max) + 1;
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage() + " max " + max);
            throw e;
            //return 0;
        }
    }


    public int sum() {
        return stream(series).sum();
//        int sum =0;
//        for (int i = 0; i < index; i++) {
//            sum+= series[i];
//        }
//        return sum;
    }

    /**
     * from 1 to limit include both
     */
    public int firstStep(int limit) {
        int nextValue = r.nextInt(limit) + 1;
        series[index] = nextValue;
        index++;
        return nextValue;
    }
    public int firstStep(int min, int max){
        int nextValue = r.nextInt(max - min + 1) + min;
        series[index] = nextValue;
        index++;
        return nextValue;
    }


    @Override
    public String toString() {
        return Arrays.toString(series) + " sum: " + sum();
    }
}
