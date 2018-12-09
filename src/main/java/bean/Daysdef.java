package bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public class Daysdef {
    @XStreamAlias("name")
    @XStreamAsAttribute
    private String name;

    public String getName() {
        return name;
    }

    public String getDays() {
        return days;
    }

    @XStreamAlias("days")
    @XStreamAsAttribute
    private String days;
}
