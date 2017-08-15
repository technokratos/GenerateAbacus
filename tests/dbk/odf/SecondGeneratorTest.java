package dbk.odf;

import com.google.common.collect.ComparisonChain;
import dbk.abacus.Lesson;
import dbk.abacus.Tuple2;
import dbk.formula.AbacusNumber;
import dbk.formula.BigAbacusNumber;
import dbk.formula.BigResult;
import dbk.formula.Formulas;
import dbk.rand.RandomLevel;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created by dbk on 13-Sep-16.
 */
public class SecondGeneratorTest {


    @Test
    public void testCommonGeneration() {
        RandomLevel.setR(1);
        Random r = RandomLevel.getR();
        final Formulas formula = Formulas.getInstance();
        final TreeSet<String> formulas = Formulas.getFormulaTable().getFormulas();
        final int maxSize = formulas.size();
        int initSize = 10;
        List<String> failFormulas = new ArrayList<>();

        for (int size = initSize; size < maxSize; size+=2) {
            List<String> workFormulas = new ArrayList<>(formulas.size());

            Lesson lesson = getLesson(10);

            formulas.stream()
                    .limit(size)
                    .peek(workFormulas::add)
                    .flatMap(s -> formula.getOperandsForFormula(s).stream())
                    .forEach(t -> {
                        lesson.put(t.getA(), t.getB());
                    });

            formulas.stream()
                    .skip(size)
                    .limit(2)
                    .peek(workFormulas::add)
                    .flatMap(s -> formula.getOperandsForFormula(s).stream())
                    .forEach(t -> {
                        lesson.put(t.getA(), t.getB(), true);
                    });

            SecondGenerator generator = new SecondGenerator(Collections.singletonList(lesson));
            List<Tuple2<Lesson, List<List<List<Integer>>>>> data = generator.generate();

            data.iterator().next().getB().forEach(
                    series -> series.forEach( steps -> {
                        BigAbacusNumber  currentNumber = new BigAbacusNumber(steps.iterator().next());
                        BigResult bigResult = null;
                        int result = steps.iterator().next();
                        for (int step = 1; step < steps.size() - 1;step++) {
                            final Integer nextNumber = steps.get(step);
                            bigResult = (nextNumber > 0)? currentNumber.add(nextNumber) : currentNumber.minus(nextNumber);
                            BigAbacusNumber prevSum = currentNumber;
                            currentNumber = bigResult.getResult();
                            result += nextNumber;
                            assertThat(bigResult.getResult().intValue())
                                    .withFailMessage("error result: %s, expected: %d, " +
                                                    "prevSum : %s, " +
                                                    "nextNumber : %d, " +
                                                    "step : %d, " +
                                                    "size : %d ",
                                            bigResult.toString(), result, prevSum.toString(), nextNumber, step, workFormulas.size())
                                    .isEqualTo(result);
                            if (Stream.of(bigResult.getOperations()).allMatch(""::equals)) {
                                Assertions.fail("empty operations{}", Arrays.toString(bigResult.getOperations()));
                            }
                            final List<String> operations = Stream.of(bigResult.getOperations()).filter(s -> !"".equals(s)).collect(Collectors.toList());

                            if (!workFormulas.containsAll(operations)) {
                                failFormulas.add(String.format("error operation for result: %s, " +
                                                "next step : %d, " +
                                                "prevSum: %d, " +
                                                "step : %d, " +
                                                "size : %d ",
                                        bigResult.toString(), nextNumber, prevSum.longValue(), step, workFormulas.size()));

                            }

                        }
                        assertThat((int) bigResult.getResult().longValue()).isEqualTo(steps.get(steps.size() -1));

                    }));


            //System.out.println(data);
        }
        Assertions.assertThat(failFormulas).isEmpty();
        //System.out.println(failFormulas);
    }

    /*
    {result={digits=[[0 + 4], [0 + 1], [0 + 1]]}, operations=[+10-5, +10-5+1, +1]}, next step : 55, prevSum: 59, step : 4, size : 28 ",
     */

    @Test
    public void testDirectGeneration() {
        RandomLevel.setR(1);
        Random r = RandomLevel.getR();
        final Formulas formula = Formulas.getInstance();
        final TreeSet<String> formulas = Formulas.getFormulaTable().getFormulas();
        final int maxSize = formulas.size();
        int initSize = 10;
        List<String> failFormulas = new ArrayList<>();

        for (int size = initSize; size < maxSize; size+=2) {
            List<String> workFormulas = new ArrayList<>(formulas.size());

            Lesson lesson = getLesson(10);

            formulas.stream()
                    .limit(size)
                    .peek(workFormulas::add)
                    .flatMap(s -> formula.getOperandsForFormula(s).stream())
                    .forEach(t -> {
                        lesson.put(t.getA(), t.getB());
                    });


            SecondGenerator generator = new SecondGenerator(Collections.singletonList(lesson));
            List<Tuple2<Lesson, List<List<List<Integer>>>>> data = generator.generate();

            data.iterator().next().getB().forEach(
                    series -> series.forEach( steps -> {
                        BigAbacusNumber  currentNumber = new BigAbacusNumber(steps.iterator().next());
                        BigResult bigResult = null;
                        int result = steps.iterator().next();
                        for (int step = 1; step < steps.size() - 1;step++) {
                            final Integer nextNumber = steps.get(step);
                            bigResult = (nextNumber > 0)? currentNumber.add(nextNumber) : currentNumber.minus(nextNumber);
                            BigAbacusNumber prevSum = currentNumber;
                            currentNumber = bigResult.getResult();
                            result += nextNumber;
                            assertThat(bigResult.getResult().intValue())
                                    .withFailMessage("error result: %s, expected: %d, " +
                                                    "prevSum : %s, " +
                                                    "nextNumber : %d, " +
                                                    "step : %d, " +
                                                    "size : %d ",
                                            bigResult.toString(), result, prevSum.toString(), nextNumber, step, workFormulas.size())
                                    .isEqualTo(result);
                            if (Stream.of(bigResult.getOperations()).allMatch(""::equals)) {
                                Assertions.fail("empty operations{}", Arrays.toString(bigResult.getOperations()));
                            }
                            final List<String> operations = Stream.of(bigResult.getOperations()).filter(s -> !"".equals(s)).collect(Collectors.toList());

                            if (!workFormulas.containsAll(operations)) {
                                failFormulas.add(String.format("error operation for result: %s, " +
                                                "next step : %d, " +
                                                "prevSum: %d, " +
                                                "step : %d, " +
                                                "size : %d ",
                                        bigResult.toString(), nextNumber, prevSum.longValue(), step, workFormulas.size()));

                            }

                        }
                        assertThat((int) bigResult.getResult().longValue()).isEqualTo(steps.get(steps.size() -1));

                    }));


            //System.out.println(data);
        }
        Assertions.assertThat(failFormulas).isEmpty();
        //System.out.println(failFormulas);
    }

    private Lesson getLesson(int steps) {
        return new Lesson("test", Arrays.asList(new Settings(1, steps,
                    0, 0, "", "", "" ,
                    1, steps,
                    true, new TreeSet<String>(), new TreeSet<>())));
    }

}