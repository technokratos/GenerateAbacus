package dbk.adapter.Poi;

import dbk.adapter.Cell;
import dbk.adapter.Sheet;
import dbk.adapter.Workbook;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * Created by dbk on 04-Oct-16.
 */
public class PoiSheet extends Sheet {
    private final HSSFSheet sheet;

    public PoiSheet(HSSFSheet sheet, Workbook workbook) {
        super(workbook);
        this.sheet = sheet;

    }

    @Override
    public void setColumnCount(int i) {
        //sheet.column

    }

    @Override
    public void setRowCount(int i) {
        for (int j = 0; j <= i; j++) {
            if (sheet.getRow(0)==null) {
                sheet.createRow(i);
            }
        }
    }

    @Override
    public Cell getCellAt(int columnNumber, int rowNumber) {
        if (sheet.getRow(rowNumber) == null) {
            sheet.createRow(rowNumber);
        }
        HSSFRow row = sheet.getRow(rowNumber);
        HSSFCell cell = row.getCell(columnNumber);
        if (cell == null) {
            cell = row.createCell(columnNumber);
        }


        return new PoiCell(cell, this);
    }


    @Override
    public void merge(int startColumn, int startRow, int columns, int rows) {
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow + rows, startColumn, startColumn + columns));
    }

    @Override
    public void addPageBreak(int lastRowNumber) {
        sheet.setRowBreak(lastRowNumber);
    }

    @Override
    public void setColumnSize(int columnNumber, int size) {
        sheet.setColumnWidth(columnNumber, size);
    }

    @Override
    public String getTitle() {
        return sheet.getSheetName();
    }

    @Override
    public void setRowHeight(int size, int rowNumber) {
        HSSFRow row;
        if ((row = sheet.getRow( rowNumber ))== null) {
            row = sheet.createRow(rowNumber);

        }
        row.setHeightInPoints(size);
    }
}
