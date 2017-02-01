package dbk.odf;
import dbk.abacus.Book;
import dbk.abacus.Level;
import dbk.abacus.Tuple2;
import dbk.adapter.*;
import dbk.texts.Texts;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import static dbk.texts.Texts.*;

/**
 * Created by dbk on 30.08.2016.
 */
public class ExerciseWriter {
    /**
     * title, sum, separator
     */
    public static final int ADDITIONAL_ROWS = 6;

    public static final short MAIN_TEXT_SIZE = 13;
    public static final short HOR_HEADER_FONT_SIZE = 20;
    public static final short VER_HEADER_FONT_SIZE = 12;
    private static final int FIRST_COLUMN = 0;
    public static final int THIRD_TITLE_COLUMN = 8;
    public static final int PAGE_NUMBER_COLUMN = THIRD_TITLE_COLUMN + 2;
    public static final int SECOND_TITLE_COLUMN = 3;
    public static final int ROW_ON_PAGE = 23;
    private final List<Tuple2<Level, List<List<List<Integer>>>>> exercises;
    private final String fileName;
    private final Book book;

    public ExerciseWriter(List<Tuple2<Level, List<List<List<Integer>>>>> exercises, String fileName, Book book) {
        this.exercises = exercises;
        this.book = book;
        this.fileName = fileName + ".xls";
    }

    public void write() {
        //SpreadSheet outSheet = SpreadSheet.createEmpty(new DefaultTableModel());
        Workbook workbook = WorkbookFactory.getWorkbook(WorkbookFactory.Library.POI, fileName);

        int pageNumber = 1;
        //pageNumber = addPageWithName(workbook, pageNumber);
        //pageNumber = addPageWithSchedule(workbook, pageNumber, book);
        int lastRowNumber = 0;

        Sheet sheet = null;
        boolean addedPageNumber = false;
        for (Tuple2<Level, List<List<List<Integer>>>> exercise: exercises) {

            Level level = exercise.getA();
            final int rowCount;
            //?????????? ??? ?????? ??????? ?? ????? ????????
            //????
            if (lastRowNumber > 15 || sheet == null) {
                System.out.println("Start new sheet " + level.getTitle());
                sheet = workbook.addSheet(level.getTitle());
                lastRowNumber = 0;
                //add odd page
                if (pageNumber % 2 == 0) {
                    pageNumber = addPageNumber(sheet, lastRowNumber, pageNumber);
                    addedPageNumber = true;
                } else {
                    addedPageNumber = false;
                }

            } else {

                System.out.println("Continue sheet " + " lastRowNumber " + lastRowNumber + ", new level " + level.getTitle());
            }
            rowCount = lastRowNumber + countRows(level.getSettings()) + 1;//1 for title
            System.out.println(" Defined row count " + rowCount + " lastRowNumber " + lastRowNumber );
            sheet.setRowCount(rowCount);


            setupColumns(sheet, level);
            for (int groupSeriesIndex = 0; groupSeriesIndex < exercise.getB().size(); groupSeriesIndex++) {
                List<List<Integer>> series = exercise.getB().get(groupSeriesIndex);
                Settings settings = level.getSettings().get(groupSeriesIndex);
                System.out.println("  Start new settings groupSeriesIndex " + groupSeriesIndex +  " lastRowNumber " + lastRowNumber );
                //???????? ???????? ???? ?? ??????
                System.out.println("  Try print description  " + settings.description + " "  + settings.description1  + " " + settings.description2 +  " lastRowNumber " + lastRowNumber );
                lastRowNumber = setDescription(sheet, lastRowNumber, settings);

                //lastRowNumber++;//???????? ?????? ??? ??????????
                for (int seriesIndex = 0; seriesIndex < series.size(); seriesIndex++) {
                    //set header of series
                    int column = seriesIndex + 1;
                    Cell headerSeries = getBorderedCell(sheet, FIRST_COLUMN + column, lastRowNumber);
                    //setRowHeaderStyle(headerSeries);
                    headerSeries.setValue(Integer.toString(seriesIndex + 1));
                }

                System.out.println("  Draw series header " + " lastRowNumber " + lastRowNumber );

                Cell numberHeader = getBorderedCell(sheet, FIRST_COLUMN, lastRowNumber );

                numberHeader.setValue(Texts.NUMBER_SYMBOLL.getText());
                //merge the first cell
                int startMergeRow = lastRowNumber + 1;

                //todo check -1 for addSum;
                int rowToMerge = series.get(0).size() - 1 + (settings.getAddSum()? -1:0);
                sheet.merge(FIRST_COLUMN, startMergeRow, 0, rowToMerge);
                System.out.println("  Merge startMergeRow " +  startMergeRow + " rowToMerge " + rowToMerge + " lastRowNumber " + lastRowNumber );
                //set border style for merged cells
                Cell seriesHeader = getVerBorderedCell(sheet, FIRST_COLUMN, startMergeRow);
                seriesHeader.setValue(Integer.toString(groupSeriesIndex + 1));
                //don't work
                //todo check 1 for


                //seriesHeader.setFontSize(24);


                for (int seriesIndex = 0; seriesIndex < series.size(); seriesIndex++) {
                    List<Integer> steps = series.get(seriesIndex);
                    if (seriesIndex == 0) {
                        //set header of lines
                        lastRowNumber++;
                        System.out.println("    First step in iterate series steps size " + steps.size() + " lastRowNumber " + lastRowNumber );
                        for (int stepIndex = 0; stepIndex < steps.size(); stepIndex++) {
                            Cell cellAt = getBorderedCell(sheet, FIRST_COLUMN, stepIndex + lastRowNumber);
                           // cellAt.setValue(Integer.toString(stepIndex + 1));
                        }
                    }
                    for (int stepIndex = 0; stepIndex < steps.size(); stepIndex++) {
                        Integer value = steps.get(stepIndex);
                        Cell cellAt = getCell(sheet, FIRST_COLUMN + seriesIndex + 1, stepIndex + lastRowNumber);
                        cellAt.setValue(value);

                        if (stepIndex == steps.size() - 1 && settings.getAddSum()  ) {
                            Style thinBorder = cellAt.getSheet().getWorkbook().getStyle("THIN_BORDER", Style::setThinBorder);
                            thinBorder.setThinBorder();
                            cellAt.setStyle(thinBorder);
                        } else {
                            Style centerStyle = cellAt.getSheet().getWorkbook().getStyle("CENTER_ALIGN", Style::setVertStyleWithBorder);
                            cellAt.setStyle(centerStyle);
                        }

                    }
                    //empty place for summ
                    if (!settings.getAddSum()) {
                        Cell cellAt = getCell(sheet, FIRST_COLUMN + seriesIndex + 1, steps.size() + lastRowNumber);
                        Style thinBorder = cellAt.getSheet().getWorkbook().getStyle("THIN_BORDER", Style::setThinBorder);
                        cellAt.setStyle(thinBorder);
                        cellAt.setValue("");

                    }
                }

                //answer header of row
                int rowForSumma = lastRowNumber + (settings.getAddSum()? -1:0) + series.get(0).size();
                System.out.println("  Draw summa " + rowForSumma );
                getBorderedCell(sheet, FIRST_COLUMN, rowForSumma).setValue(Texts.SUMMA.getText());

                if (!settings.getAddSum()) {
                    lastRowNumber++;//add empty row for answer
                }



                lastRowNumber+= series.iterator().next().size();//with step for answer
                //lastRowNumber ++;
                System.out.println("  The end of series " + " lastRowNumber " + lastRowNumber );
                //add page break
                //do not add last page break and description
                if (groupSeriesIndex % 4 == 3 && groupSeriesIndex < exercise.getB().size() - 1) {
                    System.out.println("  Add pagebreak " + " lastRowNumber " + lastRowNumber );

                    if (pageNumber % 2 == 1 && !addedPageNumber) {
                        lastRowNumber++;
                        pageNumber = addPageNumber(sheet, lastRowNumber, pageNumber);
                        sheet.addPageBreak(lastRowNumber++);
                        pageNumber = addPageNumber(sheet, lastRowNumber, pageNumber);
                        addedPageNumber = true;
                    } else {
                        sheet.addPageBreak(lastRowNumber++);
                        addedPageNumber = false;
                    }

                }
                //lastRowNumber = setDescription(sheet, lastRowNumber, settings);
            }
            //for sheet without page breaks
            if (! addedPageNumber && pageNumber % 2 == 1 && lastRowNumber >= ROW_ON_PAGE) {
                lastRowNumber++;
                pageNumber = addPageNumber(sheet, lastRowNumber, pageNumber);
            }
        }
        try {
            workbook.saveAs();
        } catch (IOException e) {
            System.out.println("Error in saving result." + e.getMessage());
        }
    }

    private int addPageWithSchedule(Workbook workbook, int pageNumber, Book book) {
        Sheet sheet = workbook.addSheet("Schedule");
        sheet.setRowCount(ROW_ON_PAGE);

//        for (int i = 0; i < ROW_ON_PAGE; i++) {
//            sheet.setRowHeight(i, 30 );
//        }

        sheet.setColumnCount(7);
        sheet.setColumnSize(0, 4500);
        sheet.setColumnSize(1, 3000);
        sheet.setColumnSize(2, 6000);
        sheet.setColumnSize(3, 6000);
        sheet.setColumnSize(4, 4000);
        sheet.setColumnSize(5, 4000);
        sheet.setColumnSize(6, 4000);

        int columnNumber = 0;
        int firstRow = 0;
        for (int i = 0; i < 7; i++) {
            getCell(sheet, i, 1);//set border
        }
        sheet.merge(columnNumber, firstRow, 0, 1);
        getCell(sheet, columnNumber++, firstRow).setValue(WEEK.getText());
        sheet.merge(columnNumber, firstRow, 0, 1);
        getCell(sheet, columnNumber++, firstRow).setValue(DATE.getText());
        sheet.merge(columnNumber, firstRow, 0, 1);
        getCell(sheet, columnNumber++, firstRow).setValue(CLASS_WORK.getText());
        sheet.merge(columnNumber, firstRow, 0, 1);
        getCell(sheet, columnNumber++, firstRow).setValue(HOME_WORK.getText());
        sheet.merge(columnNumber, firstRow, 0, 1);
        getCell(sheet, columnNumber++, firstRow).setValue(COMMENTS.getText());
        sheet.merge(columnNumber, firstRow, 1, 0);
        getCell(sheet, columnNumber, firstRow).setValue(SIGNS.getText());
        getCell(sheet, columnNumber + 1, firstRow);//set border in left up corner

        getCell(sheet, columnNumber ++, firstRow + 1).setValue(PARENTS.getText());
        getCell(sheet, columnNumber ++, firstRow + 1).setValue(INSTRUCTOR.getText());

        firstRow+=2;

        int firstWeek = (Book.EVEN == book)? 1 : 0;

        for(int i = 0; i< 9; i ++) {
            sheet.merge(0, firstRow + 2 * i, 0, 1);
            getCell(sheet, 0, firstRow + 2 * i).setValue((firstWeek + 2 * i) + TH_WEEK.getText());
            getCell(sheet, 0, firstRow + 2 * i + 1);
            for (int j = 1; j < columnNumber; j++) {
                sheet.merge(j, firstRow + 2*i, 0, 1);
                getCell(sheet, j, firstRow + 2 * i);
                getCell(sheet, j, firstRow + 2 * i + 1);
            }
        }

        return pageNumber+1;
    }

    private int addPageWithName(Workbook workbook, int pageNumber) {
        Sheet sheet = workbook.addSheet("Name");
        sheet.setRowCount(ROW_ON_PAGE);
        int nameColumn = 3;
        sheet.setColumnCount(nameColumn);
        sheet.getCellAt(nameColumn, 2).setValue(STUDENT_NAME.getText());
        sheet.getCellAt(nameColumn, 4).setValue(INSTRUCTOR_NAME.getText());
        sheet.getCellAt(nameColumn, 6).setValue(CONTACTS.getText());
        return pageNumber + 1;
    }

    private int addPageNumber(Sheet sheet, int lastRowNumber, int pageNumber) {
        System.out.println("Add page number " + sheet.getTitle() + " row " + lastRowNumber + " pageNumber " + pageNumber);
        Cell cellAt2 = sheet.getCellAt(PAGE_NUMBER_COLUMN, lastRowNumber);
        cellAt2.setValue(pageNumber);
        return ++pageNumber;
    }

    private void setupColumns(Sheet sheet, Level level) {
        int columnCount = countColumns(level.getSettings());
        sheet.setColumnCount(FIRST_COLUMN + columnCount);
        int size = 2700;
        for (int i = 0; i < columnCount; i++) {

            if (i == 0) {
                sheet.setColumnSize(FIRST_COLUMN + i, size + 1800);
            } else {
                sheet.setColumnSize(FIRST_COLUMN + i, size);
            }

        }

    }

    private String encode(String str) {
        return new String(Charset.forName("UTF-8").encode(str).array());
    }

    private int setDescription(Sheet sheet, int lastRowNumber, Settings settings) {
        if (settings.description != null && !settings.description.isEmpty()) {
            //getBorderedCell(sheet, 0, lastRowNumber).setValue(settings.description);

            Cell cellAt = sheet.getCellAt(FIRST_COLUMN, lastRowNumber);
            cellAt.setValue(settings.description);

            Cell cellAt1 = sheet.getCellAt(FIRST_COLUMN + SECOND_TITLE_COLUMN, lastRowNumber);
            cellAt1.setValue(settings.description1);
            Cell cellAt2 = sheet.getCellAt(FIRST_COLUMN + THIRD_TITLE_COLUMN, lastRowNumber);
            cellAt2.setValue(settings.description2);
            lastRowNumber++;
        }
        return lastRowNumber;
    }

    private Cell getCell(Sheet sheet, int columnNumber, int rowNumber) {

        Cell cellAt = sheet.getCellAt(columnNumber, rowNumber);
        //cellAt.setThinBorder();
        cellAt.setHeight(300);
        return cellAt;
    }

    private Cell getBorderedCell(Sheet sheet, int column, int lastRowNumber) {
//return getSideAttribute(s, "border", this.getNS("fo"));, attrName + "-" + s.name().toLowerCase()
        // "RIGHT" -> "thin solid #000000"
        Cell cellAt = sheet.getCellAt(column, lastRowNumber);
        //cellAt.setHorStyleWithBorder();
        Style horStyleWithBorder = cellAt.getSheet().getWorkbook().getStyle("HOR_STYLE_WITH_BORDER", Style::setThinBorder);
        horStyleWithBorder.setHorStyleWithBorder();
        cellAt.setStyle(horStyleWithBorder);
        return cellAt;
    }//getCellStyleDesc().findDefaultStyle(this.getODDocument().getPackage());


    private Cell getVerBorderedCell(Sheet sheet, int column, int lastRowNumber) {
        Cell cellAt = sheet.getCellAt(column, lastRowNumber);

        Style vertStyleWithBorder = cellAt.getSheet().getWorkbook().getStyle("VERT_STYLE_WITH_BORDER", Style::setVertStyleWithBorder);
        cellAt.setStyle(vertStyleWithBorder);

        return cellAt;
    }

    private int countColumns(List<Settings> settings) {
        return settings.stream().mapToInt(Settings::getSeries).max().getAsInt() + 2;
    }

    private int countRows(List<Settings> settings) {
//        int count = 0;
//        for(Settings locSettings: settings) {
//            count+= locSettings.getSteps() + 2;
//
//        }
        return settings.stream().mapToInt(Settings::getSteps).sum() + settings.size()* ADDITIONAL_ROWS;
    }
}
