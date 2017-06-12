package cj_server.com.itmonitor.Pojo;

/**
 * Created by cj-sever on 5/28/17.
 */

public class Events {
    private String eventName;
    private String  eventDescription;
    private String  eventVenue;
    private String eventTarget;
    private String eventDate;
    private String  eventTime;


    public Events(String eventName, String eventDescription, String eventVenue, String eventTarget, String eventDate, String eventTime) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventVenue = eventVenue;
        this.eventTarget = eventTarget;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
    }

    public Events() {
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventVenue() {
        return eventVenue;
    }

    public void setEventVenue(String eventVenue) {
        this.eventVenue = eventVenue;
    }

    public String getEventTarget() {
        return eventTarget;
    }

    public void setEventTarget(String eventTarget) {
        this.eventTarget = eventTarget;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }
}
