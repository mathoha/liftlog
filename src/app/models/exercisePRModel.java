package app.models;

import java.sql.Date;
import java.sql.Time;

public class exercisePRModel {

    private int exerciseID, workoutID, reps, kilos;
    private String name;
    private Date date;
    private Time time;
    private int oneRM;

    public exercisePRModel(int exerciseID, int reps, int max_kilos, String name) {
        this.exerciseID = exerciseID;
        this.reps = reps;
        this.kilos = max_kilos;
        this.name = name;
    }


    //for use in Personal Record View where specific exercise is selected
    public exercisePRModel(Date date, Time time, int exerciseID, int workoutID, int reps, int kilos, String name) {
        this.date = date;
        this.time = time;
        this.exerciseID = exerciseID;
        this.workoutID = workoutID;
        this.reps = reps;
        this.kilos = kilos;
        this.name = name;
        this.oneRM = this.getRepMax();
    }

    public int getOneRM() {
        return oneRM;
    }

    public void setOneRM(int oneRM) {
        this.oneRM = oneRM;
    }

    public int getWorkoutID() {
        return workoutID;
    }

    public void setWorkoutID(int workoutID) {
        this.workoutID = workoutID;
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

    public int getRepMax(){
        if (reps == 1){
            return kilos;
        }
        double rm = ((double) reps/30) + 1;
        double rm1 =  rm * kilos;
        return (int) rm1;
    }

    public int getExerciseID() {
        return exerciseID;
    }

    public void setExerciseID(int exerciseID) {
        this.exerciseID = exerciseID;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public int getKilos() {
        return kilos;
    }

    public void setKilos(int kilos) {
        this.kilos = kilos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
