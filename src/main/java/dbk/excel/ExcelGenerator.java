package dbk.excel;

/**
 * Created by dbk on 18-Aug-16.
 */
public class ExcelGenerator {

//    //FileInputStream file = new FileInputStream(new File("C:\\test.xls"));
//
//    //Get the workbook instance for XLS file
//    //final HSSFWorkbook workbook;// = new HSSFWorkbook(file);
//    final HSSFWorkbook workbook;// = new HSSFWorkbook(file);
//
//    //static Color DEFAULT = new HSSFColor.AUTOMATIC();
//    static short DEFAULT = 21;
//    List<Lesson> levels = new ArrayList<>();
//
//    public ExcelGenerator(String fileName) {
//        HSSFWorkbook workbook = null;
//        try{
//            FileInputStream file = new FileInputStream(new File(fileName));
//            workbook = new HSSFWorkbook(file);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        this.workbook = workbook;
//    }
//
//    public void read(){
//        for (Sheet sheet : workbook) {
//            final Lesson level = new Lesson();
//            level.setTitle(sheet.getSheetName());
//            levels.add(level);
//            readRows(level, sheet);
//        }
//    }
//
//    private void readRows(Lesson level, Sheet sheet) {
//
//        Row firstRow = sheet.getRow(0);
//        for(Row row: sheet){
//            for(Cell cell: row) {
//                CellStyle cellStyle = cell.getCellStyle();
//                ///System.out.print(((HSSFCell)cell).getRawValue() + " " + cellStyle.getIndex() + ", ");
//                if(DEFAULT != cellStyle.getIndex()) {
//                    //HSSFColor HSSFColor =  HSSFColor.toHSSFColor(new HSSFColor.AUTOMATIC())
//                    Cell headerCell = firstRow.getCell(cell.getColumnIndex());
//                    if (headerCell == null) {
//                        continue;
//                    }
//                    Integer firstValue = (int) headerCell.getNumericCellValue();
//                    Cell firstCell =  row.getCell(0);
//                    if (firstCell == null){// || firstCell.getRawValue() == null) {
//                        continue;
//                    }
//                    Integer secondValue = (int) firstCell.getNumericCellValue();
//                    level.put(firstValue, secondValue);
//
//                }
//
//            }
//            System.out.println();
//        }
//    }
}
