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

import static dbk.generator.SecondGeneratorDirectTest.getLesson;
import static dbk.generator.SecondGeneratorDirectTest.initWorkFormulas;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by dbk on 30.08.2016.
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


        Map<String, Count> countFormulas = new TreeMap<>(new Formulas.FormulaComparator());
        int amountOfObligatoryFormulas = 2;
        for (int size = initSize; size < maxSize; size += amountOfObligatoryFormulas) {

            Lesson lesson = getLesson(5, amountOfObligatoryFormulas * 4, 1);

            List<String> workFormulas = initWorkFormulas(formulas, lesson, size);

            List<String> obligatoryFormulas = formulas.stream()
                    .skip(size)
                    .limit(amountOfObligatoryFormulas)
                    .collect(Collectors.toList());

            obligatoryFormulas.stream()
                    .peek(workFormulas::add)
                    .flatMap(s -> formula.getOperandsForFormula(s).stream())
                    .forEach(t -> {
                        lesson.put(t.getA(), t.getB(), true);
                    });

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

                            if (!workFormulas.containsAll(operations)) {
                                failFormulas.add(String.format("error operation for result: %s, " +
                                                "next step : %d, " +
                                                "prevSum: %d, " +
                                                "step : %d, " +
                                                "size : %d ",
                                        bigResult.toString(), nextNumber, prevSum.longValue(), step, workFormulas.size()));

                            }
                            operations.forEach(f -> countFormulas.computeIfAbsent(f, s -> new Count()).inc());

                        }
                        assertThat((int) bigResult.getResult().longValue()).isEqualTo(steps.get(steps.size() - 1));

                    }));
            assertThat(countFormulas.keySet()).containsAll(obligatoryFormulas);
        }
        Assertions.assertThat(failFormulas).isEmpty();
        assertThat(countFormulas.keySet()).containsAll(formulas);

    }


    /**
     * Expecting empty but was:<["error operation for result: {result={maxDigits=[[0 + 3], [0 + 3]]}, operations=[-5, -5+2]}, next step : -35, prevSum: 68, step : 4, size : 12 ",
     */
    @Ignore
    @Test
    public void testNextStep() {


        RandomLevel.setR(162531289283012L);
        final Formulas formula = Formulas.getInstance();
        final TreeSet<String> formulas = Formulas.getFormulaTable().getFormulas();

        Lesson lesson = getLesson(10, 1, 1);

        long size = 12;
        List<String> workFormulas = initWorkFormulas(formulas, lesson, size);
        formulas.stream()
                .skip(size)
                .limit(2)
                .peek(workFormulas::add)
                .flatMap(s -> formula.getOperandsForFormula(s).stream())
                .forEach(t -> {
                    lesson.put(t.getA(), t.getB(), true);
                });


        final Step prevSumArray = Step.of(new int[]{9, 5});
        RandomLevel.setR(162531289283012L);
        final Step nextStep = SecondGenerator.generateDirectStep(lesson,
                lesson.getSettings().iterator().next(),
                Arrays.asList(prevSumArray),
                2);
        final int value = nextStep.getValue();
        final int prevSum = prevSumArray.getValue();
        final BigResult bigResult = BigAbacusNumber.of(prevSum).add(value);

        assertThat(bigResult.getResult().intValue()).isEqualTo(prevSum + value);
        assertThat(workFormulas).containsAll(Arrays.asList(bigResult.getOperations()));

    }


    @Test
    public void testSingleGenerateBackStepsToResult() {
        RandomLevel.setR(1);
        final Formulas formula = Formulas.getInstance();
        final TreeSet<String> formulas = Formulas.getFormulaTable().getFormulas();

        Lesson lesson = getLesson(10, 1, 4);


        List<String> workFormulas = initWorkFormulas(formulas, lesson, 30);
        formulas.stream()
                .peek(workFormulas::add)
                .flatMap(s -> formula.getOperandsForFormula(s).stream())
                .forEach(t -> {
                    lesson.put(t.getA(), t.getB(), true);
                });

        List<String> failFormulas = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            final Step expected = Step.of(new int[]{i, i, i});
            final List<Step> arSteps = SecondGenerator.generateBackStepsToResult(lesson, 10, expected);


            final List<Integer> steps = arSteps.stream().map(Step::getValue).collect(Collectors.toList());

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

                if (!workFormulas.containsAll(operations)) {
                    failFormulas.add(String.format("error operation for result: %s, " +
                                    "next step : %d, " +
                                    "prevSum: %d, " +
                                    "step : %d, " +
                                    "size : %d ",
                            bigResult.toString(), nextNumber, prevSum.longValue(), step, workFormulas.size()));

                }
                //       operations.forEach(f -> countFormulas.computeIfAbsent(f, s -> new Count().inc()));

            }
//            assertThat((int) bigResult.getResult().longValue()).isEqualTo(steps.get(steps.size() - 1));

            //assertThat(Digs.getValue(Digs.sumSimple(arSteps))).isEqualTo(Digs.getValue(expected));
            assertThat(Step.sum(arSteps).getValue()).isEqualTo(expected.getValue());

        }
        ;


    }


    @Test
    public void testGenerateBackStepsToResult() {
        RandomLevel.setR(1);
        final Formulas formula = Formulas.getInstance();
        final TreeSet<String> formulas = Formulas.getFormulaTable().getFormulas();

        Lesson lesson = getLesson(10, 1, 1);

        long size = 52;
        List<String> workFormulas = initWorkFormulas(formulas, lesson, size);
        formulas.stream()
                .skip(size)
                .limit(2)
                .peek(workFormulas::add)
                .flatMap(s -> formula.getOperandsForFormula(s).stream())
                .forEach(t -> {
                    lesson.put(t.getA(), t.getB(), true);
                });


        //for (int i = 0; i < 10; i++) {
        final Step expected = Step.of(new int[]{1, 0});
        final List<Step> steps = SecondGenerator.generateBackStepsToResult(lesson, 5, expected);

        assertThat(Step.sum(steps).getValue()).isEqualTo(expected.getValue());
        //}


    }


}