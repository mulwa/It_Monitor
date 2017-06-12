package cj_server.com.itmonitor.Pojo;

/**
 * Created by cj-sever on 5/21/17.
 */

public class NewClass {
    private String ClassName;
    private String id;

    public NewClass(String className, String id) {
        ClassName = className;
        this.id = id;
    }

    public NewClass() {
    }

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String className) {
        ClassName = className;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
