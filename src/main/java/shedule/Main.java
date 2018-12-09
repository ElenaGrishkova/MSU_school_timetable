package shedule;

import bean.*;
import com.thoughtworks.xstream.XStream;
import excel.TimetableExcelHelper;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
//        InputStreamReader inputFile = new InputStreamReader(Main.class.getClassLoader()
//                .getResourceAsStream("123.xml"), "windows-1251");
        InputStreamReader inputFile = new InputStreamReader(Main.class.getClassLoader()
                .getResourceAsStream("9week.xml"), "windows-1251");

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

        Workbook wb = WorkbookFactory.create(new File("template.xlsx"));
        TimetableExcelHelper helper = new TimetableExcelHelper();
        Map<String, Cell> cellIndex_8 = helper.createCellIndex(wb, 0);
        Map<String, Cell> cellIndex_10_11 = helper.createCellIndex(wb, 1);
        helper.createReport1(timetableData, cellIndex_8, cellIndex_10_11);
        helper.clearUnused(cellIndex_8);
        helper.clearUnused(cellIndex_10_11);

        String fileName = helper.getFileName();
        FileOutputStream out = new FileOutputStream(fileName);
        wb.write(out);
        out.close();

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
