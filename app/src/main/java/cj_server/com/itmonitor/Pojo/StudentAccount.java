package cj_server.com.itmonitor.Pojo;

import android.net.Uri;

/**
 * Created by cj-sever on 5/21/17.
 */

public class StudentAccount {
    private String name;
    private String regno;
    private String mobile;
    private Uri photoUri;


    public StudentAccount(String name, String regno, String mobile,Uri photoUri) {
        this.name = name;
        this.regno = regno;
        this.mobile = mobile;
        this.photoUri=photoUri;
    }

    public StudentAccount() {
    }

    public Uri getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(Uri photoUri) {
        this.photoUri = photoUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegno() {
        return regno;
    }

    public void setRegno(String regno) {
        this.regno = regno;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
