package dbk.odf;


import dbk.abacus.Book;
import dbk.abacus.Lesson;
import org.jopendocument.dom.spreadsheet.MutableCell;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;


import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by dbk on 19-Aug-16.
 */
public class OdfReader {

    public static final int ATTEMPT_LIMIT = 10;
    private final String fileName;
    SpreadSheet sheet;
        static int RED = -65536;//0xFF0000
    static int GREEN = -16732080;//0x00B050
    public static int BLUE = -16748352;//0x0070C0
    ArrayList<Lesson> lessons = new ArrayList<>();

    final int seriesCount = 10;
    final int stepsCount = 3;
    private Book book = Book.ODD;


    public OdfReader(String fileName) {
        this.fileName = fileName;
        SpreadSheet spreadsheet = null;
        try {
            spreadsheet = SpreadSheet.createFromFile(new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.sheet = spreadsheet;

    }

    public ArrayList<Lesson> read() {
        for (int i = 0; i < sheet.getSheetCount(); i++) {
            Sheet sheet = this.sheet.getSheet(i);
            if (i == 0 && sheet.getName() != null && sheet.getName().contains("empty")) {
                this.book = Book.EVEN;
                continue;
            }


            System.out.println(sheet.getName());
            Lesson lesson = readSheetWithSettings(sheet);

            lessons.add(lesson);
            boolean foundData = false;
            Integer headerRow = null;
            for (int r = 0; r < sheet.getRowCount(); r++) {

                boolean found = false;
                MutableCell<SpreadSheet> firstCellInRow = sheet.getCellAt(0, r);
                if (headerRow == null) {
                    if ("data".equals(firstCellInRow.getValue())) {
                        headerRow = r;
                    } else {
                        continue;
                    }
                }
                for (int c = 1; c < sheet.getColumnCount(); c++) {
                    MutableCell<SpreadSheet> cell = sheet.getCellAt(c, r);
                    if (cell != null && cell.getValue() != null && !cell.getValue().toString().isEmpty()) {
                        MutableCell headerCell = sheet.getCellAt(c, headerRow);
                        if (cell.getStyle().getBackgroundColor().getRGB() == GREEN) {
                            lesson.put(getValue(headerCell), getValue(firstCellInRow));
                        } else if (cell.getStyle().getBackgroundColor().getRGB() == BLUE) {
                            lesson.put(getValue(headerCell), getValue(firstCellInRow), true);
                        } else if (cell.getStyle().getBackgroundColor().getRGB() == RED) {
                            lesson.putBlocked(getValue(headerCell), getValue(firstCellInRow));
                        }

                        //System.out.print(getValue(cell) + " " + backgroundColor.getRGB() +  "!");
                        found = true;
                    } else {
                        if (c > 0) {
                            break;
                        }
                    }
                }
                if (!found) {
                    break;
                }
                //System.out.println();

            }

        }

        return lessons;
    }

    /**
     * series
     * steps
     * tens
     * hundreds
     * thousands
     * decimals
     */
    private Lesson readSheetWithSettings(Sheet sheet) {

        List<Settings> settings = new ArrayList<>();
        ITERATE_SETTINGS: for (int r = 0; r < sheet.getRowCount(); r++) {
            boolean found = false;
            MutableCell<SpreadSheet> firstCellInRow = sheet.getCellAt(0, r);
            String settingsName = firstCellInRow.getValue() != null ? firstCellInRow.getValue().toString(): null;
            System.out.println("Start read row " + r + " settings name " + settingsName);
            if ("data".equals(settingsName)) {
                break;
            }

            for (int c = 1; c < sheet.getColumnCount(); c++) {
                MutableCell<SpreadSheet> cell = sheet.getCellAt(c, r);
                Object value = cell.getValue();
                //???? ?? ?????? ????
                if (value != null && !value.toString().isEmpty()) {
                    final Settings locSettings;
                    if (settings.size()>= c) {
                        locSettings = settings.get(c - 1);
                        System.out.println("Get settings by c " + c + " by index " + (c -1 ));
                    } else {
                        locSettings = new Settings();
                        settings.add(locSettings);
                        System.out.println("Create new settings for c " + c);
                    }

                    locSettings.setValue(settingsName, value.toString());

                } else if (c > settings.size()) {//????????? ???? ?????? ??????? ????????
                    break;
                }
            }

        }
        Lesson lesson = new Lesson(sheet.getName(), settings);
        System.out.println(lesson);
        return lesson;
    }



    private Integer getValue(MutableCell headerCell) {
        Object value = headerCell.getValue();
        return value == null || value.toString().isEmpty() ? null : Integer.parseInt(value.toString());
    }

    public Book getBook() {
        return book;
    }


//    public void write() {
//        SpreadSheet outSheet = SpreadSheet.createEmpty(new DefaultTableModel());
//        for (Map.Entry<String, List<List<Integer>>> entry : exercises.entrySet()) {
//
//            Sheet sheet = outSheet.addSheet(entry.getKey());
//            sheet.setRowCount(stepsCount+1);
//            sheet.setColumnCount(seriesCount);
//            List<List<Integer>> series = entry.getValue();
//
//            for (int seriesIndex = 0; seriesIndex < series.size(); seriesIndex++) {
//                List<Integer> steps = series.get(seriesIndex);
//                for (int stepIndex = 0; stepIndex < steps.size(); stepIndex++) {
//                    Integer value = steps.get(stepIndex);
//                    MutableCell<SpreadSheet> cellAt = sheet.getCellAt(seriesIndex, stepIndex);
//                    cellAt.setValue(value);
//                }
//
//            }
//        }
//        try {
//            outSheet.saveAs(new File("out_" + fileName));
//        } catch (IOException e) {
//            System.out.println("Error in saving result." + e.getMessage());
//        }
//    }
}