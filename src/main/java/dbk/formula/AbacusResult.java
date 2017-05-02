package dbk.formula;

/**
 * Created by denis on 30.04.17.
 */
public class AbacusResult {
    private final AbacusNumber result;
    private final boolean carry;
    private final String operation;

    public AbacusResult(AbacusNumber result, boolean carry, String operation) {
        this.result = result;
        this.carry = carry;
        this.operation = operation;
    }

    public AbacusNumber getResult() {
        return result;
    }

    public boolean isCarry() {
        return carry;
    }

    public String getOperation() {
        return operation;
    }

    @Override
    public String toString() {
        return "{" +
                "result=" + result +
                ", carry=" + carry +
                ", operation='" + operation + '\'' +
                '}';
    }
}
