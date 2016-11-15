package dbk.adapter;

import java.awt.*;

/**
 * Created by dbk on 04-Oct-16.
 */
public abstract class Cell {

    private Sheet sheet;

    public Cell(Sheet sheet) {
        this.sheet = sheet;
    }

    public Sheet getSheet() {
        return sheet;
    }
    public abstract void setValue(String s);

    public abstract void setBackgroundColor(Color lightGray) ;

    public abstract void setHorStyleWithBorder();
    public abstract void setVertStyleWithBorder();

    public abstract void setValue(Number value);

    public abstract void setThinBorder();

    public abstract void setFontSize(int size);
}
