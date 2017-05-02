package dbk.abacus;

import dbk.rand.RandomLevel;

import java.util.Random;

public class Main {
    static Random r = RandomLevel.getR();
    public static void main(String[] args) {

        int[][] first = generateSeries(3, 10);

        for (int[] a:first) {
            for( int v: a) {
                System.out.print(v + ",");
            }
            System.out.println();
        }
    }

    private static int[][] generateSeries(int steps, int n) {
        int[][] series = new int[n][steps];
        for(int i =0;i< n; i++) {
            series[i] = generateStep(steps, 4);
        }
        return series;
    }

    private static int[] generateStep(int steps, int limit) {
        int[] array = new int[steps];
        for (int i = 0; i < array.length; i++) {

            int value = r.nextInt(limit) + 1;
            int min = -value;
            int max = limit - value;
            int nextValue = r.nextInt(max - min) + min;
            int zeroCorrect = (Math.abs(min) > Math.abs(max)) ? -1 : 1;
            nextValue = (nextValue == 0) ? nextValue + zeroCorrect : nextValue;
            array[i] = nextValue;
        }
//        System.out.println(nextValue);
        return array;
    }

    /**
     * ALL INCLUSIVE
     * @param min
     * @param max
     * @return
     */
    static int randBetween(int min, int max) {
        // 1 - 4;
        // -2   4  (6), -2
        //0 5
        return r.nextInt(max - min + 1) + min;
    }
}
