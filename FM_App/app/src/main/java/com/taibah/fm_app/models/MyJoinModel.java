package com.taibah.fm_app.models;

import java.io.Serializable;

public class MyJoinModel implements Serializable {
    private String id;
    private int student_trainer;
    private String student_id;
    private String duration;
    private String cost;
 //   private int gender;
    private String join_date;
    private String details;
    private String user_id;
    private String date;

    public MyJoinModel() {
    }

    public MyJoinModel(String id, int student_trainer, String student_id, String duration, String cost, String join_date, String details, String user_id, String date) {
        this.id = id;
        this.student_trainer = student_trainer;
        this.student_id = student_id;
        this.duration = duration;
        this.cost = cost;
       // this.gender = gender;
        this.join_date = join_date;
        this.details = details;
        this.user_id = user_id;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStudent_trainer() {
        return student_trainer;
    }

    public void setStudent_trainer(int type) {
        this.student_trainer = type;
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

 /*   public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }*/

    public String getJoin_date() {
        return join_date;
    }

    public void setJoin_date(String join_date) {
        this.join_date = join_date;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
