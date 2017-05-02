package dbk.odf;

import dbk.abacus.Level;
import dbk.abacus.Tuple2;
import dbk.rand.RandomLevel;

import java.util.*;

/**
 * Created by dbk on 30.08.2016.
 */
public class DigitGenerator {
    private Random r = RandomLevel.getR();

    public static final int ATTEMPT_LIMIT = 10;
    private final List<Level> levels;

    public DigitGenerator(List<Level> levels) {
        this.levels = levels;
    }

    public List<Tuple2<Level, List<List<List<Integer>>>>> generate(boolean addSum) {

        //        int[][][] exercises  = new int[levels.size()][seriesCount][stepCountWithSum];
        List<Tuple2<Level, List<List<List<Integer>>>>> exercises = new ArrayList<>();
        for (Level level : levels) {

            List<Integer> firstNumbers = new ArrayList<>(level.getKeyNumbers());
            if (firstNumbers.contains(0)) {
                firstNumbers.remove(0);
            }
            if (firstNumbers.isEmpty()) {
                continue;
            }
            List<List<List<Integer>>> levelList = new ArrayList<>(level.getSettings().size());
            exercises.add(new Tuple2<>(level, levelList));

            for (Settings currentSettings: level.getSettings()) {

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
        List<Integer> steps;
        int attempts = 0;
        int stepCount = settings.getSteps();
        do {
            steps = generateSteps(addSum, level, firstNumbers, settings);
            attempts++;
            if (attempts > ATTEMPT_LIMIT) {
                System.out.println("ERROR! Impossible generate steps" + ATTEMPT_LIMIT + " times.");
                System.exit(0);
            }
        } while (steps == null || steps.size() < (stepCount + ((addSum) ? 1 : 0)));
        return steps;
    }

    private void addDuplicate(List<Integer> steps, Settings currentSettings) {
        for (int index = 0; index < steps.size(); index++) {
            int variable = steps.get(index);
            for (int i = 0; i < currentSettings.duplicate; i++) {
                variable = 10* variable + variable;
            }
            steps.set(index, variable);
        }
    }

    private List<Integer> generateSteps(boolean addSum, Level level, List<Integer> firstNumbers, Settings settings) {
        int stepsCount = settings.getSteps();
        List<Integer> steps = new ArrayList<>(stepsCount + ((addSum)? 1:0));
        List<Integer> obligatoryFirstOperands = level.getObligatoryKeyNumbers();
        if (obligatoryFirstOperands.size() == 0) {
            int sum = generateStepsWithoutObligatory(level, steps, firstNumbers, settings);
            if (addSum) {
                steps.add(sum);
            }
        } else {
            //select step for obligatory
            int obligatoryStep = r.nextInt(stepsCount - 1);
            int obligatoryFirstOperand = obligatoryFirstOperands.get(r.nextInt(obligatoryFirstOperands.size()));
            List<Integer> obligatorySecondOperands = level.getObligatoryPair(obligatoryFirstOperand);
            Integer obligatorySecondOperand = obligatorySecondOperands.get(r.nextInt(obligatorySecondOperands.size()));

            int sum = 0;

            //select first value
            final Integer firstValue;
            if ((obligatoryStep > 0)) {
                Integer firstIndex = r.nextInt(firstNumbers.size());
                firstValue = firstNumbers.get(firstIndex);
                if (firstValue == obligatoryFirstOperand) {
                    obligatoryStep = 0;
                }
            } else if (obligatoryFirstOperand == 0) {
                firstValue = obligatorySecondOperand;
            } else {
                firstValue = obligatoryFirstOperand;

            }
            sum = firstValue;
            System.out.print(firstValue + ",");
            steps.add(firstValue);
            final List<Integer> path;

            if (obligatoryStep == 0) {
                path = Collections.emptyList();
            } else {
                //find path to first obligatory value
                List<List<Integer>> paths = findPaths(level, obligatoryStep - 1, firstValue, obligatoryFirstOperand);
                if (paths != null && !paths.isEmpty()) {
                    path = paths.get(r.nextInt(paths.size()));
                } else {
                    System.out.println("Not found path from " + firstValue + " to " + obligatoryFirstOperand);
                    return null;
                }
                //add second obligatory
                path.add(obligatorySecondOperand);
                steps.addAll(path);
                sum += path.stream().mapToInt(i -> i).sum();
            }

            //fill to the end by simple random values
            for (int i = path.size() + 1; i < stepsCount; i++) {
                int value = getNextValue(level, sum, steps.get(i - 1));
                sum += value;
                steps.add(value);
            }
            if (addSum) {
                steps.add(sum);
            }
            System.out.println("=" + sum);

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

    private int generateStepsWithoutObligatory(Level level,
                                               List<Integer> steps,
                                               List<Integer> firstNumbers,
                                               Settings settings) {
        int stepsCount = settings.getSteps();
        Integer firstIndex = r.nextInt(firstNumbers.size());
        Integer firstValue = firstNumbers.get(firstIndex);
        System.out.print(firstValue + ",");
        steps.add(firstValue);
        int sum = firstValue;
        for (int i = 1; i < stepsCount; i++) {
            int value = getNextValue(level, sum, steps.get(i - 1));
            //System.out.print(value + ",");
            sum += value;
            steps.add(value);
        }
        return sum;
    }

    private int getNextValue(Level level, int sum, Integer prevValue) {
        ArrayList<Integer> pairs = (ArrayList<Integer>) level.get(sum);
        //Integer index = r.nextInt(pairs.size());

        Integer nextValue = pairs.get(r.nextInt(pairs.size()));
        if (prevValue != null && nextValue == -prevValue) {
            int attemptCount = 0;
            while (prevValue == -((nextValue = pairs.get(r.nextInt(pairs.size()))))){
                attemptCount++;
                if (attemptCount> ATTEMPT_LIMIT){
                    System.out.println("ERROR! Impossible generate steps" + ATTEMPT_LIMIT + " times.");
                    //todo exit with exception with break
                    System.exit(0);
                }
            }
        }
        return nextValue;
    }


}
