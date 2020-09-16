package excel;

import bean.Card;
import bean.ImportCard;
import bean.Lesson;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import schedule.CellData;
import schedule.Classes;
import schedule.KeyCell;
import schedule.TimetableData;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class TimetableExcelHelper {

    public static String FILE_OUTPUT_NAME = "output";
    public static String EXTENTION = ".xlsx";

    private TimetableExcelReport report = new TimetableExcelReport();
    private TimetableData inData;

    private Integer balanceOpenQnt = 0;
    private Integer balanceCloseQnt = 0;
    private Integer totalOpenQnt = 0;
    private Integer totalCloseQnt = 0;

    public ExcelReport createReport1(TimetableData inData, Map<String, Cell> cellIndex_8, Map<String, Cell> cellIndex_9, Map<String, Cell> cellIndex_10_11) throws Exception {
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
                } else if (Classes.CLASS_9.equals(clazz)) {
                    updateCellValue(dayName, cell.getKey(), newValue, cellIndex_9);
                } else   {
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
                if (Arrays.asList(Classes.CLASS_8, Classes.CLASS_9).contains(clazz)) {
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
        DateFormat dateFormat = new SimpleDateFormat("_dd-MM-yyyy_HH-mm-ss");
        return FILE_OUTPUT_NAME + dateFormat.format(new Date()) + EXTENTION;
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

    public void getLessons(Sheet sheet, TimetableData timetableData) {
        Row headerRow = sheet.createRow(0);
        createCell(headerRow, 0, "ID");
        createCell(headerRow, 1, "Предмет");
        createCell(headerRow, 2, "Предмет сокращенно");
        createCell(headerRow, 3, "Класс");
        createCell(headerRow, 4, "Преподаватель");
        createCell(headerRow, 5, "Наименование в журнале 1");
        createCell(headerRow, 6, "Наименование в журнале 2");
        createCell(headerRow, 7, "Наименование в журнале 3");

        int i = 1;
        for (Lesson lesson : timetableData.getTimetable().getLessons()) {
            String[] teachers = lesson.getTeacherids().split(",");
            for (String teacherIDs : teachers) {
                Row currentRow = sheet.createRow(i);
                createCell(currentRow, 0, lesson.getId());
                String subjectName = timetableData.getSubjectIndex().get(lesson.getSubjectid()).getName();
                if (!lesson.getSeminargroup().isEmpty()) {
                    subjectName += " гр." + Integer.valueOf(lesson.getSeminargroup()) % 10;
                }
                createCell(currentRow, 1, subjectName);
                createCell(currentRow, 2, timetableData.getSubjectIndex().get(lesson.getSubjectid()).getShorty());

                StringBuilder classValue = new StringBuilder();
                String[] classes = lesson.getClassids().split(",");
                for (String classIDs : classes) {
                    if (classValue.length() > 0) {
                        classValue.append(",");
                    }
                    classValue.append(timetableData.getClassesIndex().get(classIDs).getName());
                }
                createCell(currentRow, 3, classValue.toString());
                createCell(currentRow, 4, timetableData.getTeacherIndex().get(teacherIDs).getName());
                i++;
            }
        }
    }

    private void createCell(Row row, int i, String value) {
        Cell cell = row.createCell(i);
        cell.setCellValue(value);
    }

    private String getCellValue(Row row, int i) {
        Cell cell = row.getCell(i);
        if (cell == null) return null;
        return cell.getStringCellValue();
    }

    public void processCards(Sheet sheetOut, Sheet sheetIn, TimetableData timetableData) {
        List<ImportCard> importCards = new LinkedList<>();
        for (int i = 1; i < sheetIn.getLastRowNum() + 1; i++) {
            Row currRow = sheetIn.getRow(i);
            String lessonID = currRow.getCell(0).getStringCellValue();
            String teacher = currRow.getCell(4).getStringCellValue();
            String extName1 = getCellValue(currRow, 5); //Наименование в журнале 1
            String extName2 = getCellValue(currRow, 6); //Наименование в журнале 2
            String extName3 = getCellValue(currRow, 7); //Наименование в журнале 3

            processExtName(extName1, lessonID, teacher, timetableData, importCards);
            processExtName(extName2, lessonID, teacher, timetableData, importCards);
            processExtName(extName3, lessonID, teacher, timetableData, importCards);
        }
        Collections.sort(importCards);
        int num = 1;
        for (ImportCard importCard : importCards) {
            Row currRow = sheetOut.createRow(num);
            createCell(currRow, 0, importCard.getTime());
            createCell(currRow, 1, importCard.getSubject());
            createCell(currRow, 2, importCard.getTeacher());
            createCell(currRow, 3, importCard.getRoom());
            num ++;
        }
    }

    private void processExtName(String extName, String lessonID, String teacher, TimetableData timetableData, List<ImportCard> importCards) {
        if (extName == null || extName.isEmpty()) return;
        List<Card> cardList = timetableData.getCardListIndex().get(lessonID);
        if (cardList == null) return;
        for (Card card : cardList) {
            ImportCard importCard = new ImportCard();
            String tm = String.format("%s %s - %s",
                    timetableData.getDayNameByID(card.getDays()),
                    timetableData.getPeriodIndex().get(card.getPeriod()).getStarttime(),
                    timetableData.getPeriodIndex().get(card.getPeriod()).getEndtime()
            );
            importCard.setTimeNum(new StringBuilder(card.getDays()).reverse().append(card.getPeriod()).toString());
            importCard.setTime(tm);
            importCard.setSubject(extName);
            importCard.setTeacher(teacher);
            String room = "";
            if (card.getRoomID() != null && ! card.getRoomID().isEmpty()) {
                room = timetableData.getRoomIndex().get(card.getRoomID()).getShorty();
            }
            if (StringUtils.isNumeric(room)) {
                room = "Б." + room;
            }
            importCard.setRoom(room);

            importCards.add(importCard);
        }
    }
}
