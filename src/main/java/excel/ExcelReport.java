package excel;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ExcelReport {
    protected static final String SHEET_TITLE = "Report"; //Название листа в Excel
    protected static final String EMPTY_CELL = "X";
    public static final String XLSX_EXTENSION = ".xlsx";
    private Font smallBold;

    public SXSSFWorkbook wb = new SXSSFWorkbook(1000); //Рабочая книга. Сюда сохраняются все изменения перед отправкой в файл
    protected Sheet sheet; //Лист
    protected final List<Sheet> sheetList; //Лист
    protected int currentSheet = 0; //Текущий лист
    protected Row row; //Столбец
    public int currentRow = 0; //Текущий столбец
    public int maxRow = 1048570;
    protected DataFormat df = wb.createDataFormat();
    private Font boldFont;
    private Font arial10;

    //Стили для ячеек
    //Динамически их создавать - плохая идея, поскольку максимальное кол-во стилей равно 4000. При превышении - эксепшн
    protected Map<String, CellStyle> styles = new HashMap<String, CellStyle>();

    private static final Map<String, Short> halign = new HashMap<String, Short>(); //Маппинг для горизонтального выравнивания
    private static final Map<String, Short> valign = new HashMap<String, Short>(); //Маппинг для вертикального выравнивания
    private static final Map<String, Short> borders = new HashMap<String, Short>(); //Маппинг для границ

    static {
        halign.put("LEFT", CellStyle.ALIGN_LEFT);
        halign.put("CENTER", CellStyle.ALIGN_CENTER);
        halign.put("RIGHT", CellStyle.ALIGN_RIGHT);

        valign.put("TOP", CellStyle.VERTICAL_TOP);
        valign.put("CENTER", CellStyle.VERTICAL_CENTER);
        valign.put("BOTTOM", CellStyle.VERTICAL_BOTTOM);

        borders.put("THIN", CellStyle.BORDER_THIN);
        borders.put("MEDIUM", BorderStyle.MEDIUM.getCode());
    }

    public ExcelReport() {
        sheetList = new ArrayList<Sheet>();
        sheet = wb.createSheet(SHEET_TITLE + " 1"); //Создание листа
        sheetList.add(sheet);
        initializeFonts();
    }

    public ExcelReport(String sheetTitle) {
        sheetList = new ArrayList<Sheet>();
        sheet = wb.createSheet(sheetTitle); //Создание листа
        sheetList.add(sheet);
        initializeFonts();
    }

    private void initializeFonts() {
        boldFont = wb.createFont();
        boldFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        arial10 = wb.createFont();
        arial10.setFontName("Arial");
        arial10.setFontHeightInPoints((short) 10);
        smallBold = wb.createFont();
        smallBold.setBold(true);
        smallBold.setFontName("Verdana");
        smallBold.setFontHeightInPoints((short) 9);
    }

    public void newSheet(){
        sheet = wb.createSheet(SHEET_TITLE + " " + (currentSheet + 2));
        sheetList.add(sheet);
        currentRow = 0;
        currentSheet++;
    }

    /**
     * Сбрасываем всё, что наделали в файл
     *
     * @throws IOException
     */
    public void commitChanges(String fileName) throws IOException {
        OutputStream os = new FileOutputStream(fileName);
        os.flush();
        wb.write(os);
        os.close();
    }

    /**
     * @param width ширина столбца в точках
     * @return ширина столбца в каких-то своих единицах
     */
    protected int getColumnWidthInPoints(double width) {
        return (int) (441.3793 + 256d * (width - 1d));
    }

    protected void setColumnWidthInParrors(int col, int width) {
        sheet.setColumnWidth(col, width);
    }

    /**
     * Конструктор ячейки умолчательный тип данных STRING
     *
     * @param column     номер столбца
     * @param styleTitle наименование стиля (правило наименования стиля смотрите в комментарии к методу setStyle)
     * @param value      значение, помещаемое в ячейку
     */
    protected void createCell(short column, String styleTitle, String value) {
        Cell cell = row.createCell(column);
        CellStyle style = getStyle(styleTitle);
        cell.setCellStyle(style);
        cell.setCellValue(value);
        cell.setCellType(CellType.CELL_TYPE_STRING.getCellType());
    }

    /**
     * Конструктор ячейки умолчательный тип данных STRING
     *
     * @param column     номер столбца
     * @param styleTitle наименование стиля (правило наименования стиля смотрите в комментарии к методу setStyle)
     * @param value      значение, помещаемое в ячейку
     */
    protected void createCell(int column, String styleTitle, String value) {
        createCell((short) column, styleTitle, value);
    }

    public void dispose() {
        wb.dispose();
    }

    /**
     * Конструктор ячейки
     *
     * @param column     номер столбца
     * @param styleTitle наименование стиля (правило наименования стиля смотрите в комментарии к методу setStyle)
     * @param value      значение, помещаемое в ячейку
     * @param type       тип значения ячейки
     */
    protected void createCell(short column, String styleTitle, String value, int type) {
        Cell cell = row.createCell(column);
        CellStyle style = getStyle(styleTitle);
        cell.setCellValue(value);
        cell.setCellType(type);
        cell.setCellStyle(style);
    }

    /**
     * Конструктор ячейки
     *
     * @param column     номер столбца
     * @param styleTitle наименование стиля (правило наименования стиля смотрите в комментарии к методу setStyle)
     * @param value      значение, помещаемое в ячейку
     * @param type       тип значения ячейки
     */
    protected void createCell(int column, String styleTitle, Double value, int type) {
        Cell cell = row.createCell(column);
        CellStyle style = getStyle(styleTitle);
        if (type == 0) {
            style.setDataFormat(df.getFormat("### ### ### ### ##0.00"));
        }
        cell.setCellStyle(style);
        if (value == null) {
            cell.setCellValue("");
        } else {
            cell.setCellValue(value);
        }
        cell.setCellType(type);
    }


    /**
     * Конструктор ячейки
     *
     * @param column     номер столбца
     * @param styleTitle наименование стиля (правило наименования стиля смотрите в комментарии к методу setStyle)
     * @param value      значение, помещаемое в ячейку
     * @param type       тип значения ячейки
     */
    protected void createCell(int column, String styleTitle, String value, int type) {
        Cell cell = row.createCell(column);
        CellStyle style = getStyle(styleTitle);
        if (type == 0) {
            style.setDataFormat(df.getFormat("### ### ### ### ##0.00"));
        }
        cell.setCellStyle(style);
        cell.setCellValue(value);
        cell.setCellType(type);
    }

    /**
     * Выставить цвет заливки ячейки
     *
     * @param cell  ячейка
     * @param color цвет
     */
    protected void setBackgroundColor(Cell cell, short color) {
        CellStyle newStyle = wb.createCellStyle();
        newStyle.cloneStyleFrom(cell.getCellStyle());
        newStyle.setFillForegroundColor(color);
        newStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cell.setCellStyle(newStyle);
    }

    /**
     * Вернуть текущую строку
     */
    public int getCurrentRow() {
        return currentRow;
    }

    /**
     * Сгруппировать строки (чтобы были плюсики)
     */
    public void setRowOutlining(int startRow, int endRow) {
        sheet.groupRow(startRow, endRow);
        sheet.setRowGroupCollapsed(startRow, true);
    }

    /**
     * Пропустить строки
     */
    public void skipRows(int count) {
        currentRow += count;
    }

    /**
     * Пропустить строку
     */
    public void skipRow() {
        currentRow ++;
    }

    /**
     * Создание стиля для ячеек
     *
     * @param title наименование стиля. Имя для стиля задаётся в формате
     *              [горизонтальное выравнивание]_[вертикальное выравнивание]_[граница],
     *              например:
     *              CENTER_TOP_THIN (горизонтально по центру, вертикально по верхней границе, ячейка обрамлена)
     *              LEFT_CENTER_NONE (горизонтально по левому краю, вертикально по центру, ячейка не обрамлена)
     */
    private void setStyle(String title) {
        String[] stls = title.split("_");
        CellStyle style = wb.createCellStyle();
        style.setAlignment(halign.get(stls[0]));
        style.setVerticalAlignment(valign.get(stls[1]));
        style.setWrapText(true);
        short borderStyle = borders.get("THIN");
        if (stls[2].equals("THIN")) {
            style.setBorderBottom(borderStyle);
            style.setBorderLeft(borderStyle);
            style.setBorderTop(borderStyle);
            style.setBorderRight(borderStyle);
        } else if (stls[2].equals("SIDE")) {
            style.setBorderLeft(borderStyle);
            style.setBorderRight(borderStyle);
        } else if (stls[2].equals("UPPER")) {
            style.setBorderTop(borderStyle);
        } else if (stls[2].equals("MEDIUM")) {
            final Short border = borders.get("MEDIUM");
            style.setBorderBottom(border);
            style.setBorderTop(border);
            style.setBorderLeft(border);
            style.setBorderRight(border);
        }
        if(stls.length > 3 && stls[3].equals("BOLD")) {
            style.setFont(boldFont);
        }
        if(stls.length > 3 && stls[3].equals("ARIAL")) {
            style.setFont(arial10);
        }
        if (stls.length > 3 && stls[3].equals("SMBOLD")) {
            style.setFont(smallBold);
        }
        styles.put(title, style);
    }

//    /**
//     * Конструктор ячейки
//     * @param column номер столбца
//     * @param styleTitle наименование стиля (правило наименования стиля смотрите в комментарии к методу setStyle)
//     * @param value значение, помещаемое в ячейку
//     * @param numberFormat формат суммы для значения ячейки
//     */
//    protected void createCell(int column, String styleTitle, double value, RepAmountFormat numberFormat)
//    {
//        Cell cell = row.createCell(column);
//        CellStyle style = getStyle(styleTitle);
//        style.setDataFormat(df.getFormat(numberFormat.getFormat()));
//        cell.setCellStyle(style);
//        cell.setCellValue(value);
//        cell.setCellType(0);
//    }

    /**
     * Получение стиля ячейки
     *
     * @param title наименование стиля (наименование стиля задаётся аналогично методу setStyle)
     * @return стиль ячейки
     */
    private CellStyle getStyle(String title) {
        if (!styles.containsKey(title))
            setStyle(title);
        return styles.get(title);
    }

    /**
     * Рисуем границы ячеек
     *
     * @param range диапазон
     */
    protected void setMultipleCellsBorders(CellRangeAddress range) {
        RegionUtil.setBorderRight(CellStyle.BORDER_THIN, range, sheet, wb);
        RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, range, sheet, wb);
        RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, range, sheet, wb);
        RegionUtil.setBorderTop(CellStyle.BORDER_THIN, range, sheet, wb);
    }

    protected void setMultipleCellsBordersMedium(CellRangeAddress range) {
        RegionUtil.setBorderRight(CellStyle.BORDER_MEDIUM, range, sheet, wb);
        RegionUtil.setBorderBottom(CellStyle.BORDER_MEDIUM, range, sheet, wb);
        RegionUtil.setBorderLeft(CellStyle.BORDER_MEDIUM, range, sheet, wb);
        RegionUtil.setBorderTop(CellStyle.BORDER_MEDIUM, range, sheet, wb);
    }

    public int addSheet(String sheetTitle) {
        Sheet sheetNew = wb.createSheet(sheetTitle); //Создание листа
        sheetList.add(sheetNew);
        return sheetList.size() - 1;
    }

    public Sheet getSheet(int sheetNum) {
        if (sheetNum < sheetList.size()) {
            return sheetList.get(sheetNum);
        }
        return sheet;
    }

    public void setCurrentSheet(int sheetNum) {
        if (sheetNum < sheetList.size()) {
            sheet = sheetList.get(sheetNum);
        }
    }

    public void printNoData() {
        row = sheet.createRow(currentRow);
        row.setHeightInPoints(50);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
        createCell((short)0, "CENTER_CENTER_NONE", "Нет данных для отчета по указанным параметрам", CellType.CELL_TYPE_STRING.getCellType());
        currentRow++;
    }

    protected void checkColumnWidth(int columnIndex, int width) {
        if (getColumnWidthInPoints(sheet.getColumnWidth(columnIndex)) != width) {
            sheet.setColumnWidth(columnIndex, getColumnWidthInPoints(width));
        }
    }

    protected void addMergedRegion(int firstRow, int lastRow, int firstCol, int lastCol) {
        CellRangeAddress range = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
        sheet.addMergedRegion(range);
        setMultipleCellsBorders(range);
    }

    public enum CellType {
        CELL_TYPE_NUMERIC(0),
        CELL_TYPE_STRING(1),
        CELL_TYPE_FORMULA(2),
        CELL_TYPE_BLANK(3),
        CELL_TYPE_BOOLEAN(4),
        CELL_TYPE_ERROR(5);

        private int value;

        private CellType(int value) {
            this.value = value;
        }

        public int getCellType() {
            return value;
        }

    }
}
