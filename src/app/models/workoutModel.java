package app.models;

import java.sql.Date;
import java.sql.Time;

public class workoutModel {

    private int workoutID,duration,shape,performance;
    private String note;
    private Date date;
    private Time time;


    public workoutModel(int workoutID, Date date, Time time, int duration, int shape, int performance, String note){
        this.workoutID = workoutID;
        this.date = date;
        this.time = time;
        this.duration = duration;
        this.shape = shape;
        this.performance = performance;
        this.note = note;
    }

    public int getWorkoutID() {
        return workoutID;
    }

    public void setWorkoutID(int workoutID) {
        this.workoutID = workoutID;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getShape() {
        return shape;
    }

    public void setShape(int shape) {
        this.shape = shape;
    }

    public int getPerformance() {
        return performance;
    }

    public void setPerformance(int performance) {
        this.performance = performance;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }
}
