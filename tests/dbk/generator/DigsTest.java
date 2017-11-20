package dbk.generator;

import dbk.generator.Digs;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static dbk.generator.Digs.*;

/**
 * Created by dbk on 13-Sep-16.
 */
public class DigsTest {

    @Test
    public void testAdd() throws Exception {
        Assert.assertTrue(Arrays.equals(new int[]{2,0}, Digs.add(new int[]{1}, new int[]{1})));
        Assert.assertTrue(Arrays.equals(new int[]{3, 6, 0}, Digs.add(new int[]{1, 3}, new int[]{2, 3})));
        Assert.assertTrue(Arrays.equals(new int[]{2,2,0}, Digs.add(new int[]{1, 1}, new int[]{1, 1})));
        Assert.assertTrue(Arrays.equals(new int[]{0,1}, Digs.add(new int[]{3}, new int[]{7})));
        Assert.assertTrue(Arrays.equals(new int[]{2, 3, 1}, Digs.add(new int[]{6, 6}, new int[]{6, 6})));
        Assert.assertTrue(Arrays.equals(new int[]{2, 7, 0}, Digs.add(new int[]{6}, new int[]{6, 6})));
    }

    @Test
    public void testImpossibleNegativeCarry() throws Exception {

        Assert.assertFalse(possibleNegativeCarry(new int[]{0, 0, 0}, 0)); ;
        Assert.assertTrue(possibleNegativeCarry(new int[]{1, 0, 0}, 0));
        Assert.assertFalse(possibleNegativeCarry(new int[]{0, 0}, 0)); ;
        Assert.assertTrue(possibleNegativeCarry(new int[]{3, 0}, 0));
        Assert.assertFalse(possibleNegativeCarry(new int[]{ 0}, 0)); ;
        Assert.assertTrue(possibleNegativeCarry(new int[]{3 }, 0));

        Assert.assertTrue(possibleNegativeCarry(new int[]{0, 0, 1}, 2)); ;
        Assert.assertFalse(possibleNegativeCarry(new int[]{0, 1, 0}, 2));
    }

    @Test
    public void testPossiblePositiveCarry() throws Exception {

        Assert.assertTrue(possiblePositiveCarry(new int[]{0, 0, 0}, 0)); ;

        Assert.assertFalse(possiblePositiveCarry(new int[]{9, 9}, 0)); ;
        Assert.assertTrue(possiblePositiveCarry(new int[]{8, 9}, 0));
        Assert.assertFalse(possiblePositiveCarry(new int[]{9}, 0)); ;
        Assert.assertTrue(possiblePositiveCarry(new int[]{8}, 0));

        Assert.assertTrue(possiblePositiveCarry(new int[]{0, 0, 8}, 2)); ;
        Assert.assertFalse(possiblePositiveCarry(new int[]{0, 1, 9}, 2));
    }

    @Test
    public void testSum() {
//        1
//        9
//        9
//        1
        Assert.assertTrue(Arrays.equals(new int[]{2, 0}, sum(Arrays.asList(new int[]{1}, new int[]{1}))));
        Assert.assertTrue(Arrays.equals(new int[]{3, 6, 0}, sum(Arrays.asList(new int[]{1, 3}, new int[]{2, 3}))));
        Assert.assertTrue(Arrays.equals(new int[]{2,2,0},sum(Arrays.asList(new int[]{1, 1}, new int[]{1, 1}))));
        Assert.assertTrue(Arrays.equals(new int[]{0,1}, sum(Arrays.asList(new int[]{3}, new int[]{7}))));
        Assert.assertTrue(Arrays.equals(new int[]{2, 3, 1}, sum(Arrays.asList(new int[]{6, 6}, new int[]{6, 6}))));
        Assert.assertTrue(Arrays.equals(new int[]{0,0},sum(Arrays.asList(new int[]{1}, new int[]{-1}))));
        Assert.assertTrue(Arrays.equals(new int[]{8, 1, 0}, sum(Arrays.asList(new int[]{2, 3}, new int[]{-4, -1}))));
        Assert.assertTrue(Arrays.equals(new int[]{0, 2}, sum(Arrays.asList(new int[]{1}, new int[]{9}, new int[]{9}, new int[]{1}))));


//        93        615        94        191        112 = 10012
        //0         17        91                -16                -9808

        //93 + 615 = 708
        Assert.assertTrue(Arrays.equals( new int[]{8,0,7,0}, sum(Arrays.asList(new int[]{3,9}, new int[]{5,1,6}))));
        //708+ 94 = 802
        Assert.assertTrue(Arrays.equals( new int[]{2,0,8,0}, sum(Arrays.asList(new int[]{8,0,7}, new int[]{4, 9}))));
        // 802 + 191 = 1003
        Assert.assertTrue(Arrays.equals( new int[]{3,9,9,0}, sum(Arrays.asList(new int[]{2,0,8}, new int[]{1, 9, 1}))));
        Assert.assertTrue(Arrays.equals( new int[]{5,0,1,1}, sum(Arrays.asList(new int[]{3,9,9}, new int[]{2, 1, 1}))));
        Assert.assertTrue(Arrays.equals( new int[]{5,5,5,0}, sum(Arrays.asList(new int[]{9,3}, new int[]{6,1,5}))));
        Assert.assertTrue(Arrays.equals( new int[]{4,0,6,0}, sum(Arrays.asList(new int[]{9,3}, new int[]{6,1,5}, new int[]{9,4}))));
        Assert.assertTrue(Arrays.equals( new int[]{5,9,7,0}, sum(Arrays.asList(new int[]{9,3}, new int[]{6,1,5}, new int[]{9,4}, new int[]{1,9,1}))));
//615+        94        191        112

        Assert.assertTrue(Arrays.equals( new int[]{2,1,0,1}, sum(Arrays.asList(new int[]{5,1,6}, new int[]{4,9}, new int[]{1,9,1}, new int[]{2,1,1}))));

//        Assert.assertTrue(Arrays.equals( new int[]{4,0,6,0}, sum(Arrays.asList(new int[]{9,3}, new int[]{6,1,5}, new int[]{9,4}))));
//        //Assert.assertTrue(Arrays.equals( new int[]{1,1,2}, sum(Arrays.asList(new int[]{9,3}, new int[]{6,1,5}, new int[]{9,4}, new int[]{1,9,1}))));

//        Assert.assertTrue(Arrays.equals(new int[]{2, 9, 0, 0}, sum(Arrays.asList(new int[]{0}, new int[]{1,7}, new int[]{9,1}, new int[]{-1,-6}, new int[]{1,1,2}))));





    }

    @Test
    public void checkGetValue() {
        int[] value = new int[]{1};
        Assert.assertEquals(getValue(value), getValue(getValue(getValue(value))));
        value = new int[]{1,1};
        Assert.assertEquals(getValue(value), getValue(getValue(getValue(value))));
        value = new int[]{1,1,0};
        Assert.assertEquals(getValue(value), getValue(getValue(getValue(value))));
        value = new int[]{9,9,1};
        Assert.assertEquals(getValue(value), getValue(getValue(getValue(value))));
        value = new int[]{-9};
        Assert.assertEquals(getValue(value), getValue(getValue(getValue(value))));
        value = new int[]{-9,-9};
        Assert.assertEquals(getValue(value), getValue(getValue(getValue(value))));

        value = new int[]{-9,-9,-1};
        Assert.assertEquals(getValue(value), getValue(getValue(getValue(value))));

    }
}