package dbk.formula;

import java.util.Arrays;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Math.max;

/**
 * Created by denis on 27.04.17.
 */
public class BigAbacusNumber extends Number {
    private final AbacusNumber[] digits;

    public BigAbacusNumber(AbacusNumber[] digits) {
        this.digits = digits;
    }

    public static BigAbacusNumber of(int value) {
        return new BigAbacusNumber(Math.abs(value));
    }

    public BigAbacusNumber(int value) {
        value = Math.abs(value);
        final int nums;
//        if (value > 1) {
            nums = (int) Math.ceil(Math.log10(value + 1));
//        } else {
//            nums = 1;
//        }


        digits = new AbacusNumber[nums];

        int currentValue = value;
        for (int i = 0; i < nums; i++) {
            final int value1 = currentValue % 10;
            digits[i] = new AbacusNumber(value1);
            currentValue /= 10;

        }

    }

    public BigResult add(BigAbacusNumber number) {
        //BigAbacusNumber result = new BigAbacusNumber(this.intValue() + number.intValue());

        int numOfDigits = max(number.digits.length, this.digits.length);

        AbacusNumber[] digits = new AbacusNumber[numOfDigits];
        String[] operations = new String[numOfDigits];
        boolean carry = false;
        for (int i = 0; i < numOfDigits; i++) {
            AbacusNumber a = (i < this.digits.length) ? this.digits[i]:new AbacusNumber(0);
            AbacusNumber b = (i < number.digits.length) ? number.digits[i]:new AbacusNumber(0);
            if (! carry) {
                final AbacusResult addDigitResult = a.add(b);
                carry = addDigitResult.isCarry();
                operations[i] = addDigitResult.getOperation();
                digits[i] = addDigitResult.getResult();
            }else {
                final AbacusResult bWithCarry = b.add(new AbacusNumber(1));
                final AbacusResult addB = a.add(bWithCarry.getResult());
                operations[i] = addB.getOperation();
                carry = bWithCarry.isCarry() || addB.isCarry();
                digits[i] = addB.getResult();
            }
        }
        if (carry) {
            AbacusNumber[] digitsWithCarry = new AbacusNumber[numOfDigits + 1];
            String[] operationsWithCarry = new String[numOfDigits + 1];
            System.arraycopy(digits, 0, digitsWithCarry, 0, digits.length);
            System.arraycopy(operations, 0, operationsWithCarry, 0, operations.length);

            digitsWithCarry[numOfDigits] = new AbacusNumber(1);
            operationsWithCarry[numOfDigits] = "+1";
            return new BigResult(new BigAbacusNumber(digitsWithCarry), operationsWithCarry);
        } else {
            return new BigResult(new BigAbacusNumber(digits), operations);
        }
    }

    public BigResult minus(BigAbacusNumber number) {
        int numOfDigits = this.digits.length;
        AbacusNumber[] digits = new AbacusNumber[numOfDigits];
        String[] operations = new String[numOfDigits];
        boolean carry = false;
        for (int i = 0; i < numOfDigits; i++) {
            AbacusNumber a = this.digits[i];
            AbacusNumber b = (i < number.digits.length) ? number.digits[i]:new AbacusNumber(0);

            if (carry) {
                final AbacusResult bWithCarry = b.add(new AbacusNumber(1));
                if (!bWithCarry.isCarry()) {
                    final AbacusResult minusB = a.minus(bWithCarry.getResult());
                    carry = minusB.isCarry();
                    operations[i] = minusB.getOperation();
                    digits[i] = minusB.getResult();
                } else {

                    operations[i] = "";
                    digits[i] = a;
                    carry = true;
                }
            } else {
                final AbacusResult minusB = a.minus(b);
                carry = minusB.isCarry();
                operations[i] = minusB.getOperation();
                digits[i] = minusB.getResult();
            }
        }
        //if (AbacusNumber.ONES.ONE.equals(digits[numOfDigits - 1].getOnes()) && {digits[numOfDigits -1].isFive())
        return new BigResult(new BigAbacusNumber(digits), operations);
    }

    @Override
    public int intValue() {

        return (int) longValue();



    }



    @Override
    public long longValue() {
        return IntStream.range(0, digits.length)
                .mapToObj(i-> (i))
                .collect(Collectors.summarizingInt(i -> digits[i].intValue()* (int) Math.pow(10, i))).getSum();
    }

    @Override
    public float floatValue() {
        return longValue();
    }

    @Override
    public double doubleValue() {
        return longValue();
    }

    @Override
    public String toString() {
        return "{" +
                "digits=" + Arrays.toString(digits) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BigAbacusNumber that = (BigAbacusNumber) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(digits, that.digits);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(digits);
    }

    public BigResult add(int arg) {
        return add(of(arg));
    }

    public BigResult minus(Integer arg) {
        return minus(of(arg));
    }
}
