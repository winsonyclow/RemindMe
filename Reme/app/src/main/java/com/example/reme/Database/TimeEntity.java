package com.example.reme.Database;

import androidx.room.PrimaryKey;

@androidx.room.Entity (tableName = "timeReminder")
public class TimeEntity {
    @PrimaryKey (autoGenerate = false)
    String id;
    String noteTitle;
    String date;
    String time;

    public TimeEntity(String id, String noteTitle, String date, String time) {
        this.id = id;
        this.noteTitle = noteTitle;
        this.date = date;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
