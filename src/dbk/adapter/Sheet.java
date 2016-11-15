package dbk.adapter;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

/**
 * Created by dbk on 04-Oct-16.
 */
public abstract class Sheet {
    private final Workbook workbook;

    public Sheet(Workbook workbook) {
        this.workbook = workbook;
    }

    public Workbook getWorkbook(){
        return workbook;
    }

    public abstract void setColumnCount(int i) ;

    public abstract void setRowCount(int i);

    public abstract Cell getCellAt(int columnNumber, int rowNumber);

    public abstract void merge(int startColumn, int startRow, int columns, int rows);

    public abstract void addPageBreak(int lastRowNumber);

    public abstract void setColumnSize(int columnNumber, int size);

    public String getTitle(){throw new NotImplementedException();}

    public void setRowHeight(int size, int rowNumber) {throw new NotImplementedException();}
}
