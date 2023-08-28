package com.example.notesapp.Model;

import androidx.annotation.NonNull;

public class TaskDate {
    private String dayName;
    private String dayNumber;
    private String month;
    private String hour;
    public TaskDate(){}

    public TaskDate(String dayName, String dayNumber, String month, String hour) {
        this.dayName = dayName;
        this.dayNumber = dayNumber;
        this.month = month;
        this.hour = hour;
    }

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public String getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(String dayNumber) {
        this.dayNumber = dayNumber;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    @NonNull
    @Override
    public String toString() {
        return  dayName + ", " + dayNumber + " " + month + " at " + hour;

    }
}
