package dbk.generator.probability;

import dbk.generator.types.Step;
import dbk.rand.RandomLevel;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;


public class MatrixTest {


    @Test
    public void getNextBigValueSingleDigit() throws ProbabilisticException {
        RandomLevel.setR(4);
        Matrix matrix = initMatrix();

        List<Step> list = new ArrayList<>();
        int digits = 1;
        Step sum = new Step(digits);

        int secondDigit = 0;
        int maxStep = 10;

        for (int i = 0; i < maxStep; i++) {

            Matrix.BigContext bigContext = new Matrix.BigContext(i, maxStep - i, sum, digits, maxStep);
            Step nexValue = getNextBigValue(matrix, bigContext);
            sum = sum.addWithLoan(nexValue.getValues());
            System.out.println(nexValue + " -> " + sum);
            list.add(nexValue);

            matrix.updateWeight();


        }

        System.out.println(matrix.toStringWithCount());

        Matrix.Statistic stat = matrix.getStatistic();
        int commonSum = stat.commonCount;
        assertThat(commonSum).isEqualTo(maxStep);
        System.out.println("Medium " + stat.med);
        double expectedMed = maxStep / stat.cellsNumber;
        assertThat(stat.med).isBetween(expectedMed -1 , expectedMed +1);

        double relativeSigma = stat.getRelativeSigma();
        System.out.println("sigma/med " + relativeSigma);
        assertThat(relativeSigma).isLessThanOrEqualTo(1);

    }



    @Test
    public void getNextBigValue() throws ProbabilisticException {
        RandomLevel.setR(2);
        Matrix matrix = initMatrix();

        List<Step> list = new ArrayList<>();
        int digits = 2;
        Step sum = new Step(digits);

        int secondDigit = 0;
        int maxStep = 10;

        for (int i = 0; i < maxStep; i++) {

            Matrix.BigContext bigContext = new Matrix.BigContext(i, maxStep - i, sum, digits, maxStep);
            Step nexValue = getNextBigValue(matrix, bigContext);
            sum = sum.addWithLoan(nexValue.getValues());
            System.out.println(nexValue + " -> " + sum);
            list.add(nexValue);
            matrix.updateWeight();
        }

        //System.out.println(matrix.toStringWithCount());

        Matrix.Statistic stat = matrix.getStatistic();
        int commonSum = stat.commonCount;
        assertThat(commonSum).isEqualTo(maxStep);
        System.out.println("Medium " + stat.med);
        double expectedMed = maxStep / stat.cellsNumber;
        assertThat(stat.med).isBetween(expectedMed -1 , expectedMed +1);

        double relativeSigma = stat.getRelativeSigma();
        System.out.println("sigma/med " + relativeSigma);
        assertThat(relativeSigma).isLessThanOrEqualTo(1);

    }

    public static Step getNextBigValue(Matrix matrix, Matrix.BigContext bigContext) throws ProbabilisticException {
        List<Filters> plusFilters = Arrays.asList(Filters.OP_NOT_ZERO, Filters.OPERAND_GE_ZERO, Filters.LIMIT_LAST_DIGITS_CARRY);
        List<Filters> minusFilters = Arrays.asList(Filters.OP_NOT_ZERO, Filters.OPERAND_LE_ZERO, Filters.SUM_G_ZERO, Filters.LIMIT_LAST_DIGITS_CARRY);
        List<Filters> minusFiltersWithCarry = Arrays.asList(Filters.OP_NOT_ZERO, Filters.OPERAND_LE_ZERO);

        if (bigContext.currentStep > 0  && bigContext.currentStep < bigContext.maxStep - 1) {
            minusFilters = minusFiltersWithCarry;
        }

        Step sum = bigContext.sum;

        double plusWeight = matrix.setFilters(plusFilters)
                .getWeightSum(sum, bigContext.maxDigits);
        double minusWeight = matrix.setFilters(minusFilters)
                .getWeightSum(sum, bigContext.maxDigits);
        double signWeight = RandomLevel.nextValue(plusWeight + minusWeight);
        boolean sign = signWeight <= plusWeight;

        if(sign) {
            matrix.setFilters(plusFilters);
        } else {
            matrix.setFilters(minusFilters);
        }
        Step step = new Step(bigContext.maxDigits);

        for (int i = 0; i < bigContext.maxDigits; i++) {
            int sumDig = sum.get(i);
            int nextValue;
            try {
                nextValue = matrix.getNextValue(sumDig, new Matrix.Context(0, sumDig, i, bigContext.maxDigits));
            } catch (ProbabilisticException e) {
                System.out.println(String.format("exception for sumDig %d, dig %d, reason %s", sumDig, i, e.getMessage()));
                nextValue = 0;
            }

            step.set(i, nextValue);;
        }
        return step;
    }


    @Test
    public void getNextValuePositive() throws Exception {
//        RandomLevel.setR(1);

        Matrix matrix = initMatrix();

        matrix.addFilter(Filters.OPERAND_GE_ZERO);
        List<Integer> list = new ArrayList<>();
        int sum = 0;
        int secondDigit = 0;
        int len = 10;
        for (int i = 0; i < len; i++) {
            int nexValue = matrix.getNextValue(sum, new Matrix.Context(len - i, 0, 0, 1));
            sum+= nexValue;
            if (sum >= 10) {
                secondDigit += 1;
                sum -= 10;
            } else if (sum < 0) {
                secondDigit -=1;
                sum +=10;
            }
            System.out.println(nexValue + " -> " + sum);
            list.add(nexValue);

            matrix.updateWeight();


        }

        System.out.println(matrix.toStringWithCount());

        Matrix.Statistic stat = matrix.getStatistic();
        int commonSum = stat.commonCount;
        assertThat(commonSum).isEqualTo(len);
        System.out.println("Medium " + stat.med);
        double expectedMed = len / stat.cellsNumber;
        assertThat(stat.med).isBetween(expectedMed -1 , expectedMed +1);

        double relativeSigma = stat.getRelativeSigma();
        System.out.println("sigma/med " + relativeSigma);
        assertThat(relativeSigma).isLessThanOrEqualTo(1);

    }

    @Test
    public void getNexValue() throws Exception {
//        RandomLevel.setR(1);

        Matrix matrix = initMatrix();

        //matrix.setCells
        //matrix.setStrategy
        //MatrixWrapper(matrix)
        //with positive result
        //with positive result to the end
        //available negative result
        //-----
        //get next value for n-maxDigits
        //series

        List<Integer> list = new ArrayList<>();
        int sum = 0;
        int secondDigit = 0;
        int len = 10;
        for (int i = 0; i < len; i++) {
            int nexValue = matrix.getNextValue(sum, null);
            sum+= nexValue;
            if (sum >=10) {
                secondDigit += 1;
                sum -= 10;
            } else if (sum<0) {
                secondDigit -=1;
                sum +=10;
            }
            System.out.println(nexValue + " -> " + sum);
            list.add(nexValue);

            matrix.updateWeight();


        }

        System.out.println(matrix.toStringWithCount());

        Matrix.Statistic stat = matrix.getStatistic();
        int commonSum = stat.commonCount;
        assertThat(commonSum).isEqualTo(len);
        System.out.println("Medium " + stat.med);
        double expectedMed = len / stat.cellsNumber;
        assertThat(stat.med).isBetween(expectedMed -1 , expectedMed +1);

        double relativeSigma = stat.getRelativeSigma();
        System.out.println("sigma/med " + relativeSigma);
        assertThat(relativeSigma).isLessThanOrEqualTo(1);

    }


    @Test
    public void checkStatistic() throws Exception {
        Matrix matrix = initMatrix();

        List<Integer> list = new ArrayList<>();
        int sum = 0;
        int secondDigit = 0;
        int len = 100000;
        for (int i = 0; i < len; i++) {
            int nexValue = matrix.getNextValue(sum, null);
            sum+= nexValue;
            if (sum >=10) {
                secondDigit += 1;
                sum -= 10;
            } else if (sum<0) {
                secondDigit -=1;
                sum +=10;
            }
            //System.out.println(nexValue + " -> " + prevValue);
            list.add(nexValue);

            matrix.updateWeight();


        }

        System.out.println(matrix.toStringWithCount());

        Matrix.Statistic stat = matrix.getStatistic();
        int commonSum = stat.commonCount;
        assertThat(commonSum).isEqualTo(len);
        System.out.println("Medium " + stat.med);
        double expectedMed = len / stat.cellsNumber;
        assertThat(stat.med).isBetween(expectedMed -1 , expectedMed +1);

        double relativeSigma = stat.getRelativeSigma();
        System.out.println("sigma/med " + relativeSigma);
        assertThat(relativeSigma).isLessThanOrEqualTo(1);

    }


    @Test
    public void testNextValueInColumnWithFilter() throws ProbabilisticException {
        Matrix matrix = initMatrix();
        matrix.addFilter(Filters.OPERAND_GE_ZERO);
        Matrix.Column column = matrix.getColumn(5);
        Matrix.Context context = new Matrix.Context(0, 0, 0, 1);
        double sumWeight = column.getSumWeight(context);
        assertThat(sumWeight).isEqualTo(10);
        Matrix.Cell firstCell = column.getNextValue(1, context);
        firstCell.incCount();
        assertThat(firstCell.operand).isEqualTo(0);
        assertThat(column.getNextValue(8.1, context).operand).isEqualTo(8);
        Matrix.Cell plusNine = column.getNextValue(9.5, context);
        plusNine.incCount();
        assertThat(plusNine.operand).isEqualTo(9);
        assertThat(matrix.count()).isEqualTo(2);
        assertThat(matrix.columnsMap.get(5).cells.get(9).count).isEqualTo(1);
        assertThat(matrix.columnsMap.get(5).cells.get(18).count).isEqualTo(1);
    }

    @Test
    public void testNextValueInColumn() throws ProbabilisticException {
        Matrix matrix = initMatrix();
        Matrix.Column column = matrix.getColumn(5);
        double sumWeight = column.getSumWeight(null);
        assertThat(sumWeight).isEqualTo(19);
        Matrix.Cell cellMinusNine = column.getNextValue(1, null);
        cellMinusNine.incCount();
        assertThat(cellMinusNine.operand).isEqualTo(-9);
        assertThat(column.getNextValue(17.8, null).operand).isEqualTo(8);
        Matrix.Cell plusNine = column.getNextValue(18.5, null);
        plusNine.incCount();
        assertThat(plusNine.operand).isEqualTo(9);
        assertThat(matrix.count()).isEqualTo(2);
        assertThat(matrix.columnsMap.get(5).cells.get(0).count).isEqualTo(1);
        assertThat(matrix.columnsMap.get(5).cells.get(18).count).isEqualTo(1);
    }

    public Matrix initMatrix() {
        Matrix matrix = new Matrix();

        for (int value = 0; value <= 9; value++) {
            Matrix.Column column = matrix.getColumn(value);
            for (int j = -9; j <= 9; j++) {
                column.addCell(j, value);
            }
        }
        return matrix;
    }

}