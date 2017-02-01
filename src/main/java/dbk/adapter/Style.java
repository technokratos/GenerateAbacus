package dbk.adapter;

import java.awt.*;
import java.util.Map;

/**
 * Created by denis on 31.01.17.
 */
public abstract class Style {


    public abstract void setFontSize(int size);
    public abstract void setBold();
    public abstract void alignmentCenter();

    public abstract void setBackgroundColor(Color lightGray) ;

    public abstract void setHorStyleWithBorder();
    public abstract void setVertStyleWithBorder();

    public abstract void setThinBorder();


}
