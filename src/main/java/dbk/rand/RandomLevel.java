package dbk.rand;

import java.util.Random;

/**
 * Created by denis on 29.01.17.
 */

public class RandomLevel {

    private static long seek = 0;
    private static Random random = null;

    public static Random getR(){
        return random;
    }

    public static void setR(int r) {
        System.out.println("Init random generator with seek:" +r);
        seek = r;
        random = new Random(seek);
    }
}
