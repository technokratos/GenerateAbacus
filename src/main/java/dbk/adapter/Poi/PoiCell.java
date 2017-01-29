package dbk.adapter.Poi;

import dbk.adapter.Cell;
import dbk.adapter.Sheet;
import dbk.adapter.Workbook;
import dbk.odf.ExerciseWriter;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;

import java.awt.*;

/**
 * Created by dbk on 04-Oct-16.
 */
public class PoiCell extends Cell {
    private final HSSFCell cell;
    private static HSSFCellStyle style;
    private final Sheet sheet;


    public PoiCell(HSSFCell cell, Sheet sheet) {
        super(sheet);
        this.cell = cell;
        this.sheet = sheet;

    }



    @Override
    public void setValue(String s) {
        cell.setCellValue(s);
    }

    @Override
    public Sheet getSheet() {
        return sheet;
    }

    @Override
    public void setBackgroundColor(Color lightGray) {
        getStyle();
        cell.getCellStyle().setFillBackgroundColor(new HSSFColor.BRIGHT_GREEN().getIndex() );
        //cell.setCellStyle(new HSSFCellStyle());
//        cell.getCellStyle().setBorderBottom(CellStyle.BORDER_DOUBLE);
//        cell.getCellStyle().setBorderTop(CellStyle.BORDER_DOUBLE);
//        //cell.getCellStyle().setBorderRight(CellStyle.BORDER_DOUBLE);
//        //cell.getCellStyle().setBorderLeft(CellStyle.BORDER_DOUBLE);
    }
    public void setHorStyleWithBorder() {
        Workbook workbook = sheet.getWorkbook();
        HSSFCellStyle style =  ((PoiWorkbook) workbook).getHorBorderedStyle();
        //style.setFont(((PoiWorkbook) workbook).getNumOfColumnFont());

        cell.setCellStyle(style);
    }

    public void setVertStyleWithBorder() {
        Workbook workbook = sheet.getWorkbook();
        HSSFCellStyle style =  ((PoiWorkbook) workbook).getVerBorderedStyle();
        //style.setFont(((PoiWorkbook) workbook).getNumOfColumnFont());
        cell.setCellStyle(style);
        cell.getCellStyle().setBorderBottom(CellStyle.BORDER_MEDIUM);
    }
    public void setThinBorder(){
        int size = ExerciseWriter.MAIN_TEXT_SIZE;
        //HSSFCellStyle style =  ((PoiWorkbook) workbook).getVerBorderedStyle(); getThinBorderedStyle()
        setSize(size);
    }

    private void setSize(int size) {
        Workbook workbook = sheet.getWorkbook();
        HSSFCellStyle style =((PoiWorkbook) workbook).getFontBySize(size);
        style.setWrapText(true);
        cell.setCellStyle(style);
    }

    private void getStyle() {
        if (this.style == null) {
            HSSFCellStyle style = cell.getSheet().getWorkbook().createCellStyle();
            this.style = style;
            cell.setCellStyle(style);
        }
    }

    @Override
    public void setValue(Number value) {
        cell.setCellValue(value.doubleValue());
    }

    @Override
    public void setFontSize(int size) {
//        getStyle();
////        PoiWorkbook workbook = (PoiWorkbook) this.getSheet().getWorkbook();
////        HSSFFont font = workbook.getWorkbook().createFont();
////
////        //font.setFontHeight((short) size);
////        font.setFontHeightInPoints((short) size);
//        HSSFFont font = getFont(size);
//        style.setFont(font);
//        cell.setCellStyle(style);
        //setSize(size);

        PoiWorkbook workbook = (PoiWorkbook) this.getSheet().getWorkbook();
        cell.setCellStyle(workbook.getFontBySize(size));


    }


}
