package dbk.texts;

import dbk.adapter.odf.OdfWorkbook;
import org.jopendocument.dom.spreadsheet.MutableCell;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.odftoolkit.odfdom.doc.table.OdfTableCell;
import org.odftoolkit.odfdom.doc.table.OdfTableRow;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dbk on 01-Nov-16.
 */
public enum Texts {

    NUMBER_SYMBOLL,// = "number_symbol";
    SUMMA,


    STUDENT_NAME,
    INSTRUCTOR_NAME,
    CONTACTS,

    WEEK,
    TH_WEEK,
    DATE,
    CLASS_WORK,
    HOME_WORK,
    COMMENTS,
    SIGNS,
    PARENTS,
    INSTRUCTOR;// = "summa";




    public String getText(){
        return getText(this.name());
    }

    static HashMap<String, String> map = new HashMap<>();

    public static final String TEXTS_ODS = "texts.ods";

    static {

        try {
            ClassLoader classLoader = Texts.class.getClassLoader();
            File f = new File(classLoader.getResource(TEXTS_ODS).getFile());

            SpreadSheet document = SpreadSheet.createFromFile(f);
            Sheet sheet = document.getSheet(0);


                for (int i = 0; i < sheet.getRowCount(); i++) {

                    MutableCell<SpreadSheet> cell = sheet.getCellAt(0, i);
                    if (cell != null  && cell.getValue() != null && !cell.getValue().toString().isEmpty()) {
                        String key = cell.getValue().toString();
                        MutableCell<SpreadSheet> cellValue = sheet.getCellAt(1, i);
                        if (cellValue != null  && cellValue.getValue().toString() != null && !cellValue.getValue().toString().isEmpty()) {
                            map.put(key, cellValue.getValue().toString());
                        }
                    } else {
                        break;
                    }
                }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String getText(String key) {
        String value = map.get(key);
        return (value != null) ? value : key;
    }
}
