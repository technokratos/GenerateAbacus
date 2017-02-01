package dbk.adapter;

import java.awt.*;

/**
 * Created by dbk on 04-Oct-16.
 */
public abstract class Cell {

    private Sheet sheet;
    private Style style;

    public Cell(Sheet sheet) {
        this.sheet = sheet;
    }

    public Sheet getSheet() {
        return sheet;
    }
    public abstract void setValue(String s);

    public abstract void setValue(Number value);

    public abstract void setFontSize(int size);

    public void setStyle(String styleName) {
        Style style = getSheet().getWorkbook().getStyle(styleName, null);
        this.style = style;
        setStyle(style);
    }

    public abstract void setStyle(Style style);

    public abstract void setHeight(int size);


}
