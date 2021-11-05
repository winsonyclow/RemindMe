package com.example.reme;

import java.io.Serializable;

public class NoteDetail implements Serializable {
    public String title;
    public String subtitle;
    public String note;
    public String uid;
    public String dateTime;
    public String noteUid;
    public String color;
    public String imageUrl;

    public NoteDetail() {

    }

    public NoteDetail(String title, String subtitle, String note, String uid, String dateTime, String noteUid, String color, String imageUrl){
        this.title = title;
        this.subtitle = subtitle;
        this.note = note;
        this.uid = uid;
        this.dateTime = dateTime;
        this.noteUid = noteUid;
        this.color = color;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getNote() {
        return note;
    }

    public String getUid() {
        return uid;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getNoteUid() {
        return noteUid;
    }

    public String getColor() { return color; }

}
