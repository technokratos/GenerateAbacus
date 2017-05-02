package dbk.formula;

import org.junit.Test;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dbk.formula.AbacusNumber.abacus;

/**
 * Created by denis on 28.04.17.
 */
public class FormulaTableTest {
    @Test
    public void getFormulaTable() throws Exception {

        final Formulas formulaTable = Formulas.getFormulaTable();
        final String[][] table = formulaTable.getTable();



//        final String[][] formulaTable2 = new String[table[0].length][table.length];
//        for (int i = 0; i < formulaTable2.length; i++) {
//            for (int j = 0; j < formulaTable2[i].length; j++) {
//                formulaTable2[i][j] = table[j][i];
//            }
//        }

        final String collect = Stream.of(table)
                .map(row -> Stream.of(row)
                        .filter(Objects::nonNull)
                        .map(this::normLength)
                        .collect(Collectors.joining(",", "[", "]")))
                .collect(Collectors.joining("\n"));

        System.out.println(collect);
    }

    private String normLength(String value) {
        String str = "" + value;
        int countOfSpaces = 7 - str.length();
        return str+Stream.generate(() -> " ").limit(countOfSpaces).collect(Collectors.joining());
    }

    @Test
    public void setFormula(){
        String[][] formulas = new String[9][18];
        AbacusNumber first = abacus(4);
        AbacusNumber firstPlus = abacus(4);
        AbacusNumber firstMinus = abacus(4);

        AbacusNumber second = abacus(3);
//        boolean carryPlus = firstPlus.add(second);
//        boolean carryMinus = firstMinus.minus(second);
//
//        final String plusFormula = first.getFormula(second, carryPlus, true);
//        final String minusFormula = first.getFormula(second, carryMinus, false);
//
//
//        formulas[3 + 10][4] = plusFormula;
//        formulas[10 - 3][4] = minusFormula;
    }

}