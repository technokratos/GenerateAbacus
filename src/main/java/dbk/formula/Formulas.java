package dbk.formula;

import dbk.abacus.Tuple2;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static dbk.formula.AbacusNumber.of;
import static java.lang.Integer.parseInt;
import static java.lang.Math.abs;

/**
 * Created by denis on 27.04.17.
 */
public class Formulas {

    //Pattern pattern= Pattern.compile("(\\+|-)(\\d*)(\\+|-)?(\\d)?(\\+|-)?(\\d)?");
    static Pattern pattern= Pattern.compile("([+-])(\\d+)([+-])?(\\d+)?([+-])?(\\d+)?");

    //private final TreeSet<String> formulas = new TreeSet<>((o1, o2) -> o1.length() - o2.length() == 0? o1.compareTo(o2) : o1.length() - o2.length() );
    private final TreeSet<String> formulas = new TreeSet<>(new FormulaComparator());
    private final Map<String, List<Tuple2<Integer,Integer>>> formulaToOperands = new HashMap<>();
    private final Map<Integer, Set<String>> numberToFormula = new HashMap<>();

    private final Map<AbacusNumber, TreeSet<String>> numberPositiveFormulas = new HashMap<>();
    private final Map<AbacusNumber, TreeSet<String>> numberNegativeFormulas = new HashMap<>();
    private final Map<Tuple2<String, AbacusNumber>, TreeSet<AbacusNumber>> currentNumberToNext = new HashMap<>();
    private final String[][] table = new String[20][10];

    private final static Formulas instance = getFormulaTable();

    /**
     *
     *    0,1,2 ... 9
     *
     * -9
     * -8
     * .
     * .
     * -2
     * -1
     * +1
     * +2
     * .
     * +9
     *
     * @return
     */
    public static Formulas getFormulaTable(){

        Formulas formulas = new Formulas();

        String[][] table = formulas.table;//new String[20][10];

        for (int i = 0; i <10 ; i++) {
            for (int j = 0; j < 10; j++) {
                AbacusNumber first = of(i);
                final AbacusNumber second = of(j);
                final AbacusResult plusResult = of(i).add(second);
                AbacusNumber firstPlus = plusResult.getResult();
                final AbacusResult minusResult = first.minus(second);
                AbacusNumber firstMinus = minusResult.getResult();


                boolean carryPlus = plusResult.isCarry();
                boolean carryMinus = minusResult.isCarry();


                final String minusFomula = minusResult.getOperation();
                table[10 - j][i] = minusFomula;
                final String plusFormula = plusResult.getOperation();
                table[10 + j][i] = plusFormula;


                formulas.formulas.add(minusFomula);
                formulas.formulas.add(plusFormula);


                Set<String> set = new HashSet<>();
                set.add(plusFormula);
                set.add(minusFomula);
                formulas.numberToFormula.merge(i, set, (oldValue, newValue) -> {
                    oldValue.addAll(set);
                    return oldValue;
                });

                final TreeSet<String> positiveFormulas = formulas.numberPositiveFormulas.computeIfAbsent(first, abacusNumber -> new TreeSet<>(new FormulaComparator()));
                positiveFormulas.add(plusFormula);
                final TreeSet<String> negativeFormulas = formulas.numberNegativeFormulas.computeIfAbsent(first, abacusNumber -> new TreeSet<>(new FormulaComparator()));
                negativeFormulas.add(minusFomula);

                final Tuple2<String, AbacusNumber> plusTuple = Tuple2.of(plusFormula, first);
                final TreeSet<AbacusNumber> formulaAndNumber = formulas.currentNumberToNext
                        .computeIfAbsent(plusTuple, t -> new TreeSet<>());
                formulaAndNumber.add(second);
                final Tuple2<String, AbacusNumber> minusTuple = Tuple2.of(minusFomula, first);
                final TreeSet<AbacusNumber> minusFormulaAndNumber = formulas.currentNumberToNext
                        .computeIfAbsent(minusTuple, t -> new TreeSet<>());
                minusFormulaAndNumber.add(second);

//                formulas.numberToFormula.computeIfPresent(i,(integer, strings) -> {
//                    strings.add(plusFormula);
//                    strings.add(minusFomula);
//                    return strings;});
//                formulas.numberToFormula.computeIfAbsent(i, integer -> {
//                    Set<String> set = new HashSet<>();
//                    set.add(plusFormula);
//                    set.add(minusFomula);
//                    return set;
//                });

                final ArrayList<Tuple2<Integer, Integer>> value = new ArrayList<>();
                value.add(Tuple2.of(i, j));
                formulas.formulaToOperands.merge(plusFormula, value, (oldValue, newValue) -> {
                    oldValue.addAll(value);
                    return oldValue;
                });
                final ArrayList<Tuple2<Integer, Integer>> valueMinus = new ArrayList<>();
                valueMinus.add(Tuple2.of(i, -j));
                formulas.formulaToOperands.merge(minusFomula, valueMinus, (oldValue, newValue) -> {
                    oldValue.addAll(newValue);
                    return oldValue;
                });

//                formulas.formulaToOperands.computeIfPresent(minusFomula, (formula, tuple2s) -> {
//                    tuple2s.add(new Tuple2<>(a, -b));
//                    return tuple2s;
//                } );
//                formulas.formulaToOperands.computeIfPresent(plusFormula, (formula, tuple2s) -> {
//                    tuple2s.add(new Tuple2<>(a, b));
//                    return tuple2s;
//                } );
//                formulas.formulaToOperands.computeIfAbsent(minusFomula, formula ->{
//                    List<Tuple2<Integer, Integer>>
//                } );
//                formulas.formulaToOperands.computeIfPresent(plusFormula, (formula, tuple2s) -> {
//                    tuple2s.add(new Tuple2<>(a, b));
//                    return tuple2s;
//                } );




//                if (i > j){
//                    final String plusFormula = first.getFormula(second, carryPlus, true);
//                    formulas[j + 10][i] = plusFormula;
//                    System.out.println(i + " + " + j + " " + plusFormula);
//                } else {
//                    final String minusFormula = first.getFormula(second, carryMinus, false);
//                    formulas[10 - j][i] = minusFormula;
//                    System.out.println( i + " - " + j + " " + minusFormula);
//                }




//                valueMinus[j + 10][i] = i + "+" + j;
//                valueMinus[10 - j][i] = i + "-" + j;
            }

        }
//        final String collect = Stream.of(value)
//                .map(row -> Stream.of(row)
//                        .map(formula-> formula)
//                        .collect(Collectors.joining(",", "[", "]")))
//                .collect(Collectors.joining("\n"));

        //System.out.println(collect);
        formulas.formulas.remove("");
        return formulas;
    }


    public TreeSet<String> getFormulas() {
        return formulas;
    }

    public String[][] getTable() {
        return table;
    }

    public static Formulas getInstance() {
        return instance;
    }


    public Map<Tuple2<String, AbacusNumber>, TreeSet<AbacusNumber>> getCurrentNumberToNext() {
        return currentNumberToNext;
    }

    public Map<AbacusNumber, TreeSet<String>> getNumberPositiveFormulas() {
        return numberPositiveFormulas;
    }

    public Map<AbacusNumber, TreeSet<String>> getNumberNegativeFormulas() {
        return numberNegativeFormulas;
    }

    public List<Tuple2<Integer,Integer>> getOperandsForFormula(String formula) {
        return formulaToOperands.get(formula);
    }

    public Set<String> getFormulasForOperand(Integer operand) {
        return numberToFormula.get(operand);
    }

    public static class FormulaComparator implements Comparator<String>{


        @Override
        public int compare(String o1, String o2) {
            return order(o1) - order(o2);
        }

        private int order(String o1) {
            if ("".equals( o1)) {
                return 0;
            }
            final net.objecthunter.exp4j.Expression expression = new ExpressionBuilder(o1).build();
            final int value = (int) expression.evaluate();

            int sign = value < 0 ? 1 : 0;
            int length = o1.length();
            final int res = abs(value) * 10 + sign + length * 100;
            return res;

//            final Matcher m1 = pattern.matcher(o1);
//
//            int[] w3 = new int[]{100, 1000, 1000, 10000, 100000,1000000};
//            int[] w2 = new int[]{1000, 10000, 100000,1000000};
//            int[] w1 = new int[]{100000, 1000000};
//            int[] w = w3;
//            if (m1.find()) {
//                //boolean s1 = "+".equals( o1.substring(0, 1) );
//                int s1 = sign(m1, 1);
//                int s2 = sign(m1, 3);
//                int s3 = sign(m1, 5);
//                int v1 = value(m1, 2);
//                int v2 = value(m1, 4);
//                int v3 = value(m1, 6);
//                int mux = 10;
//                if (m1.group(4) == null) {
//                    w = shift(w, 4);
//                    mux = 1
//                } else if (m1.group(6) == null) {
//                    w = shift(w,2);
//                }
//                int[] values = new int[]{s1, v1, s2, v2, s3, v3};
//
//                final int mult = (int) (mult(w, values) * Math.pow(10, w.length));
//                return mult ;
//
//            } else {
//                return 0;
//            }
        }

        private int mult(int[] w, int[] values) {
            return IntStream.range(0, Math.min(w.length, values.length))
                    .map(i -> w[i] * values[i])
                    .sum();
        }

        private int[] shift(int[] w, int pos) {
            int newW[] = new int[w.length - pos];
            System.arraycopy(w, pos, newW, 0, w.length - pos );
            return newW;
        }

        private int sign(Matcher m1, int pos) {
            return "+".equals(m1.group(pos)) ? 0 : 1;
        }

        private int value(Matcher m, int pos) {
            final String group = m.group(pos);

            if (group == null) {return 0;}
            else {
                final int value = parseInt(group);
                return value == 10 || value == 5 ? 1: value;
            }
        }
    }


}
