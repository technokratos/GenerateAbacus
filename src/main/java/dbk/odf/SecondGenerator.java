package dbk.odf;

import dbk.abacus.Lesson;
import dbk.abacus.Tuple2;
import dbk.rand.RandomLevel;

import java.util.*;
import java.util.stream.Collectors;

import static dbk.odf.Digs.getValue;
import static dbk.odf.Digs.possibleNegativeCarry;
import static dbk.odf.Digs.possiblePositiveCarry;
import static java.lang.Math.abs;

/**
 * Created by dbk on 13-Sep-16.
 */
public class SecondGenerator {

    private Random r = RandomLevel.getR();

    public static final int ATTEMPT_LIMIT = 10;
    private final List<Lesson> lessons;
    private boolean addSum = true;

    public SecondGenerator(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    public List<Tuple2<Lesson, List<List<List<Integer>>>>> generate() {

        //        int[][][] exercises  = new int[lessons.size()][seriesCount][stepCountWithSum];
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
                    List<Integer> steps = generateSteps(lesson, currentSettings);

                    if (seriesIndex == blockedSeries) {
                        removeBlocked(lesson);
                    }
                    steps=addDuplicate(steps, currentSettings);
                    steps=addDecimal(steps, currentSettings);
                    series.add(steps);
                    boolean invalid = lesson.getMarker().mark(steps, addSum);
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

    private List<Integer> generateSteps(Lesson lesson, Settings currentSettings) {
        if (lesson.getObligatoryKeyNumbers().size() > 0) {
            return generateObligatorySteps(lesson, currentSettings);
        } else {
            return generateCommonSteps(lesson, currentSettings);
        }

    }

    private List<Integer> generateObligatorySteps(Lesson lesson, Settings currentSettings) {

        List<int[]> steps = new ArrayList<>(currentSettings.steps + (addSum ? 1 : 0));


        //getObligatoryPosition
        //getObligatoryDigit
        int obligatoryPosition;
        if (checkFirstImpossible(lesson)) {
            obligatoryPosition = r.nextInt(currentSettings.steps - 2) + 1;
        } else {
            obligatoryPosition = r.nextInt(currentSettings.steps - 1);
        }
        int[] digits = getDigitsForSteps(currentSettings);

        List<int[]> obligatorySteps = new ArrayList<>(getObligatorySteps(lesson, currentSettings, digits[obligatoryPosition], obligatoryPosition == 0));

        if (obligatoryPosition == 0) {
            steps.addAll(obligatorySteps);
        } else {
            int[] obligatoryResult = obligatorySteps.remove(0);

            List<int[]> backSteps = generateBackStepsToResult(lesson, obligatoryPosition, obligatoryResult);
            if (Digs.getValue(Digs.sum(backSteps)) != Digs.getValue(obligatoryResult)) {
                //throw new IllegalStateException("Expected obligatory value isn't equals with back steps");
            }
            steps.addAll(backSteps);
            steps.addAll(obligatorySteps);
        }

        steps = steps.stream().filter(s-> Digs.getValue(s) != 0).collect(Collectors.toList());
//        if (Digs.getValue(steps.get(0)) == 0) {
//            steps.remove(0);
//        }

//        for (int i = steps.size(); i < currentSettings.getSteps(); i++) {
//            int[] step = generateDirectStep(lesson, currentSettings, steps, digits[i]);
//            steps.add(step);
//        }
        generateDirectStepsWithLimit(lesson, currentSettings, digits, steps);
        if (addSum) {
            steps.add(Digs.sum(steps));
        }
        return Digs.getValue(steps);
    }

    // keys is null and pair is negative
    private boolean checkFirstImpossible(final Lesson lesson) {
        //значени нулевое и все пары негативные
        //boolean b = lesson.getObligatoryKeyNumbers().stream().allMatch(n -> n == 0 && lesson.getObligatoryPair(n).stream().allMatch(p -> p < 0));
        //все пары негативные и больше первого значения
        boolean b = lesson.getObligatoryKeyNumbers().stream().allMatch(n -> lesson.getObligatoryPair(n).stream().allMatch(p -> (n + p) < 0));
        return b;

    }

    static List<int[]> generateBackStepsToResult(Lesson lesson, int position, int[] result) {

        Random r = RandomLevel.getR();
        if (position == 0) {


            return new ArrayList<>();
        } else {


            int[] firstPosStep = new int[result.length];
            int[] secondPosStep = new int[result.length];
            boolean positiveAttempt = SecondGenerator.generateBackStepsToResultBySign(lesson, result, firstPosStep, secondPosStep, true);
            int[] firstNegStep = new int[result.length];
            int[] secondNegStep = new int[result.length];
            boolean negativeAttmept = SecondGenerator.generateBackStepsToResultBySign(lesson, result, firstNegStep, secondNegStep, false);
            if (!negativeAttmept && !positiveAttempt) {
                return Arrays.asList(result);
            }
            final int[] firstStep;
            final int[] secondStep;
            if (positiveAttempt && negativeAttmept) {
                if (r.nextBoolean()) {
                    firstStep = firstPosStep;
                    secondStep = secondPosStep;
                } else {
                    firstStep = firstNegStep;
                    secondStep = secondNegStep;
                }
            } else if (positiveAttempt) {
                firstStep = firstPosStep;
                secondStep = secondPosStep;
            } else {
                firstStep = firstNegStep;
                secondStep = secondNegStep;
            }

            if (position == 1) {
                return new ArrayList<>(Arrays.asList(firstStep, secondStep));
            } else {
                List<int[]> steps = new ArrayList<>(generateBackStepsToResult(lesson, position - 1, firstStep));
                steps.add(secondStep);
                return steps;

            }

        }


    }

    static boolean generateBackStepsToResultBySign(Lesson lesson, int[] result, int[] firstStep, int[] secondStep, boolean sign) {
        Random r = RandomLevel.getR();
        int carry = 0;
        for (int i = 0; i < result.length; i++) {
            int resultValue = result[i] - (carry);
            List<Tuple2<Integer, Integer>> pairs = lesson.getResultMap(resultValue);
            if (pairs == null) {
                //System.out.println("Not found back step for " + resultValue + " in result " + Arrays.toString(result));
                return false;
            }

//            List<Tuple2<Integer, Integer>> resultMapWithCarry = lesson.getResultMap(resultValue + ((sign) ? 10 : -10));
//            if (resultMapWithCarry != null) {
//                for (Tuple2<Integer, Integer> tuple2 : resultMapWithCarry) {
//                    if (!pairs.contains(tuple2)) {
//                        pairs.addAll(resultMapWithCarry);
//                    }
//                }
//            }

            //sign && p.getB() >=0 || !sign && p.getB() <=0
            // !sign && p.getB() <= 0

            pairs = pairs.stream().filter(p -> (sign && p.getB() >= 0) || (!sign && p.getB() <= 0)).collect(Collectors.toList());

            if (pairs.isEmpty()) {
                //System.out.println("Not found back step for " + resultValue + " in result " + Arrays.toString(result));
                return false;
            }
            Tuple2<Integer, Integer> pair = pairs.get(r.nextInt(pairs.size()));

            int sumPair = pair.getA() + pair.getB();
            carry = (sign && sumPair >= 10) ? 1 : (! sign && sumPair < 0) ? -1 : 0;
            firstStep[i] = pair.getA();
            secondStep[i] = pair.getB();
        }
        return true;
    }


    private List<int[]> getObligatorySteps(Lesson lesson, Settings currentSettings, int digits, boolean isFirst) {

        List<Integer> obligatoryKeyNumbers = lesson.getObligatoryKeyNumbers();
        if (isFirst) {
            obligatoryKeyNumbers = obligatoryKeyNumbers.stream().filter(n -> n > 0).collect(Collectors.toList());
        }
        Integer firstObligatory = getRandom(obligatoryKeyNumbers);
        List<Integer> pairs = lesson.getObligatoryPair(firstObligatory);
        Integer secondObligatory = getRandom(pairs);
        boolean needNegativeCary = firstObligatory + secondObligatory < 0;
        //if need carry then maxObligatory => digits -1 =>0
        final int maxObligatoryDigit;
        if (needNegativeCary) {
            if (digits == 1) {
                digits = digits + 1;
            }
            maxObligatoryDigit = digits - 1;
        } else {
            //todo add to firstObligatory value for carry

            maxObligatoryDigit = digits;
        }
        //todo if = max, than many error and negative results, with -1 error is absent, but simple exercises are present in the last series.
        final int obligatoryDigit = maxObligatoryDigit - 1;
        //int obligatoryDigit = r.nextInt(maxObligatoryDigit);

        int[] firstStep = new int[digits];  //todo

        List<Integer> allCommonKeys = new ArrayList<>(lesson.getKeyNumbers());
        List<Integer> keysWithoutZero = allCommonKeys.stream().filter(n -> n > 0).collect(Collectors.toList());
        List<Integer> keysWithZero = new ArrayList<>(keysWithoutZero);
        if (allCommonKeys.contains(0)) {
            keysWithZero.add(0);
        }
        List<Integer> keys = keysWithoutZero;
        for (int i = digits - 1; i >= 0; i--) {
            if (i == obligatoryDigit) {
                firstStep[i] = firstObligatory;
            } else {
                firstStep[i] = (keys.isEmpty()) ? 0 : getRandom(keys);
            }
            //add zero if need;
            keys = keysWithZero;

        }

        int[] secondStep = getSecondObligatoryValue(lesson, firstStep, digits, obligatoryDigit, secondObligatory);

        return Arrays.asList(firstStep, secondStep);
    }

//    private int[] getSecondObligatoryValue(Lesson level, int[] obligatoryStep, int digits, int obligatoryDigit, Params currentSettings) {
//        List<Integer> secondObligatories = level.getObligatoryPair(result[obligatoryDigit]);
//        Integer secondObligatory = getRandom(secondObligatories);
//        boolean positive = secondObligatory > 0;
//
//        generateDirectStep(level, currentSettings, Arrays.asList(obligatoryStep), digits );
//
//        return new int[0];
//    }


//    private int[] getSecondObligatoryValue(Lesson level, int[] result, int digits, int obligatoryDigit) {
//        List<Integer> secondObligatories = level.getObligatoryPair(result[obligatoryDigit]);
//        Integer secondObligatory = getRandom(secondObligatories);
//        boolean positive = secondObligatory >0;
//
//
//        int carry = 0;
//        int[] nextValue = new int[result.length];
//        for (int i = 0; i < digits; i++) {
//
//            int prevValueWithCarryCorrect = result[i] - carry;
//            int value;
//            if (i == obligatoryDigit) {
//                List<Integer> obligatoryOperands = level.getObligatoryPair(prevValueWithCarryCorrect);
//                List<Integer> filtered = obligatoryOperands.stream().filter(o -> (positive && o >= 0) || (!positive && o <= 0)).collect(Collectors.toList());
//
//                value = getNextValue(prevValueWithCarryCorrect, filtered);
//            } else {
//                ArrayList<Integer> pairs = (ArrayList<Integer>) ((positive) ? level.getPositive(prevValueWithCarryCorrect) : level.getNegative(prevValueWithCarryCorrect));
//                value = getNextValue(prevValueWithCarryCorrect, pairs);
//            }
//            value += carry;
//            if (value >= 10) {
//                value -= 10;
//                carry = 1;
//            } else if (value < 0) {
//                value += 10;
//                carry = -1;
//            }
//            nextValue[i] = value;
//
//
//        }
//
//        return nextValue;
//    }


    private static int getNextValue(Integer prevValue, List<Integer> pairs) {
        //level.get(sum);

        //Integer index = r.nextInt(pairs.size());

        Integer nextValue = getRandom(pairs);
        if (prevValue != null && nextValue == -prevValue) {
            int attemptCount = 0;
            while (prevValue == -((nextValue = getRandom(pairs)))) {
                attemptCount++;
                if (attemptCount > ATTEMPT_LIMIT) {
                    System.out.println("ERROR! Impossible generate steps" + ATTEMPT_LIMIT + " times. Values = " + nextValue + " pairs " + pairs);
                    //todo exit with exception with break
                    // System.exit(0);
                    break;
                }
            }
        }
        return nextValue;
    }

    private List<Integer> generateCommonSteps(Lesson lesson, Settings currentSettings) {

        int[] digits = getDigitsForSteps(currentSettings);
        List<int[]> steps = new ArrayList<>(currentSettings.steps + (addSum ? 1 : 0));

        generateDirectStepsWithLimit(lesson, currentSettings, digits, steps);
        if (addSum) {
            steps.add(Digs.sum(steps));
        }
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
        //Tuple2<int[], Integer> sumCarry
        final int[] sum;
        int[] extSum = (steps.isEmpty()) ? new int[step.length] : Digs.sum(steps);
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

    static int[] getStepFromHigh(Lesson lesson, int digits, int[] step, int[] sum, boolean sign, int excludeZero) {
        int currentSum = 0;
        int currentCarry = 0;

        for (int dig = digits - 1; dig >= 0; dig--) {
            int prevSum = sum[dig] ;
            List<Integer> numbers;
            if (sign) {
                numbers = lesson.getPositive(prevSum);
                int carry = currentCarry;
                numbers = numbers.stream().filter(n-> (carry == 0) ? n + prevSum < 10 : n + prevSum >10).collect(Collectors.toList());
            }

            //if enable carry
            if (dig > 1) {
                if (sign) {
                    final int prevLower = sum[dig - 1];
                    final List<Integer> positives = lesson.getPositive(prevLower);
                    final long carryCount = positives.stream().filter(n -> prevLower + n >= 10).count();
                    boolean enableCarry  = RandomLevel.getR().nextDouble() > (double) carryCount / positives.size();
                    currentCarry = (enableCarry) ? 1:0;

                }
            }


    //        step[dig] = value;
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


    private static int[] getSecondObligatoryValue(Lesson lesson, int[] prevStep, int digits, int obligatoryDigit, Integer secondObligatory) {

//        int firstObligatory = prevStep[obligatoryDigit];
//        List<Integer> secondObligatories = lesson.getObligatoryPair(firstObligatory);
        //TODO  неправильно возможность переноса считает
        //if (!possibleNegativeCarry(prevStep, obligatoryDigit + 1)) {
//        if (!possibleNegativeCarry(prevStep, obligatoryDigit + 1)) {
//            //secondObligatories = secondObligatories.stream().filter(i -> i>=firstObligatory).collect(Collectors.toList());
//            int[] b = new int[obligatoryDigit + 1 + 1];
//            b[obligatoryDigit + 1] = 1;
//            Digs.add(prevStep, b);
//        }
//
////        if (possiblePositiveCarry(prevStep, obligatoryDigit)) {
////            secondObligatories = secondObligatories.stream().filter(i -> i<=0).collect(Collectors.toList());
////        }
//        Integer secondObligatory = getRandom(secondObligatories);
        boolean sign = secondObligatory >= 0;

        int step[] = new int[prevStep.length];
        //Tuple2<int[], Integer> sumCarry
        int[] sum = prevStep;

        int carry = 0;
        int i = 0;
        for (; i < digits; i++) {
            final int value;
            if (i == obligatoryDigit) {
                value = secondObligatory;
            } else {
                if (sign ) {
                    if (lesson.getPositive(prevStep[i]).size() != 0) {
                        value = getRandom(lesson.getPositive(prevStep[i]));
                    } else {
                        value = 0;
                    }
                } else {
                    List<Integer> negativePairs = lesson.getNegative(prevStep[i]);

                    if (!possibleNegativeCarry(prevStep, i + 1) ) {
                        final int index = i;
                        final int lambdaCarry = carry;
                        negativePairs = negativePairs.stream().filter(p -> p + prevStep[index] + lambdaCarry >=0).collect(Collectors.toList());
                    }
                    if (negativePairs != null && negativePairs.size() >0) {
                        value = getRandom(negativePairs);
                    } else {
                        value = 0;
                    }

                }
            }
            step[i] = value - carry;
            int digitSum = step[i] + prevStep[i];
            carry = digitSum >= 10 ? 1 : digitSum < 0 ? -1 : 0;
        }
//        if (carry != 0 && i< step.length ) {
//            step[i] = carry;
//        }

        return step;
    }

    @Deprecated
    private static int[] getSecondObligatoryValueNotWork(Lesson lesson, int[] prevStep, int digits, int obligatoryDigit) {

        int firstObligatory = prevStep[obligatoryDigit];
        List<Integer> secondObligatories = lesson.getObligatoryPair(prevStep[obligatoryDigit]);
        Integer secondObligatory = getRandom(secondObligatories);
        boolean sign = secondObligatory >= 0;

        int step[] = new int[prevStep.length];
        //Tuple2<int[], Integer> sumCarry
        int[] sum = prevStep;


        List<Integer> negative = lesson.getNegative(sum[0]);
        //??????? ???????? ?? ???????????? ?????, ???? ???? ? ?????? ???????? ?? ????????
        if (!sign && !possibleNegativeCarry(sum, 1) && negative != null) {
            negative = negative.stream().filter(n -> (sum[0] + n) >= 0).collect(Collectors.toList());
            if (negative.isEmpty()) {
                System.out.println("ERROR! negative for " + firstObligatory + "  is empty prevStep " + Arrays.toString(prevStep));
            }
        } else if (negative == null) {
            System.out.println("ERROR! negative for " + firstObligatory + "  is null prevStep " + Arrays.toString(prevStep));
        }

        List<Integer> positive = lesson.getPositive(sum[0]);
        //???????? ???????? ?? ???????????? ???? ???? ?????????? ? ??????? ??????? ??????.
        if (sign && !possiblePositiveCarry(sum, 1)) {
            positive = positive.stream().filter(n -> (sum[0] + n) <= 9).collect(Collectors.toList());
            if (positive.isEmpty()) {
                System.out.println("ERROR! positive for " + firstObligatory + "  is empty prevStep " + Arrays.toString(prevStep));
                if (!possibleNegativeCarry(sum, 1) && negative != null) {
                    negative = negative.stream().filter(n -> (sum[0] + n) >= 0).collect(Collectors.toList());
                    if (negative.isEmpty()) {
                        System.out.println("ERROR! positive for " + firstObligatory + "  is empty prevStep " + Arrays.toString(prevStep));
                        return new int[]{digits};
                    }
                } else if (negative == null) {
                    System.out.println("Error impossible find next negative and positive for " + Arrays.toString(sum));
                    return new int[]{digits};
                }
            }
        }


        List<Integer> obligatoryKeyNumbers = lesson.getObligatoryKeyNumbers();
        Map<Integer, List<Integer>> obligatorySigned = new HashMap<>();
        for (Integer key : obligatoryKeyNumbers) {
            for (Integer pair : lesson.getObligatoryPair(key)) {
                if (sign && pair >= 0 || !sign && pair <= 0)
                    Lesson.addToMap(key, pair, obligatorySigned);
            }
        }
        List<Integer> keys = new ArrayList<>(obligatorySigned.keySet());
        lesson.getObligatoryPair(getRandom(obligatoryKeyNumbers));
        int currentSum = 0;
        int currentCarry = 0;
        for (int i = 0; i < digits; i++) {

            int prevSum = sum[i] + currentCarry;


            List<Integer> numbers = (sign) ? lesson.getPositive(prevSum) : lesson.getNegative(prevSum);
            numbers = (numbers == null) ? Collections.emptyList() : numbers;
            if (sign) {
                if (!possiblePositiveCarry(sum, i + 1)) {
                    numbers = numbers.stream().filter(n -> (prevSum + n) <= 9).collect(Collectors.toList());
                }
            } else {
                if (!possibleNegativeCarry(sum, i + 1)) {
                    numbers = numbers.stream().filter(n -> (prevSum + n) >= 0).collect(Collectors.toList());
                }
            }

            int value;
            if (i == obligatoryDigit) {
                Integer key = getRandom(keys);
                value = getRandom(obligatorySigned.get(key));
            } else {
                if (!numbers.isEmpty()) {
                    value = getRandom(numbers);
                } else {
                    value = 0;
                }
            }

            currentSum = prevSum + value;
            currentCarry = (currentSum >= 10) ? 1 : (currentSum < 0) ? -1 : 0;
            step[i] = value;
        }
        return step;
    }

}
