package dbk.adapter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dbk on 04-Oct-16.
 */
public abstract class Workbook {

    private static Map<String, Style> styleMap = new HashMap<>();

    public abstract Sheet addSheet(String title, boolean landScale);

    public abstract void saveAs() throws IOException;


    public Style getStyle(String styleName, StyleInitiator initiator ) {
        Style cachedStyle = styleMap.get(styleName);
        if (cachedStyle == null) {
            Style style = initStyle();
            if (initiator != null) {
                initiator.initStyle(style);
            }
            addStyle(styleName, style);
            return style;
        }
        return cachedStyle;
    }

//    public Style getStyle(String styleName) {
//        return getStyle(styleName, null);
//    }

    protected abstract Style initStyle();

    public void addStyle(String styleName, Style style) {
        styleMap.put(styleName, style);

    }


}
