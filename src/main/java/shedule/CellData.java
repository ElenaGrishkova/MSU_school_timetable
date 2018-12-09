package shedule;

public class CellData extends KeyCell implements Comparable<CellData>{
    public static final String GROUP_1 = "1 группа";
    public static final String GROUP_2 = "2 группа";
    public static final String SHORT_GROUP_1 = "гр. 1";
    public static final String SHORT_GROUP_2 = "гр. 2";

    private String className;
    private String groupName;
    private String teacherName;
    private String subjectName;
    private String lessionID;
    private String roomID;

    public String getClassName() {
        return className;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getRoomID() {
        return roomID;
    }

    public CellData(KeyCell key, String className, String groupName, String teacherName, String subjectName, String lessionID, String roomID) {
        super(key.getPeriod(), key.getClazz());
        this.className = className;
        this.groupName = groupName;
        this.teacherName = teacherName;
        this.subjectName = subjectName;
        this.lessionID = lessionID;
        this.roomID = roomID;
    }


    public String getLessionID() {
        return lessionID;
    }

    public void setLessionID(String lessionID) {
        this.lessionID = lessionID;
    }

    public String getGroupShortName() {
        String[] ss = groupName.split(" ");
        return "гр. " + ss[0];
//        if (GROUP_1.equals(groupName)) {
//            return SHORT_GROUP_1;
//        } else if (GROUP_2.equals(groupName)) {
//            return SHORT_GROUP_2;
//        } else {
//            return groupName;
//        }
    }


    public int compareTo(CellData o) {
        int classes = className.compareTo(o.getClassName());
        if (classes == 0) {
            return groupName.compareTo(o.getGroupName());
        }
        return classes;
    }
}
