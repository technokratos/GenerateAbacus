package dbk.formula;

import dbk.abacus.Tuple2;

import java.util.*;

import static dbk.formula.AbacusNumber.abacus;

/**
 * Created by denis on 27.04.17.
 */
public class Formulas {


    private final TreeSet<String> formulas = new TreeSet<>((o1, o2) -> o1.length() - o2.length() == 0? o1.compareTo(o2) : o1.length() - o2.length() );
    private final Map<String, List<Tuple2<Integer,Integer>>> formulaToOperands = new HashMap<>();
    private final Map<Integer, Set<String>> firstOperandToFormula = new HashMap<>();

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
                AbacusNumber first = abacus(i);
                final AbacusResult plusResult = abacus(i).add(abacus(j));
                AbacusNumber firstPlus = plusResult.getResult();
                final AbacusResult minusResult = first.minus(abacus(j));
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
                formulas.firstOperandToFormula.merge(i, set, (oldValue, newValue) -> {
                    oldValue.addAll(set);
                    return oldValue;
                });

//                formulas.firstOperandToFormula.computeIfPresent(i,(integer, strings) -> {
//                    strings.add(plusFormula);
//                    strings.add(minusFomula);
//                    return strings;});
//                formulas.firstOperandToFormula.computeIfAbsent(i, integer -> {
//                    Set<String> set = new HashSet<>();
//                    set.add(plusFormula);
//                    set.add(minusFomula);
//                    return set;
//                });

                final ArrayList<Tuple2<Integer, Integer>> value = new ArrayList<>();
                value.add(Tuple2.create(i, j));
                formulas.formulaToOperands.merge(plusFormula, value, (oldValue, newValue) -> {
                    oldValue.addAll(value);
                    return oldValue;
                });
                final ArrayList<Tuple2<Integer, Integer>> valueMinus = new ArrayList<>();
                valueMinus.add(Tuple2.create(i, -j));
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




    public List<Tuple2<Integer,Integer>> getOperandsForFormula(String formula) {
        return formulaToOperands.get(formula);
    }

    public Set<String> getFormulasForOperand(Integer operand) {
        return firstOperandToFormula.get(operand);
    }

}
