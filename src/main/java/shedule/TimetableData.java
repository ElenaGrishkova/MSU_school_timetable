package shedule;

import bean.*;
import bean.Class;

import java.util.*;

public class TimetableData {
    public static final String FULL_CLASS = "Весь класс";
    public static final String SPORT_ROOM = "Спортивный зал";
    public static final String ENGLISH = "Английский язык";
    public static final List<String> SECOND_LANG = Arrays.asList("Немецкий язык", "Французский язык", "Китайский язык", "Итальянский язык", "Испанский язык");
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

    public Map<String, Set<Student>> getClassName2studentIndex() {
        return className2studentIndex;
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
    private Map<String, List<StudentSubject>> student2studentSubjectIndex = new HashMap<String, List<StudentSubject>>();
    private Map<String, Set<Student>> className2studentIndex = new HashMap<String, Set<Student>>();

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
            } else {
                String groupNameSeminar = String.valueOf(Integer.valueOf(seminargroup) %  10);
                Boolean is8 = false;
                Boolean is10 = false;
                Boolean is11 = false;
                for (String classID : classIDs) {
                    String className = classesIndex.get(classID).getName();
                    Classes clazz = Classes.getByAlias(className);
                    if (Arrays.asList(Classes.CLASS_8, Classes.CLASS_9).contains(clazz) && !is8) {
                        addCellData(classID, groupNameSeminar, period, lesson, dayTable, card.getRoomID());
                        is8 = true;
                    } else if (Classes.is11(clazz.getGeneral()) && !is11) {
                        addCellData(Classes.ALL_11, groupNameSeminar, period, lesson, dayTable, card.getRoomID(), "");
                        is11 = true;
                    } else if (Classes.is10(clazz.getGeneral()) && !is10) {
                        addCellData(Classes.ALL_10, groupNameSeminar, period, lesson, dayTable, card.getRoomID(), "");
                        is10 = true;
                    }
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
        addCellData(Classes.getByAlias(className), groupName, period, lesson, dayTable, roomID, className);
    }

    private void addCellData(Classes clazz, String groupName, Integer period, Lesson lesson,
                             Map<KeyCell, List<CellData>> dayTable, String roomID, String className) throws Exception {
        KeyCell key = new KeyCell(period, clazz);
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
            Classroom room = roomIndex.get(cellDataTmp.getRoomID());
            String roomName = room != null ? room.getName() : "";
            result2.append(cellDataTmp.getTeacherName());
            if (! SPORT_ROOM.equals(roomName)) {
                result2.append(" к. ").append(roomName);
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

    public String printCellDataSeminar(List<CellData> list) throws Exception {
        Boolean isFirst = true;
        StringBuilder result = new StringBuilder();
        for(CellData cellData : list) {
            if (!isFirst) {
                result.append(" / ");
            }
            result.append(cellData.getSubjectName()).append(" гр. ").append(cellData.getGroupName()).
                    append(" ").append(cellData.getTeacherName());
            String room = cellData.getRoomID().isEmpty() ? "" : roomIndex.get(cellData.getRoomID()).getName();
            if (! SPORT_ROOM.equals(room)) {
                result.append(" к. ").append(room);
            }
            isFirst = false;
        }
        return result.toString();
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
        for (StudentSubject studentSubject : timetable.getStudentSubjects()) {
            String studentId = studentSubject.getStudentid();
            List<StudentSubject> studentSubjects = student2studentSubjectIndex.get(studentId);
            if (studentSubjects == null) {
                studentSubjects = new LinkedList<StudentSubject>();
                student2studentSubjectIndex.put(studentId, studentSubjects);
            }
            studentSubjects.add(studentSubject);
        }
        for (Student student : timetable.getStudents()) {
            String classId = student.getClassid();
            Class clazz = classesIndex.get(classId);
            Set<Student> students = className2studentIndex.get(clazz.getName());
            if (students == null) {
                students = new TreeSet<Student>();
                className2studentIndex.put(clazz.getName(), students);
            }
            students.add(student);
        }
    }

    public String getDayNameByID (String id) {
        return dayIndex.get(id).getName();
    }

    public String getEnglishGroup(String studentId) {
        List<StudentSubject> studentSubjects = student2studentSubjectIndex.get(studentId);
        if (studentSubjects == null) return "";
        for(StudentSubject studentSubject : studentSubjects) {
            String subjectID = studentSubject.getSubjectid();
            Subject subject = subjectIndex.get(subjectID);
            if (subject.getName().equals(ENGLISH)) {
                return String.valueOf(Integer.valueOf(studentSubject.getSeminargroup()) % 10);
            }
        }
        return "";
    }

    public String getSecondLang(String studentId) {
        List<StudentSubject> studentSubjects = student2studentSubjectIndex.get(studentId);
        if (studentSubjects == null) return "";
        for(StudentSubject studentSubject : studentSubjects) {
            String subjectID = studentSubject.getSubjectid();
            Subject subject = subjectIndex.get(subjectID);
            if (SECOND_LANG.contains(subject.getName())) {
                return subject.getName();
            }
        }
        return "";
    }

    public String getSecondLangGroup(String studentId) {
        List<StudentSubject> studentSubjects = student2studentSubjectIndex.get(studentId);
        if (studentSubjects == null) return "";
        for(StudentSubject studentSubject : studentSubjects) {
            String subjectID = studentSubject.getSubjectid();
            Subject subject = subjectIndex.get(subjectID);
            if (SECOND_LANG.contains(subject.getName())) {
                return String.valueOf(Integer.valueOf(studentSubject.getSeminargroup()) % 10);
            }
        }
        return "";
    }
}
