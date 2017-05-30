package dbk.notebooks;

import dbk.abacus.Book;
import dbk.abacus.Lesson;
import dbk.abacus.Tuple2;
import dbk.excel.SheetMarkerWriter;
import dbk.odf.ExerciseWriter;
import dbk.odf.OdfFormulaReader;
import dbk.odf.SecondGenerator;
import dbk.rand.RandomLevel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dbk on 18-Aug-16.
 */
public class MainFormulaApp {

    public static final PAGE_ORIENTATION PAGE = PAGE_ORIENTATION.PORTRAIT;
    private static final String OUT_DIR = "exercises/level3/";
    private static final String TASKS_DIR = "tasks/level3/";
    public static final boolean ADD_SUM = true;
    public static final int SEEK = 4;
    public static final String TASK_NAME = "abacus_formula_odd";
    private static final String outfile = OUT_DIR + TASK_NAME + "." + SEEK;
    private static final String outMarker = OUT_DIR + TASK_NAME+ "." + SEEK + ".marker.xls";
    public static final int LANDSCAPE_SERIES = 10;
    public static final int PORTRAIT_SERIES = 7;
    public static final int LANDSCAPE_TASKS_ON_PAGE = 4;
    public static final int PORTRAINT_TASKS_ON_PAGE = 6;

    public static void main(String[] args){
        RandomLevel.setR(SEEK);
        String fileName = TASKS_DIR + TASK_NAME + ".ods";
        OdfFormulaReader reader = new OdfFormulaReader(fileName);
        List<Lesson> lessons = reader.read();



        initOrientation(PAGE, lessons);
        List<Lesson> lessonsWithHomeWork=generateHomeWork(lessons);
        Book book = reader.getBook();


        SecondGenerator generator = new SecondGenerator(lessonsWithHomeWork);
        List<Tuple2<Lesson, List<List<List<Integer>>>>> data = generator.generate();


        ExerciseWriter exerciseWriter = new ExerciseWriter(data, outfile , PAGE, false, lessons);
        exerciseWriter.write();
        ExerciseWriter exerciseWriterWithAnswer = new ExerciseWriter(data, outfile , PAGE, true, lessons);
        exerciseWriterWithAnswer.write();

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

    public static void addSum(boolean addSum, List<Lesson> lessons) {
        lessons.forEach(l-> l.getSettings().forEach(settings -> settings.setAddSum(addSum)));
    }


    public static List<Lesson> generateHomeWork(List<Lesson> lessons) {
        List<Lesson> lessonsHomeWorks = new ArrayList<>();
        lessons.forEach( l-> {
            lessonsHomeWorks.add(l);
            for (int i = 1; i <=6; i++) {
                lessonsHomeWorks.add(new Lesson(l, "_ДЗ_" + i));
            }
        });
        return lessonsHomeWorks;
    }

    public static void initOrientation(PAGE_ORIENTATION page, List<Lesson> lessons) {
        if (page == PAGE_ORIENTATION.PORTRAIT) {
            lessons.forEach(l-> l.getSettings().forEach(s-> s.setSeries(PORTRAIT_SERIES)));
        } else {
            lessons.forEach(l-> l.getSettings().forEach(s-> s.setSeries(LANDSCAPE_SERIES)));
        }
    }

    public enum PAGE_ORIENTATION {
        LANDSCAPE,
        PORTRAIT;
    }
}
