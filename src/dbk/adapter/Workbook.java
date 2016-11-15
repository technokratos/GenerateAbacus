package dbk.adapter;

import java.io.IOException;

/**
 * Created by dbk on 04-Oct-16.
 */
public abstract class Workbook {
    public abstract Sheet addSheet(String title);

    public abstract void saveAs() throws IOException;



}
