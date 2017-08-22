package dbk.rand;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

/**
 * Created by denis on 29.01.17.
 */

public class RandomLevel {

    private static long seek = 0;
    private static Random random = new Random();

    public static Random getR(){
        return random;
    }

    public static void setR(long r) {

        System.out.println("Init random generator with seek:" + r);
        seek = r;
        try {
            final Method resetSeed = Random.class.getDeclaredMethod("resetSeed", long.class);
            resetSeed.setAccessible(true);
            resetSeed.invoke(random, r);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
