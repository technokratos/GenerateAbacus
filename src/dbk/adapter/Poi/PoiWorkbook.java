package dbk.adapter.Poi;

import dbk.adapter.Sheet;
import dbk.adapter.Workbook;
import dbk.odf.ExerciseWriter;
import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dbk on 04-Oct-16.
 */
public class PoiWorkbook extends Workbook {


    public static final short VERT_HEADER_FONT_SIZE = (short) 20;
    private final HSSFWorkbook workbook;
    String fileName;
    private HSSFCellStyle horBorderedStyle;
    private HSSFCellStyle verBorderedStyle;
    private HSSFCellStyle thinBorderedStyle;
    private HSSFFont numOfColumnFont;
    static Map<Integer, HSSFCellStyle> fontMap = new HashMap<>();

    public HSSFCellStyle getFontBySize(int size){
        HSSFCellStyle style = fontMap.get(size);
        if (style == null) {
            style = getThinBorderedStyle();//workbook.createCellStyle();
            HSSFFont font = workbook.createFont();
            font.setFontHeightInPoints((short) size);
            style.setFont(font);
            style.setAlignment(CellStyle.ALIGN_CENTER);
            style.setVerticalAlignment(CellStyle.ALIGN_CENTER);

            fontMap.put(size, style);
        }
        return style;
    }

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
    public Sheet addSheet(String title) {

        HSSFSheet sheet = workbook.createSheet(title);
        sheet.getPrintSetup().setLandscape(true);
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
        workbook.close();
    }

    public HSSFWorkbook getWorkbook() {
        return workbook;
    }

    public HSSFCellStyle getHorBorderedStyle() {
        if (horBorderedStyle == null) {
            horBorderedStyle = workbook.createCellStyle();
            horBorderedStyle.setBorderBottom(CellStyle.BORDER_THIN);
            horBorderedStyle.setBorderTop(CellStyle.BORDER_THIN);
            horBorderedStyle.setBorderRight(CellStyle.BORDER_THIN);
            horBorderedStyle.setBorderLeft(CellStyle.BORDER_THIN);
            horBorderedStyle.setAlignment(CellStyle.ALIGN_CENTER);
            horBorderedStyle.setVerticalAlignment(CellStyle.ALIGN_CENTER);
            HSSFFont font = workbook.createFont();
            font.setFontHeightInPoints(ExerciseWriter.VER_HEADER_FONT_SIZE);
            font.setBold(true);
            horBorderedStyle.setFont(font);
        }
        return horBorderedStyle;
    }
    public HSSFCellStyle getVerBorderedStyle() {
        if (verBorderedStyle == null) {
            verBorderedStyle = workbook.createCellStyle();
            verBorderedStyle.setBorderBottom(CellStyle.BORDER_THIN);
            verBorderedStyle.setBorderTop(CellStyle.BORDER_THIN);
            verBorderedStyle.setBorderRight(CellStyle.BORDER_THIN);
            verBorderedStyle.setBorderLeft(CellStyle.BORDER_THIN);
            verBorderedStyle.setAlignment(CellStyle.ALIGN_CENTER);
            verBorderedStyle.setVerticalAlignment(CellStyle.ALIGN_CENTER);
            HSSFFont font = workbook.createFont();
            font.setFontHeightInPoints(ExerciseWriter.HOR_HEADER_FONT_SIZE);
            verBorderedStyle.setFont(font);
        }
        return verBorderedStyle;
    }

    public HSSFCellStyle getThinBorderedStyle() {
        if (thinBorderedStyle == null) {
            thinBorderedStyle = workbook.createCellStyle();
            thinBorderedStyle.setBorderBottom(CellStyle.BORDER_THIN);
            thinBorderedStyle.setBorderTop(CellStyle.BORDER_THIN);
            thinBorderedStyle.setBorderRight(CellStyle.BORDER_THIN);
            thinBorderedStyle.setBorderLeft(CellStyle.BORDER_THIN);
            thinBorderedStyle.setAlignment(CellStyle.ALIGN_CENTER);
            thinBorderedStyle.setVerticalAlignment(CellStyle.ALIGN_CENTER);
        }
        return thinBorderedStyle;
    }

    public HSSFFont getNumOfColumnFont() {
        if (numOfColumnFont == null) {
            numOfColumnFont = workbook.createFont();
            numOfColumnFont.setBold(true);
            numOfColumnFont.setFontHeightInPoints(ExerciseWriter.HOR_HEADER_FONT_SIZE);
        }
        return numOfColumnFont;
    }
}
