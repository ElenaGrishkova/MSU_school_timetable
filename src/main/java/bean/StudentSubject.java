package bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public class StudentSubject {
    @XStreamAlias("subjectid")
    @XStreamAsAttribute
    private String subjectid;
    @XStreamAlias("studentid")
    @XStreamAsAttribute
    private String studentid;
    @XStreamAlias("seminargroup")
    @XStreamAsAttribute
    private String seminargroup; //номер группы в семинаре

    public String getSubjectid() {
        return subjectid;
    }

    public String getStudentid() {
        return studentid;
    }

    public String getSeminargroup() {
        return seminargroup;
    }
}
