package dbk.formula.generation;

import dbk.abacus.Tuple2;
import dbk.formula.AbacusNumber;
import dbk.formula.BigAbacusNumber;
import dbk.formula.Formulas;
import dbk.rand.RandomLevel;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by denis on 02.05.17.
 */
public class GeneratorSteps {


    private final int digits;
    private final int stepsAmount;
    private Random r = RandomLevel.getR();


    private final TreeSet<String> formulas;
    private Formulas F;

    public GeneratorSteps(TreeSet<String> formulas, int digits, int stepsAmount) {
        this.formulas = formulas;
        this.digits = digits;
        this.stepsAmount = stepsAmount;
    }

    List<BigAbacusNumber> generate(){
        BigAbacusNumber firstNumber = getFirstNumber();
        LinkedList<BigAbacusNumber> steps = new LinkedList<>();
        steps.add(firstNumber);
        for (int index = 1; index < stepsAmount; index++) {
            BigAbacusNumber nextNumber = getNextStep(firstNumber);

        }

        return generateDirect(steps);

    }

    private BigAbacusNumber getNextStep(BigAbacusNumber currentResult) {

        AbacusNumber[] numbers = new AbacusNumber[digits];
        boolean carry = false;
        boolean positive = true;
        for (int digit = 0 ; digit < digits; digit++) {
            final AbacusNumber currentNumber = currentResult.get(digit);
            F = Formulas.getInstance();

            //todo carry need use to restrict



            final TreeSet<String> posFormulas = F.getNumberPositiveFormulas().get(currentNumber);
            final TreeSet<String> negFormulas = F.getNumberNegativeFormulas().get(currentNumber);

            final boolean positveMatch = posFormulas.stream().anyMatch(p -> formulas.stream().anyMatch(f -> f.equals(p)));
            final boolean negativeMatch = posFormulas.stream().anyMatch(p -> formulas.stream().anyMatch(f -> f.equals(p)));

            final List<String> filtered = formulas.stream().filter(f -> posFormulas.contains(f) || negFormulas.contains(f)).collect(Collectors.toList());
            final String formula = filtered.get(r.nextInt(filtered.size() - 1));
            //F.getCurrentNumberToNext().get(Tuple2.of())

        }
        return null;
    }

    private List<BigAbacusNumber> generateDirect(List<BigAbacusNumber> steps) {

        return null;
    }

    private BigAbacusNumber getFirstNumber() {
        //todo use available formulas
        BigAbacusNumber bigAbacusNumber;
        do {
            bigAbacusNumber = new BigAbacusNumber(Stream.generate(() -> AbacusNumber.of(r.nextInt(10)))
                    //.filter(n-> F.getFormulasForOperand(n.intValue()).stream().anyMatch(f-> formulas.))
                    .limit(digits)
                    .toArray(AbacusNumber[]::new));
        } while (bigAbacusNumber.intValue() == 0);
        return bigAbacusNumber;
    }

}
