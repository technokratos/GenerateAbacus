package dbk.adapter;

import dbk.adapter.Poi.PoiWorkbook;
import dbk.adapter.jopen.JOpenWorkbook;
import dbk.adapter.odf.OdfWorkbook;

/**
 * Created by dbk on 04-Oct-16.
 */
public class WorkbookFactory {

    public static Workbook getWorkbook(Library library, String fileName) {

        if (library == Library.OpenDocument) {
            //SpreadSheet outSheet = SpreadSheet.createEmpty(new DefaultTableModel());
            return new JOpenWorkbook(fileName);
        } else if (library == Library.ODF) {
            return new OdfWorkbook(fileName);
        }
        return new PoiWorkbook(fileName);
    }
    public enum Library{
        OpenDocument,
        POI,
        ODF
    }
}
