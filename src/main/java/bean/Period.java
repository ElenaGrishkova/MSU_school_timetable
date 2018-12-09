package bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public class Period {
    @XStreamAlias("name")
    @XStreamAsAttribute
    private Integer name;

    public Integer getName() {
        return name;
    }

    public String getStarttime() {
        return starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    @XStreamAlias("starttime")
    @XStreamAsAttribute
    private String starttime;
    @XStreamAlias("endtime")
    @XStreamAsAttribute
    private String endtime;

}
