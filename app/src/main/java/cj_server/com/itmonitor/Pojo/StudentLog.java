package cj_server.com.itmonitor.Pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.firebase.database.ServerValue;

import java.util.Date;

/**
 * Created by cj-sever on 6/1/17.
 */

public class StudentLog {
    private String summary;
    private String activityDescription;
    @JsonProperty
    private Object dateSubmitted;

    public StudentLog(String summary, String activityDescription,Object dateSubmitted) {
        this.summary = summary;
        this.activityDescription = activityDescription;
        this.dateSubmitted = dateSubmitted;
    }

    public StudentLog() {
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getActivityDescription() {
        return activityDescription;
    }

    public void setActivityDescription(String activityDescription) {
        this.activityDescription = activityDescription;
    }
    public Long getDateSubmitted(){
        if(dateSubmitted  instanceof Long){
            return (Long) dateSubmitted;
        }else {
            return  null;
        }
    }
}
