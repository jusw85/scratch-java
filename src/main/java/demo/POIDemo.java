package demo;

import com.google.common.base.Joiner;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class POIDemo {

    public static void main(String[] args) throws Exception {
        Workbook workbook = WorkbookFactory.create(new File("etc/poi.xlsx"));
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        Sheet sheet = workbook.getSheetAt(0);

        int rowNumber = findRow(sheet, "Names", false);

        Row row;
        Cell cell;
        while ((row = sheet.getRow(rowNumber)) != null &&
                (cell = row.getCell(0)) != null &&
                cell.getCellTypeEnum() != CellType.BLANK) {
            String rowValues = stringifyRowValues(sheet, rowNumber, 2, evaluator);
            System.out.println(rowValues);
            rowNumber++;
        }

        rowNumber = findRow(sheet, "Total", false);
        String rowValues = stringifyRowValues(sheet, rowNumber, 2, evaluator);
        System.out.println(rowValues);
    }

    public static String stringifyRowValues(Sheet sheet, int rowNumber, int numColumns, FormulaEvaluator evaluator) {
        Row row = sheet.getRow(rowNumber);
        List<String> vals = new ArrayList<>();
        for (int i = 0; i < numColumns; i++) {
            Cell cell = row.getCell(i);
            String cellValue = getCellValue(cell, evaluator);
            vals.add(cellValue);
        }
        return Joiner.on(",").join(vals);
    }

    public static int findRow(Sheet sheet, String searchString, boolean regex) {
        for (Row row : sheet) {
            for (Cell cell : row) {
                if (cell.getCellTypeEnum() == CellType.STRING) {
                    String cellContent = cell.getRichStringCellValue().getString().trim();
                    if (regex) {
                        if (cellContent.matches(searchString)) {
                            return row.getRowNum();
                        }
                    } else {
                        if (cellContent.equals(searchString)) {
                            return row.getRowNum();
                        }
                    }
                }
            }
        }
        return -1;
    }

    public static String getCellValue(Sheet sheet, String ref, FormulaEvaluator evaluator) {
        CellReference cellReference = new CellReference(ref);
        Row row = sheet.getRow(cellReference.getRow());
        Cell cell = row.getCell(cellReference.getCol());

        return getCellValue(cell, evaluator);
    }

    public static String getCellValue(Cell cell, FormulaEvaluator evaluator) {
        CellType cellType;
        if (cell.getCellTypeEnum() == CellType.FORMULA) {
            cellType = evaluator.evaluateFormulaCellEnum(cell);
        } else {
            cellType = cell.getCellTypeEnum();
        }

        String result = null;
        switch (cellType) {
            case BOOLEAN:
                result = String.valueOf(cell.getBooleanCellValue());
                break;
            case STRING:
                result = String.valueOf(cell.getStringCellValue());
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    result = cell.getDateCellValue().toString();
                } else {
                    result = String.valueOf(cell.getNumericCellValue());
                }
                break;
            case FORMULA:
                result = cell.getCellFormula();
                break;
            case ERROR:
                result = String.valueOf(cell.getErrorCellValue());
                break;
            case BLANK:
            default:
                // blank
                break;
        }
        return result;
    }

}
