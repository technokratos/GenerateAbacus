package dbk.odf;

import dbk.abacus.Level;
import dbk.abacus.Tuple2;
import dbk.rand.RandomLevel;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by dbk on 30.08.2016.
 */
public class SimpleGenerator {


    private Random r = RandomLevel.getR();

    public static final int ATTEMPT_LIMIT = 10;
    private final List<Level> levels;

    public SimpleGenerator(List<Level> levels) {
        this.levels = levels;
    }

    public List<Tuple2<Level, List<List<List<Integer>>>>> generate(boolean addSum) {

        //        int[][][] exercises  = new int[levels.size()][seriesCount][stepCountWithSum];
        List<Tuple2<Level, List<List<List<Integer>>>>> exercises = new ArrayList<>();
        for (Level level : levels) {

            List<Integer> firstNumbers = new ArrayList<>(level.getKeyNumbers());
            int indexOf = firstNumbers.indexOf(0);
            if (indexOf >=0) {
                firstNumbers.remove(indexOf);
            }
            if (firstNumbers.isEmpty()) {
                continue;
            }
            List<List<List<Integer>>> levelList = new ArrayList<>(level.getSettings().size());
            exercises.add(new Tuple2<>(level, levelList));

            for (Settings currentSettings : level.getSettings()) {

                int stepCount = currentSettings.getSteps();
                int stepCountWithSum = stepCount + ((addSum) ? 1 : 0);
                int seriesCount = currentSettings.getSeries();

                System.out.println(level.getTitle());
                List<List<Integer>> series = new ArrayList<>(seriesCount);
                levelList.add(series);


                for (int seriesIndex = 0; seriesIndex < seriesCount; seriesIndex++) {
                    List<Integer> steps = generateStepsWithLimit(addSum, level, firstNumbers, currentSettings);
                    addDuplicate(steps, currentSettings);

                    series.add(steps);
                }
            }
        }
        return exercises;
    }


    /**
     * limit of attemps
     */
    private List<Integer> generateStepsWithLimit(boolean addSum, Level level, List<Integer> firstNumbers, Settings settings) {
        List<Integer> steps = new ArrayList<>(settings.steps);
        List<int[]> stepsInDigits;
        int attempts = 0;
        int stepCount = settings.getSteps();
        do {
            stepsInDigits = generateSteps(addSum, level, firstNumbers, settings);
            attempts++;
            if (attempts > ATTEMPT_LIMIT) {
                System.out.println("ERROR! Impossible generate steps" + ATTEMPT_LIMIT + " times.");
                System.exit(0);
            }
        } while (stepsInDigits == null || stepsInDigits.size() < (stepCount + ((addSum) ? 1 : 0)));
        for (int i = 0; i < stepsInDigits.size(); i++) {
            int[] digits = stepsInDigits.get(i);
            int step = Digits.getValue(digits);
            steps.add(step);
        }
        return steps;
    }

    private void addDuplicate(List<Integer> steps, Settings currentSettings) {
        for (int index = 0; index < steps.size(); index++) {
            int variable = steps.get(index);
            for (int i = 0; i < currentSettings.duplicate; i++) {
                variable = 10 * variable + variable;
            }
            steps.set(index, variable);
        }
    }

    private List<int[]> generateSteps(boolean addSum, Level level, List<Integer> firstNumbers, Settings settings) {
        int stepsCount = settings.getSteps();
        List<int[]> steps = new ArrayList<>(stepsCount + ((addSum) ? 1 : 0));
        List<Integer> obligatoryFirstOperands = level.getObligatoryKeyNumbers();
        if (obligatoryFirstOperands.size() == 0) {
            int[] sum = generateStepsWithoutObligatory(level, steps, firstNumbers, settings);
            if (addSum) {
                steps.add(sum);
            }
        } else {
//            //select step for obligatory
//            int obligatoryStep = r.nextInt(stepsCount - 1);
//            int obligatoryFirstOperand = getRandom(obligatoryFirstOperands);
//            List<Integer> obligatorySecondOperands = level.getObligatoryPair(obligatoryFirstOperand);
//            Integer obligatorySecondOperand = getRandom(obligatorySecondOperands);
//
//            int sum = 0;
//
//            //select first value
//            final Integer firstValue;
//            if ((obligatoryStep > 0)) {
//                Integer firstIndex = r.nextInt(firstNumbers.size());
//                firstValue = firstNumbers.get(firstIndex);
//                if (firstValue == obligatoryFirstOperand) {
//                    obligatoryStep = 0;
//                }
//            } else if (obligatoryFirstOperand == 0) {
//                firstValue = obligatorySecondOperand;
//            } else {
//                firstValue = obligatoryFirstOperand;
//
//            }
//            sum = firstValue;
//            System.out.print(firstValue + ",");
//            steps.add(firstValue);
//            final List<Integer> path;
//
//            if (obligatoryStep == 0) {
//                path = Collections.emptyList();
//            } else {
//                //find path to first obligatory value
//                List<List<Integer>> paths = findPaths(level, obligatoryStep - 1, firstValue, obligatoryFirstOperand);
//                if (paths != null && !paths.isEmpty()) {
//                    path = paths.get(r.nextInt(paths.size()));
//                } else {
//                    System.out.println("Not found path from " + firstValue + " to " + obligatoryFirstOperand);
//                    return null;
//                }
//                //add second obligatory
//                path.add(obligatorySecondOperand);
//                steps.addAll(path);
//
//            }
            int[] sum;
            sum = generateStepsWithObligatory(level, steps, firstNumbers, settings);


            if (addSum) {
                steps.add(sum);
            }
            System.out.println(" sum =" + IntStream.of(sum).sum());

        }
        return steps;
    }


    private List<List<Integer>> findPaths(Level level, int obligatoryStep, Integer from, int to) {
        final List<Tuple2<Integer, Integer>> tuples = level.getResultMap(to);

        List<List<Integer>> paths = new LinkedList<>();
        if (obligatoryStep == 0) {
            List<Integer> path = findPairInTuples(tuples, from);
            if (!path.isEmpty()) {
                paths.add(path);
            }
            return paths;
        } else {
            final List<Integer> secondOperandsWithFrom = level.get(from);
            for (Integer secondOperand : secondOperandsWithFrom) {
                int nextSum = secondOperand + from;
                List<List<Integer>> subPaths = findPaths(level, obligatoryStep - 1, nextSum, to);
                for (List<Integer> path : subPaths) {
                    if (!path.isEmpty()) {
                        //path.add(0, secondOperand);
                        path.add(0, secondOperand);
                        paths.add(path);
                    }
                }
            }
        }
        return paths;

    }

    private List<List<Integer>> findPaths(Level level, int steps, int value) {
        if (steps == 1) {
            //List<Integer> positiveKeys = level.getPositiveKeys();
            List<Tuple2<Integer, Integer>> pairs = level.getResultMap(value);
            List<List<Integer>> lists = pairs.stream().filter(t -> t.getA() > 0).mapToInt(Tuple2::getA).mapToObj(Arrays::asList).collect(Collectors.toList());
            return lists;
        } else {
            List<Tuple2<Integer, Integer>> pairs = level.getResultMap(value);
            List<List<Integer>> paths = new ArrayList<>();
            for (Tuple2<Integer, Integer> pair : pairs) {
                List<List<Integer>> foundPaths = findPaths(level, steps - 1, pair.getA());
                foundPaths.forEach(p -> p.add(pair.getB()));
                paths.addAll(foundPaths);
            }
            return paths;

        }
    }

    private List<Integer> findPairInTuples(List<Tuple2<Integer, Integer>> tuple2s, Integer operand) {
        List<Integer> pairs = new ArrayList<>();
        for (Tuple2<Integer, Integer> tuple2 : tuple2s) {
            if (tuple2.getA().equals(operand)) {
                pairs.add(tuple2.getB());
//                return tuple2.getB();
                //          pairs.add(tuple2.getB());
            }
//            else if (tuple2.getB().equals(operand)) {
//                pairs.add(tuple2.getA());
//                //return tuple2.getA();
//    //            pairs.add(tuple2.getA());
//            }
        }
        return pairs;
    }

    private int generateNextValue(Random r, Level level, List<Integer> steps, int sum, Integer obligatoryStep, Integer obligatoryNumber, int stepsCount) {
        List<Integer> obligatoryPair = level.getObligatoryPair(obligatoryNumber);
        //
        for (int i = 1; i < stepsCount; i++) {
            ArrayList<Integer> pairs = (ArrayList<Integer>) level.get(sum);
            Integer pairForObligatoryNumber = null;
//            for(Integer pair: pairs){
//
//            }
            Integer index = r.nextInt(pairs.size());
            int nextValue = pairs.get(index);
            System.out.print(nextValue + ",");
            sum += nextValue;
            steps.add(nextValue);
        }
        return sum;
    }

    private int[] generateStepsWithoutObligatory(Level level,
                                                 List<int[]> steps,
                                                 List<Integer> firstNumbers,
                                                 Settings settings) {
        int stepsCount = settings.getSteps();
        List<Integer> extensionPosition = getExtensionPosition(settings);
        int firstValue[] = getFirstValue(firstNumbers, settings, extensionPosition.contains(0));

        steps.add(firstValue);
        Tuple2<int[], Integer> sumCarry = Digits.sumCarry(steps);
        int[] sum = sumCarry.getA();
        for (int i = 1; i < stepsCount; i++) {


            int digits = (extensionPosition.contains(i)) ? steps.size() : 1;
            int[] value = getNextValue(level, sumCarry, steps.get(i - 1), digits);
            //System.out.print(value + ",");

            steps.add(value);
            sumCarry = Digits.sumCarry(steps);
            sum = sumCarry.getA();

        }
        return sum;
    }

    private int[] generateStepsWithObligatory(Level level,
                                              List<int[]> steps,
                                              List<Integer> firstNumbers,
                                              Settings settings) {
        int stepsCount = settings.getSteps();
        List<Integer> obligatoryFirstOperands = level.getObligatoryKeyNumbers();
        int obligatoryStep = r.nextInt(stepsCount - 1);
        List<Integer> extensionPosition = getExtensionPosition(settings);
        int obligatoryDigit = (extensionPosition.contains(obligatoryStep) ? r.nextInt(settings.getExtensionDigit() + 1) : 0);
        int[] sum;
        if (obligatoryStep == 0) {
//            if (extensionPosition.contains(0)) {
//            steps.addAll(getFirstObligatoryValue());
//        }
            obligatoryFirstOperands = obligatoryFirstOperands.stream().filter(o->o != 0).collect(Collectors.toList());
            int obligatoryFirstOperand = getRandom(obligatoryFirstOperands);
            List<Integer> obligatorySecondOperands = level.getObligatoryPair(obligatoryFirstOperand);
            Integer obligatorySecondOperand = getRandom(obligatorySecondOperands);
            int length =(extensionPosition.contains(0))? 1 + settings.getExtensionDigit(): 1;
            int[] firstStep = new int[length];
            List<Integer> positiveKeys = level.getPositiveKeys();
            for (int i = firstStep.length - 1; i >= 0; i--) {
                if (i == obligatoryDigit) {
                    firstStep[i] = obligatoryFirstOperand;
                } else {
                    List<Integer> positivePairs = level.getPositive(getRandom(positiveKeys));
                    firstStep[i] = getRandom(positivePairs);

                }
            }

            boolean positive = obligatorySecondOperand > 0;
            int[] secondStep = getSecondObligatoryValue(level, firstStep, firstStep, positive, firstStep.length, obligatoryDigit);
            sum = Digits.add(firstStep, secondStep);
            steps.add(firstStep);
            steps.add(secondStep);
        } else {
//            int firstValue[] = getFirstValue(firstNumbers, settings, extensionPosition.contains(0));
//            steps.add(firstValue);
//            sum = firstValue;
            List<int[]> path = findPaths(level, obligatoryStep, obligatoryDigit, extensionPosition, settings);
            steps.addAll(path);
        }

        sum= Digits.sum(steps);

        for (int i = steps.size(); i < stepsCount; i++) {
            boolean positive = r.nextDouble() < level.getPositiveRelation(sum[sum.length - 1]);
            int digits = (extensionPosition.contains(i)) ? steps.size() : 1;
            int[] value = new int[3];// = //getNextValue(level, sum, steps.get(i - 1), positive, digits);
            //System.out.print(value + ",");
            sum = Digits.add(sum, value);

            steps.add(value);
        }
        return sum;
    }

    /**
     * obligatoryFirstOperand 9
     * ?1? +     ?44
     * 2? +    - 14
     * 3? +    + 61
     * -3-     191
     * 101
     */


    private List<int[]> findPaths(Level level, int obligatoryStep, int obligatoryDigit, List<Integer> extensionPosition, Settings settings) {

        boolean allDigits = extensionPosition.contains(obligatoryStep);
        int length = allDigits ? 1 + settings.getExtensionDigit(): 1;

        List<Integer> obligatoryKeyNumbers = level.getObligatoryKeyNumbers();
        //List<Integer> positiveNumbers = obligatoryKeyNumbers.stream().filter(i -> i > 0).collect(Collectors.toList());
        int[] obligatoryResult = new int[length];

        List<Integer> obligatoryFirstOperands = level.getObligatoryKeyNumbers();

        Integer obligatoryFirstOperand = getRandom(obligatoryFirstOperands);
        obligatoryResult[obligatoryDigit] = obligatoryFirstOperand;
        List<Integer> obligatoryOperands = level.getObligatoryPair(obligatoryFirstOperand);


        List<Integer> negative = level.getKeyNumbers().stream().filter(n -> n <= 0).collect(Collectors.toList());
        List<Integer> positive = level.getKeyNumbers().stream().filter(n -> n >= 0).collect(Collectors.toList());
        for (int i = 0; i < length; i++) {
            if (obligatoryDigit == i) {
                obligatoryResult[i] = obligatoryFirstOperand;
            } else if (obligatoryFirstOperand < 0) {
                int negativeValue = getRandom(negative);
                obligatoryResult[i] = negativeValue;
            } else if (obligatoryFirstOperand > 0) {
                obligatoryResult[i] = getRandom(positive);
            }
        }

//        if (obligatoryFirstOperand < 0) {
//            obligatoryResult[(length) - 1] = -1 * obligatoryResult[(length - 1)];
//        }

        int extensionDigit = (extensionPosition.contains(obligatoryStep) ? settings.getExtensionDigit() : obligatoryDigit) + 1;
        int[] secondObligatoryRow = new int[extensionDigit];
        //secondObligatoryRow[obligatoryDigit]=obligatorySecondOperand;
        int carry = 0;
        boolean sign = getRandom(obligatoryOperands) > 0;
        List<Integer> obligatoryPositiveOperands = obligatoryOperands.stream().filter(i -> i > 0).collect(Collectors.toList());
        List<Integer> obligatoryNegativeOperands = obligatoryOperands.stream().filter(i -> i <= 0).collect(Collectors.toList());
        for (int i = extensionDigit - 1; i >= 0; i--) {
            List<Integer> oblPairs = (sign) ? obligatoryPositiveOperands : obligatoryNegativeOperands;
            if (i == obligatoryDigit) {
                //find any positive pair
                int obligatoryPair = getRandom(oblPairs);
                //корректируем на перенос
                obligatoryPair += (sign) ? carry : -carry;
                secondObligatoryRow[i] = obligatoryPair;
            } else {
                List<Integer> pairs = level.get(obligatoryResult[i], sign);
                secondObligatoryRow[i] = getRandom(pairs);

            }
            carry = (secondObligatoryRow[i] + obligatoryResult[i] + carry) / 10;
        }
        //what about carry?


        //getObligatorySecond row for first
        //findPaths to the begin for every gits
        //cache paths for every digits?
        List<int[]> paths = findPathsToSum(level, obligatoryStep, obligatoryResult);
        paths.add(secondObligatoryRow);
        return paths;
    }

    // step 1, result 123:  111, +012, (123, 000)
    // step 0 impossible ,
    // step 2, 254: 363, -241, +132
    private List<int[]> findPathsToSum(Level level, int step, int[] result) {
        //123,
        List<int[]> path = new ArrayList<>();

        List<Tuple2<Integer, Integer>> pairsPath[] = new List[result.length];
        boolean sign = true;
        int[] lastStep = new int[result.length];
        int[] prevStep = new int[result.length];
        for (int i = result.length -1 ; i >=0 ; i--) {
            List<Tuple2<Integer, Integer>> pairs = level.getResultMap(result[i]);
            if (result.length - i > 1) {
                List<Tuple2<Integer, Integer>> overTenPairs = level.getResultMap(10 + result[i]);
                if (overTenPairs != null) {
                    pairs.addAll(overTenPairs);
                }
                List<Tuple2<Integer, Integer>> minusOverTen = level.getResultMap(-10 + result[i]);
                if (minusOverTen != null) {
                    pairs.addAll(minusOverTen);
                }
                //pairs.addAll(level.getResultMap(10));
                //pairsPath[i] = pairs;
            }

            if (i == result.length -1 ){
                Tuple2<Integer, Integer> pair = pairs.get(r.nextInt(pairs.size()));
                sign = pair.getB()>=0;
                lastStep[i] = pair.getB();
                prevStep[i] = pair.getA();
            } else {
                final boolean positive = sign;
                pairs = pairs.stream().filter(p -> positive && p.getB() >=0 || !positive && p.getB()<0).collect(Collectors.toList());
                Tuple2<Integer, Integer> pair = pairs.get(r.nextInt(pairs.size()));
                lastStep[i] = pair.getB();
                prevStep[i] = pair.getA();
            }
        }

        if (step >=1 ){
            path = findPathsToSum(level, step - 1, prevStep);
        }
        if (step == 0) {
            path.add(prevStep);
        }
        path.add(lastStep);
        return path;

        //normilize carry
    }

    private Integer getRandom(List<Integer> list) {
        return list.get(r.nextInt(list.size()));
    }

    private List<Integer> getExtensionPosition(Settings settings) {
        int extCount = settings.getExtensionCount();
        int steps = settings.steps;
        List<Integer> positions = new ArrayList<>(steps);
        if (extCount == steps) {
            for (int i = 0; i < steps; i++) {
                positions.add(i);
            }
        } else {
            for (int i = 0; i < extCount; i++) {
                int attempts = 0;
                while (attempts < ATTEMPT_LIMIT) {
                    int position = r.nextInt(steps);
                    if (!positions.contains(position)) {
                        attempts++;
                    } else {
                        positions.add(position);
                    }
                }
            }
        }
        return positions;
    }

    private int[] getFirstValue(List<Integer> firstNumbers, Settings settings, boolean allDigits) {
        int length = (allDigits)? settings.getExtensionDigit() +1 : 1;
        int[] firstValue = new int[length];
//        Integer firstIndex = r.nextInt(firstNumbers.size());
//        ;
//        int firstPosition = firstNumbers.get(firstIndex);
//        firstValue[settings.getExtensionDigit()] = firstPosition;
        for (int i = 0; i < length; i++) {
                firstValue[i] = getRandom(firstNumbers);

        }
        return firstValue;
    }

//    private int[] getFirstObligatoryValue(List<Integer> firstNumbers, Settings settings, boolean allDigits) {
//        int[] firstValue = new int[settings.getExtensionDigit()];
//        Integer firstIndex = r.nextInt(firstNumbers.size());
//        ;
//        int firstPosition = firstNumbers.get(firstIndex);
//        firstValue[settings.getExtensionDigit()] = firstPosition;
//        if (settings.getExtensionCount() == settings.getSteps() || allDigits) {
//            for (int i = 1; i < settings.getExtensionDigit(); i++) {
//                firstValue[i] = getRandom(firstNumbers);
//            }
//        }
//        return firstValue;
//    }

    /**
     * @param level

     * @param prevValue
     * @param digits
     * @return
     */
    private int[] getNextValue(Level level, Tuple2<int[], Integer> sumCarry, int[] prevValue, int digits) {
        //if carry >0 -> positive may be false
        int[] sum = sumCarry.getA();
        boolean positive = r.nextDouble() < level.getPositiveRelation(sum[0]);
        int carry = 0;//sumCarry.getB();
        int[] nextValue = new int[prevValue.length];
        int currentSum =0;
        int currentDigitForSum = 0;

        for (int i = prevValue.length - 1; i >= 0; i--) {
            //todo поправка разряда для суммы может быть вычисленна
            int sumWithCarry = sum[i] + carry;
            sumWithCarry += (sumWithCarry >= 10)? -10: (sumWithCarry< 0)? +10:0;
            List<Integer> negative = level.getNegative(sumWithCarry);
            List<Integer> positive1 = level.getPositive(sumWithCarry);
            //firstStep
            if (i == prevValue.length - 1) {
                if (positive) {//отсеить переполнение суммы

                } else {

                }
            }
            List<Integer> pairs = (positive) ? positive1 : negative;// 099- next value 01
            int value = getNextValue(prevValue[i] - carry, pairs);

            //currentSum += Digits.E(prevValue.length - i - 1 );
            currentDigitForSum = sum[i] + value + carry;
            if (currentDigitForSum >= 10) {
                carry = 1;
            } else if (currentDigitForSum <0) {
                carry = -1;
            }
            nextValue[i] = value;

        }

        return nextValue;
    }

    /**
     * @param level
     * @param sum
     * @param prevValue
     * @param positive
     * @param digits
     * @return
     */
    private int[] getSecondObligatoryValue(Level level, int[] sum, int[] prevValue, boolean positive, int digits, int obligatoryDigit) {

        int carry = 0;
        int[] nextValue = new int[prevValue.length];
        for (int i = prevValue.length - 1; i >= prevValue.length - digits; i--) {

            int prevValueWithCarryCorrect = prevValue[i] - carry;
            int value;
            if (i == obligatoryDigit) {
                List<Integer> obligatoryOperands = level.getObligatoryPair(prevValueWithCarryCorrect);
                List<Integer> filtered = obligatoryOperands.stream().filter(o -> (positive && o > 0) || (!positive && o < 0)).collect(Collectors.toList());
                value = getNextValue(prevValueWithCarryCorrect, filtered);
            } else {
                ArrayList<Integer> pairs = (ArrayList<Integer>) ((positive) ? level.getPositive(prevValueWithCarryCorrect) : level.getNegative(prevValueWithCarryCorrect));
                value = getNextValue(prevValueWithCarryCorrect, pairs);
            }
            value += carry;
            if (value >= 10) {
                value -= 10;
                carry = 1;
            } else if (value < 0) {
                value += 10;
                carry = -1;
            }
            nextValue[i] = value;


        }

        return nextValue;
    }


    private int getNextValue(Integer prevValue, List<Integer> pairs) {
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


}
