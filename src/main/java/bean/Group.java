package bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public class Group {
    @XStreamAlias("id")
    @XStreamAsAttribute
    private String id;
    @XStreamAlias("name")
    @XStreamAsAttribute
    private String name;
    @XStreamAlias("classid")
    @XStreamAsAttribute
    private String classid;
    @XStreamAlias("divisiontag")
    @XStreamAsAttribute
    private Integer divisiontag;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassid() {
        return classid;
    }

    public void setClassid(String classid) {
        this.classid = classid;
    }
}
