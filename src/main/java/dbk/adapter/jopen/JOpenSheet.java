package dbk.adapter.jopen;

import dbk.adapter.Cell;
import dbk.adapter.Sheet;
import dbk.adapter.Workbook;

/**
 * Created by dbk on 18-Oct-16.
 */
public class JOpenSheet extends Sheet {
    public JOpenSheet(Workbook jOpenWorkbook) {
        super(jOpenWorkbook);

    }

    @Override
    public void setColumnCount(int i) {

    }

    @Override
    public void setRowCount(int i) {

    }

    @Override
    public Cell getCellAt(int columnNumber, int rowNumber) {
        return null;
    }

    @Override
    public void merge(int startColumn, int startRow, int columns, int rows) {

    }

    @Override
    public void addPageBreak(int lastRowNumber) {

    }

    @Override
    public void setColumnSize(int columnNumber, int size) {

    }
}
