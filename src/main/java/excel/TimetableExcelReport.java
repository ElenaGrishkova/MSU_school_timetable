package excel;

import bean.Period;
import org.apache.poi.ss.util.CellRangeAddress;
import shedule.Classes;
import shedule.KeyCell;

import java.util.Map;

public class TimetableExcelReport extends ExcelReport {

    private static final String REPORTDATE_ONEDAY = "Ведомость %s счетов за дату %s";
    private static final String REPORTDATE_PERIOD = "Ведомость %s счетов за период с %s по %s";
    private static final String OPENACCS = "открытых";
    private static final String CLOSEACCS = "закрытых";
    private static final String OPENCLOSEACCS = "открытых и закрытых";
    private static final String NODATA = "Нет данных для отображение";

    private static final String BALANCEHEADER = "ОБЛАСТЬ УЧЕТА: %s. %s";
    static final String TOTALBALANCE = "ИТОГО по области %s";
    static final String TOTAL = "ИТОГО по ведомости";
    static final String TOTALOPEN = "открытых счет(ов)";
    static final String TOTALCLOSE = "закрытых счет(ов)";

    static final String ACCPICTURE = "Номер счета";
    static final String ACCNAME = "Наименование счета";
    static final String OWNERNAME = "Наименование клиента";

    static final byte CELL_WIDTH_ACCPICTURE = 20;
    static final byte CELL_WIDTH_ACCNAME = 28;
    static final byte CELL_WIDTH_OWNERNAME = 28;

    static int CELL_QNT = 7;
    static int DIST = 11;
    static int currentColumn = 1;

    public TimetableExcelReport() {

    }

    public TimetableExcelReport(String sheetName) {
        super(sheetName);
    }

    //объединенная ячейка на несколько колонок
    public void printDayHeader(String dayName) {
        row = sheet.createRow(currentRow);
        sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow+1, 0, CELL_QNT - 1));
        createCell((short) 0, "CENTER_CENTER_NONE_BOLD", dayName);
        row.setHeightInPoints((dayName.length() / 30 + 1) * 20);
        currentRow += 3;
    }

    //день как название колонки
    public void printDayHeader2(String dayName) {
        row = sheet.getRow(0);
        if (row == null) {
            row = sheet.createRow(0);
        }
        currentColumn ++;
        createCell((short) currentColumn, "CENTER_CENTER_NONE_BOLD", dayName);
        row.setHeightInPoints((dayName.length() / 30 + 1) * 20);

    }

    public void printClassHeader() {
        row = sheet.createRow(currentRow);
        createCell((short) 0, "CENTER_CENTER_THIN", "№");
        createCell((short) 1, "CENTER_CENTER_THIN", "Время");
        createCell((short) 2, "CENTER_CENTER_THIN", Classes.EM_10.getGeneral());
        createCell((short) 3, "CENTER_CENTER_THIN", Classes.ENJ_10.getGeneral());
        createCell((short) 4, "CENTER_CENTER_THIN", Classes.EN_10.getGeneral());
        createCell((short) 5, "CENTER_CENTER_THIN", Classes.IF_10.getGeneral());
        createCell((short) 6, "CENTER_CENTER_THIN", Classes.SP_10.getGeneral());

        row = sheet.createRow(currentRow + DIST);
        createCell((short) 0, "CENTER_CENTER_THIN", "№");
        createCell((short) 1, "CENTER_CENTER_THIN", "Время");
        createCell((short) 2, "CENTER_CENTER_THIN", Classes.M_11.getGeneral());
        createCell((short) 3, "CENTER_CENTER_THIN", Classes.ENJ_11.getGeneral());
        createCell((short) 4, "CENTER_CENTER_THIN", Classes.EN_11.getGeneral());
        createCell((short) 5, "CENTER_CENTER_THIN", Classes.IF_11.getGeneral());
        createCell((short) 6, "CENTER_CENTER_THIN", Classes.SE_11.getGeneral());

        //currentRow ++;
    }

    public void printPeriodHeader(Map<Integer, Period> periodIndex, int dist) {
        for (Map.Entry<Integer, Period> period : periodIndex.entrySet()) {
            row = sheet.createRow(currentRow + period.getKey() + dist);
            createCell((short) 0, "CENTER_CENTER_THIN", period.getKey().toString());
            createCell((short) 1, "CENTER_CENTER_THIN", period.getValue().getStarttime()
                    +"-"+period.getValue().getEndtime());
            row.setHeightInPoints(30);
        }
    }

    public void printCellData(KeyCell key, String s) {
        Classes clazz = key.getClazz();
        if (Classes.CLASS_8.equals(clazz)) return;
        row = sheet.getRow(currentRow + key.getPeriod()  +
                (Classes.is11(clazz.getGeneral()) ? DIST : 0));
        //TODO такого не должно быть. Удалить
//        if (row == null) {
//            row = sheet.createRow(currentRow + key.getPeriod() + 1 +
//                    (Classes.is11(clazz.getGeneral()) ? DIST : 0));
//        }
        createCell(clazz.getIndex() + 1, "CENTER_CENTER_THIN", s);
    }

    public void printCellData2(KeyCell key, String s) {
        row = sheet.getRow(currentRow + key.getPeriod());
        createCell(currentColumn, "CENTER_CENTER_THIN", s);
    }

    public void newDay() {
        currentRow += DIST + DIST;
    }

//    public void setWidths() {
//        sheet.setColumnWidth(0, getColumnWidthInPoints(width[i]));
//    }


//    public void printReportHeader(String dateStart, String dateEnd, String branchName) {
//        printBankName(branchName);
//        row = sheet.createRow(currentRow);
//        row.setHeightInPoints(48);
//        switch (accType) {
//            case OPEN:
//                if (dateStart.equals(dateEnd)) {
//                    createCell((short) 0, "CENTER_CENTER_NONE", String.format(REPORTDATE_ONEDAY, OPENACCS,
//                            dateStart),
//                            CellType.CELL_TYPE_STRING.getCellType());
//                } else {
//                    createCell((short) 0, "CENTER_CENTER_NONE", String.format(REPORTDATE_PERIOD, OPENACCS,
//                            dateStart, dateEnd),
//                            CellType.CELL_TYPE_STRING.getCellType());
//                }
//                break;
//            case CLOSE:
//                if (dateStart.equals(dateEnd)) {
//                    createCell((short) 0, "CENTER_CENTER_NONE", String.format(REPORTDATE_ONEDAY, CLOSEACCS,
//                            dateStart),
//                            CellType.CELL_TYPE_STRING.getCellType());
//                } else {
//                    createCell((short) 0, "CENTER_CENTER_NONE", String.format(REPORTDATE_PERIOD, CLOSEACCS,
//                            dateStart, dateEnd),
//                            CellType.CELL_TYPE_STRING.getCellType());
//                }
//                break;
//            default:
//                if (dateStart.equals(dateEnd)) {
//                    createCell((short) 0, "CENTER_CENTER_NONE", String.format(REPORTDATE_ONEDAY, OPENCLOSEACCS,
//                            dateStart),
//                            CellType.CELL_TYPE_STRING.getCellType());
//                } else {
//                    createCell((short) 0, "CENTER_CENTER_NONE", String.format(REPORTDATE_PERIOD, OPENCLOSEACCS,
//                            dateStart, dateEnd),
//                            CellType.CELL_TYPE_STRING.getCellType());
//                }
//        }
//        currentRow++;
//        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, CELL_QNT - 1));
//    }
//
//    public void printBalanceHeader(IBalanceBriefName balance) {
//        row = sheet.createRow(currentRow);
//        row.setHeightInPoints(30);
//        sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 0, CELL_QNT - 1));
//        createCell((short) 0, "LEFT_CENTER_NONE", String.format(BALANCEHEADER, balance.getBalanceBrief(),
//                balance.getBalanceName()));
//        currentRow++;
//    }
//
//    public void printAccountData(DsAccountGetAccountBook acc) {
//    }
//
//    public void printTotal(IBalanceBriefName balance, Integer openQnt, Integer closeQnt) {
//    }
//
//    public void printAccountDataHeader() {
//    }
//
//    void printAccountDataHeader(byte[] width, String[] vals) {
//        row = sheet.createRow(currentRow);
//        row.setHeightInPoints(36);
//        for (int i = 0; i < width.length; i++) {
//            sheet.setColumnWidth(i, getColumnWidthInPoints(width[i]));
//            createCell(i, "CENTER_CENTER_THIN", vals[i], CellType.CELL_TYPE_STRING.getCellType());
//        }
//        currentRow++;
//    }
//
//    @Override
//    public void printNoData() {
//        row = sheet.createRow(currentRow);
//        row.setHeightInPoints(45);
//        sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, 0, CELL_QNT - 1));
//        createCell((short) 0, "CENTER_CENTER_NONE", NODATA);
//        setMultipleCellsBorders(new CellRangeAddress(currentRow, currentRow, 0, CELL_QNT - 1));
//        currentRow++;
//    }
//
//    enum AccType {
//        OPENCLOSE(0),
//        OPEN(1),
//        CLOSE(2);
//
//        private final Integer num;
//
//        AccType(Integer num) {
//            this.num = num;
//        }
//
//        static AccType getByNum(Integer num) {
//            switch (num) {
//                case 1:
//                    return OPEN;
//                case 2:
//                    return CLOSE;
//                default:
//                    return OPENCLOSE;
//            }
//        }
//    }

}
