package dbk.writer;

import dbk.abacus.Count;
import dbk.abacus.Lesson;
import dbk.abacus.Tuple2;
import org.jopendocument.dom.spreadsheet.MutableCell;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;

/**
 * Created by dbk on 20-Sep-16.
 */
public class MarkerWriter {

    private final List<Tuple2<Lesson, SortedMap<Tuple2<Integer, Integer>, Count>>> result = new ArrayList<>();

    public MarkerWriter(List<Tuple2<Lesson, List<List<List<Integer>>>>> data) {
        data.forEach(l-> result.add(new Tuple2<>(l.getA(),l.getA().getMarker().getCountMap())));

    }


    public void write(String fileName) {
        SpreadSheet outSheet = SpreadSheet.createEmpty(new DefaultTableModel());
        for (Tuple2<Lesson, SortedMap<Tuple2<Integer, Integer>, Count>> tuple2 : result) {
            Lesson lesson = tuple2.getA();
            Sheet sheet = outSheet.addSheet(lesson.getTitle());
            SortedMap<Tuple2<Integer, Integer>, Count> map = tuple2.getB();
            Tuple2<Integer, Integer> min = findMin(map.keySet());
            Tuple2<Integer, Integer> max = findMax(map.keySet());
            sheet.setColumnCount(2 + (max.getA() - min.getA()));
            sheet.setRowCount(2 + (max.getB() - min.getB()));
            for (int i = min.getA(); i <= max.getA(); i++) {
                int columnNumber = i - min.getA() + 1;
                sheet.getCellAt(columnNumber, 0).setValue(Integer.toString(i));
            }
            for (int j = min.getB(); j <= max.getB(); j++) {
                int rowNumber = 1 + j - min.getB();
                sheet.getCellAt(0, rowNumber).setValue(Integer.toString(j));
            }

            for (int i = min.getA(); i <= max.getA(); i++) {
                int columnNumber = i - min.getA() + 1;
                for (int j = min.getB(); j <= max.getB(); j++) {
                    Tuple2<Integer, Integer> coordinate = new Tuple2<>(i, j);
                    Count count = map.get(coordinate);
                    int rowNumber = 1 + j - min.getB();
                    if (count != null) {
                        sheet.getCellAt(columnNumber, rowNumber).setValue(count.toString());
                    }
                    List<Integer> obligatoryPair = lesson.getObligatoryPair(i);
                    if (obligatoryPair != null && obligatoryPair.contains(j)) {
                        MutableCell<SpreadSheet> cell = sheet.getCellAt(columnNumber, rowNumber);


                        cell.setBackgroundColor(Color.LIGHT_GRAY);
                    }
                }
            }


        }

        try {
            outSheet.saveAs(new File(fileName));
        } catch (IOException e) {
            System.out.println("Error in saving result." + e.getMessage());
        }
    }

    private Tuple2<Integer, Integer> findMin(Set<Tuple2<Integer, Integer>> tuple2s) {
        int minA = tuple2s.stream().mapToInt(Tuple2::getA).min().getAsInt();
        int minB = tuple2s.stream().mapToInt(Tuple2::getB).min().getAsInt();
        return new Tuple2<>(minA, minB);
    }

    private Tuple2<Integer, Integer> findMax(Set<Tuple2<Integer, Integer>> tuple2s) {
        int maxA = tuple2s.stream().mapToInt(Tuple2::getA).max().getAsInt();
        int maxB = tuple2s.stream().mapToInt(Tuple2::getB).max().getAsInt();
        return new Tuple2<>(maxA, maxB);
    }
}
