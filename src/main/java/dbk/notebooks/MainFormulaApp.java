package dbk.notebooks;

import dbk.abacus.Book;
import dbk.abacus.Level;
import dbk.abacus.Tuple2;
import dbk.excel.SheetMarkerWriter;
import dbk.odf.ExerciseWriter;
import dbk.odf.OdfFormulaReader;
import dbk.odf.OdfReader;
import dbk.odf.SecondGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dbk on 18-Aug-16.
 */
public class MainFormulaApp {

    public static void main(String[] args){
//        ExcelGenerator excelGenerator = new ExcelGenerator("abacus.xls");
//        excelGenerator.read();
        //String fileName = "abacus.ods";
        String fileName = "tasks/level3/task_with_formula_base.ods";
        //String fileName = "abacusLevel9_1.ods";
        //String fileName = "abacusWithoutObligatory.ods";
        OdfFormulaReader reader = new OdfFormulaReader(fileName);
        ArrayList<Level> levels = reader.read();
        Book book = reader.getBook();
        levels.forEach( level -> level.getSettings().forEach(s-> s.setAddSum(true)));
        SecondGenerator generator = new SecondGenerator(levels);
        //ExerciseWriter exerciseWriter = new ExerciseWriter(generator.generate(false), "abacus_out.ods");

        List<Tuple2<Level, List<List<List<Integer>>>>> data = generator.generate();
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
