package shedule;

import bean.*;
import com.thoughtworks.xstream.XStream;
import excel.TimetableExcelHelper;
import excel.*;

import java.io.InputStreamReader;
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

        ExcelReport report = new TimetableExcelReport("Расписание 10, 11 кл");
        TimetableExcelHelper helper = new TimetableExcelHelper();
        try {
            report = helper.createReport(timetableData);
            String fileName = helper.writeFile(report);
    //        ExcelReportHelper.getInstance().writeFile(report,
    //                FTGLSystemProperties.getReportOutput() + "\\" + fileName,
    //                getProcessId());
            System.out.println("Created timetable " + fileName);
        } finally {
            report.dispose();
        }

    }
}
