package bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public class Lesson {
    @XStreamAlias("id")
    @XStreamAsAttribute
    private String id;
    @XStreamAlias("classids")
    @XStreamAsAttribute
    private String classids;
    @XStreamAlias("subjectid")
    @XStreamAsAttribute
    private String subjectid;
    @XStreamAlias("periodspercard") //длительность урока
    @XStreamAsAttribute
    private Integer periodspercard;
    @XStreamAlias("teacherids")
    @XStreamAsAttribute
    private String teacherids;
    @XStreamAlias("seminargroup")
    @XStreamAsAttribute
    private String seminargroup; //номер группы в семинаре

    public String getId() {
        return id;
    }

    public String getClassids() {
        return classids;
    }

    public String getSubjectid() {
        return subjectid;
    }

    public Integer getPeriodspercard() {
        return periodspercard;
    }

    public String getTeacherids() {
        return teacherids;
    }

    public String getGroupids() {
        return groupids;
    }

    @XStreamAlias("groupids")
    @XStreamAsAttribute
    private String groupids;

    public String getSeminargroup() {
        return seminargroup;
    }
}
