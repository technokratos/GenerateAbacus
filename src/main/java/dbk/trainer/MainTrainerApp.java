package dbk.trainer;

import dbk.abacus.Book;
import dbk.abacus.Lesson;
import dbk.abacus.Tuple2;
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

        String fileName = "tasks/level2/abacus.ods";
        OdfReader reader = new OdfReader(fileName);
        ArrayList<Lesson> lessons = reader.read();
        Book book = reader.getBook();
        lessons.forEach(level -> level.getSettings().forEach(s-> s.setAddSum(false)));
        SecondGenerator generator = new SecondGenerator(lessons);

        List<Tuple2<Lesson, List<List<List<Integer>>>>> data = generator.generate();

        TrainerWriter trainerWriter = new TrainerWriter(data, "trainer level 2");
        trainerWriter.write();
        System.out.println("already read");
    }
}
