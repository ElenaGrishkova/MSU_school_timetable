package bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public class Subject {
    @XStreamAlias("id")
    @XStreamAsAttribute
    private String id;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @XStreamAlias("name")
    @XStreamAsAttribute
    private String name;

    @XStreamAlias("short")
    @XStreamAsAttribute
    private String shorty;

    public String getShorty() {
        return shorty;
    }
}
