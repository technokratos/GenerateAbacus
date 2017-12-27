package dbk.generator;

import dbk.abacus.Lesson;
import dbk.abacus.Tuple2;
import dbk.rand.RandomLevel;

import java.util.*;
import java.util.stream.Collectors;

import static dbk.generator.Digs.*;

/**
 * Created by dbk on 13-Sep-16.
 */
public class ProbabilisticsGenerator {

    private Random r = RandomLevel.getR();

    public static final int ATTEMPT_LIMIT = 10;
    private final List<Lesson> lessons;

    public ProbabilisticsGenerator(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    public List<Tuple2<Lesson, List<List<List<Integer>>>>> generate() {

        List<Tuple2<Lesson, List<List<List<Integer>>>>> exercises = new ArrayList<>();
        for (Lesson lesson : lessons) {

            List<Integer> firstNumbers = new ArrayList<>(lesson.getKeyNumbers());
            int indexOf = firstNumbers.indexOf(0);
            if (indexOf >= 0) {
                firstNumbers.remove(indexOf);
            }
            if (firstNumbers.isEmpty()) {
                continue;
            }
            List<List<List<Integer>>> levelList = new ArrayList<>(lesson.getSettings().size());
            exercises.add(new Tuple2<>(lesson, levelList));
            int countSettings = 0;
            System.out.println(lesson.getTitle());

            for (Settings currentSettings : lesson.getSettings()) {
                countSettings++;
                int seriesCount = currentSettings.getSeries();


                List<List<Integer>> series = new ArrayList<>(seriesCount);
                levelList.add(series);

                int blockedSeries = r.nextInt(seriesCount);

                System.out.println("blocked series " + blockedSeries);
                for (int seriesIndex = 0; seriesIndex < seriesCount; seriesIndex++) {
                    if (seriesIndex == blockedSeries) {
                        addBlocked(lesson);
                    }
                    List<Integer> steps = generateCommonSteps(lesson, currentSettings);

                    if (seriesIndex == blockedSeries) {
                        removeBlocked(lesson);
                    }
                    steps=addDuplicate(steps, currentSettings);
                    steps=addDecimal(steps, currentSettings);
                    series.add(steps);
                    boolean invalid = lesson.getMarker().mark(steps, true);
                    if (invalid) {
                        System.out.println("Invalid steps. Lesson " + lesson.getTitle() + " settings " + countSettings
                        + " series " + seriesIndex + " steps " + steps);
                    }
                }
            }
        }
        return exercises;
    }

    private void removeBlocked(Lesson lesson) {
        lesson.getBlockedMap().entrySet().forEach(e -> lesson.remove(e.getKey(), e.getValue()));
    }

    private void addBlocked(Lesson lesson) {
        lesson.getBlockedMap().entrySet().forEach(e -> lesson.put(e.getKey(), e.getValue(), true));

    }

    private List<Integer> addDecimal(List<Integer> steps, Settings currentSettings) {
        final int dec = currentSettings.getDecimals();
        int mux = 1;
        for (int i = 0; i < dec; i++) {
            mux *= 10;
        }
        final int finalMux = mux;
        return steps.stream().map(s-> s* finalMux).collect(Collectors.toList());
    }

    private List<Integer> addDuplicate(List<Integer> steps, Settings currentSettings) {
        final int dup = currentSettings.getDuplicate();
        int mux = 1;
        for (int i = 0; i < dup; i++) {
            mux *= 10 +1;
        }
        final int finalMux = mux;
        return steps.stream().map(s-> s* finalMux).collect(Collectors.toList());
    }

    private List<Integer> generateCommonSteps(Lesson lesson, Settings currentSettings) {
        int[] digits = getDigitsForSteps(currentSettings);
        List<int[]> steps = new ArrayList<>(currentSettings.steps + 1);
        generateDirectStepsWithLimit(lesson, currentSettings, digits, steps);
        steps.add(Digs.sumSimple(steps));
        return Digs.getValue(steps);
    }

    private void generateDirectStepsWithLimit(Lesson lesson, Settings currentSettings, int[] digits, List<int[]> steps) {
        int stepCount = steps.size();
        int attemptCount = 0;
        while (stepCount < currentSettings.getSteps() && attemptCount < ATTEMPT_LIMIT) {
            //for (int i = 0; i < currentSettings.getSteps(); i++) {
            int[] step = generateDirectStep(lesson, currentSettings, steps, digits[stepCount]);
            if (getValue(step) != 0) {
                steps.add(step);
                stepCount++;
            } else {
                attemptCount++;
            }
            if (attemptCount >= ATTEMPT_LIMIT) {
                System.out.println("generation step exceed limit, steps " + getValue(steps) + ", last step " + step);
                break;
            }
        }
    }

    static int[] generateDirectStep(Lesson lesson, Settings currentSettings, List<int[]> steps, int digits) {
        int step[] = new int[1 + currentSettings.getExtensionDigit()];


        final int[] sum;
        int[] extSum = (steps.isEmpty()) ? new int[step.length] : Digs.sumSimple(steps);
        if (digits >= extSum.length) {
            sum = new int[digits];
            System.arraycopy(extSum,0, sum, 0, extSum.length );
        } else {
            sum = extSum;
        }
        boolean sign = RandomLevel.getR().nextBoolean();

        int excludeZero = (steps.size() < currentSettings.getSteps()) ? 1 : 0;
        List<Integer> negative = lesson.getNegative(sum[0]);

        if (!sign && !possibleNegativeCarry(sum, 1) && negative != null) {
            negative = negative.stream().filter(n -> (sum[0] + n) >= 0 + excludeZero).collect(Collectors.toList());
            if (negative.isEmpty()) {
                sign = true;
            }
        } else if (negative == null) {
            sign = true;
        }

        List<Integer> positive = lesson.getPositive(sum[0]);

        if (sign && !possiblePositiveCarry(sum, 1)) {
            positive = positive.stream().filter(n -> (sum[0] + n) <= 9).collect(Collectors.toList());
            if (positive.isEmpty()) {
                sign = false;
                if (!possibleNegativeCarry(sum, 1) && negative != null) {
                    negative = negative.stream().filter(n -> (sum[0] + n) >= 0 + excludeZero).collect(Collectors.toList());
                    if (negative.isEmpty()) {
                        System.out.println("Error impossible find next negative and positive for " + Arrays.toString(sum));
                        return new int[digits];
                    }
                } else if (negative == null) {
                    System.out.println("Error impossible find next negative and positive for " + Arrays.toString(sum));
                    return new int[digits];
                }
            }
        }


        return getStep(lesson, digits, step, sum, sign, excludeZero);
    }

    static int[] getStep(Lesson lesson, int digits, int[] step, int[] sum, boolean sign, int excludeZero) {
        int currentSum = 0;
        int currentCarry = 0;
        for (int i = 0; i < digits; i++) {

            int prevSum = sum[i] ;//todo remove carry

            List<Integer> numbers;
            if (sign) {
                final int carry = currentCarry;
                final List<Integer> positive = lesson.getPositive(prevSum);
                numbers = positive.stream().filter(n-> n - carry >= 0).map(n -> n - carry).collect(Collectors.toList());
            } else {

                final int carry = currentCarry;
                numbers = lesson.getNegative(prevSum).stream().filter(n-> n - carry <= 0).map(n -> n - carry).collect(Collectors.toList());
            }
            //numbers = (numbers == null) ? Collections.emptyList() : numbers;
            if (sign) {
                if (!possiblePositiveCarry(sum, i + 1)) {
                    numbers = numbers.stream()
                            .filter(n -> (prevSum + n) <= 9)
                            .collect(Collectors.toList());
                }
            } else {
                if (!possibleNegativeCarry(sum, i + 1)) {
                    numbers = numbers.stream()
                            .filter(n -> (prevSum + n) >= 0 + excludeZero)
                            .collect(Collectors.toList());
                }
            }

            int value;

            if (!numbers.isEmpty()) {
                value = getRandom(numbers);
            } else {
                value = 0;
            }

            currentSum = prevSum + value + currentCarry;
            currentCarry = (currentSum >= 10) ? 1 : (currentSum < 0) ? -1 : 0;
            step[i] = value;
        }
        return step;
    }

    private int[] getDigitsForSteps(Settings settings) {
        int[] digits = new int[settings.getSteps()];

        int extCount = settings.getExtensionCount();
        int steps = settings.steps;
        Set<Integer> positions = new HashSet<>();
        if (extCount == steps) {
            for (int i = 0; i < steps; i++) {
                digits[i] = 1 + settings.getExtensionDigit();
            }
        } else {
            for (int i = 0; i < extCount; i++) {
                int attempts = 0;
                while (attempts < ATTEMPT_LIMIT) {
                    int position = r.nextInt(steps);
                    if (!positions.contains(position)) {
                        positions.add(position);
                        break;
                    } else {
                        attempts++;
                    }
                }
            }
            for (int i = 0; i < steps; i++) {
                digits[i] = 1 + (positions.contains(i) ? settings.getExtensionDigit() : 0);
            }
        }
        return digits;
    }

    private static Integer getRandom(List<Integer> list) {
        int index = RandomLevel.getR().nextInt(list.size());
        return list.get(index);
    }
}
