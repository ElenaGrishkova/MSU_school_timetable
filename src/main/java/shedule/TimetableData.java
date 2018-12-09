package shedule;

import bean.*;
import bean.Class;

import java.util.*;

public class TimetableData {
    public static final String FULL_CLASS = "Весь класс";
    public static final String SPORT_ROOM = "Спортивный зал";
    public static final Integer MAX_LINE_SIZE = 48;

    public Map<String, Map<KeyCell, List<CellData>>> getTimetableByDay() {
        return timetableByDay;
    }

    public Map<String, Daysdef> getDayIndex() {
        return dayIndex;
    }

    public Map<Integer, Period> getPeriodIndex() {
        return periodIndex;
    }

    private Map<String, Map<KeyCell, List<CellData>>> timetableByDay = new TreeMap<String, Map<KeyCell, List<CellData>>>(new Comparator<String>() {
        //обратная сортировка
        public int compare(String o1, String o2) {
            return o2.compareTo(o1);
        }
    });
    private Timetable timetable;
    private Map<String, Lesson> lessonIndex = new HashMap<String, Lesson>();
    private Map<String, Daysdef> dayIndex = new HashMap<String, Daysdef>();
    private Map<Integer, Period> periodIndex = new TreeMap<Integer, Period>();
    private Map<String, Subject> subjectIndex = new HashMap<String, Subject>();
    private Map<String, Teacher> teacherIndex = new HashMap<String, Teacher>();
    private Map<String, Class> classesIndex = new HashMap<String, Class>();
    private Map<String, Group> groupIndex = new HashMap<String, Group>();
    private Map<String, Classroom> roomIndex = new HashMap<String, Classroom>();

    public TimetableData(Timetable timetable) {
        this.timetable = timetable;
    }

    public void process() throws Exception {
        createIndexes();
        for (Daysdef day : timetable.getDaysdefs()) {
            timetableByDay.put(day.getDays(), new HashMap<KeyCell, List<CellData>>());
        }
        for (Card card : timetable.getCards()) {
            String day = card.getDays();
            Map<KeyCell, List<CellData>> dayTable = timetableByDay.get(day);
            Integer period = card.getPeriod();
            Lesson lesson = lessonIndex.get(card.getLessonid());
//            Integer periodsCount = lesson.getPeriodspercard();
//            for (int periodAdd = 0; periodAdd < periodsCount; periodAdd++) {
//                period+=periodAdd;
            String seminargroup = lesson.getSeminargroup();
            String[] classIDs = lesson.getClassids().split(",");
            String[] groupIDs = lesson.getGroupids().split(",");

            if (seminargroup.isEmpty()) {

                if (lesson.getGroupids().isEmpty()) {
                    for (String classID : classIDs) {
                        addCellData(classID, FULL_CLASS, period, lesson, dayTable, card.getRoomID());
                    }
                } else {
                    for (String groupID : groupIDs) {
                        Group group = groupIndex.get(groupID);
                        String classID = group.getClassid();
                        addCellData(classID, group.getName(), period, lesson, dayTable, card.getRoomID());
                    }
                }
            } else if ( classIDs.length <= 4) {
                String groupNameSeminar = String.valueOf(Integer.valueOf(seminargroup) %  10) + " группа";
                Map<String, String> class2group = new HashMap<String, String>();
                for (String groupID : groupIDs) {
                    if (groupID.isEmpty()) continue;
                    Group group = groupIndex.get(groupID);
                    if (group == null) {
                        System.out.println();
                    }
                    String classID = group.getClassid();
                    if (class2group.containsKey(classID)) {
                        throw new Exception("Several groups by one class in seminars. Lesson " + lesson.getId());
                    }
                    class2group.put(classID, group.getName());
                }
                for (String classID : classIDs) {
                    String groupName = class2group.get(classID);
                    if (groupName == null || groupName.isEmpty()) {
                        groupName = groupNameSeminar;
                    }
                    addCellData(classID, groupName, period, lesson, dayTable, card.getRoomID());
                }
            }
//            }
        }
        for (Map.Entry<String, Map<KeyCell, List<CellData>>> table : timetableByDay.entrySet()) {
            if (dayIndex.get(table.getKey()).getName().equals("Пятница")) {
                System.out.println("----------------------------------------" +
                        dayIndex.get(table.getKey()).getName() +
                        "----------------------------------------");
                for (Map.Entry<KeyCell, List<CellData>> cell : table.getValue().entrySet()) {
                    if (cell.getKey().getClazz().getGeneral().equals("10 СП")) {
                        System.out.println(String.format("Урок %s, класс %s",
                                periodIndex.get(cell.getKey().getPeriod()).getName(),
                                cell.getKey().getClazz().getGeneral()));
                        System.out.println();
                        String s = printCellData(cell.getValue());
                        if (!s.isEmpty()) {
                            System.out.println(s);
                        }
                    }
                }
            }
        }
        System.out.println();
    }

    private void addCellData(String classID, String groupName, Integer period, Lesson lesson,
                             Map<KeyCell, List<CellData>> dayTable, String roomID) throws Exception {
        String className = classesIndex.get(classID).getName();
        KeyCell key = new KeyCell(period, Classes.getByAlias(className));
        String teacherName = "";
        if (lesson.getTeacherids().contains(",")) {
            String[] strings = lesson.getTeacherids().split(",");
            for (int i = 0; i < strings.length; i++) {
                if (i > 0) {
                    teacherName += " / ";
                }
                Teacher teacher = teacherIndex.get(strings[i]);
                teacherName += teacher.getLastname() + " " + teacher.getFirstname();
            }
//            throw new Exception(String.format("Two teachers %s in one lesson %s", lesson
//                    .getTeacherids(), lesson.getId()));
        } else {
            Teacher teacher = teacherIndex.get(lesson.getTeacherids());
            teacherName = teacher.getLastname() + " " +teacher.getFirstname();
        }
        if (teacherName.isEmpty()) {
            System.out.println();
        }
        String subjectName = subjectIndex.get(lesson.getSubjectid()).getName();
        CellData cellData = new CellData(key, className, groupName, teacherName, subjectName,
                lesson.getId(), roomID);
        if (dayTable.containsKey(key)) {
            dayTable.get(key).add(cellData);
        } else {
            List<CellData> list = new ArrayList<CellData>();
            list.add(cellData);
            dayTable.put(key, list);
        }
    }

    public String printCellData(List<CellData> list) throws Exception {
        StringBuilder result1 = new StringBuilder();//предметы, классы
        StringBuilder result2 = new StringBuilder();//учителя, кабинеты

//        if (list.size() == 1) {
//            CellData cellData = list.get(0);
//            result.append(cellData.getSubjectName()).append(" (").
//                    append(cellData.getClassName()).append(")\n").
//                    append(cellData.getTeacherName()).append("\n");
//        } else {
        Collections.sort(list);
        Map<String, List<CellData>> teacher2cellList = new LinkedHashMap<String, List<CellData>>();

        for(CellData cellData : list) {
            String teacher = cellData.getTeacherName();
            if (teacher2cellList.containsKey(teacher)) {
                teacher2cellList.get(teacher).add(cellData);
            } else {
                List<CellData> listTmp = new ArrayList<CellData>();
                listTmp.add(cellData);
                teacher2cellList.put(teacher, listTmp);
            }
        }
        Boolean isFirst = true;
        for (Map.Entry<String, List<CellData>> entry : teacher2cellList.entrySet()) {
            if (!isFirst) {
                result1.append(" / ");
                result2.append(" / ");
            }
            List<String> classNames = new ArrayList<String>();
            String subject = "";
            Boolean isFullClass2group = true;
            for (CellData cell : entry.getValue()) {
                classNames.add(cell.getClassName());
                if (isFullClass2group && ! FULL_CLASS.equals(cell.getGroupName())) {
                    isFullClass2group = false;
                }
                if (subject.isEmpty()) {
                    subject = cell.getSubjectName();
                } else if (! subject.equals(cell.getSubjectName())) {
                    throw new Exception(String.format("Two subjects %s and %s in one teacher %s",
                            subject, cell.getSubjectName(), entry.getKey()));
                }
            }
            Boolean isFullClass2Class = Classes.checkAllIn(classNames);

            CellData cellDataTmp = entry.getValue().get(0);
            String room = roomIndex.get(cellDataTmp.getRoomID()).getName();
            result2.append(cellDataTmp.getTeacherName());
            if (! SPORT_ROOM.equals(room)) {
                result2.append(" к. ").append(room);
            }
            if (isFullClass2group && isFullClass2Class) {
                result1.append(cellDataTmp.getSubjectName());
            } else if (isFullClass2group) {
                result1.append(cellDataTmp.getSubjectName()).append(" (");
                for (String className : classNames) {
                    result1.append(className.substring(3)).append("/");
                }
                result1.setCharAt(result1.length()-1, ')');
            } else if (isFullClass2Class) {
                result1.append(cellDataTmp.getSubjectName()).append(" ").
                        append(cellDataTmp.getGroupShortName());
            } else {
                result1.append(cellDataTmp.getSubjectName()).append(" ").
                        append(cellDataTmp.getGroupShortName()).append(" (");
                for (String className : classNames) {
                    result1.append(className.substring(3)).append("/");
                }
                result1.setCharAt(result1.length()-1, ')');
               //throw  new Exception("Not full class incorrect");
            }


//            }
             isFirst = false;
        }
        processNewLine(result1);
        processNewLine(result2);
        return result1.append("\n").append(result2).toString();
    }

    private void processNewLine(StringBuilder s) {
        int indexNewLine = s.lastIndexOf("\n");
        int length = s.length();
        if (indexNewLine != -1) {
            length = s.substring(indexNewLine).length();
        }

        if (length <= MAX_LINE_SIZE) {
            return;
        } else {
            int index = s.indexOf("/");
            if (index == -1) {
                return;
            } else {
                s.replace(index - 1, index + 2, "\n");
                processNewLine(s);
            }
        }
    }

    private void createIndexes() {
        for (Lesson lesson : timetable.getLessons()) {
            lessonIndex.put(lesson.getId(), lesson);
        }
        for (Daysdef day : timetable.getDaysdefs()) {
            dayIndex.put(day.getDays(), day);
        }
        for (Period period : timetable.getPeriods()) {
            periodIndex.put(period.getName(), period);
        }
        for (Subject subject : timetable.getSubjects()) {
            subjectIndex.put(subject.getId(), subject);
        }
        for (Teacher teacher : timetable.getTeachers()) {
            teacherIndex.put(teacher.getId(), teacher);
        }
        for (Class clazz : timetable.getClasses()) {
            classesIndex.put(clazz.getId(), clazz);
        }
        for (Group group : timetable.getGroups()) {
            groupIndex.put(group.getId(), group);
        }
        for (Classroom classroom : timetable.getClassrooms()) {
            roomIndex.put(classroom.getId(), classroom);
        }
    }

    public String getDayNameByID (String id) {
        return dayIndex.get(id).getName();
    }
}
