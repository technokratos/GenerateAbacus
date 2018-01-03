package dbk.generator.types;

import dbk.generator.types.Step;
import org.junit.Assert;
import org.junit.Test;

import java.util.Objects;

import static dbk.generator.types.Step.newSizeWithoutNines;
import static dbk.generator.types.Step.of;
import static java.util.Arrays.asList;

public class StepTest {

    @Test
    public void testMinus(){
        Assert.assertEquals(-1, of(1, 3).addWithLoan(new int[]{-2, -3}, 2).getValue());
        Assert.assertEquals(-21, Step.sum(asList(of(1, 1), of(-2, -3))).getValue());

        Assert.assertEquals(-1, Step.sum(asList(of(new int[]{1}), of(new int[]{-2}))).getValue());
        Assert.assertEquals(-21, of(1, 1).addWithLoan(new int[]{-2, -3}, 3).getValue());
        Assert.assertEquals(-98, of(1).addWithLoan(new int[]{-9, -9}, 2).getValue());

        Assert.assertEquals(-12, Step.sum(asList(of(7), of(-8), of(-5), of(-6))).getValue());


        Assert.assertEquals(22, Step.sum(asList(of(1, 1), of(1, 1))).getValue());

        Assert.assertEquals(10, Step.sum(asList(of(new int[]{3}), of(new int[]{7}))).getValue());
        Assert.assertEquals(132, Step.sum(asList(of(6, 6), of(6, 6))).getValue());




    }

    @Test
    public void testNewSize() {
        /*
        max digits ->2
     * -199 -> -199
     * -1999-> -199
     * -19789-> -1789
     *   */

        Assert.assertEquals(2, newSizeWithoutNines(2, of( 9, 9, -1)));
        Assert.assertEquals(2, newSizeWithoutNines(2, of(9, 9, 9, -1)));
        Assert.assertEquals(2, newSizeWithoutNines(2, of(7, 9, 9, 9, -1)));
        Assert.assertEquals(6, newSizeWithoutNines(2, of(7, 9, 9, 9, 9, 7, 9, -1)));

    }

    @Test
    public void testSum() {
//        1
//        9
//        9
//        1

        Assert.assertEquals(of(6,0,0,0), Step.sum(asList(of(9, 7, -1), of(7, 2))));
        Assert.assertEquals(6, Step.sum(asList(of(9, 7, -1), of(7, 2))).getValue());

        Assert.assertEquals(63, Step.sum(asList(of(1, 3), of(2, 3))).getValue());
        Assert.assertEquals(22, Step.sum(asList(of(1, 1), of(1, 1))).getValue());
        Assert.assertEquals(10, Step.sum(asList(of(new int[]{3}), of(new int[]{7}))).getValue());
        Assert.assertEquals(132, Step.sum(asList(of(6, 6), of(6, 6))).getValue());
        Assert.assertEquals(of(2), of(new int[]{1}).addWithLoan(new int[]{1}, 2));
        Assert.assertEquals(of(2, 0), Step.sum(asList(of(new int[]{1}), of(new int[]{1}))));
        Assert.assertEquals(of(3, 6, 0), Step.sum(asList(of(1, 3), of(2, 3))));
        Assert.assertEquals(of(2,2, 0), Step.sum(asList(of(1, 1), of(1, 1))));
        Assert.assertEquals(of(0,1 ), Step.sum(asList(of(new int[]{3}), of(new int[]{7}))));
        Assert.assertEquals(of(2, 3, 1), Step.sum(asList(of(6, 6), of(6, 6))));
        Assert.assertEquals(of(0, 0), Step.sum(asList(of(new int[]{1}), of(new int[]{-1}))));
        Assert.assertEquals(of(8, 1, 0), Step.sum(asList(of(2, 3), of(-4, -1))));
        Assert.assertEquals(of(0, 2), Step.sum(asList(of(new int[]{1}), of(new int[]{9}), of(new int[]{9}), of(new int[]{1}))));


//        93        615        94        191        112 = 10012
        //0         17        91                -16                -9808

        //93 + 615 = 708
        Assert.assertEquals( of(8,0,7, 0), Step.sum(asList(of(3,9), of(5,1,6))));
        //708+ 94 = 802
        Assert.assertEquals( of(2,0,8, 0), Step.sum(asList(of(8,0,7), of(4, 9))));
        // 802 + 191 = 1003
        Assert.assertEquals( of(3,9,9, 0), Step.sum(asList(of(2,0,8), of(1, 9, 1))));
        Assert.assertEquals( of(5,0,1,1), Step.sum(asList(of(3,9,9), of(2, 1, 1))));
        Assert.assertEquals( of(5,5,5, 0), Step.sum(asList(of(9,3), of(6,1,5))));
        Assert.assertEquals( of(4,0,6, 0), Step.sum(asList(of(9,3), of(6,1,5), of(9,4))));
        Assert.assertEquals( of(5,9,7, 0), Step.sum(asList(of(9,3), of(6,1,5), of(9,4), of(1,9,1))));
//615+        94        191        112

        Assert.assertEquals( of(2,1,0,1), Step.sum(asList(of(5,1,6), of(4,9), of(1,9,1), of(2,1,1))));

//        Assert.assertEquals( Step.of(new int[]{4,0,6,0}, sum(Objects.asList(Step.of(new int[]{9,3}, Step.of(new int[]{6,1,5}, Step.of(new int[]{9,4}))));
//        //Assert.assertEquals( Step.of(new int[]{1,1,2}, sum(Objects.asList(Step.of(new int[]{9,3}, Step.of(new int[]{6,1,5}, Step.of(new int[]{9,4}, Step.of(new int[]{1,9,1}))));

//        Assert.assertEquals(Step.of(new int[]{2, 9, 0, 0}, sum(Objects.asList(Step.of(new int[]{0}, Step.of(new int[]{1,7}, Step.of(new int[]{9,1}, Step.of(new int[]{-1,-6}, Step.of(new int[]{1,1,2}))));





    }


}