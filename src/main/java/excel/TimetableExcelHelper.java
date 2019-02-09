package excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import shedule.CellData;
import shedule.Classes;
import shedule.KeyCell;
import shedule.TimetableData;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimetableExcelHelper {

    public static String FILE_OUTPUT_NAME = "output";
    public static String EXTENTION = ".xlsx";

    private TimetableExcelReport report = new TimetableExcelReport();
    private TimetableData inData;

    private Integer balanceOpenQnt = 0;
    private Integer balanceCloseQnt = 0;
    private Integer totalOpenQnt = 0;
    private Integer totalCloseQnt = 0;

    public ExcelReport createReport1(TimetableData inData, Map<String, Cell> cellIndex_8, Map<String, Cell> cellIndex_10_11) throws Exception {
        this.inData = inData;
        Map<String, Map<KeyCell, List<CellData>>> dataMap = inData.getTimetableByDay();
        for (Map.Entry<String, Map<KeyCell, List<CellData>>> entry : dataMap.entrySet()) {
            String dayName = inData.getDayNameByID(entry.getKey());
            if (Arrays.asList("Каждый день", "В любой день").contains(dayName)) continue;

            for (Map.Entry<KeyCell, List<CellData>> cell : entry.getValue().entrySet()) {
                Boolean isSeminar = Arrays.asList(Classes.ALL_10, Classes.ALL_11).contains(cell.getKey().getClazz());
                String newValue;
                if (isSeminar) {
                    newValue = inData.printCellDataSeminar(cell.getValue());
                } else {
                    newValue = inData.printCellData(cell.getValue());
                }
                Classes clazz = cell.getKey().getClazz();
                if (Classes.CLASS_8.equals(clazz)) {
                    updateCellValue(dayName, cell.getKey(), newValue, cellIndex_8);
                } else {
                    updateCellValue(dayName, cell.getKey(), newValue, cellIndex_10_11);
                }
            }
            report.newDay();
        }

        return report;
    }

    private void updateCellValue(String dayName, KeyCell key, String newValue, Map<String, Cell> cellIndex) {
        StringBuilder builder = new StringBuilder();
        builder.append(dayName).append("_").append(key.getClazz().getGeneral()).append("_").append(key.getPeriod());
        String keyString = builder.toString();
        Cell cell = cellIndex.remove(keyString);
        cell.setCellValue(newValue);
    }

    public ExcelReport createReport(TimetableData inData) throws Exception {
        this.inData = inData;
        Map<String, Map<KeyCell, List<CellData>>> dataMap = inData.getTimetableByDay();
        for (Map.Entry<String, Map<KeyCell, List<CellData>>> entry : dataMap.entrySet()) {
            String dayName = inData.getDayNameByID(entry.getKey());
            if (Arrays.asList("Каждый день", "В любой день").contains(dayName)) continue;
            report.printDayHeader(dayName);
            report.printClassHeader();
            report.printPeriodHeader(inData.getPeriodIndex(), 0);
            report.printPeriodHeader(inData.getPeriodIndex(), TimetableExcelReport.DIST);
            for (Map.Entry<KeyCell, List<CellData>> cell : entry.getValue().entrySet()) {
                Boolean isSeminar = Arrays.asList(Classes.ALL_10, Classes.ALL_11).contains(cell.getKey().getClazz());
                report.printCellData(cell.getKey(), inData.printCellData(cell.getValue()));
            }
            report.newDay();
        }
        report.newSheet();
        report.printPeriodHeader(inData.getPeriodIndex(), 0);
        for (Map.Entry<String, Map<KeyCell, List<CellData>>> entry : dataMap.entrySet()) {
            String dayName = inData.getDayNameByID(entry.getKey());
            if (Arrays.asList("Каждый день", "В любой день").contains(dayName)) continue;
            report.printDayHeader2(dayName);
            for (Map.Entry<KeyCell, List<CellData>> cell : entry.getValue().entrySet()) {
                Classes clazz = cell.getKey().getClazz();
                if (Classes.CLASS_8.equals(clazz)) {
                    report.printCellData2(cell.getKey(), inData.printCellData(cell.getValue()));
                }
            }

        }

//        report.printReportHeader(startDateString, endDateString, branchName);
//        if (accountList.isEmpty()) {
//            report.printAccountDataHeader();
//            report.printNoData();
//            report.printTotal(null, totalOpenQnt, totalCloseQnt);
//            return report;
//        }
//
//        Iterator<DsAccountGetAccountBook> accIter = accountList.iterator();
//        DsAccountGetAccountBook prevAcc = accIter.next();
//
//        checkEmptyBalance(prevAcc);
//
//        report.printBalanceHeader(prevAcc);
//        report.printAccountDataHeader();
//        while (accIter.hasNext()) {
//            countQnt(prevAcc);
//            report.printAccountData(prevAcc);
//            DsAccountGetAccountBook acc = accIter.next();
//            if (!acc.getBalanceID().equals(prevAcc.getBalanceID())) {
//                report.printTotal(prevAcc, balanceOpenQnt, balanceCloseQnt);
//                balanceOpenQnt = 0;
//                balanceCloseQnt = 0;
//                checkEmptyBalance(acc);
//                report.printBalanceHeader(acc);
//                report.printAccountDataHeader();
//            }
//            prevAcc = acc;
//        }
//        countQnt(prevAcc);
//        report.printAccountData(prevAcc);
//        report.printTotal(prevAcc, balanceOpenQnt, balanceCloseQnt);
//        checkEmptyBalance();
//        report.printTotal(null, totalOpenQnt, totalCloseQnt);

        return report;
    }

    public Map<String, Cell> createCellIndex(Workbook wb, int sheetIndex) {
        Map<String, Cell> index = new HashMap<String, Cell>();
        Sheet sheet = wb.getSheetAt(sheetIndex);
        for(Row row : sheet) {
            for(Cell cell : row) {
                try {
                    index.put(cell.getStringCellValue(), cell);
                } catch (IllegalStateException ex) {
                    //do nothing
                }
            }
        }
        return index;
    }

    public String writeFile(ExcelReport report) throws IOException {
        String fileName = getFileName();
//        fileName = fileName.replace("\\", "/");
//        fileName = fileName.replace("//", "/");
//        fileName = fileName.replace("/", File.separator);

//            if(new File(FILE_OUTPUT_NAME).isFile()){
//                throw new Exception("Файл с указанным наименованием (" + fileName + ") уже существует.");
//            }
            report.commitChanges(fileName);

        return fileName;
    }

    public String getFileName() {
        return FILE_OUTPUT_NAME + System.currentTimeMillis() + EXTENTION;
    }

    public void clearUnused(Map<String, Cell> cellIndex) {
        for (Map.Entry<String, Cell> entry : cellIndex.entrySet()) {
            String key = entry.getKey();
            Cell cell = entry.getValue();
            if (key.contains("ALL")) {
                cell.getRow().setZeroHeight(true);
            }
            if (key.contains("_")) {
                cell.setCellValue("");
            }
        }
    }

    public void addStudentsList(Workbook wb, TimetableData inData) {
        TimetableExcelReport report = new TimetableExcelReport(wb);
        for(Classes clazz : Classes.values()) {
            if (Arrays.asList(Classes.ALL_10, Classes.ALL_11).contains(clazz)) continue;
            report.newSheet(clazz.getGeneral());
            report.printStudentHeader();
            for (String alias : clazz.getAliases()) {
                report.printStudentInfo(inData, alias);
            }

        }
    }
}
