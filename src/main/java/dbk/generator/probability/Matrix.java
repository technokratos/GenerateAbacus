package dbk.generator.probability;

import dbk.abacus.Lesson;

import java.util.ArrayList;
import java.util.List;

public class ProbMatrix {

    private final Lesson lesson;
    TreeMap<ProbValue> probValues;
    private ProbMatrix seriesMatrix;

    List<ProbMatrix> childs = new ArrayList<>();
    ProbMatrix root = null;

    public ProbMatrix(Lesson lesson) {
        this.lesson = lesson;
    }

    public ProbMatrix(ProbMatrix probMatrix) {
        root = probMatrix;
    }

    public ProbMatrix getChildMatrix() {
        ProbMatrix probMatrix = new ProbMatrix(this);
        childs.add(probMatrix);
        return probMatrix;
    }


}
