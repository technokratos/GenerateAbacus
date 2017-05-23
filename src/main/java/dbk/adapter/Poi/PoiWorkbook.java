package dbk.adapter.Poi;

import dbk.adapter.Sheet;
import dbk.adapter.Style;
import dbk.adapter.Workbook;
import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.PrintSetup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by dbk on 04-Oct-16.
 */
public class PoiWorkbook extends Workbook {


    public static final short VERT_HEADER_FONT_SIZE = (short) 20;
    private final HSSFWorkbook workbook;
    String fileName;




    public PoiWorkbook(String fileName) {
//        workbook = HSSFWorkbook.create(InternalWorkbook.createWorkbook());
        HSSFWorkbook workbook = null;

        this.fileName = fileName;
        try {
            File file1 = new File(fileName);
            if (!file1.exists()) {
                if (!file1.createNewFile()) {
                    throw new IllegalStateException("File not created " + fileName);
                }
            }
//            FileInputStream file = new FileInputStream(file1);
//            workbook = new HSSFWorkbook(file);
            workbook = HSSFWorkbook.create(InternalWorkbook.createWorkbook());


        } catch (IOException e) {
            e.printStackTrace();
        }
        this.workbook = workbook;
    }

    @Override
    public Sheet addSheet(String title, boolean landScale) {

        HSSFSheet sheet = workbook.createSheet(title);

        sheet.getPrintSetup().setLandscape(landScale);
        //sheet.getPrintSetup().setPaperSize(landScale?PrintSetup.A4_PAPERSIZE:PrintSetup.A4_ROTATED_PAPERSIZE);
        sheet.getPrintSetup().setPaperSize(PrintSetup.A4_ROTATED_PAPERSIZE);
        return new PoiSheet(sheet, this);
    }

    @Override
    public void saveAs() throws IOException {
//        workbook.close();
//        byte[] bytes = workbook.getBytes();
//        FileOutputStream file = new FileOutputStream(new File(fileName));
//        file.write(bytes);
//        file.close();

        FileOutputStream fileOut = new FileOutputStream(fileName);
        workbook.write(fileOut);
        fileOut.close();
        //workbook.close();
    }

    @Override
    protected Style initStyle() {
        HSSFCellStyle cellStyle = getWorkbook().createCellStyle();
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        return new PoiStyle(cellStyle, this);

    }

    public HSSFWorkbook getWorkbook() {
        return workbook;
    }

    }
