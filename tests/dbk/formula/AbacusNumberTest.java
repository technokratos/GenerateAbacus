package dbk.formula;

import org.junit.Test;

import static dbk.formula.AbacusNumber.abacus;
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
        assertEquals("+5-4", abacus(4).add(abacus(1)).getOperation());
        assertEquals("+5-3", abacus(4).add(abacus(2)).getOperation());
        assertEquals("+5-2", abacus(4).add(abacus(3)).getOperation());
        assertEquals("+5-1", abacus(4).add(abacus(4)).getOperation());
        assertEquals("+5", abacus(4).add(abacus(5)).getOperation());

        assertEquals("+10-4", abacus(4).add(abacus(6)).getOperation());
        assertEquals("+10-3", abacus(4).add(abacus(7)).getOperation());
        assertEquals("+10-2", abacus(4).add(abacus(8)).getOperation());
        assertEquals("+10-1", abacus(4).add(abacus(9)).getOperation());
        //empty
        assertEquals("", abacus(4).add(abacus(0)).getOperation());
        assertEquals("", abacus(4).minus(abacus(0)).getOperation());

        //minus
        assertEquals("-1", abacus(4).minus(abacus(1)).getOperation());
        assertEquals("-2", abacus(4).minus(abacus(2)).getOperation());
        assertEquals("-3", abacus(4).minus(abacus(3)).getOperation());
        assertEquals("-4", abacus(4).minus(abacus(4)).getOperation());
        assertEquals("-10+5", abacus(4).minus(abacus(5)).getOperation());
        assertEquals("-10+5-1", abacus(4).minus(abacus(6)).getOperation());
        assertEquals("-10+5-2", abacus(4).minus(abacus(7)).getOperation());
        assertEquals("-10+5-3", abacus(4).minus(abacus(8)).getOperation());
        assertEquals("-10+5-4", abacus(4).minus(abacus(9)).getOperation());
    }

    @Test
    public void getFormula4() throws Exception {
        assertEquals("+5-4", abacus(4).getFormula(abacus(5),false, true));
        assertEquals("+5-3", abacus(4).getFormula(abacus(6),false, true));
        assertEquals("+5-2", abacus(4).getFormula(abacus(7),false, true));
        assertEquals("+5-1", abacus(4).getFormula(abacus(8),false, true));
        assertEquals("+5", abacus(4).getFormula(abacus(9),false, true));

        assertEquals("+10-4", abacus(4).getFormula(abacus(0), true, true));
        assertEquals("+10-3", abacus(4).getFormula(abacus(1),true, true));
        assertEquals("+10-2", abacus(4).getFormula(abacus(2), true, true));
        assertEquals("+10-1", abacus(4).getFormula(abacus(3), true, true));
        //empty
        assertEquals("", abacus(4).getFormula(abacus(4), false, false));
        assertEquals("", abacus(4).getFormula(abacus(4), false, true));

        //minus
        assertEquals("-1", abacus(4).getFormula(abacus(3),false, false));
        assertEquals("-2", abacus(4).getFormula(abacus(2),false, false));
        assertEquals("-3", abacus(4).getFormula(abacus(1),false, false));
        assertEquals("-4", abacus(4).getFormula(abacus(0),false, false));
        assertEquals("-10+5", abacus(4).getFormula(abacus(9),true, false));
        assertEquals("-10+5-1", abacus(4).getFormula(abacus(8), true, false));
        assertEquals("-10+5-2", abacus(4).getFormula(abacus(7),true, false));
        assertEquals("-10+5-3", abacus(4).getFormula(abacus(6), true, false));
        assertEquals("-10+5-4", abacus(4).getFormula(abacus(5), true, false));
    }


    @Test
    public void getFormula4Sign() throws Exception {
        assertEquals("+5-4", abacus(4).getFormula(abacus(5),false, false));
        assertEquals("+5-3", abacus(4).getFormula(abacus(6),false, false));
        assertEquals("+5-2", abacus(4).getFormula(abacus(7),false, false));
        assertEquals("+5-1", abacus(4).getFormula(abacus(8),false, false));
        assertEquals("+5", abacus(4).getFormula(abacus(9),false, false));

        assertEquals("+10-4", abacus(4).getFormula(abacus(0), true, true));
        assertEquals("+10-3", abacus(4).getFormula(abacus(1),true, true));
        assertEquals("+10-2", abacus(4).getFormula(abacus(2), true, true));
        assertEquals("+10-1", abacus(4).getFormula(abacus(3), true, true));
        //empty
        assertEquals("", abacus(4).getFormula(abacus(4), false, false));
        assertEquals("", abacus(4).getFormula(abacus(4), false, false));

        //minus
        assertEquals("-1", abacus(4).getFormula(abacus(3),false, false));
        assertEquals("-2", abacus(4).getFormula(abacus(2),false, false));
        assertEquals("-3", abacus(4).getFormula(abacus(1),false, false));
        assertEquals("-4", abacus(4).getFormula(abacus(0),false, false));
        assertEquals("-10+5", abacus(4).getFormula(abacus(9),true, false));
        assertEquals("-10+5-1", abacus(4).getFormula(abacus(8), true, false));
        assertEquals("-10+5-2", abacus(4).getFormula(abacus(7),true, false));
        assertEquals("-10+5-3", abacus(4).getFormula(abacus(6), true, false));
        assertEquals("-10+5-4", abacus(4).getFormula(abacus(5), true, false));
    }

    @Test
    public void formula9() throws Exception {
        assertEquals("-5-3", abacus(9).getFormula(abacus(1), false, true));
        assertEquals("-5-3", abacus(9).getFormula(abacus(1), false, false));

    }

}