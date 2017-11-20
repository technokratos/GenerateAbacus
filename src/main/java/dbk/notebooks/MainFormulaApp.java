package dbk.notebooks;

import dbk.abacus.Book;
import dbk.abacus.Lesson;
import dbk.abacus.Tuple2;
import dbk.writer.SheetMarkerWriter;
import dbk.writer.ExerciseWriter;
import dbk.readers.OdfFormulaReader;
import dbk.generator.SecondGenerator;
import dbk.rand.RandomLevel;
import dbk.texts.Texts;
import dbk.trainer.TrainerWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dbk on 18-Aug-16.
 */
public class MainFormulaApp {

    public static void main(String[] args) throws IOException {

        Params params = new Params(args);
        String fileName = params.taskDir + params.taskName + ".ods";

        if (!params.isTrainer()) {
            RandomLevel.setR(params.seek);
            OdfFormulaReader reader = new OdfFormulaReader(fileName);
            List<Lesson> lessons = reader.read();


            initOrientation(params.page, lessons);
            List<Lesson> lessonsWithHomeWork = generateHomeWork(lessons, 6);
            Book book = reader.getBook();


            SecondGenerator generator = new SecondGenerator(lessonsWithHomeWork);
            List<Tuple2<Lesson, List<List<List<Integer>>>>> data = generator.generate();


            ExerciseWriter exerciseWriter = new ExerciseWriter(data, params.outFile, params.page, false, lessons);
            exerciseWriter.write();
            ExerciseWriter exerciseWriterWithAnswer = new ExerciseWriter(data, params.outFile, params.page, true, lessons);
            exerciseWriterWithAnswer.write();


//        final Process process = Runtime.getRuntime()
//                    .exec(String.format("pdftk %s.pdf background watermarkerp.pdf output %s.wm.pdf", outFile, outFile));

            SheetMarkerWriter markerWriter = new SheetMarkerWriter(data);
            markerWriter.write(params.outMarker);
            System.out.println("already read");
        } else {

            generateTrainer(fileName, params);
        }
    }

    private static void generateTrainer(String fileName, Params params) throws IOException {
        RandomLevel.setR(params.trainerSeek);
        OdfFormulaReader reader = new OdfFormulaReader(fileName);
        List<Lesson> lessons = reader.read();
        Book book = reader.getBook();
        lessons.forEach(level -> level.getSettings().forEach(s-> s.setAddSum(false)));
        SecondGenerator generator = new SecondGenerator(lessons);
        List<Tuple2<Lesson, List<List<List<Integer>>>>> data = generator.generate();

        TrainerWriter trainerWriter = new TrainerWriter(data, params.outTrainerDir);
        trainerWriter.write();

        SheetMarkerWriter markerWriter = new SheetMarkerWriter(data);
        markerWriter.write(params.trainerMarkerFile);


    }


    public static List<Lesson> generateHomeWork(List<Lesson> lessons, int countOfHomeWorks) {
        List<Lesson> lessonsHomeWorks = new ArrayList<>();
        lessons.forEach( l-> {
            lessonsHomeWorks.add(l);
            for (int i = 1; i <= countOfHomeWorks; i++) {
                final Lesson lesson = new Lesson(l, "_ДЗ_" + i);
                lessonsHomeWorks.add(lesson);
                lesson.getSettings().stream().limit(1).forEach(s-> s.setDescription2(Texts.HOME_WORK.getText()));
            }
        });
        return lessonsHomeWorks;
    }

    public static void initOrientation(PAGE_ORIENTATION page, List<Lesson> lessons) {
        if (page == PAGE_ORIENTATION.PORTRAIT) {
            lessons.forEach(l-> l.getSettings().forEach(s-> s.setSeries(Params.PORTRAIT_SERIES)));
        } else {
            lessons.forEach(l-> l.getSettings().forEach(s-> s.setSeries(Params.LANDSCAPE_SERIES)));
        }
    }

    public enum PAGE_ORIENTATION {
        LANDSCAPE,
        PORTRAIT;
    }
}
