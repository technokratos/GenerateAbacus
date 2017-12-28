package dbk.generator;

import dbk.abacus.Count;
import dbk.abacus.Lesson;
import dbk.abacus.Tuple2;
import dbk.formula.BigAbacusNumber;
import dbk.formula.BigResult;
import dbk.formula.Formulas;
import dbk.generator.types.Step;
import dbk.rand.RandomLevel;
import org.assertj.core.api.Assertions;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created by dbk on 13-Sep-16.
 */
public class SecondGeneratorDirectTest {



    /*
    {result={maxDigits=[[0 + 4], [0 + 1], [0 + 1]]}, operations=[+10-5, +10-5+1, +1]}, next step : 55, prevSum: 59, step : 4, size : 28 ",
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
        Map<String, Count> countFormulas = new TreeMap<>(new Formulas.FormulaComparator());

        for (int size = initSize; size <= maxSize; size += 2) {

            Lesson lesson = getLesson(10, 20, 1);

            List<String> workFormulas =  initWorkFormulas(formulas, lesson, size);

            SecondGenerator generator = new SecondGenerator(Collections.singletonList(lesson));
            List<Tuple2<Lesson, List<List<List<Integer>>>>> data = generator.generate();

            data.iterator().next().getB().forEach(
                    series -> series.forEach(steps -> {
                        BigAbacusNumber currentNumber = new BigAbacusNumber(steps.iterator().next());
                        BigResult bigResult = null;
                        int result = steps.iterator().next();
                        for (int step = 1; step < steps.size() - 1; step++) {
                            final Integer nextNumber = steps.get(step);
                            bigResult = (nextNumber > 0) ? currentNumber.add(nextNumber) : currentNumber.minus(nextNumber);
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

                            operations.forEach(f -> countFormulas.computeIfAbsent(f, s -> new Count().inc()));

                            if (!workFormulas.containsAll(operations)) {
                                failFormulas.add(String.format("error operation for result: %s, " +
                                                "next step : %d, " +
                                                "prevSum: %d, " +
                                                "step : %d, " +
                                                "size : %d ",
                                        bigResult.toString(), nextNumber, prevSum.longValue(), step, workFormulas.size()));

                            }

                        }
                        assertThat((int) bigResult.getResult().longValue()).isEqualTo(steps.get(steps.size() - 1));

                    }));


            //System.out.println(data);
        }
        assertThat(countFormulas.keySet()).containsAll(formulas);
        Assertions.assertThat(failFormulas).isEmpty();
        //System.out.println(failFormulas);
    }

    static Lesson getLesson(int steps, int series, int extensionDigit) {
        return new Lesson("test", Arrays.asList(new Settings(series, steps,
                0, 0, "", "", "",
                extensionDigit, steps,
                true, new TreeSet<String>(), new TreeSet<>(), false, null)));
    }


    @Ignore
    @Test
    public void testGetStep() {
        //seed 170949629201965
    }

    @Test
    public void getDirectStep() {
        //Expecting empty but was:<["error operation for result: {result={maxDigits=[[0 + 4], [0 + 1], [0 + 1]]}, operations=[+10-5, +10-5+1, +1]}, next step : 55, prevSum: 59, step : 4, size : 28 ",
        //seed 162531289283012..162531289283012
        RandomLevel.setR(162531289283012L);
        final Formulas formula = Formulas.getInstance();
        final TreeSet<String> formulas = Formulas.getFormulaTable().getFormulas();

        Lesson lesson = getLesson(10, 1, 1);

        long size = 28;
        List<String> workFormulas = initWorkFormulas(formulas, lesson, size);

        final Step prevSumArray = Step.of(new int[]{9, 5});
        RandomLevel.setR(162531289283012L);
        final Step nextStep = SecondGenerator.generateDirectStep(lesson,
                lesson.getSettings().iterator().next(),
                Arrays.asList(prevSumArray),
                2);
        final int value = nextStep.getValue();// Digs.getValue(nextStep);
        final int prevSum = prevSumArray.getValue();// Digs.getValue(prevSumArray);
        final BigResult bigResult = BigAbacusNumber.of(prevSum).add(value);

        assertThat(bigResult.getResult().intValue()).isEqualTo(prevSum + value);
        assertThat(workFormulas).containsAll(Arrays.asList(bigResult.getOperations()));

    }

    static List<String> initWorkFormulas(TreeSet<String> formulas, Lesson lesson, long size) {
        final Formulas formula = Formulas.getInstance();
        List<String> workFormulas = new ArrayList<>(formulas.size());
        formulas.stream()
                .limit(size)
                .peek(workFormulas::add)
                .flatMap(s -> formula.getOperandsForFormula(s).stream())
                .forEach(t -> {
                    lesson.put(t.getA(), t.getB());
                });
        return workFormulas;
    }


}