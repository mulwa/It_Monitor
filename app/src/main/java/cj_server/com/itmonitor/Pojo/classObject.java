package cj_server.com.itmonitor.Pojo;

/**
 * Created by cj-sever on 4/11/17.
 */

public class classObject {
    private String name;
    private String id;

    public classObject(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public classObject() {
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }
}
