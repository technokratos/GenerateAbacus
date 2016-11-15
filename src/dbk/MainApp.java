package dbk;

import dbk.abacus.Book;
import dbk.abacus.Level;
import dbk.abacus.Tuple2;
import dbk.excel.SheetMarkerWriter;
import dbk.odf.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dbk on 18-Aug-16.
 */
public class MainApp {

    public static void main(String[] args){
//        ExcelGenerator excelGenerator = new ExcelGenerator("abacus.xls");
//        excelGenerator.read();
        String fileName = "abacus.ods";
        //String fileName = "abacus_print_test.ods";
        //String fileName = "abacusLevel9_1.ods";
        //String fileName = "abacusWithoutObligatory.ods";
        OdfReader reader = new OdfReader(fileName);
        ArrayList<Level> levels = reader.read();
        Book book = reader.getBook();
        SecondGenerator generator = new SecondGenerator(levels);
        //ExerciseWriter exerciseWriter = new ExerciseWriter(generator.generate(false), "abacus_out.ods");
        boolean addSum = true;
        List<Tuple2<Level, List<List<List<Integer>>>>> data = generator.generate(addSum);
        ExerciseWriter exerciseWriter = new ExerciseWriter(data,"abacus_out", book);
        //todo generate report with count of used pairs
        exerciseWriter.write();
        //MarkerWriter markerWriter = new MarkerWriter(data);
        //markerWriter.write("abacus_marker.odf");
        SheetMarkerWriter markerWriter = new SheetMarkerWriter(data);
        markerWriter.write("abacus_marker.xls");
        System.out.println("already read");
    }
}
