package bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public class Card {
    @XStreamAlias("lessonid")
    @XStreamAsAttribute
    private String lessonid;
    @XStreamAlias("period")
    @XStreamAsAttribute
    private Integer period;
    @XStreamAlias("classroomids")
    @XStreamAsAttribute
    private String roomID;

    public String getLessonid() {
        return lessonid;
    }

    public Integer getPeriod() {
        return period;
    }

    public String getDays() {
        return days;
    }

    @XStreamAlias("days")
    @XStreamAsAttribute
    private String days;

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }
}
