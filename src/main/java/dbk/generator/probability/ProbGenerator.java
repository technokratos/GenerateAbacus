package dbk.generator.probability;

import dbk.abacus.Lesson;
import dbk.generator.Settings;
import dbk.generator.types.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ProbGenerator {

    public static Step getNextStep(Settings settings, Matrix matrix) {
        return Step.of(new int[0]);
    }

    public static Steps getSteps(Settings settings, Matrix matrix) {

        return new Steps(IntStream.range(0, settings.getSteps())
                .mapToObj(value -> getNextStep(settings, matrix))
                .collect(Collectors.toList()));
    }



    public static Series generateSeries(Matrix matrix, Settings settings) {
//        List<Steps> collect = IntStream.range(0, settings.getSeries())
//                .mapToObj(value -> getSteps(matrix.getStepsMatrix(), settings))
//                .collect(Collectors.toList());
//        return new Series(collect);
        return null;

    }

    public static Block generate(Lesson lesson) {
//        Matrix matrix = new Matrix(lesson);
//        List<Series> series = lesson.getSettings().stream()
//                .map(settings -> generateSeries(matrix.getChildMatrix(), settings))
//                .collect(Collectors.toList());
//        return new ClassicBlock(series);
        return null;
    }


}
