package dbk.adapter.jopen;

import dbk.adapter.Sheet;
import dbk.adapter.Workbook;

import java.io.IOException;

/**
 * Created by dbk on 18-Oct-16.
 */
public class JOpenWorkbook extends Workbook {
    private final String fileName;

    public JOpenWorkbook(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public Sheet addSheet(String title) {
        return new JOpenSheet(this);
    }

    @Override
    public void saveAs() throws IOException {

    }
}
