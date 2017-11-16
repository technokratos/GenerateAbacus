package dbk.abacus;

import dbk.formula.Formulas;
import dbk.odf.Digs;
import dbk.odf.SecondGenerator;
import dbk.odf.Settings;
import dbk.rand.RandomLevel;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by denis on 15.09.17.
 */
public class ProbabilisticLesson {

    Random r = RandomLevel.getR();
    String title;


    private final List<Settings> settings;
    private int settingsIndex = -1;




    private final List<Pair> pairs = new ArrayList<>();
    Map<Integer, List<Pair>> valueToPair = new TreeMap<>();
    Map<String, List<Pair>> formulaMap = new TreeMap<>(new Formulas.FormulaComparator());
    Map<String, List<Pair>> obligatoryMap = new TreeMap<>(new Formulas.FormulaComparator());
    private int operationSeriesCount;

    public ProbabilisticLesson(List<Settings> settings, List<String> workFormulas, List<String> obligatoryFormulas) {
        this.settings = settings;

        final Formulas formula = Formulas.getInstance();


        workFormulas.stream()
                .map(f -> Tuple2.of(f, formula.getOperandsForFormula(f).stream()
                        .map(t-> new Pair(t.getA(), t.getB(), 0.5)).collect(Collectors.toList())))
                .peek(t-> formulaMap.put(t.getA(), t.getB()))
                .peek(t-> t.getB().forEach( p -> valueToPair.computeIfAbsent(p.getUp(), i-> new ArrayList<>()).add(p)))
                .forEach(t-> pairs.addAll(t.getB()));

        obligatoryFormulas.stream()
                .map(f -> Tuple2.of(f, formula.getOperandsForFormula(f).stream()
                        .map(t-> new Pair(t.getA(), t.getB(), 1)).collect(Collectors.toList())))
                .peek(t-> obligatoryMap.put(t.getA(), t.getB()))
                .peek(t-> t.getB().forEach( p -> valueToPair.computeIfAbsent(p.getUp(), i-> new ArrayList<>()).add(p)))
                .forEach(t-> pairs.addAll(t.getB()));

    }

    public void startSteps() {
        final Settings curSettings = this.settings.get(settingsIndex);
        final int operationCount = curSettings.getSteps() * curSettings.getExtensionDigit() *
                curSettings.getExtensionCount();
        obligatoryMap.forEach((s, pairs1) -> pairs1.forEach(Pair::resetWeight));
    }

    public void startSeries() {
        settingsIndex++;
        final Settings curSettings = this.settings.get(settingsIndex);
        operationSeriesCount = curSettings.getSeries() * curSettings.getSteps() * curSettings.getExtensionDigit() *
                curSettings.getExtensionCount();

    }

    public void resetAllWeights(){
        pairs.forEach(Pair::resetWeight);
    }

    public void resetObligatoryWeights() {
        obligatoryMap.values().forEach(l-> l.forEach(Pair::resetWeight));
    }


    public List<int[]> generateStep() {
        final Settings curSettings = this.settings.get(settingsIndex);


        final int[] digitsForSteps = getDigits(curSettings);


        int stepsAmount = curSettings.getSteps();

        final Pair[] firstPair = getFirstPair(digitsForSteps[0]);
        int[] firstStep = Arrays.stream(firstPair).map(Pair::getUp).mapToInt(value -> value).toArray();
        //todo correct second step
        int[] secondStep = pairToStep(firstPair);
        List<int[]> steps = new ArrayList<>();
        steps.add(firstStep);
        steps.add(secondStep);

        for (int i = 2; i < stepsAmount; i++) {
            final int[] currentSum = Digs.sumWithCut(steps);
            final Pair[] nextStep = getNextStep(currentSum, digitsForSteps[i]);
            //todo correct next step
            int[] step = pairToStep(nextStep);
            steps.add(step);
            if (Digs.getValue(Digs.sum(steps)) == 0) {
                //repeat to exclude zeros
                steps.remove(steps.size() - 1);
                final Pair[] nextStepTwice = getNextStep(currentSum, digitsForSteps[i]);
                final int[] pairToStep = pairToStep(nextStepTwice);
                steps.add(pairToStep);
            }

        }

        return steps;

    }

    /**
     *
     *   1 9 9
     * +   1 1    -> 0 1
     *
     *   1 0 3
     * +   0 9 если младший разряд переполнение старший не должен быть ноль, но может быть 9
     *
     *   1 2 3
     * - 1 9 9    ->  - 0 8 9
     */
    static int[] pairToStep(Pair[] nextStep) {

        //boolean sign = Arrays.stream(nextStep).allMatch(p-> p.getDown() >= 0);
        int prevSum = nextStep[0].getUp() + nextStep[0].getDown();
        //boolean prevCarry = prevSum >10 || prevSum < 0;
        int[] step = new int[nextStep.length];// todo may be trunced length for negative
        step[0] = nextStep[0].getDown();
        for (int digIndex = 1; digIndex < nextStep.length; digIndex++) {
            final int stepDig;
            final int down = (nextStep[digIndex] != null) ? nextStep[digIndex].getDown() : 0;
            if (prevSum >= 10) {
                if (down == 0) {
                    stepDig = 1;//todo check
                } else {
                    stepDig = down - 1;
                }
            } else if (prevSum < 0) {
                if (down == 0) {
                    stepDig = -1;
                } else {
                    stepDig = down + 1;
                }
            } else {
                stepDig = down;

            }
            step[digIndex] = stepDig;
            prevSum = ((nextStep[digIndex] != null) ? nextStep[digIndex].getUp() : 0) + down;
        }
        return step;
    }

    private int[] getDigits(Settings curSettings) {
        final List<Integer> digitsStepList = Stream.generate(() -> 1)
                .limit(curSettings.getSteps())
                .collect(Collectors.toList());
        Stream.iterate(0, n-> n + 1). limit(curSettings.getExtensionCount()).forEach(v-> digitsStepList.set(v, 1 + curSettings.getExtensionDigit()));
        if (curSettings.getExtensionCount() < curSettings.getSteps()) {
            Collections.shuffle(digitsStepList);
        }
        return digitsStepList.stream().mapToInt(v -> v).toArray();
    }

    public Pair[] getFirstPair(int digits) {
        final double allSum = pairs.stream().mapToDouble(Pair::getWeight).sum();
        final double positiveSum = pairs.stream().filter(p->p.getDown() > 0).mapToDouble(Pair::getWeight).sum();
        final double signSelect = r.nextDouble();
        boolean sign = (signSelect > positiveSum / allSum);


        final List<Pair> signPairs = pairs.stream().filter(v -> sign && v.getDown() >= 0 || !sign && v.getDown() <= 0).collect(Collectors.toList());

        Pair[] resultPairs = new Pair[digits];
        boolean negCarry = false;
        for (int digIndex = 0; digIndex < digits; digIndex++) {
            if (digIndex == digits - 1 && !sign ) {
                boolean finalNegCarry = negCarry;
                final List<Pair> filtered = signPairs.stream()
                        .filter(p ->
                                finalNegCarry && (p.getUp() + p.getDown() > 0) || !finalNegCarry && (p.getUp() + p.getDown() >= 0))
                        .collect(Collectors.toList());
                probabilisticPair(resultPairs, digIndex, filtered);
            } else {
                probabilisticPair(resultPairs, digIndex, signPairs);
                final Pair newPair = resultPairs[digIndex];
                final int sum = newPair.getDown() + newPair.getUp();
                negCarry = sum < 0 || (negCarry && sum == 0) ;


            }
        }
        return resultPairs;
    }


    public Pair[] getNextStep(int[] currentValue, int digits) {
        //many positive can cover one negative obligatory?
        final boolean sign = selectSign(currentValue, digits);

        Pair[] resultPairs = new Pair[Math.max(currentValue.length, digits)];

        boolean negCarry = false;
        for (int digIndex = 0; digIndex < digits; digIndex++) {
            int value = currentValue[digIndex];
//            if (negCarry ) {
//                value --;
//            }
            final Stream<Pair> pairStream = valueToPair.get(value).stream()
                    .filter(p -> sign && p.getDown() >= 0 || !sign && p.getDown() <= 0);

            if (digIndex == digits - 1 && !sign ) {
                boolean finalNegCarry = negCarry;
                ;
                final List<Pair> signPairs = pairStream.filter(p -> finalNegCarry && (p.getUp() + p.getDown() > 0) || !finalNegCarry && (p.getUp() + p.getDown() >= 0))
                        .collect(Collectors.toList());
                probabilisticPair(resultPairs, digIndex, signPairs);
            } else {
                final List<Pair> signPairs = pairStream .collect(Collectors.toList());
                probabilisticPair(resultPairs, digIndex, signPairs);
                final Pair newPair = resultPairs[digIndex];
                final int sum = newPair.getDown() + newPair.getUp();
                negCarry = sum < 0 || (negCarry && sum == 0);
            }
        }

        return resultPairs;
    }


    private void probabilisticPair(Pair[] resultPairs, int digIndex, List<Pair> signPairs) {
        final double sumAllWeights = signPairs.stream()
                .mapToDouble(Pair::getWeight)
                .sum();

        final double select = r.nextDouble();
        double weightSum = 0;
        int resultIndex = signPairs.size() - 1;
        for (int j = 0; j < signPairs.size(); j++) {
            weightSum += signPairs.get(j).getWeight();
            final double currentWeight = weightSum / sumAllWeights;
            //todo the left digit should depend on carry from previus digit 145 - 99 ->  46
            //todo carry if carry is true and current down is zero then next should not zero ...
            if (currentWeight > select) {
                resultIndex = j;
                break;
            }
        }
        if (signPairs.size() == 0) {
            System.out.println("empty pairs");
            resultPairs[digIndex] = null;
            return;
        }
        final Pair pair = signPairs.get(resultIndex);
        pair.inc();
        resultPairs[digIndex] = pair;
    }


    private boolean selectSign(int[] currentValue, int digits) {
        final boolean hasPositive = Arrays.stream(currentValue)
                .limit(digits)
                .mapToObj(value -> valueToPair.get(value))
                .allMatch(l -> l.stream().anyMatch(v -> v.getDown() >= 0));

        final boolean hasNegative = Arrays.stream(currentValue)
                .limit(digits)
                .mapToObj(value -> valueToPair.get(value))
                .allMatch(l -> l.stream().anyMatch(v -> v.getDown() <= 0));

        final boolean isZero = Digs.getValue(currentValue) == 0;
        if (isZero ) {
            System.out.println("Found zero values");
            return true;
        }
        final boolean sign;
        if (hasNegative && hasPositive) {
            final double posSum = Arrays.stream(currentValue)
                    .limit(digits)
                    .mapToDouble(value -> valueToPair.get(value).stream()
                            .filter(v -> v.getDown() >= 0)
                            .mapToDouble(Pair::getWeight)
                            .sum())
                    .sum();

            final double negSum = Arrays.stream(currentValue)
                    .limit(digits)
                    .mapToDouble(value -> valueToPair.get(value).stream()
                            .filter(v -> v.getDown() <= 0)
                            .mapToDouble(Pair::getWeight)
                            .sum())
                    .sum();
            sign = r.nextDouble() > posSum / (posSum + negSum);
        } else {
            sign = hasPositive;
        }
        return sign;
    }
}
