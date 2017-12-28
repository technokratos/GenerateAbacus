package dbk.generator;

import dbk.abacus.Lesson;
import dbk.formula.BigAbacusNumber;
import dbk.formula.BigResult;
import dbk.formula.Formulas;
import dbk.rand.RandomLevel;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import static dbk.generator.SecondGeneratorDirectTest.initWorkFormulas;
import static org.assertj.core.api.Assertions.assertThat;

public class SecondGeneratorMinusTest {

    @Test
    public void getDirectStep() {
        //Expecting empty but was:<["error operation for result: {result={digits=[[0 + 4], [0 + 1], [0 + 1]]}, operations=[+10-5, +10-5+1, +1]}, next step : 55, prevSum: 59, step : 4, size : 28 ",
        //seed 162531289283012..162531289283012
        RandomLevel.setR(162531289283012L);
        final Formulas formula = Formulas.getInstance();
        final TreeSet<String> formulas = Formulas.getFormulaTable().getFormulas();

        Lesson lesson = getLesson(10, 1, 1);


    }

    static Lesson getLesson(int steps, int series, int extensionDigit) {
        Settings settings = new Settings(series, steps,
                0, 0, "", "", "",
                extensionDigit, steps,
                true, new TreeSet<String>(), new TreeSet<>(), true, null);
        return new Lesson("test", Arrays.asList(settings));
    }

}
