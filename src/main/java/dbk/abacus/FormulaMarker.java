package dbk.abacus;

import dbk.formula.BigAbacusNumber;
import dbk.formula.BigResult;

import java.util.*;
import java.util.stream.Stream;

/**
 * Created by denis on 16.05.17.
 */
public class FormulaMarker extends Marker {

    Map<String, Count> formulaCount = new HashMap<>();

    @Override
    public boolean mark(List<Integer> steps, boolean addSum) {

        final boolean result = super.mark(steps, addSum);

        BigAbacusNumber value = new BigAbacusNumber(steps.get(0));
        for (int i = 1; i < steps.size() - (addSum ? 1 : 0); i++) {

            BigAbacusNumber operand = new BigAbacusNumber(Math.abs(steps.get(i)));

            try {
                final BigResult bigResult = (steps.get(i)>0)? value.add(operand) : value.minus(operand);

                value = bigResult.getResult();
                Stream.of(bigResult.getOperations()).forEach(this::addToMap);
            } catch (Exception e) {
                System.out.println(e.getLocalizedMessage() + steps);
            }

        }

        return result;


    }

    public Map<String, Count> getFormulaCount() {
        return formulaCount;
    }

    void addToMap(String formula) {
        formulaCount.compute(formula, (s, count) -> (count != null) ? count.inc() : new Count().inc());
    }
}
