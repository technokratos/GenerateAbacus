package dbk.notebooks;

import dbk.abacus.Book;
import dbk.abacus.Lesson;
import dbk.abacus.Tuple2;
import dbk.excel.SheetMarkerWriter;
import dbk.odf.ExerciseWriter;
import dbk.odf.OdfFormulaReader;
import dbk.odf.SecondGenerator;
import dbk.rand.RandomLevel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dbk on 18-Aug-16.
 */
public class MainFormulaApp {

    public static final PAGE_ORIENTATION PAGE = PAGE_ORIENTATION.PORTRAIT;
    public static final String dir = "exercises/level3/";
    public static final String outMarker = dir + "abacus_marker.xls";
    public static final boolean ADD_SUM = true;
    public static final int SEEK = 3;
    public static final String TASKS_DIR = "tasks/level3/";
    public static final String TASK_NAME = "abacus_formula_even";
    public static final String outfile = dir + TASK_NAME;
    public static final int LANDSCAPE_SERIES = 10;
    public static final int PORTRAIT_SERIES = 7;

    public static void main(String[] args){
        RandomLevel.setR(SEEK);
        String fileName = TASKS_DIR + TASK_NAME + ".ods";
        OdfFormulaReader reader = new OdfFormulaReader(fileName);
        ArrayList<Lesson> lessons = reader.read();


        initOrientation(PAGE, lessons);

        generateHomeWork(lessons);


        Book book = reader.getBook();
//        generate(lessons, false, book);

        List<Tuple2<Lesson, List<List<List<Integer>>>>> data = generate(lessons, true, book);


        SheetMarkerWriter markerWriter = new SheetMarkerWriter(data);
        markerWriter.write(outMarker);
        System.out.println("already read");
    }

    private static void generateHomeWork(ArrayList<Lesson> lessons) {

    }

    private static void initOrientation(PAGE_ORIENTATION page, ArrayList<Lesson> lessons) {
        if (page == PAGE_ORIENTATION.PORTRAIT) {
            lessons.forEach(l-> l.getSettings().forEach(s-> s.setSeries(PORTRAIT_SERIES)));
        } else {
            lessons.forEach(l-> l.getSettings().forEach(s-> s.setSeries(LANDSCAPE_SERIES)));
        }
    }

    private static List<Tuple2<Lesson, List<List<List<Integer>>>>> generate(ArrayList<Lesson> lessons, boolean addSum, Book book) {
        lessons.forEach(level -> level.getSettings().forEach(s -> {
            s.setAddSum(addSum);
        }));
        SecondGenerator generator = new SecondGenerator(lessons);
        List<Tuple2<Lesson, List<List<List<Integer>>>>> data = generator.generate();


        ExerciseWriter exerciseWriter = new ExerciseWriter(data, outfile + "_" + SEEK  + (addSum? "_answered":""), book);
        exerciseWriter.write();
        return data;
    }

    private enum PAGE_ORIENTATION {
        LANDSCAPE,
        PORTRAIT;
    }
}
