package bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public class Teacher {
    @XStreamAlias("id")
    @XStreamAsAttribute
    private String id;

    public String getId() {
        return id;
    }

    public String getLastname() {
        return lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    @XStreamAlias("lastname")
    @XStreamAsAttribute
    private String lastname;
    @XStreamAlias("firstname")
    @XStreamAsAttribute
    private String firstname;
    @XStreamAlias("name")
    @XStreamAsAttribute
    private String name;


    public String getName() {
        return name;
    }
}
