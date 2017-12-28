package dbk.generator.probability;

import java.util.function.BiFunction;

public enum Filters implements BiFunction<Matrix.Context, Matrix.Cell, Boolean> {

    OP_NOT_ZERO {
        @Override
        public Boolean apply(Matrix.Context context, Matrix.Cell cell) {
            return cell.operand != 0;
        }
    },
    SUM_G_ZERO {
        @Override
        public Boolean apply(Matrix.Context context, Matrix.Cell cell) {
            return cell.operand + context.prevValue > 0;
        }
    },
    OPERAND_LE_ZERO {
        @Override
        public Boolean apply(Matrix.Context context, Matrix.Cell cell) {
            return cell.operand <= 0;
        }
    },
    OPERAND_GE_ZERO {
        @Override
        public Boolean apply(Matrix.Context context, Matrix.Cell cell) {
            return cell.operand >= 0;
        }
    },
    LIMIT_LAST_DIGITS_CARRY {
        @Override
        public Boolean apply(Matrix.Context context, Matrix.Cell cell) {
            int pos = context.maxDig - context.dig - 1;
            return pos > 0 || (pos == 0 && cell.resultCarry == Carry.NONE);
        }
    },
    LIMIT_PLUS_LAST_DIGITS_CARRY {
        @Override
        public Boolean apply(Matrix.Context context, Matrix.Cell cell) {
            int pos = context.maxDig - context.dig - 1;
            return pos > 0 || (pos == 0 && cell.resultCarry == Carry.NONE);
        }
    }




    /*
    public static final BiFunction<Context, Cell, Boolean> SUM_G_ZERO = (context, cell) -> cell.operand + context.prevValue >= 0;
    public static final BiFunction<Context, Cell, Boolean> OPERAND_LE_ZERO = (context, cell) -> cell.operand >= 0;
    public static final BiFunction<Context, Cell, Boolean> OPERAND_GE_ZERO = (context, cell) -> cell.operand <= 0;
     */
}
