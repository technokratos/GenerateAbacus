package dbk.adapter.odf;

import dbk.adapter.Cell;
import dbk.adapter.Sheet;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;

/**
 * Created by dbk on 18-Oct-16.
 */
public class OdfSheet extends Sheet {
    public OdfSheet(OdfWorkbook document) {
        super(document);

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
