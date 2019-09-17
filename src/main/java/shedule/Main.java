package shedule;

import bean.Timetable;
import com.thoughtworks.xstream.XStream;
import excel.TimetableExcelHelper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        InputStreamReader inputFile = null;
        String inputFileName = null;
        String mode = null;
        if (2 == args.length) {
            inputFileName = args[0];
            mode = args[1];
        } else {
            System.err.println("Invalid arguments count:" + args.length);
            System.exit(1);
        }
//        InputStreamReader inputFile = new InputStreamReader(Main.class.getClassLoader()
//                .getResourceAsStream("123.xml"), "windows-1251");
//        InputStreamReader inputFile = new InputStreamReader(Main.class.getClassLoader()
//                .getResourceAsStream("18week.xml"), "windows-1251");
        inputFile = new InputStreamReader(new FileInputStream(inputFileName), "windows-1251");

        Scanner scan = new Scanner(inputFile);
        StringBuilder xml = new StringBuilder();
        while (scan.hasNextLine()) {
            xml.append(scan.nextLine());
        }
        inputFile.close();

        XStream xstream = new XStream();
        xstream.processAnnotations(Timetable.class);
        xstream.ignoreUnknownElements();
        Timetable timetable = (Timetable)xstream.fromXML(xml.toString());


        TimetableData timetableData = new TimetableData(timetable);
        timetableData.process();
        TimetableExcelHelper helper = new TimetableExcelHelper();

        if ("-fill_template".equals(mode)) {
            Workbook wb = WorkbookFactory.create(Main.class.getClassLoader()
                    .getResourceAsStream("template.xlsx"));

            Map<String, Cell> cellIndex_8 = helper.createCellIndex(wb, 0);
            Map<String, Cell> cellIndex_9 = helper.createCellIndex(wb, 1);
            Map<String, Cell> cellIndex_10_11 = helper.createCellIndex(wb, 2);
            helper.createReport1(timetableData, cellIndex_8, cellIndex_9, cellIndex_10_11);
            helper.clearUnused(cellIndex_8);
            helper.clearUnused(cellIndex_9);
            helper.clearUnused(cellIndex_10_11);
            helper.addStudentsList(wb, timetableData);

            String fileName = helper.getFileName();
            FileOutputStream out = new FileOutputStream(fileName);
            wb.write(out);
            out.close();
        } else if ("-get_lessons".equals(mode)) {
            Workbook wb = new XSSFWorkbook();
            Sheet sheet = wb.createSheet("Карточки");
            helper.getLessons(sheet, timetableData);

            String filename = "Lessons.xlsx";
            FileOutputStream fout = new FileOutputStream(filename);
            wb.write(fout);
            fout.close();
        } else if ("-prepare_import".equals(mode)) {
            Workbook wb = WorkbookFactory.create(Main.class.getClassLoader()
                    .getResourceAsStream("Schedule.xlsx"));
            Sheet sheet = wb.getSheet("Расписание");
            Workbook wb2 = WorkbookFactory.create(Main.class.getClassLoader()
                    .getResourceAsStream("Lessons.xlsx"));
            Sheet sheet2 = wb2.getSheet("Карточки");
            helper.processCards(sheet, sheet2, timetableData);

            FileOutputStream out = new FileOutputStream("Schedule.xlsx");
            wb.write(out);
            out.close();
        } else {
            System.err.println("Invalid program mode:" + mode);
        }

//        ExcelReport report = new TimetableExcelReport("Расписание 10, 11 кл");
//        TimetableExcelHelper helper = new TimetableExcelHelper();
//        try {
//            report = helper.createReport(timetableData);
//            String fileName = helper.writeFile(report);
//            System.out.println("Created timetable " + fileName);
//        } finally {
//            report.dispose();
//        }

    }
}
