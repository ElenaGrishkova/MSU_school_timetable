package bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public class Classroom {
    @XStreamAlias("id")
    @XStreamAsAttribute
    private String id;
    @XStreamAlias("name")
    @XStreamAsAttribute
    private String name;
    @XStreamAlias("short")
    @XStreamAsAttribute
    private String shorty;

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

    public String getShorty() {
        return shorty;
    }

    public void setShorty(String shorty) {
        this.shorty = shorty;
    }
}
