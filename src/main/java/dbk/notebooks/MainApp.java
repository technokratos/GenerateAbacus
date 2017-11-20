package dbk.notebooks;

import dbk.abacus.Lesson;
import dbk.abacus.Tuple2;
import dbk.writer.SheetMarkerWriter;
import dbk.readers.OdfReader;
import dbk.generator.SecondGenerator;
import dbk.rand.RandomLevel;

import java.io.IOException;
import java.util.List;

import static dbk.notebooks.MainFormulaApp.generateHomeWork;

/**
 * Created by dbk on 18-Aug-16.
 */
public class MainApp {

    public static final String LEVEL = "level3";

    private static final String OUT_DIR = "exercises/" + LEVEL + "/";
    public static final String TASKS_DIR = "tasks/" + LEVEL + "/";

    public static final int SEEK = 4;
    public static final String TASK_NAME = "abacus_formula_odd";
    private static final String outfile = OUT_DIR + TASK_NAME + "." + SEEK;
    private static final String outMarker = OUT_DIR + TASK_NAME+ "." + SEEK + ".marker.xls";

    public static void main(String[] args){
        RandomLevel.setR(SEEK);
        String fileName = TASKS_DIR + TASK_NAME + ".ods";
        OdfReader reader = new OdfReader(fileName);
        List<Lesson> lessons = reader.read();



//        initOrientation(page, lessons);
        List<Lesson> lessonsWithHomeWork=generateHomeWork(lessons, 6);
//        List<Lesson> lessonsWithHomeWork = lessons;


        SecondGenerator generator = new SecondGenerator(lessonsWithHomeWork);
        List<Tuple2<Lesson, List<List<List<Integer>>>>> data = generator.generate();


//        ExerciseWriter exerciseWriter = new ExerciseWriter(data, outfile , page, false, lessons);
//        exerciseWriter.write();
//        ExerciseWriter exerciseWriterWithAnswer = new ExerciseWriter(data, outfile , page, true, lessons);
//        exerciseWriterWithAnswer.write();

        try {
            final Process process = Runtime.getRuntime()
                    .exec(String.format("pdftk %s.pdf background watermarkerp.pdf output %s.wm.pdf", outfile, outfile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        SheetMarkerWriter markerWriter = new SheetMarkerWriter(data);
        markerWriter.write(outMarker);
        System.out.println("already read");
    }
}
