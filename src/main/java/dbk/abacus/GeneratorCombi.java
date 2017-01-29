package dbk.abacus;

/**
 * Created by dbk on 16-Aug-16.
 */
public class GeneratorCombi extends Generator {
    final int valueOne;
    final int valueTwo;

    final int stepCombi;

    public GeneratorCombi(int size, int limit, int valueOne, int valueTwo) {
        super(size, limit);
        this.valueOne = valueOne;
        this.valueTwo = valueTwo;
        stepCombi = r.nextInt(size - 1);
    }


    public void generate(){
        if(stepCombi == 0){
            boolean isFirst = r.nextBoolean();
            series[stepCombi] = (isFirst)? valueOne: valueTwo;
            series[stepCombi + 1] = (isFirst)? valueTwo: valueOne;
            index+= 2;
            for (; index < series.length; ) {
                next();
            }
        } else {
            firstStep(limit);
            for (; index < series.length; ) {
                if ( index == stepCombi && (index + 1 < series.length)) {
                    boolean isFirst = r.nextBoolean();
                    int curSum = sum();
                    int nextValue = (isFirst) ? (valueOne - curSum):(valueTwo - curSum);
                    series[stepCombi]= nextValue;
                    series[stepCombi + 1] = (isFirst)? valueTwo: valueOne;
                    index+= 2;
                } else {
                    next();
                }
            }

        }

    }

}
