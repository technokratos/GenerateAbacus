package dbk.formula;

/**
 * Created by denis on 27.04.17.
 */
public class AbacusNumber extends Number {

    private final boolean isFive;
    private final ONES ones;

    public AbacusNumber(int value) {
        isFive = value >= 5;
        ones = ONES.values()[value - ((isFive)?5:0)];
    }

    public AbacusNumber(boolean isFive, ONES ones) {
        this.isFive = isFive;
        this.ones = ones;
    }

    public static AbacusNumber abacus(int value) {
        return new AbacusNumber(value);
    }

    public AbacusResult add(AbacusNumber number) {
        final boolean carry;
        final String operation;
        final boolean isFive;
        final int changeOnes;

        final int ordinalSum = ones.ordinal() + number.ones.ordinal();
        if (ordinalSum < ONES.values().length) {
            changeOnes = number.ones.ordinal();
            carry = this.isFive && number.isFive;
            isFive = this.isFive ^ number.isFive;
            //operation = (carry?"+10":"") +((this.isFive && number.isFive) ? "-5" : (!this.isFive && isFive)?"+5": "")  + (changeOnes != 0? "+" + changeOnes:"");
            operation = (carry?"+10":"") +((!isFive && this.isFive) ? "-5" : (isFive && !this.isFive)? "+5":"")  + (changeOnes != 0? "+" + changeOnes:"");
            //return carry;
        } else {
            changeOnes = - (5 - number.ones.ordinal());
            //carryFive + isFive + number.isFive =  newFive /carry
            //true + true + true                = true  /true
            //true + true + false               = false /true
            //true + false + true               = false /true
            //true + false + false              = true  /false
            carry = this.isFive || number.isFive;
            isFive = this.isFive == number.isFive;
            String addFive = ((!isFive && this.isFive) ? "-5" : (isFive && !this.isFive)? "+5":"");
            operation = (carry?"+10":"") + addFive + changeOnes;
        }
        final ONES ones = ONES.values()[this.ones.ordinal() + changeOnes];
        return new AbacusResult(new AbacusNumber(isFive, ones), carry, operation);
    }

    public AbacusResult minus(AbacusNumber number) {
        final boolean carry;
        final String operation;
        final boolean isFive;
        final int changeOnes;


        if (this.ones.ordinal() >= number.ones.ordinal()) {
            changeOnes = -number.ones.ordinal();
            carry = !this.isFive && number.isFive;
            ////            this.isFive = carry || isFive && !number.isFive;//only for true- false
            isFive = this.isFive ^ number.isFive;
            operation = (carry?"-10":"") +((!isFive && this.isFive) ? "-5" : (isFive && !this.isFive)? "+5":"")  + (changeOnes != 0? changeOnes:"");
            //return carry;
        } else {
            //a - b ;  c; c = (5-|b|);
            //3 - 4 ;  +1
            //2 - 4 ;  +1
            //2 - 3 ;  +2;
            //1 - 4 ;  +1;
            //1 - 3 ;  +2
            //1 - 2 ;  +3
            //0 - 4 ;  +1
            //0 - 3 ;  +2;
            //0 - 2 ;  +3;
            //0 - 1 ;  +4

            changeOnes = (5 - number.ones.ordinal());

//            carry = this.isFive || number.isFive;
//            isFive = this.isFive == number.isFive;

            // isFive, number.isFive , isFive, carry
            //  0,          0,          1,      1
            //  1,          0,          0,      0
            //  0,          1,          0,      1
            //  1,          1,          1,      1

            carry = !(this.isFive && !number.isFive);
            isFive =  (this. isFive == number.isFive);
            String addFive = ((!isFive && this.isFive) ? "-5" : (isFive && !this.isFive)? "+5":"");
            operation = (carry?"-10":"") + addFive + "+" + changeOnes;
        }
        final ONES ones = ONES.values()[this.ones.ordinal() + changeOnes];
        return new AbacusResult(new AbacusNumber(isFive, ones), carry, operation);
    }

//    public boolean minus(AbacusNumber number) {
//        final int ordinalMinus = ones.ordinal() - number.ones.ordinal();
//        if(ordinalMinus >=0) {
//            ones = ONES.values()[ordinalMinus];
//            boolean carry = !isFive && number.isFive;//carry for false - true
//            this.isFive = carry || isFive && !number.isFive;//only for true- false
//            return carry;
//        } else {
//            this.ones = ONES.values()[ordinalMinus + 5];
//            //isFive, number.isFive , isFive, carry
//            //  0,          0,          1,      1
//            //  1,          0,          0,      0
//            //  0,          1,          0,      1
//            //  1,          1,          1,      1
//            boolean carry = !(isFive && !number.isFive);
//            this.isFive =  (isFive == number.isFive);
//            return carry;
//        }
//    }
    public String getFormula(AbacusNumber number, boolean carry, boolean sign) {
        //final boolean add = add(number);
        if (carry && (sign && this.intValue() < number.intValue() || !sign && this.intValue() > number.intValue() )) {
            throw new IllegalArgumentException("Distance betwee numbers " +
                    this + " and " + number +" with carry more then 10, with sign " + (sign?"+":"-") );
        }

        if (sign) {
            String result = carry ? "+" + "10" : "";
            result += (!this.isFive && number.isFive) ? "+5" : "";
            result += (this.isFive && !number.isFive) ? "-5" : "";
            result += (number.ones.ordinal() > this.ones.ordinal()) ? "+" : "";
            result += (number.ones.ordinal() != this.ones.ordinal()) ? (number.ones.ordinal() - this.ones.ordinal()) : "";
            return result;
        } else {
            String result = carry ? "-" + "10" : "";
            result += (!this.isFive && number.isFive) ? "+5" : "";
            result += (this.isFive && !number.isFive) ? "-5" : "";
            result += (number.ones.ordinal() > this.ones.ordinal()) ? "+" : "";
            result += (number.ones.ordinal() != this.ones.ordinal()) ? (number.ones.ordinal() - this.ones.ordinal()) : "";
            return result;
        }
    }


    @Override
    public int intValue() {
        return (isFive?5:0) + ones.ordinal();
    }

    @Override
    public long longValue() {
        return (isFive?5:0) + ones.ordinal();
    }

    @Override
    public float floatValue() {
        return (isFive?5:0) + ones.ordinal();
    }

    @Override
    public double doubleValue() {
        return (isFive?5:0) + ones.ordinal();
    }

    enum ONES{
        ZERO, ONE, TWO, THRE, FOUR;
    }

    @Override
    public String toString() {
        return "[" + (isFive? "5 + " + ones.ordinal() : "0 + " + ones.ordinal()) + "]";
    }
}
