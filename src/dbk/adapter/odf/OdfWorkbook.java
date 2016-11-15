package dbk.adapter.odf;

import dbk.adapter.Sheet;
import dbk.adapter.Workbook;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;

import java.io.File;
import java.io.IOException;

/**
 * Created by dbk on 04-Oct-16.
 */
public class OdfWorkbook extends Workbook {
    private final String fileName;
    OdfSpreadsheetDocument document;

    public OdfWorkbook(String fileName) {
        this.fileName = fileName;
        try {
            document = OdfSpreadsheetDocument.newSpreadsheetDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public Sheet addSheet(String title) {
        return new OdfSheet(this);
    }

    @Override
    public void saveAs() throws IOException {
        try {
            document.save(new File(fileName));
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
