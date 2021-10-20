package com.example.moveonotes.model;

public class NoteObject {
    public NoteObject(String title, String textBody, String currentDate, String currentTime) {
        this.title = title;
        this.textBody = textBody;
        this.currentDate = currentDate;
        this.currentTime = currentTime;
    }

    String title;
    String textBody;
    String currentDate;
    String currentTime;
    String latitude, longitude;
    String photo;


    public NoteObject(String title, String textBody, String currentDate, String currentTime, String latitude, String longitude, String photo) {
        this.title = title;
        this.textBody = textBody;
        this.currentDate = currentDate;
        this.currentTime = currentTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photo = photo;
    }

    public NoteObject(String title, String photo, String body, String time, String date) {
        this.title = title;
        this.textBody = body;
        this.currentDate = date;
        this.currentTime = time;
        this.photo = photo;
    }

    public String getPhoto() {
        return photo;
    }



    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public NoteObject(String title, String textBody, String currentDate, String currentTime, String latitude, String longitude) {
        this.title = title;
        this.textBody = textBody;
        this.currentDate = currentDate;
        this.currentTime = currentTime;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public NoteObject() {
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }


    //location

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTextBody() {
        return textBody;
    }

    public void setTextBody(String textBody) {
        this.textBody = textBody;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }


}
