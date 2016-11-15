package dbk.abacus;

import dbk.odf.Digs;

import java.util.*;
import java.util.List;

/**
 * Created by dbk on 20-Sep-16.
 */
public class Marker {


    //private final boolean addSum;

    //private final List<Tuple2<Level, SortedMap<Tuple2<Integer, Integer>, Count>>> result = new ArrayList<>();
    private SortedMap<Tuple2<Integer, Integer>, Count> countMap = new TreeMap<>();


//    public static void mark() {
//        for (Tuple2<Level, List<List<List<Integer>>>> tuple2 : data) {
//            Level level = tuple2.getA();
//
//            Tuple2<Level, SortedMap<Tuple2<Integer, Integer>, Count>> resultPair = new Tuple2<>(level, countMap);
//            result.add(resultPair);
//            for (List<List<Integer>> series : tuple2.getB()) {
//                mark(series);
//            }
//
//        }
//    }

//    private void markSeries(List<List<Integer>> series) {
//        for (List<Integer> steps : series) {
//            mark(steps, addSum);
//        }
//    }

    public boolean mark(List<Integer> steps, boolean addSum) {
        int sum = steps.get(0);
        boolean invalid = false;
        for (int i = 1; i < steps.size() + ((addSum) ? -1 : 0); i++) {

            boolean stepInvalid = mark(sum, steps.get(i));
            invalid = invalid || stepInvalid;
            sum += steps.get(i);
        }
        return invalid;
    }

    private boolean mark(Integer step, Integer pair) {

        int[] firstValue = Digs.getValue(step);
        int[] secondValue = Digs.getValue(pair);
        int maxLen = firstValue.length > secondValue.length ? firstValue.length : secondValue.length;

        int invalidDig = 0;
        int invalidPair = 0;
        for (int i = 0; i < maxLen; i++) {
            int firstDig = (i < firstValue.length) ? firstValue[i] : 0;
            int secondDig = (i < secondValue.length) ? secondValue[i] : 0;
            addToMap(firstDig, secondDig, countMap);
            if (firstDig < 0) {
                invalidDig = firstDig;
                invalidPair = invalidPair;
            }
        }
        if (invalidDig != 0) {
            System.out.println(" invalid " + invalidDig + " " + invalidPair  + " step  " + step + " pair " + pair);
        }
        return invalidDig !=0;

    }

    public static void addToMap(Integer firstValue, Integer pair, SortedMap<Tuple2<Integer, Integer>, Count> map) {
        Tuple2<Integer, Integer> tuple2 = new Tuple2<>(firstValue, pair);
        Count count = map.get(tuple2);
        if (count == null) {
            count = new Count();
            map.put(tuple2, count);
        }
        count.inc();
    }

    public SortedMap<Tuple2<Integer, Integer>, Count> getCountMap() {
        return countMap;
    }

    //    public void write(String fileName) {
//        SpreadSheet outSheet = SpreadSheet.createEmpty(new DefaultTableModel());
//        for (Tuple2<Level, SortedMap<Tuple2<Integer, Integer>, Count>> tuple2 : result) {
//            Level level = tuple2.getA();
//            Sheet sheet = outSheet.addSheet(level.getTitle());
//            SortedMap<Tuple2<Integer, Integer>, Count> map = tuple2.getB();
//            Tuple2<Integer, Integer> min = findMin(map.keySet());
//            Tuple2<Integer, Integer> max = findMax(map.keySet());
//            sheet.setColumnCount(2 + (max.getA() - min.getA()));
//            sheet.setRowCount(2 + (max.getB() - min.getB()));
//            for (int i = min.getA(); i <= max.getA(); i++) {
//                int columnNumber = i - min.getA() + 1;
//                sheet.getCellAt(columnNumber, 0).setValue(Integer.toString(i));
//            }
//            for (int j = min.getB(); j <= max.getB(); j++) {
//                int rowNumber = 1 + j - min.getB();
//                sheet.getCellAt(0, rowNumber).setValue(Integer.toString(j));
//            }
//
//            for (int i = min.getA(); i <= max.getA(); i++) {
//                int columnNumber = i - min.getA() + 1;
//                for (int j = min.getB(); j <= max.getB(); j++) {
//                    Tuple2<Integer, Integer> coordinate = new Tuple2<>(i, j);
//                    Count count = map.get(coordinate);
//                    int rowNumber = 1 + j - min.getB();
//                    if (count != null) {
//                        sheet.getCellAt(columnNumber, rowNumber).setValue(count.toString());
//                    }
//                    List<Integer> obligatoryPair = level.getObligatoryPair(i);
//                    if (obligatoryPair != null && obligatoryPair.contains(j)) {
//                        MutableCell<SpreadSheet> cell = sheet.getCellAt(columnNumber, rowNumber);
//
//
//                        cell.setBackgroundColor(Color.LIGHT_GRAY);
//                    }
//                }
//            }
//
//
//        }
//
//        try {
//            outSheet.saveAs(new File(fileName));
//        } catch (IOException e) {
//            System.out.println("Error in saving result." + e.getMessage());
//        }
//    }

//    private Tuple2<Integer, Integer> findMin(Set<Tuple2<Integer, Integer>> tuple2s) {
//        int minA = tuple2s.stream().mapToInt(Tuple2::getA).min().getAsInt();
//        int minB = tuple2s.stream().mapToInt(Tuple2::getB).min().getAsInt();
//        return new Tuple2<>(minA, minB);
//    }
//
//    private Tuple2<Integer, Integer> findMax(Set<Tuple2<Integer, Integer>> tuple2s) {
//        int maxA = tuple2s.stream().mapToInt(Tuple2::getA).max().getAsInt();
//        int maxB = tuple2s.stream().mapToInt(Tuple2::getB).max().getAsInt();
//        return new Tuple2<>(maxA, maxB);
//    }

}