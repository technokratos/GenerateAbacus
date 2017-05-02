package dbk.rand;

import java.util.Random;

/**
 * Created by denis on 29.01.17.
 */
public class RandomLevel {

    private static long seek = 0;
    public static Random getR(){
        return new Random(seek++);
    }

}
