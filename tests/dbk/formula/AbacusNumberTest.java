package dbk.formula;

import org.junit.Test;

import static dbk.formula.AbacusNumber.of;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertThat;


/**
 * Created by denis on 27.04.17.
 */
public class AbacusNumberTest {
    @Test
    public void add() throws Exception {
        for (int i = 0; i < 10; i++) {

            for (int j = 0; j < 10; j++) {
                final AbacusNumber second = new AbacusNumber(j);
                {
                    AbacusNumber number = new AbacusNumber(i);
                    final AbacusResult add = number.add(second);
                    final boolean carry = add.isCarry();
                    int result = i + j - (i + j >=10? 10:0);
                    final int actual = add.getResult().intValue();
                    System.out.println(number + " + " + second + " " + add.toString());
                    assertEquals("result plus " + i + " " + j, result, actual);
                    assertEquals("carry plus " + i + " " + j, i + j >= 10, carry);
                }

                {
                    AbacusNumber numberForMinus = new AbacusNumber(i);
                    int minusResult = ((i < j) ? 10 : 0) + i - j;
                    final AbacusResult minus = numberForMinus.minus(second);
                    final boolean carryMinus = minus.isCarry();
                    final int actual = minus.getResult().intValue();
                    System.out.println(numberForMinus + " - " + second + " " + minus.toString());
                    assertEquals("result minus " + i + " " + j, minusResult, actual);
                    assertEquals("carrt minus " + i + " " + j, i < j, carryMinus);
                }

            }
        }
    }

    @Test
    public void addFormula() throws Exception {
        assertEquals("+5-4", of(4).add(of(1)).getOperation());
        assertEquals("+5-3", of(4).add(of(2)).getOperation());
        assertEquals("+5-2", of(4).add(of(3)).getOperation());
        assertEquals("+5-1", of(4).add(of(4)).getOperation());
        assertEquals("+5", of(4).add(of(5)).getOperation());

        assertEquals("+10-4", of(4).add(of(6)).getOperation());
        assertEquals("+10-3", of(4).add(of(7)).getOperation());
        assertEquals("+10-2", of(4).add(of(8)).getOperation());
        assertEquals("+10-1", of(4).add(of(9)).getOperation());
        //empty
        assertEquals("", of(4).add(of(0)).getOperation());
        assertEquals("", of(4).minus(of(0)).getOperation());

        //minus
        assertEquals("-1", of(4).minus(of(1)).getOperation());
        assertEquals("-2", of(4).minus(of(2)).getOperation());
        assertEquals("-3", of(4).minus(of(3)).getOperation());
        assertEquals("-4", of(4).minus(of(4)).getOperation());
        assertEquals("-10+5", of(4).minus(of(5)).getOperation());
        assertEquals("-10+5-1", of(4).minus(of(6)).getOperation());
        assertEquals("-10+5-2", of(4).minus(of(7)).getOperation());
        assertEquals("-10+5-3", of(4).minus(of(8)).getOperation());
        assertEquals("-10+5-4", of(4).minus(of(9)).getOperation());
    }

    @Test
    public void getFormula4() throws Exception {
        assertEquals("+5-4", of(4).getFormula(of(5),false, true));
        assertEquals("+5-3", of(4).getFormula(of(6),false, true));
        assertEquals("+5-2", of(4).getFormula(of(7),false, true));
        assertEquals("+5-1", of(4).getFormula(of(8),false, true));
        assertEquals("+5", of(4).getFormula(of(9),false, true));

        assertEquals("+10-4", of(4).getFormula(of(0), true, true));
        assertEquals("+10-3", of(4).getFormula(of(1),true, true));
        assertEquals("+10-2", of(4).getFormula(of(2), true, true));
        assertEquals("+10-1", of(4).getFormula(of(3), true, true));
        //empty
        assertEquals("", of(4).getFormula(of(4), false, false));
        assertEquals("", of(4).getFormula(of(4), false, true));

        //minus
        assertEquals("-1", of(4).getFormula(of(3),false, false));
        assertEquals("-2", of(4).getFormula(of(2),false, false));
        assertEquals("-3", of(4).getFormula(of(1),false, false));
        assertEquals("-4", of(4).getFormula(of(0),false, false));
        assertEquals("-10+5", of(4).getFormula(of(9),true, false));
        assertEquals("-10+5-1", of(4).getFormula(of(8), true, false));
        assertEquals("-10+5-2", of(4).getFormula(of(7),true, false));
        assertEquals("-10+5-3", of(4).getFormula(of(6), true, false));
        assertEquals("-10+5-4", of(4).getFormula(of(5), true, false));
    }


    @Test
    public void getFormula4Sign() throws Exception {
        assertEquals("+5-4", of(4).getFormula(of(5),false, false));
        assertEquals("+5-3", of(4).getFormula(of(6),false, false));
        assertEquals("+5-2", of(4).getFormula(of(7),false, false));
        assertEquals("+5-1", of(4).getFormula(of(8),false, false));
        assertEquals("+5", of(4).getFormula(of(9),false, false));

        assertEquals("+10-4", of(4).getFormula(of(0), true, true));
        assertEquals("+10-3", of(4).getFormula(of(1),true, true));
        assertEquals("+10-2", of(4).getFormula(of(2), true, true));
        assertEquals("+10-1", of(4).getFormula(of(3), true, true));
        //empty
        assertEquals("", of(4).getFormula(of(4), false, false));
        assertEquals("", of(4).getFormula(of(4), false, false));

        //minus
        assertEquals("-1", of(4).getFormula(of(3),false, false));
        assertEquals("-2", of(4).getFormula(of(2),false, false));
        assertEquals("-3", of(4).getFormula(of(1),false, false));
        assertEquals("-4", of(4).getFormula(of(0),false, false));
        assertEquals("-10+5", of(4).getFormula(of(9),true, false));
        assertEquals("-10+5-1", of(4).getFormula(of(8), true, false));
        assertEquals("-10+5-2", of(4).getFormula(of(7),true, false));
        assertEquals("-10+5-3", of(4).getFormula(of(6), true, false));
        assertEquals("-10+5-4", of(4).getFormula(of(5), true, false));
    }

    @Test
    public void formula9() throws Exception {
        assertEquals("-5-3", of(9).getFormula(of(1), false, true));
        assertEquals("-5-3", of(9).getFormula(of(1), false, false));

    }

}