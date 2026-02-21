package com.lifelink.donorms.model;

public class Donation {

    private String id;
    private String donorEmail;
    private String date;
    private String time;
    private String location;
    private String type;
    private String status;

    public Donation() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDonorEmail() { return donorEmail; }
    public void setDonorEmail(String donorEmail) { this.donorEmail = donorEmail; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}