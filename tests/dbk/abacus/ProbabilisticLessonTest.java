package dbk.abacus;

import dbk.formula.Formulas;
import dbk.odf.Settings;
import dbk.rand.RandomLevel;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Created by denis on 15.09.17.
 */
public class ProbabilisticLessonTest {

    @Before
    public void setUp(){
        RandomLevel.setR(1);
    }
    @Test
    public void generateStep() throws Exception {

        final Formulas formula = Formulas.getInstance();
        final TreeSet<String> formulas = Formulas.getFormulaTable().getFormulas();
        final int maxSize = 40;
        final List<String> work = formulas.stream().limit(maxSize).collect(Collectors.toList());
        final List<String> obligatory = formulas.stream().skip(maxSize).limit(2).collect(Collectors.toList());
        final ProbabilisticLesson lesson = new ProbabilisticLesson(Arrays.asList(getSettings(10, 1, 1)), work, obligatory);
        lesson.startSeries();
        final List<int[]> steps = lesson.generateStep();
        steps.forEach(s-> {
            Arrays.stream(s).forEach(d-> System.out.print(d + ","));
            System.out.println();
        });


    }


    @Test
    public void pairToStepTest() {
        Pair[] pairs = new Pair[] {new Pair(3, 7, 0),
                new Pair(4, 8, 0),
                new Pair(2, 1, 0)};//impossible 0

        //243 , 87 -> 243 +  077
        assertArrayEquals(ProbabilisticLesson.pairToStep(pairs), new int[]{7, 7, 0});

        /*
         *   1 9 9
         * + 1 1 1    -> 9? 0 1
         */
        Pair[] pairs2 = new Pair[] {new Pair(9, 1, 0),
                new Pair(9, 1, 0),
                new Pair(1, 1, 0)};//impossible 0


        final int[] step2 = ProbabilisticLesson.pairToStep(pairs2);
        assertArrayEquals(step2, new int[]{1, 0, 0});
    }

    @Test
    public void pairToStepTest49_89() {
        Pair[] pairs = new Pair[] {new Pair(9, 9, 0),
                new Pair(4, 8, 0)};//impossible 0

        final int[] expecteds = ProbabilisticLesson.pairToStep(pairs);
        assertArrayEquals(expecteds, new int[]{9, 7});

    }

    @Test
    public void pairToStepTest16__16() {
        Pair[] pairs = new Pair[] {new Pair(6, -6, 0),
                new Pair(1, -1, 0)};//impossible 0

        final int[] expecteds = ProbabilisticLesson.pairToStep(pairs);
        assertArrayEquals(expecteds, new int[]{-6, -1});

    }

    @Test
    public void pairToStepTest16__7() {
        Pair[] pairs = new Pair[] {new Pair(6, -7, 0)};//impossible 0

        final int[] expecteds = ProbabilisticLesson.pairToStep(pairs);
        assertArrayEquals(expecteds, new int[]{-7, -0 });

    }




    @Test
    public void pairToStepNegativeTest() {
        Pair[] pairs = new Pair[] {new Pair(3, -7, 0),
                new Pair(4, -6, 0),
                new Pair(2, -1, 0)};//impossible 0 for carry

        //243 and -167 -> 243 - 57
        assertArrayEquals(ProbabilisticLesson.pairToStep(pairs), new int[]{-7,-5, 0});
    }

    private static Settings getSettings(int steps, int series, int extensionDigit) {
        return new Settings(series, steps,
                    0, 0, "", "", "",
                    extensionDigit, steps,
                    true, new TreeSet<String>(), new TreeSet<>());
    }

}