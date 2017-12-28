package dbk.formula;

import dbk.abacus.Tuple2;
import org.junit.Test;

import java.util.List;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by denis on 12.08.17.
 */
public class FormulasTest {
    @Test
    public void getOperandsForFormula() throws Exception {

        final TreeSet<String> formulas = Formulas.getFormulaTable().getFormulas();

        formulas.forEach(s-> {
            final List<Tuple2<Integer, Integer>> operandsForFormula = Formulas.getInstance().getOperandsForFormula(s);

            operandsForFormula.forEach( t-> {
                AbacusNumber a = AbacusNumber.of(t.getA());
                final boolean sign = t.getB()>=0;

                final AbacusNumber b = AbacusNumber.of((sign)?t.getB(): -t.getB());
                final AbacusResult result = sign ? a.add(b) : a.minus(b);
                assertThat(result.getOperation()).isEqualTo(s);
            });


        });
    }

    @Test
    public  void testOperands() {
        String f = "+10-5";
        final List<Tuple2<Integer, Integer>> operandsForFormula = Formulas.getInstance().getOperandsForFormula(f);


        operandsForFormula.forEach( t-> {
            AbacusNumber.of(t.getA());

        });
    }

}