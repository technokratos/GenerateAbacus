package dbk.adapter.Poi;

import dbk.adapter.Cell;
import dbk.adapter.Sheet;
import dbk.adapter.Style;
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
    public void setValue(Number value) {
        cell.setCellValue(value.doubleValue());
    }

    @Override
    public void setFontSize(int size) {

        String styleName = "FONT_SIZE_" + size;
        Style style = this.getSheet().getWorkbook().getStyle(styleName, style1 -> style1.setFontSize(size));
        style.setFontSize(size);

    }

    @Override
    public void setStyle(Style style) {
        cell.setCellStyle(((PoiStyle)style).getStyle());
    }

    @Override
    public void setHeight(int size) {
        cell.getRow().setHeight((short) size);
    }
}
