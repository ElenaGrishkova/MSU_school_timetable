package excel;

import shedule.CellData;
import shedule.Classes;
import shedule.KeyCell;
import shedule.TimetableData;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
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

    public ExcelReport createReport1() throws Exception {
        report.printDayHeader("AAA");
        return report;
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

    public String writeFile(ExcelReport report) throws IOException {
        String fileName = FILE_OUTPUT_NAME + System.currentTimeMillis() + EXTENTION;
//        fileName = fileName.replace("\\", "/");
//        fileName = fileName.replace("//", "/");
//        fileName = fileName.replace("/", File.separator);

//            if(new File(FILE_OUTPUT_NAME).isFile()){
//                throw new Exception("Файл с указанным наименованием (" + fileName + ") уже существует.");
//            }
            report.commitChanges(fileName);

        return fileName;
    }
}
