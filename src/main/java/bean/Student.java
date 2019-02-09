package bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public class Student implements Comparable<Student> {
    @XStreamAlias("id")
    @XStreamAsAttribute
    private String id;
    @XStreamAlias("classid")
    @XStreamAsAttribute
    private String classid;
    @XStreamAlias("name")
    @XStreamAsAttribute
    private String name;


    public String getId() {
        return id;
    }

    public String getClassid() {
        return classid;
    }

    public String getName() {
        return name;
    }

    public int compareTo(Student o) {
        return this.getName().compareTo(o.getName());
    }
}
