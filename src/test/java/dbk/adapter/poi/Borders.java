package dbk.adapter.poi;

/**
 * Created by dbk on 04-Oct-16.
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import org.apache.poi.hssf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;

public class Borders {
    public Borders() {
    }

    public static void main(String[] args) throws IOException {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("new sheet");
        HSSFRow row = sheet.createRow(1);
        HSSFCell cell = row.createCell(1);
        cell.setCellValue(4.0D);
        HSSFCellStyle style = wb.createCellStyle();
        style.setBorderBottom((short)1);
        style.setBottomBorderColor((short)8);
        style.setBorderLeft((short)1);
        style.setLeftBorderColor((short)17);
        style.setBorderRight((short)1);
        style.setRightBorderColor((short)12);
        style.setBorderTop((short)8);
        style.setTopBorderColor((short)53);
        style.setFillBackgroundColor((short) 8);
        cell.setCellStyle(style);
        FileOutputStream fileOut = new FileOutputStream("workbook.xls");
        wb.write(fileOut);
        fileOut.close();
    }
}
