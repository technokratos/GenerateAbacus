package dbk.trainer;

import dbk.abacus.Book;
import dbk.abacus.Level;
import dbk.abacus.Tuple2;
import dbk.excel.SheetMarkerWriter;
import dbk.odf.ExerciseWriter;
import dbk.odf.OdfReader;
import dbk.odf.SecondGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dbk on 18-Aug-16.
 */
public class MainTrainerApp {

    public static void main(String[] args) throws IOException {

        String fileName = "abacus_application_for_exercises.ods";
        OdfReader reader = new OdfReader(fileName);
        ArrayList<Level> levels = reader.read();
        Book book = reader.getBook();
        levels.forEach( level -> level.getSettings().forEach(s-> s.setAddSum(false)));
        SecondGenerator generator = new SecondGenerator(levels);

        List<Tuple2<Level, List<List<List<Integer>>>>> data = generator.generate();

        TrainerWriter trainerWriter = new TrainerWriter(data, "trainer level 2");
        trainerWriter.write();
        System.out.println("already read");
    }
}
