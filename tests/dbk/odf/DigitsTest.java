package dbk.odf;

import org.junit.Assert;
import org.junit.Test;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by dbk on 10-Sep-16.
 */
public class DigitsTest {

    @Test
    public void testAdd() throws Exception {
        Assert.assertTrue(Arrays.equals(new int[]{0,2},Digits.add(new int[]{1}, new int[]{1})));
        Assert.assertTrue(Arrays.equals(new int[]{0, 3, 6}, Digits.add(new int[]{1, 3}, new int[]{2, 3})));
        Assert.assertTrue(Arrays.equals(new int[]{0,2,2},Digits.add(new int[]{1, 1}, new int[]{1, 1})));
        Assert.assertTrue(Arrays.equals(new int[]{1,0}, Digits.add(new int[]{3}, new int[]{ 7})));
        Assert.assertTrue(Arrays.equals(new int[]{1, 3, 2}, Digits.add(new int[]{6, 6}, new int[]{6, 6})));
    }

    @Test
    public void testSum() throws Exception {
        Assert.assertTrue(Arrays.equals(new int[]{0,2},Digits.sum(Arrays.asList(new int[]{1}, new int[]{1}))));
        Assert.assertTrue(Arrays.equals(new int[]{0, 3, 6}, Digits.sum(Arrays.asList(new int[]{1, 3}, new int[]{2, 3}))));
        Assert.assertTrue(Arrays.equals(new int[]{0,2,2},Digits.sum(Arrays.asList(new int[]{1, 1}, new int[]{1, 1}))));
        Assert.assertTrue(Arrays.equals(new int[]{1,0}, Digits.sum(Arrays.asList(new int[]{3}, new int[]{7}))));
        Assert.assertTrue(Arrays.equals(new int[]{1, 3, 2}, Digits.sum(Arrays.asList(new int[]{6, 6}, new int[]{6, 6}))));
        Assert.assertTrue(Arrays.equals(new int[]{0,0},Digits.sum(Arrays.asList(new int[]{1}, new int[]{-1}))));
        Assert.assertTrue(Arrays.equals(new int[]{0, 0, 9}, Digits.sum(Arrays.asList(new int[]{2, 3}, new int[]{-1, -4}))));







    }

    @Test
    public void testGetValue() throws Exception {
        Assert.assertEquals(10, Digits.getValue(new int[]{1, 0}));
        Assert.assertEquals(1, Digits.getValue(new int[]{0,1}));

        Assert.assertEquals(-11, Digits.getValue(new int[]{-1, -1}));
        Assert.assertEquals(113, Digits.getValue(new int[]{1, 1, 3}));
        Assert.assertEquals(-113, Digits.getValue(new int[]{-1, -1, -3}));

    }

    @Test
    public void testE(){
        Assert.assertEquals(1, Digits.E(0));
        Assert.assertEquals(10, Digits.E(1));
        Assert.assertEquals(100, Digits.E(2));
    }
}