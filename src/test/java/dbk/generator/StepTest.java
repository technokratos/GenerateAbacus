package dbk.generator;

import dbk.generator.types.Step;
import org.junit.Test;

public class StepTest {

    @Test
    public void testMinus(){
        Step step = new Step(3);
        step.minus(step);
    }

}