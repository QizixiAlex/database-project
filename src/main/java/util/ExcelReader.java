package util;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelReader {
    String filePath;
    int sheetIndex;
    public ExcelReader(String filePath, int sheetIndex) {
        this.filePath = filePath;
        this.sheetIndex = sheetIndex;
    }

    public ExcelReader(String filePath){
        this.filePath = filePath;
        this.sheetIndex = 0;
    }

    public List<List<String>> getRows() {
        List<List<String>> rows = new ArrayList<List<String>>();
        try {
            POIFSFileSystem fs=new POIFSFileSystem(new FileInputStream(this.filePath));
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(this.sheetIndex);

            for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
                HSSFRow row = sheet.getRow(i);

                List<String> cellListOfCurrentRow = new ArrayList<String>();
                for (int j = row.getFirstCellNum(); j < row.getLastCellNum(); j++) {
                    HSSFCell cell = row.getCell((short)j);
                    if (cell != null) {
                        int cellType = cell.getCellType();
                        String cellContent = "";
                        switch (cellType){
                            case Cell.CELL_TYPE_BLANK:
                                cellContent = "null";
                                break;
                            case Cell.CELL_TYPE_BOOLEAN:
                                cellContent = String.valueOf(cell.getBooleanCellValue());
                                break;
                            case Cell.CELL_TYPE_NUMERIC:
                                cellContent = String.valueOf(cell.getNumericCellValue());
                                break;
                            case Cell.CELL_TYPE_ERROR:
                                cellContent = String.valueOf(cell.getErrorCellValue());
                                break;
                            case Cell.CELL_TYPE_FORMULA:
                                cellContent = cell.getCellFormula();
                                break;
                            case Cell.CELL_TYPE_STRING:
                                cellContent = cell.getStringCellValue();
                                break;
                            default:
                                cellContent = "null";
                                break;
                        }
                        if (cellContent.length() == 0){
                            cellContent = "null";
                        }
                        cellListOfCurrentRow.add(cellContent);
                    } else {
                        cellListOfCurrentRow.add("null");
                    }

                }

                rows.add(cellListOfCurrentRow);
            }
            fs.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return rows;
    }

}
