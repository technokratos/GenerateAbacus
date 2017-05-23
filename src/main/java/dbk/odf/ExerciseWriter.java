package dbk.odf;
import dbk.abacus.Book;
import dbk.abacus.Lesson;
import dbk.abacus.Tuple2;
import dbk.adapter.*;
import dbk.notebooks.MainFormulaApp;
import dbk.texts.Texts;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Random;

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
    public static final int LANDSCAPE_THIRD_TITLE_COLUMN = 8;
    public static final int PORTRAIN_THIRD_TITLE_COLUMN = 5;
    public static final String HOR_STYLE_WITH_BORDER = "HOR_STYLE_WITH_BORDER";
    public static final String HOR_STYLE_WITH_BORDER_BOLD = "HOR_STYLE_WITH_BORDER_BOLD";
    public static final String VERT_STYLE_WITH_BORDER = "VERT_STYLE_WITH_BORDER";
    public static final String VERT_STYLE_WITH_BORDER_BOLD = "VERT_STYLE_WITH_BORDER_BOLD";
    public final int thirdTitleColumn;
    public final int pageNumberColumn;
    public static final int SECOND_TITLE_COLUMN = 3;
    public static final int LANDSCAPE_ROW_ON_PAGE = 23;
    private final List<Tuple2<Lesson, List<List<List<Integer>>>>> exercises;
    private final String fileName;
    //private final Book book;
    private final MainFormulaApp.PAGE_ORIENTATION page;
    public final int rowOnPage;
    private final boolean isPortrait;
    private final List<Lesson> lessons;
    private int startNewPage;
    private int groupOnPage;
    private final boolean printSum;


    private String vertBrderBold;
    private String vertBorder;
    private String border;
    private String borderBold;

    Random r = new Random();
    private String thinBorder;
    private String centerAlign;

    public ExerciseWriter(List<Tuple2<Lesson, List<List<List<Integer>>>>> exercises, String fileName, MainFormulaApp.PAGE_ORIENTATION page, boolean printSum, List<Lesson> lessons) {
        this.exercises = exercises;
        this.lessons = lessons;
        this.fileName = fileName +  (printSum? ".answered":"") + ".xls";
        this.page = page;
        isPortrait = page == MainFormulaApp.PAGE_ORIENTATION.PORTRAIT;
        rowOnPage = !isPortrait ? LANDSCAPE_ROW_ON_PAGE: 30;
        startNewPage = !isPortrait ? 15: 20;;

        thirdTitleColumn = !isPortrait ? LANDSCAPE_THIRD_TITLE_COLUMN: PORTRAIN_THIRD_TITLE_COLUMN;
        pageNumberColumn = thirdTitleColumn + 2;
        groupOnPage = !isPortrait ? 4: 7;

        this.printSum = printSum;

        vertBrderBold = VERT_STYLE_WITH_BORDER_BOLD + r.nextInt();
        vertBorder = VERT_STYLE_WITH_BORDER + r.nextInt();
        border = HOR_STYLE_WITH_BORDER + r.nextInt();
        borderBold = HOR_STYLE_WITH_BORDER_BOLD + r.nextInt();
        thinBorder = "THIN_BORDER" + r.nextInt();
        centerAlign = "CENTER_ALIGN" + r.nextInt();
    }

    public void write() {
        //SpreadSheet outSheet = SpreadSheet.createEmpty(new DefaultTableModel());
        Workbook workbook = WorkbookFactory.getWorkbook(WorkbookFactory.Library.POI, fileName);

        int pageNumber = 1;
        pageNumber = addPageWithName(workbook, pageNumber);
        pageNumber = addPageWithSchedule(workbook, pageNumber, exercises);
        int lastRowNumber = 0;

        Sheet sheet = null;
        boolean addedPageNumber = false;
        for (Tuple2<Lesson, List<List<List<Integer>>>> exercise: exercises) {

            Lesson lesson = exercise.getA();
            final int rowCount;
            //?????????? ??? ?????? ??????? ?? ????? ????????
            //????

            if (lastRowNumber > startNewPage || sheet == null) {
                System.out.println("Start new sheet " + lesson.getTitle());
                sheet = workbook.addSheet(lesson.getTitle(), isPortrait);
                lastRowNumber = 0;
                //add odd page
                if (!isPortrait) {
                    if (pageNumber % 2 == 0) {
                        pageNumber = addPageNumber(sheet, lastRowNumber, pageNumber);
                        addedPageNumber = true;
                    } else {
                        addedPageNumber = false;
                    }
                } else {
                    pageNumber = addPageNumber(sheet, lastRowNumber, pageNumber);
                }

            } else {

                System.out.println("Continue sheet " + " lastRowNumber " + lastRowNumber + ", new lesson " + lesson.getTitle());
            }
            rowCount = lastRowNumber + countRows(lesson.getSettings()) + 1;//1 for title
            System.out.println(" Defined row count " + rowCount + " lastRowNumber " + lastRowNumber );
            sheet.setRowCount(rowCount);


            setupColumns(sheet, lesson);
            for (int groupSeriesIndex = 0; groupSeriesIndex < exercise.getB().size(); groupSeriesIndex++) {
                List<List<Integer>> series = exercise.getB().get(groupSeriesIndex);
                Settings settings = lesson.getSettings().get(groupSeriesIndex);
                System.out.println("  Start new settings groupSeriesIndex " + groupSeriesIndex +  " lastRowNumber " + lastRowNumber );
                System.out.println("  Try print description  " + settings.description + " "  + settings.description1  + " " + settings.description2 +  " lastRowNumber " + lastRowNumber );
                lastRowNumber = setDescription(sheet, lastRowNumber, settings);

                for (int seriesIndex = 0; seriesIndex < series.size(); seriesIndex++) {
                    //set header of series
                    int column = seriesIndex + 1;
                    Cell headerSeries = getBorderedBoldCell(sheet, FIRST_COLUMN + column, lastRowNumber);
                    headerSeries.setValue(Integer.toString(seriesIndex + 1));
                }

                System.out.println("  Draw series header " + " lastRowNumber " + lastRowNumber );

                Cell numberHeader = getBorderedBoldCell(sheet, FIRST_COLUMN, lastRowNumber );

                numberHeader.setValue(Texts.NUMBER_SYMBOLL.getText());
                //merge the first cell
                int startMergeRow = lastRowNumber + 1;

                //todo check -1 for addSum;

                int rowToMerge = series.get(0).size() - 1 + (printSum ? -1:0);

//                sheet.merge(FIRST_COLUMN, startMergeRow, 0, rowToMerge);
//                System.out.println("  Merge startMergeRow " +  startMergeRow + " rowToMerge " + rowToMerge + " lastRowNumber " + lastRowNumber );
                //set border style for merged cells
                getVerBorderedCell(sheet, FIRST_COLUMN, startMergeRow ).setValue(Integer.toString(groupSeriesIndex + 1));
                getVerBorderedCell(sheet, FIRST_COLUMN, startMergeRow + 1).setValue("__мин.");
                getVerBorderedCell(sheet, FIRST_COLUMN, startMergeRow + 2).setValue("__сек.");
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
                            Cell cellAt = getVerBorderedCell(sheet, FIRST_COLUMN, stepIndex + lastRowNumber);
                           // cellAt.setValue(Integer.toString(stepIndex + 1));
                        }
                    }

                    for (int stepIndex = 0; stepIndex < steps.size(); stepIndex++) {
                        Integer value = steps.get(stepIndex);
                        Cell cellAt = getCell(sheet, FIRST_COLUMN + seriesIndex + 1, stepIndex + lastRowNumber);
                        cellAt.setValue(value);

                        //add sum cell
                        if (stepIndex == steps.size() - 1 && printSum) {
                            Style thinBorder = cellAt.getSheet().getWorkbook().getStyle(this.thinBorder, Style::setThinBorder);
                            thinBorder.setThinBorder();
                            cellAt.setStyle(thinBorder);
                        } else {

                            Style centerStyle = cellAt.getSheet().getWorkbook().getStyle(centerAlign, Style::setVertStyleWithBorder);
                            cellAt.setStyle(centerStyle);
                        }

                    }
                    //empty place for summ
                    if (!printSum) {
                        Cell cellAt = getCell(sheet, FIRST_COLUMN + seriesIndex + 1, steps.size() + lastRowNumber);
                        Style thinBorder = cellAt.getSheet().getWorkbook().getStyle(this.thinBorder, Style::setThinBorder);
                        cellAt.setStyle(thinBorder);
                        cellAt.setValue("");

                    }
                }

                //answer header of row
                int rowForSumma = lastRowNumber + (printSum ? -1:0) + series.get(0).size();
                System.out.println("  Draw summa " + rowForSumma );
                getBorderedCell(sheet, FIRST_COLUMN, rowForSumma).setValue(Texts.SUMMA.getText());

                if (!printSum) {
                    lastRowNumber++;//add empty row for answer
                }



                lastRowNumber+= series.iterator().next().size();//with step for answer
                //lastRowNumber ++;
                System.out.println("  The end of series " + " lastRowNumber " + lastRowNumber );
                //add page break
                //do not add last page break and description
                //todo worked code because false
                if (groupSeriesIndex % groupOnPage == groupOnPage - 1 && groupSeriesIndex < exercise.getB().size() - 1) {
                    System.out.println("  Add pagebreak " + " lastRowNumber " + lastRowNumber );

                    if (!isPortrait) {
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
                    } else {

                    }

                }

                //lastRowNumber = setDescription(sheet, lastRowNumber, settings);
            }
            //for sheet without page breaks
            if (!isPortrait) {
                if (!addedPageNumber && pageNumber % 2 == 1 && lastRowNumber >= rowOnPage) {
                    lastRowNumber++;
                    pageNumber = addPageNumber(sheet, lastRowNumber, pageNumber);
                }
            } else {
                getBorderedCell(sheet, FIRST_COLUMN, lastRowNumber).setValue("____мин.");
                getBorderedCell(sheet, FIRST_COLUMN+1, lastRowNumber).setValue("____сек.");
                lastRowNumber++;
            }

        }
        try {
            workbook.saveAs();
        } catch (IOException e) {
            System.out.println("Error in saving result." + e.getMessage());
        }
    }

    private int addPageWithSchedule(Workbook workbook, int pageNumber, List<Tuple2<Lesson, List<List<List<Integer>>>>> exercises) {
        Sheet sheet = workbook.addSheet("Schedule", true);
        sheet.setRowCount(rowOnPage);

//        for (int i = 0; i < rowOnPage; i++) {
//            sheet.setRowHeight(i, 30 );
//        }

        sheet.setColumnCount(7);
        sheet.setColumnSize(0, 3000);
        sheet.setColumnSize(1, 3000);
        sheet.setColumnSize(2, 6000);
        sheet.setColumnSize(3, 6000);
        sheet.setColumnSize(4, 7000);
        sheet.setColumnSize(5, 3500);
        sheet.setColumnSize(6, 3500);

        int columnNumber = 0;
        int lastRow = 0;
        for (int i = 0; i < 7; i++) {
            getBorderedCell(sheet, i, 1);//set border
        }
        sheet.merge(columnNumber, lastRow, 0, 1);
        getBorderedBoldCell(sheet, columnNumber++, lastRow).setValue(WEEK.getText());
        sheet.merge(columnNumber, lastRow, 0, 1);
        getBorderedBoldCell(sheet, columnNumber++, lastRow).setValue(DATE.getText());
        sheet.merge(columnNumber, lastRow, 0, 1);
        getBorderedBoldCell(sheet, columnNumber++, lastRow).setValue(CLASS_WORK.getText());
        sheet.merge(columnNumber, lastRow, 0, 1);
        getBorderedBoldCell(sheet, columnNumber++, lastRow).setValue(HOME_WORK.getText());
        sheet.merge(columnNumber, lastRow, 0, 1);
        getBorderedBoldCell(sheet, columnNumber++, lastRow).setValue(COMMENTS.getText());
        sheet.merge(columnNumber, lastRow, 1, 0);
        getBorderedBoldCell(sheet, columnNumber, lastRow).setValue(SIGNS.getText());
        getBorderedCell(sheet, columnNumber + 1, lastRow);//set border in left up corner

        getBorderedBoldCell(sheet, columnNumber ++, lastRow + 1).setValue(PARENTS.getText());
        getBorderedBoldCell(sheet, columnNumber ++, lastRow + 1).setValue(INSTRUCTOR.getText());

        lastRow+=2;

        //int firstWeek = (Book.EVEN == book)? 1 : 0;

        final int rows = 3;
        final int mergeRows = rows - 1;

        for(int i = 0; i< lessons.size(); i ++) {
            final Lesson lesson = lessons.get(i);
            final Settings settings = lesson.getSettings().get(0);
            for (int j = 0; j < columnNumber; j++) {
                sheet.merge(j, lastRow + rows *i, 0, mergeRows);
                getBorderedCell(sheet, j, lastRow + rows * i);
                for (int k = 0; k < mergeRows; k++) {
                    getBorderedCell(sheet, j, lastRow + rows * i + k);
                }
            }

            getBorderedCell(sheet, 0, lastRow + rows * i).setValue(settings.getDescription());
            getBorderedCell(sheet, 2, lastRow + rows * i).setValue(settings.getDescription1());
        }
        lastRow+= lessons.size() * rows;
        for (int j = 0; j < columnNumber; j++) {
                getBorderedCell(sheet, j, lastRow -1).setValue("test");
            }


        return pageNumber+1;
    }

    private int addPageWithName(Workbook workbook, int pageNumber) {
        Sheet sheet = workbook.addSheet("Name", isPortrait);
        sheet.setRowCount(rowOnPage);
        int nameColumn = (isPortrait)? 1: 3;
        sheet.setColumnCount(nameColumn);
        sheet.getCellAt(nameColumn, (isPortrait)? 3:2).setValue(STUDENT_NAME.getText());
        sheet.getCellAt(nameColumn, (isPortrait)? 5:4).setValue(INSTRUCTOR_NAME.getText());
        sheet.getCellAt(nameColumn, (isPortrait)? 7:6).setValue(CONTACTS.getText());
        return pageNumber + 1;
    }

    private int addPageNumber(Sheet sheet, int lastRowNumber, int pageNumber) {
        System.out.println("Add page number " + sheet.getTitle() + " row " + lastRowNumber + " pageNumber " + pageNumber);
        Cell cellAt2 = sheet.getCellAt(pageNumberColumn, lastRowNumber);
        cellAt2.setValue(pageNumber);
        return ++pageNumber;
    }

    private void setupColumns(Sheet sheet, Lesson lesson) {
        int columnCount = countColumns(lesson.getSettings());
        sheet.setColumnCount(FIRST_COLUMN + columnCount);
        int size = 2700;
        for (int i = 0; i < columnCount; i++) {

            if (i == 0) {
                sheet.setColumnSize(FIRST_COLUMN + i, size + 600);
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
            cellAt.setValue(settings.description + " " + settings.description1);

            Cell cellAt2 = sheet.getCellAt(FIRST_COLUMN + thirdTitleColumn, lastRowNumber);
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
        Cell cellAt = sheet.getCellAt(column, lastRowNumber);

        Style horStyleWithBorder = cellAt.getSheet().getWorkbook().getStyle(border, Style::setThinBorder);
//        horStyleWithBorder.setHorStyleWithBorder();
//        horStyleWithBorder.setVertStyleWithBorder();
        cellAt.setStyle(horStyleWithBorder);
        return cellAt;
    }

    private Cell getBorderedBoldCell(Sheet sheet, int column, int lastRowNumber) {
        Cell cellAt = sheet.getCellAt(column, lastRowNumber);

        Style horStyleWithBorder = cellAt.getSheet().getWorkbook().getStyle(borderBold, Style::setThinBorder);
        horStyleWithBorder.setBold();
        horStyleWithBorder.setHorStyleWithBorder();

        cellAt.setStyle(horStyleWithBorder);
        return cellAt;
    }


    private Cell getVerBorderedCell(Sheet sheet, int column, int lastRowNumber) {
        Cell cellAt = sheet.getCellAt(column, lastRowNumber);


        Style vertStyleWithBorder = cellAt.getSheet().getWorkbook().getStyle(vertBorder, Style::setVertStyleWithBorder);
        cellAt.setStyle(vertStyleWithBorder);

        return cellAt;
    }
    private Cell getVerBorderedBoldCell(Sheet sheet, int column, int lastRowNumber) {

        Cell cellAt = sheet.getCellAt(column, lastRowNumber);
        Style vertStyleWithBorder = cellAt.getSheet().getWorkbook().getStyle(vertBrderBold, Style::setVertStyleWithBorder);
        cellAt.setStyle(vertStyleWithBorder);
        vertStyleWithBorder.setBold();
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
