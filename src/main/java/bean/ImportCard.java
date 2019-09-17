package bean;

public class ImportCard implements Comparable<ImportCard>{
    private String timeNum;
    private String time;
    private String subject;
    private String teacher;
    private String room;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getTimeNum() {
        return timeNum;
    }

    public void setTimeNum(String timeNum) {
        this.timeNum = timeNum;
    }

    @Override
    public int compareTo(ImportCard o) {
        int comp = timeNum.compareTo(o.timeNum);
        if (comp == 0) return subject.compareTo(o.subject);
        return comp;
    }
}
