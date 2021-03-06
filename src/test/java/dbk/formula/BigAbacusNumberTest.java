package dbk.formula;

import org.hamcrest.core.Is;
import org.junit.Test;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;


/**
 * Created by denis on 09.05.17.
 */
public class BigAbacusNumberTest {


    @Test
    public void constructorTest() {
        for (int i = 0; i < 10000; i++) {
            final int value = i;
            BigAbacusNumber bigAbacusNumber = new BigAbacusNumber(value);
            final int actual = bigAbacusNumber.intValue();
            assertThat(actual, Is.is(value));
        }
    }
    
    @Test
    public void addTest() {

//        final BigResult simple = new BigAbacusNumber(111)
//                .add(new BigAbacusNumber(222));
//
//        assertThat(simple.getResult(), Is.is(new BigAbacusNumber(333)));
//        assertThat(simple.getOperations(), Is.is(new String[]{"+2","+2","+2"}));
//
//
//        final BigResult overFive = new BigAbacusNumber(22)
//                .add(new BigAbacusNumber(33));
//
//        assertThat(overFive.getResult(), Is.is(new BigAbacusNumber(55)));
//        assertThat(overFive.getOperations(), Is.is(new String[]{"+5-2","+5-2"}));

        final BigResult overTen = new BigAbacusNumber(22)
                .add(new BigAbacusNumber(99));

        assertThat(overTen.getResult(), Is.is(new BigAbacusNumber(121)));
        assertThat(overTen.getOperations(), Is.is(new String[]{"+10-1", "", "+1"}));

        final BigResult overThousend = new BigAbacusNumber(999)
                .add(new BigAbacusNumber(2));

        assertThat(overThousend.getResult(), Is.is(new BigAbacusNumber(1001)));
        assertThat(overThousend.getOperations(), Is.is(new String[]{"+10-5-3", "+10-5-4", "+10-5-4", "+1"}));
        

    }

    @Test
    public void minusTest() {

        final BigResult simple = new BigAbacusNumber(222)
                .minus(new BigAbacusNumber(111));

        assertThat(simple.getResult(), Is.is(new BigAbacusNumber(111)));
        assertThat(simple.getOperations(), Is.is(new String[]{"-1","-1","-1"}));


        final BigResult overFive = new BigAbacusNumber(50)
                .minus(new BigAbacusNumber(33));

        assertThat(overFive.getResult(), Is.is(new BigAbacusNumber(17)));
        assertThat(overFive.getOperations(), Is.is(new String[]{"-10+5+2", "-5+1"}));

        final BigResult overTen = new BigAbacusNumber(100)
                .minus(new BigAbacusNumber(29));

        assertThat(overTen.getResult().intValue(), Is.is(new BigAbacusNumber(71).intValue()));
        assertThat(overTen.getOperations(), Is.is(new String[]{"-10+1", "-10+5+2", "-1"}));





    }

    @Test
    public void testMinus120_92() {

        //{result={maxDigits=[[5 + 3], [0 + 2], [0 + 1]]}, operations=[-10+5+3, , ]}, expected: 28, prevSum : {maxDigits=[[0 + 0], [0 + 2], [0 + 1]]}, nextNumber : -92, step : 7, size : 48
        //{result={digits=[[5 + 3], [0 + 2], [0 + 1]]}, operations=[-10+5+3, , ]}, expected: 28, next step : -92
    final BigResult overTen = new BigAbacusNumber(120)
                .minus(new BigAbacusNumber(92));

        assertThat(overTen.getResult().intValue(), Is.is(new BigAbacusNumber(28).intValue()));
        assertThat(overTen.getOperations(), Is.is(new String[]{"-10+5+3", "",  "-1"}));
    }


    @Test
    public void testAdd28_92() {

        //{result={maxDigits=[[5 + 3], [0 + 2], [0 + 1]]}, operations=[-10+5+3, , ]}, expected: 28, prevSum : {maxDigits=[[0 + 0], [0 + 2], [0 + 1]]}, nextNumber : -92, step : 7, size : 48
        //{result={digits=[[5 + 3], [0 + 2], [0 + 1]]}, operations=[-10+5+3, , ]}, expected: 28, next step : -92
        final BigResult overTen = new BigAbacusNumber(28)
                .add(new BigAbacusNumber(92));

        assertThat(overTen.getResult().intValue(), Is.is(new BigAbacusNumber(120).intValue()));
        assertThat(overTen.getOperations(), Is.is(new String[]{"+10-5-3", "",  "+1"}));
    }

    @Test
    public void testAdd59_55() {

        final BigResult overTen = new BigAbacusNumber(59)
                .add(new BigAbacusNumber(55));

        assertThat(overTen.getResult().intValue(), Is.is(new BigAbacusNumber(114).intValue()));
        assertThat(overTen.getOperations(), Is.is(new String[]{"+10-5", "+10-5+1",  "+1"}));
    }


}