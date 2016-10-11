package test;

import com.opencsv.CSVWriter;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelHelper {

    private String filename;
    private Workbook workbook;
    private Sheet sheet;
    private FormulaEvaluator evaluator;

    public ExcelHelper(String filename) throws IOException, InvalidFormatException {
        this.filename = filename;
    }

    public static void main(String[] args) throws Exception {

//        String foldername = "Vallianz Charlton";
        for (File f0 : new File("./dpr2/").listFiles()) {
            String foldername = f0.getName();

            for (File f : new File("./dpr2/" + foldername).listFiles()) {
                String filename = f.getName();
//            if (filename.startsWith("Morning Report")) {
                System.out.println(filename);

                ExcelHelper poi = new ExcelHelper(f.getAbsolutePath());
                try {
                    poi.init(0);
                } catch (Exception e) {
                    continue;
                }
                if (!poi.containsCrew()) {
                    continue;
                }
                List<String[]> results = poi.getCrewDetails();

                String outFileName = FilenameUtils.removeExtension(filename) + ".csv";
                File outFile = new File("./dpr3/" + foldername, outFileName);
                outFile.getParentFile().mkdirs();
                FileWriter fileWriter = new FileWriter(outFile);

                CSVWriter csvwriter = new CSVWriter(fileWriter);
                csvwriter.writeAll(results);
                csvwriter.close();
//            }
            }
        }
    }

    private boolean containsCrew() {
        int row = findRow("(?i).*crew details.*", true);
        return row != 0;
    }

    private List<String[]> getCrewDetails() {
        List<String[]> results = new ArrayList<>();

        int row = findRow("(?i).*crew details.*", true);
        while (true) {
            Row header = sheet.getRow(++row);

            if (header == null) {
                break;
            }
            Cell cell0 = header.getCell(0);
            if (cell0 == null || cell0.getCellType() == Cell.CELL_TYPE_BLANK) {
                break;
            }
            Cell cell3 = header.getCell(3);
            if (cell3 == null || cell3.getCellType() == Cell.CELL_TYPE_BLANK) {
                break;
            }

            List<String> resultRow = new ArrayList<>();
            for (Cell cell : header) {
                if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {
                    break;
                }
                String value = getCellValue(cell);
                resultRow.add(value);
            }
            String[] strings = resultRow.toArray(new String[resultRow.size()]);
            results.add(strings);
        }
        return results;
    }

    public int findRow(String searchString, boolean regex) {
        for (Row row : sheet) {
            for (Cell cell : row) {
                if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
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
        return 0;
    }

    public void init(int sheetNumber) throws IOException, InvalidFormatException {
        if (workbook == null) {
            workbook = WorkbookFactory.create(new File(filename));
            evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            sheet = workbook.getSheetAt(sheetNumber);
        }
    }

    public String getCellValue(String ref) {
        return getCellValue(ref, 0);
    }

    public String getCellValue(String ref, int sheetNumber) {
        if (workbook == null) {
            return null;
        }

        CellReference cellReference = new CellReference(ref);
        Row row = sheet.getRow(cellReference.getRow());
        Cell cell = row.getCell(cellReference.getCol());

        return getCellValue(cell);
    }

    public String getCellValue(Cell cell) {
        int cellType;
        if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
            cellType = evaluator.evaluateFormulaCell(cell);
        } else {
            cellType = cell.getCellType();
        }

        String result = null;
        switch (cellType) {
            case Cell.CELL_TYPE_BOOLEAN:
                result = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_STRING:
                result = String.valueOf(cell.getStringCellValue());
                break;
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    result = cell.getDateCellValue().toString();
                } else {
                    result = String.valueOf(cell.getNumericCellValue());
                }
                break;
            case Cell.CELL_TYPE_FORMULA:
                result = cell.getCellFormula();
                break;
            case Cell.CELL_TYPE_ERROR:
                result = String.valueOf(cell.getErrorCellValue());
                break;
            case Cell.CELL_TYPE_BLANK:
            default:
                // blank
                break;
        }
        return result;
    }

}
