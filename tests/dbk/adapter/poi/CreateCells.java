package dbk.adapter.poi;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by dbk on 04-Oct-16.
 */
public class CreateCells {
    public CreateCells() {
    }

    public static void main(String[] args) throws IOException {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("new sheet");
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue(1.0D);
        row.createCell(1).setCellValue(1.2D);
        row.createCell(2).setCellValue("This is a string");
        row.createCell(3).setCellValue(true);
        FileOutputStream fileOut = new FileOutputStream("workbook.xls");
        wb.write(fileOut);
        fileOut.close();
        wb.close();
    }
}