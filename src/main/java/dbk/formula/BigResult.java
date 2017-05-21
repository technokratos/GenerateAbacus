package dbk.formula;

import java.util.Arrays;

/**
 * Created by denis on 09.05.17.
 */
public class BigResult {

    private final BigAbacusNumber result;
    private final String[] operations;

    public BigResult(BigAbacusNumber result, String[] operations) {
        this.result = result;
        this.operations = operations;
    }

    public BigAbacusNumber getResult() {
        return result;
    }

    public String[] getOperations() {
        return operations;
    }

    @Override
    public String toString() {
        return "{" +
                "result=" + result.toString() +
                ", operations=" + Arrays.toString(operations) +
                '}';
    }
}
