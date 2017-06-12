package cj_server.com.itmonitor.Pojo;


import java.lang.reflect.Array;

/**
 * Created by cj-sever on 4/10/17.
 */

public class AttachmentDetails {
    private String  organizationName;
    private String buildingName;
    private  String  Branch;
    private  String floorNumber;
    private  String mobileNumber;
    private  String  startDate;
    private String address;
    private String placeId;
    private double lat;
    private  double lng;
    private String studentName;
    private String studentRegNo;

    public AttachmentDetails(String studentName,String studentRegNo,String organizationName, String buildingName, String branch,
                             String floorNumber, String mobileNumber, String startDate,
                             String address, String placeId, double lat, double lng) {
        this.studentName = studentName;
        this.studentRegNo = studentRegNo;
        this.organizationName = organizationName;
        this.buildingName = buildingName;
        Branch = branch;
        this.floorNumber = floorNumber;
        this.mobileNumber = mobileNumber;
        this.startDate = startDate;
        this.address = address;
        this.placeId = placeId;
        this.lat = lat;
        this.lng = lng;
    }

    public AttachmentDetails() {
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentRegNo() {
        return studentRegNo;
    }

    public void setStudentRegNo(String studentRegNo) {
        this.studentRegNo = studentRegNo;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public String getBranch() {
        return Branch;
    }

    public String getFloorNumber() {
        return floorNumber;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getAddress() {
        return address;
    }

    public String getPlaceId() {
        return placeId;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
