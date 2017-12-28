package dbk.generator.probability;

import dbk.generator.types.Step;
import dbk.rand.RandomLevel;

import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Matrix {
    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    final TreeMap<Integer, Column> columnsMap = new TreeMap<>();


    Function<Cell, Double> weightFunction = cell -> (double) cell.weight;
    Consumer<Cell> updateWeight = cell -> {
        double cellWeight = 1.0 / (1 + cell.count);
        cell.weight = cellWeight;
    };

    List<Filters> filters =  new ArrayList<>();

    private boolean debug = true;


    public Matrix() {

    }


    public int getNextValue(int currentValue, Context context) throws ProbabilisticException {

        Cell cell = getNextCell(currentValue, context);
        cell.incCount();
        return cell.operand;

    }

    public double getWeightSum(Step step, int maxDig) {

        double sum = 0;
        boolean isZeroWeight = false;
        for (int i = 0; i < maxDig; i++) {
            int value = step.get(i);
            Column column = columnsMap.get(value);
            if (column != null) {
                double sumWeight = column.getSumWeight(new Context(0, value, i, maxDig));
                isZeroWeight |= (sumWeight == 0);
                sum+= sumWeight;
            } else {
                isZeroWeight |= true;
            }
        }

        return isZeroWeight ? 0 : sum;

    }

    public Cell getNextCell(int currentValue, Context context) throws ProbabilisticException {
        Column column = columnsMap.get(currentValue);
        if (column == null) {
            throw new ProbabilisticException("Absent column for value " + currentValue);
        }
        Stream<Cell> stream = column.cells.stream();
        double sumWeight = column.getSumWeight(context);
//        double sum = stream
//                .filter(c-> filters.stream()
//                        .allMatch(f-> f.apply(context, c)))
//                .mapToDouble(weightFunction::apply)
//                .sum();
//        column.sumWeight = sum;
//        double sumOfValue = sum;

        double rand = RandomLevel.nextValue(sumWeight);

        return column.getNextValue(rand, context);
    }

//    public Cell getNextCell(int currentValue, Context context) throws ProbabilisticException {
//        Column column = columnsMap.get(currentValue);
//        if (column == null) {
//            throw new ProbabilisticException("Absent column for value " + currentValue);
//        }
//        double sumOfValue = column.getSumWeight(context);
//        double rand = RandomLevel.nextValue(sumOfValue);
//
//        return column.getNextValue(rand, context);
//    }

    public Column getColumn(int value) {
        return columnsMap.computeIfAbsent(value, i -> new Column(value));
    }

    public int count() {
        return columnsMap.values().stream()
                .mapToInt(Column::count)
                .sum();
    }

    public void updateWeight() {
        columnsMap.values()
                .forEach(column -> column.cells
                        .forEach(c -> updateWeight.accept(c)));
    }
    public void resetWeightsAndCounts() {
        columnsMap.values()
                .forEach(column -> column.cells
                        .forEach(c -> {
                            c.count = 0;
                            c.weight = c.initialWeight;
                        }));
    }

    public Statistic getStatistic() {
        double[] counts = columnsMap.values().stream()
                .flatMap(column -> column.cells.stream())
                .mapToDouble(c -> c.count).toArray();
        double commonSum = Arrays.stream(counts).sum();
        double med = commonSum / counts.length;
//        System.out.println("Medium " + med);

        int cellsNumber = columnsMap.values().stream()
                .mapToInt(c -> c.cells.size())
                .sum();
        double dispN = Arrays.stream(counts).map(c -> c - med)
                .map(c1 -> c1 * c1)
                .sum();
        dispN /= counts.length - 1;
        double sigma = Math.sqrt(dispN);
//        System.out.println("sigma " + sigma);
//        System.out.println("sigma/med " + sigma / Math.sqrt(med));
        return new Statistic((int) commonSum, med, sigma, cellsNumber);

    }




    public Matrix setFilters(List<Filters> filters) {
        this.filters = filters;
        return this;
    }

    public void addFilter(Filters filter) {
        this.filters.add(filter);
    }

    public void removeFilter(Filters filter) {
        this.filters.remove(filter);
    }

    public boolean signMatch(Context context, boolean sign){
        Column column = columnsMap.get(context.prevValue);
        if (column == null) {
            System.out.println("Error! Absent column for value " + context.prevValue);
            return false;
        }
        Stream<Cell> cellStream = column.cells.stream()
                .filter(c -> filters.stream()
                        .allMatch(f -> f.apply(context, c)));
        if (debug) {
            List<Cell> collect = cellStream.collect(Collectors.toList());
            cellStream = collect.stream();
        }
        return cellStream
                .anyMatch(c -> sign && (c.operand >=0) || !sign && (c.operand <=0));
    }

    class Column {

        private final int value;
        private double sumWeight;
        List<Cell> cells = new ArrayList<>();

        Cell addCell(int operand, int value) {
            Cell cell = new Cell(operand, value, 1, this);
            cells.add(cell);
            return cell;
        }

        Column(int value) {
            this.value = value;
        }

        public double getSumWeight(Context context) {
            Stream<Cell> stream = cells.stream();

            double sum = stream
                    .filter(c-> filters.stream()
                            .allMatch(f-> f.apply(context, c)))
                    .mapToDouble(weightFunction::apply)
                    .sum();
            this.sumWeight = sum;
            return sum;
        }

        // 1, 1, 1 - 3
        // 0.99 -> 2.97
        //
        public Cell getNextValue(double rand, Context context) throws ProbabilisticException {
            double sum = 0;
            Cell cell;

            List<Cell> filtered = cells.stream()
                    .filter(c-> filters.stream()
                            .allMatch(f-> f.apply(context, c)))
                    .collect(Collectors.toList());


            for (int i = 0; i < filtered.size(); i++) {
                cell = filtered.get(i);
                sum += weightFunction.apply(cell);
                if (sum >= rand) {
                    return cell;
                }
            }
            throw new ProbabilisticException(String.format("Not found cells for column value %d, rand %f, values %s",
                    value, rand, filtered.stream()
                            .map(weightFunction)
                            .map(DECIMAL_FORMAT::format)
                            .collect(Collectors.joining())));
        }

        @Override
        public String toString() {
            return "Column{" +
                    "cells=" + cells +
                    '}' + "\n";
        }

        public int count() {
            return cells.stream()
                    .mapToInt(Cell::count)
                    .sum();
        }


    }



    class Cell {
        int count = 0;
        final int operand;
        final double initialWeight;
        double weight;
        final Column column;

        final int resultValue;
        final Carry resultCarry;


        Cell(int operand, int value, double initialWeight, Column column) {
            this.operand = operand;
            this.initialWeight = initialWeight;
            this.weight = initialWeight;
            this.column = column;
            int nextValue = value + operand;
            if (nextValue >= 10) {
                nextValue -= 10;
                resultCarry = Carry.PLUS;
            } else if (nextValue < 0) {
                nextValue += 10;
                resultCarry = Carry.MINUS;
            } else {
                resultCarry = Carry.NONE;
            }
            this.resultValue = nextValue;

        }

        public void incCount() {
            count++;
        }

        @Override
        public String toString() {
            return "Cell{" +
                    "count=" + count +
                    ", operand=" + operand +
                    '}';
        }

        public int count() {
            return count;
        }
    }

    class Statistic {

        final int commonCount;
        final double med;
        final double sigma;
        final int cellsNumber;

        Statistic(int commonCount, double med, double sigma, int cellsNumber) {

            this.commonCount = commonCount;
            this.med = med;
            this.sigma = sigma;
            this.cellsNumber = cellsNumber;
        }

        double getRelativeSigma() {
            return sigma / Math.sqrt(med);
        }
    }

    static class Context {

        final int maxStep;
        final int prevValue;
        final int dig;
        final int maxDig;


        Context(int maxStep, int prevValue, int dig, int maxDig) {
            this.maxStep = maxStep;
            this.prevValue = prevValue;
            this.dig = dig;
            this.maxDig = maxDig;
        }

    }

    @Override
    public String toString() {
        return "Matrix{" +
                "columnsMap=" + columnsMap +
                '}';
    }

    public String toStringWithCount() {
        return columnsMap.entrySet()
                .stream().map(m -> m.getKey() + ":" + m.getValue().cells
                        .stream()
                        .filter(c -> c.count > 0)
                        .map(c -> String.valueOf(c.operand) + " -> " + c.count)
                        .collect(Collectors.joining("; ")))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining("\n"));

    }

    public static class BigContext  {
        public final int currentStep;
        public final int restSteps;
        public final Step sum;
        public final int maxDigits;
        public final int maxStep;

        public BigContext(int currentStep, int restSteps, Step sum, int maxDigits, int maxStep) {
            this.currentStep = currentStep;
            this.restSteps = restSteps;
            this.sum = sum;
            this.maxDigits = maxDigits;
            this.maxStep = maxStep;
        }
    }

}
